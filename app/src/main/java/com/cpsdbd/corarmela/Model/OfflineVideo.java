package com.cpsdbd.corarmela.Model;

import java.io.Serializable;

/**
 * Created by Genius 03 on 7/8/2017.
 */

public class OfflineVideo implements Serializable {
    private String name;
    private String path;


    public OfflineVideo() {
    }

    public OfflineVideo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
