package com.therapy.nest.shared.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class Response<T> {

    private Boolean status;

    private Integer code;

    private String message;

    private T data;

    private List<T> dataList;

    public Response() {
    }

    public Response(int code, boolean status, String message, T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Response(int code, boolean status, String message, T data, List<T> dataList) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
        this.dataList = dataList;
    }

    public Response(Integer code, boolean status, String message, T data, List<T> dataList) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
        this.dataList = dataList;
    }
}

	
