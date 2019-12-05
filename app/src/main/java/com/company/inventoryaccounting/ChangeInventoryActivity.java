package com.company.inventoryaccounting;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
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

public class ChangeInventoryActivity extends AppCompatActivity implements BackgroundWorkerResponse {//View.OnTouchListener, AdapterView.OnItemClickListener
    Button btnAddInventory, btnChangeInventory, btnRemoveInventory;
    Spinner spnCurrentOrNewEqup, spnCondition, spnInvResponsible, spnInvPlace;
    //AutoCompleteTextView , actvInvCategory;

    Map<String, String> allEquipmentsName, allInvNum, allInvCategory, allInvCondition, allInvResponsible, allInvAddresses;
    Map<String, String> allConditions, allResponsible, allAddresses;

    ArrayAdapter<String> spnConditionsDataAdapter, responsibleDataAdapter, addressesDataAdapter;
    ArrayList<String> categoryData;

    String responsibleData;
    String addressesData;
    String instrumentId, name, inventory_num, category, responsible, space, equip_condition, barcode, description;

    EditText etCategory, etBarcode, etName, etInvNum, etDescription;
    private ListPopupWindow lpwCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_inventory);

        btnAddInventory = (Button) findViewById(R.id.btnAddInventory);
        btnChangeInventory = (Button) findViewById(R.id.btnChangeInventory);
        btnRemoveInventory = (Button) findViewById(R.id.btnRemoveInventory);
        spnCondition = (Spinner) findViewById(R.id.spnCondition);
        spnInvResponsible = (Spinner) findViewById(R.id.spnInvResponsible);
        spnInvPlace = (Spinner) findViewById(R.id.spnInvPlace);
        spnCurrentOrNewEqup = (Spinner) findViewById(R.id.spnCurrentOrNewEqup);
        etName = findViewById(R.id.etName);
        etInvNum = findViewById(R.id.etInvNum);
        etBarcode = findViewById(R.id.etBarCode);
        etDescription = findViewById(R.id.etDesc);
        etCategory = (EditText) findViewById(R.id.categorySpinEt);

        fillSpinResponsible();
        fillSpinAddresses();
        fillSpinCondition();

        instrumentId = getIntent().getExtras().getString("idInstrument");
        new BackgroundWorker(this, this).execute("getEquipByID", instrumentId);
        ifAdmin();
    }


    public void onAddInventoryBtn(View view) {
        String type = "addNewInventory";
        new BackgroundWorker(this, this).execute(type, etName.getText().toString(), etInvNum.getText().toString(), etBarcode.getText().toString(), etCategory.getText().toString(),
                spnCondition.getSelectedItem().toString(), getKeyByValue(allResponsible, spnInvResponsible.getSelectedItem().toString()), getKeyByValue(allAddresses, spnInvPlace.getSelectedItem().toString()),
                etDescription.getText().toString());
    }

    public void onChangeInventoryBtn(View view) {
        String type = "saveEquip";
        new BackgroundWorker(this, this).execute(type, instrumentId, etName.getText().toString(), etInvNum.getText().toString(), etBarcode.getText().toString(), etCategory.getText().toString(),
                spnCondition.getSelectedItem().toString(), getKeyByValue(allResponsible, spnInvResponsible.getSelectedItem().toString()), getKeyByValue(allAddresses, spnInvPlace.getSelectedItem().toString()),
                etDescription.getText().toString());
    }

    public void onRemoveInventoryBtn(View view) {
        String type = "removeInventory";
        new BackgroundWorker(this, this).execute(type, instrumentId);
    }

    public void ifAdmin() {
        if(((GlobalInventoryaccounting) this.getApplication()).isAdmin())
        {
            fillEtSpinCategory();

        }
        else {
            spnCurrentOrNewEqup.setEnabled(false);
            spnCurrentOrNewEqup.setVisibility(View.GONE);
            btnAddInventory.setEnabled(false);
            btnAddInventory.setVisibility(View.GONE);
            btnRemoveInventory.setEnabled(false);
            btnRemoveInventory.setVisibility(View.GONE);
            etName.setCursorVisible(false);
            etName.setFocusable(false);
            etName.setFocusableInTouchMode(false);
            etName.setClickable(false);
            etName.setTextColor(Color.DKGRAY);
            etInvNum.setCursorVisible(false);
            etInvNum.setFocusable(false);
            etInvNum.setFocusableInTouchMode(false);
            etInvNum.setClickable(false);
            etInvNum.setTextColor(Color.DKGRAY);
            etBarcode.setCursorVisible(false);
            etBarcode.setFocusable(false);
            etBarcode.setFocusableInTouchMode(false);
            etBarcode.setClickable(false);
            etBarcode.setTextColor(Color.DKGRAY);
            etCategory.setCursorVisible(false);
            etCategory.setFocusable(false);
            etCategory.setFocusableInTouchMode(false);
            etCategory.setClickable(false);
            etCategory.setTextColor(Color.DKGRAY);
        }
       /* spnInventoryId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


                    btnChangeInventory.setEnabled(true);
                    btnChangeInventory.getBackground().setAlpha(255);

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
        });*/
    }


    private void fillEtSpinCategory() {
        etCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);//добавляем иконку
        categoryData = ((GlobalInventoryaccounting) this.getApplication()).getAllCategoryList();
        etCategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {// Check if touch point is in the area of the right button
                    if (motionEvent.getX() >= (view.getWidth() - ((EditText) view)
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        lpwCategory.show();// your action here
                        return true;
                    }
                }
                return false;
            }
        });

        lpwCategory = new ListPopupWindow(this);
        lpwCategory.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, categoryData));
        lpwCategory.setAnchorView(etCategory);
        lpwCategory.setModal(true);
        lpwCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = categoryData.get(position);
                etCategory.setText(item);
                lpwCategory.dismiss();
            }
        });

    }

    private void fillSpinCondition() {
        allConditions = new HashMap<String, String>();
        allConditions.put("0", "Работает");
        allConditions.put("1", "В ремонте");
        allConditions.put("2", "Списан");
        spnConditionsDataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, new ArrayList<String>(allConditions.values()));
        spnConditionsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCondition.setAdapter(spnConditionsDataAdapter);
    }

    private void fillSpinResponsible() {
        responsibleData = ((GlobalInventoryaccounting) this.getApplication()).getStaffData();
        try {
            String responsibleFullName, responsibleId;
            allResponsible = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject(responsibleData);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                responsibleFullName = jsonObj.getString("full_name");
                responsibleId = jsonObj.getString("id");
                allResponsible.put(responsibleId, responsibleFullName);
            }
            responsibleDataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, new ArrayList<String>(allResponsible.values()));
            responsibleDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnInvResponsible.setAdapter(responsibleDataAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillSpinAddresses() {
        addressesData = ((GlobalInventoryaccounting) this.getApplication()).getAddressesData();
        try {
            String fullAddress, addressId;
            allAddresses = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject(addressesData);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                fullAddress = jsonObj.getString("full_address");
                addressId = jsonObj.getString("id");
                allAddresses.put(addressId, fullAddress);
            }
            addressesDataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, new ArrayList<String>(allAddresses.values()));
            addressesDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnInvPlace.setAdapter(addressesDataAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillActByEquip()
    {
        etName.setText(name);
        etInvNum.setText(inventory_num);
        etBarcode.setText(barcode);
        int spinCondPosition = spnConditionsDataAdapter.getPosition(equip_condition);
        spnCondition.setSelection(spinCondPosition);
        int spinResponsiblePosition = responsibleDataAdapter.getPosition(allResponsible.get(responsible));
        spnInvResponsible.setSelection(spinResponsiblePosition);
        int spinSpacePosition = addressesDataAdapter.getPosition(allAddresses.get(space));
        spnInvPlace.setSelection(spinSpacePosition);
        etCategory.setText(category);
        etDescription.setText(description);
    }

    private void fillSpnCurrentOrNewEqup(){
        if(spnCurrentOrNewEqup.isEnabled())
        {
            List<String> spnCurrentOrNewEqupArray =  new ArrayList<String>();
            spnCurrentOrNewEqupArray.add(name);
            spnCurrentOrNewEqupArray.add("Новый инструмент");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spnCurrentOrNewEqupArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnCurrentOrNewEqup.setAdapter(adapter);

            spnCurrentOrNewEqup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (spnCurrentOrNewEqup.getSelectedItem().equals("Новый инструмент")) {
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
                        fillActByEquip();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    public void processFinish(String output, String typeFinishedProc) {
        if (typeFinishedProc.equals("getEquipByID")) {
            try {
                JSONObject jsonObject = new JSONObject(output);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                jsonObject = jsonArray.getJSONObject(0);
                name = jsonObject.getString("name");
                inventory_num = jsonObject.getString("inventory_num");
                barcode = jsonObject.getString("barcode");
                equip_condition = jsonObject.getString("equip_condition");
                responsible = jsonObject.getString("responsible");
                space = jsonObject.getString("space");
                category = jsonObject.getString("category");
                description = jsonObject.getString("description");
                /*if (!(((GlobalInventoryaccounting) this.getApplication()).isAdmin()))
                {
                    etEquipName.setEnabled(false);
                    etEquipInventoryNum.setEnabled(false);
                }*/
                //Log.d("Values", "getAllEquip id =" + id + " name = " + name + " inventoryNum = " + inventory_num + " category = " + category + " responsible = " + responsible + " space = " + space + " equip_condition = " + equip_condition);
                fillActByEquip();
                fillSpnCurrentOrNewEqup();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(typeFinishedProc.equals("addNewInventory"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Добавлен новый инвентарь", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(".MainMenuActivity");
                startActivity(intent);
            }
        }
        else if(typeFinishedProc.equals("saveEquip"))
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
        /*if(typeFinishedProc.equals("getAllEquip"))
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


        */
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
