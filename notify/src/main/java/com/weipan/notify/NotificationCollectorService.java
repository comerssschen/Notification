package com.weipan.notify;

import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 作者：Created by cc on 2018/8/24 17:27.
 * 邮箱：904359289@QQ.com.
 * 类 ：
 */

public class NotificationCollectorService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("xiaolong", "open" + "-----" + sbn.getPackageName());
        Log.i("xiaolong", "open" + "------" + sbn.getNotification().tickerText);
        Bundle extras = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            extras = sbn.getNotification().extras;
        }
        Log.i("xiaolong", "open" + "-----" + extras.get("android.title"));
        Log.i("xiaolong", "open" + "-----" + extras.get("android.text"));
        if (extras.get("android.text") != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("PackageName", sbn.getPackageName());
            map.put("Notification", sbn.getNotification().tickerText);
            map.put("title", extras.get("android.title").toString());
            map.put("text", extras.get("android.text").toString());
            JSONObject obj = new JSONObject(map);
            EventBus.getDefault().post(new FirstEvent(obj.toString()));
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("xiaolong", "remove" + "-----" + sbn.getPackageName());

    }
}
