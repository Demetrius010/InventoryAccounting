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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class StaffListActivity extends AppCompatActivity {
    SubMenu positionFilterSubMenu, teamFilterSubMenu;
    LinearLayout staffLinearLayout;

    JSONObject jsonObject;
    JSONArray jsonArray;
    Boolean positionFilterIsAct = false, teamFilterIsAct = false; // флаг выбранного фильтра
    Boolean positionFilterWasChanged = false, teamFilterWasChanged = false, sortWasChanged = true; // флаг что фильтр был изменен
    Boolean staffAscendingSort = true, staffDescendingSort = false, staffAlphabetSort = false, staffReverseAlphabetSort = false; // флаг сортировки

    ArrayList<String> uniquePosition =  new ArrayList<String>();
    ArrayList<String> uniqueTeam =  new ArrayList<String>();
    ArrayList<String> selectedPosition = new ArrayList<String>();  //Хранят выбранные пункты фильтров
    ArrayList<String> selectedTeam = new ArrayList<String>();
    ArrayList<ChoosedEmployee> choosedStaffArrayList = new ArrayList<ChoosedEmployee>();//Отобранные данные удовлетворяющие фильтру

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);
        staffLinearLayout = (LinearLayout) findViewById(R.id.staffLinLayout);
        Toolbar toolbar = findViewById(R.id.staff_tool_bar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        getFiltersData();
        fillActByStaff();
        super.onStart();
    }

    public boolean onCreateOptionsMenu(Menu menu) { // выполняем при создании меню
        MenuInflater inflater = getMenuInflater(); // подключаем меню
        inflater.inflate(R.menu.staff_list_menu, menu);
        positionFilterSubMenu = menu.getItem(1).getSubMenu().getItem(0).getSubMenu();
        for (int i = 0; i<uniquePosition.size(); i++) // заполняем фильтр должность
        {
            positionFilterSubMenu.add(0, i,0,uniquePosition.get(i)).setCheckable(true);
        }
        teamFilterSubMenu = menu.getItem(1).getSubMenu().getItem(1).getSubMenu();
        for (int i = 0; i<uniqueTeam.size(); i++)// заполняем фильтр бригада
        {
            teamFilterSubMenu.add(1, i,0,uniqueTeam.get(i)).setCheckable(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { //выполняем после того как создали меню
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean b) { // получаем состояние меню свернуто / развернуто
                if((positionFilterWasChanged || teamFilterWasChanged || sortWasChanged) && !b) //если был установлен фильтр для места и фильтр был свернут
                    fillActByStaff(); // если свернуто и была выбрана сортировка или фильтр то получаем соответствующие инструменты
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// выполняем когда выбран определенный пункт меню
        switch (item.getItemId()) {
            case R.id.staffAscendingSort:
                item.setChecked(!item.isChecked());
                staffAscendingSort = true;
                sortWasChanged = true;
                staffDescendingSort = staffAlphabetSort = staffReverseAlphabetSort = false;
                return true;
            case R.id.staffDescendingSort:
                item.setChecked(!item.isChecked());
                staffDescendingSort = true;
                sortWasChanged = true;
                staffAscendingSort = staffAlphabetSort = staffReverseAlphabetSort = false;
                return true;
            case R.id.staffAlphabetSort:
                item.setChecked(!item.isChecked());
                staffAlphabetSort = true;
                sortWasChanged = true;
                staffAscendingSort = staffDescendingSort = staffReverseAlphabetSort = false;
                return true;
            case R.id.staffReverseAlphabetSort:
                item.setChecked(!item.isChecked());
                staffReverseAlphabetSort = true;
                sortWasChanged = true;
                staffAlphabetSort = staffAscendingSort = staffDescendingSort = false;
                return true;
            case R.id.staffFilterBtn:
            case R.id.sortStaffBtn:
                positionFilterIsAct = false;
                teamFilterIsAct = false;
                positionFilterWasChanged = false;
                teamFilterWasChanged = false;
                sortWasChanged = false;
                break;
            case R.id.positionFilter:
                positionFilterIsAct = true;
                return true;
            case R.id.teamFilter:
                teamFilterIsAct = true;
                return true;
        }

        if(positionFilterIsAct){
            positionFilterWasChanged = true;
            //Log.d("Value", "value: " + item.getTitle());
            if(item.getItemId() == R.id.positionNone) { // если место "не выбрано" то убираем все галки
                for(int i = 0; i <positionFilterSubMenu.size(); i++){
                    positionFilterSubMenu.getItem(i).setChecked(false);
                }
                item.setChecked(true);
                selectedPosition.clear(); //и очищаем список выбранных должностей
                positionFilterSubMenu.getItem().setIcon(null); // и убераем икону
            }
            else{
                positionFilterSubMenu.getItem(0).setChecked(false);
                if(item.isChecked()){
                    item.setChecked(false);
                    selectedPosition.remove(item.getTitle().toString());// если снали галку удаляем из списка
                }
                else {
                    item.setChecked(true);// если поставили то добавляем в список
                    if(!selectedPosition.contains(item.getTitle().toString())) {
                        selectedPosition.add(item.getTitle().toString());
                    }
                }
                positionFilterSubMenu.getItem().setIcon(R.drawable.ic_playlist_add_check_black);
            }
        }
        else if(teamFilterIsAct){
            teamFilterWasChanged = true;
            if(item.getItemId() == R.id.teamNone) {
                for(int i = 0; i <teamFilterSubMenu.size(); i++){
                    teamFilterSubMenu.getItem(i).setChecked(false);
                }
                item.setChecked(true);
                selectedTeam.clear();
                teamFilterSubMenu.getItem().setIcon(null);
            }
            else{
                teamFilterSubMenu.getItem(0).setChecked(false);
                if(item.isChecked()){
                    item.setChecked(false);
                    selectedTeam.remove(item.getTitle().toString());
                }
                else {
                    item.setChecked(true);
                    if(!selectedTeam.contains(item.getTitle().toString())) {
                        selectedTeam.add(item.getTitle().toString());
                    }
                }
                teamFilterSubMenu.getItem().setIcon(R.drawable.ic_playlist_add_check_black);
            }
        }

        if(positionFilterIsAct || teamFilterIsAct){//Подавление сокрытия меню
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
        return true; //default: return super.onOptionsItemSelected(item); //super - текущий экземпляр родительского класса.
    }

    private void getFiltersData(){
        String responsiblePosition, responsibleTeam;
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting)this.getApplication()).getStaffData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                responsiblePosition = jsonObj.getString("position");
                if(!uniquePosition.contains(responsiblePosition))
                    uniquePosition.add(responsiblePosition);
                responsibleTeam = jsonObj.getString("team");
                if(!uniqueTeam.contains(responsibleTeam))
                    uniqueTeam.add(responsibleTeam);
            }
            ((GlobalInventoryaccounting)this.getApplication()).setAllPositionList(uniquePosition);
            ((GlobalInventoryaccounting)this.getApplication()).setAllTeamList(uniqueTeam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillActByStaff(){// получаем ответственных
        staffLinearLayout.removeAllViews();
        choosedStaffArrayList.clear();
        String reponsibleId, responsibleFullName, responsiblePhone, responsiblePosition, responsibleTeam;
        try {
            jsonObject = new JSONObject(((GlobalInventoryaccounting)this.getApplication()).getStaffData());
            jsonArray = jsonObject.getJSONArray("server_response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                responsiblePosition = jsonObj.getString("position");
                responsibleTeam = jsonObj.getString("team");
                if((selectedPosition.contains(responsiblePosition) || selectedPosition.isEmpty()) && (selectedTeam.contains(responsibleTeam) || selectedTeam.isEmpty())) {
                    reponsibleId = jsonObj.getString("id");
                    responsibleFullName = jsonObj.getString("full_name");
                    responsiblePhone = jsonObj.getString("phone");
                    choosedStaffArrayList.add(new ChoosedEmployee(reponsibleId, responsibleFullName, responsiblePhone, responsiblePosition, responsibleTeam));
                }
            }
            addChoosedToolsOnAct();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addChoosedToolsOnAct(){
        String id, name, phone, position, team;//employeeID, employeeName, employeePosition, employeeTeam;
        if(staffAscendingSort){// в зависимости от сортировки сортируем список и выводим отобранные инструменты в своих TextView
            Collections.sort(choosedStaffArrayList, new SortStaffById());
            for (int i = 0; i < choosedStaffArrayList.size(); i++)
            {
                id = choosedStaffArrayList.get(i).employeeID;
                name = choosedStaffArrayList.get(i).employeeName;
                position = choosedStaffArrayList.get(i).employeePosition;
                team = choosedStaffArrayList.get(i).employeeTeam;
                phone = choosedStaffArrayList.get(i).employeePhone;
                staffLinearLayout.addView(createTextView(id, name, phone, position, team));
            }
        }
        else if(staffDescendingSort){
            Collections.sort(choosedStaffArrayList, new SortStaffById());;
            for (int i = choosedStaffArrayList.size()-1; i >= 0; i--)
            {
                id = choosedStaffArrayList.get(i).employeeID;
                name = choosedStaffArrayList.get(i).employeeName;
                position = choosedStaffArrayList.get(i).employeePosition;
                team = choosedStaffArrayList.get(i).employeeTeam;
                phone = choosedStaffArrayList.get(i).employeePhone;
                staffLinearLayout.addView(createTextView(id, name, phone, position, team));
            }
        }
        else if(staffAlphabetSort){
            Collections.sort(choosedStaffArrayList, new SortStaffByName());
            for (int i = 0; i < choosedStaffArrayList.size(); i++)
            {
                id = choosedStaffArrayList.get(i).employeeID;
                name = choosedStaffArrayList.get(i).employeeName;
                position = choosedStaffArrayList.get(i).employeePosition;
                team = choosedStaffArrayList.get(i).employeeTeam;
                phone = choosedStaffArrayList.get(i).employeePhone;
                staffLinearLayout.addView(createTextView(id, name, phone, position, team));
            }
        }
        else if(staffReverseAlphabetSort){
            Collections.sort(choosedStaffArrayList, new SortStaffByName());
            for (int i = choosedStaffArrayList.size()-1; i >= 0; i--)
            {
                id = choosedStaffArrayList.get(i).employeeID;
                name = choosedStaffArrayList.get(i).employeeName;
                position = choosedStaffArrayList.get(i).employeePosition;
                team = choosedStaffArrayList.get(i).employeeTeam;
                phone = choosedStaffArrayList.get(i).employeePhone;
                staffLinearLayout.addView(createTextView(id, name, phone, position, team));
            }
        }
    }

    private TextView createTextView(final String employeeID, final String employeeName, final String employeePhone, final String employeePosition, final String employeeTeam){//employeeID, employeeName, employeePosition, employeeTeam;
        String text = employeeName + "\n" + employeePosition + " | " + employeeTeam;
        int nameLength = employeeName.length();
        LinearLayout.LayoutParams myLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        SpannableString ss;
        TextView textView = new TextView(this);
        ss = new SpannableString(text);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, nameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // ДЕЛАЕМ ИМЯ ИНСТРУМЕНТА ЖИРНЫМ
        ss.setSpan(new RelativeSizeSpan(1.5f), 0, nameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Устанавливаем размер имени инструмента
        ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, nameLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// Устанавливаем цвет текста имени инструмента
        ss.setSpan(new RelativeSizeSpan(1.1f), nameLength, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Устанавливаем размер текста после имени
        ss.setSpan(new ForegroundColorSpan(Color.DKGRAY), nameLength, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);// Устанавливаем цвет текста после имени
        textView.setLayoutParams(myLinearLayoutParams);
        textView.setText(ss);
        if ((((GlobalInventoryaccounting) this.getApplication()).isAdmin())) {
            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(".ChangeEmployeeActivity");
                intent.putExtra("employeeID", employeeID);
                intent.putExtra("employeeName", employeeName);
                intent.putExtra("employeePhone", employeePhone);
                intent.putExtra("employeePosition", employeePosition);
                intent.putExtra("employeeTeam", employeeTeam);
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
}