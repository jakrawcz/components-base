package com.pon.ents.base.ss;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.common.collect.Streams;
import com.pon.ents.base.closeable.CloseableIterator;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Inputs;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.io.IoBuffers;
import com.pon.ents.base.io.Sizes;
import com.pon.ents.base.ss.impl.BlockSsIndexer;
import com.pon.ents.base.ss.impl.IoBufferSsIndexEntry;
import com.pon.ents.base.ss.impl.SsIndex;
import com.pon.ents.base.ss.impl.SsIndex.SsIndexEntry;
import com.pon.ents.base.ss.impl.SsIndexBuilder;
import com.pon.ents.base.ss.impl.SsIndexer;

public class SsTableTest {

    @Test
    public void conformsToComparatorContract() {
        // given
        // when
        int firstGreater = SortedInputs.compare(Inputs.fromByteArray(new byte[] {2, 0}), Inputs.fromByte((byte) 2));
        int firstLesser = SortedInputs.compare(Inputs.fromByteArray(new byte[] {0, 2}), Inputs.fromByte((byte) 2));
        int equal = SortedInputs.compare(Inputs.fromByte((byte) 2), Inputs.fromByte((byte) 2));

        // then
        MatcherAssert.assertThat(firstGreater, Matchers.greaterThan(0));
        MatcherAssert.assertThat(firstLesser, Matchers.lessThan(0));
        MatcherAssert.assertThat(equal, Matchers.equalTo(0));
    }

    @Test
    public void higherValueIsGreater() {
        // given
        // when
        IoBuffer greater = greaterOf(IoBuffers.of(2, 3), IoBuffers.of(2, 4));

        // then
        MatcherAssert.assertThat(greater, Matchers.equalTo(IoBuffers.of(2, 4)));
    }

    @Test
    public void longerIsGreater() {
        // given
        // when
        IoBuffer greater = greaterOf(IoBuffers.of(2, 3, 1), IoBuffers.of(2, 3));

        // then
        MatcherAssert.assertThat(greater, Matchers.equalTo(IoBuffers.of(2, 3, 1)));
    }

    @Test
    public void emptyIsMinimum() {
        // given
        // when
        IoBuffer greater = greaterOf(IoBuffers.of(0), IoBuffers.empty());

        // then
        MatcherAssert.assertThat(greater, Matchers.equalTo(IoBuffers.of(0)));
    }

    @Test
    public void everythingIsLesserThanMaximum() {
        // given
        // when
        Input lesser = SortedInputs.lesserOf(Inputs.fromByte((byte) 255), SortedInputs.maximum());

        // then
        MatcherAssert.assertThat(Inputs.toIoBuffer(lesser), Matchers.equalTo(IoBuffers.of(255)));
    }

    @Test
    public void handlesInfiniteInputWithoutBuffering() {
        // given
        // when
        Input greater = SortedInputs.greaterOf(Inputs.fromIoBuffer(IoBuffers.of(255, 255, 3)), SortedInputs.maximum());

        // then
        MatcherAssert.assertThat(Inputs.toIoBuffer(Inputs.limit(greater, 4)),
                Matchers.equalTo(IoBuffers.of(255, 255, 255, 255)));
    }

    @Test
    public void blockIndexBuilderPicksMinimumIntermediateEntries() {
        // given
        SsIndexer ssIndexer = new BlockSsIndexer(Sizes.bytes(16));
        SsIndexBuilder builder = ssIndexer.builder();

        // when
        builder.write(new byte[] {1, 2, 3});
        builder.cut(7);

        builder.write(new byte[] {2, 2});
        builder.cut(6);

        builder.write(new byte[] {2, 2, 3});
        builder.cut(8);

        builder.write(new byte[] {2, 2, 8, 7});
        builder.cut(6);

        builder.write(new byte[] {2, 2, 9, 7});
        builder.cut(9);

        builder.write(new byte[] {2, 2, 9, 7, 1});
        builder.cut(5);

        builder.write(new byte[] {3, 0, 1});
        builder.cut(3);

        // then
        SsIndex index = builder.produce();
        MatcherAssert.assertThat(Inputs.toIoBuffer(index.readFirst()),
                Matchers.equalTo(IoBuffers.of(1, 2, 3)));
        Iterator<SsIndexEntry> intermediateEntryIterator = index.intermediateEntryIterator();
        MatcherAssert.assertThat(intermediateEntryIterator.next(),
                Matchers.equalTo(new IoBufferSsIndexEntry(IoBuffers.of(2), 13)));
        MatcherAssert.assertThat(intermediateEntryIterator.next(),
                Matchers.equalTo(new IoBufferSsIndexEntry(IoBuffers.of(2, 2, 9), 27)));
        MatcherAssert.assertThat(intermediateEntryIterator.next(),
                Matchers.equalTo(new IoBufferSsIndexEntry(IoBuffers.of(3), 41)));
        MatcherAssert.assertThat(Inputs.toIoBuffer(index.readLast()),
                Matchers.equalTo(IoBuffers.of(3, 0, 1)));
    }

