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
}
/*
* // set
((MyApplication) this.getApplication()).setSomeVariable("foo");

// get
String s = ((MyApplication) this.getApplication()).getSomeVariable();
* */