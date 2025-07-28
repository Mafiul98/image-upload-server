package com.example.imageupload;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imageview,imageedit;
    Button uploadbutton;
    TextView tvdisplay;

    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageview=findViewById(R.id.imageview);
        imageedit=findViewById(R.id.imageedit);
        uploadbutton=findViewById(R.id.uploadbutton);
        tvdisplay=findViewById(R.id.tvdisplay);
        progressbar=findViewById(R.id.progressbar);

        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageview.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,outputStream);

                byte[] imagebyte = outputStream.toByteArray();
                String image64 = Base64.encodeToString(imagebyte, Base64.NO_WRAP);
                tvdisplay.setText(image64);

                stringrequest(image64);

            }
        });


        imageedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                galaryLauncher.launch(intent);

                 */


                if (ChackcameraPermission()){
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Cameralauncher.launch(intent);
                }
            }
        });

    }

    //===========================CameraPermission====================================

    private boolean ChackcameraPermission(){
        boolean haspermission = false;

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            haspermission = true;
        }else {
            haspermission = false;
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},105);
        }

        return haspermission;

    }

    //=======================CameraLanucher==========================================
    ActivityResultLauncher<Intent> Cameralauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK){
                        tvdisplay.setText("Image Capture");
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        imageview.setImageBitmap(bitmap);
                    }else {
                        tvdisplay.setText("Image not Capture");
                    }

                }
            });

    //======================gallery launcher======================================
    ActivityResultLauncher<Intent> galaryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        tvdisplay.setText("Image selected");
                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            imageview.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                    }else {
                        tvdisplay.setText("image not selected");
                    }


                }
            });

//=====================Stringrequest==================================================
    private void stringrequest(String image64){
        progressbar.setVisibility(VISIBLE);
        String url = "https://mafiul.shop/file.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                tvdisplay.setText(response);
                progressbar.setVisibility(GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                tvdisplay.setText(error.getMessage());
                progressbar.setVisibility(GONE);

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map mymap = new HashMap<String,String>();
                mymap.put("pass","123456");
                mymap.put("email","mafi@gmail.com");
                mymap.put("image",image64);

                return mymap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);


    }




}