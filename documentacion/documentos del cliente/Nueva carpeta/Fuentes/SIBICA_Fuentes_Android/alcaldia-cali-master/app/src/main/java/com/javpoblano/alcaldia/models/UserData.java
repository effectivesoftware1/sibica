package com.javpoblano.alcaldia.models;

/**
 * Created by javpoblano on 06/01/2017.
 */

public class UserData {
    String nombre,token;

    public UserData() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
