package com.jdjz.weexlib.weex.modle.entity;

public class SystemInfoEntity {


    String model;    //	手机型号   ok
    int pixelRatio;    //设备像素比
    int windowWidth;    //窗口宽度  ok
    int windowHeight;    //窗口高度  ok
    String language;    //	设置的语言   ok
    String version;    //		版本号  ok  getAppVersionCode
    String storage;    //		设备磁盘容量   ok
    String currentBattery;    //		当前电量百分比   ok
    String system;    //		系统版本  ok
    String platform;    //	系统名：Android，iOS   ok
    int screeWidth;    //屏幕宽度  ok
    int screenHeight;    //屏幕高度  ok
    String brand;    //	手机品牌   ok
    float fontSizeSetting;    //	用户设置字体大小   ok

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPixelRatio() {
        return pixelRatio;
    }

    public void setPixelRatio(int pixelRatio) {
        this.pixelRatio = pixelRatio;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getCurrentBattery() {
        return currentBattery;
    }

    public void setCurrentBattery(String currentBattery) {
        this.currentBattery = currentBattery;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getScreeWidth() {
        return screeWidth;
    }

    public void setScreeWidth(int screeWidth) {
        this.screeWidth = screeWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public float getFontSizeSetting() {
        return fontSizeSetting;
    }

    public void setFontSizeSetting(float fontSizeSetting) {
        this.fontSizeSetting = fontSizeSetting;
    }
}
