package com.jude.utils;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.temporal.JulianFields;
import java.util.List;
import java.util.Locale;

public class JUtils {
	public static final int NETWORK_NONE = 0; // 没有网络连接
	public static final int NETWORK_WIFI = 1; // wifi连接
	public static final int NETWORK_2G = 2; // 2G
	public static final int NETWORK_3G = 3; // 3G
	public static final int NETWORK_4G = 4; // 4G
	public static final int NETWORK_MOBILE = 5; // 手机流量



	public static String TAG;
	public static boolean DEBUG = false;
	private static Application mApplicationContent;

	public static void initialize(Application app){
		mApplicationContent = app;
	}

	
	public static void setDebug(boolean isDebug,String TAG){
		JUtils.TAG = TAG;
		JUtils.DEBUG = isDebug;
	}

	public static Application getApplication(){
		return mApplicationContent;
	}


	public static void Log(String TAG,String text){
		if(DEBUG){
			Log.i(TAG, text);
		}
	}

	public static void Log(String text){
		if(DEBUG){
			Log.i(TAG, text);
		}
	}


	public static void Toast(String text){
            android.widget.Toast.makeText(mApplicationContent, text, android.widget.Toast.LENGTH_SHORT).show();
	}

	public static void ToastLong(String text){
            android.widget.Toast.makeText(mApplicationContent, text, android.widget.Toast.LENGTH_LONG).show();
	}


