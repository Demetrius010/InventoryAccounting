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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChangePlaceActivity extends AppCompatActivity implements BackgroundWorkerResponse{
    Button btnAddPlace, btnChangePlace, btnRemovePlace;
    Spinner spnId;
    AutoCompleteTextView actvDesc, actvFullAddress;
    String addressesData;
    Map<String, String> allShortAddresses, allFullAddresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_place);
        btnAddPlace = (Button)findViewById(R.id.btnAddPlace);
        btnChangePlace = (Button)findViewById(R.id.btnChangePlace);
        btnRemovePlace = (Button)findViewById(R.id.btnRemovePlace);
        spnId = (Spinner)findViewById(R.id.spnId);

        addressesData = ((GlobalInventoryaccounting) this.getApplication()).getAddressesData();
        actvDesc = findViewById(R.id.actvDesc);
        actvFullAddress = findViewById(R.id.actvFullAddress);
        fillActvDesc();
        ifNewObjectSelected();
    }

    public void ifNewObjectSelected(){
        spnId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spnId.getSelectedItem().equals("Новый объект")) {
                    btnAddPlace.setEnabled(true);
                    btnAddPlace.getBackground().setAlpha(255);
                    btnChangePlace.setEnabled(false);
                    btnChangePlace.getBackground().setAlpha(100);
                    btnRemovePlace.setEnabled(false);
                    btnRemovePlace.getBackground().setAlpha(100);

                }
                else {
                    btnAddPlace.setEnabled(false);
                    btnAddPlace.getBackground().setAlpha(100);
                    btnChangePlace.setEnabled(true);
                    btnChangePlace.getBackground().setAlpha(255);
                    btnRemovePlace.setEnabled(true);
                    btnRemovePlace.getBackground().setAlpha(255);
                    String currentKey = getKeyByValue(allShortAddresses, spnId.getSelectedItem().toString());
                    actvDesc.setText(allShortAddresses.get(currentKey));
                    actvDesc.setSelection(actvDesc.getText().length());
                    actvFullAddress.setText(allFullAddresses.get(currentKey));
                    actvFullAddress.setSelection(actvFullAddress.getText().length());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void fillActvDesc() {
        try {
            String addressId, shortAddress, fullAddress;
            allShortAddresses = new HashMap<String, String>();
            allFullAddresses = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject(addressesData);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                addressId = jsonObj.getString("id");
                shortAddress = jsonObj.getString("short_address");
                allShortAddresses.put(addressId, shortAddress);
                fullAddress = jsonObj.getString("full_address");
                allFullAddresses.put(addressId, fullAddress);

            }
            ArrayAdapter<String> shortAddressesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(allShortAddresses.values()));
            actvDesc.setAdapter(shortAddressesDataAdapter);

            ArrayAdapter<String> shortAddressesSpinDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(allShortAddresses.values()));
            shortAddressesSpinDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shortAddressesSpinDataAdapter.insert("Новый объект", 0);
            spnId.setAdapter(shortAddressesSpinDataAdapter);

            ArrayAdapter<String> fullAddressesDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(allFullAddresses.values()));
            actvFullAddress.setAdapter(fullAddressesDataAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onAddBtn(View view)
    {
        String type = "addNewPlace";
        new BackgroundWorker(this, this).execute(type, actvDesc.getText().toString(), actvFullAddress.getText().toString());
    }

    public void onChangeBtn(View view)
    {
        String type = "changePlace";
        new BackgroundWorker(this, this).execute(type, getKeyByValue(allShortAddresses, spnId.getSelectedItem().toString()), actvDesc.getText().toString(), actvFullAddress.getText().toString());
    }

    public void onRemoveBtn(View view)
    {
        String type = "removePlace";
        new BackgroundWorker(this, this).execute(type, getKeyByValue(allShortAddresses, spnId.getSelectedItem().toString()));
    }


    public void processFinish(String output, String typeFinishedProc) {
        if(typeFinishedProc.equals("addNewPlace"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Добавлен новый объект", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(".MainMenuActivity");
                startActivity(intent);
            }
        }
        if(typeFinishedProc.equals("changePlace"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Объект изменен", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(".MainMenuActivity");
                startActivity(intent);
            }
        }
        if(typeFinishedProc.equals("removePlace"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Объект удален", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(".MainMenuActivity");
                startActivity(intent);
            }
            else {
                Toast toast = Toast.makeText(this, "На данном объекте находится инвентарь!", Toast.LENGTH_LONG);
                TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                if( textView != null) textView.setGravity(Gravity.CENTER);
                toast.show();
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
