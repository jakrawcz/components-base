package com.pon.ents.base.ss.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.IoBuffers;
import com.pon.ents.base.io.Outputs;
import com.pon.ents.base.io.ProducingOutput;
import com.pon.ents.base.io.Size;
import com.pon.ents.base.io.Sizes;
import com.pon.ents.base.ss.impl.SsIndex.SsIndexEntry;

public class BlockSsIndexer implements SsIndexer {

    public static final Size DEFAULT_MAX_BLOCK_SIZE = Sizes.kilobytes(64);

    private final long maxBlockLength;

    public BlockSsIndexer() {
        this(DEFAULT_MAX_BLOCK_SIZE);
    }

    public BlockSsIndexer(Size maxBlockSize) {
        this.maxBlockLength = maxBlockSize.bytes();
    }

    @Override
    public SsIndexBuilder builder() {
        return new BlockSsIndexBuilder();
    }

    private class BlockSsIndexBuilder implements SsIndexBuilder {

        private final List<SsIndexEntry> entries;

        private ProducingOutput<IoBuffer> producingOutput;

        @Nullable
        private IoBuffer first;

        @Nullable
        private IoBuffer current;

        @Nullable
        private IoBuffer blockLeading;

        private long blockStartOffset;
        private long at;

        public BlockSsIndexBuilder() {
            this.entries = new ArrayList<>();
            this.producingOutput = Outputs.producingIoBuffer();
            this.blockStartOffset = 0;
            this.at = 0;
        }

        @Override
        public void cut(long writtenInputEncodedLength) {
            @Nullable IoBuffer previous = current;
            this.current = producingOutput.produce();
            long nextAt = at + writtenInputEncodedLength;
            if (previous == null) {
                this.first = current;
                this.blockLeading = current;
            } else if (nextAt > blockStartOffset + maxBlockLength) {
                int commonPrefixLength = IoBuffers.commonPrefixLength(blockLeading, current);
                if (commonPrefixLength < current.length()) {
                    int currentOffset = current.offset();
                    IoBuffer difference = current.rePositioned(currentOffset, currentOffset + commonPrefixLength + 1);
                    entries.add(new IoBufferSsIndexEntry(difference, at));
                    this.blockStartOffset = at;
                    this.blockLeading = current;
                }
            }
            this.producingOutput = Outputs.producingIoBuffer();
            this.at = nextAt;
        }

        @Override
        public void write(byte[] buffer, int offset, int length) {
            producingOutput.write(buffer, offset, length);
        }

        @Override
        public void write(int b) {
            producingOutput.write(b);
        }

        @Override
        public SsIndex produce() {
            return new IoBufferSsIndex(first, entries, current);
        }

        @Override
        public void close() {
        }
    }
}
