package com.example.pjt_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pjt_android.com.tje.model.DetailBoardItemView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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

        setDetailView();

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
                    connection.setRequestMethod("GET");

                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
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



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(DetailBoardItemViewActivity.this).load(imgUrl).into(detail_item_image);

                                detail_item_nickname.setText(result.getNickname());
                                detail_item_write_date.setText(result.getWrite_dateString());
                                detail_item_category.setText(result.getCategoryString());
                                detail_item_price.setText(result.getPrice());
                                detail_item_number.setText(result.getNumber()+"개");

                                detail_item_like.setText("좋아요 : "+result.getLike_cnt());
                                detail_item_dislike.setText("싫어요 : "+result.getDislike_cnt());

                                detail_item_content.setText(result.getContent());
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
