package com.jdjz.weexlib.weex.modle.RequestParams;

import java.util.List;

public class RequestChooseImagesParams {
    private int count;
    private List<String> sizeType;
    private List<String> sourceType;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getSizeType() {
        return sizeType;
    }

    public void setSizeType(List<String> sizeType) {
        this.sizeType = sizeType;
    }

    public List<String> getSourceType() {
        return sourceType;
    }

    public void setSourceType(List<String> sourceType) {
        this.sourceType = sourceType;
    }
}
