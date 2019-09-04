package com.example.pjt_android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private ArrayList<SimpleBoardItemView> itemList;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    private OnItemClickListener listener = null ;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView simpleItem_image;
        TextView simpleItem_title;
        TextView simpleItem_nickname;
        TextView simpleItem_date;
        TextView simpleItem_category;
        TextView simpleItem_like_cnt;
        TextView simpleItem_dislike_cnt;
        TextView simpleItem_view_cnt;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            simpleItem_image=itemView.findViewById(R.id.simpleItem_image);
            simpleItem_title=itemView.findViewById(R.id.simpleItem_title);
            simpleItem_nickname=itemView.findViewById(R.id.simpleItem_nickname);
            simpleItem_date=itemView.findViewById(R.id.simpleItem_date);
            simpleItem_category=itemView.findViewById(R.id.simpleItem_category);
            simpleItem_like_cnt=itemView.findViewById(R.id.simpleItem_like_cnt);
            simpleItem_dislike_cnt=itemView.findViewById(R.id.simpleItem_dislike_cnt);
            simpleItem_view_cnt=itemView.findViewById(R.id.simpleItem_view_cnt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        SimpleBoardItemView item=itemList.get(pos);

                        Intent intent=new Intent(view.getContext(), DetailBoardItemViewActivity.class);
                        intent.putExtra("board_id", item.getBoard_id());

                        view.getContext().startActivity(intent);

//                        if(listener!=null){
//                            listener.onItemClick(view, pos);
//                        }
                    }
                }
            });
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

        Context context=holder.itemView.getContext();
        //String imgUrl="http://192.168.0.25:8080/webapp/resources/images/"+item.getImage();

        String imgUrl=context.getString(R.string.HOST_NETWORK_PROTOCOL)+
                context.getString(R.string.HOST_ADDRESS)+
                context.getString(R.string.HOST_IMG_URL)+
                item.getImage();


        Glide.with(holder.itemView.getContext()).load(imgUrl).into(holder.simpleItem_image);

        //holder.simpleItem_image.setImageResource(R.drawable.kakaotalk_icon);
        holder.simpleItem_title.setText(item.getTitle());
        holder.simpleItem_nickname.setText(item.getNickname());
        holder.simpleItem_date.setText(item.getWrite_dateString());
        holder.simpleItem_category.setText(item.getCategoryString());
        holder.simpleItem_like_cnt.setText("좋아요 : "+item.getLike_cnt());
        holder.simpleItem_dislike_cnt.setText("싫어요 : "+item.getDislike_cnt());
        holder.simpleItem_view_cnt.setText("조회수 : "+item.getView_cnt());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
