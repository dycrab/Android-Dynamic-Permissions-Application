package com.example.callphone;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Permission permission;
    String phoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //String [] permissionStr = {Manifest.permission.CALL_PHONE};
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1000);

        permission = new Permission();
        // 添加需要的权限
        permission.setPermissions(new String [] {Manifest.permission.CALL_PHONE});
        //检查并申请打电话的权限
        permission.checkPermission(MainActivity.this);
        //设置号码
        phoneNum = "5554";
        //拨打电话
        Button button = findViewById(R.id.button1);
        button.setOnClickListener(this);
    }

    public void callPhone(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        callPhone(phoneNum);
    }
}

