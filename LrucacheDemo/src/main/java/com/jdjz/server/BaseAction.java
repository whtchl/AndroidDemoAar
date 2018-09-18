package com.jdjz.server;

import android.content.Context;

import com.jdjz.server.network.http.HttpException;
import com.jdjz.server.utils.json.JsonMananger;

public class BaseAction {

    private static final String DOMAIN = "http://api.sealtalk.im";
    protected Context mContext;



    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public BaseAction(Context context) {
        this.mContext = context;
    }


    /**
     * JSON转JAVA对象方法
     *
     * @param json json
     * @param cls  class
     * @throws HttpException
     */
    public <T> T jsonToBean(String json, Class<T> cls) throws HttpException {
        return JsonMananger.jsonToBean(json, cls);
    }
}
