package com.dldud.riceapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.dldud.riceapp.UserProfileSettingActivity.userId;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPingFragment extends Fragment {

    String userIdx, myString;
    MyPingRecyclerAdapter adapter;
    ArrayList<MyPingItemData> pIData = new ArrayList<>();
    String imgUrl = "http://52.78.18.156/data/riceapp/";

    public MyPingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fragment_my_ping, container, false);
        if(getArguments() != null) {
            userId = getArguments().getString("userId");
        }

        userIdx = userId;


        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_grid);

        try {
            Task task = new Task();
            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();
            task.jsonParser(myString);

            String[] idx = task.idx.toArray(new String[task.idx.size()]);
            String[] writer_id = task.writer_id.toArray(new String[task.writer_id.size()]);
            String[] thumbnail = task.thumbnail.toArray(new String[task.thumbnail.size()]);

            int length = idx.length;

            for(int i = 0; i<length; i++){
                if(userIdx.equals(writer_id[i])){
                    MyPingItemData pItem = new MyPingItemData();

                    pItem.strUserImage = imgUrl + thumbnail[i];
                    pItem.strImageIdx = idx[i];

                    pIData.add(pItem);
                }

            }


            int numberOfColumns = 3;
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
            adapter = new MyPingRecyclerAdapter(getActivity(), pIData);
            recyclerView.setAdapter(adapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        return v;
    }

}
