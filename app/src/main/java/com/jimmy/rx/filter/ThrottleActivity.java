package com.jimmy.rx.filter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jimmy.rx.R;


/**
 * Throttle 没个一段时间内取第一个，一般用于按键规定时间内响应第一次。
 * <p>
 * sample 规定时间内取最后一个。每隔一段时间就进行采样，在时间间隔范围内获取最后一个发布的Observable
 */
public class ThrottleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throttle);
    }
}
