package com.example.pjt_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pjt_android.com.tje.model.SimpleBoardItemView;
import com.example.pjt_android.com.tje.model.SimpleBoardReviewView;

import java.util.ArrayList;

public class SimpleReviewRecyclerViewAdapter extends RecyclerView.Adapter<SimpleReviewRecyclerViewAdapter.ViewHolder> {

    private ArrayList<SimpleBoardReviewView> itemList;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    private OnItemClickListener listener = null ;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView simpleReview_image;
        TextView simpleReview_title;
        TextView simpleReview_nickname;
        TextView simpleReview_date;
        TextView simpleReview_category;
        TextView simpleReview_like_cnt;
        TextView simpleReview_dislike_cnt;
        TextView simpleReview_view_cnt;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            simpleReview_image=itemView.findViewById(R.id.simpleReview_image);
            simpleReview_title=itemView.findViewById(R.id.simpleReview_title);
            simpleReview_nickname=itemView.findViewById(R.id.simpleReview_nickname);
            simpleReview_date=itemView.findViewById(R.id.simpleReview_date);
            simpleReview_category=itemView.findViewById(R.id.simpleReview_category);
            simpleReview_like_cnt=itemView.findViewById(R.id.simpleReview_like_cnt);
            simpleReview_dislike_cnt=itemView.findViewById(R.id.simpleReview_dislike_cnt);
            simpleReview_view_cnt=itemView.findViewById(R.id.simpleReview_view_cnt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        SimpleBoardReviewView item=itemList.get(pos);

                        item.setView_cnt(item.getView_cnt()+1);

                        notifyItemChanged(pos);

                        Intent intent=new Intent(view.getContext(), DetailReviewActivity.class);
                        intent.putExtra("board_id", item.getBoard_id());

                        view.getContext().startActivity(intent);
                        ((Activity)view.getContext()).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
//                        if(listener!=null){
//                            listener.onItemClick(view, pos);
//                        }
                    }
                }
            });
        }
    }

    public SimpleReviewRecyclerViewAdapter(ArrayList<SimpleBoardReviewView> itemList) {
        if(itemList==null){
            this.itemList=new ArrayList<>();
        }else{
            this.itemList = itemList;
        }
    }

    @NonNull
    @Override
    public SimpleReviewRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SimpleBoardReviewView item=this.itemList.get(position);

        Context context=holder.itemView.getContext();
        //String imgUrl="http://192.168.0.25:8080/webapp/resources/images/"+item.getImage();

        String imgUrl=context.getString(R.string.HOST_NETWORK_PROTOCOL)+
                context.getString(R.string.HOST_ADDRESS)+
                context.getString(R.string.HOST_IMG_URL)+
                item.getImage();


        Glide.with(holder.itemView.getContext()).load(imgUrl).into(holder.simpleReview_image);

        //holder.simpleReview_image.setImageResource(R.drawable.kakaotalk_icon);
        holder.simpleReview_title.setText(item.getTitle());
        holder.simpleReview_nickname.setText(item.getNickname());
        holder.simpleReview_date.setText(item.getWrite_dateString());
        holder.simpleReview_category.setText(item.getCategoryString());
        holder.simpleReview_like_cnt.setText(""+item.getLike_cnt());
        holder.simpleReview_dislike_cnt.setText(""+item.getDislike_cnt());
        holder.simpleReview_view_cnt.setText("조회수 : "+item.getView_cnt());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}
