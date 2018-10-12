package com.jdjz.weex.modle;

import com.jdjz.contact.ContactInfo;

import java.util.ArrayList;

public class ResultContacts extends ResultTemp {
    private ArrayList<ContactInfo> contactInfoArrayList;

    public ArrayList<ContactInfo> getContactInfoArrayList() {
        return contactInfoArrayList;
    }

    public void setContactInfoArrayList(ArrayList<ContactInfo> contactInfoArrayList) {
        this.contactInfoArrayList = contactInfoArrayList;
    }
}
