package com.dldud.riceapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class ProfileGridAdapter extends BaseAdapter {

    int CustomGalleryItemBg;
    String mBasePath;
    Context mContext;
    String[] mImgs;
    Bitmap bm;
    DataSetObservable mDataSetObservable = new DataSetObservable();

    public ProfileGridAdapter(Context context, String basepath){
        this.mContext = context;
        this.mBasePath = basepath;

        File file = new File(mBasePath);
        if(!file.exists()){
            if(!file.mkdirs()){
                Log.d("grid", "failed to create directory");
            }
        }
        mImgs = file.list();

        /*
        TypedArray array = mContext.obtainStyledAttributes(R.styleable.GalleryTheme);
        CustomGalleryItemBg = array.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
        array.recycle();*/
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        /*
        bm = BitmapFactory.decodeFile(mBasePath + File.separator + mImgList[position]);
        Bitmap mThumbnail = ThumbnailUtils.extractThumbnail(bm, 300, 300);
        imageView.setPadding(8, 8, 8, 8);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        imageView.setImageBitmap(mThumbnail);*/
        return imageView;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer){ // DataSetObserver의 등록(연결)
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer){ // DataSetObserver의 해제
        mDataSetObservable.unregisterObserver(observer);
    }

    @Override
    public void notifyDataSetChanged(){ // 위에서 연결된 DataSetObserver를 통한 변경 확인
        mDataSetObservable.notifyChanged();
    }

}
