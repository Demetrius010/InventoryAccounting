package com.company.inventoryaccounting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.view.KeyEvent.KEYCODE_SEARCH;

public class ToolListActivity extends AppCompatActivity{
    LinearLayout myLinearLayout;
    EditText searchEt;
    JSONObject jsonObject;
    JSONArray jsonArray;

    Map<String, String> allResponsible;
    Map<String, String> allAddresses;
    Map<String, String> allCategories;

    Boolean placeFilterIsAct = false, categoryFilterIsAct = false, conditionFilterIsAct = false, employeeFilterIsAct = false; // флаг выбранного фильтра
    Boolean placeFilterWasChanged = false, categoryFilterWasChanged = false, conditionFilterWasChanged = false, employeeFilterWasChanged = false, sortWasChanged = true; // флаг что фильтр изменен
    Boolean ascendingSort = true, descendingSort = false, alphabetSort = false, reverseAlphabetSort = false; // флаг сортировки
    //Boolean dynamicSearch = false;
    SubMenu placeFilterSubMenu, categoryFilterSubMenu, conditionFilterSubMenu, employeeFilterSubMenu;// подменю каждого фильтра

    ArrayList<String> selectedPlace = new ArrayList<String>();  //Хранят выбранные пункты фильтров
    ArrayList<String> selectedEmployee = new ArrayList<String>();
    ArrayList<String> selectedCondition = new ArrayList<String>();
    ArrayList<String> selectedCategory = new ArrayList<String>();

