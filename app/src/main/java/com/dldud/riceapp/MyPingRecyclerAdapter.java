package com.dldud.riceapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class MyPingRecyclerAdapter extends RecyclerView.Adapter<MyPingRecyclerAdapter.ViewHolder> {

    Context context;
    private ArrayList<MyPingItemData> items = new ArrayList<>();

    public MyPingRecyclerAdapter(Context context, ArrayList<MyPingItemData> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_ping_img,parent,false);
        return new MyPingRecyclerAdapter.ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final MyPingRecyclerAdapter.ViewHolder holder, int position) {
        final MyPingItemData item = items.get(position);

        Glide.with(context)
                .load(item.getUserImage())
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,FeedDetailActivity.class);
                intent.putExtra("position",item.getImgeIdx());
                intent.putExtra("page","2");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.imgView);
        }
    }

}
