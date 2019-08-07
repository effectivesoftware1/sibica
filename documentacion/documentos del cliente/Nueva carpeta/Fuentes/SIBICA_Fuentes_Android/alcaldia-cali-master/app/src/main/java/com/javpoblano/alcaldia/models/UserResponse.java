package com.javpoblano.alcaldia.models;

/**
 * Created by javpoblano on 06/01/2017.
 */

public class UserResponse {
    int success;
    String msg;
    UserData data;

    public UserResponse() {
    }

    public UserResponse(int success, String msg, UserData data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
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

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }
}
