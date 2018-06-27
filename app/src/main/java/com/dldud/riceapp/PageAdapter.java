package com.dldud.riceapp;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by dldud on 2018-03-24.
 * 소셜페이지 pageAdapter 입니다.
 */

public class PageAdapter extends FragmentStatePagerAdapter{

    private int tabCount;
    public GridView gridView;
    private Context mContext;
    public ArrayList<Integer> myLike = new ArrayList<>();
    public ArrayList<Integer> myPing = new ArrayList<>();


    public PageAdapter(FragmentManager fm, int tabCount, Context c){
        super(fm);
        this.tabCount = tabCount;
        mContext = c;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch(position){
            case 0:
                //MyViewFragment myViewFragment = new MyViewFragment();
                FeedFragment ff = new FeedFragment();
                ff.setIdxs(myLike);

                return ff;
            case 1:
                FeedFragment ff2 = new FeedFragment();
                ff2.setIdxs(myPing);

                return ff2;
            case 2:
                PingFragment pingFragment = new PingFragment();
                return pingFragment;
            case 3:
                PingConnectFragment pingConnectFragment = new PingConnectFragment();
                return pingConnectFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (!(object instanceof  FeedFragment)) {
            return POSITION_NONE;
        } else {
            return super.getItemPosition(object);
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
