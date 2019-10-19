package com.company.inventoryaccounting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChangeInstrument extends AppCompatActivity implements BackgroundWorkerResponse {
    String instrumentId;
    String responsibleData;
    String addressesData;

    EditText etEquipName, etEquipInventoryNum;
    Spinner spinPlace, spinCondition, spinResponsible;

    ArrayAdapter<String> dataAdapterForConditions;
    Map<String, String> allResponsible;
    ArrayAdapter<String> responsibleDataAdapter;
    Map<String, String> allAddresses;
    ArrayAdapter<String> addressesDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_instrument);

        instrumentId = getIntent().getExtras().getString("idInstrument");
        //Log.d("Value", "instrumentId = " + instrumentId);
        etEquipName = (EditText) findViewById(R.id.etEquipName);
        etEquipInventoryNum = (EditText) findViewById(R.id.etEquipInventoryNum);
        spinPlace = (Spinner)findViewById(R.id.spinPlace);
        spinCondition = (Spinner)findViewById(R.id.spinCondition);
        spinResponsible = (Spinner)findViewById(R.id.spinResponsible);

        responsibleData = ((GlobalInventoryaccounting) this.getApplication()).getResponsibleData();
        addressesData = ((GlobalInventoryaccounting) this.getApplication()).getAddressesData();
        fillSpinCondition();
        fillSpinResponsible();
        fillSpinAddresses();
        String type = "getEquipByID";
        new BackgroundWorker(this, this).execute(type, instrumentId);
    }

    public void fillSpinCondition() {
        List<String> allConditions =  new ArrayList<String>();
        allConditions.add("Работает");
        allConditions.add("Сломан");
        allConditions.add("В ремонте");
        dataAdapterForConditions = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allConditions);
        dataAdapterForConditions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCondition.setAdapter(dataAdapterForConditions);
    }

    public void fillSpinResponsible() {
        try {
            String responsibleFullName, responsibleId;
            allResponsible = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject(responsibleData);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                responsibleFullName = jsonObj.getString("full_name");
                responsibleId = jsonObj.getString("id");
                allResponsible.put(responsibleId, responsibleFullName);
            }
            responsibleDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(allResponsible.values()));
            responsibleDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinResponsible.setAdapter(responsibleDataAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fillSpinAddresses() {
        try {
            String fullAddress, addressId;
            allAddresses = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject(addressesData);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                fullAddress = jsonObj.getString("full_address");
                addressId = jsonObj.getString("id");
                //Log.d("Value", "addressId = " + addressId + "fullAddress = " + fullAddress);
                allAddresses.put(addressId, fullAddress);
            }
            addressesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(allAddresses.values()));
            addressesDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinPlace.setAdapter(addressesDataAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onSave(View view){
        String type = "saveEquip";
        //Log.d("Value", " type = " + type + " instrumentId = " + instrumentId + " etEquipName = " + etEquipName.getText().toString() + " etEquipInventoryNum = " + etEquipInventoryNum.getText().toString() + " spinResponsible = " + getKeyByValue(allResponsible, spinResponsible.getSelectedItem().toString()) + " spinPlace = " +  getKeyByValue(allAddresses, spinPlace.getSelectedItem().toString()) + " spinCondition = " + spinCondition.getSelectedItem().toString());
        new BackgroundWorker(this, this).execute(type, instrumentId, etEquipName.getText().toString(),
                etEquipInventoryNum.getText().toString(), getKeyByValue(allResponsible, spinResponsible.getSelectedItem().toString()),
                getKeyByValue(allAddresses, spinPlace.getSelectedItem().toString()), spinCondition.getSelectedItem().toString());
    }

    @Override
    public void processFinish(String output, String typeFinishedProc) {
        if (typeFinishedProc.equals("getEquipByID")) {
            try {
                String name, inventory_num, responsible, space, equip_condition;
                JSONObject jsonObject = new JSONObject(output);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                jsonObject = jsonArray.getJSONObject(0);

                name = jsonObject.getString("name");
                inventory_num = jsonObject.getString("inventory_num");
                etEquipName.setText(name);
                etEquipInventoryNum.setText(inventory_num);

                equip_condition = jsonObject.getString("equip_condition");
                int spinCondPosition = dataAdapterForConditions.getPosition(equip_condition);
                spinCondition.setSelection(spinCondPosition);

                responsible = jsonObject.getString("responsible");
                int spinResponsiblePosition = responsibleDataAdapter.getPosition(allResponsible.get(responsible));
                spinResponsible.setSelection(spinResponsiblePosition);

                space = jsonObject.getString("space");
                int spinSpacePosition = addressesDataAdapter.getPosition(allAddresses.get(space));
                spinPlace.setSelection(spinSpacePosition);

                if (!(((GlobalInventoryaccounting) this.getApplication()).isAdmin()))
                {
                    etEquipName.setEnabled(false);
                    etEquipInventoryNum.setEnabled(false);
                }
                //category = jsonObject.getString("category"); responsible = jsonObj.getString("responsible");//space = jsonObj.getString("space");//
                //Log.d("Values", "getAllEquip id =" + id + " name = " + name + " inventoryNum = " + inventory_num + " category = " + category + " responsible = " + responsible + " space = " + space + " equip_condition = " + equip_condition);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (typeFinishedProc.equals("saveEquip")) {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(".MainMenuActivity");
                startActivity(intent);
            }
        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}

