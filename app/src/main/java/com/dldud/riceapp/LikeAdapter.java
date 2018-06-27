package com.dldud.riceapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    Context context;
    private ArrayList<LikeItemData> items = new ArrayList<>();

    public LikeAdapter(Context context, ArrayList<LikeItemData> items){
        this.context = context;
        this.items = items;
    }

    public void add(LikeItemData data){
        items.add(data);
        notifyDataSetChanged();
    }


    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.like_card,parent,false);
        return new LikeAdapter.ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final LikeAdapter.ViewHolder holder, int position) {
        final LikeItemData item = items.get(position);

        holder.oTextName.setText(item.getStrLikeUserName());

        Glide.with(context)
                .load(item.getStrLikeUserImage())
                .apply(new RequestOptions().circleCropTransform())
                .into(holder.oUserImage);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView oTextName;
        private ImageView oUserImage;


        ViewHolder(View v) {
            super(v);

            oTextName = (TextView)v.findViewById(R.id.likeUser);
            oUserImage = (ImageView)v.findViewById(R.id.likeProfile);
        }
    }
}
