package com.dldud.riceapp;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.maps.helper.Utility.getPackageInfo;


public class MainActivity extends AppCompatActivity {
    
    static BottomNavigationViewEx navigation;
    private static String TAG;
    private int prevId = -1;

    public BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(item.getItemId() != prevId || prevId == R.id.navigation_feed) {
                prevId = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.navigation_camera:
                        transaction.replace(R.id.ViewPage, new CameraFragment()).commit();
                        navigation.setVisibility(View.GONE);
                        return true;
                    case R.id.navigation_feed:
                        transaction.replace(R.id.ViewPage, new FeedFragment()).commit();
                        navigation.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_map:
                        transaction.replace(R.id.ViewPage, new MapFragment()).commit();
                        navigation.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_myProfile:
                        MyProfileFragment profile = new MyProfileFragment();
                        if(navigation.getTag() != null) {
                            String target = (navigation.getTag().toString());
                            if (!target.equals(null))
                                profile.setTargetUser(Integer.parseInt(target));
                            else
                                profile.setTargetUser(0);
                        }
                        transaction.replace(R.id.ViewPage, profile).commit();
                        navigation.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_setting:
                        transaction.replace(R.id.ViewPage, new SettingFragment()).commit();
                        navigation.setVisibility(View.VISIBLE);
                        return true;
                }
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.enableShiftingMode(false);
        navigation.setTextVisibility(false);

        if(getIntent().getIntExtra("fragmentNumber",0)==1){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigation.setSelectedItemId(R.id.navigation_map);
                }
            },500);
        } else{
            navigation.setSelectedItemId(R.id.navigation_camera);
        }
    }
}
