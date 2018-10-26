package com.jdjz.weexlib.weex.modle.RequestParams;

import java.util.List;

public class RequestPreviewImagesParams {
    private String current;
    private List<String> urls;

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
