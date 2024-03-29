package com.example.pjt_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity {

    private final int REGIST_REQUESTCODE=200;
    private final int LOGIN_REQUESTCODE=300;

    Button btn_regist;
    Button btn_login;
    Button btn_review;
    Button btn_item;

    GoogleApiClient googleApiClient;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("메인");

        btn_regist=findViewById(R.id.btn_regist);
        btn_login=findViewById(R.id.btn_login);
        btn_review=findViewById(R.id.btn_review);
        btn_item=findViewById(R.id.btn_item);

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, ReviewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        btn_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, ItemViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        getKeyHash(this);

    }

    public void regist(){
        Intent intent=new Intent(this, RegistActivity.class);
        startActivityForResult(intent, REGIST_REQUESTCODE);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    public void login(){
        if(btn_login.getText().toString().equals("로그인")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUESTCODE);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        }else{
            logout();
        }
    }

    public void logout(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String targetURL="/logout";

                try{
                    URL endpoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL)+
                            getString(R.string.HOST_ADDRESS)+
                            getString(R.string.HOST_APP_NAME)+
                            targetURL);

                    HttpURLConnection connection= (HttpURLConnection) endpoint.openConnection();

                    String cookieString =
                            CookieManager.getInstance().getCookie(
                                    getString(R.string.HOST_NETWORK_PROTOCOL) +
                                            getString(R.string.HOST_ADDRESS) +
                                            getString(R.string.HOST_APP_NAME));

                    if( cookieString != null )
                        connection.setRequestProperty("Cookie", cookieString);
                    else
                        return;

                    connection.setRequestMethod("GET");

                    if( connection.getResponseCode() == HttpURLConnection.HTTP_OK ) {

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

                        HashMap<String, String> result=new HashMap<>();
                        result=gson.fromJson(in, result.getClass());

                        final String logout_result=result.get("logout_result");
                        final String logout_msg=result.get("logout_msg");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(logout_result.equals("true")){
                                    Toast.makeText(MainActivity.this, logout_msg, Toast.LENGTH_SHORT).show();
                                    btn_login.setText("로그인");

                                    // 카카오 로그아웃
                                    if(Session.getCurrentSession().isOpened()){
                                        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                            @Override
                                            public void onCompleteLogout() {
                                                Log.e("kakao_logout_success", "kakao_logout_success");
                                            }
                                        });
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, logout_msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    connection.disconnect();

                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==LOGIN_REQUESTCODE){
            if(resultCode==RESULT_OK) {
                String login_msg = data.getStringExtra("login_msg");
                Toast.makeText(this, login_msg, Toast.LENGTH_SHORT).show();
                btn_login.setText("로그아웃");
            }else if(resultCode==10){
                String member_id=data.getStringExtra("member_id");
                String member_type=String.valueOf(data.getIntExtra("member_type",0));
                Intent intent=new Intent(MainActivity.this, SnsRegistActivity.class);
                intent.putExtra("member_id", member_id);
                intent.putExtra("member_type",member_type);
                startActivityForResult(intent, REGIST_REQUESTCODE);
            }
        }
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("hashkey", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("e", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }
}
