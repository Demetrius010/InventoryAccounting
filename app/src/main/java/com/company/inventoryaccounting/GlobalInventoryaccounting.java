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

    private String equipmentData;
    public String getEquipmentData() {
        return equipmentData;
    }
    public void setEquipmentData(String data) {
        this.equipmentData = data;
    }

    private String staffData;
    public String getStaffData() {
        return staffData;
    }
    public void setStaffData(String data) {
        this.staffData = data;
    }

    private String addressesData;
    public String getAddressesData() {
        return addressesData;
    }
    public void setAddressesData(String data) {
        this.addressesData = data;
    }


}
/*
* // set
((MyApplication) this.getApplication()).setSomeVariable("foo");

// get
String s = ((MyApplication) this.getApplication()).getSomeVariable();
* */