package com.pon.ents.base.ss.impl;

import com.pon.ents.base.io.ProducingOutput;

public interface SsIndexBuilder extends ProducingOutput<SsIndex> {

    void cut(long writtenInputEncodedLength);
}
