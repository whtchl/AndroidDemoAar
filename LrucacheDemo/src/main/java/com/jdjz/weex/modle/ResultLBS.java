package com.jdjz.weex.modle;

import com.jdjz.weex.modle.RequestScanParams.RequestLBSParams;
import com.jdjz.weex.modle.entity.LBSEntity;

public class ResultLBS extends ResultTemp {
    private LBSEntity lbsEntity;

    public LBSEntity getLbsEntity() {
        return lbsEntity;
    }

    public void setLbsEntity(LBSEntity lbsEntity) {
        this.lbsEntity = lbsEntity;
    }
}
