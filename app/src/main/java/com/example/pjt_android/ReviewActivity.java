package com.example.pjt_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.example.pjt_android.com.tje.model.SimpleBoardReviewView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    private ArrayList<SimpleBoardReviewView> reviewList;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SimpleReviewRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        setTitle("리뷰 리스트");

        recyclerView=findViewById(R.id.review_recyclerview);

        setReviewList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    public void setReviewList(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String targetURL="/simpleReview_selectAll";

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

                        Type type=new TypeToken<ArrayList<SimpleBoardReviewView>>(){}.getType();
                        reviewList=gson.fromJson(in, type);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linearLayoutManager=new LinearLayoutManager(ReviewActivity.this);
                                recyclerView.setLayoutManager(linearLayoutManager);

                                adapter=new SimpleReviewRecyclerViewAdapter(reviewList);
                                recyclerView.addItemDecoration(new DividerItemDecoration(ReviewActivity.this, 1));
                                recyclerView.setAdapter(adapter);

                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReviewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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
