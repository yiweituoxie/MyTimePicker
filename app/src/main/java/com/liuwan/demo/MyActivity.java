package com.liuwan.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwan.demo.datepicker.CustomDatePicker;
import com.liuwan.demo.datepicker.DateFormatUtils;
import com.liuwan.demo.datepicker.MyDatePicker;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity implements View.OnClickListener {

    private TextView mTvMySelectedTime;
    private MyDatePicker mMyTimerPicker;
    private RecyclerView mRecyclerView;
    private TimeAdapter mTimeAdapter;
    private List<String> mTimeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        findViewById(R.id.ll_my_time).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        mTvMySelectedTime = findViewById(R.id.tv_my_selected_time);
        mRecyclerView = findViewById(R.id.recycler_view);
        initMyTimerPicker();
        initRecycleView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_my_time:
                // 日期格式为HH:mm:ss
                mMyTimerPicker.show(mTvMySelectedTime.getText().toString());
                break;
            case R.id.btn_send:
                // 日期格式为HH:mm:ss
                if (mTimeList.size() == 0) {
                    Toast.makeText(MyActivity.this, "没有数据发送", Toast.LENGTH_LONG).show();
                } else {
                    String code = transTimeList();
                    Toast.makeText(MyActivity.this, code, Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyTimerPicker.onDestroy();
    }

    private void initMyTimerPicker() {
        String currentTime = DateFormatUtils.long2Str2(System.currentTimeMillis());

        mTvMySelectedTime.setText(currentTime);

        // 通过日期字符串初始化日期，格式请用：HH:mm:ss
        mMyTimerPicker = new MyDatePicker(this, new MyDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                String time = DateFormatUtils.long2Str2(timestamp);
                mTvMySelectedTime.setText(time);
                if (mTimeList.contains(time)) return;
                mTimeAdapter.addData(time);

            }
        }, currentTime);
        // 允许点击屏幕或物理返回键关闭
        mMyTimerPicker.setCancelable(true);
        // 允许循环滚动
        mMyTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mMyTimerPicker.setCanShowAnim(true);
    }

    private void initRecycleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  //线性布局
        mRecyclerView.setHasFixedSize(true);

        mTimeList = new ArrayList<>();

        mTimeAdapter = new TimeAdapter(mTimeList);
        mRecyclerView.setAdapter(mTimeAdapter);
    }

    /**
     * 讲mTimeList转换成字符串
     */
    private String transTimeList() {
        StringBuilder sb = new StringBuilder();
        for (String time : mTimeList) {
            sb.append(time.replace(":", ""));
        }
        return sb.toString();
    }
}
