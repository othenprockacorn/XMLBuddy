package com.acorn.xmlsnap.model;

public class NodeFilter {

    private final String nameFilter;
    private final String valueFilter;
    private final boolean isNot;
    private final boolean isAttribute;

    int hitCount;

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public NodeFilter(String nameFilter, String valueFilter, boolean isNot, boolean isAttribute) {
        this.nameFilter = nameFilter;
        this.valueFilter = valueFilter;
        this.isNot = isNot;
        this.isAttribute = isAttribute;
        hitCount = 0;
    }


    public String getNameFilter() {
        return nameFilter;
    }

    public String getValueFilter() {
        return valueFilter;
    }

    public boolean getIsNot() {return isNot;}

    public boolean getIsAttribute() {return isAttribute;}
}
