package com.company.inventoryaccounting;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements BackgroundWorkerResponse {

    Map<String, String> instrumentInvNum = new HashMap<String, String>();
    String instrumentID, inventoryNum;
    Button btnAllEquip, bntEmployee, btnPlaces, btnScan;
    ImageButton btnSearch;
    EditText search;
    Boolean receivedEquipData, receivedStaffData, receivedAddressesData;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        btnAllEquip = (Button) findViewById(R.id.btnAllEquip);
        bntEmployee = (Button) findViewById(R.id.btnEmployee);
        btnPlaces = (Button) findViewById(R.id.btnPlaces);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnSearch = findViewById(R.id.BtnSearch);
        search = findViewById(R.id.etSearchByInvNum);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        receivedEquipData = receivedStaffData = receivedAddressesData = false;
        new BackgroundWorker(this, this).execute("getEquip");
        new BackgroundWorker(this, this).execute("getStaff");
        new BackgroundWorker(this, this).execute("getAddresses");
        progressBar.setVisibility(View.VISIBLE);
        actViewsEnabled(false);
        super.onStart();
    }

    public void onScanButton(View view) {
        //if(receivedEquipData)
        //{
            Intent intent = new Intent(".ScannerActivity");
            startActivity(intent);
        //}
    }

    public void onSearchButton(View view) {
        if(instrumentInvNum.containsValue(search.getText().toString()))//receivedEquipData &&
        {
            Intent intent = new Intent(".ChangeInventoryActivity");
            intent.putExtra("idInstrument", getKeyByValue(instrumentInvNum, search.getText().toString()));
            startActivity(intent);
        }
        else if(!instrumentInvNum.containsValue(search.getText().toString()))
        {
            Toast.makeText(this, "Инструмент не найден", Toast.LENGTH_LONG).show();
        }
    }

    public void onAllEquipButton(View view) {
        //if(receivedEquipData && receivedStaffData && receivedAddressesData)
        //{
            Intent intent = new Intent(".ToolListActivity");
            startActivity(intent);
        //}
    }

    public void onEmployeeButton(View view) {
        //if(receivedStaffData) {
            Intent intent = new Intent(".StaffListActivity");
            startActivity(intent);
        //}
    }

    public void onPlacesButton(View view) {
        //if(receivedAddressesData) {
            Intent intent = new Intent(".PlaceListActivity");
            startActivity(intent);
        //}
    }

    private void actViewsEnabled(Boolean state){
        btnAllEquip.setEnabled(state);
        bntEmployee.setEnabled(state);
        btnPlaces.setEnabled(state);
        btnScan.setEnabled(state);
        btnSearch.setEnabled(state);
        search.setEnabled(state);
    }

    private void ifAllDataReceived(){
        if(receivedEquipData && receivedStaffData && receivedAddressesData){
            progressBar.setVisibility(View.INVISIBLE);
            actViewsEnabled(true);
        }
    }

    private void fillInventoryNumDictionary(){ // получаем категории
        instrumentInvNum.clear();
        try {
            JSONObject jsonObject = new JSONObject(((GlobalInventoryaccounting) this.getApplication()).getEquipmentData());
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                instrumentID = jsonObj.getString("id");
                inventoryNum = jsonObj.getString("inventory_num");
                instrumentInvNum.put(instrumentID, inventoryNum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String output, String typeFinishedProc) {
        if(output != null){
            if (typeFinishedProc.equals("getEquip")) {
                ((GlobalInventoryaccounting)this.getApplication()).setEquipmentData(output);
                receivedEquipData = true;
                fillInventoryNumDictionary();
                ifAllDataReceived();
            }
            else if(typeFinishedProc.equals("getStaff")){
                ((GlobalInventoryaccounting)this.getApplication()).setStaffData(output);
                receivedStaffData = true;
                ifAllDataReceived();
            }
            else if(typeFinishedProc.equals("getAddresses")){
                ((GlobalInventoryaccounting)this.getApplication()).setAddressesData(output);
                receivedAddressesData = true;
                ifAllDataReceived();
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

