package com.cpsdbd.corarmela.ResponseModel;

import java.io.Serializable;

/**
 * Created by Genius 03 on 6/18/2017.
 */

public class PageInfo implements Serializable {
    private int totalResults;
    private int resultsPerPage;

    public PageInfo() {
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }
}
