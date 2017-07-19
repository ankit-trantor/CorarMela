package com.cpsdbd.corarmela.ResponseModel;

import java.io.Serializable;

/**
 * Created by Genius 03 on 6/18/2017.
 */

public class Item implements Serializable {

    private String id;
    private Snippet snippet;

    public Item() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }
}
