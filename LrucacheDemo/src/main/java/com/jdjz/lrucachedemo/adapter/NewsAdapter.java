package com.jdjz.lrucachedemo.adapter;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jdjz.lrucachedemo.R;
import com.jdjz.lrucachedemo.beans.NewsBean;
import com.jdjz.lrucachedemo.utils.DiskCacheUtil;
import com.jdjz.lrucachedemo.utils.LruCacheUtil;
import com.jdjz.lrucachedemo.utils.ThreadUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by happen on 2018-07-19.
 */
//AbsListView 是ListView的父类
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private Context context;
    private List<NewsBean> list;
    private LruCacheUtil lruCacheUtil;
    private DiskCacheUtil mDiskCacheUtil;
    private ThreadUtil mThreadUtil;
    private int mStart,mEnd;  //滑动的起始位置
    public static String[] urls; //用来保存当前获取到的所有图片的Url地址

    //是否是第一次进入  （优化AtomicBoolean）
    private AtomicBoolean mFirstIn = new AtomicBoolean();

    private int type = 3;//1表示disklrucache,2表示lrucache，3表示ThreadUtil


    public NewsAdapter(Context context,List<NewsBean> list,ListView lv){
        this.context = context;
        this.list = list;
        lruCacheUtil = new LruCacheUtil(lv);
        mDiskCacheUtil = new DiskCacheUtil(context,lv);
        mThreadUtil = new ThreadUtil();
        //存入url地址
        urls = new String[list.size()];
        for(int i=0; i<list.size();i++){
            urls[i] = list.get(i).newsIconUrl;
        }
        mFirstIn.set(true);
        //注册监听事件
        lv.setOnScrollListener(this);
    }

    /**
     * 滑动状态改变的时候才会去调用此方法
     *
     * @param view        滚动的View
     * @param scrollState 滚动的状态
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if(scrollState == SCROLL_STATE_IDLE){
            Log.i("jdjz","onScrollStateChanged SCROLL_STATE_IDLE ");
            //加载可预见
            try{
                switch (type){
                    case 1:
                        mDiskCacheUtil.loadImages(mStart,mEnd);
                        break;
                    case 2:
                        lruCacheUtil.loadImages(mStart,mEnd);
                        break;
                    default:
                        Log.i("jdjz","onScrollStateChanged SCROLL_STATE_IDLE不是前两种方式(disklrucache and lrucache)加载图片");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            //停止加载任务
            mDiskCacheUtil.cancelAllTask();
        }
    }

    /**
     * 滑动过程中 一直会调用此方法
     *
     * @param view             滚动的View
     * @param firstVisibleItem 第一个可见的item
     * @param visibleItemCount 可见的item的长度
     * @param totalItemCount   总共item的个数
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        //如果是第一次进入 且可见item大于0 预加载
        if (mFirstIn.get() && visibleItemCount > 0) {
            try {
                Log.i("jdjz","onScroll 如果是第一次进入 且可见item大于0 预加载 ");
                switch(type){
                    case 1:
                        mDiskCacheUtil.loadImages(mStart, mEnd);
                        break;
                    case 2:
                        lruCacheUtil.loadImages(mStart,mEnd);
                        break;
                    default:
                        Log.i("jdjz","第一次进入: 不是前两种方式(disklrucache and lrucache)加载图片") ;
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            mFirstIn.set(false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        Log.i("tchl","getItem:"+i);
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.i("tchl","getItemId:"+i);
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("jdjz","getView");
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_news,null);
        }
        //得到一个ViewHolder
        viewHolder = ViewHolder.getViewHolder(convertView);
        //先加载默认图片 防止有的没有图片
        viewHolder.iconImage.setImageResource(R.mipmap.ic_launcher);
        String iconUrl = list.get(position).newsIconUrl;
        //当前位置的ImageView与URL中的图片绑定
        viewHolder.iconImage.setTag(iconUrl);

        Log.i("jdjz","iconUrl:"+iconUrl);
        switch (type){
            case 1:
                //第1种方式 通过异步任务方式设置 且利用DiskLruCache存储到磁盘缓存中
                try {
                    mDiskCacheUtil.showImageByAsyncTask(viewHolder.iconImage, iconUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:

                //第2种方式 通过异步任务方式设置 且利用LruCache存储到内存缓存中
                lruCacheUtil.showImageByAsyncTask(viewHolder.iconImage, iconUrl);
                break;
            case 3:
                //第3种方式 通过子线程设置
                mThreadUtil.showImageByThread(viewHolder.iconImage, iconUrl);
                break;
            default:
                Log.i("jdjz","getView  非前3种方式显示图片");
                break;
        }

        viewHolder.titleText.setText(list.get(position).newsTitle);
        viewHolder.contentText.setText(list.get(position).newsContent);

        return convertView;
    }


    static class ViewHolder {
        ImageView iconImage;
        TextView titleText;
        TextView contentText;

        // 构造函数中就初始化View
        public ViewHolder(View convertView) {
            iconImage = (ImageView) convertView.findViewById(R.id.iv_icon);
            titleText = (TextView) convertView.findViewById(R.id.tv_title);
            contentText = (TextView) convertView.findViewById(R.id.tv_content);
        }

        // 得到一个ViewHolder
        public static ViewHolder getViewHolder(View convertView) {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            return viewHolder;
        }
    }
}
