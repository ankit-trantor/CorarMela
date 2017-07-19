package com.cpsdbd.corarmela.ResponseModel;

import java.io.Serializable;

/**
 * Created by Genius 03 on 6/18/2017.
 */

public class ResourceId implements Serializable{
    private String kind;
    private String videoId;

    public ResourceId() {
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
