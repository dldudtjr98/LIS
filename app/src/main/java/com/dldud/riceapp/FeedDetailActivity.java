package com.dldud.riceapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.concurrent.ExecutionException;

public class FeedDetailActivity extends AppCompatActivity {

    String myString, userString;
    SwipeViewPager viewPager;
    int linearNum, userLinearNum;
    String page, data, feedTitle,feedThumbnail,feedWriter_id,feedBanner,feedContent,feedLocationLat,feedLocationLong,feedCreate_date, feedWriterNick, feedWriterProfile;
    String[] idx, title, thumbnail, writer_id, banner, content,locationLat,locationLong, create_date, link_id, nickname, profile, userIdx;
    private String imgUrl = "http://52.78.18.156/data/riceapp/";

    int MAX_PAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(FeedDetailActivity.this)
                .setSmallIcon(R.drawable.ping_on)
                .setContentTitle("테스트")
                .setContentText("테스트내용");

        //Feed Idx
        Intent intent = getIntent();
        data = intent.getStringExtra("position");
        page = intent.getStringExtra("page");

        try {
            Task task = new Task();
            TaskUser taskUser = new TaskUser();
            myString = task.execute("http://52.78.18.156/public/ping_db.php").get();
            userString = taskUser.execute("http://52.78.18.156/public/user_db.php").get();

            task.jsonParser(myString);
            taskUser.jsonParser(userString);

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

            linearNum = idx.length;
            userLinearNum = userIdx.length;

            for(int i = 0 ; i < linearNum ; i++){
                if(data.equals(idx[i])){
                    feedWriter_id = writer_id[i];
                    feedTitle = title[i];
                    feedBanner = banner[i];
                    feedThumbnail = thumbnail[i];
                    feedContent = content[i];
                    feedLocationLat = locationLat[i];
                    feedLocationLong = locationLong[i];
                    feedCreate_date = create_date[i];

                    break;
                }
            }

            for(int i = 0 ; i< userLinearNum ; i++){
                if(feedWriter_id.equals(link_id[i])){
                    feedWriterNick = nickname[i];
                    feedWriterProfile = profile[i];

                    break;
                }
            }

        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }

        ImageView profileImage = (ImageView)findViewById(R.id.userImage);
        ImageView contentImage = (ImageView)findViewById(R.id.detailBannerImage);
        TextView content = (TextView)findViewById(R.id.detailContent);
        TextView userid = (TextView)findViewById(R.id.userIdText);
        TextView time = (TextView)findViewById(R.id.detailTime);

        viewPager = (SwipeViewPager) findViewById(R.id.detailViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.detailTabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.like));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.reply));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ping));
        //tabLayout.setupWithViewPager(viewPager);

        adapter detailPagerAdapter = new adapter(getSupportFragmentManager());
        viewPager.setAdapter(detailPagerAdapter);

        //Set ViewPager Start Position

        if(page.equals("2")){
            viewPager.setCurrentItem(1);
            tabLayout.getTabAt(1).select(); // 탭 또한 처음에 선택되게
        }else if(page.equals("3")){
            viewPager.setCurrentItem(2);
            tabLayout.getTabAt(2).select();
        }
        else {
            viewPager.setCurrentItem(0);
            tabLayout.getTabAt(0).select();
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        content.setText(feedContent);
        userid.setText(feedWriterNick);

        Glide.with(this)
                .load(imgUrl + feedThumbnail)
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().placeholder(R.drawable.loading_img))
                .into(contentImage);

        Glide.with(this)
                .load(imgUrl + feedWriterProfile)
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().circleCropTransform())
                .apply(new RequestOptions().placeholder(R.drawable.loading_img))
                .into(profileImage);

    }


    private class adapter extends FragmentStatePagerAdapter {
        public adapter (FragmentManager fm){
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    Intent intent0 = new Intent(getBaseContext(),PageLikeFragment.class);
                    intent0.putExtra("position",data);
                    viewPager.setPagingEnabled(false);
                    return new PageLikeFragment();
                case 1:
                    Intent intent1 = new Intent(getBaseContext(),PageReplyFragment.class);
                    intent1.putExtra("position",data);
                    viewPager.setPagingEnabled(false);
                    return new PageReplyFragment();
                case 2:
                    Intent intent2 = new Intent(getBaseContext(),PageMapFragment.class);
                    intent2.putExtra("position",data);
                    viewPager.setPagingEnabled(false);
                    return new PageMapFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }
}
