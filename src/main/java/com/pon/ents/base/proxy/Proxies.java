package com.pon.ents.base.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.reflector.TypeReflectors;
import com.pon.ents.base.unsafe.Unsafes;

public abstract class Proxies {

    /**
     * Returns a proxy for the given {@link Object} that will route all methods of all its implemented interfaces
     * through the given {@link Executor}, expecting it to return immediately.
     * <p>
     * The Java method invocation API is inherently synchronous, which means that the provided {@link Executor} must
     * always block until the executed method returns.
     */
    @SuppressWarnings("unchecked")
    public static <T> T executingOn(T object, Executor executor) {
        Class<? extends Object> cls = object.getClass();
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), interfacesOf(cls),
                new ExecutingInvocationHandler(executor, object));
    }

    /**
     * Returns a proxy for the given {@link Object} that will route all methods of all its implemented interfaces
     * through the given {@link Executor}, blocking until they return.
     * <p>
     * This kind of behavior is only required when a work has to be performed on some specific thread.
     */
    @SuppressWarnings("unchecked")
    public static <T> T deferringTo(T object, Executor executor) {
        Class<? extends Object> cls = object.getClass();
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), interfacesOf(cls),
                new DeferringInvocationHandler(executor, object));
    }

    /**
     * Returns a proxy for the given {@link Object} that will simply call its methods whenever a method of the given
     * {@link Class wrappingInterface} is called.
     * <p>
     * This is useful for "implementing" marker interfaces, or "wrapping" generic implementations in named interfaces.
     */
    @SuppressWarnings("unchecked")
    public static <T extends S, S> T wrapping(S object, Class<T> wrappingInterface) {
        return (T) Proxy.newProxyInstance(wrappingInterface.getClassLoader(), new Class[] {wrappingInterface},
                new DelegatingInvocationHandler(object));
    }

    /**
     * Returns a proxy for the given {@link Object} that will always throw a newly {@link Supplier#get supplied}
     * {@link Throwable} whenever a method of the given {@link Class implementedInterface} is called.
     */
    @SuppressWarnings("unchecked")
    public static <T extends S, S> T throwing(Class<T> implementedInterface, Supplier<Throwable> supplier) {
        return (T) Proxy.newProxyInstance(implementedInterface.getClassLoader(), new Class[] {implementedInterface},
                new ThrowingInvocationHandler(supplier));
    }

    /**
     * Returns a proxy that will use the given {@link Supplier} and call the supplied object's methods whenever a method
     * of the given {@link Class implementedInterface} is called.
     * <p>
     * This is useful for handing out "lazy" references to future objects.
     */
    @SuppressWarnings("unchecked")
    public static <T> T dynamic(Supplier<T> objectSupplier, Class<T> implementedInterface) {
        return (T) Proxy.newProxyInstance(implementedInterface.getClassLoader(), new Class[] {implementedInterface},
                new DynamicInvocationHandler(objectSupplier));
    }

    @Nullable
    private static Object invokeDirectly(Method method, Object object, Object[] args) {
        try {
            return method.invoke(object, args);
        } catch (InvocationTargetException e) {
            Unsafes.get().throwException(e.getCause()); // this is safe; even a checked cause must have been declared
            throw new AssertionError("there is an unchecked, but unconditional throw above", e);
        } catch (IllegalAccessException e) {
            throw new AssertionError("proxies only route interface method calls, which are always accessible", e);
        }
    }

    @Nullable
    private static Object invokeDefault(Method method, Object proxy, Object[] args) {
        try {
            Constructor<MethodHandles.Lookup> lookupConstructor =
                    MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
            lookupConstructor.setAccessible(true);
            return lookupConstructor.newInstance(method.getDeclaringClass(), MethodHandles.Lookup.PRIVATE)
                    .unreflectSpecial(method, method.getDeclaringClass())
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new AssertionError("proxies only route interface method calls, which are always accessible", e);
        } catch (Throwable e) {
            Unsafes.get().throwException(e); // this is safe; even a checked cause must have been declared
            throw new AssertionError("there is an unchecked, but unconditional throw above", e);
        }
    }

    private static Class<?>[] interfacesOf(Class<? extends Object> cls) {
        return TypeReflectors.of(cls).streamImplementedInterfaces().toArray(Class[]::new);
    }

    private static class ExecutingInvocationHandler implements InvocationHandler {

        private static final Object NO_RESULT_MARKER = new Object();

        private final Executor executor;
        private final Object object;

        public ExecutingInvocationHandler(Executor executor, Object object) {
            this.executor = executor;
            this.object = object;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return invokeDirectly(method, object, args);
            }
            AtomicReference<Object> reference = new AtomicReference<>(NO_RESULT_MARKER);
            executor.execute(() -> {
                @Nullable Object current = invokeDirectly(method, object, args);
                @Nullable Object previous = reference.getAndSet(current);
                Preconditions.checkState(previous == NO_RESULT_MARKER,
                        "the underlying executor has already returned %s and should not return %s", previous, current);
            });
            @Nullable Object result = reference.get();
            Preconditions.checkState(result != NO_RESULT_MARKER,
                    "the underlying executor has not completed the task before returning (is it not direct?)");
            return result;
        }
    }

    private static class DeferringInvocationHandler implements InvocationHandler {

        private final Executor executor;
        private final Object object;

        public DeferringInvocationHandler(Executor executor, Object object) {
            this.executor = executor;
            this.object = object;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return invokeDirectly(method, object, args);
            }
            CompletableFuture<Object> resultFuture = new CompletableFuture<>();
            executor.execute(() -> {
                @Nullable Object result = invokeDirectly(method, object, args);
                resultFuture.complete(result);
            });
            return resultFuture.get();
        }
    }

    private static class DelegatingInvocationHandler implements InvocationHandler {

        private final Object object;

        public DelegatingInvocationHandler(Object object) {
            this.object = object;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass().isInstance(object)) {
                return invokeDirectly(method, object, args);
            }
            if (method.isDefault()) {
                return invokeDefault(method, proxy, args);
            }
            throw new IllegalArgumentException("a (non-default) " + method + " was called on an instance of "
                    + object.getClass() + " (which does not provide any implementation of it)");
        }
    }

    private static class DynamicInvocationHandler implements InvocationHandler {

        private final Supplier<?> objectSupplier;

        public DynamicInvocationHandler(Supplier<?> objectSupplier) {
            this.objectSupplier = objectSupplier;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object object = objectSupplier.get();
            return invokeDirectly(method, object, args);
        }
    }

    private static class ThrowingInvocationHandler implements InvocationHandler {

        private final Supplier<Throwable> supplier;

        public ThrowingInvocationHandler(Supplier<Throwable> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            throw supplier.get();
        }
    }
}
