package com.company.inventoryaccounting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChangeEmployeeActivity extends AppCompatActivity  implements BackgroundWorkerResponse{
    Button btnAddEmployee, btnChangeEmployee, btnRemoveEmployee;
    Spinner spnCurrentOrNewEmployee;
    EditText etEmployeeName, etEmployeePhone, etEmployeePosition, etEmployeeTeam;
    String employeeID, employeeName, employeePhone, employeePosition, employeeTeam;
    ArrayList<String> positionData, teamData;
    private ListPopupWindow lpwPosition, lpwTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_employee);
        btnAddEmployee = (Button)findViewById(R.id.btnAddEmployee);
        btnChangeEmployee = (Button)findViewById(R.id.btnChangeEmployee);
        btnRemoveEmployee = (Button)findViewById(R.id.btnRemoveEmployee);
        spnCurrentOrNewEmployee = (Spinner)findViewById(R.id.spnCurrentOrNewEmployee);
        etEmployeeName = findViewById(R.id.etEmployeeName);
        etEmployeePhone = findViewById(R.id.etEmployeePhone);
        etEmployeePosition = findViewById(R.id.spinEtEmployeePosition);
        etEmployeeTeam = findViewById(R.id.spinEtEmployeeTeam);
        employeeID = getIntent().getExtras().getString("employeeID");
        employeePhone = getIntent().getExtras().getString("employeePhone");
        employeeName = getIntent().getExtras().getString("employeeName");
        employeePosition = getIntent().getExtras().getString("employeePosition");
        employeeTeam = getIntent().getExtras().getString("employeeTeam");
        fillSpnCurrentOrNewEmployee();
        fillActvByEmployeeInfo();
        fillEtSpinPositionAndTeam();
    }


    private void fillSpnCurrentOrNewEmployee(){
        List<String> spnCurrentOrNewEmployeeArray =  new ArrayList<String>();
        spnCurrentOrNewEmployeeArray.add(employeeName);
        spnCurrentOrNewEmployeeArray.add("Новый сотрудник");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spnCurrentOrNewEmployeeArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCurrentOrNewEmployee.setAdapter(adapter);
        ifNewObjectSelected();
    }

    private void fillEtSpinPositionAndTeam() {
        positionData = ((GlobalInventoryaccounting) this.getApplication()).getAllPositionList();
        lpwPosition = new ListPopupWindow(this);
        lpwPosition.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, positionData));
        lpwPosition.setAnchorView(etEmployeePosition);
        lpwPosition.setModal(true);
        lpwPosition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = positionData.get(position);
                etEmployeePosition.setText(item);
                lpwPosition.dismiss();
            }
        });
        etEmployeePosition.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {// Check if touch point is in the area of the right button
                    if (motionEvent.getX() >= (view.getWidth() - ((EditText) view)
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        lpwPosition.show();// your action here
                        return true;
                    }
                }
                return false;
            }
        });

        teamData = ((GlobalInventoryaccounting) this.getApplication()).getAllTeamList();
        lpwTeam = new ListPopupWindow(this);
        lpwTeam.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, teamData));
        lpwTeam.setAnchorView(etEmployeeTeam);
        lpwTeam.setModal(true);
        lpwTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = teamData.get(position);
                etEmployeeTeam.setText(item);
                lpwTeam.dismiss();
            }
        });
        etEmployeeTeam.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {// Check if touch point is in the area of the right button
                    if (motionEvent.getX() >= (view.getWidth() - ((EditText) view)
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        lpwTeam.show();// your action here
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void ifNewObjectSelected(){
        spnCurrentOrNewEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spnCurrentOrNewEmployee.getSelectedItem().equals("Новый сотрудник")) {
                    btnAddEmployee.setEnabled(true);
                    btnAddEmployee.getBackground().setAlpha(255);
                    btnChangeEmployee.setEnabled(false);
                    btnChangeEmployee.getBackground().setAlpha(100);
                    btnRemoveEmployee.setEnabled(false);
                    btnRemoveEmployee.getBackground().setAlpha(100);
                }
                else {
                    fillActvByEmployeeInfo();
                    btnAddEmployee.setEnabled(false);
                    btnAddEmployee.getBackground().setAlpha(100);
                    btnChangeEmployee.setEnabled(true);
                    btnChangeEmployee.getBackground().setAlpha(255);
                    btnRemoveEmployee.setEnabled(true);
                    btnRemoveEmployee.getBackground().setAlpha(255);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }

    public void fillActvByEmployeeInfo() {
        etEmployeeName.setText(employeeName);
        etEmployeePhone.setText(employeePhone);
        etEmployeePosition.setText(employeePosition);
        etEmployeeTeam.setText(employeeTeam);
    }

    public void onAddBtn(View view)
    {
        String type = "addNewEmployee";
        if(etEmployeePhone.getText().length()==10){
            new BackgroundWorker(this, this).execute(type, etEmployeeName.getText().toString(), etEmployeePhone.getText().toString(), etEmployeePosition.getText().toString(), etEmployeeTeam.getText().toString());
        }
        else{
            Toast toast = Toast.makeText(this, "Номер телефона должен содержать 10 цифр", Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    public void onChangeBtn(View view)
    {
        String type = "changeEmployee";
        new BackgroundWorker(this, this).execute(type, employeeID, etEmployeeName.getText().toString(), etEmployeePhone.getText().toString(), etEmployeePosition.getText().toString(), etEmployeeTeam.getText().toString());
    }

    public void onRemoveBtn(View view)
    {
        String type = "removeEmployee";
        new BackgroundWorker(this, this).execute(type, employeeID);
    }

    private void getFreshData(){
        new BackgroundWorker(this, this).execute("getEquip");
        new BackgroundWorker(this, this).execute("getStaff");
        new BackgroundWorker(this, this).execute("getAddresses");
    }

    public void processFinish(String output, String typeFinishedProc) {
        if(typeFinishedProc.equals("addNewEmployee"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Добавлен новый сотрудник", Toast.LENGTH_LONG).show();
                getFreshData();
                Intent intent = new Intent(".StaffListActivity");
                startActivity(intent);
            }
        }
        if(typeFinishedProc.equals("changeEmployee"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Сотрудник изменен", Toast.LENGTH_LONG).show();
                getFreshData();
                Intent intent = new Intent(".StaffListActivity");
                startActivity(intent);
            }
        }
        if(typeFinishedProc.equals("removeEmployee"))
        {
            if(output.equals("Update successful")) {
                Toast.makeText(this, "Сотрудник удален", Toast.LENGTH_LONG).show();
                getFreshData();
                Intent intent = new Intent(".StaffListActivity");
                startActivity(intent);
            }
            else {
                Toast toast = Toast.makeText(this, "За этим сотрудником закреплен инвентарь!", Toast.LENGTH_LONG);
                TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                if( textView != null) textView.setGravity(Gravity.CENTER);
                toast.show();
            }
        }
        else if (typeFinishedProc.equals("getEquip")) {
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
