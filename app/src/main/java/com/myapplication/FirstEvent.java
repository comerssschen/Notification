package com.myapplication;

import android.telephony.SmsMessage;

/**
 * 作者：Created by cc on 2018/8/24 17:50.
 * 邮箱：904359289@QQ.com.
 * 类 ：
 */

public class FirstEvent {
    private SmsMessage mMsg;

    public FirstEvent(SmsMessage msg) {
        mMsg = msg;
    }

    public SmsMessage getMsg() {
        return mMsg;
    }
}
