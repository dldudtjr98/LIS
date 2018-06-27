package com.dldud.riceapp;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.dldud.riceapp.MainActivity.navigation;
import static com.dldud.riceapp.UserProfileSettingActivity.userId;

/**
 * A simple {@link Fragment} subclass.
 * implements AbsListView.OnScrollListener
 */
public class FeedFragment extends Fragment implements FeedAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{

    private ArrayList<Integer> feedIdxs = new ArrayList<>();
    private FeedAdapter oAdapter;
    private ArrayList<ItemData> oData;
    private ArrayList<FeedLikeItemData> flData;
    private List<ItemData> load;

    boolean isLike= false;
    boolean isDistance = false;

    private String imgUrl = "http://52.78.18.156/data/riceapp/";

    private String user_index;
    private String dateDiff;
    private Date date;
    String[] idx, title, thumbnail, writer_id, banner, content,
            locationLat, locationLong,create_date, link_id, nickname,
            profile, userIdx, likeIdx, likeCnt, replyIdx, replyCnt, likeUserIdx;
    int linearNum, userLinearNum, likeLinearNum, replyLinearNum;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView recyclerView;
    double latitude,longitude;

    private Boolean isFabOpen = false;
    private FloatingActionButton fabOpen,fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private ConstraintLayout fabBack;
    private ConstraintLayout videoBack;
    private ConstraintLayout pictureBack;

    public FeedFragment() {
        // Required empty public constructor
    }

    public void setIdxs(ArrayList<Integer> arr)
    {
        feedIdxs = arr;
    }

