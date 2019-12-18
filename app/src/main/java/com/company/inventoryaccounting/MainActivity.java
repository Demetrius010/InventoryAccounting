package com.company.inventoryaccounting;


import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BackgroundWorkerResponse {
    Button btnLogin;
    EditText etPhone;  //etPassword
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        etPhone = (EditText)findViewById(R.id.etPhone);
        //etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        phoneMaskInput();
    }

    private void phoneMaskInput(){
        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher(){
            //we need to know if the user is erasing or inputing some new character
            private boolean backspacingFlag = false;
            //we need to block the :afterTextChanges method to be called again after we just replaced the EditText text
            private  boolean editedFlag = false;
            //we need to mark the cursor position and restore it after the edition
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //we store the cursor local relative to the end of the string in the EditText before the edition
                cursorComplement = s.length() - etPhone.getSelectionStart();
                //we check if the user ir inputing or erasing a character
                if (count > after){
                    backspacingFlag = true;
                }
                else {
                    backspacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public synchronized void afterTextChanged(Editable s) {
                String str = s.toString();
                //what matters are the phone digits beneath the mask, so we always work with a raw string with only digits
                String phone = str.replace("+7", "").replaceAll("[^\\d]", ""); //The expression [^\\d] means not a digit where [] means one of these.[^ ..] means (not one of these)\  is escape character (because in Java you need to escape it)\d   means digit

                //if the text was just edited, :afterTextChanged is called another time... so we need to verify the flag of edition
                //if the flag is false, this is a original user-typed entry. so we go on and do some magic
                if(!editedFlag){
                    //we start verifying the worst case, many characters mask need to be added
                    //example: 999999999 <- 6+ digits already typed
                    // masked: +7(999) 999-99-99

                    if(phone.length() >= 9 && !backspacingFlag){
                        //we will edit. next call on this textWatcher will be ignored
                        editedFlag = true;
                        //here is the core. we substring the raw digits and add the mask as convenient
                        String ans = "+7(" + phone.substring(0, 3) + ") " +phone.substring(3,6) + "-" + phone.substring(6,8) + "-" + phone.substring(8);
                        etPhone.setText(ans);
                        //we deliver the cursor to its original position relative to the end of the string
                        etPhone.setSelection(etPhone.getText().length()-cursorComplement);
                    }
                    else if(phone.length() >= 6 && !backspacingFlag){
                        editedFlag = true;
                        String ans = "+7(" + phone.substring(0, 3) + ") " +phone.substring(3,6) + "-" + phone.substring(6);
                        etPhone.setText(ans);
                        etPhone.setSelection(etPhone.getText().length()-cursorComplement);
                    }
                    else if (phone.length() >= 3 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = "+7(" +phone.substring(0, 3) + ") " + phone.substring(3);
                        etPhone.setText(ans);
                        etPhone.setSelection(etPhone.getText().length()-cursorComplement);
                    }
                    else if (phone.length() > 0 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = "+7(" +phone.substring(0);
                        etPhone.setText(ans);
                        etPhone.setSelection(etPhone.getText().length()-cursorComplement);
                    }
                }
                // We just edited the field, ignoring this cicle of the watcher and getting ready for the nex
                else {
                    editedFlag = false;
                }
            }
        });
    }

    public void onLogin(View view){
        String type = "login";
        String phone = etPhone.getText().toString().replace("+7", "").replaceAll("[^\\d]", "");
        //String password = etPassword.getText().toString();
        //BackgroundWorker backgroundWorkerLogin = new BackgroundWorker(this);
        //backgroundWorkerLogin.execute(type, phone);
        new BackgroundWorker(this, this).execute(type, phone);
        progressBar.setVisibility(View.VISIBLE);
        actViewsEnabled(false);
    }

    private void actViewsEnabled(Boolean state){
        etPhone.setEnabled(state);
        btnLogin.setEnabled(state);
    }

    private void ifResponseReceived(){
        progressBar.setVisibility(View.INVISIBLE);
        actViewsEnabled(true);
    }


    @Override
    public void processFinish(String output, String typeFinishedProc) {
        if(output != null){
            if(typeFinishedProc.equals("login")){
                try {
                    ifResponseReceived();
                    String admin = "";
                    JSONObject jsonObject = new JSONObject(output);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    if(jsonArray.length()>0){
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            admin = jsonObj.getString("admin");
                        }
                        if(admin.equals("1"))
                        {
                            ((GlobalInventoryaccounting)this.getApplication()).setAdminFlag(true);
                        }
                        else {
                            ((GlobalInventoryaccounting)this.getApplication()).setAdminFlag(false);
                        }

                        Intent intent = new Intent(".MainMenuActivity");
                        startActivity(intent);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
    }
}
