package com.pon.ents.base.ss.impl;

import com.pon.ents.base.io.Input;
import com.pon.ents.base.serie.Serie;
import com.pon.ents.base.serie.Series;
import com.pon.ents.base.ss.SsTable;

public class EmptySsTable implements SsTable {

    @Override
    public Serie<Input> get(Input from, Input to) {
        return Series.empty();
    }
}
