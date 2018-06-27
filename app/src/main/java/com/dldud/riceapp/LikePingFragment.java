package com.dldud.riceapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
public class LikePingFragment extends Fragment {

    String pageIdx, userIdx,myString,likeString, userString, pingIdx;
    MyPingRecyclerAdapter adapter;
    ArrayList<MyPingItemData> pIData = new ArrayList<>();
    String imgUrl = "http://52.78.18.156/data/riceapp/";

    public LikePingFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_like_ping, container, false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_grid);

        try {
            Task task = new Task();
            TaskLike taskLike = new TaskLike();
            TaskUser taskUser = new TaskUser();
            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();
            likeString= taskLike.execute("http://52.78.18.156/public/ping_like_db.php").get();
            userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();

            task.jsonParser(myString);
            taskLike.jsonParser(likeString);
            taskUser.jsonParser(userString);

            String[] idx = task.idx.toArray(new String[task.idx.size()]);
            String[] thumbnail = task.thumbnail.toArray(new String[task.thumbnail.size()]);

            String[] like_idx = taskLike.idx.toArray(new String[taskLike.idx.size()]);
            String[] like_user_idx = taskLike.user_idx.toArray(new String[taskLike.user_idx.size()]);
            String[] like_ping_idx = taskLike.ping_idx.toArray(new String[taskLike.ping_idx.size()]);

            String[] user_idx = taskUser.idx.toArray(new String[taskUser.idx.size()]);
            String[] link_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);

            int length = idx.length;
            int like_length = like_idx.length;
            int user_length = user_idx.length;

            for(int i = 0; i < user_length ; i++){
                if(userId.equals(link_id[i])){
                    userIdx = user_idx[i];
                    break;
                }
            }

            for (int i = 0 ; i<like_length ; i++){
                if(userIdx.equals(like_user_idx[i])){

                    pingIdx = like_ping_idx[i];
                    for(int j = 0 ; j < length ; j++){
                        if(pingIdx.equals(idx[j])) {
                            MyPingItemData pItem = new MyPingItemData();

                            pItem.strUserImage = imgUrl + thumbnail[j];
                            pItem.strImageIdx = idx[j];

                            pIData.add(pItem);
                        }
                    }
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
