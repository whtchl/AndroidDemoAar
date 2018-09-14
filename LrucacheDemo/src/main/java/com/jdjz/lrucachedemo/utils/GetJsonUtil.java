package com.jdjz.lrucachedemo.utils;

import android.util.Log;

import com.jdjz.lrucachedemo.beans.NewsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by happen on 2018-07-19.
 */

public class GetJsonUtil {
    public static List<NewsBean> getJson(String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        NewsBean newsBean;
        try {
            //获取请求返回字符串
            String jsonString = JsonToStringUtil.JsonToString(new URL(url).openStream());
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                Log.i("tchl","jsonObject size:"+jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject dataJson = jsonArray.getJSONObject(i);
                    newsBean = new NewsBean();
                    newsBean.newsIconUrl = dataJson.getString("picSmall");
                    newsBean.newsTitle = dataJson.getString("name");
                    newsBean.newsContent = dataJson.getString("description");

                    newsBeanList.add(newsBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }
}
