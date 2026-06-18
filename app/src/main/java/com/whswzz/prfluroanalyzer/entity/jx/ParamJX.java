package com.whswzz.prfluroanalyzer.entity.jx;

import com.whswzz.prfluroanalyzer.app.MyApp;

public final class ParamJX {
    private final String appid="PRAIO";
    private final String appsecret="UVubNbngfyx3RY3U6x4k";
    private String sn= MyApp.getApp().getJemenId();


    public String getAppid() {
        return appid;
    }


    public String getParams(){
        return "appid="+appid+"&appsecret="+appsecret+"&sn="+sn;
    }

}
