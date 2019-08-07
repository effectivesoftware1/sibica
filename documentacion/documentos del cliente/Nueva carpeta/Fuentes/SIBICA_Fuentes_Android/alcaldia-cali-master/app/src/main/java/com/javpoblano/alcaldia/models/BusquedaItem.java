package com.javpoblano.alcaldia.models;

/**
 * Created by javpoblano on 05/01/2017.
 */

public class BusquedaItem {
    String latitud,longitud,
            direccion,tipo,
            matricula,comuna,
            barrio,predial;

    public BusquedaItem(String latitud, String longitud, String direccion, String tipo, String matricula, String comuna, String barrio, String predial) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.tipo = tipo;
        this.matricula = matricula;
        this.comuna = comuna;
        this.barrio = barrio;
        this.predial = predial;
    }

    public BusquedaItem() {
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getPredial() {
        return predial;
    }

    public void setPredial(String predial) {
        this.predial = predial;
    }
}
