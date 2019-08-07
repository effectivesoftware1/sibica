package com.javpoblano.alcaldia.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by javpoblano on 17/01/2017.
 */

public class LineaAmoblamiento {
    String id;
    List<LatLng> line;
    String color;

    public LineaAmoblamiento() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LatLng> getLine() {
        return line;
    }

    public void setLine(List<LatLng> line) {
        this.line = line;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