    /*
    public static FeedFragment newInstance{
        return new FeedFragment();
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.dynamicLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        videoBack = (ConstraintLayout)v.findViewById(R.id.videoOn);
        pictureBack = (ConstraintLayout)v.findViewById(R.id.imageOn);

        fabBack = (ConstraintLayout)v.findViewById(R.id.fabOn);
        fabOpen = (FloatingActionButton)v.findViewById(R.id.fabOpen);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab1 = (FloatingActionButton)v.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)v.findViewById(R.id.fab2);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);

        oData = new ArrayList<>();

        GPSInfo gps = new GPSInfo(getActivity());
        //GPS 사용유무
        if(gps.isGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        long now = System.currentTimeMillis();
        date = new Date(now);

        String myString;
        String userString;
        String likeString;
        String replyString;

        fabBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
            Animation.AnimationListener closeAnimationListener = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabBack.setVisibility(View.GONE);
                    fabOpen.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            };
            fab_close.setAnimationListener(closeAnimationListener);
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);

            isFabOpen = false;
            return true;
            }
        });

        videoBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                videoBack.setVisibility(View.GONE);
                return true;
            }
        });

        pictureBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                pictureBack.setVisibility(View.GONE);
                return true;
            }
        });

        fabOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            fabBack.setVisibility(View.VISIBLE);
            fabOpen.setVisibility(View.GONE);
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            }
        });

        fab1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                setLikeData();

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                oAdapter.setRecyclerView(recyclerView);
                oAdapter.setLinearLayoutManager(layoutManager);
                recyclerView.setAdapter(oAdapter);

                Animation.AnimationListener closeAnimationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fabBack.setVisibility(View.GONE);
                        fabOpen.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                fab_close.setAnimationListener(closeAnimationListener);
                fab.startAnimation(rotate_backward);
                fab1.startAnimation(fab_close);
                fab2.startAnimation(fab_close);
                fab1.setClickable(false);
                fab2.setClickable(false);

                isFabOpen = false;
                return true;
            }
        });

        fab2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                setDistanceData();

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                oAdapter.setRecyclerView(recyclerView);
                oAdapter.setLinearLayoutManager(layoutManager);
                recyclerView.setAdapter(oAdapter);

                Animation.AnimationListener closeAnimationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fabBack.setVisibility(View.GONE);
                        fabOpen.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                fab_close.setAnimationListener(closeAnimationListener);
                fab.startAnimation(rotate_backward);
                fab1.startAnimation(fab_close);
                fab2.startAnimation(fab_close);
                fab1.setClickable(false);
                fab2.setClickable(false);

                isFabOpen = false;
                return true;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    fabOpen.hide();
                } else if (dy < 0){
                    fabOpen.show();
                }
            }
        });

        try{
            FeedLikeItemData fItem = new FeedLikeItemData();
            Task task = new Task();
            TaskUser taskUser = new TaskUser();
            TaskLike taskLike = new TaskLike();
            TaskReply taskReply = new TaskReply();

            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();
            userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();
            likeString = taskLike.execute("http://52.78.18.156/public/ping_like_db.php").get();
            replyString = taskReply.execute("http://52.78.18.156/public/comment_db.php").get();

            task.jsonParser(myString);
            taskUser.jsonParser(userString);
            taskLike.jsonParser(likeString);
            taskReply.jsonParser(replyString);

            //list to array
            idx = task.idx.toArray(new String[task.idx.size()]);
            title = task.title.toArray(new String[task.title.size()]);
            thumbnail = task.thumbnail.toArray(new String[task.thumbnail.size()]);
            writer_id = task.writer_id.toArray(new String[task.writer_id.size()]);
            banner = task.banner.toArray(new String[task.banner.size()]);
            content = task.content.toArray(new String[task.content.size()]);
            locationLat = task.locationlat.toArray(new String[task.locationlat.size()]);
            locationLong = task.locationlong.toArray(new String[task.locationlong.size()]);
            create_date = task.create_date.toArray(new String[task.create_date.size()]);

            link_id = taskUser.link_id.toArray(new String[taskUser.link_id.size()]);
            nickname = taskUser.nickname.toArray(new String[taskUser.nickname.size()]);
            profile = taskUser.profile.toArray(new String[taskUser.profile.size()]);
            userIdx = taskUser.idx.toArray(new String[taskUser.idx.size()]);

            likeIdx = taskLike.idx.toArray(new String[taskLike.idx.size()]);
            likeCnt = taskLike.ping_idx.toArray(new String[taskLike.ping_idx.size()]);
            likeUserIdx = taskLike.user_idx.toArray(new String[taskLike.user_idx.size()]);

            replyIdx = taskReply.idx.toArray(new String[taskReply.idx.size()]);
            replyCnt = taskReply.ping_idx.toArray(new String[taskReply.ping_idx.size()]);



            linearNum = idx.length;
            userLinearNum = userIdx.length;
            likeLinearNum = likeIdx.length;
            replyLinearNum = replyIdx.length;
/*
            for(int i = 0; i < userLinearNum ; i++){
                if(userId.equals(userIdx[i])){
                    user_index = userIdx[i];
                    break;
                }
            }

            for(int j = 0 ; j < likeLinearNum ; j++){
                if(user_index.equals(likeUserIdx[j])){
                    fItem.strIdx = likeUserIdx[j];
                    flData.add(fItem);
                }
            }
*/

        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        oAdapter = new FeedAdapter(getActivity(),this);
        oAdapter.setRecyclerView(recyclerView);
        oAdapter.setLinearLayoutManager(layoutManager);
        recyclerView.setAdapter(oAdapter);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        loadData();
    }

    // load initial data
    private void loadData() {
        oData.clear();
        for (int i = 0; i <= 5; i++) {
            if (feedIdxs.contains(Integer.parseInt(idx[linearNum - (i + 1)])) || feedIdxs.size() == 0) {
                try {
                    ItemData oItem = new ItemData();
                    int like = 0;
                    int reply = 0;

                    oItem.strIdx = idx[linearNum - (i + 1)];
                    oItem.strLike = "좋아요";
                    oItem.strShare = "공유";
                    oItem.strUserId = writer_id[linearNum - (i + 1)];
                    oItem.strReply = "댓글";
                    oItem.strContent = content[linearNum - (i + 1)];
                    oItem.strTitile = title[linearNum - (i + 1)];

                    oItem.strThumbnailImage = imgUrl + thumbnail[linearNum - (i + 1)];
                    oItem.douLatitude = Double.parseDouble(locationLat[linearNum - (i + 1)]);
                    oItem.douLongitude = Double.parseDouble(locationLong[linearNum - (i + 1)]);
                    oItem.strVideo = imgUrl + banner[linearNum - (i + 1)];
                    oItem.pingDistance = Math.sqrt(Math.pow((latitude - oItem.douLatitude),2) + Math.pow((longitude - oItem.douLongitude),2));
                    oItem.strDistance = String.valueOf(oItem.pingDistance);

                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                    Date d2 = f.parse(create_date[linearNum - (i + 1)]);
                    long diff = date.getTime() - d2.getTime();
                    long sec = diff / 1000;
                    if(sec < 60) {
                        dateDiff = Long.toString(sec) + "초 전";
                    } else if (sec < 3600 && sec >= 60){
                        dateDiff = Long.toString(sec/60) + "분 전";
                    } else if (sec < 86400 && sec >=3600){
                        dateDiff = Long.toString(sec/3600) + "시간 전";
                    } else if (sec < 604800 && sec >= 86400){
                        dateDiff = Long.toString(sec/86400) + "일 전";
                    } else if (sec < 2419200 && sec >= 604800){
                        dateDiff = Long.toString(sec/604800) + "주 전";
                    } else if (sec < 31536000 && sec >= 2419200){
                        dateDiff = Long.toString(sec/2419200) + "달 전";
                    } else{
                        dateDiff = "오래 전";
                    }
                    oItem.strTime = dateDiff;

                    if (!oItem.strUserId.equals("0")) {
                        for (int j = 0; j < userLinearNum; j++) {
                            String val = link_id[j];
                            if (val.contains(oItem.strUserId)) {
                                oItem.strUserImage = imgUrl + profile[j];
                                oItem.strUserName = nickname[j];
                            }
                        }
                    } else {
                        oItem.strUserImage = imgUrl + "null.jpg";
                        oItem.strUserName = "익명";
                    }
                    for (int k = 0; k < likeLinearNum; k++) {
                        if (likeCnt[k].equals(idx[linearNum - (i + 1)])) {
                            like++;
                        }
                    }

                    for (int l = 0; l < replyLinearNum; l++) {
                        if (replyCnt[l].equals(idx[linearNum - (i + 1)])) {
                            reply++;
                        }
                    }
                    String strLike;
                    String strReply;

                    strLike = String.valueOf(like);
                    strReply = String.valueOf(reply);

                    oItem.pingReply = reply;
                    oItem.pingLike = like;

                    oData.add(oItem);
                }catch(ParseException e){
                    e.printStackTrace();
                }
            }
        }
        oAdapter.addAll(oData);
    }

    @Override
    public void onLoadMore() {
        if(!isDistance && !isLike) {
            Log.d("MainActivity_", "onLoadMore");
            oAdapter.setProgressMore(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    oData.clear();
                    oAdapter.setProgressMore(false);

                    int start = oAdapter.getItemCount();
                    int end = start + 5;

                    //bound end
                    if(end > linearNum){
                        end = linearNum;
                    }

                    for (int i = start + 1; i < end; i++) {
                        if (feedIdxs.contains(Integer.parseInt(idx[linearNum - (i + 1)])) || feedIdxs.size() == 0) {
                            try {
                                ItemData oItem = new ItemData();
                                int like = 0;
                                int reply = 0;

                                oItem.strIdx = idx[linearNum - (i + 1)];
                                oItem.strLike = "좋아요";
                                oItem.strShare = "공유";
                                oItem.strUserId = writer_id[linearNum - (i + 1)];
                                oItem.strReply = "댓글";
                                oItem.strContent = content[linearNum - (i + 1)];
                                oItem.strTitile = title[linearNum - (i + 1)];

                                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                                Date d2 = f.parse(create_date[linearNum - (i + 1)]);
                                long diff = date.getTime() - d2.getTime();
                                long sec = diff / 1000;
                                if(sec < 60) {
                                    dateDiff = Long.toString(sec) + "초 전";
                                } else if (sec < 3600 && sec >= 60){
                                    dateDiff = Long.toString(sec/60) + "분 전";
                                } else if (sec < 86400 && sec >=3600){
                                    dateDiff = Long.toString(sec/3600) + "시간 전";
                                } else if (sec < 604800 && sec >= 86400){
                                    dateDiff = Long.toString(sec/86400) + "일 전";
                                } else if (sec < 2419200 && sec >= 604800){
                                    dateDiff = Long.toString(sec/604800) + "주 전";
                                } else if (sec < 31536000 && sec >= 2419200){
                                    dateDiff = Long.toString(sec/2419200) + "달 전";
                                } else{
                                    dateDiff = "오래 전";
                                }

                                oItem.strTime = dateDiff;
                                oItem.strThumbnailImage = imgUrl + thumbnail[linearNum - (i + 1)];
                                oItem.douLatitude = Double.parseDouble(locationLat[linearNum - (i + 1)]);
                                oItem.douLongitude = Double.parseDouble(locationLong[linearNum - (i + 1)]);
                                oItem.strVideo = imgUrl + banner[linearNum - (i + 1)];
                                oItem.pingDistance = Math.sqrt(Math.pow((latitude - oItem.douLatitude),2) + Math.pow((longitude - oItem.douLongitude),2));
                                oItem.strDistance = String.valueOf(oItem.pingDistance);

                                if (!oItem.strUserId.equals("0")) {
                                    for (int j = 0; j < userLinearNum; j++) {
                                        String val = link_id[j];
                                        if (val.contains(oItem.strUserId)) {
                                            oItem.strUserImage = imgUrl + profile[j];
                                            oItem.strUserName = nickname[j];
                                        }
                                    }
                                } else {
                                    oItem.strUserImage = imgUrl + "null.jpg";
                                    oItem.strUserName = "익명";
                                }
                                for (int k = 0; k < likeLinearNum; k++) {
                                    if (likeCnt[k].equals(idx[linearNum - (i + 1)])) {
                                        like++;
                                    }
                                }

                                for (int l = 0; l < replyLinearNum; l++) {
                                    if (replyCnt[l].equals(idx[linearNum - (i + 1)])) {
                                        reply++;
                                    }
                                }

                                oItem.pingReply = reply;
                                oItem.pingLike = like;

                                oData.add(oItem);
                            } catch(ParseException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    //////////////////////////////////////////////////
                    if(feedIdxs.size() > 0)
                        oAdapter.setShowMap(false);
                    oAdapter.addItemMore(oData);
                    oAdapter.setMoreLoading(false);
                }
            }, 1000);
        }else if(isDistance){
            Log.d("MainActivity_", "onLoadMore");
            oAdapter.setProgressMore(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    oData.clear();
                    oAdapter.setProgressMore(false);

                    int start = oAdapter.getItemCount();
                    int end = start + 5;

                    //bound end
                    if(end > linearNum){
                        end = linearNum;
                    }

                    for (int i = 0; i < linearNum; i++) {
                        try {
                            ItemData oItem = new ItemData();
                            int like = 0;
                            int reply = 0;

                            oItem.douLatitude = Double.parseDouble(locationLat[linearNum - (i + 1)]);
                            oItem.douLongitude = Double.parseDouble(locationLong[linearNum - (i + 1)]);
                            oItem.pingDistance = Math.sqrt(Math.pow((latitude - oItem.douLatitude),2) + Math.pow((longitude - oItem.douLongitude),2));
                            oItem.strDistance = String.valueOf(oItem.pingDistance);

                            oItem.strIdx = idx[linearNum - (i + 1)];
                            oItem.strLike = "좋아요";
                            oItem.strShare = "공유";
                            oItem.strUserId = writer_id[linearNum - (i + 1)];
                            oItem.strReply = "댓글";
                            oItem.strContent = content[linearNum - (i + 1)];
                            oItem.strTitile = title[linearNum - (i + 1)];

                            oItem.strThumbnailImage = imgUrl + thumbnail[linearNum - (i + 1)];

                            oItem.strVideo = imgUrl + banner[linearNum - (i + 1)];

                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                            Date d2 = f.parse(create_date[linearNum - (i + 1)]);
                            long diff = date.getTime() - d2.getTime();
                            long sec = diff / 1000;
                            if (sec < 60) {
                                dateDiff = Long.toString(sec) + "초 전";
                            } else if (sec < 3600 && sec >= 60) {
                                dateDiff = Long.toString(sec / 60) + "분 전";
                            } else if (sec < 86400 && sec >= 3600) {
                                dateDiff = Long.toString(sec / 3600) + "시간 전";
                            } else if (sec < 604800 && sec >= 86400) {
                                dateDiff = Long.toString(sec / 86400) + "일 전";
                            } else if (sec < 2419200 && sec >= 604800) {
                                dateDiff = Long.toString(sec / 604800) + "주 전";
                            } else if (sec < 31536000 && sec >= 2419200) {
                                dateDiff = Long.toString(sec / 2419200) + "달 전";
                            } else {
                                dateDiff = "오래 전";
                            }
                            oItem.strTime = dateDiff;

                            if (!oItem.strUserId.equals("0")) {
                                for (int j = 0; j < userLinearNum; j++) {
                                    String val = link_id[j];
                                    if (val.contains(oItem.strUserId)) {
                                        oItem.strUserImage = imgUrl + profile[j];
                                        oItem.strUserName = nickname[j];
                                    }
                                }
                            } else {
                                oItem.strUserImage = imgUrl + "null.jpg";
                                oItem.strUserName = "익명";
                            }
                            for (int k = 0; k < likeLinearNum; k++) {
                                if (likeCnt[k].equals(idx[linearNum - (i + 1)])) {
                                    like++;
                                }
                            }

                            for (int l = 0; l < replyLinearNum; l++) {
                                if (replyCnt[l].equals(idx[linearNum - (i + 1)])) {
                                    reply++;
                                }
                            }

                            oItem.pingReply = reply;
                            oItem.pingLike = like;

                            oData.add(oItem);


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    //DISTANCE 오름차순 정렬
                    Collections.sort(oData, new Comparator<ItemData>() {
                        @Override
                        public int compare(ItemData d1, ItemData d2) {
                            if (d1.getPingDistance() < d2.getPingDistance()) {
                                return -1;
                            } else if (d1.getPingDistance() > d2.getPingDistance()) {
                                return 1;
                            }
                            return 0;
                        }
                    });

                    for (int i = start + 1; i < end; i++) {
                        load = oData.subList(start,end);
                    }
                    //////////////////////////////////////////////////
                    if(feedIdxs.size() > 0)
                        oAdapter.setShowMap(false);
                    oAdapter.addItemMore(load);
                    oAdapter.setMoreLoading(false);
                }
            }, 1000);
        }else if(isLike) {
            Log.d("MainActivity_", "onLoadMore");
            oAdapter.setProgressMore(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    oData.clear();
                    oAdapter.setProgressMore(false);

                    int start = oAdapter.getItemCount();
                    int end = start + 5;

                    //bound end
                    if (end > linearNum) {
                        end = linearNum;
                    }

                    for (int i = 0; i < linearNum; i++) {
                        try {
                            ItemData oItem = new ItemData();
                            int like = 0;
                            int reply = 0;

                            oItem.douLatitude = Double.parseDouble(locationLat[linearNum - (i + 1)]);
                            oItem.douLongitude = Double.parseDouble(locationLong[linearNum - (i + 1)]);
                            oItem.pingDistance = Math.sqrt(Math.pow((latitude - oItem.douLatitude), 2) + Math.pow((longitude - oItem.douLongitude), 2));
                            oItem.strDistance = String.valueOf(oItem.pingDistance);

                            oItem.strIdx = idx[linearNum - (i + 1)];
                            oItem.strLike = "좋아요";
                            oItem.strShare = "공유";
                            oItem.strUserId = writer_id[linearNum - (i + 1)];
                            oItem.strReply = "댓글";
                            oItem.strContent = content[linearNum - (i + 1)];
                            oItem.strTitile = title[linearNum - (i + 1)];

                            oItem.strThumbnailImage = imgUrl + thumbnail[linearNum - (i + 1)];

                            oItem.strVideo = imgUrl + banner[linearNum - (i + 1)];

                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                            Date d2 = f.parse(create_date[linearNum - (i + 1)]);
                            long diff = date.getTime() - d2.getTime();
                            long sec = diff / 1000;
                            if (sec < 60) {
                                dateDiff = Long.toString(sec) + "초 전";
                            } else if (sec < 3600 && sec >= 60) {
                                dateDiff = Long.toString(sec / 60) + "분 전";
                            } else if (sec < 86400 && sec >= 3600) {
                                dateDiff = Long.toString(sec / 3600) + "시간 전";
                            } else if (sec < 604800 && sec >= 86400) {
                                dateDiff = Long.toString(sec / 86400) + "일 전";
                            } else if (sec < 2419200 && sec >= 604800) {
                                dateDiff = Long.toString(sec / 604800) + "주 전";
                            } else if (sec < 31536000 && sec >= 2419200) {
                                dateDiff = Long.toString(sec / 2419200) + "달 전";
                            } else {
                                dateDiff = "오래 전";
                            }
                            oItem.strTime = dateDiff;

                            if (!oItem.strUserId.equals("0")) {
                                for (int j = 0; j < userLinearNum; j++) {
                                    String val = link_id[j];
                                    if (val.contains(oItem.strUserId)) {
                                        oItem.strUserImage = imgUrl + profile[j];
                                        oItem.strUserName = nickname[j];
                                    }
                                }
                            } else {
                                oItem.strUserImage = imgUrl + "null.jpg";
                                oItem.strUserName = "익명";
                            }
                            for (int k = 0; k < likeLinearNum; k++) {
                                if (likeCnt[k].equals(idx[linearNum - (i + 1)])) {
                                    like++;
                                }
                            }

                            for (int l = 0; l < replyLinearNum; l++) {
                                if (replyCnt[l].equals(idx[linearNum - (i + 1)])) {
                                    reply++;
                                }
                            }

                            oItem.pingReply = reply;
                            oItem.pingLike = like;

                            oData.add(oItem);


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    //LIKE 내림차순 정렬
                    Collections.sort(oData, new Comparator<ItemData>() {
                        @Override
                        public int compare(ItemData d1, ItemData d2) {
                            if (d1.getPingLike() > d2.getPingLike()) {
                                return -1;
                            } else if (d1.getPingLike() < d2.getPingLike()) {
                                return 1;
                            }
                            return 0;
                        }
                    });

                    for (int i = start + 1; i < end; i++) {
                        load = oData.subList(start, end);
                    }
                    //////////////////////////////////////////////////
                    if (feedIdxs.size() > 0)
                        oAdapter.setShowMap(false);
                    oAdapter.addItemMore(load);
                    oAdapter.setMoreLoading(false);
                }
            }, 1000);
        }
    }

    @Override
    public void onRefresh() {
        if(!isDistance && !isLike) {
            navigation.setSelectedItemId(R.id.navigation_feed);
            mSwipeRefreshLayout.setRefreshing(false);
        }else if(isDistance){
            setDistanceData();
            mSwipeRefreshLayout.setRefreshing(false);
        }else if(isLike){
            setLikeData();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    private void setLikeData() {
        oData.clear();
        isLike = true;
        isDistance = false;
        for (int i = 0; i < linearNum; i++) {
            try {
                ItemData oItem = new ItemData();
                int like = 0;
                int reply = 0;

                oItem.strIdx = idx[linearNum - (i + 1)];
                oItem.strLike = "좋아요";
                oItem.strShare = "공유";
                oItem.strUserId = writer_id[linearNum - (i + 1)];
                oItem.strReply = "댓글";
                oItem.strContent = content[linearNum - (i + 1)];
                oItem.strTitile = title[linearNum - (i + 1)];

                oItem.strThumbnailImage = imgUrl + thumbnail[linearNum - (i + 1)];
                oItem.douLatitude = Double.parseDouble(locationLat[linearNum - (i + 1)]);
                oItem.douLongitude = Double.parseDouble(locationLong[linearNum - (i + 1)]);
                oItem.strVideo = imgUrl + banner[linearNum - (i + 1)];
                oItem.pingDistance = Math.sqrt(Math.pow((latitude - oItem.douLatitude),2) + Math.pow((longitude - oItem.douLongitude),2));
                oItem.strDistance = String.valueOf(oItem.pingDistance);

                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                Date d2 = f.parse(create_date[linearNum - (i + 1)]);
                long diff = date.getTime() - d2.getTime();
                long sec = diff / 1000;
                if (sec < 60) {
                    dateDiff = Long.toString(sec) + "초 전";
                } else if (sec < 3600 && sec >= 60) {
                    dateDiff = Long.toString(sec / 60) + "분 전";
                } else if (sec < 86400 && sec >= 3600) {
                    dateDiff = Long.toString(sec / 3600) + "시간 전";
                } else if (sec < 604800 && sec >= 86400) {
                    dateDiff = Long.toString(sec / 86400) + "일 전";
                } else if (sec < 2419200 && sec >= 604800) {
                    dateDiff = Long.toString(sec / 604800) + "주 전";
                } else if (sec < 31536000 && sec >= 2419200) {
                    dateDiff = Long.toString(sec / 2419200) + "달 전";
                } else {
                    dateDiff = "오래 전";
                }
                oItem.strTime = dateDiff;

                if (!oItem.strUserId.equals("0")) {
                    for (int j = 0; j < userLinearNum; j++) {
                        String val = link_id[j];
                        if (val.contains(oItem.strUserId)) {
                            oItem.strUserImage = imgUrl + profile[j];
                            oItem.strUserName = nickname[j];
                        }
                    }
                } else {
                    oItem.strUserImage = imgUrl + "null.jpg";
                    oItem.strUserName = "익명";
                }
                for (int k = 0; k < likeLinearNum; k++) {
                    if (likeCnt[k].equals(idx[linearNum - (i + 1)])) {
                        like++;
                    }
                }

                for (int l = 0; l < replyLinearNum; l++) {
                    if (replyCnt[l].equals(idx[linearNum - (i + 1)])) {
                        reply++;
                    }
                }
                String strLike;
                String strReply;

                strLike = String.valueOf(like);
                strReply = String.valueOf(reply);

                oItem.pingReply = reply;
                oItem.pingLike = like;

                oData.add(oItem);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //LIKE 내림차순 정렬
        Collections.sort(oData, new Comparator<ItemData>() {
            @Override
            public int compare(ItemData d1, ItemData d2) {
                if (d1.getPingLike() > d2.getPingLike()) {
                    return -1;
                } else if (d1.getPingLike() < d2.getPingLike()) {
                    return 1;
                }
                return 0;
            }
        });
        load = oData.subList(0,5);

        oAdapter.addAll(load);
    }

    private void setDistanceData() {
        oData.clear();
        isLike = false;
        isDistance = true;

        for (int i = 0; i < linearNum; i++) {
            try {
                ItemData oItem = new ItemData();
                int like = 0;
                int reply = 0;

                oItem.douLatitude = Double.parseDouble(locationLat[linearNum - (i + 1)]);
                oItem.douLongitude = Double.parseDouble(locationLong[linearNum - (i + 1)]);
                oItem.pingDistance = Math.sqrt(Math.pow((latitude - oItem.douLatitude),2) + Math.pow((longitude - oItem.douLongitude),2));
                oItem.strDistance = String.valueOf(oItem.pingDistance);

                oItem.strIdx = idx[linearNum - (i + 1)];
                oItem.strLike = "좋아요";
                oItem.strShare = "공유";
                oItem.strUserId = writer_id[linearNum - (i + 1)];
                oItem.strReply = "댓글";
                oItem.strContent = content[linearNum - (i + 1)];
                oItem.strTitile = title[linearNum - (i + 1)];

                oItem.strThumbnailImage = imgUrl + thumbnail[linearNum - (i + 1)];

                oItem.strVideo = imgUrl + banner[linearNum - (i + 1)];

                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                Date d2 = f.parse(create_date[linearNum - (i + 1)]);
                long diff = date.getTime() - d2.getTime();
                long sec = diff / 1000;
                if (sec < 60) {
                    dateDiff = Long.toString(sec) + "초 전";
                } else if (sec < 3600 && sec >= 60) {
                    dateDiff = Long.toString(sec / 60) + "분 전";
                } else if (sec < 86400 && sec >= 3600) {
                    dateDiff = Long.toString(sec / 3600) + "시간 전";
                } else if (sec < 604800 && sec >= 86400) {
                    dateDiff = Long.toString(sec / 86400) + "일 전";
                } else if (sec < 2419200 && sec >= 604800) {
                    dateDiff = Long.toString(sec / 604800) + "주 전";
                } else if (sec < 31536000 && sec >= 2419200) {
                    dateDiff = Long.toString(sec / 2419200) + "달 전";
                } else {
                    dateDiff = "오래 전";
                }
                oItem.strTime = dateDiff;

                if (!oItem.strUserId.equals("0")) {
                    for (int j = 0; j < userLinearNum; j++) {
                        String val = link_id[j];
                        if (val.contains(oItem.strUserId)) {
                            oItem.strUserImage = imgUrl + profile[j];
                            oItem.strUserName = nickname[j];
                        }
                    }
                } else {
                    oItem.strUserImage = imgUrl + "null.jpg";
                    oItem.strUserName = "익명";
                }
                for (int k = 0; k < likeLinearNum; k++) {
                    if (likeCnt[k].equals(idx[linearNum - (i + 1)])) {
                        like++;
                    }
                }

                for (int l = 0; l < replyLinearNum; l++) {
                    if (replyCnt[l].equals(idx[linearNum - (i + 1)])) {
                        reply++;
                    }
                }

                oItem.pingReply = reply;
                oItem.pingLike = like;

                oData.add(oItem);


            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


        //DISTANCE 오름차순 정렬
        Collections.sort(oData, new Comparator<ItemData>() {
            @Override
            public int compare(ItemData d1, ItemData d2) {
                if (d1.getPingDistance() < d2.getPingDistance()) {
                    return -1;
                } else if (d1.getPingDistance() > d2.getPingDistance()) {
                    return 1;
                }
                return 0;
            }
        });

        load = oData.subList(0,4);

        oAdapter.addAll(load);
    }



}