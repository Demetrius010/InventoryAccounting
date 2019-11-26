package com.company.inventoryaccounting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ToolListActivity extends AppCompatActivity{ //implements BackgroundWorkerResponse {
    LinearLayout myLinearLayout;

    JSONObject jsonObject;
    JSONArray jsonArray;

    Map<String, String> allResponsible;
    Map<String, String> allAddresses;
    Map<String, String> allCategories = new HashMap<String, String>();;

    //String jsonString;
    //Spinner spnCategory;

    //private ListPopupWindow lpw;
    //private String[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        myLinearLayout = (LinearLayout) findViewById(R.id.linLayout);
        fillActWithItems();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*jsonString = getIntent().getStringExtra("jsonArray");
        spnCategory = (Spinner)findViewById(R.id.spnCategory);
        spnCategory.setAdapter(null);
        spnCategory.setOnItemSelectedListener(listener);*/
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_list_menu, menu);
        SubMenu placeFilterSubMenu = menu.getItem(1).getSubMenu().getItem(0).getSubMenu();
        for (Map.Entry<String,String> entry : allAddresses.entrySet()) //String selectedAddressId = getKeyByValue(allAddresses, spnAddress.getSelectedItem().toString());
        {
            placeFilterSubMenu.add(0,Integer.parseInt(entry.getKey()),0,entry.getValue());//add(int groupId, int itemId, int order, CharSequence title)
        }
        SubMenu categoryFilterSubMenu = menu.getItem(1).getSubMenu().getItem(1).getSubMenu();
        for (Map.Entry<String,String> entry : allCategories.entrySet())
        {
            categoryFilterSubMenu.add(1,Integer.parseInt(entry.getKey()),0,entry.getValue());
        }
        //SubMenu conditionFilterSubMenu = menu.getItem(1).getSubMenu().getItem(2).getSubMenu();
        SubMenu employeeFilterSubMenu = menu.getItem(1).getSubMenu().getItem(3).getSubMenu();
        for (Map.Entry<String,String> entry : allResponsible.entrySet())
        {
            employeeFilterSubMenu.add(3,Integer.parseInt(entry.getKey()),0,entry.getValue());
        }
        return true;
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Menu myFilterMenu = (Menu) menu.getItem(R.id.placeFilterMenu);
        return super.onPrepareOptionsMenu(menu);
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Value", "value: " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.ascendingSort:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            case R.id.descendingSort:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            case R.id.alphabetSort:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            case R.id.reverseAlphabetSort:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item); //super - текущий экземпляр родительского класса.
        }
    }

    public void onImgSortBtnClick (View view){
        LinearLayout sortLinLayout = new LinearLayout(this);
        LinearLayout.LayoutParams sortLinLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //sortLinLayoutParams.width = 10;
        //sortLinLayoutParams.height = 10;

        sortLinLayout.setLayoutParams(sortLinLayoutParams);
        sortLinLayout.setOrientation(LinearLayout.HORIZONTAL);

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("one");
        spinnerArray.add("two");
        spinnerArray.add("three");
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);

        sortLinLayout.addView(spinner);
       setContentView(sortLinLayout);

   }

    public void onBtnFilterClick(View view){

    }

    private void fillActWithItems(){ //Нужно: +ИМЯ,  +СОСТОЯНИЕ, +ИНВЕНТАРНЫЙ НОМЕР, +АДРЕС, +ОТВЕТСТВЕННЫЙ
        fillResponsibleDictionary();
        fillAddressesDictionary();

        myLinearLayout.removeAllViews();
        LinearLayout.LayoutParams myLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView;
        SpannableString ss;

        String name = "", inventory_num, category, responsible, condition, address;
        String text = "";
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting) this.getApplication()).getEquipmentData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                //if(jsonObj.getString("category").equals(spnCategory.getSelectedItem().toString()))
                //{
                    final String idInst = jsonObj.getString("id");
                    name = jsonObj.getString("name");
                    inventory_num = jsonObj.getString("inventory_num");
                    category = jsonObj.getString("category");
                    if(!(allCategories.containsValue(category))) {
                        allCategories.put(idInst, category);
                    }
                    responsible = jsonObj.getString("responsible");
                    condition = jsonObj.getString("equip_condition");
                    address = jsonObj.getString("space");
                    text = name + "\n" + condition + " | " + allAddresses.get(address) + " | " + allResponsible.get(responsible) + " | " + inventory_num;// СОБИРАЕМ ИНФОРМАЦИЮ ОБ ОДНОМ ИНСТРУМЕНТЕ В СТРОКУ

                    textView = new TextView(this);
                    ss = new SpannableString(text);
                    ss.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // ДЕЛАЕМ ИМЯ ИНСТРУМЕНТА ЖИРНЫМ
                    ss.setSpan(new RelativeSizeSpan(1.5f), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Устанавливаем размер имени инструмента
                    ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// Устанавливаем цвет текста имени инструмента
                    ss.setSpan(new RelativeSizeSpan(1.1f), name.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Устанавливаем размер текста после имени
                    ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), name.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// Устанавливаем цвет текста после имени
                    textView.setLayoutParams(myLinearLayoutParams);
                    textView.setText(ss);
                    textView.setClickable(true);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent intent = new Intent(".ChangeInstrument");
                            intent.putExtra("idInstrument", idInst); //in new act in onCreate() {.... variable = getIntent().getExtras().getString("NAME")}
                            startActivity(intent);*/
                        }
                    });

                    TypedValue typedValue = new TypedValue();
                    getTheme().resolveAttribute(android.R.attr.editTextBackground, typedValue, true);
                    if (typedValue.resourceId != 0) {// it's probably a good idea to check if the color wasn't specified as a resource
                        textView.setBackgroundResource(typedValue.resourceId);
                    } else {
                        // this should work whether there was a resource id or not
                        textView.setBackgroundColor(typedValue.data);
                    }

                    myLinearLayout.addView(textView);
                //}
            }
        }
       catch (JSONException e) {
           e.printStackTrace();
        }
    }

    private void fillResponsibleDictionary(){
        String reponsibleId, responsibleFullName;
        allResponsible = new HashMap<String, String>();//map.put("dog", "type of animal");//System.out.println(map.get("dog"));
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting)this.getApplication()).getStaffData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                reponsibleId = jsonObj.getString("id");
                responsibleFullName = jsonObj.getString("full_name");;
                allResponsible.put(reponsibleId, responsibleFullName);
                //Log.d("Value", "reponsibleId = " + reponsibleId + " responsibleFullName = " + allResponsible.get(reponsibleId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillAddressesDictionary(){
        String addressId, shortAddress;
        allAddresses = new HashMap<String, String>();//map.put("dog", "type of animal");//System.out.println(map.get("dog"));
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting)this.getApplication()).getAddressesData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                addressId = jsonObj.getString("id");
                shortAddress = jsonObj.getString("short_address");;
                allAddresses.put(addressId, shortAddress);
                //Log.d("Value", "reponsibleId = " + reponsibleId + " responsibleFullName = " + allResponsible.get(reponsibleId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCategories()
    {/*
        try {
            String category;
            List<String> allCategories = new ArrayList<String>();
            JSONArray jsonArray = new JSONArray(jsonString);
            //Log.d("Value", "jsonString" + jsonString);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                category = jsonObj.getString("category");
                if (!allCategories.contains(category)) {
                    allCategories.add(category);
                }
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allCategories);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnCategory.setAdapter(dataAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }


    /*private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            fillActWithItems();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void processFinish(String output, String typeFinishedProc) {

    }*/

}



