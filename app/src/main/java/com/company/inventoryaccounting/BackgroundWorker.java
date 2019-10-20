package com.company.inventoryaccounting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    String type;

    public BackgroundWorkerResponse delegate = null;

    public BackgroundWorker(Context ctx, BackgroundWorkerResponse delegate){
        this.delegate = delegate;
        context = ctx;
    }


    @Override
    protected String doInBackground(String... params) {
        type = params[0];
        String login_url = "http://192.168.1.2:8080/login.php";//"http://37.20.16.44:8080/login.php";
        String saveEquip_url = "http://192.168.1.2:8080/saveEquip.php";
        String addresses_url = "http://192.168.1.2:8080/getAddresses.php";
        String equip_url = "http://192.168.1.2:8080/getEquip.php";
        String responsible_url = "http://192.168.1.2:8080/getResponsible.php";
        String equipByID_url = "http://192.168.1.2:8080/getEquipById.php";
        String addAddress_url = "http://192.168.1.2:8080/addAddress.php";
        if(type.equals("login")){
            try {
                String phone = params[1];
                String password = params [2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(inputStream,  "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else if(type.equals("getAddresses")){
            try {
                String jsonString;
                URL url = new URL(addresses_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine())!= null)
                {
                    stringBuilder.append(jsonString+"\n");

                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else if(type.equals("getEquip")){
            try {
                String address = params[1];
                String equipCondition = params[2];
                String jsonString;
                URL url = new URL(equip_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("space", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")+ "&"
                        + URLEncoder.encode("condition", "UTF-8") + "=" + URLEncoder.encode(equipCondition, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine())!= null)
                {
                    stringBuilder.append(jsonString+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else if(type.equals("getResponsible")) {
            try {
                String jsonString;
                URL url = new URL(responsible_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine())!= null)
                {
                    stringBuilder.append(jsonString+"\n");

                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else if(type.equals("getEquipByID")){
            try {
                String equipId = params[1];
                String jsonString;
                URL url = new URL(equipByID_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(equipId, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine())!= null)
                {
                    stringBuilder.append(jsonString+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else if (type.equals("saveEquip")){
            try {
                String id = params[1];
                String name = params[2];
                String inventory_num = params[3];
                String responsible = params[4];
                String place = params[5];
                String condition = params[6];
                String jsonString;
                //Log.d("Value",  "id = " + id + "\nname = " + name + "\ninventory_num = " + inventory_num + "\nresponsible = " + responsible + "\nplace = " +  place + "\ncondition = " + condition);
                URL url = new URL(saveEquip_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&"
                        + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                        + URLEncoder.encode("inventory_num", "UTF-8") + "=" + URLEncoder.encode(inventory_num, "UTF-8") + "&"
                        + URLEncoder.encode("responsible", "UTF-8") + "=" + URLEncoder.encode(responsible, "UTF-8") + "&"
                        + URLEncoder.encode("place", "UTF-8") + "=" + URLEncoder.encode(place, "UTF-8")+ "&"
                        + URLEncoder.encode("condition", "UTF-8") + "=" + URLEncoder.encode(condition, "UTF-8");;
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(inputStream));//,  "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        else if (type.equals("addNewPlace")){
            try {
                String shortAddress = params[1];
                String fullAddress = params[2];
                String jsonString;
                URL url = new URL(addAddress_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("short_address", "UTF-8") + "=" + URLEncoder.encode(shortAddress, "UTF-8") + "&"
                        + URLEncoder.encode("full_address", "UTF-8") + "=" + URLEncoder.encode(fullAddress, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(inputStream));//,  "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        //alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        //alertDialog.setMessage(result);
        //alertDialog.show();
        delegate.processFinish(result, type);
        //Log.d("Value", "result= " + result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
