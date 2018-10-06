package com.jdjz.weex.modle;

import com.jdjz.contact.ContactInfo;

import java.util.List;

public class ResultContact {

    String  responseCode;
    String  responseMsg;
    List<ContactInfo> responseResult;
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public List<ContactInfo> getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(List<ContactInfo> responseResult) {
        this.responseResult = responseResult;
    }


}
