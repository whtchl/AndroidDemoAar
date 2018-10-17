package com.jdjz.weex.modle;

import com.jdjz.weex.modle.entity.ChooseImagesFileInfoEntity;
import com.jdjz.weex.modle.entity.TempFile;

import java.util.List;

public class ResultChooseImages extends  ResultTemp {

    private ChooseImagesFileInfoEntity responseResult;

    public ChooseImagesFileInfoEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ChooseImagesFileInfoEntity responseResult) {
        this.responseResult = responseResult;
    }
}
