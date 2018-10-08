package com.jdjz.weex.modle.entity;

public class NetworkStatusEntity {

    private int type;
    private boolean isNetWorkAvilable;
    public boolean isNetWorkAvilable() {
        return isNetWorkAvilable;
    }

    public void setNetWorkAvilable(boolean netWorkAvilable) {
        isNetWorkAvilable = netWorkAvilable;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
