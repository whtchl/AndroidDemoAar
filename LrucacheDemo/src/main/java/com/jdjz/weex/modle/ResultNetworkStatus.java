package com.jdjz.weex.modle;

import com.jdjz.weex.modle.entity.NetworkStatusEntity;

public class ResultNetworkStatus extends ResultTemp{
    public NetworkStatusEntity getNetworkStatusEntity() {
        return networkStatusEntity;
    }

    public void setNetworkStatusEntity(NetworkStatusEntity networkStatusEntity) {
        this.networkStatusEntity = networkStatusEntity;
    }

    private NetworkStatusEntity networkStatusEntity;
}
