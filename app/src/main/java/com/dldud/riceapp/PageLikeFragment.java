package com.dldud.riceapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageLikeFragment extends Fragment {

    String PageIdx, userString, likeString;
    String imgUrl = "http://52.78.18.156/data/riceapp/";
    ArrayList<LikeItemData> lData = new ArrayList<>();
    LinearLayoutManager replyLayoutManager;
    LikeAdapter lAdapter;


    public PageLikeFragment() {
        // Required empty public constructor
    }

    public static PageLikeFragment newInstance(){
        Bundle args = new Bundle();

        PageLikeFragment fragment = new PageLikeFragment();
        fragment.setArguments(args);
        return fragment;

    }

    public void setIdx(String idx){
        PageIdx = idx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_page_like, container, false);

        RecyclerView recyclerView;
        recyclerView = (RecyclerView)v.findViewById(R.id.likeRecycler);


        //Feed Idx
        Intent intent = getActivity().getIntent();
        PageIdx = intent.getStringExtra("position");

        try {
            TaskLike taskLike = new TaskLike();
            TaskUser taskUser = new TaskUser();
            likeString = taskLike.execute("http://52.78.18.156/public/ping_like_db.php").get();
            userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
            taskUser.jsonParser(userString);
            taskLike.jsonParser(likeString);

            String[] likeIdx = taskLike.idx.toArray(new String[taskLike.idx.size()]);
            String[] user_idx = taskLike.user_idx.toArray(new String[taskLike.user_idx.size()]);
            String[] ping_idx = taskLike.ping_idx.toArray(new String[taskLike.ping_idx.size()]);

            String[] nickname = taskUser.nickname.toArray(new String[taskUser.nickname.size()]);
            String[] profile = taskUser.profile.toArray(new String[taskUser.profile.size()]);
            String[] userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);

            int likeNum = likeIdx.length;
            int userLinearNum = userIdx.length;

            for (int i = 0; i < likeNum; i++) {
                if (PageIdx.equals(ping_idx[i])) {
                    LikeItemData lItem = new LikeItemData();

                    String strUserId = user_idx[i];

                    for (int j = 0; j < userLinearNum; j++) {
                        String val1 = userIdx[j];
                        if (val1.contains(strUserId)) {
                            lItem.strLikeUserImage = imgUrl + profile[j];
                            lItem.strLikeUserName = nickname[j];
                        }
                    }
                    lData.add(lItem);
                }
            }
            replyLayoutManager = new LinearLayoutManager(getActivity());
            replyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(replyLayoutManager);
            lAdapter = new LikeAdapter(getActivity(), lData);
            recyclerView.setAdapter(lAdapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return v;
    }
}

