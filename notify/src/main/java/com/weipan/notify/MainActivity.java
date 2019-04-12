package com.weipan.notify;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.weipan.notify.Util.MSG;
import static com.weipan.notify.Util.MSGBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private EditText et;
    private EditText et_key;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private LinearLayout llMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(MainActivity.this);


        LogToFile.init(MainActivity.this);


        sp = getSharedPreferences("apikey", 0);

        editor = sp.edit();

        String string = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (!string.contains(NotificationCollectorService.class.getName())) {
            startActivity(new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }

        llMain = (LinearLayout) findViewById(R.id.ll_main);
        tv = (TextView) findViewById(R.id.tv);
        et = (EditText) findViewById(R.id.et);
        et_key = (EditText) findViewById(R.id.et_key);
        et.setText(sp.getString("api", ""));
        et_key.setText(sp.getString("key", ""));

        if (sp.getString("api", "") != "" && sp.getString("key", "") != "") {
            llMain.setVisibility(View.INVISIBLE);
        }
        Button bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_key.getText().toString().length() < 8) {
                    Toast.makeText(MainActivity.this, "key长短必须大于八位", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString("api", et.getText().toString());
                    editor.putString("key", et_key.getText().toString());
                    editor.commit();
                    if (sp.getString("api", "") != "" && sp.getString("key", "") != "") {
                        llMain.setVisibility(View.INVISIBLE);
                    }
                    test("api:" + et.getText().toString() + ",key:" + et_key.getText().toString(), et.getText().toString(), et_key.getText().toString());

                    Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        isNotificationListenersEnabled();
        gotoNotificationAccessSetting(MainActivity.this);
        toggleNotificationListenerService(MainActivity.this);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
                .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    @Override
    protected void onResume() {
        super.onResume();
        tv.setText(MSGBuilder.toString());

        toggleNotificationListenerService(MainActivity.this);
    }

    @Subscribe
    public void onMsgsEvent(FirstEvent events) {
        LogToFile.i("log", events.getMsg());
        if (!TextUtils.isEmpty(et.getText().toString()) && !TextUtils.isEmpty(et_key.getText().toString())) {
            test(events.getMsg(), et.getText().toString(), et_key.getText().toString());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/log/");
                        if (!file.exists()) {
                            Log.d("jim", "path1 create:" + file.mkdirs());
                        }
                    }
                    break;
                }
        }
    }

    private void test(final String msg, String url, String key) {
//        String url = "http://a.info6s.com/addons/pay/api/msg";
        tv.setText(msg);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        Map<String, Object> map = new HashMap<>();
        map.put("type", "1");
        map.put("msg", msg);
        map.put("key", key);
        map.put("time", simpleDateFormat.format(date));
        final JSONObject obj = new JSONObject(map);

        Log.i("test", "请求参数：" + obj.toString());
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, obj.toString());
        Request request = new Request.Builder().url(url).post(body).build();
        HttpUtils.postAsyn(this, request, new HttpUtils.CallBack() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("test", "请求失败");
            }

            @Override
            public void onResponse(String json) {
                Log.e("test", json);
                try {
                    JSONObject object = new JSONObject(json);

                    if (object.optInt("Result") > 0) {

                        tv.setText(msg + "/r/n发送成功！");
                    } else {

                        tv.setText(msg + "/r/n发送失败！");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("test", "请求失败");
                    tv.setText(msg + "/r/n请求失败！");
                }
            }
        });
    }

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    private boolean isNotificationListenersEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;

        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(context, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }


    public static void toggleNotificationListenerService(Context context) {
        Log.e("", "toggleNotificationListenerService");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, NotificationCollectorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, NotificationCollectorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
