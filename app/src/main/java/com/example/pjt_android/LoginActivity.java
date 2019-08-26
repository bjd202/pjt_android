package com.example.pjt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String HOST_NETWORK_PROTOCOL="http://";
    private static final String HOST_ADDRESS="192.168.219.100:8181";
    private static final String HOST_APP_NAME="/webapp/android";

    TextInputLayout til_id;
    TextInputLayout til_pw;

    TextInputEditText et_id;
    TextInputEditText et_pw;

    Button btn_login;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        til_id=findViewById(R.id.loginact_til_id);
        til_pw=findViewById(R.id.loginact_til_pw);

        et_id=findViewById(R.id.loginact_et_id);
        et_pw=findViewById(R.id.loginact_et_pw);

        btn_login=findViewById(R.id.loginact_btn_login);
        btn_back=findViewById(R.id.loginact_btn_back);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    private void login(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String id=et_id.getText().toString().trim();
                String pw=et_pw.getText().toString().trim();

                if(id.length()==0 || pw.length()==0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "ID와 PW를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                String param=String.format("member_id=%s&password=%s", id, pw);
                String targetURL="/login";

                try{
                    URL endPoint=new URL(HOST_NETWORK_PROTOCOL +
                            HOST_ADDRESS +
                            HOST_APP_NAME +
                            targetURL);
                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();

                    String cookieString= CookieManager.getInstance().getCookie(HOST_NETWORK_PROTOCOL +
                            HOST_ADDRESS +
                            HOST_APP_NAME);

                    if(cookieString != null){
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
                                        HOST_NETWORK_PROTOCOL +
                                                HOST_ADDRESS +
                                                HOST_APP_NAME, cookieString);
                            }
                        }

                        BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                        Gson gson=new Gson();

                        HashMap<String, String> result=new HashMap<>();
                        result=gson.fromJson(in, result.getClass());

                        final String login_result=result.get("login_result");
                        final String login_msg=result.get("login_msg");
                        final String login_nickname=result.get("login_nickname");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(login_result.equals("true")){
                                    Intent intent=getIntent();
                                    setResult(RESULT_OK, intent);
                                    intent.putExtra("login_msg", login_msg);
                                    intent.putExtra("login_nickname", login_nickname);
                                    finish();
                                }else {
                                    Toast.makeText(LoginActivity.this, login_msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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

    private void back(){
        finish();
    }
}
