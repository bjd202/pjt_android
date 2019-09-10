package com.example.pjt_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pjt_android.com.tje.model.Comment;
import com.example.pjt_android.com.tje.model.DetailBoardItemView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailBoardItemViewActivity extends AppCompatActivity {

    ImageView detail_item_image;
    TextView detail_item_nickname;
    TextView detail_item_write_date;
    TextView detail_item_category;
    TextView detail_item_price;
    TextView detail_item_number;

    Button detail_item_buy;
    Button detail_item_cart;
    Button detail_item_like;
    Button detail_item_dislike;

    TextView detail_item_content;

    EditText detail_item_comment_content;
    Button detail_item_comment_btn;

    RecyclerView comment_recyclerView;
    CommentRecyclerViewAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_board_item_view);

        detail_item_image=findViewById(R.id.detail_item_image);
        detail_item_nickname=findViewById(R.id.detail_item_nickname);
        detail_item_write_date=findViewById(R.id.detail_item_write_date);
        detail_item_category=findViewById(R.id.detail_item_category);
        detail_item_price=findViewById(R.id.detail_item_price);
        detail_item_number=findViewById(R.id.detail_item_number);

        detail_item_buy=findViewById(R.id.detail_item_buy);
        detail_item_cart=findViewById(R.id.detail_item_cart);
        detail_item_like=findViewById(R.id.detail_item_like);
        detail_item_dislike=findViewById(R.id.detail_item_dislike);

        detail_item_content=findViewById(R.id.detail_item_content);

        detail_item_comment_content=findViewById(R.id.detail_item_comment_content);
        detail_item_comment_btn=findViewById(R.id.detail_item_comment_btn);

        comment_recyclerView=findViewById(R.id.comment_recyclerView);

        detail_item_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_and_dislike(1);
            }
        });

        detail_item_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_and_dislike(2);
            }
        });

        detail_item_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_comment();
            }
        });

        setDetailView();
    }

    private void like_and_dislike(final int like_or_dislike){


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Intent intent=getIntent();
                int board_id=intent.getIntExtra("board_id", 0);
                int topic=3;

                String param=String.format("board_id=%d&is_like=%d&topic=%d", board_id, like_or_dislike, topic);
                String targetURL="/like_and_dislike";

                try {
                    URL endPoint = new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);
                    HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();

                    String cookieString = CookieManager.getInstance().getCookie(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME));

                    if (cookieString != null) {
                        connection.setRequestProperty("Cookie", cookieString);
                    }

                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.getOutputStream().write(param.getBytes());

                    if( connection.getResponseCode() == HttpURLConnection.HTTP_OK ){
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

                        BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                        Gson gson=new Gson();

                        HashMap<String, Object> result=new HashMap<>();
                        result=gson.fromJson(in, result.getClass());

                        final String fail= (String) result.get("fail");
                        Double d_like_cnt= Double.parseDouble(result.get("like_cnt").toString());
                        final int like_cnt=d_like_cnt.intValue();
                        Double d_dislike_cnt= Double.parseDouble(result.get("dislike_cnt").toString());
                        final int dislike_cnt=d_dislike_cnt.intValue();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(fail!=null){
                                    Toast.makeText(DetailBoardItemViewActivity.this, fail, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                detail_item_like.setText("좋아요 : "+like_cnt);
                                detail_item_dislike.setText("싫어요 : "+dislike_cnt);
                            }
                        });


                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailBoardItemViewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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

    private void add_comment(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Intent intent=getIntent();
                int board_id=intent.getIntExtra("board_id", 0);
                String content=detail_item_comment_content.getText().toString();

                if(content.length()==0){
                    Toast.makeText(DetailBoardItemViewActivity.this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String param=String.format("board_id=%d&content=%s", board_id, content);
                String targetURL="/add_comment";

                try {
                    URL endPoint = new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);
                    HttpURLConnection connection = (HttpURLConnection) endPoint.openConnection();

                    String cookieString = CookieManager.getInstance().getCookie(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME));

                    if (cookieString != null) {
                        connection.setRequestProperty("Cookie", cookieString);
                    }

                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.getOutputStream().write(param.getBytes());

                    if( connection.getResponseCode() == HttpURLConnection.HTTP_OK ){
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

                        BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                        Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

                        HashMap<String, Object> result=new HashMap<>();
                        result=gson.fromJson(in, result.getClass());

                        final String comment_result= (String) result.get("comment_result");
                        final Comment comment= gson.fromJson(gson.toJson(result.get("comment")), Comment.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailBoardItemViewActivity.this, comment_result, Toast.LENGTH_SHORT).show();
                                if(comment!=null)
                                    adapter.add_comment(comment);
                            }
                        });


                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailBoardItemViewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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

    private void setDetailView(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Intent intent=getIntent();
                int board_id=intent.getIntExtra("board_id", 0);

                String targetURL="/detailBoardItemView/"+board_id;

                try {
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);



                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();

                    String cookieString = CookieManager.getInstance().getCookie(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME));

                    if (cookieString != null) {
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
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

                        HashMap<String, Object> map=new HashMap<>();
                        map=gson.fromJson(in, map.getClass());

                        if( map.get("view_cnt_update_fail") != null ){
                            Toast.makeText(DetailBoardItemViewActivity.this, map.get("view_cnt_update_fail").toString(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final DetailBoardItemView result = gson.fromJson( gson.toJson(map.get("detail_item")), DetailBoardItemView.class);

                        final String imgUrl=getString(R.string.HOST_NETWORK_PROTOCOL)+
                                getString(R.string.HOST_ADDRESS)+
                                getString(R.string.HOST_IMG_URL)+
                                result.getImage();

                        final String login_result= (String) map.get("login_result");

                        Type type=new TypeToken<ArrayList<Comment>>(){}.getType();
                        final ArrayList<Comment> commentList=gson.fromJson(gson.toJson(map.get("comment_list")), type);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(login_result.equals("true")){
                                    detail_item_like.setEnabled(true);
                                    detail_item_dislike.setEnabled(true);
                                    detail_item_comment_btn.setEnabled(true);
                                    detail_item_buy.setEnabled(true);
                                    detail_item_cart.setEnabled(true);
                                }

                                Glide.with(DetailBoardItemViewActivity.this).load(imgUrl).into(detail_item_image);

                                detail_item_nickname.setText(result.getNickname());
                                detail_item_write_date.setText(result.getWrite_dateString());
                                detail_item_category.setText(result.getCategoryString());
                                detail_item_price.setText(result.getPrice()+"원");
                                detail_item_number.setText(result.getNumber()+"개");

                                detail_item_like.setText("좋아요 : "+result.getLike_cnt());
                                detail_item_dislike.setText("싫어요 : "+result.getDislike_cnt());

                                detail_item_content.setText(result.getContent());

                                if( commentList!=null ) {
                                    linearLayoutManager = new LinearLayoutManager(DetailBoardItemViewActivity.this);
                                    comment_recyclerView.setLayoutManager(linearLayoutManager);

                                    adapter = new CommentRecyclerViewAdapter(commentList);
                                    comment_recyclerView.setAdapter(adapter);
                                }

                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailBoardItemViewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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
