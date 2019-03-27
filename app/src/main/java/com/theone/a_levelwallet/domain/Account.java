package com.theone.a_levelwallet.domain;

/**
 * Created by lh on 2015/8/18.
 */
public class Account {
    private String name;
    private String phoneNumber;
    private String psdForLogin;
    private String gesturePsd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPsdForLogin() {
        return psdForLogin;
    }

    public void setPsdForLogin(String psdForLogin) {
        this.psdForLogin = psdForLogin;
    }

    public String getGesturePsd() {
        return gesturePsd;
    }

    public void setGesturePsd(String gesturePsd) {
        this.gesturePsd = gesturePsd;
    }

}
