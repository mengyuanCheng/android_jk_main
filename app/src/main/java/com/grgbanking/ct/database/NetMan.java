package com.grgbanking.ct.database;

/**
 * 网点人员
 * Created by cmy on 2016/9/14.
 */
public class NetMan {

    private String RFID;
    private String name;
    private int bankid;//网点号

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBankid() {
        return bankid;
    }

    public void setBankid(int bankid) {
        this.bankid = bankid;
    }

    public NetMan(){

    }

    public NetMan(String RFID,String name,int bankid){
        this.RFID=RFID;
        this.name=name;
        this.bankid=bankid;
    }
}
