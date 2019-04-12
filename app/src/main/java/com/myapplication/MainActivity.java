package com.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_api)
    EditText etApi;
    @BindView(R.id.et_userid)
    EditText etUserid;
    @BindView(R.id.et_miyao)
    EditText etMiyao;
    @BindView(R.id.et_banknum)
    EditText etBanknum;
    @BindView(R.id.bt_save)
    Button btSave;
    @BindView(R.id.tv)
    TextView tv;
    private SPUtils instance;
    private StringBuilder MSGBuilder = new StringBuilder();
    private boolean editable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(MainActivity.this);
        PermissionUtils.permission(PermissionConstants.SMS).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied() {
            }
        }).request();
        instance = SPUtils.getInstance();
        etApi.setText(instance.getString("Api"));
        etUserid.setText(instance.getString("Userid"));
        etMiyao.setText(instance.getString("Miyao"));
        etBanknum.setText(instance.getString("BankNum"));

        etApi.setFocusable(false);
        etApi.setFocusableInTouchMode(false);
        etBanknum.setFocusable(false);
        etBanknum.setFocusableInTouchMode(false);
        etMiyao.setFocusable(false);
        etMiyao.setFocusableInTouchMode(false);
        etUserid.setFocusable(false);
        etUserid.setFocusableInTouchMode(false);

    }

    @Subscribe
    public void onMsgsEvent(FirstEvent events) {
        MSGBuilder.insert(0, "\r\n");
        MSGBuilder.insert(0, "\r\n");
        MSGBuilder.insert(0, "发送人：" + events.getMsg().getOriginatingAddress() + "，短信内容:" + events.getMsg().getDisplayMessageBody() + "，时间：" + TimeUtils.millis2String(events.getMsg().getTimestampMillis()));
        if (MSGBuilder.length() > 8000) {
            MSGBuilder.substring(0, 4000);
        }
        tv.setText(MSGBuilder);

        String bankNum = instance.getString("BankNum");
        if (ObjectUtils.isEmpty(instance.getString("Api")) || ObjectUtils.isEmpty(instance.getString("Userid")) || ObjectUtils.isEmpty(instance.getString("Miyao")) || ObjectUtils.isEmpty(bankNum)) {

        } else {
            if (bankNum.length() > 4) {
                if (events.getMsg().getDisplayMessageBody().contains(bankNum.substring(bankNum.length() - 3))) {
                    Uploade(events.getMsg());
                }
            }

        }
    }

    private void Uploade(SmsMessage smsMessage) {
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("cardid", instance.getString("BankNum"));
//        parameters.put("content", EncodeUtils.base64Encode2String("卓晓辉于2019-01-09 15:48:57向您账户5240发起12519.00元的汇款。本短信不作入账凭证，请查询您的银行账户确认是否入账。[招商银行]".getBytes()));
        parameters.put("content", EncodeUtils.base64Encode2String(smsMessage.getDisplayMessageBody().getBytes()));
        parameters.put("sendtime", TimeUtils.millis2String(smsMessage.getTimestampMillis()));
        parameters.put("userid", instance.getString("Userid"));
        String mySign = createSign(parameters, instance.getString("Miyao"));

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        PostBean bean = new PostBean();
        bean.setUserid(instance.getString("Userid"));
        bean.setCardid(instance.getString("BankNum"));
        bean.setSendtime(TimeUtils.millis2String(smsMessage.getTimestampMillis()));
        bean.setContent(EncodeUtils.base64Encode2String(smsMessage.getDisplayMessageBody().getBytes()));
        bean.setSign(mySign);

        OkGo.<String>post(instance.getString("Api"))
                .tag(MainActivity.this)
                .upJson(gson.toJson(bean))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String json = response.body();
                            ToastUtils.showShort(json);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });

    }


    @OnClick({R.id.bt_save, R.id.tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                if (editable) {
                    String strApi = etApi.getText().toString().trim();
                    String strUserid = etUserid.getText().toString().trim();
                    String strMiyao = etMiyao.getText().toString().trim();
                    String strBankNum = etBanknum.getText().toString().trim();

                    if (ObjectUtils.isEmpty(strApi) || ObjectUtils.isEmpty(strUserid) || ObjectUtils.isEmpty(strMiyao) || ObjectUtils.isEmpty(strBankNum)) {
                        ToastUtils.showShort("缺少必填参数");
                        return;
                    }
                    instance.put("Api", strApi);
                    instance.put("Userid", strUserid);
                    instance.put("Miyao", strMiyao);
                    instance.put("BankNum", strBankNum);
                    ToastUtils.showShort("保存成功！");
                    btSave.setText("编辑");

                    etApi.setFocusable(false);
                    etApi.setFocusableInTouchMode(false);
                    etBanknum.setFocusable(false);
                    etBanknum.setFocusableInTouchMode(false);
                    etMiyao.setFocusable(false);
                    etMiyao.setFocusableInTouchMode(false);
                    etUserid.setFocusable(false);
                    etUserid.setFocusableInTouchMode(false);

                } else {
                    etApi.setFocusableInTouchMode(true);
                    etApi.setFocusable(true);
                    etApi.requestFocus();
                    etBanknum.setFocusableInTouchMode(true);
                    etBanknum.setFocusable(true);
                    etBanknum.requestFocus();
                    etMiyao.setFocusableInTouchMode(true);
                    etMiyao.setFocusable(true);
                    etMiyao.requestFocus();
                    etUserid.setFocusableInTouchMode(true);
                    etUserid.setFocusable(true);
                    etUserid.requestFocus();
                    btSave.setText("保存");
                }
                editable = !editable;

                break;
            case R.id.tv:
                break;
        }
    }


    public static String createSign(SortedMap<Object, Object> parameters, String key) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbkey = new StringBuffer();
        Set es = parameters.entrySet();  //所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            //空值不传递，不参与签名组串
            if (null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
                sbkey.append(k + "=" + v + "&");
            }
        }
        sbkey = sbkey.append("key=" + key);
        //MD5加密,结果转换为大写字符

        Log.i("test", sbkey.toString());
        return EncryptUtils.encryptMD5ToString(sbkey.toString()).toUpperCase();
    }

}
