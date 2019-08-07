package com.javpoblano.alcaldia.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by javpoblano on 09/01/2017.
 */

public class AmoblamientoPunto implements ClusterItem{
    private LatLng position;
    private int id;

    public AmoblamientoPunto()
    {

    }

    public AmoblamientoPunto(int id, double lat, double lng)
    {
        position= new LatLng(lat,lng);
        this.id=id;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(double lat, double lng) {
        position= new LatLng(lat,lng);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
