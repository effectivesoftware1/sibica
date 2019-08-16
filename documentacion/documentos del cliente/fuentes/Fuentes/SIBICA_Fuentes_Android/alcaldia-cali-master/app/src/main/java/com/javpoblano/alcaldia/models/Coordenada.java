package com.javpoblano.alcaldia.models;

/**
 * Created by javpoblano on 12/21/16.
 */

public class Coordenada {
    double lng;
    double lat;

    public Coordenada(double lat, double lng) {
        this.lng = lng;
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
