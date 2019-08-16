package com.javpoblano.alcaldia.models;

import java.util.List;

/**
 * Created by javpoblano on 05/01/2017.
 */

public class BusquedaData {
    String msg;
    int success;
    List<BusquedaItem> data;


    public BusquedaData(String msg, int success, List<BusquedaItem> data) {
        this.msg = msg;
        this.success = success;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<BusquedaItem> getData() {
        return data;
    }

    public void setData(List<BusquedaItem> data) {
        this.data = data;
    }
}
