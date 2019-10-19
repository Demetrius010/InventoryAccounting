package com.company.inventoryaccounting;

import android.app.Application;

public class GlobalInventoryaccounting extends Application {

    private Boolean adminFlag;
    public Boolean isAdmin() {
        return adminFlag;
    }
    public void setAdminFlag(boolean admin) {
        this.adminFlag = admin;
    }

    private String addressesData;
    public String getAddressesData() {
        return addressesData;
    }
    public void setAddressesData(String data) {
        this.addressesData = data;
    }

    private String responsibleData;
    public String getResponsibleData() {
        return responsibleData;
    }
    public void setResponsibleData(String data) {
        this.responsibleData = data;
    }
}
/*
* // set
((MyApplication) this.getApplication()).setSomeVariable("foo");

// get
String s = ((MyApplication) this.getApplication()).getSomeVariable();
* */