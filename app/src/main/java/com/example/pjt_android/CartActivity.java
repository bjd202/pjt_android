package com.example.pjt_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pjt_android.com.tje.model.Cart;
import com.example.pjt_android.com.tje.model.CartJsonModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    ArrayList<Cart> cartList;
    RecyclerView cart_recycerview;
    CartRecyclerViewAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    EditText et_cart_address_post;
    EditText et_cart_address_basic;
    TextInputEditText et_cart_address_detail;
    Button cart_btn_address;
    TextInputEditText cart_name;

    WebView webView;
    Dialog dialog;
    private Handler handler=new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("장바구니");

        cart_recycerview=findViewById(R.id.cart_recyclerview);

        et_cart_address_post=findViewById(R.id.et_cart_address_post);
        et_cart_address_basic=findViewById(R.id.et_cart_address_basic);
        et_cart_address_detail=findViewById(R.id.et_cart_address_detail);
        cart_btn_address=findViewById(R.id.cart_btn_address);
        cart_name=findViewById(R.id.cart_name);

        setCartRecyclerView();

        cart_btn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initWebView();
            }
        });
    }

    private void initWebView(){

        String targetURL="/address_search";

        dialog=new Dialog(this);
        dialog.setContentView(R.layout.input_address);

        webView=dialog.findViewById(R.id.webView1);
        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        webView.addJavascriptInterface(new CartActivity.AndroidBridge(), "pjt_android");
        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());
        // web view url load
        webView.loadUrl(getString(R.string.HOST_NETWORK_PROTOCOL) +
                getString(R.string.HOST_ADDRESS) +
                getString(R.string.HOST_APP_NAME) +
                targetURL);

        WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
        params.width= LinearLayout.LayoutParams.MATCH_PARENT;
        params.height=LinearLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);

        dialog.show();
    }

    private class AndroidBridge{
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    et_cart_address_post.setText(arg1);
                    et_cart_address_basic.setText(arg2);
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
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
                ArrayList<Cart> cartArrayList=adapter.getCartList();

                String address_post=et_cart_address_post.getText().toString().trim();
                String address_basic=et_cart_address_basic.getText().toString().trim();
                String address_detail=et_cart_address_detail.getText().toString().trim();
                String name=cart_name.getText().toString().trim();

                for (Cart cart : cartArrayList) {
                    if(cart.isSelected()){
                        if(cart.getNumber()==0 || String.valueOf(cart.getNumber()).equals("")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CartActivity.this, "개수를 입력해주세요", Toast.LENGTH_SHORT).show();
                                }
                            });

                            return;
                        }
                        CartJsonModel model=new CartJsonModel(
                                cart.getBoard_id(),
                                cart.getCategory(),
                                cart.getMember_id(),
                                name,
                                address_post,
                                address_basic,
                                address_detail,
                                cart.getTitle(),
                                cart.getNumber(),
                                cart.getPrice(),
                                cart.getCart_id()
                        );

                        jsonList.add(model);
                    }
                }

                if(jsonList.isEmpty()){
                    Toast.makeText(CartActivity.this, "선택된 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Gson gson=new Gson();
                String json=gson.toJson(jsonList);
                Log.e("json", json);

                try{
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);

                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();

                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod("POST");

                    connection.setDoOutput(true);
                    connection.getOutputStream().write(json.getBytes());

                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){

                        BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        HashMap<String, String> map=new HashMap<>();
                        map=gson.fromJson(in, map.getClass());;

                        final String result=map.get("result");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(result.equals("true")){
                                    Toast.makeText(CartActivity.this, "상품 구매를 완료했습니다.", Toast.LENGTH_SHORT).show();
                                    setCartRecyclerView();
                                }else{
                                    Toast.makeText(CartActivity.this, "상품 구매를 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
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
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String targetURL="/cart_delete";
                ArrayList<CartJsonModel> jsonList=new ArrayList<>();
                ArrayList<Cart> cartArrayList=adapter.getCartList();

                for (Cart cart : cartArrayList) {
                    if(cart.isSelected()){
                        CartJsonModel model=new CartJsonModel();
                        model.setCart_id(cart.getCart_id());

                        jsonList.add(model);
                    }
                }

                if(jsonList.isEmpty()){
                    Toast.makeText(CartActivity.this, "선택된 상품이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Gson gson=new Gson();
                String json=gson.toJson(jsonList);
                Log.e("json", json);

                try{
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);

                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();

                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod("POST");

                    connection.setDoOutput(true);
                    connection.getOutputStream().write(json.getBytes());

                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){

                        BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        HashMap<String, String> map=new HashMap<>();
                        map=gson.fromJson(in, map.getClass());;

                        final String result=map.get("result");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(result.equals("true")){
                                    Toast.makeText(CartActivity.this, "장바구니 삭제를 완료했습니다.", Toast.LENGTH_SHORT).show();
                                    setCartRecyclerView();
                                }else{
                                    Toast.makeText(CartActivity.this, "장바구니를 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
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
}
