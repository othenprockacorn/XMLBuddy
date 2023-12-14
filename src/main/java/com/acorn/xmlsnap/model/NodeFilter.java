package com.acorn.xmlsnap.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class NodeFilter {

    private SimpleStringProperty typeFilter = new SimpleStringProperty("");
    private SimpleStringProperty nameFilter = new SimpleStringProperty("");
    private SimpleStringProperty attributeName = new SimpleStringProperty("");
    private SimpleStringProperty evalFilter = new SimpleStringProperty("");
    private SimpleStringProperty valueFilter = new SimpleStringProperty("");
    private SimpleBooleanProperty searchAttribute = new SimpleBooleanProperty();
    int hitCount;

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }


    public NodeFilter() {
        this.typeFilter.set("");
        this.nameFilter.set("");
        this.attributeName.set("");
        this.evalFilter.set("");
        this.valueFilter.set("");
        hitCount = 0;
        searchAttribute.set(false);
    }

    public NodeFilter(String typeFilter, String nameFilter, String attributeName, String evalFilter, String valueFilter) {
        this.typeFilter.set(typeFilter);
        this.nameFilter.set(nameFilter);
        this.attributeName.set(attributeName);
        this.evalFilter.set(evalFilter);
        this.valueFilter.set(valueFilter);
        hitCount = 0;
        searchAttribute.set(false);
    }

    public void setTypeFilter(String typeFilter) {
        this.typeFilter.set(typeFilter);
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter.set(nameFilter);
    }

    public void setAttributeName(String attributeName) {
        this.attributeName.set(attributeName);
    }

    public void setEvalFilter(String evalFilter) {
        this.evalFilter.set(evalFilter);
    }

    public void setValueFilter(String valueFilter) {
        this.valueFilter.set(valueFilter);
    }

    public SimpleStringProperty getTypeFilter() {return typeFilter;}
    public SimpleStringProperty getNameFilter() {return nameFilter;}
    public SimpleStringProperty getAttributeName() {return attributeName;}
    public SimpleStringProperty getEvalFilter() {return evalFilter;}
    public SimpleStringProperty getValueFilter() {return valueFilter; }

    public SimpleBooleanProperty getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(SimpleBooleanProperty searchAttribute) {
        this.searchAttribute = searchAttribute;
    }
}
