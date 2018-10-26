package com.jdjz.weexlib.weex.modle;


import com.jdjz.weexlib.weex.modle.entity.ChooseImagesFileInfoEntity;

public class ResultChooseImages extends  ResultTemp {

    private ChooseImagesFileInfoEntity responseResult;

    public ChooseImagesFileInfoEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ChooseImagesFileInfoEntity responseResult) {
        this.responseResult = responseResult;
    }
}
