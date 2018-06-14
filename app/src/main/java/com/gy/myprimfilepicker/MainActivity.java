package com.gy.myprimfilepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import picker.prim.com.primpicker_core.PrimPicker;
import picker.prim.com.primpicker_core.engine.ImageEngine;
import picker.prim.com.primpicker_core.entity.MediaItem;
import picker.prim.com.primpicker_core.entity.MimeType;

public class MainActivity extends AppCompatActivity {

    private EditText spaneditText;
    private EditText maxEditText;
    private ImageView iv_one;
    private TextView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }
        findViewById(R.id.btn_video).setOnClickListener(listener);
        findViewById(R.id.btn_img).setOnClickListener(listener);
        findViewById(R.id.btn_all).setOnClickListener(listener);
        findViewById(R.id.btn_current_add).setOnClickListener(listener);
        findViewById(R.id.iv_one).setOnClickListener(listener);
        spaneditText=findViewById(R.id.et_spanCount);
        maxEditText=findViewById(R.id.et_maxCount);
        iv_one=findViewById(R.id.iv_one);
        result=findViewById(R.id.result);
    }

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             switch (v.getId()){
                 case R.id.btn_video:
                     gotoVideo();
                     break;
                 case R.id.btn_img:
                      goImg();
                     break;
                 case R.id.btn_all:
                     goAll();
                     break;
                 case R.id.btn_current_add:
                     goadd();
                     break;
                 case R.id.iv_one:

                     PrimPicker
                             .with(MainActivity.this)
                             .preview(MimeType.ofImage())
                             .setImageLoader(new ImageLoader())
                             .setPreviewItems(list)
                             .lastGo(1001);

                     break;
             }
        }
    };



    private void gotoVideo(){
        String span=spaneditText.getText().toString();
        String max=maxEditText.getText().toString();

        if (TextUtils.isEmpty(span)) {
            span = "3";
        }

        if (TextUtils.isEmpty(max)) {
            max = "1";
        }
        PrimPicker
                .with(this)
                .choose(MimeType.ofVideo())
                .setSpanCount(Integer.parseInt(span))
                .setMaxSelected(Integer.parseInt(max))
                .setImageLoader(new ImageLoader())
                .setShowSingleMediaType(true)
                .setCapture(true)
                .lastGo(1001);
    }

    private void goImg() {
        String span = spaneditText.getText().toString();
        String  max = maxEditText.getText().toString();
        if (TextUtils.isEmpty(span)) {
            span = "3";
        }

        if (TextUtils.isEmpty(max)) {
            max = "1";
        }
        PrimPicker
                .with(this)
                .choose(MimeType.ofImage())
                .setSpanCount(Integer.parseInt(span))
                .setMaxSelected(Integer.parseInt(max))
                .setImageLoader(new ImageLoader())
                .setShowSingleMediaType(true)
                .setCapture(true)
                .lastGo(1001);
    }

    private void goAll() {
        String span = spaneditText.getText().toString();
        String max = maxEditText.getText().toString();
        if (TextUtils.isEmpty(span)) {
            span = "3";
        }

        if (TextUtils.isEmpty(max)) {
            max = "1";
        }
        PrimPicker
                .with(this)
                .choose(MimeType.ofAll())
                .setCapture(true)
                .setSpanCount(Integer.parseInt(span))
                .setImageLoader(new ImageLoader())
                .setMaxSelected(Integer.parseInt(max))
                .setShowSingleMediaType(true)
                .lastGo(1001);
    }

    private void goadd() {
        PrimPicker
                .with(this)
                .choose(MimeType.ofImage())
                .setSpanCount(3)
                .setMaxSelected(9)
                .setImageLoader(new ImageLoader())
                .setShowSingleMediaType(true)
                .setCapture(true)
                .setDefaultItems(list)
                .lastGo(1001);
    }

    ArrayList<MediaItem> list=new ArrayList<>();
    List<String> pathList;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            StringBuffer str=new StringBuffer();
            list=PrimPicker.obtainItemsResult(data);
            pathList=PrimPicker.obtainPathResult(data);
            if (pathList!=null&&!pathList.isEmpty()){
                iv_one.setVisibility(View.VISIBLE);
                Glide.with(this).load("file://" +pathList.get(0)).into(iv_one);
            }else {
                iv_one.setVisibility(View.GONE);
            }
            str.append("返回结果:").append("Uri:").append("\n");
            for (Uri uri:PrimPicker.obtainUriResult(data)){
                str.append(uri).append("\n");
            }
            str.append("Path:").append("\n");
            for (String s:PrimPicker.obtainPathResult(data)){
                str.append(s).append("\n");
            }
            if (PrimPicker.obtainCompressResult(data)){
                str.append("压缩视频");
            }else {
                str.append("不压缩视频");
            }
            result.setText(str.toString());
        }
    }

    class ImageLoader implements ImageEngine {

        @Override
        public void loadImageThumbnail(Context context, int resize, Drawable placeholder, ImageView view, Uri uri) {
            Glide.with(context).load(uri).asBitmap().placeholder(placeholder).override(resize, resize).centerCrop().into(view);
        }

        @Override
        public void loadImage(Context context, int resizeX, int resizeY, Drawable placeholder, ImageView view, Uri uri) {
            Glide.with(context).load(uri).asBitmap().placeholder(placeholder).override(resizeX, resizeY).fitCenter().into(view);
        }

        @Override
        public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView view, Uri uri) {
            Glide.with(context).load(uri).asGif().placeholder(placeholder).override(resize, resize).centerCrop().into(view);
        }

        @Override
        public void loadGifImage(Context context, int resizeX, int resizeY, Drawable placeholder, ImageView view, Uri uri) {
            Glide.with(context).load(uri).asGif().placeholder(placeholder).override(resizeX, resizeY).into(view);
        }
    }
}
