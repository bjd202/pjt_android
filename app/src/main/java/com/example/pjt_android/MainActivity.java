package com.example.pjt_android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final int REGIST_REQUESTCODE=200;

    Button btn_regist;
    Button btn_login;
    Button btn_review;
    Button btn_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    public void regist(){
        Intent intent=new Intent(this, RegistActivity.class);
        startActivityForResult(intent, REGIST_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
