package com.jdjz.common;

public class TransScanEntity {
    private boolean type;//true = qcode; false=bar
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }


}
