package com.jdjz;

import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.weex.plugin.loader.WeexPluginContainer;
import com.jdjz.testConfig.SealConst;
import com.jdjz.weexlib.weex.Component.RichText;
import com.jdjz.weexlib.weex.Component.WXWebJS;
import com.jdjz.weexlib.weex.Component.WXWebJsBridge;
import com.jdjz.weexlib.weex.ImageAdapter;
import com.jdjz.weexlib.weex.util.AppConfig;
import com.jude.utils.JUtils;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
//import com.tencent.smtt.sdk.QbSdk;


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
                //WXSDKEngine.registerModule("event", WXEventModule.class);
                WXSDKEngine.registerComponent("richText", RichText.class);
                WXSDKEngine.registerComponent("wXWebJS", WXWebJS.class);
                WXSDKEngine.registerComponent("wXWebJsBridge", WXWebJsBridge.class);
                //WXSDKEngine.registerComponent("webViewX5Test", WebViewX5.class);

            } catch (WXException e) {
                e.printStackTrace();
            }
            AppConfig.init(this);
            WeexPluginContainer.loadAll(this);


/*            //tbs x5
            QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

                @Override
                public void onViewInitFinished(boolean b) {
                    //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                    Log.d("app", " onViewInitFinished is " + b);
                }

                @Override
                public void onCoreInitFinished() {
                    // TODO Auto-generated method stub
                }
            };

            //x5内核初始化接口
            QbSdk.initX5Environment(getApplicationContext(), cb);*/
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
