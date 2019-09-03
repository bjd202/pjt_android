package com.example.pjt_android;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private ArrayList<SimpleBoardItemView> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView simpleItem_image;
        TextView simpleItem_title;
        TextView simpleItem_nickname;
        TextView simpleItem_like_cnt;
        TextView simpleItem_dislike_cnt;
        TextView simpleItem_view_cnt;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            simpleItem_image=itemView.findViewById(R.id.simpleItem_image);
            simpleItem_title=itemView.findViewById(R.id.simpleItem_title);
            simpleItem_nickname=itemView.findViewById(R.id.simpleItem_nickname);
            simpleItem_like_cnt=itemView.findViewById(R.id.simpleItem_like_cnt);
            simpleItem_dislike_cnt=itemView.findViewById(R.id.simpleItem_dislike_cnt);
            simpleItem_view_cnt=itemView.findViewById(R.id.simpleItem_view_cnt);
        }
    }

    public SimpleItemRecyclerViewAdapter(ArrayList<SimpleBoardItemView> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleBoardItemView item=this.itemList.get(position);

        holder.simpleItem_image.setImageResource(R.drawable.kakaotalk_icon);
        holder.simpleItem_title.setText(item.getTitle());
        holder.simpleItem_nickname.setText(item.getNickname());
        holder.simpleItem_like_cnt.setText("좋아요 : "+item.getLike_cnt());
        holder.simpleItem_dislike_cnt.setText("싫어요 : "+item.getDislike_cnt());
        holder.simpleItem_view_cnt.setText("조회수 : "+item.getView_cnt());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
