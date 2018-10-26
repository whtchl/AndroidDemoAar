package com.jdjz.weexlib.weex.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jdjz.common.TransScanEntity;
import com.jdjz.contact.ChooseModel;
import com.jdjz.contact.ContactInfo;
import com.jdjz.weexlib.date.DateConfig;
import com.jdjz.weexlib.date.DateDialog;
import com.jdjz.weexlib.weex.activity.ContactListActivity;
import com.jdjz.weexlib.weex.activity.UserProfileActivity;
import com.jdjz.weexlib.weex.jsbridge.BridgeHandler;
import com.jdjz.weexlib.weex.jsbridge.BridgeWebView;
import com.jdjz.weexlib.weex.jsbridge.BridgeWebViewClient;
import com.jdjz.weexlib.weex.jsbridge.CallBackFunction;
import com.jdjz.weexlib.weex.jsbridge.DefaultHandler;
import com.jdjz.weexlib.weex.modle.Brand;
import com.jdjz.weexlib.weex.modle.Event.ContactEvent;
import com.jdjz.weexlib.weex.modle.Event.ContactsEvent;
import com.jdjz.weexlib.weex.modle.Event.GetImageInfoEvent;
import com.jdjz.weexlib.weex.modle.Event.LocationEvent;
import com.jdjz.weexlib.weex.modle.Event.PhoneEvent;
import com.jdjz.weexlib.weex.modle.Event.PreviewImageEvent;
import com.jdjz.weexlib.weex.modle.Event.SaveImageToPhotosAlbumEvent;
import com.jdjz.weexlib.weex.modle.Event.StartAutoLBSEvent;
import com.jdjz.weexlib.weex.modle.Event.StopAutoLBSEvent;
import com.jdjz.weexlib.weex.modle.ModleConfig;
import com.jdjz.weexlib.weex.modle.PreviewImagesData;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestChooseImagesParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestContactsParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestEnterpriseChatParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestGetImageInfoParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestLBSParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestLBSWGS84_GCJ02Params;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestPhoneParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestPreviewImagesParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestSaveImg;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestScanParams;
import com.jdjz.weexlib.weex.modle.RequestParams.RequestUserProfileParams;
import com.jdjz.weexlib.weex.modle.RequestParams.ReuquestDateParams;
import com.jdjz.weexlib.weex.modle.RequestParams.ReuquestDateParams2;
import com.jdjz.weexlib.weex.modle.ResultChooseImages;
import com.jdjz.weexlib.weex.modle.ResultContact;
import com.jdjz.weexlib.weex.modle.ResultContacts;
import com.jdjz.weexlib.weex.modle.ResultDate;
import com.jdjz.weexlib.weex.modle.ResultGetImageInfo;
import com.jdjz.weexlib.weex.modle.ResultLBS;
import com.jdjz.weexlib.weex.modle.ResultNetworkStatus;
import com.jdjz.weexlib.weex.modle.ResultSaveImage;
import com.jdjz.weexlib.weex.modle.ResultScan;
import com.jdjz.weexlib.weex.modle.ResultSystemInfo;
import com.jdjz.weexlib.weex.modle.ResultTemp;
import com.jdjz.weexlib.weex.modle.ResultToken;
import com.jdjz.weexlib.weex.modle.entity.ChooseImagesFileInfoEntity;
import com.jdjz.weexlib.weex.modle.entity.GetImageInfoEntity;
import com.jdjz.weexlib.weex.modle.entity.LBSEntity;
import com.jdjz.weexlib.weex.modle.entity.NetworkStatusEntity;
import com.jdjz.weexlib.weex.modle.entity.ScanEntity;
import com.jdjz.weexlib.weex.modle.entity.Street;
import com.jdjz.weexlib.weex.modle.entity.SystemInfoEntity;
import com.jdjz.weexlib.weex.modle.entity.TempFile;
import com.jdjz.weexlib.weex.util.ImgUtil;
import com.jdjz.weexlib.weex.util.RequestPermissonType;
import com.jdjz.weexlib.weex.util.StrUtil;
import com.jude.utils.JUtils;
import com.jude.utils.permission.PermissionListener;
import com.jude.utils.permission.PermissionNameList;
import com.jude.utils.permission.PermissionRequestCode;
import com.jude.utils.permission.PermissionsUtil;
import com.taobao.weex.ui.view.IWebView;
import com.taobao.weex.utils.WXLogUtils;
/*import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;*/
import com.whamu2.previewimage.Preview;
import com.whamu2.previewimage.entity.Image;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.github.xudaojie.qrcodelib.CaptureActivity;
import me.iwf.photopicker.PhotoPicker;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WXWebViewJsBridge implements IWebView {

    Context mContext;
    private BridgeWebView mWebView;  //x5
    private ProgressBar mProgressBar;
    private boolean mShowLoading = true;

    private OnErrorListener mOnErrorListener;
    private OnPageListener mOnPageListener;


    CallBackFunction callBackFunction;
    String h5Data;  //来自网页端的数据
    //Context jsContext;

    String lbs_type ="0";//默认是0，获取经纬度。lbs_type>1 需要街道级别逆地理的才会有的字段，街道门牌信息：lbs_type>2,需要POI级别逆地理的才会有的字段，定位点附近的 POI 信息
    int responseTypeLBS=0;//0:返回给getLocation()； 1：返回给NativeToJSstartAutoLBS
    String gs84_gcj02;//wgs84是gps坐标，gcj02是火星坐标（默认）
    //高德地图
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    //选择照片的返回值
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    public WXWebViewJsBridge(Context context) {
        mContext = context;

    }

    @Override
    public View getView() {
        EventBus.getDefault().register(this);
        FrameLayout root = new FrameLayout(mContext);
        root.setBackgroundColor(Color.WHITE);

        mWebView = new BridgeWebView(mContext);//mContext.getApplicationContext();
        FrameLayout.LayoutParams wvLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        wvLayoutParams.gravity = Gravity.CENTER;
        mWebView.setLayoutParams(wvLayoutParams);
        root.addView(mWebView);
        initWebView(mWebView);

        mProgressBar = new ProgressBar(mContext);
        showProgressBar(false);
        FrameLayout.LayoutParams pLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        mProgressBar.setLayoutParams(pLayoutParams);
        pLayoutParams.gravity = Gravity.CENTER;
        root.addView(mProgressBar);


        //初始化定位
        initLocation();
        return root;
    }

    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(mContext);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }


    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            ResultLBS resultLBS = new ResultLBS();
            LBSEntity lbsEntity = new LBSEntity();
            Street street = new Street();
            String str2="";
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                    //sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");

                    resultLBS.setResponseCode(ModleConfig.RES200);
                    resultLBS.setResponseMsg(ModleConfig.RES_SUCCESS);


                    lbsEntity.setLongitude(location.getLongitude()+"" );  //经度
                    lbsEntity.setLatitude(location.getLatitude()+"");  //纬度

                    lbsEntity.setAccuracy(location.getAccuracy()+"");   //精确度，单位m
                    lbsEntity.setHorizontalAccuracy("");//水平精确度，单位m   高德地图不能获得

                    if(Integer.parseInt(lbs_type)>0){
                        lbsEntity.setCountry(location.getCountry());
                        lbsEntity.setCountryCode("");//国家码另外写代码
                        lbsEntity.setProvince(location.getProvince());
                        lbsEntity.setCity(location.getCity());
                        lbsEntity.setCityAdcode(location.getCityCode());
                        lbsEntity.setDistrict(location.getDistrict());
                        lbsEntity.setDistrictAdcode(location.getAdCode());
                    }

                    //设置街道信息
                    if(Integer.parseInt(lbs_type)>1) {
                        street.setNumber(location.getStreetNum());
                        street.setStreet(location.getStreet());
                        lbsEntity.setStreetObject(street);
                    }

                    //设置pois
                    if(Integer.parseInt(lbs_type)>2){
                        lbsEntity.setPois(location.getPoiName());
                    }
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");

                    if(responseTypeLBS==0) {  //返回给getLocation()
                        resultLBS.setResponseCode(ModleConfig.RES404);
                        resultLBS.setResponseMsg("[A"+location.getErrorCode()+"]" + location.getLocationDetail());
                    }else if(responseTypeLBS==1){
                        resultLBS.setResponseCode(ModleConfig.RES404);
                        resultLBS.setResponseMsg("[A"+location.getErrorCode()+"]" + location.getLocationDetail());
                        //NativeToJSstartAutoLBS();
                    }
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
                //sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
               // sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
                JUtils.Log(sb.toString());
                //解析定位结果，
                if(responseTypeLBS==0){  //返回给getLocation()

                    String result = sb.toString();
                    stopLocation();
                    resultLBS.setResponseResult(lbsEntity);
                    str2 = new Gson().toJson(resultLBS);
                    callBackFunction.onCallBack(str2);
                }else if(responseTypeLBS==1){
                    JUtils.Log("call ativeToJSstartAutoLBS(resultLBS);");
                    resultLBS.setResponseResult(lbsEntity);
                    NativeToJSstartAutoLBS(resultLBS);
                }

            } else {
                if(responseTypeLBS==0) {  //返回给getLocation()
                    stopLocation();
                    JUtils.Log("定位失败，loc is null");
                    resultLBS.setResponseCode(ModleConfig.RES404);
                    resultLBS.setResponseMsg("定位失败");
                    resultLBS.setResponseResult(null);
                    str2 = new Gson().toJson(resultLBS);
                    callBackFunction.onCallBack(str2);
                }else if(responseTypeLBS==1){
                    resultLBS.setResponseCode(ModleConfig.RES404);
                    resultLBS.setResponseMsg("定位失败");
                    resultLBS.setResponseResult(null);
                    NativeToJSstartAutoLBS(resultLBS);
                }

            }

        }
    };


    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    //重新设置定位参数
    private void resetOption(boolean isOnceLocation) {
        if(isOnceLocation){
            locationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        }else{
            locationOption.setOnceLocation(false);
        }


    }


    @Override
    public void destroy() {
        if (getWebView() != null) {
            destroyLocation();
            getWebView().removeAllViews();
            getWebView().destroy();
            mWebView = null;
        }
    }

    @Override
    public void loadUrl(String url) {
        if(getWebView() == null)
            return;
        getWebView().loadUrl(url);
    }

    @Override
    public void reload() {
        if(getWebView() == null)
            return;
        getWebView().reload();
    }

    @Override
    public void goBack() {
        if(getWebView() == null)
            return;
        getWebView().goBack();
    }

    @Override
    public void goForward() {
        if(getWebView() == null)
            return;
        getWebView().goForward();
    }

    /*@Override
    public void setVisibility(int visibility) {
        if (mRootView != null) {
            mRootView.setVisibility(visibility);
        }
    }*/

    @Override
    public void setShowLoading(boolean shown) {
        mShowLoading = shown;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    @Override
    public void setOnPageListener(OnPageListener listener) {
        mOnPageListener = listener;
    }

    private void showProgressBar(boolean shown) {
        if (mShowLoading) {
            mProgressBar.setVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }

    private void showWebView(boolean shown) {
        mWebView.setVisibility(shown ? View.VISIBLE : View.INVISIBLE);
    }

    private @Nullable
    WebView getWebView() {
        //TODO: remove this, duplicate with getView semantically.
        return mWebView;
    }

    private void initWebView(WebView wv) {
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        wv.setWebViewClient(new MyWebViewClient(mWebView));
        // set HadlerCallBack
        mWebView.setDefaultHandler(new myHadlerCallBack());
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                showWebView(newProgress == 100);
                showProgressBar(newProgress != 100);
                WXLogUtils.v("tag", "onPageProgressChanged " + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mOnPageListener != null) {
                    mOnPageListener.onReceivedTitle(view.getTitle());
                }
            }
        });



        //模拟用户信息 获取本地位置，用户名返回给html
       /* User user = new User();
        user.setLocation("上海");
        user.setName("Bruce");
        // 回调 "functionInJs"
        mWebView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

                Toast.makeText(mContext, "网页在获取你的位置!!!，"+ data, Toast.LENGTH_SHORT).show();

            }
        });*/

        //NativeToJSstartAutoLBS(10);

        mWebView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {

                String str = "tchl 这是html返回给java的数据:" + data;
                // 例如你可以对原始数据进行处理
                str = str + ",Java经过处理后截取了一部分：" + str.substring(0, 5);
                //回调返回给Js
                function.onCallBack(str + ",Java经过处理后截取了一部分：" + str.substring(0, 5));
            }

        });

        reqGetOpenId();
        mWebView.registerHandler("requestFromNativeTypeSetPrivateToken", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = requestFromNativeTypeSetPrivateToken, data from web = " + data);
                JUtils.getSharedPreference().edit().putString("tokenMyServer",data).apply();

                function.onCallBack("点击“获取token”按钮查看" );
            }

        });

        reqGetNetworkType();
        reqMakePhoneCall();
        reqGetSystemInfo();
        reqDatePicker();
        reqScan();
        reqGetLocation();
        reqStopAutoLBS();
        reqStartAutoLBS();
        reqOpenEnterpriseChat();
        reqOpenUserProfile();
        reqSelectEnterpriseContact();
        reqPreviewImage();
        reqChooseImage();
        reqSaveImageToPhotosAlbum();
        reqGetImageInfo();
        mWebView.send("hello");
    }

    /**
     * 停止定位
     */
    private void reqStopAutoLBS() {

        //停止定位。 stopAutoLBS
        mWebView.registerHandler("reqStopAutoLBS", new BridgeHandler() {




            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqStopAutoLBS, data from web = " + data);

                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_LOCATION,PermissionRequestCode.PERMISSION_REQUEST_CODE_STOPAUTO_LOCATION);


/*                ResultTemp resultTemp = new ResultTemp();
                String str2 = "";
                if(locationClient!=null){
                    stopLocation();
                    resultTemp.setResponseCode(ModleConfig.RES200);
                    resultTemp.setResponseCode("停止定位成功");
                    str2 = new Gson().toJson(resultTemp);
                    function.onCallBack(str2);
                }else{
                    resultTemp.setResponseCode(ModleConfig.RES404);
                    resultTemp.setResponseCode("停止定位前先开启定位");
                    str2 = new Gson().toJson(resultTemp);
                    function.onCallBack(str2);
                }*/
            }

        });
    }

    /**
     * 获取一次地理位置信息
     */
    private void reqGetLocation() {
        //获取一次地理位置信息。 getLocation
        mWebView.registerHandler("reqGetLocation", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqGetLocation, data from web = " + data);
                h5Data=  data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_LOCATION,PermissionRequestCode.PERMISSION_REQUEST_CODE_LOCATION);
            }

        });
    }

    /**
     *  二维码 条码扫描
     */
    private void reqScan() {

        mWebView.registerHandler("reqScan", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqScan, data from web = " + data);
                RequestScanParams scanEntity = new Gson().fromJson(data,RequestScanParams.class);

                ResultScan resultScan = new ResultScan();
                if(scanEntity!=null ){
                    if(scanEntity.getType().equals("bar")){

                        Intent i = new Intent(mContext, CaptureActivity.class);
                        i.putExtra("type",false);
                        mContext.startActivity(i);
                        callBackFunction = function;

                    }else{
                        Intent i = new Intent(mContext, CaptureActivity.class);
                        i.putExtra("type",true);
                        mContext.startActivity(i);
                        callBackFunction = function;
                    }
                }else{
                    resultScan.setResponseCode(ModleConfig.RES404);
                    resultScan.setResponseMsg("输入参数错误");
                    resultScan.setResponseResult(null);
                    String str2 = new Gson().toJson(resultScan);
                    function.onCallBack(str2);
                }
            }

        });


    }

    /**
     * 获取日期时间
     */
    private void reqDatePicker2() {
        mWebView.registerHandler("reqDatePicker", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqDatePicker, data from web = " + data);
                ReuquestDateParams dateEntity = new Gson().fromJson(data,ReuquestDateParams.class);

                //final CallBackFunction f = function;
                int mode=DateDialog.MODE_2;
                if(dateEntity.getFormat().equalsIgnoreCase("yyyy-MM-zz")){
                    mode = DateDialog.MODE_2;
                }else if(dateEntity.getFormat().equalsIgnoreCase("HH:mm")){
                    mode = DateDialog.MODE_3;
                }else if(dateEntity.getFormat().equalsIgnoreCase("yyyy-MM-zz HH:mm")){
                    mode = DateDialog.MODE_1;
                }else{
                    mode = DateDialog.MODE_2;
                }
                JUtils.Log("currentDate:"+dateEntity.getCurrentDate());
                final CallBackFunction f = function;
                final  ResultDate resultDate = new ResultDate();
                DateDialog dateDialog = new DateDialog(mContext, "时间日期", mode, dateEntity.getCurrentDate(), dateEntity.getStartDate(), dateEntity.getEnzzate(),
                        new DateDialog.InterfaceDateDialog() {
                            @Override
                            public void getTime(String dateTime)  {
                                if (TextUtils.isEmpty(dateTime)) {
                                    resultDate.setResponseCode(ModleConfig.RES404);

                                    resultDate.setResponseMsg(ModleConfig.RES_FAIL);
                                    resultDate.setResponseResult("");
                                } else {

                                    resultDate.setResponseCode(ModleConfig.RES200);

                                    resultDate.setResponseMsg(ModleConfig.RES_SUCCESS);
                                    resultDate.setResponseResult(dateTime);
                                }
                                String str2 = new Gson().toJson(resultDate);
                                // final JSONObject jsresult = new JSONObject(new Gson().toJson(result));
                                f.onCallBack(str2);
                            }
                        });
                dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dateDialog.show();
                //function.onCallBack("datapicker");
            }
        });
    }


    private void reqDatePicker() {
        mWebView.registerHandler("reqDatePicker", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqDatePicker, data from web = " + data);
                ReuquestDateParams dateEntity = new Gson().fromJson(data,ReuquestDateParams.class);

                //final CallBackFunction f = function;
                int mode=DateDialog.MODE_2;
                if(dateEntity.getFormat().equalsIgnoreCase("yyyy-MM-dd")){
                    mode = DateDialog.MODE_2;
                }else if(dateEntity.getFormat().equalsIgnoreCase("HH:mm")){
                    mode = DateDialog.MODE_3;
                }else if(dateEntity.getFormat().equalsIgnoreCase("yyyy-MM-dd HH:mm")){
                    mode = DateDialog.MODE_1;
                }else if(dateEntity.getFormat().equalsIgnoreCase("yyyy-MM-dd HH:mm:ss")) {
                    mode = DateDialog.MODE_5;
                }
                else{
                    mode = DateDialog.MODE_2;
                }
                JUtils.Log("currentDate2:"+dateEntity.getCurrentDate());
                final CallBackFunction f = function;
                final  ResultDate resultDate = new ResultDate();
                final int finalMode = mode;
                DateDialog dateDialog = new DateDialog(mContext, "时间日期", mode, dateEntity.getCurrentDate(), dateEntity.getStartDate(), dateEntity.getEnzzate(),
                        new DateDialog.InterfaceDateDialog() {
                            @Override
                            public void getTime(String dateTime)  {
                                if (TextUtils.isEmpty(dateTime)) {
                                    resultDate.setResponseCode(ModleConfig.RES404);

                                    resultDate.setResponseMsg(ModleConfig.RES_FAIL);
                                    resultDate.setResponseResult("");
                                } else {

                                    resultDate.setResponseCode(ModleConfig.RES200);

                                    resultDate.setResponseMsg(ModleConfig.RES_SUCCESS);

                                    if(DateDialog.MODE_3 == finalMode){
                                        resultDate.setResponseResult(dateTime);
                                    }else{
                                        resultDate.setResponseResult( Long.toString(DateConfig.convertTimeToLong2(dateTime, finalMode)));
                                    }
                                }
                                String str2 = new Gson().toJson(resultDate);
                                // final JSONObject jsresult = new JSONObject(new Gson().toJson(result));
                                f.onCallBack(str2);
                            }
                        });
                dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dateDialog.show();
            }
        });
    }

    /**
     *  //获取设备信息
     */
    private void reqGetSystemInfo() {

        mWebView.registerHandler("reqGetSystemInfo", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqGetSystemInfo, data from web = " + data);
                ResultSystemInfo resultSystemInfo = new ResultSystemInfo();
                SystemInfoEntity systemInfoEntity = new SystemInfoEntity();
                systemInfoEntity.setModel(JUtils.getSystemModel());
                systemInfoEntity.setPixelRatio(JUtils.getPixelRatio());
                systemInfoEntity.setWindowHeight(JUtils.getWindowHeight(mContext));
                systemInfoEntity.setWindowWidth(JUtils.getWindowWidth(mContext));
                systemInfoEntity.setLanguage(JUtils.getSystemLanguage());
                systemInfoEntity.setVersion(JUtils.getAppVersionCode()+"");
                systemInfoEntity.setStorage(JUtils.readSystemStorage()+"KB");
                systemInfoEntity.setCurrentBattery(JUtils.getSharedPreference().getString(StrUtil.BATTYER_LEVEL,""));
                systemInfoEntity.setSystem(JUtils.getSystemVersion());
                systemInfoEntity.setPlatform("Android");

                systemInfoEntity.setScreenHeight(JUtils.getScreenHeight());
                systemInfoEntity.setScreeWidth(JUtils.getScreenWidth());
                systemInfoEntity.setBrand(JUtils.getDeviceBrand());
                systemInfoEntity.setFontSizeSetting(JUtils.getFontSize(mContext));

                resultSystemInfo.setResponseResult(systemInfoEntity);
                resultSystemInfo.setResponseCode(ModleConfig.RES200);
                resultSystemInfo.setResponseMsg(ModleConfig.RES_SUCCESS);
                String str2 = new Gson().toJson(resultSystemInfo);
                function.onCallBack(str2);

            }

        });
    }

    /**
     * 打电话
     */
    private void reqMakePhoneCall() {
        mWebView.registerHandler("reqMakePhoneCall", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqMakePhoneCall, data from web = " + data);

                callBackFunction = function;
                h5Data = data;
                //华为手机在禁止拨打电话权限后，拨打电话时，有拨打电话的系统权限，但是不让打电话，log显示not startActivity****。 所以对华为手机改为跳转到拨号界面
                if(JUtils.getDeviceBrand().contains(Brand.HUAWEI) || JUtils.getDeviceBrand().contains(Brand.HUAWEI.toLowerCase())){
                    startDialActivity();
                }else{
                    checkPermission((Activity)mContext,PermissionNameList.PERMISSIONS_PHONE,PermissionRequestCode.PERMISSION_REQUEST_CODE_PHONE);

                }
            }
        });
    }

    /**
     * 获取网络状态
     */
    private void reqGetNetworkType() {

        mWebView.registerHandler("reqGetNetworkType", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqGetNetworkType, data from web = " + data);
                ResultNetworkStatus resultNetworkStatus = new ResultNetworkStatus();
                NetworkStatusEntity networkStatusEntity = new NetworkStatusEntity();
                boolean isNetWorkAvilable =  JUtils.isNetWorkAvilable();

                resultNetworkStatus.setResponseCode(ModleConfig.RES200);
                resultNetworkStatus.setResponseMsg(ModleConfig.RES_MSG_NETWORKSTATUS_SUC);

                networkStatusEntity.setNetworkAvailable(isNetWorkAvilable);
                String type  = "";
                switch (JUtils.getNetworkState(mContext)){
                    case 1:
                        type = "WWAN";
                        break;
                    case 2:
                        type = "2G";
                        break;

                    case 3:
                        type = "3G";
                        break;

                    case 4:
                        type = "4G";
                        break;

                    default:
                        type = "UNKNOWN ";
                        break;
                }

                if(TextUtils.isEmpty(isNetWorkReachable(mContext))){
                    type = "NOTREACHABLE ";
                }
               /* if(!JUtils.checkOnlineState(mContext)){
                    type = "NOTREACHABLE";
                }*/

                if(isNetWorkAvilable){
                    networkStatusEntity.setType(type);
                }else{
                    networkStatusEntity.setType(type);
                }
                resultNetworkStatus.setResponseResult(networkStatusEntity);

                String str2 = new Gson().toJson(resultNetworkStatus);
                JUtils.Log("call back");
                function.onCallBack(str2);
            }

        });
    }


    /**
     * 自定义的WebViewClient
     */
    class MyWebViewClient extends BridgeWebViewClient {

        public MyWebViewClient(BridgeWebView webView) {
            super(webView);
        }

       /* @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            WXLogUtils.v("tag", "onPageOverride " + url);
            return true;
        }*/

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            WXLogUtils.v("tag", "onPageStarted " + url);
            if (mOnPageListener != null) {
                mOnPageListener.onPageStart(url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            WXLogUtils.v("tag", "onPageFinished " + url);
            if (mOnPageListener != null) {
                mOnPageListener.onPageFinish(url, view.canGoBack(), view.canGoForward());
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (mOnErrorListener != null) {
                //mOnErrorListener.onError("error", "page error code:" + error.getErrorCode() + ", desc:" + error.getDescription() + ", url:" + request.getUrl());
                mOnErrorListener.onError("error", "page error");
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (mOnErrorListener != null) {
                mOnErrorListener.onError("error", "http error");
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            if (mOnErrorListener != null) {
                mOnErrorListener.onError("error", "ssl error");
            }
        }
    }

    /**
     * 自定义回调
     */
    class myHadlerCallBack extends DefaultHandler {

        @Override
        public void handler(String data, CallBackFunction function) {
            if(function != null){

                Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
            }
        }
    }



   @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ContactEvent event) {
        ArrayList<ContactInfo> contactInfos = event.getContactInfos();
        ResultContacts resultContacts = new ResultContacts();
        resultContacts.setResponseCode(ModleConfig.RES200);
        resultContacts.setResponseMsg(ModleConfig.RES_SUCCESS);
        resultContacts.setResponseResult(contactInfos);
        callBackFunction.onCallBack(new Gson().toJson(resultContacts));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TransScanEntity transScanEntity) {
        ResultScan resultScan = new ResultScan();
        ScanEntity scanEntity = new ScanEntity();
        if(transScanEntity ==null){
            resultScan.setResponseCode(ModleConfig.RES404);
            resultScan.setResponseMsg("Scan:"+ModleConfig.RES_FAIL);
            resultScan.setResponseResult(null);
            String str2 = new Gson().toJson(resultScan);
            callBackFunction.onCallBack(str2);

        }else{
            resultScan.setResponseCode(ModleConfig.RES200);
            resultScan.setResponseMsg(ModleConfig.RES_SUCCESS);
            if(transScanEntity.isType()){  //true qcode
                scanEntity.setCode(transScanEntity.getResult());
                scanEntity.setBarCode("");
                scanEntity.setQrCode(transScanEntity.getResult());
                resultScan.setResponseResult(scanEntity);
            }else{
                scanEntity.setCode(transScanEntity.getResult());
                scanEntity.setBarCode(transScanEntity.getResult());
                scanEntity.setQrCode("");
                resultScan.setResponseResult(scanEntity);
            }
            resultScan.setResponseResult(scanEntity);
            String str2 = new Gson().toJson(resultScan);
            callBackFunction.onCallBack(str2);
        }
    }

    /**
     * 选择照片后返回值
     * @param file
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(List<File> file) {
        ResultChooseImages responseResult = new ResultChooseImages();
        ChooseImagesFileInfoEntity chooseImagesFileInfoEntity = new ChooseImagesFileInfoEntity();
        List<String> tempFilePaths = new ArrayList<>();
        List<TempFile> tempFiles = new ArrayList<>();
        for(int i=0; i<file.size();i++){
            TempFile tempFile = new TempFile();
            Log.i("tchl","selectedPhotos:"+file.get(i).getAbsolutePath());
            tempFilePaths.add(file.get(i).getAbsolutePath());

            tempFile.setPath(file.get(i).getAbsolutePath());
            tempFile.setSize(file.get(i).length());
            tempFiles.add(tempFile);

        }
        chooseImagesFileInfoEntity.setTempFilePaths(tempFilePaths);
        chooseImagesFileInfoEntity.setTempFiles(tempFiles);

        responseResult.setResponseCode(ModleConfig.RES200);
        responseResult.setResponseMsg(ModleConfig.RES_SUCCESS);
        responseResult.setResponseResult(chooseImagesFileInfoEntity);
        String str2 = new Gson().toJson(responseResult);
        callBackFunction.onCallBack(str2);

    }

    /**
     * 申请地理位置权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始定位。
     * @param locationEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LocationEvent locationEvent) {
        handleGetLocation();
    }

    /**
     * 申请联系人权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始获取联系人。
     * @param contactsEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ContactsEvent contactsEvent) {
        handleSelectEnterpriseContact();
    }

    /**
     * 申请联系人权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始获取联系人。
     * @param phoneEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PhoneEvent phoneEvent) {
        handleMakePhoneCall();
    }

    /**
     * 申请自动获取地理位置权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始获取自动获取地理位置。
     * @param startAutoLBSEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StartAutoLBSEvent startAutoLBSEvent) {
        handleStartAutoLBS();
    }

    /**
     * 申请停止自动获取地理位置权限，点击允许后，回调WXPageActivity onRequestPermissionsResult。 开始停止自动获取地理位置。
     * @param stopAutoLBSEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StopAutoLBSEvent stopAutoLBSEvent) {
        handleStopAutoLBS();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PreviewImageEvent previewImageEvent) {
        handlePreviewImage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SaveImageToPhotosAlbumEvent saveImageToPhotosAlbumEvent) {
        handleSaveImageToPhotosAblum();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GetImageInfoEvent getImageInfoEvent) {
        handleGetImageInfo();
    }
    /**
     * 判断网络是否可达
     * @param context
     * @return
     */
    public  String  isNetWorkReachable(Context context) {
        String netAddress = null;
        try
        {
            netAddress = new NetTask().execute("www.baidu.com").get();//"http://www.fsa111f.com/"
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        JUtils.Log("百度的IP:"+netAddress);
        return netAddress;
    }


    public class NetTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            InetAddress addr = null;
            try
            {
                addr = InetAddress.getByName(params[0]);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            if(addr != null){
              return  addr.getHostAddress();
            }
            return "" ;
        }
    }

    /**
     * 回调 NativeToJSstartAutoLBS, 返回信息为html
     */
    private void NativeToJSstartAutoLBS(final ResultLBS resultLBS){
        mWebView.callHandler("NativeToJSstartAutoLBS", new Gson().toJson(resultLBS), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

                //Toast.makeText(mContext, "网页在获取你的位置!!!，"+ data, Toast.LENGTH_SHORT).show();
                JUtils.Log("NativeToJSstartAutoLBS"+data);
            }
        });
    }


    /**
     * 获取openid
     */
    private void reqGetOpenId(){
        mWebView.registerHandler("reqGetOpenId", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqGetOpenId, data from web = " + data);

                String str = JUtils.getSharedPreference().getString("tokenMyServer", ModleConfig.RES404);
                ResultToken resultToken = new ResultToken();
                if (str.equals(ModleConfig.RES404)) {
                    resultToken.setResponseCode(ModleConfig.RES404);
                    resultToken.setResponseMsg(ModleConfig.RES_FAIL);
                    resultToken.setResponseResult("");
                } else {
                    resultToken.setResponseCode(ModleConfig.RES200);
                    resultToken.setResponseMsg(ModleConfig.RES_SUCCESS);
                    JUtils.Log("get openid:"+str);
                    resultToken.setResponseResult(str);
                }
                String str2 = new Gson().toJson(resultToken);

                function.onCallBack(str2);
            }

        });
    }

    /**
     *开启持续定位
     */
    private void reqStartAutoLBS(){
        mWebView.registerHandler("reqStartAutoLBS", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqStartAutoLBS, data from web = " + data);
          /*      RequestLBSWGS84_GCJ02Params requestLBSWGS84_gcj02Params = new Gson().fromJson(data, RequestLBSWGS84_GCJ02Params.class);
                responseTypeLBS = 1;
                resetOption(false);
                startLocation();*/



                h5Data=  data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_LOCATION,PermissionRequestCode.PERMISSION_REQUEST_CODE_STARTAUTO_LOCATION);
            }

        });
    }

    /**
     * 创建会话
     */
    private void reqOpenEnterpriseChat(){
        mWebView.registerHandler("reqOpenEnterpriseChat", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqOpenEnterpriseChat, data from web = " + data);
                RequestEnterpriseChatParams requestEnterpriseChatParams = new RequestEnterpriseChatParams();
                requestEnterpriseChatParams = new Gson().fromJson(data,RequestEnterpriseChatParams.class);
                String[] listUserIds = requestEnterpriseChatParams.getUserIds().split(";");

                function.onCallBack("未确定用哪家的IM");
            }

        });
    }

    /**
     * 打开个人信息页
     */
    private void reqOpenUserProfile(){
        mWebView.registerHandler("reqOpenUserProfile", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqOpenUserProfile, data from web = " + data);
                RequestUserProfileParams  requestUserProfileParams= new RequestUserProfileParams();
                requestUserProfileParams = new Gson().fromJson(data,RequestUserProfileParams.class);
               // String[] listUserIds = requestEnterpriseChatParams.getUserIds().split(";");
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("type",requestUserProfileParams.getType());
                intent.putExtra("userid",requestUserProfileParams.getUserid());
                mContext.startActivity(intent);
                ResultTemp resultTemp = new ResultTemp();
                resultTemp.setResponseCode(ModleConfig.RES200);
                resultTemp.setResponseMsg(ModleConfig.RES_SUCCESS);
                function.onCallBack(new Gson().toJson(resultTemp));
            }

        });
    }

    /**
     * 获取联系人
     */
    private void reqSelectEnterpriseContact(){
        mWebView.registerHandler("reqSelectEnterpriseContact", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqSelectEnterpriseContact, data from web = " + data);
                h5Data=  data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_CONTACTS,PermissionRequestCode.PERMISSION_REQUEST_CODE_CONTACTS);
            }

        });
    }


    /**
     * 预览图片
     */
    private void reqPreviewImage(){
        mWebView.registerHandler("reqPreviewImage", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqPreviewImage, data from web = " + data);
                h5Data = data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_STORAGE,PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_PREVIEWIMAGE);


            }

        });
    }

