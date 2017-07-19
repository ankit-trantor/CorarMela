package com.cpsdbd.corarmela.ResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Genius 03 on 6/18/2017.
 */

public class PlaylistResponse implements Serializable {

    private PageInfo pageInfo;
    private List<Item> items;


    public PlaylistResponse() {
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
