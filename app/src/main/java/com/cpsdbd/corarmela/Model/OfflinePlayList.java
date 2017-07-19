package com.cpsdbd.corarmela.Model;

import java.io.Serializable;

/**
 * Created by Genius 03 on 7/6/2017.
 */

public class OfflinePlayList implements Serializable{
    private String tempName;
    private String name;

    public OfflinePlayList() {
    }

    public OfflinePlayList(String tempName, String name) {
        this.tempName = tempName;
        this.name = name;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
