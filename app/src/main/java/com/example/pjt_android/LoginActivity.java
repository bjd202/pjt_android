package com.example.pjt_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Context context=this;

    TextInputLayout til_id;
    TextInputLayout til_pw;

    TextInputEditText et_id;
    TextInputEditText et_pw;

    Button btn_login;
    Button btn_back;
    OAuthLoginButton btn_naver_login;
    LoginButton btn_kakao_login;
    SignInButton btn_google_login;

    public static OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();
    SessionCallback callback=null;

    final int R_SIGN_IN=9000;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("로그인");

        mOAuthLoginModule.init(
                this,
                getString(R.string.naverClientID),
                getString(R.string.naverClientSecret),
                getString(R.string.naverClientName)
        );

        til_id=findViewById(R.id.loginact_til_id);
        til_pw=findViewById(R.id.loginact_til_pw);

        et_id=findViewById(R.id.loginact_et_id);
        et_pw=findViewById(R.id.loginact_et_pw);

        btn_login=findViewById(R.id.loginact_btn_login);
        btn_back=findViewById(R.id.loginact_btn_back);
        btn_naver_login=findViewById(R.id.naver_login);
        btn_kakao_login=findViewById(R.id.kakao_login);
        btn_google_login=findViewById(R.id.google_login);

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

        // 네이버 로그인
        btn_naver_login.setOAuthLoginHandler(mOAuthLoginHandler);

        // 카카오 로그인
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        // 구글 로그인
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient=GoogleSignIn.getClient(LoginActivity.this, gso);

        firebaseAuth=FirebaseAuth.getInstance();

        btn_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=googleSignInClient.getSignInIntent();
                startActivityForResult(intent, R_SIGN_IN);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
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

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                final String accessToken = mOAuthLoginModule.getAccessToken(context);
                //String refreshToken = mOAuthLoginModule.getRefreshToken(context);
                //long expiresAt = mOAuthLoginModule.getExpiresAt(context);
                //String tokenType = mOAuthLoginModule.getTokenType(context);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String apiResult = mOAuthLoginModule.requestApi(context, accessToken, "https://openapi.naver.com/v1/nid/me");

                        JsonParser jsonParser=new JsonParser();
                        JsonElement jsonElement=jsonParser.parse(apiResult);

                        JsonElement response=jsonElement.getAsJsonObject().get("response");
                        final String id=response.getAsJsonObject().get("id").getAsString();
                        String nickname=response.getAsJsonObject().get("nickname").toString();

                        String param=String.format("member_id=%s", id);
                        String targetURL="/naver_login";

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
                                            Intent intent=getIntent();
                                            setResult(10, intent);
                                            intent.putExtra("member_id", id);
                                            intent.putExtra("member_type",1);
                                            finish();
                                        }
                                    }
                                });


                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

            }else{
                String errorCode = mOAuthLoginModule.getLastErrorCode(context).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(context);
                Toast.makeText(context, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        };
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == R_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.e("google login failed", "Google sign in failed", e);
                    // ...
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }



    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }

    private void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onSuccess(final MeV2Response response) {
                Logger.d("user id : " + response.getId());

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final String id=String.valueOf(response.getId());

                        String param=String.format("member_id=%s", id);
                        String targetURL="/kakao_login";

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
                                            Intent intent=getIntent();
                                            setResult(RESULT_CANCELED, intent);
                                            intent.putExtra("member_id", id);
                                            intent.putExtra("member_type", 3);
                                            finish();
                                        }
                                    }
                                });


                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.e("firebaseAuth", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("signIn", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();

                            // String uid=user.getUid(); firebase ID
                            final String id=acct.getId();

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    String param=String.format("member_id=%s", id);
                                    String targetURL="/google_login";

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
                                                        Intent intent=getIntent();
                                                        setResult(RESULT_CANCELED, intent);
                                                        intent.putExtra("member_id", id);
                                                        intent.putExtra("member_type", 2);
                                                        finish();
                                                    }
                                                }
                                            });


                                        }else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(LoginActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("fail", "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }






    private void back(){
        finish();
    }
}
