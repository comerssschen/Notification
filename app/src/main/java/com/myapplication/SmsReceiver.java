package com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * 作者：create by comersss on 2019/1/29 17:47
 * 邮箱：904359289@qq.com
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Log.i("test", "SmsReceiver---------" + "address:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() +
                        "  time:" + msg.getTimestampMillis());
                EventBus.getDefault().post(new FirstEvent(msg));
            }
        }
    }
}
