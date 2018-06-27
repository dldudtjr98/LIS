package com.dldud.riceapp;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by dldud on 2018-03-22.
 */

public class Task extends AsyncTask<String, Void, String>{
    private String str, receiveMsg;
    public ArrayList<String> idx = new ArrayList<>();
    public ArrayList<String> writer_id = new ArrayList<>();
    public ArrayList<String> title = new ArrayList<>();
    public ArrayList<String> banner = new ArrayList<>();
    public ArrayList<String> content = new ArrayList<>();
    public ArrayList<String> thumbnail = new ArrayList<>();
    public ArrayList<String> locationlat = new ArrayList<>();
    public ArrayList<String> locationlong = new ArrayList<>();
    public ArrayList<String> create_date = new ArrayList<>();

    @Override
    protected String doInBackground(String... params) {
        String serverURL = params[0];
        try{
            URL url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn.getResponseCode() == conn.HTTP_OK){
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while((str = reader.readLine())!= null){
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg :",receiveMsg);
                reader.close();
            } else {
                Log.i("통신결과", conn.getResponseCode() + "에러");
            }
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return receiveMsg;
    }

    public void jsonParser(String jsonString){
        try{
            JSONArray jarray = new JSONObject(jsonString).getJSONArray("webnautes");
            for(int i = 0 ; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);

                idx.add(jObject.getString("idx"));
                writer_id.add(jObject.getString("writer_id"));
                title.add(jObject.getString("title"));
                banner.add(jObject.getString("banner"));
                content.add(jObject.getString("content"));
                thumbnail.add(jObject.getString("thumbnail"));
                locationlat.add(jObject.getString("locationlat"));
                locationlong.add(jObject.getString("locationlong"));
                create_date.add(jObject.getString("create_date"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
