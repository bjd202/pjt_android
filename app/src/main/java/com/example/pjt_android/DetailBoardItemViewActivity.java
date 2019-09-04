package com.example.pjt_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class DetailBoardItemViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_board_item_view);

        Intent intent=getIntent();

        int board_id=intent.getIntExtra("board_id", 0);

        Toast.makeText(DetailBoardItemViewActivity.this, ""+board_id, Toast.LENGTH_SHORT).show();
    }
}
