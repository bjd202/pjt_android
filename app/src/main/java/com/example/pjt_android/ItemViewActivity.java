package com.example.pjt_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pjt_android.com.tje.model.SimpleBoardItemView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ItemViewActivity extends AppCompatActivity {

    private ArrayList<SimpleBoardItemView> itemList;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        recyclerView=findViewById(R.id.recyclerView);

        setRecyclerView();

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
                                recyclerView.setAdapter(adapter);

//                                adapter.setOnItemClickListener(new SimpleItemRecyclerViewAdapter.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(View v, int position) {
//                                        SimpleBoardItemView item=itemList.get(position);
//                                        Intent intent=new Intent(ItemViewActivity.this, DetailBoardItemViewActivity.class);
//                                        intent.putExtra("board_id", item.getBoard_id());
//                                        startActivity(intent);
//                                    }
//                                });
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
