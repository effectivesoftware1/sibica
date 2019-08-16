package com.javpoblano.alcaldia.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javpoblano on 12/21/16.
 */

public class AllPrediosResponse {
    int success;
    String msg;
    List<PredioParse> data;

    public AllPrediosResponse() {
        this.data = new ArrayList<>();
    }

    public void addSingleData(PredioParse aux)
    {
        data.add(aux);
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<PredioParse> getData() {
        return data;
    }

    public void setData(List<PredioParse> data) {
        this.data = data;
    }
}
