package com.company.inventoryaccounting;

import android.app.Application;

import java.util.ArrayList;

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

    private ArrayList<String> allCategoryList = new ArrayList<String>();;
    public ArrayList<String> getAllCategoryList() {
        return allCategoryList;
    }
    public void setAllCategoryList(ArrayList<String> newList) {
        this.allCategoryList = newList;
    }

    private ArrayList<String> allPositionList = new ArrayList<String>();;
    public ArrayList<String> getAllPositionList() {
        return allPositionList;
    }
    public void setAllPositionList(ArrayList<String> newList) {
        this.allPositionList = newList;
    }

    private ArrayList<String> allTeamList = new ArrayList<String>();;
    public ArrayList<String> getAllTeamList() {
        return allTeamList;
    }
    public void setAllTeamList(ArrayList<String> newList) {
        this.allTeamList = newList;
}
}
/*
* // set
((MyApplication) this.getApplication()).setSomeVariable("foo");

// get
String s = ((MyApplication) this.getApplication()).getSomeVariable();
* */