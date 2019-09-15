package com.example.pjt_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pjt_android.com.tje.model.Comment;
import com.example.pjt_android.com.tje.model.DetailBoardReviewView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

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

public class DetailReviewActivity extends AppCompatActivity {

    private static final int REQUEST_USED_PERMISSION=700;
    private static final String[] needPermissions={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    
    Location lastKnownLocation;

    Context context=this;

    ImageView detail_review_image;
    TextView detail_review_nickname;
    TextView detail_review_write_date;
    TextView detail_review_category;
    
    Button detail_review_like;
    Button detail_review_dislike;

    TextView detail_review_content;

    EditText detail_review_comment_content;
    Button detail_review_comment_btn;

    RecyclerView comment_recyclerView;
    CommentRecyclerViewAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    MapView mapView;
    ViewGroup mapViewContainer;

    Button load_search_btn;

    double current_lat=0.0;
    double current_long=0.0;

    double arrival_lat;
    double arrival_long;

    int b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_review);
        setTitle("리뷰");

        detail_review_image=findViewById(R.id.detail_review_image);
        detail_review_nickname=findViewById(R.id.detail_review_nickname);
        detail_review_write_date=findViewById(R.id.detail_review_write_date);
        detail_review_category=findViewById(R.id.detail_review_category);
 
        detail_review_like=findViewById(R.id.detail_review_like);
        detail_review_dislike=findViewById(R.id.detail_review_dislike);

        detail_review_content=findViewById(R.id.detail_review_content);

        detail_review_comment_content=findViewById(R.id.detail_review_comment_content);
        detail_review_comment_btn=findViewById(R.id.detail_review_comment_btn);

        comment_recyclerView=findViewById(R.id.review_comment_recyclerView);

        load_search_btn=findViewById(R.id.load_search_btn);

        detail_review_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_and_dislike(1);
            }
        });

        detail_review_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_and_dislike(2);
            }
        });

        detail_review_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_comment();
            }
        });

        load_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String permission : needPermissions){
                    if(ActivityCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(DetailReviewActivity.this, needPermissions, REQUEST_USED_PERMISSION);
                        Log.e("위치정보 허용x", permission);
                    }else{
                        Log.e("위치정보 허용o", permission);
                    }
                }

                if(existDaumMap()){

                    //'37.555440075383814', '126.9361204389213'
                    //String url = "daummaps://route?sp="+37.555440075383814+","+126.9361204389213+"&ep="+arrival_lat+","+arrival_long+"&by=FOOT";
                    getMyLocation();
//                    Log.e("cur lat", latLng.getLatitude()+"");
////                    Log.e("cur long", latLng.getLongitude()+"");
//                    String url="daummaps://route?sp="+latLng.getLatitude()+","+latLng.getLongitude()+"&ep="+arrival_lat+","+arrival_long+"&by=FOOT";
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
                }else{
                    String downloadURI="market://details?id=net.daum.android.map";
                    Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(downloadURI));
                    startActivity(intent);
                    return;
                }
            }
        });

        setDetailView();

