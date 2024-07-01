package com.ciot.deliverywear.ui.widgets;
import android.view.View;

public abstract class CustomClickListener implements View.OnClickListener{
    private long mLastClickTime;
    private long timeInterval = 1000L;

    public CustomClickListener() {

    }

    public CustomClickListener(long interval) {
        this.timeInterval = interval;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();

        if (nowTime - mLastClickTime > timeInterval) {
            // 点击一次
            mLastClickTime = nowTime;
            onSingleClick(v);
        }
        /*else {
            // 快速且连续
            onFastClick();
        }*/
    }

    protected abstract void onSingleClick(View v);
    //protected abstract void onFastClick();
}