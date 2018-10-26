package com.jdjz.weexlib.weex;

//import com.tencent.smtt.sdk.WebView;

import android.webkit.WebView;

/**
 * Created by miao on 2018/4/23.
 * 进行加载监控回调
 */

public interface LoadInterface {
    /**
     * 进度改变回调
     * @param view
     * @param newProgress
     */
    void onProgressChanged(WebView view, int newProgress);

    /**
     * 加载完成回调
     * @param view
     * @param url
     */
    void onPageFinished(WebView view, String url);
}
