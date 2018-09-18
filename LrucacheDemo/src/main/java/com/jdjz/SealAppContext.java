package com.jdjz;

import android.content.Context;

import java.util.ArrayList;

public class SealAppContext {
    private Context mContext;
    private static SealAppContext mSealAppContextInstance;

    public SealAppContext(Context mContext) {
        this.mContext = mContext;
        SealUserInfoManager.init(mContext);
    }



    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mSealAppContextInstance == null) {
            synchronized (SealAppContext.class) {

                if (mSealAppContextInstance == null) {
                    mSealAppContextInstance = new SealAppContext(context);
                }
            }
        }

    }

}
