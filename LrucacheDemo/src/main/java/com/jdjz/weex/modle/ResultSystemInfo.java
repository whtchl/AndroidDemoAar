package com.jdjz.weex.modle;

import com.jdjz.weex.modle.entity.NetworkStatusEntity;
import com.jdjz.weex.modle.entity.SystemInfoEntity;

public class ResultSystemInfo extends ResultTemp{
    private SystemInfoEntity responseResult;


    public SystemInfoEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(SystemInfoEntity responseResult) {
        this.responseResult = responseResult;
    }
}
