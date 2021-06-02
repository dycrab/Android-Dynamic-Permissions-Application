@[TOC](安卓动态权限申请)
# 前言
写这的想法是尽量给没有基础的朋友介绍如何动态的申请安卓的权限。希望大家可以学习新的安卓版本开发，而不是偷懒去下个安卓5去复制粘贴完事。

大二，也是刚开始学安卓，老师用的版本都是安卓4，自己想用安卓10，无奈动态权限申请老师也不讲，只能自己去找。
但是找了很多，都没有找到一个说的比较详细的做法，导致一直没真正理解动态申请是如何实现的，今天去B站看了看，看到一篇专门介绍[如何动态申请安卓权限](https://www.bilibili.com/video/BV1ep4y197Yh)的视频，这才解开了心中的疑惑。


# 一、一行代码实现动态权限申请
其实真正的申请就这一句话，但是有时候会为了判断**权限是否已经赋予**或**SDK版本是否高于23**而写更多的代码。我们都知道当SDK版本大于等于23也就是安卓版本大于等于6的时候，谷歌为了给予用户更多的权限，开启了动态申请权限的时代。
```java
// 三个参数分别是activity、权限字符数组、还有requestCode
// 第一个和第三个不用改变，第二个参数需要改成自己需要的参数
ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1000);
```
这里我将上面的代码用于实现自动打电话。

**下面的代码放进去就可以运行，但是前提是你的清单文件里面已经添加了这个activity。**

**AndroidManifest.xml**(后面有代码可以复制)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210602165209928.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NTc1NTgzMQ==,size_16,color_FFFFFF,t_70)


**MainActivity.java**

```java
package com.example.callphone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity  {

    String phoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //直接打电话 不需要布局文件
        //申请打电话的权限
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1000);
        //设置号码
        phoneNum = "5554";
        //拨打电话
        callPhone(phoneNum);
    }
    public void callPhone(String phoneNum){
    	// 声明intent为打电话
        Intent intent = new Intent(Intent.ACTION_CALL);
        // 设置Uri格式 为打电话
        Uri data = Uri.parse("tel:" + phoneNum);
        // 传入Uri
        intent.setData(data);
        // 实现intent
        startActivity(intent);
    }
}


```

在安卓10版本的手机上运行后，会提示是否开启权限，点击“允许”，就会自动开始打电话。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210602162603469.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NTc1NTgzMQ==,size_16,color_FFFFFF,t_70#pic_center)
# 二、封装权限申请为单独的类
不废话 最多用了两个文件，算上清单文件和布局文件是四个，而且都不超50行。
代码目录：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021060216502851.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NTc1NTgzMQ==,size_16,color_FFFFFF,t_70)



这里我是将权限申请封装成一个Permission类，用于申请不同的权限。
该类的内部代码一般情况下不需要进行修改。

**Permission.java**

```java
package com.example.callphone;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {
	// 需要申请的权限（判断是否有权限前）
    public static String[] permissions;
	// 自定义的requestCode
    public int REQUEST_CODE = 1000;
	// 经过判断后真正需要申请的权限
    public List<String> permissionList = new ArrayList<>();
	// 设置需要申请的权限
    public void setPermissions(String[] permissions) {
        Permission.permissions = permissions;
    }
	// 检查需要的权限是否需要进行申请
	// 1、SDK 版本大于 API 23 
	// 2、是否已经获取该权限
    public void checkPermission(Activity activity){
        // 判断 SDK版本是否大于 API23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        	// loop循环判断
            for (String permission : permissions) {
            	// 2、是否已经获取该权限 未获取就添加进列表
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            // 判断列别是否为非空并申请列表中的权限
            if (permissionList.size() > 0) {
                requestPermission(activity);
            }
        }
    }
	
	//申请权限
    public void requestPermission(Activity activity){
    	// 申请权限
        ActivityCompat.requestPermissions(activity,permissionList.toArray(new String[permissionList.size()]),REQUEST_CODE);
    }

}

```

下面是如何使用该封装类。

**MainActivity.java**
```java
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
        permission = new Permission();
        // 添加需要的权限
        permission.setPermissions(new String [] {Manifest.permission.CALL_PHONE});
        //检查并申请打电话的权限
        permission.checkPermission(MainActivity.this);
        //设置号码
        phoneNum = "5554";
        // 获取按钮并设置监听
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


```

布局文件：
**activity_main.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">


    <Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="150dp"
        android:layout_marginTop="100dp"
        android:layout_marginEnd="150dp"
        android:layout_marginBottom="350dp"
        android:text="@string/button1"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

不要忘记在清单文件里面添加Activity的声明。

**AndroidManifest.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.callphone">
    <uses-permission android:name="android.permission.CALL_PHONE"/>
    <uses-sdk android:minSdkVersion="30" android:maxSdkVersion="30"/>
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.CallPhone">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

该代码运行结果同上。

**希望大家都能去尝试新东西 而不是一直徘徊不前。**




