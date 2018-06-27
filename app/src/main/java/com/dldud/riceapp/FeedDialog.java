package com.dldud.riceapp;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;

public class FeedDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_feed_dialog2);

        ArrayList<Integer> arr = getIntent().getIntegerArrayListExtra("indexs");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        FeedFragment fg = new FeedFragment();
        fg.setIdxs(arr);

        transaction.replace(R.id.feedDialogView, fg).commit();
    }
}
