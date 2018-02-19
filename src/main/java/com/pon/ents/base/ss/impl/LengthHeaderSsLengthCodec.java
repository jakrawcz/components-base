package com.pon.ents.base.ss.impl;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Chars;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInts;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Output;

public class LengthHeaderSsLengthCodec implements SsLengthCodec {

    @Override
    public int encode(Output output, long length) {
        if (length <= 0) {
            Preconditions.checkArgument(length == 0, length);
            output.write(0);
            return 1;
        }
        if (length <= 0xffL) {
            output.write(Byte.BYTES);
            output.writeByte(UnsignedBytes.checkedCast(length));
            return 1 + Byte.BYTES;
        }
        if (length <= 0xffffL) {
            output.write(Short.BYTES);
            output.writeChar(Chars.checkedCast(length));
            return 1 + Short.BYTES;
        }
        if (length <= 0xffffffffL) {
            output.write(Integer.BYTES);
            output.writeInt(UnsignedInts.checkedCast(length));
            return 1 + Integer.BYTES;
        }
        Preconditions.checkArgument(length <= Long.MAX_VALUE, length);
        output.write(Long.BYTES);
        output.writeLong(length);
        return 1 + Long.BYTES;
    }

    @Override
    public long decode(Input input) {
        int lengthEncoding = input.read();
        switch (lengthEncoding) {
            case -1:
                return -1;
            case 0:
                return 0;
            case Byte.BYTES:
                return UnsignedBytes.toInt(input.readByte());
            case Short.BYTES:
                return input.readChar(); // I actually wondered why guava exposes no UnsignedShorts :)
            case Integer.BYTES:
                return UnsignedInts.toLong(input.readInt());
            case Long.BYTES:
                return input.readLong();
            default:
                throw new IllegalStateException("a length encoding ID was expected, but read byte " + lengthEncoding);
        }
    }
}
