package com.example.sw2.entity;

import java.io.Serializable;
import java.util.HashMap;

public class StorageServiceResponse extends HashMap<String, String> implements Serializable {

    private String fileName;
    private String url;
    private String status;
    private String msg;



    public StorageServiceResponse(){
        status="";
    }

    public StorageServiceResponse(String status, String msg){
        this.status=status;
        this.msg = msg;
    }

    public StorageServiceResponse(String fileName, String url, String status, String msg){
        this.fileName = fileName;
        this.url = url;
        this.status = status;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess(){
        return this.status.equals("exito");
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
