package com.example.sango.thegift;

import android.os.Message;
import android.os.Handler;

/**
 * Created by 104525006 on 2016/4/7.
 */
public class MyHandler extends Handler {
    private MainActivity mainActivity;
    public MyHandler(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
    }

    @Override
    public void handleMessage(Message msg) {
        String str = msg.getData().getString("msg");
        this.mainActivity.showCard(str);
    }
}
