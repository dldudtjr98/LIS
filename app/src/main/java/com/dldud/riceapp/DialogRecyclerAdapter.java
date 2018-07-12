package com.dldud.riceapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder> {

    Context context;
    private ArrayList<DialogItemData> items = new ArrayList<>();

    public DialogRecyclerAdapter(Context context, ArrayList<DialogItemData> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public DialogRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_img,parent,false);
        return new DialogRecyclerAdapter.ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final DialogRecyclerAdapter.ViewHolder holder, int position) {
        final DialogItemData item = items.get(position);

        Glide.with(context)
                .load(item.getStrThumbnailImage())
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DialogReplyActivity.class);
                intent.putExtra("position",item.getStrIdx());
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
            imageView = (ImageView)itemView.findViewById(R.id.dialogImgView);
        }
    }
}
