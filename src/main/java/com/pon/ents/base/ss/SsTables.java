package com.pon.ents.base.ss;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.InputOpeners;
import com.pon.ents.base.io.IoBuffer;
import com.pon.ents.base.ss.impl.EmptySsTable;
import com.pon.ents.base.ss.impl.ListSsTable;
import com.pon.ents.base.ss.impl.MergingSsTable;

public abstract class SsTables {

    private static final SsTable EMPTY = new EmptySsTable();

    /**
     * Returns an SsTable that will contain all the sorted strings from the given {@link SsTable}s.
     * <p>
     * This will most likely be achieved by returning a view, since that is what {@link SsTable}s are good at.
     */
    public static SsTable add(SsTable... ssTables) {
        return add(Arrays.asList(ssTables));
    }

    /**
     * Returns an SsTable that will contain all the sorted strings from the given {@link SsTable}s.
     * <p>
     * This will most likely be achieved by returning a view, since that is what {@link SsTable}s are good at.
     */
    public static SsTable add(Collection<SsTable> ssTables) {
        switch (ssTables.size()) {
            case 0: return SsTables.empty();
            case 1: return Iterables.getOnlyElement(ssTables);
            // TODO: PERFORMANCE: introduce something like a HasSsIndex (internal API) to detect non-overlapping tables
            default: return new MergingSsTable(ssTables);
        }
    }

    /**
     * Returns an empty {@link SsTable}.
     */
    public static SsTable empty() {
        return EMPTY;
    }

    /**
     * Returns a static {@link SsTable} that will serve the {@link Input}s opened from the given {@link IoBuffer}s
     * (which do not need to be sorted).
     */
    public static SsTable from(IoBuffer... ioBuffers) {
        Arrays.sort(ioBuffers, SortedIoBuffers.comparator());
        return fromSorted(Arrays.asList(ioBuffers));
    }

    /**
     * Returns a static {@link SsTable} that will serve the {@link Input}s opened from the given {@link IoBuffer}s
     * (which do not need to be sorted).
     */
    public static SsTable from(Iterator<IoBuffer> ioBuffers) {
        List<IoBuffer> sortedIoBuffers = Streams.stream(ioBuffers)
                .sorted(SortedIoBuffers.comparator())
                .collect(Collectors.toList());
        return fromSorted(sortedIoBuffers);
    }

    private static SsTable fromSorted(List<IoBuffer> sortedIoBuffers) {
        return new ListSsTable(Lists.transform(sortedIoBuffers, InputOpeners::ofIoBuffer));
    }
}
