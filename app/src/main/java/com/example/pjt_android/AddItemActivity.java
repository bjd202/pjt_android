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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddItemActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION=5000;

    TextInputLayout add_item_til_title;
    TextInputLayout add_item_til_content;

    TextInputEditText add_item_title;
    TextInputEditText add_item_content;

    Spinner add_item_category;
    int category;

    EditText add_item_number;
    EditText add_item_price;

    ImageView add_item_image;
    File image=null;

    Button add_item_select_image;
    Button add_item_add_btn;
    Button add_item_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        setTitle("상품 추가");

        add_item_til_title=findViewById(R.id.add_item_til_title);
        add_item_til_content=findViewById(R.id.add_item_til_content);

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
                add_item();
            }
        });

        add_item_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    private void add_item(){
        if(add_item_title.length()==0 || add_item_title.length()>add_item_til_title.getCounterMaxLength()){
            Toast.makeText(this, "제목을 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(add_item_content.length()==0 || add_item_content.length()>add_item_til_content.getCounterMaxLength()){
            Toast.makeText(this, "내용을 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(add_item_number.length()==0){
            Toast.makeText(this, "개수를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(add_item_price.length()==0){
            Toast.makeText(this, "가격을 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String title=add_item_title.getText().toString().trim();
                String content=add_item_content.getText().toString().trim();
                String str_number=add_item_number.getText().toString().trim();
                int number=Integer.parseInt(str_number);
                String price=add_item_price.getText().toString().trim();

                String targetURL="/add_item";
                HashMap<String, Object> params=new HashMap<>();
                params.put("title", title);
                params.put("content", content);
                params.put("category", category);
                params.put("number", number);
                params.put("price", price);

                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "androidupload";

                byte[] buffer;
                int maxBufferSize = 100*1024*1024;

                try {
                    URL endPoint=new URL(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME) +
                            targetURL);

                    HttpURLConnection connection=(HttpURLConnection) endPoint.openConnection();
                    connection.setRequestMethod("POST");

                    connection.setDoOutput(true);
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

                    String cookieString= CookieManager.getInstance().getCookie(getString(R.string.HOST_NETWORK_PROTOCOL) +
                            getString(R.string.HOST_ADDRESS) +
                            getString(R.string.HOST_APP_NAME));

                    if(cookieString != null){
                        connection.setRequestProperty("Cookie", cookieString);
                    }

                    String delimiter = twoHyphens + boundary + lineEnd; // --androidupload\r\n
                    StringBuffer postDataBuilder = new StringBuffer();

                    for (String key : params.keySet()) {
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append("Content-Disposition: form-data; name=\"" + key +"\";filename=\""+lineEnd+lineEnd+params.get(key)+lineEnd);
                    }

                    if(image!=null){
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append("Content-Disposition: form-data; name=\"" + "image" + "\";filename=\"" + image +"\"" + lineEnd);
                    }

                    try{
                        DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
                        ds.write(postDataBuilder.toString().getBytes());

                        if(image!=null){
                            ds.writeBytes(lineEnd);
                            FileInputStream fis = new FileInputStream(image);
                            buffer = new byte[maxBufferSize];
                            int length = -1;
                            while((length=fis.read(buffer)) != -1){
                                ds.write(buffer,0,length);
                            }
                            ds.writeBytes(lineEnd);
                            ds.writeBytes(lineEnd);
                            ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); // requestbody end
                            fis.close();
                        }else{
                            ds.writeBytes(lineEnd);
                            ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); // requestbody end
                        }
                        ds.flush();
                        ds.close();

                    }catch (Exception e){
                        e.printStackTrace();
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
                        Gson gson=new Gson();

                        HashMap<String, String> result=new HashMap<>();
                        result=gson.fromJson(in, result.getClass());

                        final String add_item_result=result.get("add_item_result");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (add_item_result.equals("true")) {
                                    Toast.makeText(AddItemActivity.this, "상품 추가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent=getIntent();
                                    setResult(ItemViewActivity.ADD_REQUEST_CODE, intent);
                                    finish();
                                }
                                else
                                    Toast.makeText(AddItemActivity.this, "상품 추가가 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        // 통신 에러
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddItemActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
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

            if(data==null){
                return;
            }

            String imagePath=getRealPathFromURI(data.getData());
            image=new File(imagePath);

            // Exchangeable image file format
            // 사진 정보
            ExifInterface exif=null;

            try{
                exif=new ExifInterface(imagePath);
            }catch (Exception e){
                e.printStackTrace();
            }

            int exifOrientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree=exifOrientationToDegrees(exifOrientation);

            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            add_item_image.setImageBitmap(rotate(bitmap, exifDegree));
        }
    }

    private Bitmap rotate(Bitmap src, float degree){
        Matrix matrix=new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }

        return 0;
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
