package com.company.inventoryaccounting;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class PlaceListActivity extends AppCompatActivity {
    LinearLayout placeLinearLayout;
    ArrayList<ChoosedPlace> choosedPlaceArrayList = new ArrayList<ChoosedPlace>();//Отобранные данные удовлетворяющие фильтру
    Boolean placeAscendingSort = true, placeDescendingSort = false, placeAlphabetSort = false, placeReverseAlphabetSort = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        Toolbar toolbar = findViewById(R.id.place_tool_bar);
        setSupportActionBar(toolbar);
        placeLinearLayout = (LinearLayout) findViewById(R.id.placeLinLayout);
    }

    @Override
    protected void onStart() {
        getAddressesData();
        super.onStart();
    }


    public boolean onCreateOptionsMenu(Menu menu) { // выполняем при создании меню
        MenuInflater inflater = getMenuInflater(); // подключаем меню
        inflater.inflate(R.menu.place_list_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {// выполняем когда выбран определенный пункт меню
        switch (item.getItemId()) {
            case R.id.placeAscendingSort:
                item.setChecked(!item.isChecked());
                placeAscendingSort = true;
                placeDescendingSort = placeAlphabetSort = placeReverseAlphabetSort = false;
                addChoosedPlaceOnAct();
                return true;
            case R.id.placeDescendingSort:
                item.setChecked(!item.isChecked());
                placeDescendingSort = true;
                placeAscendingSort = placeAlphabetSort = placeReverseAlphabetSort = false;
                addChoosedPlaceOnAct();
                return true;
            case R.id.placeAlphabetSort:
                item.setChecked(!item.isChecked());
                placeAlphabetSort = true;
                placeAscendingSort = placeDescendingSort = placeReverseAlphabetSort = false;
                addChoosedPlaceOnAct();
                return true;
            case R.id.placeReverseAlphabetSort:
                item.setChecked(!item.isChecked());
                placeReverseAlphabetSort = true;
                placeAscendingSort = placeDescendingSort = placeAlphabetSort = false;
                addChoosedPlaceOnAct();
                return true;
        }
        return true; ////default: return super.onOptionsItemSelected(item); //super - текущий экземпляр родительского класса.
    }

    private void addChoosedPlaceOnAct(){
        placeLinearLayout.removeAllViews();
        if(placeAscendingSort){
            Collections.sort(choosedPlaceArrayList, new SortPlaceById());
            for (int i = 0; i < choosedPlaceArrayList.size(); i++)
            {
                placeLinearLayout.addView(createPlaceTextView(choosedPlaceArrayList.get(i).addresID, choosedPlaceArrayList.get(i).shortAddresses, choosedPlaceArrayList.get(i).fullAddresses));
            }
        }
        else if (placeDescendingSort){
            Collections.sort(choosedPlaceArrayList, new SortPlaceById());
            for (int i = choosedPlaceArrayList.size()-1; i >= 0; i--)
            {
                placeLinearLayout.addView(createPlaceTextView(choosedPlaceArrayList.get(i).addresID, choosedPlaceArrayList.get(i).shortAddresses, choosedPlaceArrayList.get(i).fullAddresses));
            }
        }
        else if(placeAlphabetSort){
            Collections.sort(choosedPlaceArrayList, new SortPlaceByShortAdr());
            for (int i = 0; i < choosedPlaceArrayList.size(); i++)
            {
                placeLinearLayout.addView(createPlaceTextView(choosedPlaceArrayList.get(i).addresID, choosedPlaceArrayList.get(i).shortAddresses, choosedPlaceArrayList.get(i).fullAddresses));
            }
        }
        else if(placeReverseAlphabetSort)
        {
            Collections.sort(choosedPlaceArrayList, new SortPlaceByShortAdr());
            for (int i = choosedPlaceArrayList.size()-1; i >= 0; i--)
            {
                placeLinearLayout.addView(createPlaceTextView(choosedPlaceArrayList.get(i).addresID, choosedPlaceArrayList.get(i).shortAddresses, choosedPlaceArrayList.get(i).fullAddresses));
            }
        }

    }

    private TextView createPlaceTextView(final String placeID, final String shortAddress, final String fullAddress) {
        String fullAddressData = shortAddress + "\n" + fullAddress;
        int lengthShortAddress = shortAddress.length();
        LinearLayout.LayoutParams myLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        SpannableString ss;
        TextView textView = new TextView(this);
        ss = new SpannableString(fullAddressData);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, lengthShortAddress, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // ДЕЛАЕМ shortAddress ЖИРНЫМ
        ss.setSpan(new RelativeSizeSpan(1.5f), 0, lengthShortAddress, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Устанавливаем размер shortAddress
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, lengthShortAddress, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// Устанавливаем цвет текста shortAddress
        ss.setSpan(new RelativeSizeSpan(1.1f), lengthShortAddress, fullAddressData.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Устанавливаем размер текста после shortAddress
        ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), lengthShortAddress, fullAddressData.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// Устанавливаем цвет текста после имени
        textView.setLayoutParams(myLinearLayoutParams);
        textView.setText(ss);
        if ((((GlobalInventoryaccounting) this.getApplication()).isAdmin()))
        {
            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(".ChangePlaceActivity");
                    intent.putExtra("employeeID", placeID); //in new act in onCreate() {.... variable = getIntent().getExtras().getString("NAME")}
                    intent.putExtra("shortAddress", shortAddress);
                    intent.putExtra("fullAddress", fullAddress);
                    startActivity(intent);
                }
            });
        }
        TypedValue typedValue = new TypedValue(); // добавляем разделительную черту и отступы, устанавливая стиль
        getTheme().resolveAttribute(android.R.attr.editTextBackground, typedValue, true);
        if (typedValue.resourceId != 0) {// it's probably a good idea to check if the color wasn't specified as a resource
            textView.setBackgroundResource(typedValue.resourceId);
        } else {
            // this should work whether there was a resource id or not
            textView.setBackgroundColor(typedValue.data);
        }
        return textView;
    } // создаем TextView для отобранного инструмента

    private void getAddressesData(){// получаем адреса
        choosedPlaceArrayList.clear();
        String placeID, shortAddress, fullAddress;
        try {
            JSONObject jsonObject = new JSONObject(((GlobalInventoryaccounting)this.getApplication()).getAddressesData());
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                placeID = jsonObj.getString("id");
                shortAddress = jsonObj.getString("short_address");
                fullAddress = jsonObj.getString("full_address");
                choosedPlaceArrayList.add(new ChoosedPlace(placeID, shortAddress, fullAddress));
            }
            addChoosedPlaceOnAct();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