//        MapView mapView=new MapView(this);
//        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
//        mapViewContainer.addView(mapView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permissionAccepted=true;

        switch (requestCode){
            case REQUEST_USED_PERMISSION:
                for (int result : grantResults){
                    if(result != PackageManager.PERMISSION_GRANTED){
                        permissionAccepted=false;
                        break;
                    }
                }
                break;
        }

        if(permissionAccepted==false){
            Toast.makeText(context, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            getMyLocation();
        }
    }
    
    public void getMyLocation(){
        if(ActivityCompat.checkSelfPermission(DetailReviewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(DetailReviewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        FusedLocationProviderClient fusedLocationProviderClient=new FusedLocationProviderClient(DetailReviewActivity.this);
        Task<Location> task=fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    new AlertDialog.Builder(DetailReviewActivity.this)
                            .setTitle("위치 정보 허용 필요")
                            .setMessage("이 기능을 사용하려면 위치 정보의 허용이 필요합니다.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    //startActivity(intent);
                                    startActivityForResult(intent, 90);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                }

                if(location!=null) {
                    lastKnownLocation = location;
                    Log.e("location object", location.toString());
                    current_lat = lastKnownLocation.getLatitude();
                    Log.e("cur_lat", current_lat + "");
                    current_long = lastKnownLocation.getLongitude();
                    Log.e("cur_long", current_long + "");

                    String url = "daummaps://route?sp=" + current_lat + "," + current_long + "&ep=" + arrival_lat + "," + arrival_long + "&by=FOOT";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }else{
//                    new AlertDialog.Builder(DetailReviewActivity.this)
//                            .setTitle("위치 정보 허용 필요")
//                            .setMessage("이 기능을 사용하려면 위치 정보의 허용이 필요합니다.")
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                    //startActivity(intent);
//                                    startActivityForResult(intent, 90);
//                                }
//                            })
//                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    return;
//                                }
//                            })
//                            .show();
                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (ActivityCompat.checkSelfPermission(DetailReviewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(DetailReviewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Toast.makeText(context, "현재 위치를 가져오지 못함", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==90){
            String url = "daummaps://open";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    public boolean existDaumMap(){
        PackageManager pm=context.getPackageManager();

        try {
            return (pm.getPackageInfo("net.daum.android.map", PackageManager.GET_SIGNATURES) != null);
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    private void like_and_dislike(final int like_or_dislike){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Intent intent=getIntent();
                int board_id=intent.getIntExtra("board_id", 0);
                int topic=1;

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
                                    Toast.makeText(DetailReviewActivity.this, fail, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                detail_review_like.setText(""+like_cnt);
                                detail_review_dislike.setText(""+dislike_cnt);
                            }
                        });


                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailReviewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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
                String content=detail_review_comment_content.getText().toString();
                int topic=1;

                if(content.length()==0){
                    Toast.makeText(DetailReviewActivity.this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String param=String.format("board_id=%d&content=%s&topic=%d", board_id, content, topic);
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
                                Toast.makeText(DetailReviewActivity.this, comment_result, Toast.LENGTH_SHORT).show();
                                if(comment!=null){
                                    adapter.add_comment(comment);
                                }
                            }
                        });


                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailReviewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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
                //int board_id=intent.getIntExtra("board_id", 0);
                b=intent.getIntExtra("board_id",0);

                String targetURL="/detailReview/"+b;

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
                            Toast.makeText(DetailReviewActivity.this, map.get("view_cnt_update_fail").toString(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final DetailBoardReviewView result = gson.fromJson( gson.toJson(map.get("detail_review")), DetailBoardReviewView.class);

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
                                    detail_review_like.setEnabled(true);
                                    detail_review_dislike.setEnabled(true);
                                    detail_review_comment_btn.setEnabled(true);
                                }

                                Glide.with(DetailReviewActivity.this).load(imgUrl).into(detail_review_image);

                                detail_review_nickname.setText(result.getNickname());
                                detail_review_write_date.setText(result.getWrite_dateString());
                                detail_review_category.setText(result.getCategoryString());

                                detail_review_like.setText(""+result.getLike_cnt());
                                detail_review_dislike.setText(""+result.getDislike_cnt());

                                detail_review_content.setText(result.getContent());

                                linearLayoutManager = new LinearLayoutManager(DetailReviewActivity.this);
                                comment_recyclerView.setLayoutManager(linearLayoutManager);

                                adapter = new CommentRecyclerViewAdapter(commentList);
                                comment_recyclerView.setAdapter(adapter);

                                if(result.getCategoryString().equals("장소")) {
                                    arrival_lat=result.getSelectedLat();
                                    arrival_long=result.getSelectedLng();

                                    mapView = new MapView(context);
                                    mapViewContainer = findViewById(R.id.map_view);
                                    mapViewContainer.setVisibility(View.VISIBLE);
                                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(result.getSelectedLat(), result.getSelectedLng());
                                    mapView.setMapCenterPoint(mapPoint, true);
                                    mapViewContainer.addView(mapView);

                                    MapPOIItem marker = new MapPOIItem();
                                    marker.setItemName(result.getSelectedAddress());
                                    marker.setTag(0);
                                    marker.setMapPoint(mapPoint);
                                    marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                                    mapView.addPOIItem(marker);
                                }
                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailReviewActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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
