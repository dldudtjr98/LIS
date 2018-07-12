package com.dldud.riceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapPingActivity extends AppCompatActivity {

    String myString;
    ArrayList<DialogItemData> dIData = new ArrayList<>();
    DialogRecyclerAdapter adapter;
    String imgUrl = "http://52.78.18.156/data/riceapp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_map_ping);

        NetworkUtil.setNetworkPolicy();


        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.mapPingGrid);

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude",0);
        double longitude = intent.getDoubleExtra("longitude", 0);


        try {
            Task task = new Task();
            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();

            task.jsonParser(myString);

            //0.002 근처

            String[] idx = task.idx.toArray(new String[task.idx.size()]);
            String[] writer_id = task.writer_id.toArray(new String[task.writer_id.size()]);
            String[] thumbnail = task.thumbnail.toArray(new String[task.thumbnail.size()]);
            String[] locationlat = task.locationlat.toArray(new String[task.locationlat.size()]);
            String[] locationlong = task.locationlong.toArray(new String[task.locationlong.size()]);

            int length = idx.length;

            for(int i = 0; i<length; i++) {
                double dLat = Double.parseDouble(locationlat[i]);
                double dLong = Double.parseDouble(locationlong[i]);

                if(Math.abs(latitude-dLat)<0.002 && Math.abs(longitude-dLong)<0.002){
                    DialogItemData dItem = new DialogItemData();

                    dItem.strThumbnailImage = imgUrl + thumbnail[i];
                    dItem.strIdx = idx[i];

                    dIData.add(dItem);
                }

            }

            int numberOfColumns = 2;
            recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
            adapter = new DialogRecyclerAdapter(this, dIData);
            recyclerView.setAdapter(adapter);

        } catch(InterruptedException e){
            e.printStackTrace();
        } catch(ExecutionException e){
            e.printStackTrace();
        }


    }
}
