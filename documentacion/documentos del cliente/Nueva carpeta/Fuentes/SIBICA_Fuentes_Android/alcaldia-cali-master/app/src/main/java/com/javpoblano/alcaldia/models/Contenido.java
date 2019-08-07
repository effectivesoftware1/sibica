package com.javpoblano.alcaldia.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by javpoblano on 12/21/16.
 */

public class Contenido {
    String nombre,direccion,tipo,predial,matricula,coordenadas;
    LatLng parsedCoordenate;

    public LatLng getParsedCoordenate() {
        return parsedCoordenate;
    }

    public void setParsedCoordenate(LatLng parsedCoordenate) {
        this.parsedCoordenate = parsedCoordenate;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPredial() {
        return predial;
    }

    public void setPredial(String predial) {
        this.predial = predial;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
        String[] auxCoordenadas = coordenadas.split(",");
        double lat = Double.parseDouble(auxCoordenadas[0].trim());
        double lng = Double.parseDouble(auxCoordenadas[1].trim());
        this.parsedCoordenate = new LatLng(lat,lng);
    }
}