    @Test
    public void selectsFromPresentToPresent() {
        // given
        SsTable ssTable = SsTables.from(IoBuffers.of(0, 0, 1), IoBuffers.of(0, 0, 2), IoBuffers.of(0, 1));

        // when
        List<IoBuffer> selectedIoBuffers = buffer(ssTable.iterator(
                Inputs.fromByteArray(new byte[] {0, 0, 1}),
                Inputs.fromByteArray(new byte[] {0, 1})));

        // then
        MatcherAssert.assertThat(selectedIoBuffers, Matchers.equalTo(Arrays.asList(
                IoBuffers.of(0, 0, 1), IoBuffers.of(0, 0, 2))));
    }

    @Test
    public void selectsFromNonPresentToNonPresent() {
        // given
        SsTable ssTable = SsTables.from(IoBuffers.of(0, 0, 1), IoBuffers.of(0, 0, 2), IoBuffers.of(0, 1));

        // when
        List<IoBuffer> selectedIoBuffers = buffer(ssTable.iterator(
                Inputs.fromByteArray(new byte[] {0, 0, 0, 1}),
                Inputs.fromByteArray(new byte[] {0, 0, 2, 1})));

        // then
        MatcherAssert.assertThat(selectedIoBuffers, Matchers.equalTo(Arrays.asList(
                IoBuffers.of(0, 0, 1), IoBuffers.of(0, 0, 2))));
    }

    @Test
    public void addingSsTablesReturnsMergingView() {
        // given
        SsTable first = SsTables.from(IoBuffers.of(0, 0, 1), IoBuffers.of(0, 0, 2), IoBuffers.of(0, 1));
        SsTable second = SsTables.from(IoBuffers.of(0, 0), IoBuffers.of(0, 1), IoBuffers.of(7));

        // when
        SsTable added = SsTables.add(first, second);

        // then
        List<IoBuffer> addedIoBuffers = buffer(added.iterator(
                Inputs.fromByteArray(new byte[] {0, 0, 0}),
                Inputs.fromByteArray(new byte[] {7})));
        MatcherAssert.assertThat(addedIoBuffers, Matchers.equalTo(Arrays.asList(
                IoBuffers.of(0, 0, 1), IoBuffers.of(0, 0, 2), IoBuffers.of(0, 1), IoBuffers.of(0, 1))));
    }

    @Test
    public void mergingViewIteratesUnbounded() {
        // given
        SsTable first = SsTables.from(IoBuffers.of(0, 0, 2), IoBuffers.of(0, 1));
        SsTable second = SsTables.from(IoBuffers.of(0, 1), IoBuffers.of(7));

        // when
        SsTable added = SsTables.add(first, second);

        // then
        List<IoBuffer> addedIoBuffers = buffer(added.iterator());
        MatcherAssert.assertThat(addedIoBuffers, Matchers.equalTo(Arrays.asList(
                IoBuffers.of(0, 0, 2), IoBuffers.of(0, 1), IoBuffers.of(0, 1), IoBuffers.of(7))));
    }

    private List<IoBuffer> buffer(CloseableIterator<Input> iterator) {
        List<IoBuffer> list = Streams.stream(iterator).map(Inputs::toIoBuffer).collect(Collectors.toList());
        iterator.close();
        return list;
    }

    private IoBuffer greaterOf(IoBuffer first, IoBuffer second) {
        Input greater = SortedInputs.greaterOf(Inputs.fromIoBuffer(first), Inputs.fromIoBuffer(second));
        return Inputs.toIoBuffer(greater);
    }
}
