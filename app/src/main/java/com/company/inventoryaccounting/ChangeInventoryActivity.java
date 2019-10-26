package com.company.inventoryaccounting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class ChangeInventoryActivity extends AppCompatActivity implements BackgroundWorkerResponse{
    Button btnAddInventory, btnChangeInventory, btnRemoveInventory;
    Spinner spnInventoryId, spnCondition, spnInvResponsible, spnInvPlace;
    AutoCompleteTextView actvName, actvInvNum, actvInvCategory;

    Map<String, String> allEquipmentsName, allInvNum, allInvCategory, allInvCondition, allInvResponsible, allInvAddresses;
    Map<String, String> allConditions, allResponsible, allAddresses;

    ArrayAdapter<String> responsibleDataAdapter, addressesDataAdapter;

    String responsibleData;
    String addressesData;
    String currentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_inventory);
        btnAddInventory = (Button)findViewById(R.id.btnAddInventory);
        btnChangeInventory = (Button)findViewById(R.id.btnChangeInventory);
        btnRemoveInventory = (Button)findViewById(R.id.btnRemoveInventory);
        spnInventoryId = (Spinner)findViewById(R.id.spnInventoryId);
        spnCondition = (Spinner)findViewById(R.id.spnCondition);
        spnInvResponsible = (Spinner)findViewById(R.id.spnInvResponsible);
        spnInvPlace = (Spinner)findViewById(R.id.spnInvPlace);
        actvName = findViewById(R.id.actvName);
        actvInvNum = findViewById(R.id.actvInvNum);
        actvInvCategory = findViewById(R.id.actvInvCategory);

        addressesData = ((GlobalInventoryaccounting) this.getApplication()).getAddressesData();

        String type = "getAllEquip";
        new BackgroundWorker(this, this).execute(type);
        fillspnCondition();
        ifNewEquipSelected();
        fillSpinResponsible();
        fillSpinAddresses();
    }

    public void onAddInventoryBtn(View view)
    {
        String type = "addNewInventory";
        new BackgroundWorker(this, this).execute(type, actvName.getText().toString(), actvInvNum.getText().toString(),
                actvInvCategory.getText().toString(), getKeyByValue(allResponsible, spnInvResponsible.getSelectedItem().toString()),
                getKeyByValue(allAddresses, spnInvPlace.getSelectedItem().toString()), spnCondition.getSelectedItem().toString());
    }

    public void onChangeInventoryBtn(View view)
    {
        String type = "saveInventory";
        new BackgroundWorker(this, this).execute(type, currentID, actvName.getText().toString(), actvInvNum.getText().toString(),
                actvInvCategory.getText().toString(), getKeyByValue(allResponsible, spnInvResponsible.getSelectedItem().toString()),
                getKeyByValue(allAddresses, spnInvPlace.getSelectedItem().toString()), spnCondition.getSelectedItem().toString());
    }

    public void onRemoveInventoryBtn(View view)
    {
        String type = "removeInventory";
        new BackgroundWorker(this, this).execute(type, currentID);
    }

    public void ifNewEquipSelected(){
        spnInventoryId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spnInventoryId.getSelectedItem().equals("Новый инвентарь")) {
                    btnAddInventory.setEnabled(true);
                    btnAddInventory.getBackground().setAlpha(255);
                    btnChangeInventory.setEnabled(false);
                    btnChangeInventory.getBackground().setAlpha(100);
                    btnRemoveInventory.setEnabled(false);
                    btnRemoveInventory.getBackground().setAlpha(100);

                }
                else {
                    btnAddInventory.setEnabled(false);
                    btnAddInventory.getBackground().setAlpha(100);
                    btnChangeInventory.setEnabled(true);
                    btnChangeInventory.getBackground().setAlpha(255);
                    btnRemoveInventory.setEnabled(true);
                    btnRemoveInventory.getBackground().setAlpha(255);
                    currentID = getKeyByValue(allEquipmentsName, spnInventoryId.getSelectedItem().toString());
                    actvName.setText(allEquipmentsName.get(currentID));
                    actvName.setSelection(actvName.getText().length());
                    actvInvNum.setText(allInvNum.get(currentID));
                    actvInvNum.setSelection(actvInvNum.getText().length());
                    actvInvCategory.setText(allInvCategory.get(currentID));
                    actvInvCategory.setSelection(actvInvCategory.getText().length());
                    spnCondition.setSelection(Integer.parseInt(getKeyByValue(allConditions, allInvCondition.get(currentID))));
                    spnInvResponsible.setSelection(responsibleDataAdapter.getPosition(allResponsible.get(allInvResponsible.get(currentID))));
                    spnInvPlace.setSelection(addressesDataAdapter.getPosition(allAddresses.get(allInvAddresses.get(currentID))));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }


    public void fillspnCondition() {
        allConditions =  new HashMap<String, String>();
        allConditions.put("0", "Работает");
        allConditions.put("1","Сломан");
        allConditions.put("2","В ремонте");
        ArrayAdapter<String> spnConditionsDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(allConditions.values()));
        spnConditionsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCondition.setAdapter(spnConditionsDataAdapter);
    }

    public void fillSpinResponsible() {
        responsibleData = ((GlobalInventoryaccounting) this.getApplication()).getResponsibleData();
        if (responsibleData == null) {
            String type = "getResponsible";
            new BackgroundWorker(this, this).execute(type);
        }
        else {
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
                spnInvResponsible.setAdapter(responsibleDataAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                allAddresses.put(addressId, fullAddress);
            }
            addressesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(allAddresses.values()));
            addressesDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnInvPlace.setAdapter(addressesDataAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processFinish(String output, String typeFinishedProc) {
        if(typeFinishedProc.equals("getAllEquip"))
        {
            try {
                String id, name, inventory_num, category, condition, responsible, address;
                allEquipmentsName = new HashMap<String, String>();
                allInvNum = new HashMap<String, String>();
                allInvCategory = new HashMap<String, String>();
                allInvCondition = new HashMap<String, String>();
                allInvResponsible = new HashMap<String, String>();
                allInvAddresses = new HashMap<String, String>();
                List<String> uniqueInvCategory = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(output);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    id =  jsonObj.getString("id");
                    name = jsonObj.getString("name");
                    allEquipmentsName.put(id, name);
                    inventory_num = jsonObj.getString("inventory_num");
                    allInvNum.put(id, inventory_num);
                    category = jsonObj.getString("category");
                    allInvCategory.put(id, category);
                    if (!uniqueInvCategory.contains(category)) {
                        uniqueInvCategory.add(category);
                    }
                    condition = jsonObj.getString("equip_condition");
                    allInvCondition.put(id, condition);
                    responsible = jsonObj.getString("responsible");
                    allInvResponsible.put(id, responsible);
                    address = jsonObj.getString("space");
                    allInvAddresses.put(id, address);
                }
                ArrayAdapter<String> equipNamesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(allEquipmentsName.values()));
                actvName.setAdapter(equipNamesDataAdapter);

                ArrayAdapter<String> equipNamesSpinDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(allEquipmentsName.values()));
                equipNamesSpinDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                equipNamesSpinDataAdapter.insert("Новый инвентарь", 0);
                spnInventoryId.setAdapter(equipNamesSpinDataAdapter);

                ArrayAdapter<String> invNumDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(allInvNum.values()));
                actvInvNum.setAdapter(invNumDataAdapter);

                ArrayAdapter<String> uniqueinvCategoryDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, uniqueInvCategory);
                actvInvCategory.setAdapter(uniqueinvCategoryDataAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(typeFinishedProc.equals("getResponsible"))
        {
            ((GlobalInventoryaccounting)this.getApplication()).setResponsibleData(output);
            fillSpinResponsible();
        }
        if(typeFinishedProc.equals("addNewInventory"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Добавлен новый инвентарь", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(".MainMenuActivity");
                startActivity(intent);
            }
        }
        if(typeFinishedProc.equals("saveInventory"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Инвентарь изменен", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(".MainMenuActivity");
                startActivity(intent);
            }
        }
        if(typeFinishedProc.equals("removeInventory"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Инвентарь удален", Toast.LENGTH_LONG).show();
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
