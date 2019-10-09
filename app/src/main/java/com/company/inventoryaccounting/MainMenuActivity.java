package com.company.inventoryaccounting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainMenuActivity extends AppCompatActivity implements BackgroundWorkerResponse {

    Button btnAllEquip, btnBrokenEquip, btnEquipUnderRepair, bntAddNewPlace, btnAddNewEquip;
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

    public void onAllEquipButton(View view){
        String type = "getdata";
        new BackgroundWorker(this, this).execute(type);
    }

    @Override
    public void processFinish(String output) {
        String phone, password;
        try {
            jsonObject = new JSONObject(output);
            jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                phone = jsonObj.getString("phone");
                password = jsonObj.getString("password");
                Log.d("Value","phone =" + phone);
                Log.d("Value","password =" + password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
