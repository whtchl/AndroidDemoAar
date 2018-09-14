package com.jdjz.lrucachedemo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.jdjz.lrucachedemo.adapter.NewsAdapter;
import com.jdjz.lrucachedemo.beans.NewsBean;
import com.jdjz.lrucachedemo.utils.GetJsonUtil;

import java.util.List;

public class LrucacheDemo extends AppCompatActivity {

    private ListView mainListView;
    private Context mainContext = LrucacheDemo.this;
    GetJsonTask getJsonTask;
    private String url = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrucache);

        initView();
        //开启异步任务
        getJsonTask = new GetJsonTask();
        getJsonTask.execute(url);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        getJsonTask.cancel(true);
    }

    /**
     * 初始化view
     */
    private void initView() {
        mainListView = (ListView) findViewById(R.id.lv_main);
    }

    /**
     * 自定义异步任务解析json数据
     */
    class GetJsonTask extends AsyncTask<String,Void,List<NewsBean>>{

        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return GetJsonUtil.getJson(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            NewsAdapter newsAdapter = new NewsAdapter(mainContext, newsBeen,mainListView);
            mainListView.setAdapter(newsAdapter);
        }
    }
}
