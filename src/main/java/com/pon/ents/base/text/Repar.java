package com.pon.ents.base.text;

import javax.annotation.Nullable;

import com.pon.ents.base.functional.Bijection;
import com.pon.ents.base.io.Codec;
import com.pon.ents.base.io.Input;

/**
 * A counterpart of {@link Codec} working with {@link String}s rather than {@link Input}s/{@link Output}s.
 * <p>
 * Fun fact: the "repar" name comes from "renderer-parser", similarly to "codec" coming from "coder-decoder".
 */
public interface Repar<T> extends Bijection<T, String> {

    /**
     * Returns a {@link String} representation of the object.
     */
    String render(@Nullable T object);

    /**
     * Parses the {@link String} to an object.
     */
    @Nullable
    T parse(String string);

    @Override
    default String forward(@Nullable T a) {
        return render(a);
    }

    @Override
    @Nullable
    default T backward(String b) {
        return parse(b);
    }
}
