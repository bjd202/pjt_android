package com.example.pjt_android.com.tje.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleBoardReviewView {
    private int board_id;
    private int category;
    private String title;
    private int comment_cnt;
    private String image;
    private String content;
    private String member_id;
    private String nickname;
    private int view_cnt;
    private int like_cnt;
    private int dislike_cnt;
    private Date write_date;

    public SimpleBoardReviewView() {
    }

    public SimpleBoardReviewView(int board_id, int category, String title, int comment_cnt, String image,
                                 String content, String member_id, String nickname, int view_cnt, int like_cnt, int dislike_cnt,
                                 Date write_date) {
        this.board_id = board_id;
        this.category = category;
        this.title = title;
        this.comment_cnt = comment_cnt;
        this.image = image;
        this.content = content;
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

    public int getCategory() {
        return category;
    }

    public String getCategoryString(){
        if( category == 1 ) {
            return "전체";
        } else if ( category == 2 ) {
            return "상품";
        } else if ( category == 3 ) {
            return "피트니스";
        } else if ( category == 4 ) {
            return "장소";
        } else if ( category == 5) {
            return "다이어트";
        } else if ( category == 6 ) {
            return "웨이트 트레이닝";
        } else if ( category == 7 ) {
            return "레시피";
        } else {
            return "카테고리 설정 에러";
        }
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public void setWrite_date(Date write_date) {
        this.write_date = write_date;
    }

    public String getWrite_dateString(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(write_date);
    }

    public String getAbstractContent() {
        int size = this.content.length();
        if( size > 90 )
            size = 90;

        return this.content.substring(0,size);

    }
}
