package com.pon.ents.base.unsafe;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class Unsafes {

    private static final Unsafe UNSAFE = getUnsafe();

    /**
     * Returns the famous {@link Unsafe}.
     */
    public static Unsafe get() {
        return UNSAFE;
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            return (Unsafe) theUnsafeField.get(null);
        } catch (Exception e) {
            throw new AssertionError("cannot get access to Unsafe", e);
        }
    }
}
