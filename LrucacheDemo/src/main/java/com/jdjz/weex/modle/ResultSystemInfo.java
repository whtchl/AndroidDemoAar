package com.jdjz.weex.modle;

import com.jdjz.weex.modle.entity.NetworkStatusEntity;
import com.jdjz.weex.modle.entity.SystemInfoEntity;

public class ResultSystemInfo extends ResultTemp{
    public SystemInfoEntity getSystemInfoEntity() {
        return systemInfoEntity;
    }

    public void setSystemInfoEntity(SystemInfoEntity systemInfoEntity) {
        this.systemInfoEntity = systemInfoEntity;
    }

    private SystemInfoEntity systemInfoEntity;


}
