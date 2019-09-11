package com.example.pjt_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.example.pjt_android.com.tje.model.Cart;
import com.example.pjt_android.com.tje.model.CartJsonModel;
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

public class CartActivity extends AppCompatActivity {

    ArrayList<Cart> cartList;
    RecyclerView cart_recycerview;
    CartRecyclerViewAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart_recycerview=findViewById(R.id.cart_recyclerview);

        setCartRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.cart_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.selected_buy:
                selected_buy();
                return true;
            case R.id.selected_delete:
                selected_delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setCartRecyclerView(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String targetURL="/cart_list";

                try{
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);

                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();
                    connection.setRequestMethod("GET");

                    String cookieString = CookieManager.getInstance().getCookie(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME));

                    if (cookieString != null) {
                        connection.setRequestProperty("Cookie", cookieString);
                    }

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

                        Type type=new TypeToken<ArrayList<Cart>>(){}.getType();
                        cartList=gson.fromJson(in, type);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linearLayoutManager=new LinearLayoutManager(CartActivity.this);
                                cart_recycerview.setLayoutManager(linearLayoutManager);

                                adapter=new CartRecyclerViewAdapter(cartList);
                                cart_recycerview.setAdapter(adapter);

                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CartActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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

    private void selected_buy(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String targetURL="/item_buy";
                ArrayList<CartJsonModel> jsonList=new ArrayList<>();
                ArrayList<Cart> cartList=adapter.getCartList();

                for (Cart cart : cartList) {
                    CartJsonModel model=new CartJsonModel();
                }

                try{
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);

                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();
                    connection.setRequestProperty("Content-Type", "appication/json");
                    connection.setRequestMethod("POST");

                    String cookieString = CookieManager.getInstance().getCookie(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME));

                    if (cookieString != null) {
                        connection.setRequestProperty("Cookie", cookieString);
                    }

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

                        Type type=new TypeToken<ArrayList<Cart>>(){}.getType();
                        cartList=gson.fromJson(in, type);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                

                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CartActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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

    private void selected_delete(){

    }
}
