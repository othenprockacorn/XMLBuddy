package com.acorn.xmlsnap.model;

public class NodeFilter {

    private String nameFilter;
    private String valueFilter;
    private boolean isNot;
    private boolean isAttribute;


    public NodeFilter(String nameFilter, String valueFilter, boolean isNot, boolean isAttribute) {
        this.nameFilter = nameFilter;
        this.valueFilter = valueFilter;
        this.isNot = isNot;
        this.isAttribute = isAttribute;
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

    public boolean isNot() {return isNot;}

    public boolean isAttribute() {return isAttribute;}
}
