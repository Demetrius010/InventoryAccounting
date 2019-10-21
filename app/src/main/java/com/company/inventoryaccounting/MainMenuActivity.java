package com.company.inventoryaccounting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements BackgroundWorkerResponse {

    Button btnAllEquip, btnBrokenEquip, btnEquipUnderRepair, bntObjects, btnInventory;
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
        bntObjects = (Button)findViewById(R.id.bntObjects);
        btnInventory = (Button)findViewById(R.id.btnInventory);
        spnAddress = (Spinner)findViewById(R.id.spnAddress);

        ifAdmin();
        getAddresses();
    }

    private void ifAdmin(){
        if (((GlobalInventoryaccounting) this.getApplication()).isAdmin())
        {
            //Log.d("Value","admin = " + ((GlobalInventoryaccounting) this.getApplication()).isAdmin());
            bntObjects.setVisibility(View.VISIBLE);
            btnInventory.setVisibility(View.VISIBLE);
        }
        else{
            bntObjects.setVisibility(View.INVISIBLE);
            btnInventory.setVisibility(View.INVISIBLE);
        }
    }

    private  void getAddresses(){
        String type = "getAddresses";
        new BackgroundWorker(this, this).execute(type);
    }

    private void getEquip(String equipCond)
    {
        String type = "getEquip";
        String selectedAddressId = getKeyByValue(allAddresses, spnAddress.getSelectedItem().toString());
        //Log.d("Value", "selectedAddressId = " + selectedAddressId);
        new BackgroundWorker(this, this).execute(type, selectedAddressId, equipCond);
    }

    public void onAllEquipButton(View view){
        String condition = "Работает";
        getEquip(condition);
    }


    public void onBrokenEquipButton(View view){
        String condition = "Сломан";
        getEquip(condition);
    }

    public void onEquipUnderRepairButton(View view){
        String condition = "В ремонте";
        getEquip(condition);
    }

    public void onObjectsButton(View view){
        Intent intent = new Intent(".ChangePlaceActivity");
        startActivity(intent);
    }

    public void onInventoryButton(View view){
        Intent intent = new Intent(".ChangeInventoryActivity");
        startActivity(intent);
    }



    @Override
    public void processFinish(String output, String typeFinishedProc) {
        if(typeFinishedProc.equals("getAddresses"))
        {
            ((GlobalInventoryaccounting)this.getApplication()).setAddressesData(output);
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
        else if (typeFinishedProc.equals("getEquip")) {
            try {
                String id, name, inventory_num, category, responsible, space, equip_condition;
                jsonObject = new JSONObject(output);
                jsonArray = jsonObject.getJSONArray("server_response");
                if (jsonArray.length() == 0)
                {
                    Toast.makeText(this, "Инвентарь отсутствует", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    id =  jsonObj.getString("id");
                    name = jsonObj.getString("name");
                    inventory_num = jsonObj.getString("inventory_num");
                    category = jsonObj.getString("category");
                    responsible = jsonObj.getString("responsible");
                    space = jsonObj.getString("space");
                    equip_condition = jsonObj.getString("equip_condition");
                    //Log.d("Values", "getAllEquip id =" + id + " name = " + name + " inventoryNum = " + inventory_num + " category = " + category + " responsible = " + responsible + " space = " + space + " equip_condition = " + equip_condition);
                }
                Intent intent = new Intent(".ToolListActivity");
                intent.putExtra("jsonArray", jsonArray.toString()); //in new act in onCreate() {.... variable = getIntent().getExtras().getString("NAME")}
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}