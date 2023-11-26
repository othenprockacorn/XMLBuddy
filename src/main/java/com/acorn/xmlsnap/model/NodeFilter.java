package com.acorn.xmlsnap.model;

public class NodeFilter {

    private final String nameFilter;
    private final String attributeName;
    private final String valueFilter;
    private final boolean isNot;
    int hitCount;

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public NodeFilter(String nameFilter, String attributeName, String valueFilter, boolean isNot) {
        this.nameFilter = nameFilter;
        this.attributeName = attributeName;
        this.valueFilter = valueFilter;
        this.isNot = isNot;
        hitCount = 0;
    }


    public String getNameFilter() {
        return nameFilter;
    }
    public String getAttributeName() {return attributeName;}
    public String getValueFilter() {
        return valueFilter;
    }
    public boolean getIsNot() {return isNot;}

}
