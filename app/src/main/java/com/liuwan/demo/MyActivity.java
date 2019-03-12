package com.liuwan.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.liuwan.demo.datepicker.CustomDatePicker;
import com.liuwan.demo.datepicker.DateFormatUtils;
import com.liuwan.demo.datepicker.MyDatePicker;


public class MyActivity extends Activity implements View.OnClickListener {

    private TextView mTvMySelectedTime;
    private MyDatePicker mMyTimerPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        findViewById(R.id.ll_my_time).setOnClickListener(this);
        mTvMySelectedTime = findViewById(R.id.tv_my_selected_time);
        initMyTimerPicker();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_my_time:
                // 日期格式为HH:mm:ss
                mMyTimerPicker.show(mTvMySelectedTime.getText().toString());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyTimerPicker.onDestroy();
    }

    private void initMyTimerPicker() {
        String currentTime = DateFormatUtils.long2Str2(System.currentTimeMillis(), true);

        mTvMySelectedTime.setText(currentTime);

        // 通过日期字符串初始化日期，格式请用：HH:mm:ss
        mMyTimerPicker = new MyDatePicker(this, new MyDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mTvMySelectedTime.setText(DateFormatUtils.long2Str2(timestamp, true));
            }
        },  currentTime);
        // 允许点击屏幕或物理返回键关闭
        mMyTimerPicker.setCancelable(true);
        // 显示时和分
        mMyTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mMyTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mMyTimerPicker.setCanShowAnim(true);
    }

}
