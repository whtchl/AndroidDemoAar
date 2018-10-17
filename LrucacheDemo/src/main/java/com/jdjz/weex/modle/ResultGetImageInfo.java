package com.jdjz.weex.modle;

import com.jdjz.weex.modle.entity.GetImageInfoEntity;

public class ResultGetImageInfo extends ResultTemp {
    private GetImageInfoEntity responseResult;

    public GetImageInfoEntity getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(GetImageInfoEntity responseResult) {
        this.responseResult = responseResult;
    }
}
