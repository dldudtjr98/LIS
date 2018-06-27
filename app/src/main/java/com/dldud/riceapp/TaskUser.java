package com.dldud.riceapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by dldud on 2018-05-07.
 */

public class TaskUser extends AsyncTask<String, Void, String> {
    private String str, receiveMsg;
    public ArrayList<String> idx = new ArrayList<>();
    public ArrayList<String> link_id = new ArrayList<>();
    public ArrayList<String> profile = new ArrayList<>();
    public ArrayList<String> nickname = new ArrayList<>();

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
                link_id.add(jObject.getString("link_id"));
                profile.add(jObject.getString("profile"));
                nickname.add(jObject.getString("nickname"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
