package com.example.pjt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class SnsRegistActivity extends AppCompatActivity {

    private Handler handler=new Handler();

    TextInputLayout[] textInputLayouts;

    TextInputEditText et_name;
    TextInputEditText et_nickname;
    TextInputEditText et_tel;
    TextInputEditText et_address_detail;

    Button btn_regist;
    Button btn_back;
    Button btn_address;

    EditText et_address_post;
    EditText et_address_basic;

    WebView webView;

    CheckBox[] checkBoxes;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sns_regist);
        setTitle("sns 회원가입");

        textInputLayouts=new TextInputLayout[4];
        textInputLayouts[0]=findViewById(R.id.sns_til_name);
        textInputLayouts[1]=findViewById(R.id.sns_til_nickname);
        textInputLayouts[2]=findViewById(R.id.sns_til_tel);
        textInputLayouts[3]=findViewById(R.id.sns_til_address_detail);

        et_name=findViewById(R.id.sns_et_name);
        et_nickname=findViewById(R.id.sns_et_nickname);
        et_tel=findViewById(R.id.sns_et_tel);
        et_address_detail=findViewById(R.id.sns_et_address_detail);

        et_name.addTextChangedListener(new InputTextVerification("^[a-zA-z가-힣]{1,8}$", et_name, textInputLayouts[0]));
        et_nickname.addTextChangedListener(new InputTextVerification("^[a-zA-z가-힣]{3,8}$", et_nickname, textInputLayouts[1]));
        et_tel.addTextChangedListener(new InputTextVerification("^[0-9]{10,12}$", et_tel, textInputLayouts[2]));
        et_address_detail.addTextChangedListener(new InputTextVerification("^[a-zA-Z0-9가-힣]{0,45}$", et_address_detail, textInputLayouts[3]));

        btn_regist=findViewById(R.id.sns_btn_regist);
        btn_back=findViewById(R.id.sns_btn_back);
        btn_address=findViewById(R.id.sns_btn_address);

        et_address_post=findViewById(R.id.sns_et_address_post);
        et_address_basic=findViewById(R.id.sns_et_address_basic);

        checkBoxes=new CheckBox[6];
        checkBoxes[0]=findViewById(R.id.sns_checkBox1);
        checkBoxes[1]=findViewById(R.id.sns_checkBox2);
        checkBoxes[2]=findViewById(R.id.sns_checkBox3);
        checkBoxes[3]=findViewById(R.id.sns_checkBox4);
        checkBoxes[4]=findViewById(R.id.sns_checkBox5);
        checkBoxes[5]=findViewById(R.id.sns_checkBox6);

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist();
            }
        });

        btn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initWebView();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    private void initWebView(){

        String targetURL="/address_search";

        dialog=new Dialog(this);
        dialog.setContentView(R.layout.input_address);

        webView=dialog.findViewById(R.id.webView);
        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        webView.addJavascriptInterface(new SnsRegistActivity.AndroidBridge(), "pjt_android");
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
                    et_address_post.setText(arg1);
                    et_address_basic.setText(arg2);
                    dialog.dismiss();
                }
            });
        }
    }

    private void regist(){
        int check_cnt=0;

        for(int i=0; i<textInputLayouts.length; i++){
            if(textInputLayouts[i].getError()!=null){
                Toast.makeText(this, "올바르지 못한 데이터 포함", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        for(int i=0; i<checkBoxes.length; i++){
            if(checkBoxes[i].isChecked()){
                check_cnt++;
            }
        }

        if(check_cnt==0) {
            Toast.makeText(this, "관심분야는 1개 이상 체크해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Intent intent=getIntent();

                String member_id=intent.getStringExtra("member_id");
                String password=String.valueOf(new Random().nextInt());
                String name=et_name.getText().toString().trim();
                String nickname=et_nickname.getText().toString().trim();
                String tel=et_tel.getText().toString().trim();
                String address_post=et_address_post.getText().toString().trim();
                String address_basic=et_address_basic.getText().toString().trim();
                String address_detail=et_address_detail.getText().toString().trim();
                String interest="";
                String member_type=intent.getStringExtra("member_type");

                for(int i=0; i<checkBoxes.length; i++){
                    if(checkBoxes[i].isChecked()){
                        interest+=checkBoxes[i].getText().toString()+",";
                    }
                }
                interest=interest.substring(0, interest.length()-1);

                String param=String.format("member_id=%s&password=%s&name=%s&nickname=%s&tel=%s&address_post=%s&address_basic=%s&address_detail=%s&interest=%s&member_type=%s",
                        member_id, password, name, nickname, tel, address_post, address_basic, address_detail, interest, member_type);

                String targetURL="/sns_regist";

                try {
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);

                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();
                    connection.setRequestMethod("POST");

                    if(param!=null){
                        connection.setDoOutput(true);
                        connection.getOutputStream().write(param.getBytes());
                    }

                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        Gson gson=new Gson();

                        HashMap<String, String> result=new HashMap<>();
                        result=gson.fromJson(in, result.getClass());

                        final String msg=result.get("msg");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SnsRegistActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SnsRegistActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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
