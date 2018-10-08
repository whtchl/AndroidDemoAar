package com.jdjz.weex.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jdjz.SealUserInfoManager;
import com.jdjz.contact.ChooseModel;
import com.jdjz.contact.ContactInfo;
import com.jdjz.contacts.ContactEvent;
import com.jdjz.contacts.ContactListActivity;
import com.jdjz.contacts.ContactsActivity;
import com.jdjz.testConfig.SealConst;
import com.jdjz.weex.jsbridge.BridgeHandler;
import com.jdjz.weex.jsbridge.BridgeWebView;
import com.jdjz.weex.jsbridge.BridgeWebViewClient;
import com.jdjz.weex.jsbridge.CallBackFunction;
import com.jdjz.weex.jsbridge.DefaultHandler;
import com.jdjz.weex.modle.ModleConfig;
import com.jdjz.weex.modle.ResultContact;
import com.jdjz.weex.modle.ResultNetworkStatus;
import com.jdjz.weex.modle.ResultSystemInfo;
import com.jdjz.weex.modle.ResultToken;
import com.jdjz.weex.modle.User;
import com.jdjz.weex.modle.entity.JSParams;
import com.jdjz.weex.modle.entity.NetworkStatusEntity;
import com.jdjz.weex.modle.entity.SystemInfoEntity;
import com.jdjz.weex.util.NetUtils;
import com.jude.utils.JUtils;
import com.taobao.weex.ui.view.IWebView;
import com.taobao.weex.utils.WXLogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WXWebViewJsBridge implements IWebView { //BridgeView   WXWebView

    Context mContext;
    private BridgeWebView mWebView;  //x5
    private ProgressBar mProgressBar;
    private boolean mShowLoading = true;

    private OnErrorListener mOnErrorListener;
    private OnPageListener mOnPageListener;


    CallBackFunction callBackFunction;
    //Context jsContext;

    public WXWebViewJsBridge(Context context) {
        mContext = context;

    }

    @Override
    public View getView() {
        JUtils.Log("1111");
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
        return root;
    }

    @Override
    public void destroy() {
        if (getWebView() != null) {
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

    private @Nullable WebView getWebView() {
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
        User user = new User();
        user.setLocation("上海");
        user.setName("Bruce");
        // 回调 "functionInJs"
        mWebView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

                Toast.makeText(mContext, "网页在获取你的位置!!!，"+ data, Toast.LENGTH_SHORT).show();

            }
        });

        mWebView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {

                String str = "tchl 这是html返回给java的数据:" + data;
                // 例如你可以对原始数据进行处理
                str = str + ",Java经过处理后截取了一部分：" + str.substring(0, 5);
                //Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
                //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                //回调返回给Js
                function.onCallBack(str + ",Java经过处理后截取了一部分：" + str.substring(0, 5));
            }

        });


        mWebView.registerHandler("requestFromNativeTypePrivateToken", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = requestFromNativeTypePrivateToken, data from web = " + data);

                String str = JUtils.getSharedPreference().getString("tokenMyServer", ModleConfig.RES404);
                ResultToken resultToken = new ResultToken();
                if (str.equals(ModleConfig.RES404)) {
                    resultToken.setResponseCode(ModleConfig.RES404);
                    resultToken.setResponseResult("");
                } else {
                    resultToken.setResponseCode(ModleConfig.RES200);
                    resultToken.setResponseResult(str);
                }
                String str2 = new Gson().toJson(resultToken);

                function.onCallBack(str2);
            }

        });


        mWebView.registerHandler("requestFromNativeTypeSetPrivateToken", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = requestFromNativeTypeSetPrivateToken, data from web = " + data);
                JUtils.getSharedPreference().edit().putString("tokenMyServer",data).apply();
                function.onCallBack("点击“获取token”按钮查看" );
            }

        });

        mWebView.registerHandler("requestFromNativeTypeContacts", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = requestFromNativeTypeContacts, data from web = " + data);
                String str2 ;//= new Gson().toJson(resultToken);
                ResultContact resultContact = new ResultContact();
                if(TextUtils.isEmpty(data)){
                    JUtils.Log("404");
                    resultContact.setResponseCode(ModleConfig.RES404);
                    resultContact.setResponseMsg("输入参数为空");
                    resultContact.setResponseResult(null);
                    str2 =  new Gson().toJson(resultContact);
                    function.onCallBack(str2);
                   return;
                }else if(data.equals(ModleConfig.RESMULIT)){
                    JUtils.Log("RESMULIT");
                    Intent intent = new Intent(mContext, ContactListActivity.class);
                    intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_MULTI);
                    mContext.startActivity(intent);


                }else if(data.equals(ModleConfig.RESSINGLE)){
                    JUtils.Log("RESSINGLE");
                    Intent intent = new Intent(mContext, ContactListActivity.class);
                    intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_SINGLE);
                    mContext.startActivity(intent);
                }

                JUtils.Log("RESMULIT");
                Intent intent = new Intent(mContext, ContactListActivity.class);
                intent.putExtra(ChooseModel.CHOOSEMODEL,ChooseModel.MODEL_MULTI);
                mContext.startActivity(intent);

                callBackFunction = function;
            }
        });


        //获取网络状态
        mWebView.registerHandler("requestFromNativeNetworkStatus", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = requestFromNativeNetworkStatus, data from web = " + data);
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
                resultNetworkStatus.setNetworkStatusEntity(networkStatusEntity);

                String str2 = new Gson().toJson(resultNetworkStatus);
                JUtils.Log("call back");
                function.onCallBack(str2);
            }

        });


        //获取网络状态
        mWebView.registerHandler("requestFromNativeMakePhoneCall", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = requestFromNativeMakePhoneCall, data from web = " + data);
                JSParams jsParams = new Gson().fromJson(data,JSParams.class);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.CALL");
                intent.setData(Uri.parse("tel:" + jsParams.getParam()));
                mContext.startActivity(intent);
            }
        });

        //获取设备信息

        //获取网络状态
        mWebView.registerHandler("requestFromNativeSystemInfo", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JUtils.Log("handler = requestFromNativeSystemInfo, data from web = " + data);
                ResultSystemInfo resultSystemInfo = new ResultSystemInfo();
                SystemInfoEntity systemInfoEntity = new SystemInfoEntity();
                systemInfoEntity.setModel(JUtils.getSystemModel());
                systemInfoEntity.setPixelRatio(0);
                systemInfoEntity.setWindowHeight(JUtils.getWindowHeight(mContext));
                systemInfoEntity.setWindowWidth(JUtils.getWindowWidth(mContext));
                systemInfoEntity.setLanguage(JUtils.getSystemLanguage());
                systemInfoEntity.setVersion(JUtils.getAppVersionCode()+"");
                systemInfoEntity.setStorage(JUtils.readSystemStorage()+"KB");
                systemInfoEntity.setCurrentBattery(JUtils.getSharedPreference().getString(SealConst.BATTYER_LEVEL,""));
                systemInfoEntity.setSystem(JUtils.getSystemVersion());
                systemInfoEntity.setPlatform("Android");

                systemInfoEntity.setScreenHeight(JUtils.getScreenHeight());
                systemInfoEntity.setScreeWidth(JUtils.getScreenWidth());
                systemInfoEntity.setBrand(JUtils.getDeviceBrand());
                systemInfoEntity.setFontSizeSetting(JUtils.getFontSize());


                resultSystemInfo.setSystemInfoEntity(systemInfoEntity);
                String str2 = new Gson().toJson(resultSystemInfo);
                function.onCallBack(str2);

            }

        });
        mWebView.send("hello");
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
        callBackFunction.onCallBack(contactInfos.size()+" 个");
        /*Collections.sort(contactInfos, new Comparator<ContactInfo>() {
            @Override
            public int compare(ContactInfo o1, ContactInfo o2) {
                //升序排列
                if (o1.getLetter().equals("#") || o2.getLetter().equals("#")) {
                    return 1;
                }
                return o1.getLetter().compareTo(o2.getLetter());
            }
        });

        mContactAdapter.setContactList(contactInfos);*/
    }

    //判断网络是否可达
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

}
