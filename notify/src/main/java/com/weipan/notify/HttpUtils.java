package com.weipan.notify;

import android.app.Activity;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Name: HttpUtils
 * Author: cc
 * Comment: //TODO 网络请求工具类
 * Date: 2016-10-08 19:38
 * Time: 19:38
 * FIXME
 */
public class HttpUtils {
    public static final OkHttpClient okhttp = new OkHttpClient();
    static {
        okhttp.setConnectTimeout(30, TimeUnit.SECONDS);
        okhttp.setWriteTimeout(20, TimeUnit.SECONDS);
        okhttp.setReadTimeout(30, TimeUnit.SECONDS);
    }
    public interface CallBack {
        void onFailure(Request request, IOException e);
        void onResponse(String json);
    }
    /****
     * get异步请求
     * @param activity
     * @param request
     * @param callback
     */
    public static void getAsyn(final Activity activity, Request request, final CallBack callback) {
        final String url = request.urlString();
        try {
            okhttp.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(request, e);
                        }
                    });
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    final String json = response.body().string();

                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(json);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /***
     * post异步请求
     * @param activity
     * @param request
     * @param callback
     */
    public static void postAsyn(final Activity activity, Request request, final CallBack callback) {
        final String url = request.urlString();
        try {
            okhttp.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(request, e);
                        }
                    });
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    final String json = response.body().string();

                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(json);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * put异步请求方式
     * @param activity
     * @param request
     * @param callback
     */
    public static void putAsyn(final Activity activity, Request request, final CallBack callback) {
        final String url = request.urlString();
        try {
            okhttp.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(request, e);
                        }
                    });
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    final String json = response.body().string();
                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(json);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}