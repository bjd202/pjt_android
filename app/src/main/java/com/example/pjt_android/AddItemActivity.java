package com.example.pjt_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;


public class AddItemActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION=5000;

    TextInputEditText add_item_title;
    TextInputEditText add_item_content;

    Spinner add_item_category;
    int category;

    EditText add_item_number;
    EditText add_item_price;

    ImageView add_item_image;

    Button add_item_select_image;
    Button add_item_add_btn;
    Button add_item_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        add_item_title=findViewById(R.id.add_item_title);
        add_item_content=findViewById(R.id.add_item_content);
        add_item_category=findViewById(R.id.add_item_category);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.category, R.layout.support_simple_spinner_dropdown_item);
        add_item_category.setAdapter(adapter);
        add_item_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category=add_item_category.getSelectedItemPosition()+1;
                Log.e("category", String.valueOf(category));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        add_item_number=findViewById(R.id.add_item_number);
        add_item_price=findViewById(R.id.add_item_price);

        add_item_image=findViewById(R.id.add_item_image);

        add_item_select_image=findViewById(R.id.add_item_select_image);
        add_item_add_btn=findViewById(R.id.add_item_add_btn);
        add_item_cancel=findViewById(R.id.add_item_cancel);

        add_item_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(AddItemActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(AddItemActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }

                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });

        add_item_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        add_item_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_PERMISSION){
            for (int i=0; i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                    Toast.makeText(AddItemActivity.this, "권한 허용이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1){
            Bitmap bitmap= BitmapFactory.decodeFile(getRealPathFromURI(data.getData()));
            add_item_image.setImageBitmap(bitmap);
        }
    }

    private String getRealPathFromURI(Uri uri){
        String res = null;

        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);

        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
