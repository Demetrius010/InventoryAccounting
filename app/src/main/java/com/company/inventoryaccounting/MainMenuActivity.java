package com.company.inventoryaccounting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements BackgroundWorkerResponse {

    Button btnAllEquip, bntEmployee, btnPlaces;
    EditText search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        btnAllEquip = (Button) findViewById(R.id.btnAllEquip);
        bntEmployee = (Button) findViewById(R.id.btnEmployee);
        btnPlaces = (Button) findViewById(R.id.btnPlaces);
        search = findViewById(R.id.etSearchByBarcode);
    }

    @Override
    protected void onStart() {
        new BackgroundWorker(this, this).execute("getEquip");
        new BackgroundWorker(this, this).execute("getStaff");
        new BackgroundWorker(this, this).execute("getAddresses");
        super.onStart();
    }

    public void onAllEquipButton(View view) {
        Intent intent = new Intent(".ToolListActivity");
        startActivity(intent);
}


    public void onEmployeeButton(View view) {
        Intent intent = new Intent(".StaffListActivity");
        startActivity(intent);
    }

    public void onPlacesButton(View view) {
        Intent intent = new Intent(".PlaceListActivity");
        startActivity(intent);
    }


    @Override
    public void processFinish(String output, String typeFinishedProc) {
        if (typeFinishedProc.equals("getEquip")) {
            ((GlobalInventoryaccounting)this.getApplication()).setEquipmentData(output);
            //Log.d("Value","EquipmentData = " + ((GlobalInventoryaccounting) this.getApplication()).getEquipmentData());
        }
        else if(typeFinishedProc.equals("getStaff")){
            ((GlobalInventoryaccounting)this.getApplication()).setStaffData(output);
            //Log.d("Value","StaffData = " + ((GlobalInventoryaccounting) this.getApplication()).getStaffData());
        }
        else if(typeFinishedProc.equals("getAddresses")){
            ((GlobalInventoryaccounting)this.getApplication()).setAddressesData(output);
            //Log.d("Value","AddressesData = " + ((GlobalInventoryaccounting) this.getApplication()).getAddressesData());
        }
    }
}

