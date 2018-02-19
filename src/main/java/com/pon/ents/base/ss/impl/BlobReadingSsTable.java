package com.pon.ents.base.ss.impl;

import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.closeable.CloseableIterators;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.InputOpener;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.ss.SsTable;

public class BlobReadingSsTable implements SsTable {

    private final SsIndexQuerier indexQuerier;
    private final InputOpener inputOpener;
    private final SsLengthCodec lengthCodec;

    public BlobReadingSsTable(SsIndexQuerier indexQuerier, InputOpener inputOpener, SsLengthCodec lengthCodec) {
        this.indexQuerier = indexQuerier;
        this.inputOpener = inputOpener;
        this.lengthCodec = lengthCodec;
    }

    @Override
    public CloseableIterator<Input> iterator(Input from, Input to) {
        long[] offsets = indexQuerier.offsetsOf(from, to);

        long fromOffset = offsets[0];
        if (fromOffset == SsIndexQuerier.OFFSET_TOO_HIGH) {
            return CloseableIterators.empty();
        }

        long toOffset = offsets[1];
        if (toOffset == SsIndexQuerier.OFFSET_TOO_LOW) {
            return CloseableIterators.empty();
        }

        long offset = fromOffset == SsIndexQuerier.OFFSET_TOO_LOW ? 0 : fromOffset;
        long limit = toOffset == SsIndexQuerier.OFFSET_TOO_HIGH ? -1 : (toOffset - offset);

        Input input = inputOpener.openAt(offset);
        Input limitedInput = limit == -1 ? input : Inputs.limit(input, limit);

        return new InputSlicingCloseableIterator(limitedInput, lengthCodec);
    }
}
