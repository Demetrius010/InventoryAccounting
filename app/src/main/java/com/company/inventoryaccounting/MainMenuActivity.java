package com.company.inventoryaccounting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    //JSONObject jsonObject;
    //JSONArray jsonArray;
    //Map<String, String> allAddresses; //String selectedAddressId = getKeyByValue(allAddresses, spnAddress.getSelectedItem().toString());

    /*public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        btnAllEquip = (Button) findViewById(R.id.btnAllEquip);
        bntEmployee = (Button) findViewById(R.id.btnEmployee);
        btnPlaces = (Button) findViewById(R.id.btnPlaces);

        new BackgroundWorker(this, this).execute("getEquip");
        new BackgroundWorker(this, this).execute("getStaff");
        new BackgroundWorker(this, this).execute("getAddresses");
    }

    public void onAllEquipButton(View view) {
        Intent intent = new Intent(".ToolListActivity");
        startActivity(intent);
    }


    public void onEmployeeButton(View view) {

    }

    public void onPlacesButton(View view) {

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

        /*if(typeFinishedProc.equals("getAddresses"))
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
                //spnAddress.setAdapter(dataAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       */


       /*private void ifAdmin(){
        if (((GlobalInventoryaccounting) this.getApplication()).isAdmin())
        {
            //Log.d("Value","admin = " + ((GlobalInventoryaccounting) this.getApplication()).isAdmin());
            bntObjects.setVisibility(View.VISIBLE);
        }
    }*/


       /*
       *                 Intent intent = new Intent(".ToolListActivity");
                intent.putExtra("jsonArray", jsonArray.toString()); //in new act in onCreate() {.... variable = getIntent().getExtras().getString("NAME")}
                startActivity(intent);
       *
       * */