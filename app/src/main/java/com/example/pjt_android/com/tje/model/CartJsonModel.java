package com.example.pjt_android.com.tje.model;

public class CartJsonModel {
    private int board_id;
    private int category;
    private String member_id;
    private String name;
    private String address_post;
    private String address_basic;
    private String address_detail;
    private String title;
    private int number;
    private String price;
    private int cart_id;
    private int index;

    public CartJsonModel() {
        // TODO Auto-generated constructor stub
    }

    public CartJsonModel(int board_id, int category, String member_id, String name, String address_post,
                         String address_basic, String address_detail, String title, int number, String price, int cart_id) {
        super();
        this.board_id = board_id;
        this.category = category;
        this.member_id = member_id;
        this.name = name;
        this.address_post = address_post;
        this.address_basic = address_basic;
        this.address_detail = address_detail;
        this.title = title;
        this.number = number;
        this.price = price;
        this.cart_id = cart_id;
    }

    public CartJsonModel(int board_id, int category, String member_id, String name, String address_post,
                         String address_basic, String address_detail, String title, int number, String price, int cart_id,
                         int index) {
        super();
        this.board_id = board_id;
        this.category = category;
        this.member_id = member_id;
        this.name = name;
        this.address_post = address_post;
        this.address_basic = address_basic;
        this.address_detail = address_detail;
        this.title = title;
        this.number = number;
        this.price = price;
        this.cart_id = cart_id;
        this.index = index;
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

    public void setCategory(int category) {
        this.category = category;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress_post() {
        return address_post;
    }

    public void setAddress_post(String address_post) {
        this.address_post = address_post;
    }

    public String getAddress_basic() {
        return address_basic;
    }

    public void setAddress_basic(String address_basic) {
        this.address_basic = address_basic;
    }

    public String getAddress_detail() {
        return address_detail;
    }

    public void setAddress_detail(String address_detail) {
        this.address_detail = address_detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
