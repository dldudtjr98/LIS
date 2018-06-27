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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dldud on 2018-05-10.
 */

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    Context context;
    private ArrayList<ReplyItemData> items = new ArrayList<>();

    public ReplyAdapter(Context context, ArrayList<ReplyItemData> items){
        this.context = context;
        this.items = items;
    }

    public void add(ReplyItemData data){
        items.add(data);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reply_card, parent, false);
            return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ReplyItemData item = items.get(position);

        holder.oTextName.setText(item.getStrReplyUserName());
//        holder.oTextTime.setText(item.getStrReplyTime());
        holder.oTextContent.setText(item.getStrReplyContent());

        Glide.with(context)
                .load(item.getStrReplyUserImage())
                .apply(new RequestOptions().circleCropTransform())
                .into(holder.oUserImage);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView oTextName;
        private TextView oTextTime;
        private TextView oTextContent;
        private ImageView oUserImage;


        ViewHolder(View v) {
            super(v);

            oTextName = (TextView)v.findViewById(R.id.replyName);
            //oTextTime = (TextView)v.findViewById(R.id.replyTime);
            oTextContent = (TextView)v.findViewById(R.id.replyContent);
            oUserImage = (ImageView)v.findViewById(R.id.replyUserImage);
        }
    }
}
