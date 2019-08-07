package com.javpoblano.alcaldia.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by javpoblano on 12/30/16.
 */

public class ConstruccionParsed {
    String id;
    List<LatLng> latLngs;
    String color;
    LatLng coordenadas;
    int capaId;
    String capa;

    public ConstruccionParsed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LatLng getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(LatLng coordenadas) {
        this.coordenadas = coordenadas;
    }

    public int getCapaId() {
        return capaId;
    }

    public void setCapaId(int capaId) {
        this.capaId = capaId;
    }

    public String getCapa() {
        return capa;
    }

    public void setCapa(String capa) {
        this.capa = capa;
    }
}
