package com.company.inventoryaccounting;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ToolListActivity extends AppCompatActivity {
    String jsonArray;
    Spinner spnCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        jsonArray = getIntent().getExtras().getString("jsonArray");
        spnCategory = (Spinner)findViewById(R.id.spnCategory);
        spnCategory.setOnItemSelectedListener(listener);
        getCategories();
        fillActWithItems();
    }


    private void fillActWithItems(){
        String text = "It is my text";
        SpannableString ss = new SpannableString(text);

        LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.linLayout);
        LinearLayout.LayoutParams myLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(this);
        textView.setLayoutParams(myLinearLayoutParams);
        textView.setText(text);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.editTextBackground, typedValue, true);
        if (typedValue.resourceId != 0) {// it's probably a good idea to check if the color wasn't specified as a resource
            textView.setBackgroundResource(typedValue.resourceId);
        } else {
            // this should work whether there was a resource id or not
            textView.setBackgroundColor(typedValue.data);
        }

        myLinearLayout.addView(textView);
    }

    private void getCategories()
    {
        try {
            String category;
            List<String> allCategories = new ArrayList<String>();
            JSONArray array = new JSONArray(jsonArray);
            for(int i = 0; i < array.length(); i++){
                JSONObject jsonObj = array.getJSONObject(i);
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
        }
    }

    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}
