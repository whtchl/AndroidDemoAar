package com.jdjz;

import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.alibaba.weex.plugin.loader.WeexPluginContainer;
import com.jdjz.testConfig.SealConst;
import com.jdjz.weex.Component.RichText;
import com.jdjz.weex.Component.WebViewTest;
import com.jdjz.weex.ImageAdapter;
import com.jdjz.weex.extend.WXEventModule;
import com.jdjz.weex.util.AppConfig;
import com.jude.utils.JUtils;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;


public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
         //考虑多进程的情况
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

            //初始化JUtils
            JUtils.initialize(this);
            JUtils.setDebug(true, "tchl");

            //db
            SealConst.setSharepreferenct();
            SealAppContext.init(this);
            openSealDBIfHasCachedToken();

            //weex
            /*InitConfig config=new InitConfig.Builder().setImgAdapter(new ImageAdapter()).build();
            WXSDKEngine.initialize(this,config);*/
            WXSDKEngine.addCustomOptions("appName", "WXSample");
            WXSDKEngine.addCustomOptions("appGroup", "WXApp");
            WXSDKEngine.initialize(this,
                    new InitConfig.Builder().setImgAdapter(new ImageAdapter()).build()
            );
            try {
                WXSDKEngine.registerModule("event", WXEventModule.class);
                WXSDKEngine.registerComponent("richText", RichText.class);
                WXSDKEngine.registerComponent("webViewTest", WebViewTest.class);
            } catch (WXException e) {
                e.printStackTrace();
            }
            AppConfig.init(this);
            WeexPluginContainer.loadAll(this);
        }

    }



    private void openSealDBIfHasCachedToken() {

        String cachedToken = JUtils.getSharedPreference().getString("loginToken","");//sp.getString("loginToken", "");
        if (!TextUtils.isEmpty(cachedToken)) {
            String current = getCurProcessName(this);
            String mainProcessName = getPackageName();
            if (mainProcessName.equals(current)) {

                SealUserInfoManager.getInstance().openDB();
            }
        }
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
