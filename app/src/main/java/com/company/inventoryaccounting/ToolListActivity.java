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
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolListActivity extends AppCompatActivity implements BackgroundWorkerResponse  {
    String jsonString;
    Spinner spnCategory;
    Map<String, String> allResponsible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_list);

        jsonString = getIntent().getExtras().getString("jsonArray");
        spnCategory = (Spinner)findViewById(R.id.spnCategory);
        spnCategory.setOnItemSelectedListener(listener);
        getCategories();

        String type = "getResponsible";
        new BackgroundWorker(this, this).execute(type);
   }


    private void fillActWithItems(){
        LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.linLayout);
        myLinearLayout.removeAllViews();
        LinearLayout.LayoutParams myLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView;
        SpannableString ss;


        String name = "", inventory_num, responsible;
        String text = "";
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.getString("category").equals(spnCategory.getSelectedItem().toString()))
                {
                    final String idInst = jsonObj.getString("id");
                    name = jsonObj.getString("name");
                    inventory_num = jsonObj.getString("inventory_num");
                    responsible = jsonObj.getString("responsible");
                    text = name + "\n" + inventory_num + " | " + allResponsible.get(responsible);

                    textView = new TextView(this);
                    ss = new SpannableString(text);
                    ss.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new RelativeSizeSpan(1.75f), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
                    ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// set color
                    textView.setLayoutParams(myLinearLayoutParams);
                    textView.setText(ss);
                    textView.setClickable(true);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(".ChangeInstrument");
                            intent.putExtra("idInstrument", idInst); //in new act in onCreate() {.... variable = getIntent().getExtras().getString("NAME")}
                            startActivity(intent);
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
                }
            }
        }
       catch (JSONException e) {
           e.printStackTrace();
        }
    }

    private void getCategories()
    {
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
        }
    }

    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
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
        if(typeFinishedProc.equals("getResponsible"))
        {
            try {
                String reponsibleId, responsibleFullName;
                allResponsible = new HashMap<String, String>();//map.put("dog", "type of animal");//System.out.println(map.get("dog"));
                JSONObject jsonObject = new JSONObject(output);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    reponsibleId = jsonObj.getString("id");
                    responsibleFullName = jsonObj.getString("full_name");;
                    allResponsible.put(reponsibleId, responsibleFullName);
                    //Log.d("Value", "reponsibleId = " + reponsibleId + " responsibleFullName = " + allResponsible.get(reponsibleId));
                }
                fillActWithItems();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