/*
       AlertDialog.Builder alert = new AlertDialog.Builder(this);
       //alert.setMessage("Enter Your Message");
       //alert.setTitle("Enter Your Title");

       final EditText edittext = new EditText(this);
       edittext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand_arrow, 0);

       edittext.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               final int DRAWABLE_RIGHT = 2;

               if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                   if (motionEvent.getX() >= (view.getWidth() - ((EditText) view)
                           .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                       lpw.show();
                       return true;
                   }
               }
               return false;
           }
       });

       list = new String[] { "item1", "item2", "item3", "item4" };
       lpw = new ListPopupWindow(this);
       lpw.setAdapter(new ArrayAdapter<String>(this,
               android.R.layout.simple_list_item_1, list));
       lpw.setAnchorView(edittext);
       lpw.setModal(true);
       lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               String item = list[i];
               edittext.setText(item);
               lpw.dismiss();
           }
       });

       alert.setView(edittext);

       alert.setPositiveButton("Установить фильтр", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
               //What ever you want to do with the value
               Editable YouEditTextValue = edittext.getText();
               //OR
               //String YouEditTextValue = edittext.getText().toString();
           }
       });

       /*alert.setNegativeButton("No Option", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
               // what ever you want to do with No option.
           }
       });*/

//alert.show();