    ArrayList<ChoosedTool> choosedToolArrayList = new ArrayList<ChoosedTool>();//Отобранные данные удовлетворяющие фильтру
    String searchStr = "";
    //Spinner spnCategory;
    //private ListPopupWindow lpw;
    //private String[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedPlace.clear();
        selectedEmployee.clear();
        selectedCategory.clear();
        setContentView(R.layout.activity_tool_list);
        searchEt = (EditText) findViewById(R.id.etSearchByName);
        myLinearLayout = (LinearLayout) findViewById(R.id.linLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
   }

public void onSearchIcon(View view){

}
    @Override
    protected void onStart() {
        //dynamicSearch = false;
        fillAddressesDictionary();
        fillCategoryDictionary();
        fillResponsibleDictionary();
        fillActWithItems();
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    searchStr = searchEt.getText().toString();
                    fillActWithItems();
                }
                return false;
            }
        });
        /*searchEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                searchEt.setSelection(searchEt.getText().length());//устанавливаем позицию курсора
                final int DRAWABLE_LEFT = 0;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() <= (searchEt.getLeft() + searchEt.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {// если нажали по иконке поиска меняем режим поиска
                        dynamicSearch = !dynamicSearch;
                        if(dynamicSearch)
                            searchEt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_refresh_search_24dp, 0, 0, 0);// Set drawables for left, top, right, and bottom - send 0 for nothing
                        else
                            searchEt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);// Set drawables for left, top, right, and bottom - send 0 for nothing
                        //searchEt.setSelection(searchEt.getText().length(), searchEt.getText().length());//устанавливаем позицию курсора
                        //return true;
                        searchEt.clearFocus();
                    }
                }
                return false;
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(dynamicSearch){
                    searchStr = charSequence.toString();
                    fillActWithItems();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        /*searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {// убираем фокус со строки поиска если была нажата кнопка "принять"
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    searchEt.clearFocus();
                }
                return false;
            }
        });*/
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // выполняем при создании меню
        MenuInflater inflater = getMenuInflater(); // подключаем меню
        inflater.inflate(R.menu.tool_list_menu, menu);
        placeFilterSubMenu = menu.getItem(1).getSubMenu().getItem(0).getSubMenu();
        for (Map.Entry<String,String> entry : allAddresses.entrySet()) // заполняем фильтр адресс
        {
            placeFilterSubMenu.add(0,Integer.parseInt(entry.getKey()),0,entry.getValue()).setCheckable(true);//add(int groupId, int itemId, int order, CharSequence title)
        }
        categoryFilterSubMenu = menu.getItem(1).getSubMenu().getItem(1).getSubMenu();
        for (Map.Entry<String,String> entry : allCategories.entrySet())// заполняем фильтр категории
        {
            categoryFilterSubMenu.add(1,Integer.parseInt(entry.getKey()),0,entry.getValue()).setCheckable(true);
        }
        conditionFilterSubMenu = menu.getItem(1).getSubMenu().getItem(2).getSubMenu();
        employeeFilterSubMenu = menu.getItem(1).getSubMenu().getItem(3).getSubMenu();
        for (Map.Entry<String,String> entry : allResponsible.entrySet())// заполняем фильтр работников
        {
            employeeFilterSubMenu.add(3,Integer.parseInt(entry.getKey()),0,entry.getValue()).setCheckable(true);
        }
        return true;
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { //выполняем после того как создали меню
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean b) { // получаем состояние меню свернуто / развернуто
                if((placeFilterWasChanged || conditionFilterWasChanged || categoryFilterWasChanged || employeeFilterWasChanged || sortWasChanged) && !b) //если был установлен фильтр для места и фильтр был свернут
                    fillActWithItems(); // если свернуто и была выбрана сортировка или фильтр то получаем соответствующие инструменты
                    //Log.d("Value", "value: onMenuVisibilityChanged = " + b);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// выполняем когда выбран определенный пункт меню
        View view = this.getCurrentFocus();// Check if no view has focus:// скрываем клавиатуру
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            searchEt.clearFocus();
        }
        switch (item.getItemId()) {
            case R.id.applyConditionFilter:
            case R.id.applyEmployeeFilter:
            case R.id.applyPlaceFilter:
            case R.id.applyСategoryFilter:
                return true;
            case R.id.ascendingSort:
                item.setChecked(!item.isChecked());
                ascendingSort = true;
                sortWasChanged = true;
                descendingSort = alphabetSort = reverseAlphabetSort = false;
                return true;
            case R.id.descendingSort:
                item.setChecked(!item.isChecked());
                descendingSort = true;
                sortWasChanged = true;
                ascendingSort = alphabetSort = reverseAlphabetSort = false;
                return true;
            case R.id.alphabetSort:
                item.setChecked(!item.isChecked());
                alphabetSort = true;
                sortWasChanged = true;
                ascendingSort = descendingSort = reverseAlphabetSort = false;
                return true;
            case R.id.reverseAlphabetSort:
                item.setChecked(!item.isChecked());
                reverseAlphabetSort = true;
                sortWasChanged = true;
                alphabetSort = ascendingSort = descendingSort = false;
                return true;
            case R.id.filterBtn:
            case R.id.sortBtn:
                placeFilterIsAct = false;
                categoryFilterIsAct = false;
                conditionFilterIsAct = false;
                employeeFilterIsAct = false;
                placeFilterWasChanged = false;
                categoryFilterWasChanged = false;
                conditionFilterWasChanged = false;
                employeeFilterWasChanged = false;
                sortWasChanged = false;
                break;
            case R.id.placeFilter:
                placeFilterIsAct = true;
                return true;
            case R.id.categoryFilter:
                categoryFilterIsAct = true;
                return true;
            case R.id.conditionFilter:
                conditionFilterIsAct = true;
                return true;
            case R.id.employeeFilter:
                employeeFilterIsAct = true;
                return true;
        }

        if(placeFilterIsAct){
            placeFilterWasChanged = true;
            //Log.d("Value", "value: " + item.getTitle());
            if(item.getItemId() == R.id.placeNone) { // если место "не выбрано" то убираем все галки
                for(int i = 1; i <placeFilterSubMenu.size(); i++){//0->1
                    placeFilterSubMenu.getItem(i).setChecked(false);
                }
                item.setChecked(true);
                selectedPlace.clear(); //и очищаем список выбранных мест
                placeFilterSubMenu.getItem().setIcon(null); // и убераем икону
            }
            else{
                placeFilterSubMenu.getItem(1).setChecked(false);//0->1
                if(item.isChecked()){
                    item.setChecked(false);
                    selectedPlace.remove(item.getTitle().toString());// если снали галку удаляем из списка
                }
                else {
                    item.setChecked(true);// если поставили то добавляем в список
                    if(!selectedPlace.contains(item.getTitle().toString())) {
                        selectedPlace.add(item.getTitle().toString());
                    }
                }
                placeFilterSubMenu.getItem().setIcon(R.drawable.ic_playlist_add_check_black);
            }
        }
        else if(categoryFilterIsAct){
            categoryFilterWasChanged = true;
            //Log.d("Value", "value: " + item.getTitle());
            if(item.getItemId() == R.id.categoryNone) {
                for(int i = 1; i <categoryFilterSubMenu.size(); i++){//0->1
                    categoryFilterSubMenu.getItem(i).setChecked(false);
                }
                item.setChecked(true);
                selectedCategory.clear();
                categoryFilterSubMenu.getItem().setIcon(null);
            }
            else{
                categoryFilterSubMenu.getItem(1).setChecked(false);//0->1
                if(item.isChecked()){
                    item.setChecked(false);
                    selectedCategory.remove(item.getTitle().toString());
                }
                else {
                    item.setChecked(true);
                    if(!selectedCategory.contains(item.getTitle().toString())) {
                        selectedCategory.add(item.getTitle().toString());
                    }
                }
                categoryFilterSubMenu.getItem().setIcon(R.drawable.ic_playlist_add_check_black);
            }
        }
        else if(conditionFilterIsAct){
            conditionFilterWasChanged = true;
            //Log.d("Value", "value: " + item.getTitle());
            if(item.getItemId() == R.id.conditionNone) {
                for(int i = 1; i <conditionFilterSubMenu.size(); i++){//0->1
                    conditionFilterSubMenu.getItem(i).setChecked(false);
                }
                item.setChecked(true);
                selectedCondition.clear();
                conditionFilterSubMenu.getItem().setIcon(null);
            }
            else{
                conditionFilterSubMenu.getItem(1).setChecked(false);//0->1
                if(item.isChecked()){
                    item.setChecked(false);
                    selectedCondition.remove(item.getTitle().toString());
                }
                else {
                    item.setChecked(true);
                    if(!selectedCondition.contains(item.getTitle().toString())) {
                        selectedCondition.add(item.getTitle().toString());
                    }
                }
                conditionFilterSubMenu.getItem().setIcon(R.drawable.ic_playlist_add_check_black);
            }
        }
        else if(employeeFilterIsAct){
            employeeFilterWasChanged = true;
            //Log.d("Value", "value: " + item.getTitle());
            if(item.getItemId() == R.id.employeeNone) {
                for(int i = 1; i <employeeFilterSubMenu.size(); i++){//0->1
                    employeeFilterSubMenu.getItem(i).setChecked(false);
                }
                item.setChecked(true);
                selectedEmployee.clear();
                employeeFilterSubMenu.getItem().setIcon(null);
            }
            else{
                employeeFilterSubMenu.getItem(1).setChecked(false);//0->1
                if(item.isChecked()){
                    item.setChecked(false);
                    selectedEmployee.remove(item.getTitle().toString());
                }
                else {
                    item.setChecked(true);
                    if(!selectedEmployee.contains(item.getTitle().toString())) {
                        selectedEmployee.add(item.getTitle().toString());
                    }
                }
                employeeFilterSubMenu.getItem().setIcon(R.drawable.ic_playlist_add_check_black);
            }
        }
        if(placeFilterIsAct || categoryFilterIsAct || conditionFilterIsAct || employeeFilterIsAct){/*Подавление сокрытия меню*/
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW); //marking the item as it has expandable/collapsible behavior
            item.setActionView(new View(this)); //is a view when item is in expanded state.It is just a dummy view because we will never let it expand
            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {// so it will call this
                @Override //returning false from both the methods of setOnActionExpandListener to suppress expansion and collapsing of the item so the view we gave in previous step would never show up and menu would remain open.
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return false;
                }
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return false;
                }
            });
            return false;
        }
        return true; ////default: return super.onOptionsItemSelected(item); //super - текущий экземпляр родительского класса.
    }

    private void fillActWithItems(){ //Нужно: +ИМЯ,  +СОСТОЯНИЕ, +ИНВЕНТАРНЫЙ НОМЕР, +АДРЕС, +ОТВЕТСТВЕННЫЙ
        myLinearLayout.removeAllViews();
        choosedToolArrayList.clear();
        String idInst, name = "", inventory_num, category, responsible, condition, address;
        String text = "";
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting) this.getApplication()).getEquipmentData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                address = jsonObj.getString("space");
                category = jsonObj.getString("category");
                condition = jsonObj.getString("equip_condition");
                responsible = jsonObj.getString("responsible");
                if((selectedPlace.contains(allAddresses.get(address)) || selectedPlace.isEmpty()) && (selectedEmployee.contains(allResponsible.get(responsible)) || selectedEmployee.isEmpty()) && (selectedCondition.contains(condition) || selectedCondition.isEmpty()) && (selectedCategory.contains(category) || selectedCategory.isEmpty())) {
                    name = jsonObj.getString("name");
                    if(searchStr.length() == 0 || name.toLowerCase().contains(searchStr.toLowerCase())){//выдача тех инструментов в назваии которых есть искомое слово в случае если оно было введено
                        idInst = jsonObj.getString("id");
                        inventory_num = jsonObj.getString("inventory_num");
                        text = name + "\n" + condition + " | " + allAddresses.get(address) + " | " + allResponsible.get(responsible) + " | " + inventory_num;// СОБИРАЕМ ИНФОРМАЦИЮ ОБ ОДНОМ ИНСТРУМЕНТЕ В СТРОКУ
                        choosedToolArrayList.add(new ChoosedTool(text, name, idInst));
                    }
                }
            }
            addChoosedToolsOnAct();
        }
       catch (JSONException e) {
           e.printStackTrace();
        }
    }


    private TextView createTextView(final String text, final String instrumentID, int nameLength){
        LinearLayout.LayoutParams myLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        SpannableString ss;
        final MultipleSelection multipleSelection = new MultipleSelection(false);
        final TextView textView = new TextView(this);
        ss = new SpannableString(text);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, nameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // ДЕЛАЕМ ИМЯ ИНСТРУМЕНТА ЖИРНЫМ
        ss.setSpan(new RelativeSizeSpan(1.5f), 0, nameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Устанавливаем размер имени инструмента
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, nameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// Устанавливаем цвет текста имени инструмента
        ss.setSpan(new RelativeSizeSpan(1.1f), nameLength, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Устанавливаем размер текста после имени
        ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), nameLength, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// Устанавливаем цвет текста после имени
        textView.setLayoutParams(myLinearLayoutParams);
        textView.setText(ss);
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(multipleSelection.getMultipleSelectionFlag()){
                    Intent intent = new Intent(".ChangeMultipleInventoryActivity");
                    startActivity(intent);
                }
                else {*/
                    Intent intent = new Intent(".ChangeInventoryActivity");
                    intent.putExtra("idInstrument", instrumentID); //in new act in onCreate() {.... variable = getIntent().getExtras().getString("NAME")}
                    startActivity(intent);
                //}
            }
        });

        final TypedValue typedValue = new TypedValue(); // добавляем разделительную черту и отступы, устанавливая стиль
        getTheme().resolveAttribute(android.R.attr.editTextBackground, typedValue, true);
        if (typedValue.resourceId != 0) {// it's probably a good idea to check if the color wasn't specified as a resource
            textView.setBackgroundResource(typedValue.resourceId);
        } else {
            // this should work whether there was a resource id or not
            textView.setBackgroundColor(typedValue.data);
        }

        /*textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                multipleSelection.setMultipleSelectionFlag(!multipleSelection.getMultipleSelectionFlag());
                if (multipleSelection.getMultipleSelectionFlag()){
                    textView.setBackgroundColor(getColor(R.color.colorAccent));
                    textView.getBackground().setAlpha(125);//отсчет Alpha с 0
                }
                else {//иначе возвращаем изначальный стиль с разделительной чертой
                    if (typedValue.resourceId != 0) {// it's probably a good idea to check if the color wasn't specified as a resource
                        textView.setBackgroundResource(typedValue.resourceId);
                    } else {
                        // this should work whether there was a resource id or not
                        textView.setBackgroundColor(typedValue.data);
                    }
                }
                return true; //false -> после LongClick срабатывает Click
            }
        });*/

        return textView;
    } // создаем TextView для отобранного инструмента

    private void addChoosedToolsOnAct(){
        String dataString, name, id;
        if(ascendingSort){// в зависимости от сортировки сортируем список и выводим отобранные инструменты в своих TextView
            Collections.sort(choosedToolArrayList, new SortById());
            for (int i = 0; i < choosedToolArrayList.size(); i++)
            {
                dataString = choosedToolArrayList.get(i).toolData;
                name = choosedToolArrayList.get(i).toolName;
                id = choosedToolArrayList.get(i).toolID;
                myLinearLayout.addView(createTextView(dataString, id, name.length()));
            }
        }
        else if(descendingSort){
            Collections.sort(choosedToolArrayList, new SortById());
            for (int i = choosedToolArrayList.size()-1; i >= 0; i--)
            {
                dataString = choosedToolArrayList.get(i).toolData;
                name = choosedToolArrayList.get(i).toolName;
                id = choosedToolArrayList.get(i).toolID;
                myLinearLayout.addView(createTextView(dataString, id, name.length()));
            }
        }
        else if(alphabetSort){
            Collections.sort(choosedToolArrayList, new SortByName());
            for (int i = 0; i < choosedToolArrayList.size(); i++)
            {
                dataString = choosedToolArrayList.get(i).toolData;
                name = choosedToolArrayList.get(i).toolName;
                id = choosedToolArrayList.get(i).toolID;
                myLinearLayout.addView(createTextView(dataString, id, name.length()));
            }
        }
        else if(reverseAlphabetSort){
            Collections.sort(choosedToolArrayList, new SortByName());
            for (int i = choosedToolArrayList.size()-1; i >= 0; i--)
            {
                dataString = choosedToolArrayList.get(i).toolData;
                name = choosedToolArrayList.get(i).toolName;
                id = choosedToolArrayList.get(i).toolID;
                myLinearLayout.addView(createTextView(dataString, id, name.length()));
            }
        }
    }

    private void fillCategoryDictionary(){ // получаем категории
        String instrumentID, category;
        allCategories = new HashMap<String, String>();//map.put("dog", "type of animal");//System.out.println(map.get("dog"));
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting) this.getApplication()).getEquipmentData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                instrumentID = jsonObj.getString("id");
                category = jsonObj.getString("category");
                if(!(allCategories.containsValue(category))) {//заполняем список категорий который будет отображаться в сортировке
                    allCategories.put(instrumentID, category);
                }
            }
            ((GlobalInventoryaccounting)this.getApplication()).setAllCategoryList(new ArrayList<String>(allCategories.values()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillResponsibleDictionary(){// получаем ответственных
        String reponsibleId, responsibleFullName;
        allResponsible = new HashMap<String, String>();//map.put("dog", "type of animal");//System.out.println(map.get("dog"));
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting)this.getApplication()).getStaffData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                reponsibleId = jsonObj.getString("id");
                responsibleFullName = jsonObj.getString("full_name");
                allResponsible.put(reponsibleId, responsibleFullName);
                //Log.d("Value", "reponsibleId = " + reponsibleId + " responsibleFullName = " + allResponsible.get(reponsibleId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillAddressesDictionary(){// получаем адреса
        String addressId, shortAddress;
        allAddresses = new HashMap<String, String>();//map.put("dog", "type of animal");//System.out.println(map.get("dog"));
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting)this.getApplication()).getAddressesData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                addressId = jsonObj.getString("id");
                shortAddress = jsonObj.getString("short_address");
                allAddresses.put(addressId, shortAddress);
                //Log.d("Value", "reponsibleId = " + reponsibleId + " responsibleFullName = " + allResponsible.get(reponsibleId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}


/* private void getCategories()
    {/*
        try {
            String category;
            List<String> instrumentBarcode = new ArrayList<String>();
            JSONArray jsonArray = new JSONArray(jsonString);
            //Log.d("Value", "jsonString" + jsonString);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                category = jsonObj.getString("category");
                if (!instrumentBarcode.contains(category)) {
                    instrumentBarcode.add(category);
                }
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instrumentBarcode);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnCategory.setAdapter(dataAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
 //   }


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

  /*  public void onImgSortBtnClick (View view){
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
    }*/

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