/*
    public  boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {

        if (permissions.length == 0) {
            return false;
        }

        for (String per : permissions ) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if ( result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }*/




    /**
     * 选择照片
     */
    private void reqChooseImage() {
        mWebView.registerHandler("reqChooseImage", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqChooseImage, data from web = " + data);
                RequestChooseImagesParams requestChooseImagesParams = new RequestChooseImagesParams();
                requestChooseImagesParams = new Gson().fromJson(data,RequestChooseImagesParams.class);

               if( PermissionsUtil.hasPermission(mContext,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})){
                   JUtils.Log("reqChooseImage have storage 权限");

               }else{
                   JUtils.Log("reqChooseImage do not have storage 权限");
               }

                int mCompress = 2;//0->origin; 1->compressed; 2->两者都有
                Boolean isCamera = true;
                if(requestChooseImagesParams.getSourceType().size()==2){
                    mCompress = 2;
                    JUtils.Log("mCompress = 2");
                }else if(requestChooseImagesParams.getSourceType().size()==1){
                    if(requestChooseImagesParams.getSourceType().get(0).equals("original")){
                        mCompress = 0;
                        JUtils.Log(" mCompress = 0");
                    }else if(requestChooseImagesParams.getSourceType().get(0).equals("compressed")){
                        mCompress = 1;
                        JUtils.Log("mCompress = 1");
                    }
                }

                if(requestChooseImagesParams.getSizeType().size()==2){
                    isCamera = true;
                    JUtils.Log("isCamera is true 2");
                }else if(requestChooseImagesParams.getSizeType().size()==1){
                    if(requestChooseImagesParams.getSizeType().get(0).equals("album")){
                        isCamera = false;
                        JUtils.Log("isCamera is false");
                    }else if(requestChooseImagesParams.getSizeType().get(0).equals("camera")){
                        isCamera = true;
                        JUtils.Log("isCamera is true");
                    }
                }

                PhotoPicker.builder()
                        .setPhotoCount(requestChooseImagesParams.getCount())
                        .setGridColumnCount(4)
                        .setCompress(mCompress)
                        .setShowCamera(isCamera)
                        .start(mContext);

                callBackFunction = function;
            }
        });
    }

    /**
     * 保存图片到相册
     */
    private void reqSaveImageToPhotosAlbum(){
        mWebView.registerHandler("reqSaveImageToPhotosAlbum", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqSaveImageToPhotosAlbum, data from web = " + data);
                h5Data = data;
                callBackFunction = function;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_STORAGE,PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SAVEIMAGETOPHOTOSALBUM);
            }
        });
    }

    /**
     * 获取图片信息
     */
    private void reqGetImageInfo(){

        mWebView.registerHandler("reqGetImageInfo", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = reqGetImageInfo, data from web = " + data);
                callBackFunction = function;
                h5Data = data;
                checkPermission((Activity)mContext, PermissionNameList.PERMISSIONS_STORAGE,PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_GETIMAGEINFO);
            }
        });
    }

    private void saveImage(String localPath){
        ResultSaveImage resultSaveImage = new ResultSaveImage();

        //String localPath = "/sdcard/jpg.jpg";
        Bitmap bmp = BitmapFactory.decodeFile(localPath);
        if(bmp!=null){
            String filepath = "";
            filepath = ImgUtil.saveImageToGallery(mContext,bmp);
            if(!filepath.equals("")){
                resultSaveImage.setResponseCode(ModleConfig.RES200);
                resultSaveImage.setResponseMsg(ModleConfig.RES_SUCCESS);
                resultSaveImage.setResponseResult(filepath);
            }else{
                resultSaveImage.setResponseCode(ModleConfig.RES404);
                resultSaveImage.setResponseMsg(ModleConfig.RES_FAIL);
                resultSaveImage.setResponseResult("文件保存失败");
            }
        }else{
            JUtils.Log("bmp is null");
            resultSaveImage.setResponseCode(ModleConfig.RES404);
            resultSaveImage.setResponseMsg(ModleConfig.RES_FAIL);
            resultSaveImage.setResponseResult("文件不存在");
        }
        String str = new Gson().toJson(resultSaveImage);
        callBackFunction.onCallBack(str);
    }

    private  void  getImageInfo(String src){
        ResultGetImageInfo resultGetImageInfo = new ResultGetImageInfo();
        GetImageInfoEntity getImageInfoEntity = new GetImageInfoEntity();
        BitmapFactory.Options options = new BitmapFactory.Options();

        if(!isExist(src)){

            resultGetImageInfo.setResponseCode(ModleConfig.RES404);
            resultGetImageInfo.setResponseMsg(ModleConfig.RES_FAIL);
            resultGetImageInfo.setResponseResult(null);
        }else{
            /**
             * 最关键在此，把options.inJustDecodeBounds = true;
             * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
             */

            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(src, options); // 此时返回的bitmap为null
            /**
             *options.outHeight为原始图片的高
             */
            //return new String[]{String.valueOf(options.outWidth) ,String.valueOf(options.outHeight),src};
            JUtils.Log("width:"+options.outWidth+"  height:"+ options.outHeight);
            getImageInfoEntity.setHeight(options.outHeight);
            getImageInfoEntity.setWidth(options.outWidth);
            getImageInfoEntity.setPath(src);
            resultGetImageInfo.setResponseCode(ModleConfig.RES200);
            resultGetImageInfo.setResponseMsg(ModleConfig.RES_SUCCESS);
            resultGetImageInfo.setResponseResult(getImageInfoEntity);
        }

        String str = new Gson().toJson(resultGetImageInfo);
        callBackFunction.onCallBack(str);
    }


    /**
     * 判断文件是否存在
     *
     * @param path
     */
    private Boolean isExist(String path) {
        File file = new File(path);
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            return false;
        }
        return  true;
    }


    public static int[] getImageWidthHeight(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }

    private void requestPhoneCall(String phone, @NonNull String... permissions){

        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "获取打电话权限", "禁止", "允许");
        PermissionsUtil.requestPermission(mContext, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL");
                intent.setData(Uri.parse("tel:152365"));
                mContext.startActivity(intent);

            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                //Toast.makeText(, "用户拒绝使用读写存储权限", Toast.LENGTH_LONG).show();
                JUtils.Toast("用户拒绝使用打电话权限");
            }
        }, new String[]{Manifest.permission.CALL_PHONE}, true, tip);

    }


    private void checkPermission(Activity activity, @NonNull String[] permissions, @NonNull int reqCode) {
        //Build.VERSION_CODES.M

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            switch (reqCode) {
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_LOCATION:
                    handleGetLocation();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_CONTACTS:
                    handleSelectEnterpriseContact();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_PHONE:
                    handleMakePhoneCall();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STARTAUTO_LOCATION:
                    handleStartAutoLBS();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STOPAUTO_LOCATION:
                    handleStopAutoLBS();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_GETIMAGEINFO:
                    handleGetImageInfo();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_PREVIEWIMAGE:
                    handlePreviewImage();
                    break;
                case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SAVEIMAGETOPHOTOSALBUM:
                    handleSaveImageToPhotosAblum();
                    break;
                default:
                    break;
            }
        } else {
            if (PermissionsUtil.hasPermission(mContext, permissions)) {
                JUtils.Log("checkPermission: have the permission");
                switch (reqCode) {
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_LOCATION:
                        handleGetLocation();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_CONTACTS:
                        handleSelectEnterpriseContact();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_PHONE:
                        handleMakePhoneCall();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STARTAUTO_LOCATION:
                        handleStartAutoLBS();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STOPAUTO_LOCATION:
                        handleStopAutoLBS();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_GETIMAGEINFO:
                        handleGetImageInfo();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_PREVIEWIMAGE:
                        handlePreviewImage();
                        break;
                    case PermissionRequestCode.PERMISSION_REQUEST_CODE_STORAGE_SAVEIMAGETOPHOTOSALBUM:
                        handleSaveImageToPhotosAblum();
                        break;
                    default:
                        break;

                }
            } else {

                ActivityCompat.requestPermissions((Activity) mContext, permissions, reqCode);
                JUtils.Log("have no permissions");
            }
        }
    }

    private void handleSaveImageToPhotosAblum() {
        RequestSaveImg requestSaveImg = new RequestSaveImg();
        requestSaveImg = new Gson().fromJson(h5Data,RequestSaveImg.class);
        saveImage(requestSaveImg.getFilePath());
        //requestStorage(requestSaveImg.getFilePath(),RequestPermissonType.REQ_SAVE_IMAGE_TO_PHOTO_ALBUM);
    }

    private void handlePreviewImage() {

        RequestPreviewImagesParams requestPreviewImagesParams = new RequestPreviewImagesParams();
        requestPreviewImagesParams = new Gson().fromJson(h5Data,RequestPreviewImagesParams.class);
        Gson gs = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        List<PreviewImagesData> datas = new ArrayList<>();

        for(int i=0;i<requestPreviewImagesParams.getUrls().size();i++){
            PreviewImagesData previewImagesData = new PreviewImagesData();
            JUtils.Log(requestPreviewImagesParams.getUrls().get(i));

            previewImagesData.setOriginUrl(requestPreviewImagesParams.getUrls().get(i));
            previewImagesData.setThumbnailUrl(requestPreviewImagesParams.getUrls().get(i));
            datas.add(previewImagesData);
        }
        PreviewImagesData current = new PreviewImagesData();
        current.setOriginUrl(requestPreviewImagesParams.getCurrent());
        current.setThumbnailUrl(requestPreviewImagesParams.getCurrent());

        int i=0;
        int currentNumber=0;
        List<Image> images = new ArrayList<>();
        for (PreviewImagesData d : datas) {
            Image image = new Image();
            image.setOriginUrl(d.getOriginUrl());
            image.setThumbnailUrl(d.getThumbnailUrl());
            images.add(image);
            i++;
            if(d.getOriginUrl().equals(current.getOriginUrl())){
                JUtils.Log("*** list 中找到了这个current image：是第"+i+"个");
                currentNumber = i-1;
            }
        }

        Preview.with(mContext)
                .builder()
                .load(images)
                .displayCount(true)
                .markPosition(currentNumber)
                .showDownload(false)
                .showOriginImage(false)
                .downloadLocalPath("Preview")
                .show();


    }

    private void handleGetImageInfo() {
        RequestGetImageInfoParams requestGetImageInfoParams = new RequestGetImageInfoParams();
        requestGetImageInfoParams = new Gson().fromJson(h5Data,RequestGetImageInfoParams.class);
        //saveImage(requestGetImageInfoParams.getSrc());
        getImageInfo(requestGetImageInfoParams.getSrc());
        //requestStorage(requestGetImageInfoParams.getSrc(),RequestPermissonType.REQ_GET_IMAGE_INFO);
    }

    private void handleStopAutoLBS() {
        ResultTemp resultTemp = new ResultTemp();
        String str2 = "";
        if(locationClient!=null){
            stopLocation();
            resultTemp.setResponseCode(ModleConfig.RES200);
            resultTemp.setResponseMsg("停止定位成功");
            str2 = new Gson().toJson(resultTemp);
            callBackFunction.onCallBack(str2);
        }else{
            resultTemp.setResponseCode(ModleConfig.RES404);
            resultTemp.setResponseMsg("停止定位前先开启定位");
            str2 = new Gson().toJson(resultTemp);
            callBackFunction.onCallBack(str2);
        }
    }

    private void handleStartAutoLBS() {
        RequestLBSWGS84_GCJ02Params requestLBSWGS84_gcj02Params = new Gson().fromJson(h5Data, RequestLBSWGS84_GCJ02Params.class);
        responseTypeLBS = 1;
        resetOption(false);
        startLocation();
    }

    /**
     * 直接打电话
     */
    private void handleMakePhoneCall() {
        RequestPhoneParams jsParams = new Gson().fromJson(h5Data,RequestPhoneParams.class);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:"+jsParams.getPhone()));
        mContext.startActivity(intent);
    }

    /**
     * 跳到拨号界面
     */
    private void startDialActivity(){
        RequestPhoneParams jsParams = new Gson().fromJson(h5Data,RequestPhoneParams.class);
        JUtils.Log("phone:"+jsParams.getPhone());
        Intent intentNO = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+jsParams.getPhone()));
        mContext.startActivity(intentNO);
    }

    private void handleSelectEnterpriseContact() {

        //JUtils.Toast("联系人暂无");
        RequestContactsParams requestContactsParams = new RequestContactsParams();
        requestContactsParams = new Gson().fromJson(h5Data,RequestContactsParams.class);
        String str2 ;//= new Gson().toJson(resultToken);
        ResultContact resultContact = new ResultContact();
        if(TextUtils.isEmpty(requestContactsParams.getContacts())){
            JUtils.Log("404");
            resultContact.setResponseCode(ModleConfig.RES404);
            resultContact.setResponseMsg("输入参数为空");
            resultContact.setResponseResult(null);
            str2 =  new Gson().toJson(resultContact);
            callBackFunction.onCallBack(str2);
            return;
        }else if(requestContactsParams.getContacts().equals(ModleConfig.RESMULIT)){
            JUtils.Log("RESMULIT");
            Intent intent = new Intent(mContext, ContactListActivity.class);
            intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_MULTI);
            mContext.startActivity(intent);


        }else if(requestContactsParams.getContacts().equals(ModleConfig.RESSINGLE)){
            JUtils.Log("RESSINGLE");
            Intent intent = new Intent(mContext, ContactListActivity.class);
            intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_SINGLE);
            mContext.startActivity(intent);
        }

    }

    private void handleGetLocation() {
        RequestLBSParams requestLBSParams = new Gson().fromJson(h5Data,RequestLBSParams.class);
        ResultLBS resultLBS = new ResultLBS();
        responseTypeLBS = 0;
        lbs_type = requestLBSParams.getType();
        if(lbs_type.equals("1") ||lbs_type.equals("2") ||lbs_type.equals("3")){
            ;
        }else{
            lbs_type="0";
        }
        resetOption(true);
        startLocation();
    }

    private void requestStorage(final String path, final int type) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "获取SD卡读写权限", "禁止", "允许");
        PermissionsUtil.requestPermission(mContext, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                switch (type) {
                    case RequestPermissonType.REQ_SAVE_IMAGE_TO_PHOTO_ALBUM:
                        saveImage(path);
                        break;
                    case RequestPermissonType.REQ_GET_IMAGE_INFO:
                        getImageInfo(path);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                //Toast.makeText(, "用户拒绝使用读写存储权限", Toast.LENGTH_LONG).show();
                JUtils.Toast("用户拒绝使用读写存储权限");
            }
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, true, tip);
    }



}