	/** 
	 * dp转px
	 * 
	 */  
	public static int dip2px(float dpValue) {  
		final float scale = mApplicationContent.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);  
	} 


	/** 
	 *	px转dp
	 */  
	public static int px2dip(float pxValue) {  
		final float scale = mApplicationContent.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);  
	}


	/**
	 * 取屏幕宽度
	 * @return
	 */
	public static int getScreenWidth(){
		DisplayMetrics dm = mApplicationContent.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * 取屏幕高度
	 * @return
	 */
	public static int getScreenHeight(){
		DisplayMetrics dm = mApplicationContent.getResources().getDisplayMetrics();
		return dm.heightPixels-getStatusBarHeight();
	}

	/**
	 * 获取当前手机系统语言。
	 *
	 * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
	 */
	public static String getSystemLanguage() {
		return Locale.getDefault().getLanguage();
	}

	/**
	 * 获取手机型号
	 *
	 * @return  手机型号
	 */
	public static String getSystemModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取手机厂商
	 *
	 * @return  手机厂商
	 */
	public static String getDeviceBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * 获取当前手机系统版本号
	 *
	 * @return  系统版本号
	 */
	public static String getSystemVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 取屏幕高度包含状态栏高度
	 * @return
	 */
	public static int getScreenHeightWithStatusBar(){
		DisplayMetrics dm = mApplicationContent.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * 取导航栏高度
	 * @return
	 */
	public static int getNavigationBarHeight() {
		int result = 0;
		int resourceId = mApplicationContent.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = mApplicationContent.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 获取设备像素比： 设备像素（物理像素）除以 设备独立像素
	 * 设备像素：即是分辨率 720*1080
	 * 在Android上，设备独立像素==设备像素
	 */
	public static int getPixelRatio(){
		//DisplayMetrics dm = mApplicationContent.getResources().getDisplayMetrics();
		//dm.widthPixels/dm.widthPixels
		return 1;
	}

	/**
	 * 取状态栏高度
	 * @return
	 */
	public static int getStatusBarHeight() {
		int result = 0;
		int resourceId = mApplicationContent.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = mApplicationContent.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

    public static int getActionBarHeight() {
		int actionBarHeight = 0;

		final TypedValue tv = new TypedValue();
        if (mApplicationContent.getTheme()
                .resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
					tv.data, mApplicationContent.getResources().getDisplayMetrics());
        }
		return actionBarHeight;
	}


	/**
	 * 关闭输入法
	 * @param act
	 */
	public static void closeInputMethod(Activity act){
		View view = act.getCurrentFocus();
		if(view!=null){
			((InputMethodManager)mApplicationContent.getSystemService(Context.INPUT_METHOD_SERVICE)).
			hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}


	/**
	 * 判断应用是否处于后台状态
	 * @return
	 */
	public static boolean isBackground() {
		ActivityManager am = (ActivityManager) mApplicationContent.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(mApplicationContent.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 复制文本到剪贴板
	 * @param text
	 */
	public static void copyToClipboard(String text){
		if(Build.VERSION.SDK_INT >= 11){
			ClipboardManager cbm = (ClipboardManager) mApplicationContent.getSystemService(Activity.CLIPBOARD_SERVICE);
			cbm.setPrimaryClip(ClipData.newPlainText(mApplicationContent.getPackageName(), text));
		}else {
			android.text.ClipboardManager cbm = (android.text.ClipboardManager) mApplicationContent.getSystemService(Activity.CLIPBOARD_SERVICE);
			cbm.setText(text);
		}
	}

	/**
	 * 获取SharedPreferences
	 * @return SharedPreferences
	 */
	public static SharedPreferences getSharedPreference() {
		return mApplicationContent.getSharedPreferences(mApplicationContent.getPackageName(), Activity.MODE_PRIVATE);
	}

	/**
	 * 获取SharedPreferences
	 * @return SharedPreferences
	 */
	public static SharedPreferences getSharedPreference(String name) {
		return mApplicationContent.getSharedPreferences(name, Activity.MODE_PRIVATE);
	}

	/**
	 * 获取SharedPreferences
	 * @return SharedPreferences
	 */
	public static SharedPreferences getSharedPreference(String name,int mode) {
		return mApplicationContent.getSharedPreferences(name, mode);
	}

	/**
	 * 经纬度测距
	 * @param jingdu1
	 * @param weidu1
	 * @param jingdu2
	 * @param weidu2
	 * @return
	 */
	public static double distance(double jingdu1, double weidu1, double jingdu2,   double weidu2) {
		double a, b, R;  
		R = 6378137; // 地球半径  
		weidu1 = weidu1 * Math.PI / 180.0;
		weidu2 = weidu2 * Math.PI / 180.0;
		a = weidu1 - weidu2;  
		b = (jingdu1 - jingdu2) * Math.PI / 180.0;
		double d;  
		double sa2, sb2;  
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2  
				* R  
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(weidu1)
				* Math.cos(weidu2) * sb2 * sb2));
		return d;  
	} 

	/**
	 * 是否有网络
	 * @return
	 */
	public static boolean isNetWorkAvilable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) mApplicationContent
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 * 获取当前网络连接的类型
	 *
	 * @param context context
	 * @return int
	 */
	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取网络服务
		if (null == connManager) { // 为空则认为无网络
			return NETWORK_NONE;
		}
		// 获取网络类型，如果为空，返回无网络
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
			return NETWORK_NONE;
		}
		// 判断是否为WIFI
		NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (null != wifiInfo) {
			NetworkInfo.State state = wifiInfo.getState();
			if (null != state) {
				if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
					return NETWORK_WIFI;
				}
			}
		}
		// 若不是WIFI，则去判断是2G、3G、4G网

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = telephonyManager.getNetworkType();
		switch (networkType) {
            /*
             GPRS : 2G(2.5) General Packet Radia Service 114kbps
             EDGE : 2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
             UMTS : 3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
             CDMA : 2G 电信 Code Division Multiple Access 码分多址
             EVDO_0 : 3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
             EVDO_A : 3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
             1xRTT : 2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
             HSDPA : 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
             HSUPA : 3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
             HSPA : 3G (分HSDPA,HSUPA) High Speed Packet Access
             IDEN : 2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
             EVDO_B : 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
             LTE : 4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
             EHRPD : 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
             HSPAP : 3G HSPAP 比 HSDPA 快些
             */
			// 2G网络
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_IDEN:
				return NETWORK_2G;
			// 3G网络
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				return NETWORK_3G;
			// 4G网络
			case TelephonyManager.NETWORK_TYPE_LTE:
				return NETWORK_4G;
			default:
				return NETWORK_MOBILE;
		}
	}



	/**
	 * 获取运营商名字
	 *
	 * @param context context
	 * @return int
	 */
	public static String getOperatorName(Context context) {
		/*
		 * getSimOperatorName()就可以直接获取到运营商的名字
		 * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
		 * IMSI相关链接：http://baike.baidu.com/item/imsi
		 */
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		// getSimOperatorName就可以直接获取到运营商的名字
		return telephonyManager.getSimOperatorName();
	}



	/**
	 * 取APP版本号
	 * @return
	 */
	public static int getAppVersionCode(){
		try {
			PackageManager mPackageManager = mApplicationContent.getPackageManager();
			PackageInfo _info = mPackageManager.getPackageInfo(mApplicationContent.getPackageName(),0);
			return _info.versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}
	
	/**
	 * 取APP版本名
	 * @return
	 */
	public static String getAppVersionName(){
		try {
			PackageManager mPackageManager = mApplicationContent.getPackageManager();
			PackageInfo _info = mPackageManager.getPackageInfo(mApplicationContent.getPackageName(),0);
			return _info.versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * 获取系统字体大小
	 * @return
	 */
	public static float getFontSize(Context context) {
		Configuration mCurConfig = context.getResources().getConfiguration();//new Configuration();
		JUtils.Log("getFontSize(), Font size is " + mCurConfig.fontScale);
		return mCurConfig.fontScale;

	}

	/**
	 * 获取窗口宽度
	 * @return
	 */
	public static int getWindowWidth(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = windowManager.getDefaultDisplay();
		JUtils.Log(d.getWidth()+"  宽度");
		return d.getWidth();
	}

	/**
	 * 获取窗口高度
	 * @return
	 */
	public static int getWindowHeight(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = windowManager.getDefaultDisplay();
		JUtils.Log(d.getHeight()+"  高度");
		return d.getHeight();
	}




    public static Bitmap BitmapZoom(Bitmap b, float x, float y)
    {
        int w=b.getWidth();
        int h=b.getHeight();
        float sx=x/w;
        float sy=y/h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
				h, matrix, true);
        return resizeBmp;
    }


	public static String MD5(byte[] data) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md5.update(data);
		byte[] m = md5.digest();//加密
		return Base64.encodeToString(m, Base64.DEFAULT);
	}

	public static String getStringFromAssets(String fileName){
		try {
			InputStreamReader inputReader = new InputStreamReader(mApplicationContent.getResources().getAssets().open(fileName) );
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line="";
			String Result="";
			while((line = bufReader.readLine()) != null)
				Result += line;
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Uri getUriFromRes(int id){
		return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
				+ mApplicationContent.getResources().getResourcePackageName(id) + "/"
				+ mApplicationContent.getResources().getResourceTypeName(id) + "/"
				+ mApplicationContent.getResources().getResourceEntryName(id));
	}

	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 获取sdcard容量
	 */
	public static void	readSDCard() {
		String  state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			Log.d("", "block大小:" +blockSize + ",block数目:" +blockCount + ",总大小:" + blockSize * blockCount / 1024 + "KB");
			Log.d("", "可用的block数目：:" +availCount + ",剩余空间:" +availCount * blockSize / 1024 + "KB");
		}
	}

	/**
	 * 获取系统磁盘容量
	 * @return
	 */
	public static long readSystemStorage(){
        File root = Environment.getRootDirectory();  
        StatFs sf = new StatFs(root.getPath());  
        long blockSize = sf.getBlockSize();  
        long blockCount = sf.getBlockCount();  
        long availCount = sf.getAvailableBlocks();
        JUtils.Log("block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
		JUtils.Log("可用的block数目：:"+ availCount+",可用大小:"+ availCount*blockSize/1024+"KB");
		return blockSize*blockCount/1024;
    }



}
