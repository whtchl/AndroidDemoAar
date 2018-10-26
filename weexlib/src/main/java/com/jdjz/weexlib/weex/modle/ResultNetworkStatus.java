package com.jdjz.weexlib.weex.modle;


import com.jdjz.weexlib.weex.modle.entity.NetworkStatusEntity;

public class ResultNetworkStatus extends ResultTemp{
    private NetworkStatusEntity responseResult;

    public NetworkStatusEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(NetworkStatusEntity responseResult) {
        this.responseResult = responseResult;
    }
}
