package com.cpsdbd.corarmela.ResponseModel;

import java.io.Serializable;

/**
 * Created by Genius 03 on 6/18/2017.
 */

public class Thumbnails implements Serializable {
    private High high;

    public Thumbnails() {
    }

    public High getHigh() {
        return high;
    }

    public void setHigh(High high) {
        this.high = high;
    }
}
