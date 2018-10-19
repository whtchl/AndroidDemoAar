package com.jude.utils.permission;

import android.Manifest;

public class PermissionNameList {
    public static final String[] PERMISSIONS_LOCATION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

    public static final String[] PERMISSIONS_CONTACTS = new String[]{Manifest.permission.WRITE_CONTACTS,Manifest.permission.GET_ACCOUNTS,Manifest.permission.READ_CONTACTS};

    public static final String[] PERMISSIONS_PHONE= new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG};


    public static final String[] PERMISSIONS_STORAGE= new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
}
