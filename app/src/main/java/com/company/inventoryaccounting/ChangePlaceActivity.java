package com.company.inventoryaccounting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChangePlaceActivity extends AppCompatActivity implements BackgroundWorkerResponse{
    Button btnAddPlace, btnChangePlace, btnRemovePlace;
    Spinner spnCurrentOrNewPlace;
    ProgressBar progressBar;
    EditText etShortAddress, etFullAddress;
    String placeID, shortAddress, fullAddress;
    Toast toastMessage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_place);
        btnAddPlace = (Button)findViewById(R.id.btnAddPlace);
        btnChangePlace = (Button)findViewById(R.id.btnChangePlace);
        btnRemovePlace = (Button)findViewById(R.id.btnRemovePlace);
        spnCurrentOrNewPlace = (Spinner)findViewById(R.id.spnCurrentOrNewPlace);
        etShortAddress = findViewById(R.id.etShortAddress);
        etFullAddress = findViewById(R.id.etFullAddress);
        progressBar = findViewById(R.id.progressBar);
        placeID = getIntent().getExtras().getString("employeeID");
        shortAddress = getIntent().getExtras().getString("shortAddress");
        fullAddress = getIntent().getExtras().getString("fullAddress");
        fillSpnCurrentOrNewPlace();
        fillActvByInfo();
    }

    private void fillSpnCurrentOrNewPlace(){
        List<String> spnCurrentOrNewPlaceArray =  new ArrayList<String>();
        spnCurrentOrNewPlaceArray.add(shortAddress);
        spnCurrentOrNewPlaceArray.add("Новый объект");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spnCurrentOrNewPlaceArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCurrentOrNewPlace.setAdapter(adapter);
        ifNewObjectSelected();
    }

    public void ifNewObjectSelected(){
        spnCurrentOrNewPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spnCurrentOrNewPlace.getSelectedItem().equals("Новый объект")) {
                    btnAddPlace.setEnabled(true);
                    btnAddPlace.getBackground().setAlpha(255);
                    btnChangePlace.setEnabled(false);
                    btnChangePlace.getBackground().setAlpha(100);
                    btnRemovePlace.setEnabled(false);
                    btnRemovePlace.getBackground().setAlpha(100);
                }
                else {
                    fillActvByInfo();
                    btnAddPlace.setEnabled(false);
                    btnAddPlace.getBackground().setAlpha(100);
                    btnChangePlace.setEnabled(true);
                    btnChangePlace.getBackground().setAlpha(255);
                    btnRemovePlace.setEnabled(true);
                    btnRemovePlace.getBackground().setAlpha(255);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }

    public void fillActvByInfo() {
        etShortAddress.setText(shortAddress);
        etFullAddress.setText(fullAddress);
    }

    public void onAddBtn(View view)
    {
        String type = "addNewPlace";
        new BackgroundWorker(this, this).execute(type, etShortAddress.getText().toString(), etFullAddress.getText().toString());
        progressBar.setVisibility(View.VISIBLE);
        actViewsEnabled(false);
    }

    public void onChangeBtn(View view)
    {
        String type = "changePlace";
        new BackgroundWorker(this, this).execute(type, placeID, etShortAddress.getText().toString(), etFullAddress.getText().toString());
        progressBar.setVisibility(View.VISIBLE);
        actViewsEnabled(false);
    }

    public void onRemoveBtn(View view)
    {
        String type = "removePlace";
        new BackgroundWorker(this, this).execute(type, placeID);
        progressBar.setVisibility(View.VISIBLE);
        actViewsEnabled(false);
    }


    private void getFreshData(){
        new BackgroundWorker(this, this).execute("getAddresses");
    }

    private void actViewsEnabled(Boolean state){
        btnAddPlace.setEnabled(state);
        btnChangePlace.setEnabled(state);
        btnRemovePlace.setEnabled(state);
        spnCurrentOrNewPlace.setEnabled(state);
        etShortAddress.setEnabled(state);
        etFullAddress.setEnabled(state);
    }

    private void ifAllDataReceived(){
        progressBar.setVisibility(View.INVISIBLE);
        actViewsEnabled(true);
    }

    public void processFinish(String output, String typeFinishedProc) {
        if(typeFinishedProc.equals("addNewPlace"))
        {
            if(output.equals("Update successful")) {
                toastMessage = Toast.makeText(this, "Добавлен новый объект", Toast.LENGTH_LONG);
                toastMessage.show();
                getFreshData();
            }
        }
        else if(typeFinishedProc.equals("changePlace"))
        {
            if(output.equals("Update successful")) {
                toastMessage = Toast.makeText(this, "Объект изменен", Toast.LENGTH_LONG);
                toastMessage.show();
                getFreshData();
            }
        }
        else if(typeFinishedProc.equals("removePlace"))
        {
            if(output.equals("Update successful")) {
                toastMessage = Toast.makeText(this, "Объект удален", Toast.LENGTH_LONG);
                toastMessage.show();
                getFreshData();
            }
            else {
                Toast toast = Toast.makeText(this, "На данном объекте находится инвентарь!", Toast.LENGTH_LONG);
                TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                if( textView != null) textView.setGravity(Gravity.CENTER);
                toast.show();
            }
        }
        else if(typeFinishedProc.equals("getAddresses")){
            ((GlobalInventoryaccounting)this.getApplication()).setAddressesData(output);
            ifAllDataReceived();
            if (toastMessage!= null) {//скрываем сообщение
                toastMessage.cancel();
            }
            Intent intent = new Intent(".PlaceListActivity");
            startActivity(intent);
        }
    }
}
