package com.cpsdbd.corarmela.ResponseModel;

import java.io.Serializable;

/**
 * Created by Genius 03 on 6/18/2017.
 */

public class Snippet implements Serializable {
    private Thumbnails thumbnails;
    private ResourceId resourceId;


    public Snippet() {
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }
}
