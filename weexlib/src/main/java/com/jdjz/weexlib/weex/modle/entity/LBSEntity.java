package com.jdjz.weexlib.weex.modle.entity;

public class LBSEntity {
    
    String longitude;                   //                	经度
    String latitude;                    //            	纬度
    String accuracy;                    //          	精确度，单位m
    String horizontalAccuracy;          //          	水平精确度，单位m
    String country;                     //          	国家(type>0生效)
    String countryCode;                   //            	国家编号 (type>0生效)
    String province;                 //          	省份(type>0生效)
    String city;                  //          	城市(type>0生效)
    String cityAdcode;                   //            	城市级别的地区代码(type>0生效)
    String district;                 //          	区县(type>0生效)
    String districtAdcode;                   //                	区县级别的地区代码(type>0生效)
    Street  streetObject;                 //          		需要街道级别逆地理的才会有的字段，街道门牌信息，结构是：{street, number} (type>1生效)
    String pois;                 //          	需要POI级别逆地理的才会有的字段，定位点附近的 POI 信息，结构是：{name, azzress}（type>2生效）

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    public void setHorizontalAccuracy(String horizontalAccuracy) {
        this.horizontalAccuracy = horizontalAccuracy;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityAdcode() {
        return cityAdcode;
    }

    public void setCityAdcode(String cityAdcode) {
        this.cityAdcode = cityAdcode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrictAdcode() {
        return districtAdcode;
    }

    public void setDistrictAdcode(String districtAdcode) {
        this.districtAdcode = districtAdcode;
    }

    public Street getStreetObject() {
        return streetObject;
    }

    public void setStreetObject(Street streetObject) {
        this.streetObject = streetObject;
    }

    public String getPois() {
        return pois;
    }

    public void setPois(String pois) {
        this.pois = pois;
    }

}
