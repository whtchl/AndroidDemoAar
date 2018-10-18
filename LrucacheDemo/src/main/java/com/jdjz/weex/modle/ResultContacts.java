package com.jdjz.weex.modle;

import com.jdjz.contact.ContactInfo;

import java.util.ArrayList;

public class ResultContacts extends ResultTemp {
    private ArrayList<ContactInfo> responseResult;

    public ArrayList<ContactInfo> getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ArrayList<ContactInfo> responseResult) {
        this.responseResult = responseResult;
    }
}
