package com.jdjz.weexlib.weex.modle;


import com.jdjz.weexlib.weex.modle.entity.ScanEntity;

public class ResultScan extends ResultTemp {
    private ScanEntity responseResult;

    public ScanEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ScanEntity responseResult) {
        this.responseResult = responseResult;
    }
}
