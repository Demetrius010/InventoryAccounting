package com.company.inventoryaccounting;

import android.content.Intent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements BackgroundWorkerResponse {

    Button btnAllEquip, btnBrokenEquip, btnEquipUnderRepair, bntAddNewPlace, btnAddNewEquip;
    Spinner spnAddress;
    JSONObject jsonObject;
    JSONArray jsonArray;
    Map<String, String> allAddresses;

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

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
        String type = "getAllEquip";
        String selectedAddressId = getKeyByValue(allAddresses, spnAddress.getSelectedItem().toString());
        //Log.d("Value", "selectedAddressId = " + selectedAddressId);
        new BackgroundWorker(this, this).execute(type, selectedAddressId);
    }


    public void onBrokenEquipButton(View view){
        String type = "getBrokenEquip";
        String selectedAddressId = getKeyByValue(allAddresses, spnAddress.getSelectedItem().toString());
        new BackgroundWorker(this, this).execute(type, selectedAddressId);
    }

    public void onEquipUnderRepairButton(View view){
        String type = "getEquipUnderRepair";
        String selectedAddressId = getKeyByValue(allAddresses, spnAddress.getSelectedItem().toString());
        new BackgroundWorker(this, this).execute(type, selectedAddressId);
    }

    public void onAddNewPlaceButton(View view){
        String type = "getAddNewPlaceButton";
        new BackgroundWorker(this, this).execute(type);
    }

    public void onAddNewEquipButton(View view){
        String type = "getAddNewEquipButton";
        new BackgroundWorker(this, this).execute(type);
    }



    @Override
    public void processFinish(String output, String typeFinishedProc) {
        if(typeFinishedProc.equals("getAddresses"))
        {
            try {
                String addressId, address;
                //List<String> allAddresses = new ArrayList<String>();
                allAddresses = new HashMap<String, String>();//map.put("dog", "type of animal");//System.out.println(map.get("dog"));
                jsonObject = new JSONObject(output);
                jsonArray = jsonObject.getJSONArray("server_response");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    address = jsonObj.getString("short_address");
                    //allAddresses.add(address);
                    addressId = jsonObj.getString("id");
                    allAddresses.put(addressId, address);
                    //Log.d("Value", "addressId = " + addressId + " address = " + allAddresses.get(addressId));
                }
                //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allAddresses);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(allAddresses.values()));
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnAddress.setAdapter(dataAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (typeFinishedProc.equals("getAllEquip")) {
            try {
                String name, inventory_num, category, responsible, space, equip_condition;
                jsonObject = new JSONObject(output);
                jsonArray = jsonObject.getJSONArray("server_response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    name = jsonObj.getString("name");
                    inventory_num = jsonObj.getString("inventory_num");
                    category = jsonObj.getString("category");
                    responsible = jsonObj.getString("responsible");
                    space = jsonObj.getString("space");
                    equip_condition = jsonObj.getString("equip_condition");
                    //Log.d("Values", "getAllEquip name = " + name + " inventoryNum = " + inventory_num + " category = " + category + " responsible = " + responsible + " space = " + space + " equip_condition = " + equip_condition);
                }
                Intent intent = new Intent(".ToolListActivity");
                intent.putExtra("jsonArray", jsonArray.toString()); //in new act in onCreate() {.... variable = getIntent().getExtras().getString("NAME")}
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (typeFinishedProc.equals("getBrokenEquip")) {
            try {

                String name, inventory_num, category, responsible, space, equip_condition;
                jsonObject = new JSONObject(output);
                jsonArray = jsonObject.getJSONArray("server_response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    name = jsonObj.getString("name");
                    inventory_num = jsonObj.getString("inventory_num");
                    category = jsonObj.getString("category");
                    responsible = jsonObj.getString("responsible");
                    space = jsonObj.getString("space");
                    equip_condition = jsonObj.getString("equip_condition");
                    Log.d("Values", "getBrokenEquip name = " + name + " inventoryNum = " + inventory_num + " category = " + category + " responsible = " + responsible + " space = " + space + " equip_condition = " + equip_condition);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (typeFinishedProc.equals("getEquipUnderRepair")) {
            try {

                String name, inventory_num, category, responsible, space, equip_condition;
                jsonObject = new JSONObject(output);
                jsonArray = jsonObject.getJSONArray("server_response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    name = jsonObj.getString("name");
                    inventory_num = jsonObj.getString("inventory_num");
                    category = jsonObj.getString("category");
                    responsible = jsonObj.getString("responsible");
                    space = jsonObj.getString("space");
                    equip_condition = jsonObj.getString("equip_condition");
                    Log.d("Values", "getEquipUnderRepair name = " + name + " inventoryNum = " + inventory_num + " category = " + category + " responsible = " + responsible + " space = " + space + " equip_condition = " + equip_condition);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}