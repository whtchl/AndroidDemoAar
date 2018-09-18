package com.jdjz;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.jdjz.testConfig.SealConst;
import com.jude.utils.JUtils;

import java.time.temporal.JulianFields;

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
         //考虑多进程的情况
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

            //初始化JUtils
            JUtils.initialize(this);
            JUtils.setDebug(true, "tchl");
            SealConst.setSharepreferenct();
            SealAppContext.init(this);
            openSealDBIfHasCachedToken();
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
