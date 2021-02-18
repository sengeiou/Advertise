package com.hs.advertise.mvp.model.bean;

import com.lunzn.tool.util.CommonUtil;

import java.util.HashMap;

/**
 * Desc: TODO
 * <p>
 * Author: ganxinrong
 * PackageName: com.hs.advertise.mvp
 * ProjectName: Advertise
 * Date: 2020/3/10 15:34
 */
public class AdConfigMap extends BaseMap {

    private long dataVersion;

    private String dataJson;

    @Override
    public HashMap<String, Object> getMapData() {
        HashMap<String, Object> map = super.getMapData();
        if (getDataVersion() != -1) {
            map.put("dataVersion", getDataVersion());
        }
        if (!CommonUtil.isEmpty(getDataJson())) {
            map.put("data", getDataJson());
        }
        return map;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }


    public long getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(long dataVersion) {
        this.dataVersion = dataVersion;
    }

}
