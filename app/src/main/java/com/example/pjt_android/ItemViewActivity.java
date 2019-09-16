package com.example.pjt_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.pjt_android.com.tje.model.SimpleBoardItemView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ItemViewActivity extends AppCompatActivity {

    public static final int ADD_REQUEST_CODE=500;
    public static final int DETAIL_REQUEST_CODE=400;

    private ArrayList<SimpleBoardItemView> itemList;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SimpleItemRecyclerViewAdapter adapter;

    MenuItem add_item;
    MenuItem cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        setTitle("상품 리스트");

        recyclerView=findViewById(R.id.recyclerView);


        setRecyclerView();
        is_login();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fileList();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.simple_menu, menu);

        add_item=menu.findItem(R.id.add_item);
        cart=menu.findItem(R.id.cart);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_item:
                Intent intent=new Intent(ItemViewActivity.this, AddItemActivity.class);
                startActivityForResult(intent, ADD_REQUEST_CODE);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                return true;
            case R.id.cart:
                Intent cart_intent=new Intent(ItemViewActivity.this, CartActivity.class);
                startActivity(cart_intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ADD_REQUEST_CODE){
            setRecyclerView();
        }else if(requestCode==400){
            setRecyclerView();
        }
    }

    private void is_login(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String targetURL="/is_login";

                try{
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);

                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();

                    String cookieString= CookieManager.getInstance().getCookie(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME));

                    if(cookieString != null){
                        connection.setRequestProperty("Cookie", cookieString);
                    }

                    connection.setRequestMethod("GET");

                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        Map<String, List<String>> headerFields = connection.getHeaderFields();
                        String COOKIES_HEADER = "Set-Cookie";
                        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                        if(cookiesHeader != null) {
                            for (String cookie : cookiesHeader) {
                                String cookieName = HttpCookie.parse(cookie).get(0).getName();
                                String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                                cookieString = cookieName + "=" + cookieValue;
                                CookieManager.getInstance().setCookie(
                                        getString(R.string.HOST_NETWORK_PROTOCOL) +
                                                getString(R.string.HOST_ADDRESS) +
                                                getString(R.string.HOST_APP_NAME), cookieString);
                            }
                        }

                        BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        Gson gson=new Gson();

                        final String is_login=gson.fromJson(in, String.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(is_login.equals("true")){
                                    add_item.setEnabled(true);
                                    cart.setEnabled(true);
                                }

                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ItemViewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    connection.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setRecyclerView(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String targetURL="/simpleItem_selectAll";

                try{
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);

                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();
                    connection.setRequestMethod("GET");

                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        //Gson gson=new Gson();
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

                        Type type=new TypeToken<ArrayList<SimpleBoardItemView>>(){}.getType();
                        itemList=gson.fromJson(in, type);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linearLayoutManager=new LinearLayoutManager(ItemViewActivity.this);
                                recyclerView.setLayoutManager(linearLayoutManager);

                                adapter=new SimpleItemRecyclerViewAdapter(itemList);
                                recyclerView.addItemDecoration(new DividerItemDecoration(ItemViewActivity.this, 1));
                                recyclerView.setAdapter(adapter);

                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ItemViewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    connection.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}
