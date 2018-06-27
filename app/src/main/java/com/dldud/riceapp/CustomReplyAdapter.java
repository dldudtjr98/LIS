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

import static com.dldud.riceapp.UserProfileSettingActivity.userId;

public class CustomReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private ArrayList<ReplyItemData> items = new ArrayList<>();

    public CustomReplyAdapter(Context context, ArrayList<ReplyItemData> items){
        this.context = context;
        this.items = items;
    }

    public void add(ReplyItemData data){
        items.add(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0 :
                View convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reply_card, parent, false);
                return new CustomReplyAdapter.otherViewHolder(convertView);
            case 2:
                View myconvertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_reply_card, parent, false);
                return new CustomReplyAdapter.myViewHolder(myconvertView);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position){
        final ReplyItemData item = items.get(position);
        if(item.getStrReplyUserId().equals(userId)) {
            return 2;
        }else{
            return 0;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0 :
                otherViewHolder otherHolder = (otherViewHolder)holder;
                final ReplyItemData item = items.get(position);
                otherHolder.oTextName.setText(item.getStrReplyUserName());
                //holder.oTextTime.setText(item.getStrReplyTime());
                otherHolder.oTextContent.setText(item.getStrReplyContent());

                Glide.with(context)
                        .load(item.getStrReplyUserImage())
                        .apply(new RequestOptions().circleCropTransform())
                        .into(otherHolder.oUserImage);
                break;
            case 2:
                myViewHolder myHolder = (myViewHolder)holder;
                final ReplyItemData myItem = items.get(position);
                myHolder.myTextContent.setText(myItem.getStrReplyContent());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class otherViewHolder extends RecyclerView.ViewHolder{

        private TextView oTextName;
        private TextView oTextTime;
        private TextView oTextContent;
        private ImageView oUserImage;


        otherViewHolder(View v) {
            super(v);

            oTextName = (TextView)v.findViewById(R.id.replyName);
            //oTextTime = (TextView)v.findViewById(R.id.replyTime);
            oTextContent = (TextView)v.findViewById(R.id.replyContent);
            oUserImage = (ImageView)v.findViewById(R.id.replyUserImage);
        }
    }

    static class myViewHolder extends RecyclerView.ViewHolder{
        private TextView myTextContent;

        myViewHolder(View v){
            super(v);

            myTextContent = (TextView)v.findViewById(R.id.myReplyContent);
        }
    }
}
