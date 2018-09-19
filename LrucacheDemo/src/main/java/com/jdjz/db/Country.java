package com.jdjz.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Country {
     @Id
    String countryid;
    String name;
@Generated(hash = 981330120)
public Country(String countryid, String name) {
    this.countryid = countryid;
    this.name = name;
}
@Generated(hash = 668024697)
public Country() {
}
public String getCountryid() {
    return this.countryid;
}
public void setCountryid(String countryid) {
    this.countryid = countryid;
}
public String getName() {
    return this.name;
}
public void setName(String name) {
    this.name = name;
}
}
