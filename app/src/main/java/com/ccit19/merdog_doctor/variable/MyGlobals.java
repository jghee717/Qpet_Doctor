package com.ccit19.merdog_doctor.variable;

public class MyGlobals {
    private String data = "http://jghee717.cafe24.com";
    public String getData()
    {
        return data;
    }
    public void setData(String data)
    {
        this.data = data;
    }
    private static MyGlobals instance = null;

    public static synchronized MyGlobals getInstance(){
        if(null == instance){
            instance = new MyGlobals();
        }
        return instance;
    }
}



