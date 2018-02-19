package com.pon.ents.base.blob.impl;

import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.pon.ents.base.blob.Blob;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.IoBuffers;
import com.pon.ents.base.io.Output;
import com.pon.ents.base.io.impl.IoBufferBuilder;
import com.pon.ents.base.io.impl.IoBufferOutput;
import com.pon.ents.base.management.Manager;

public class MemoryBlobManager<K> implements Manager<K, Blob> {

    private final Map<K, IoBuffer> map;

    public MemoryBlobManager(Map<K, IoBuffer> map) {
        this.map = map;
    }

    @Override
    public Blob access(K key) {
        return new TrackedBlob(key);
    }

    @Override
    public Stream<K> streamNonEmptyObjectKeys() {
        return map.keySet().stream();
    }

    private class TrackedBlob implements Blob {

        private final K key;

        public TrackedBlob(K key) {
            this.key = key;
        }

        @Override
        public Input read(long relativeOffset) {
            @Nullable IoBuffer ioBuffer = map.get(key);
            if (ioBuffer == null) {
                return Inputs.empty();
            }
            return Inputs.fromIoBuffer(ioBuffer);
        }

        @Override
        public Output write(long relativeOffset) {
            IoBuffer ioBuffer = map.getOrDefault(key, IoBuffers.empty());
            int offset = (int) RelativeOffsets.toOffset(relativeOffset, ioBuffer.limit());
            return new IoBufferOutput(new IoBufferBuilder(ioBuffer.rePositioned(0, offset)), this::onOutputClosed);
        }

        private void onOutputClosed(IoBuffer ioBuffer) {
            if (ioBuffer.length() != 0) {
                map.put(key, ioBuffer);
            } else {
                map.remove(key);
            }
        }
    }
}
