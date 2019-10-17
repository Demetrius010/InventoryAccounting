package com.company.inventoryaccounting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;

public class ChangeInstrument extends AppCompatActivity {
    String instrumentId;
    EditText etEquipName, etEquipInventoryNum;
    Spinner spinPlace, spinCondition, spinResponsible;

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

        etEquipName.setText(instrumentId);
    }
}
