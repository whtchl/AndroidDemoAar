package com.jdjz.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class area {
    @Id
    private String areaid;
    private String areaname;
    @Generated(hash = 482498086)
    public area(String areaid, String areaname) {
        this.areaid = areaid;
        this.areaname = areaname;
    }
    @Generated(hash = 539558592)
    public area() {
    }
    public String getAreaid() {
        return this.areaid;
    }
    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }
    public String getAreaname() {
        return this.areaname;
    }
    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }
}
