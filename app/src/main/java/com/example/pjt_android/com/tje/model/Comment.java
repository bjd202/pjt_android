package com.example.pjt_android.com.tje.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    public int comment_id;
    public int board_id;
    public int topic;
    public String member_id;
    public String nickname;
    public String content;
    public Date write_date;

    public Comment() {}

    public Comment(int comment_id, int board_id, int topic, String member_id, String nickname, String content,
                   Date write_date) {
        this.comment_id = comment_id;
        this.board_id = board_id;
        this.topic = topic;
        this.member_id = member_id;
        this.nickname = nickname;
        this.content = content;
        this.write_date = write_date;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getBoard_id() {
        return board_id;
    }

    public void setBoard_id(int board_id) {
        this.board_id = board_id;
    }

    public int getTopic() {
        return topic;
    }

    public void setTopic(int topic) {
        this.topic = topic;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getWrite_date() {
        return write_date;
    }

    public String getWrite_dateString(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(write_date);
    }

    public void setWrite_date(Date write_date) {
        this.write_date = write_date;
    }
}
