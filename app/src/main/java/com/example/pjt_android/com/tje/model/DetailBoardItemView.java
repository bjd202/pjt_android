package com.example.pjt_android.com.tje.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailBoardItemView implements Serializable {
    private int board_id;
    private int topic;
    private int category;
    private String title;
    private String content;
    private String price;
    private int number;
    private String image;
    private int comment_cnt;
    private String member_id;
    private String nickname;
    private int view_cnt;
    private int like_cnt;
    private int dislike_cnt;
    private Date write_date;

    public DetailBoardItemView() {
    }

    public DetailBoardItemView(int board_id, int topic, int category, String title, String content, String price,
                               int number, String image, int comment_cnt, String member_id, String nickname, int view_cnt, int like_cnt,
                               int dislike_cnt, Date write_date) {
        super();
        this.board_id = board_id;
        this.topic = topic;
        this.category = category;
        this.title = title;
        this.content = content;
        this.price = price;
        this.number = number;
        this.image = image;
        this.comment_cnt = comment_cnt;
        this.member_id = member_id;
        this.nickname = nickname;
        this.view_cnt = view_cnt;
        this.like_cnt = like_cnt;
        this.dislike_cnt = dislike_cnt;
        this.write_date = write_date;
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

    public int getCategory() {
        return category;
    }

    public String getCategoryString() {
        if (this.category == 1)
            return "운동기구";
        else if (this.category == 2)
            return "보충제";
        else
            return "기타";
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getRestrictedTitle() {
        int length = this.title.length();

        if (length >= 15) {
            StringBuilder sb = new StringBuilder(this.title);
            String s = sb.substring(0, 15);
            s += "...";
            return s;
        }

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getRestrictedContent() {
        int length = this.content.length();

        if (length >= 50) {
            StringBuilder sb = new StringBuilder(this.content);
            String s = sb.substring(0, 50);
            s += "...";
            return s;
        }

        return content;
    }

    public String getRestrictedContent2() {
        int length = this.content.length();

        if (length >= 15) {
            StringBuilder sb = new StringBuilder(this.content);
            String s = sb.substring(0, 15);
            s += "...";
            return s;
        }

        return content;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
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

    public int getView_cnt() {
        return view_cnt;
    }

    public void setView_cnt(int view_cnt) {
        this.view_cnt = view_cnt;
    }

    public int getLike_cnt() {
        return like_cnt;
    }

    public void setLike_cnt(int like_cnt) {
        this.like_cnt = like_cnt;
    }

    public int getDislike_cnt() {
        return dislike_cnt;
    }

    public void setDislike_cnt(int dislike_cnt) {
        this.dislike_cnt = dislike_cnt;
    }

    public Date getWrite_date() {
        return write_date;
    }

    public String getWrite_dateString(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(write_date);
    }

    public void setWrite_date(Date write_date) {
        this.write_date = write_date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(!( obj instanceof DetailBoardItemView ))
            return false;

        DetailBoardItemView item=(DetailBoardItemView)obj;

        if(item.board_id==board_id)
            return true;
        else
            return false;
    }
}
