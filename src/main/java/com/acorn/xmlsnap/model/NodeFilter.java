package com.acorn.xmlsnap.model;

import javafx.beans.property.SimpleStringProperty;

public class NodeFilter {

    private final SimpleStringProperty nameFilter = new SimpleStringProperty("");;
    private final SimpleStringProperty attributeName = new SimpleStringProperty("");;
    private final SimpleStringProperty valueFilter = new SimpleStringProperty("");;
    private final boolean isNot;
    int hitCount;

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public NodeFilter(String nameFilter, String attributeName, String valueFilter, boolean isNot) {
        this.nameFilter.set(nameFilter);
        this.attributeName.set(attributeName);
        this.valueFilter.set(valueFilter);
        this.isNot = isNot;
        hitCount = 0;
    }


    public SimpleStringProperty getNameFilter() {return nameFilter;
    }
    public SimpleStringProperty getAttributeName() {return attributeName;}
    public SimpleStringProperty getValueFilter() {
        return valueFilter;
    }
    public boolean getIsNot() {return isNot;}

}
