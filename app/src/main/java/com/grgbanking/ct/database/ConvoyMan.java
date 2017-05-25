package com.grgbanking.ct.database;

/**
 * 押运人员
 * Created by cmy on 2016/9/14.
 */
public class ConvoyMan {

    /**
     *
     */
    private String name;
    private String RFID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public ConvoyMan(){

    }

    public ConvoyMan(String name,String RFID){
        this.name=name;
        this.RFID=RFID;
    }
}
