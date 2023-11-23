package com.acorn.xmlsnap.model;

public class NodeFilter {

    private String nameFilter;
    private String valueFilter;

    public NodeFilter(String nameFilter, String valueFilter) {
        this.nameFilter = nameFilter;
        this.valueFilter = valueFilter;
    }


    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public String getValueFilter() {
        return valueFilter;
    }

    public void setValueFilter(String valueFilter) {
        this.valueFilter = valueFilter;
    }
}
