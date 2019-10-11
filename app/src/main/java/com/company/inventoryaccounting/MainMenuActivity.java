package com.company.inventoryaccounting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity implements BackgroundWorkerResponse {

    Button btnAllEquip, btnBrokenEquip, btnEquipUnderRepair, bntAddNewPlace, btnAddNewEquip;
    Spinner spnAddress;
    JSONObject jsonObject;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        btnAllEquip = (Button)findViewById(R.id.btnAllEquip);
        btnBrokenEquip = (Button)findViewById(R.id.btnBrokenEquip);
        btnEquipUnderRepair = (Button)findViewById(R.id.btnEquipUnderRepair);
        bntAddNewPlace = (Button)findViewById(R.id.bntAddNewPlace);
        btnAddNewEquip = (Button)findViewById(R.id.btnAddNewEquip);
        spnAddress = (Spinner)findViewById(R.id.spnAddress);

        ifAdmin();
        getAddresses();
    }

    private void ifAdmin(){
        if (((GlobalInventoryaccounting) this.getApplication()).isAdmin())
        {
            Log.d("Value","admin = " + ((GlobalInventoryaccounting) this.getApplication()).isAdmin());
            bntAddNewPlace.setVisibility(View.VISIBLE);
            btnAddNewEquip.setVisibility(View.VISIBLE);
        }
        else{
            bntAddNewPlace.setVisibility(View.INVISIBLE);
            btnAddNewEquip.setVisibility(View.INVISIBLE);
        }
    }

    private  void getAddresses(){
        String type = "getAddresses";
        new BackgroundWorker(this, this).execute(type);
    }

    public void onAllEquipButton(View view){

    }

    @Override
    public void processFinish(String output) {

        try {
            String address;
            List<String> allAddresses = new ArrayList<String>();
            jsonObject = new JSONObject(output);
            jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                address = jsonObj.getString("short_address");
                allAddresses.add(address);
                //Log.d("Value","address =" + address);
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allAddresses);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAddress.setAdapter(dataAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}