package com.hs.advertise.mvp.model.bean;

import com.hs.advertise.MyApplication;
import com.hs.advertise.utils.PackageUtil;
import com.lunzn.tool.log.LogUtil;
import com.platform.sdk.m2.request.RequestInit;

import java.util.HashMap;

/**
 * Desc: TODO
 * <p>
 * Author: ganxinrong
 * PackageName: com.hs.advertise.mvp
 * ProjectName: Advertise
 * Date: 2020/3/10 15:34
 */
public class BaseMap {

    private static final String TAG = "Tag_"+ BaseMap.class.getSimpleName();
    private String version;
    private String launcherVer;
    private String launcherChannel;
    private String romChannel;
    private String sn;
    private String timestamp;


    public HashMap<String, Object> getMapData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("version", PackageUtil.getLauncherVsnInfo());
        map.put("launcherVer", RequestInit.getLauncherVsn());//PackageUtil.getLauncherVsnInfo()
        map.put("launcherChannel", RequestInit.getLauncherSign());//"AD001"
        map.put("romChannel", RequestInit.getRomSign());//先传mac，SystemUtil.getEthernetMac()
        map.put("sn", MyApplication.getInstance().getSn());//SystemUtil.getSN(),"009RT3OV"
        map.put("romVer", RequestInit.getRomSign());
        map.put("timestamp", System.currentTimeMillis());
        LogUtil.i(TAG,"MapJson:"+map.toString());
        return map;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLauncherVer() {
        return launcherVer;
    }

    public void setLauncherVer(String launcherVer) {
        this.launcherVer = launcherVer;
    }

    public String getLauncherChannel() {
        return launcherChannel;
    }

    public void setLauncherChannel(String launcherChannel) {
        this.launcherChannel = launcherChannel;
    }

    public String getRomChannel() {
        return romChannel;
    }

    public void setRomChannel(String romChannel) {
        this.romChannel = romChannel;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
