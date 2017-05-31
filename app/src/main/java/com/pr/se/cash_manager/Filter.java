package com.pr.se.cash_manager;

import java.io.Serializable;
import java.util.List;

public class Filter implements Serializable{
    private String filter;
    private boolean check;
    private List<Filter> subfilter;

    public Filter(String filter, boolean check) {
        this.filter = filter;
        this.check = check;
    }

    public Filter(String filter, boolean check, List<Filter> subfilter) {
        this.filter = filter;
        this.check = check;
        this.subfilter = subfilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public List<Filter> getSubfilter() {
        return subfilter;
    }

    public void setSubfilter(List<Filter> subfilter) {
        this.subfilter = subfilter;
    }
}
