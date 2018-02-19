package com.pon.ents.base.ss.impl;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.io.Output;

public interface SsLengthCodec {

    // a number of bytes used is returned
    int encode(Output output, long length);

    // the length, or -1 on EOF
    long decode(Input input);
}
