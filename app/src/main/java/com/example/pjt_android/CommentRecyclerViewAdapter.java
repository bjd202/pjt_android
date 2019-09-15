package com.example.pjt_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pjt_android.com.tje.model.Comment;

import java.util.ArrayList;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {

    ArrayList<Comment> commentList;

    public CommentRecyclerViewAdapter(ArrayList<Comment> commentList) {
        if(commentList==null){
            this.commentList=new ArrayList<>();
        }else {
            this.commentList = commentList;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
         TextView comment_nickname;
         TextView comment_write_date;
         TextView comment_content;

         public ViewHolder(@NonNull final View itemView){
             super(itemView);

             comment_nickname=itemView.findViewById(R.id.comment_nickname);
             comment_write_date=itemView.findViewById(R.id.comment_write_date);
             comment_content=itemView.findViewById(R.id.comment_content);
         }
     }

    @NonNull
    @Override
    public CommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment=this.commentList.get(position);

        holder.comment_nickname.setText(comment.getNickname());
        holder.comment_write_date.setText(comment.getWrite_dateString());
        holder.comment_content.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return this.commentList.size();
    }

    public void add_comment(Comment comment){
        this.commentList.add(comment);
        notifyDataSetChanged();
    }
}
