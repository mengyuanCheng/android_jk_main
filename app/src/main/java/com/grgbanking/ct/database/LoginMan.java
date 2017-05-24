package com.grgbanking.ct.database;

/**
 * 登陆人员
 * Created by cmy on 2016/9/14.
 */
public class LoginMan {

    //RFID编号
    private String RFID;
    //登录名
    private String username;
    //登陆密码
    private String password;
    //个人姓名
    private String name;
    //线路
    private String line;
    //判断是网点人员or押运人员
    private String flag;

    public LoginMan(){

    }

    public LoginMan(String RFID,String username,String password,String name,String line,String flag){
        this.RFID=RFID;
        this.username=username;
        this.password=password;
        this.name=name;
        this.line=line;
        this.flag=flag;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
