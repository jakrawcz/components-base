package com.pon.ents.base.blob.impl;

import java.util.function.Function;

import com.pon.ents.base.blob.Blob;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.IoFilter;
import com.pon.ents.base.io.Output;

public class IoFilterApplicator implements Function<Blob, Blob> {

    private final IoFilter ioFilter;

    public IoFilterApplicator(IoFilter ioFilter) {
        this.ioFilter = ioFilter;
    }

    @Override
    public Blob apply(Blob blob) {
        return new IoFilteringBlob(blob);
    }

    private class IoFilteringBlob implements Blob {

        private final Blob blob;

        public IoFilteringBlob(Blob blob) {
            this.blob = blob;
        }

        @Override
        public Input read(long relativeOffset) {
            return ioFilter.in(blob.read(relativeOffset));
        }

        @Override
        public Output write(long relativeOffset) {
            return ioFilter.out(blob.write(relativeOffset));
        }
    }
}
