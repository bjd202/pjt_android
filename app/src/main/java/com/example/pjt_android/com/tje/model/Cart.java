package com.example.pjt_android.com.tje.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Cart {
    private int cart_id;
    private int board_id;
    private String member_id;
    private String image;
    private String title;
    private int category;
    private String price;
    private Date add_time;
    private boolean isSelected;
    private int number;

    public Cart() {
    }

    public Cart(int cart_id, int board_id, String member_id, String image, String title, int category, String price,
                Date add_time) {
        this.cart_id = cart_id;
        this.board_id = board_id;
        this.member_id = member_id;
        this.image = image;
        this.title = title;
        this.category = category;
        this.price = price;
        this.add_time = add_time;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getBoard_id() {
        return board_id;
    }

    public void setBoard_id(int board_id) {
        this.board_id = board_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategory() {
        return category;
    }

    public String getCategoryString() {
        if(this.category==1)
            return "운동기구";
        else if(this.category==2)
            return "보충제";
        else
            return "기타";
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getAdd_time() {
        return add_time;
    }

    public String getAdd_timeString(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(add_time);
    }

    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
