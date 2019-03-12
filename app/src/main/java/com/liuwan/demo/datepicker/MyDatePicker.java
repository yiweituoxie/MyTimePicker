package com.liuwan.demo.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.liuwan.demo.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MyDatePicker implements View.OnClickListener, PickerView.OnSelectListener {

    private Context mContext;
    private Callback mCallback;
    private Calendar mCurrentTime, mSelectedTime;
    private boolean mCanDialogShow;

    private Dialog mPickerDialog;
    private PickerView mDpvHour, mDpvMinute, mDpvSecond;

    private int mBeginHour, mBeginMinute, mBeginSecond;
    private List<String> mHourUnits = new ArrayList<>(), mMinuteUnits = new ArrayList<>(), mSecondUnits = new ArrayList<>();
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");


    /**
     * 时间单位的最大显示值
     */
    private static final int MAX_SECOND_UNIT = 59;
    private static final int MAX_MINUTE_UNIT = 59;
    private static final int MAX_HOUR_UNIT = 23;

    /**
     * 级联滚动延迟时间
     */
    private static final long LINKAGE_DELAY_DEFAULT = 100L;

    /**
     * 时间选择结果回调接口
     */
    public interface Callback {
        void onTimeSelected(long timestamp);
    }

    /**
     * 通过日期字符串初始换时间选择器
     *
     * @param context  Activity Context
     * @param callback 选择结果回调
     */
    public MyDatePicker(Context context, Callback callback, String currentTime) {
        this(context, callback, DateFormatUtils.str2Long(currentTime, true));
    }

    /**
     * 通过时间戳初始换时间选择器，毫秒级别
     *
     * @param context  Activity Context
     * @param callback 选择结果回调
     */
    public MyDatePicker(Context context, Callback callback, long currentTimestamp) {
        if (context == null || callback == null) {
            mCanDialogShow = false;
            return;
        }

        mContext = context;
        mCallback = callback;
        mSelectedTime = Calendar.getInstance();
        mCurrentTime = Calendar.getInstance();
        mCurrentTime.setTimeInMillis(currentTimestamp);

        initView();
        initData();
        mCanDialogShow = true;
    }

    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.date_picker_dialog);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.dialog_time_picker);

        Window window = mPickerDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        mPickerDialog.findViewById(R.id.tv_cancel).setOnClickListener(this);
        mPickerDialog.findViewById(R.id.tv_confirm).setOnClickListener(this);

        mDpvHour = mPickerDialog.findViewById(R.id.dpv_hour);
        mDpvHour.setOnSelectListener(this);
        mDpvMinute = mPickerDialog.findViewById(R.id.dpv_minute);
        mDpvMinute.setOnSelectListener(this);
        mDpvSecond = mPickerDialog.findViewById(R.id.dpv_second);
        mDpvSecond.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                break;

            case R.id.tv_confirm:
                if (mCallback != null) {
                    mCallback.onTimeSelected(mSelectedTime.getTimeInMillis());
                }
                break;
        }

        if (mPickerDialog != null && mPickerDialog.isShowing()) {
            mPickerDialog.dismiss();
        }
    }

    @Override
    public void onSelect(View view, String selected) {
        if (view == null || TextUtils.isEmpty(selected)) return;

        int timeUnit;
        try {
            timeUnit = Integer.parseInt(selected);
        } catch (Throwable ignored) {
            return;
        }

        switch (view.getId()) {
            case R.id.dpv_hour:
                mSelectedTime.set(Calendar.HOUR_OF_DAY, timeUnit);
                break;

            case R.id.dpv_minute:
                mSelectedTime.set(Calendar.MINUTE, timeUnit);
                break;
            case R.id.dpv_second:
                mSelectedTime.set(Calendar.SECOND, timeUnit);
                break;
        }
    }

    private void initData() {
        mSelectedTime.setTimeInMillis(mCurrentTime.getTimeInMillis());

        mBeginHour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        mBeginMinute = mCurrentTime.get(Calendar.MINUTE);
        mBeginSecond = mCurrentTime.get(Calendar.SECOND);

        initDateUnits();
    }

    private void initDateUnits() {

        mHourUnits.add(mDecimalFormat.format(mBeginHour));

        mMinuteUnits.add(mDecimalFormat.format(mBeginMinute));

        mSecondUnits.add(mDecimalFormat.format(mBeginSecond));

        mDpvHour.setDataList(mHourUnits);
        mDpvHour.setSelected(0);
        mDpvMinute.setDataList(mMinuteUnits);
        mDpvMinute.setSelected(0);
        mDpvSecond.setDataList(mSecondUnits);
        mDpvSecond.setSelected(0);

        setCanScroll();
    }

    private void setCanScroll() {
        mDpvHour.setCanScroll(true);
        mDpvMinute.setCanScroll(true);
        mDpvSecond.setCanScroll(true);
    }

    /**
     * 联动“时”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageHourUnit(final boolean showAnim, final long delay) {
        int minHour = 0;
        int maxHour = MAX_HOUR_UNIT;

        mHourUnits.clear();
        for (int i = minHour; i <= maxHour; i++) {
            mHourUnits.add(mDecimalFormat.format(i));
        }
        mDpvHour.setDataList(mHourUnits);

        int selectedHour = getValueInRange(mSelectedTime.get(Calendar.HOUR_OF_DAY), minHour, maxHour);
        mSelectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
        mDpvHour.setSelected(selectedHour - minHour);
        if (showAnim) {
            mDpvHour.startAnim();
        }

        mDpvHour.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageMinuteUnit(showAnim, delay);
            }
        }, delay);
    }

    /**
     * 联动“分”变化
     *
     * @param showAnim 是否展示滚动动画
     */
    private void linkageMinuteUnit(final boolean showAnim, final long delay) {
        int minMinute = 0;
        int maxMinute = MAX_MINUTE_UNIT;
        mMinuteUnits.clear();
        for (int i = minMinute; i <= maxMinute; i++) {
            mMinuteUnits.add(mDecimalFormat.format(i));
        }
        mDpvMinute.setDataList(mMinuteUnits);

        int selectedMinute = getValueInRange(mSelectedTime.get(Calendar.MINUTE), minMinute, maxMinute);
        mSelectedTime.set(Calendar.MINUTE, selectedMinute);
        mDpvMinute.setSelected(selectedMinute - minMinute);
        if (showAnim) {
            mDpvMinute.startAnim();
        }
        mDpvMinute.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageSecondUnit(showAnim);
            }
        }, delay);
    }

    /**
     * 联动“秒”变化
     *
     * @param showAnim 是否展示滚动动画
     */
    private void linkageSecondUnit(final boolean showAnim) {
        int minSecond = 0;
        int maxSecond = MAX_SECOND_UNIT;
        mSecondUnits.clear();
        for (int i = minSecond; i <= maxSecond; i++) {
            mSecondUnits.add(mDecimalFormat.format(i));
        }
        mDpvSecond.setDataList(mSecondUnits);

        int selectedSecond = getValueInRange(mSelectedTime.get(Calendar.SECOND), minSecond, maxSecond);
        mSelectedTime.set(Calendar.SECOND, selectedSecond);
        mDpvSecond.setSelected(selectedSecond - minSecond);
        if (showAnim) {
            mDpvSecond.startAnim();
        }

        setCanScroll();
    }

    private int getValueInRange(int value, int minValue, int maxValue) {
        if (value < minValue) {
            return minValue;
        } else if (value > maxValue) {
            return maxValue;
        } else {
            return value;
        }
    }

    /**
     * 展示时间选择器
     *
     * @param dateStr 日期字符串，格式为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm
     */
    public void show(String dateStr) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) return;

        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedTime(dateStr, false)) {
            mPickerDialog.show();
        }
    }

    private boolean canShow() {
        return mCanDialogShow && mPickerDialog != null;
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param dateStr  日期字符串
     * @param showAnim 是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedTime(String dateStr, boolean showAnim) {
        return canShow() && !TextUtils.isEmpty(dateStr)
                && setSelectedTime(DateFormatUtils.str2Long2(dateStr), showAnim);
    }

    /**
     * 展示时间选择器
     *
     * @param timestamp 时间戳，毫秒级别
     */
    public void show(long timestamp) {
        if (!canShow()) return;

        if (setSelectedTime(timestamp, false)) {
            mPickerDialog.show();
        }
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param timestamp 毫秒级时间戳
     * @param showAnim  是否展示动画
     * @return 是否设置成功
     */
    public boolean setSelectedTime(long timestamp, boolean showAnim) {
        if (!canShow()) return false;

        mSelectedTime.setTimeInMillis(timestamp);
        linkageHourUnit(showAnim, showAnim ? LINKAGE_DELAY_DEFAULT : 0);
        return true;
    }

    /**
     * 设置是否允许点击屏幕或物理返回键关闭
     */
    public void setCancelable(boolean cancelable) {
        if (!canShow()) return;

        mPickerDialog.setCancelable(cancelable);
    }


    /**
     * 设置日期控件是否可以循环滚动
     */
    public void setScrollLoop(boolean canLoop) {
        if (!canShow()) return;

        mDpvHour.setCanScrollLoop(canLoop);
        mDpvMinute.setCanScrollLoop(canLoop);
    }

    /**
     * 设置日期控件是否展示滚动动画
     */
    public void setCanShowAnim(boolean canShowAnim) {
        if (!canShow()) return;

        mDpvHour.setCanShowAnim(canShowAnim);
        mDpvMinute.setCanShowAnim(canShowAnim);
    }

    /**
     * 销毁弹窗
     */
    public void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;

            mDpvHour.onDestroy();
            mDpvMinute.onDestroy();
        }
    }

}
