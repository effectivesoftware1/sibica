package com.javpoblano.alcaldia.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by javpoblano on 12/21/16.
 */

public class Predio {
    List<com.google.android.gms.maps.model.LatLng> latLngs;
    String color;
    Contenido contenido;
    List<String> construcciones;
    public String fraude;

    public List<LatLng> getLatLng() {
        return latLngs;
    }

    public void setLatLng(List<LatLng> latLng) {
        latLngs = latLng;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Contenido getContenido() {
        return contenido;
    }

    public void setContenido(Contenido contenido) {
        this.contenido = contenido;
    }

    public List<String> getConstrucciones() {
        return construcciones;
    }

    public void setConstrucciones(List<String> construcciones) {
        this.construcciones = construcciones;
    }
}
