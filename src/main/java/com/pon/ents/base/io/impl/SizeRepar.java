package com.pon.ents.base.io.impl;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.pon.ents.base.io.Size;
import com.pon.ents.base.io.Sizes;
import com.pon.ents.base.text.Repar;

public class SizeRepar implements Repar<Size> {

    public static final Repar<Size> INSTANCE = new SizeRepar();

    private static final Pattern SIZE_PATTERN = createSizePattern();

    @Override
    public Size parse(String string) {
        Matcher matcher = SIZE_PATTERN.matcher(string.toUpperCase());
        Preconditions.checkArgument(matcher.matches(),
                "size text must use format %s but got %s", SIZE_PATTERN, string);

        @Nullable String countString = matcher.group(1);
        @Nullable String unitString = matcher.group(2);

        long count = Long.valueOf(countString);
        Size.SizeUnit sizeUnit = unitString == null ? Size.SizeUnit.B : Size.SizeUnit.valueOf(unitString);

        return Sizes.of(count, sizeUnit);
    }

    @Override
    public String render(Size size) {
        long quantity = size.bytes();
        if (quantity == 0) {
            return "0";
        }
        for (Size.SizeUnit unit : EnumSet.allOf(Size.SizeUnit.class)) {
            if ((quantity % Size.ORDER_OF_MAGNITUDE) != 0) {
                return quantity + unit.name();
            }
            quantity /= Size.ORDER_OF_MAGNITUDE;
        }
        return quantity + Size.SizeUnit.PB.name();
    }

    private static Pattern createSizePattern() {
        String sizesSeparatedByOrSign = EnumSet.allOf(Size.SizeUnit.class).stream()
                .map(Size.SizeUnit::name)
                .collect(Collectors.joining("|"));
        return Pattern.compile("(\\d+)\\s*(" + sizesSeparatedByOrSign + ")??");
    }
}
