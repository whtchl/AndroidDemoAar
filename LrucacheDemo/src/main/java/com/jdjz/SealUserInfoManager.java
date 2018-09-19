package com.jdjz;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.jdjz.db.DBManager;
import com.jdjz.db.Friend;
import com.jdjz.db.FriendDao;
import com.jdjz.db.response.UserRelationshipResponse;
import com.jdjz.server.SealAction;
import com.jdjz.server.network.async.AsyncTaskManager;
import com.jdjz.server.network.async.OnDataListener;
import com.jdjz.server.network.http.HttpException;
import com.jude.utils.JUtils;

import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.List;


/**
 * 数据库访问接口,目前接口有同步和异步之分
 * 第一次login时从app server获取数据,之后数据库读,数据的更新使用IMKit里的通知类消息
 * 因为存在app server获取数据失败的情况,代码里有很多这种异常情况的判断处理并重新从app server获取数据
 * 1.add...类接口为插入或更新数据库
 * 2.get...类接口为读取数据库
 * 3.delete...类接口为删除数据库数据
 * 4.sync...为同步接口,因为存在去掉sync相同名称的异步接口,有些同步类接口不带sync
 * 5.fetch...,pull...类接口都是从app server获取数据并存数据库,不同的只是返回值不一样,此类接口全部为private
 */

public class SealUserInfoManager implements OnDataListener {
    private final static String TAG = "SealUserInfoManager";

    private static SealUserInfoManager sInstance;
    private final Context mContext;
    //private final AsyncTaskManager mAsyncTaskManager;
    private final SealAction action;
    private DBManager mDBManager;
    private Handler mWorkHandler;
    private HandlerThread mWorkThread;
    static Handler mHandler;
    private SharedPreferences sp;

    private FriendDao mFriendDao;

    public static SealUserInfoManager getInstance() {
        return sInstance;
    }

    public SealUserInfoManager(Context context) {
        mContext = context;
        //mAsyncTaskManager = AsyncTaskManager.getInstance(mContext);
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        action = new SealAction(mContext);
        mHandler = new Handler(Looper.getMainLooper());
        //mGroupsList = null;
    }

    public static void init(Context context) {
        sInstance = new SealUserInfoManager(context);
    }

    /**
     * 修改数据库的存贮路径为.../appkey/userID/,
     * 必须确保userID存在后才能初始化DBManager
     */
    public void openDB() {
        JUtils.Log(TAG, "SealUserInfoManager openDB");
        if (mDBManager == null || !mDBManager.isInitialized()) {
            mDBManager = DBManager.init(mContext);
            mWorkThread = new HandlerThread("sealUserInfoManager");
            mWorkThread.start();
            mWorkHandler = new Handler(mWorkThread.getLooper());
            mFriendDao = getFriendDao();

        }
    }

    public void closeDB() {
        JUtils.Log(TAG, "SealUserInfoManager closeDB");
        if (mDBManager != null) {
            mDBManager.uninit();
            mDBManager = null;
            mFriendDao = null;
        }
        if (mWorkThread != null) {
            mWorkThread.quit();
            mWorkThread = null;
            mWorkHandler = null;
        }

    }


    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    private FriendDao getFriendDao() {
        if (mDBManager != null && mDBManager.getDaoSession() != null) {
            return mDBManager.getDaoSession().getFriendDao();
        } else {
            return null;
        }
    }

    /**
     * 异步将friends 插入数据库
     */
    public void getAllUserInfo() {
        if (!JUtils.isNetWorkAvilable()) {
            JUtils.Toast("网络断开，请检查设置");
            return;
        }
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JUtils.Log(TAG, "线程名字和id：" + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                    if (!fetchFriends()) {
                        JUtils.Toast("Friends 异步 插入数据库成功");
                    } else {
                        JUtils.Toast("Friends 异步 插入数据库失败");
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private boolean fetchFriends() throws HttpException {
        UserRelationshipResponse userRelationshipResponse;
        try {
            userRelationshipResponse = action.getAllUserRelationship();
        } catch (JSONException e) {
            JUtils.Log(TAG, "fetchFriends occurs JSONException e=" + e.toString());
            return true;
        }
        if (userRelationshipResponse != null && userRelationshipResponse.getCode() == 200) {
            List<UserRelationshipResponse.ResultEntity> list = userRelationshipResponse.getResult();
            if (list != null && list.size() > 0) {
                syncDeleteFriends();
                addFriends(list);
            }
            //mGetAllUserInfoState |= FRIEND;
            return false;
        }
        return true;
    }

    /**
     * 同步接口,从server获取的好友信息插入数据库,目前只有同步接口,如果需要可以加异步接口
     *
     * @param list server获取的好友信息
     * @return List<Friend> 好友列表
     */
    private List<Friend> addFriends(final List<UserRelationshipResponse.ResultEntity> list) {
        if (list != null && list.size() > 0) {
            List<Friend> friendsList = new ArrayList<>();
            for (UserRelationshipResponse.ResultEntity resultEntity : list) {
                if (resultEntity.getStatus() == 20) {
                    Friend friend = new Friend(
                            resultEntity.getUser().getId(),
                            resultEntity.getUser().getNickname(),
                            Uri.parse(resultEntity.getUser().getPortraitUri()),
                            resultEntity.getDisplayName(),
                            null, null, null, null,
                            null, null);
                    /*if (friend.getPortraitUri() == null || TextUtils.isEmpty(friend.getPortraitUri().toString())) {
                        String portrait = getPortrait(friend);
                        if (portrait != null) {
                            friend.setPortraitUri(Uri.parse(getPortrait(friend)));
                        }
                    }*/
                    friendsList.add(friend);
                }
            }
            if (friendsList.size() > 0) {
                if (mFriendDao != null) {
                    mFriendDao.insertOrReplaceInTx(friendsList);
                }
            }
            return friendsList;
        } else {
            return null;
        }
    }

    private void syncDeleteFriends() {
        if (mFriendDao != null) {
            mFriendDao.deleteAll();
        }
    }


    /**
     * 异步接口,获取全部好友信息
     *
     * @param callback 获取好友信息的回调
     */
    public void getFriends(final ResultCallback<List<Friend>> callback) {
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {

                JUtils.Toast("异步获取好友的线程：" + Thread.currentThread().getName() + "  " + Thread.currentThread().getId());
                List<Friend> friendsList;
                if (JUtils.isNetWorkAvilable()) {
                    /*if (!isNetworkConnected()) {
                        onCallBackFail(callback);
                        return;
                    }*/
                    try {
                        friendsList = pullFriends();
                    } catch (HttpException e) {
                        onCallBackFail(callback);
                        JUtils.Log(TAG, "getFriends occurs HttpException e=" + e.toString());
                        return;
                    }
                } else {
                    JUtils.Toast("网络断开，异步从数据库获得数据");
                    friendsList = getFriendsDB();
                }
                if (callback != null) {
                    callback.onCallback(friendsList);
                }
            }
        });
    }

    //

    /**
     * 同步重服务器上获取数据:如果网络可达，同步重服务器服务器上获取数据，如果不可达，从数据库中得到数据
     *
     * @return
     */
    public List<Friend> syncGetFriends() {
        List<Friend> friendsList = null;
        JUtils.Toast("同步获取好友信息的线程：" + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        if (JUtils.isNetWorkAvilable()) {
           /* if (!isNetworkConnected()) {
                return null;
            }*/
            try {
                friendsList = pullFriends();
            } catch (HttpException e) {
                JUtils.Log(TAG, "getFriends occurs HttpException e=" + e.toString());
            }
        } else {
            JUtils.Toast("网络断开，同步从数据库获得数据");
            friendsList = getFriendsDB();
        }
        return friendsList;
    }

    /**
     * 同步接口,获取全部好友信息
     *
     * @return List<Friend> 好友列表
     */
    public List<Friend> getFriendsDB() {
        if (mFriendDao != null) {
            return mFriendDao.loadAll();
        } else {
            return null;
        }
    }

    private List<Friend> pullFriends() throws HttpException {
        List<Friend> friendsList = null;
        UserRelationshipResponse userRelationshipResponse;
        try {
            userRelationshipResponse = action.getAllUserRelationship();
        } catch (JSONException e) {
            JUtils.Log(TAG, "pullFriends occurs JSONException e=" + e.toString());
            return null;
        }
        if (userRelationshipResponse != null && userRelationshipResponse.getCode() == 200) {
            List<UserRelationshipResponse.ResultEntity> list = userRelationshipResponse.getResult();
            if (list != null && list.size() > 0) {
                syncDeleteFriends();
                friendsList = addFriends(list);
            }
        }
        return friendsList;
    }



    /**
     * 同步接口,精确获取1个好友信息
     *
     * @param mDisplayName 好友DisplayName
     * @return Friend 好友信息
     */
    public Friend getFriendByName(String mDisplayName) {
        if (TextUtils.isEmpty(mDisplayName)) {
            return null;
        } else {
            if (mFriendDao != null) {
                return mFriendDao.queryBuilder().where(FriendDao.Properties.DisplayName.eq(mDisplayName)).unique();
            } else {
                return null;
            }
        }
    }

    /**
     * 异步接口,精确获取1个好友信息
     *
     * @param mDisplayName   好友DisplayName
     * @param callback 获取好友信息回调
     */
    public void getFriendByName(final String mDisplayName, final ResultCallback<Friend> callback) {
        if (TextUtils.isEmpty(mDisplayName)) {
            if (callback != null)
                callback.onError(null);
        } else {
            mWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    Friend friend = null;
                    if (mFriendDao != null) {
                        friend = mFriendDao.queryBuilder().where(FriendDao.Properties.DisplayName.eq(mDisplayName)).unique();
//                     /   mFriendDao.queryBuilder().where((FriendDao.Properties.DisplayName.like()))
                    }
                    if (callback != null) {
                        if (friend != null) {
                            callback.onCallback(friend);
                        } else {
                            callback.onFail("Appointed UserInfo does not existed.");
                        }
                    }
                }
            });
        }
    }



    /**
     * 同步接口,模糊获取好友信息
     *
     * @param mDisplayName 好友DisplayName
     * @return Friend 好友信息
     */
    public List<Friend> getMhFriendByName(String mDisplayName) {
        if (TextUtils.isEmpty(mDisplayName)) {
            return null;
        } else {
            if (mFriendDao != null) {
                return mFriendDao.queryBuilder().where(FriendDao.Properties.DisplayName.like("%"+mDisplayName+"%")).list();
            } else {
                return null;
            }
        }
    }

    /**
     * 异步接口,模糊获取好友信息
     *
     * @param mDisplayName   好友DisplayName
     * @param callback 获取好友信息回调
     */
    public void getMhFriendByName(final String mDisplayName, final ResultCallback<List<Friend>> callback) {
        if (TextUtils.isEmpty(mDisplayName)) {
            if (callback != null)
                callback.onError(null);
        } else {
            mWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    List<Friend> friend = null;
                    if (mFriendDao != null) {
                        friend = mFriendDao.queryBuilder().where(FriendDao.Properties.DisplayName.like("%"+mDisplayName+"%")).list();
//                     /   mFriendDao.queryBuilder().where((FriendDao.Properties.DisplayName.like()))
                    }
                    if (callback != null) {
                        if (friend != null) {
                            callback.onCallback(friend);
                        } else {
                            callback.onFail("Appointed UserInfo list does not existed.");
                        }
                    }
                }
            });
        }
    }

    //异步删除所有好友
    public void deleteFriends() {
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mFriendDao != null) {
                    mFriendDao.deleteAll();
                    JUtils.Toast("删除所有好友成功");
                }
            }
        });
    }

    //同步删除一个好友
    public void deleteOneFriends(String userid){
        Friend friend = new Friend(userid,null,null);
        friend.setUserId(userid);
        mFriendDao.delete(friend);
        JUtils.Toast("删除当前一个好友成功");
    }

    /**
     * 同步更新一个好友
     * @param displayname
     * @return
     */
    public void updateOneFriend(String displayname){
        Friend friend =  getFriendByName(displayname);

        if(friend != null){
            friend.setDisplayName("李三1");
            mFriendDao.update(friend);
            JUtils.Toast("更新 李三 -> 李三1 成功");
        }else{
            JUtils.Toast("更新 李三 -> 李三1 失败");
        }
    }

    /**
     * 异步更新数据库（更新的是list）
     * @param callback
     */
    public void updateFriends(final ResultCallback<String> callback) {
        final Friend lisan =  getFriendByName("李三");
        final Friend zhangsi = getFriendByName("张四");


        if (lisan ==null || zhangsi == null) {
            if (callback != null)
                callback.onError("没有找到李三或张四 这个好友");
        } else {
            mWorkHandler.post(new Runnable() {
                @Override
                public void run() {
                    List<Friend> friends = new ArrayList<>();
                    lisan.setDisplayName("李三2");
                    zhangsi.setDisplayName("张四2");
                    friends.add(lisan);
                    friends.add(zhangsi);
                    if (mFriendDao != null) {
                        mFriendDao.updateInTx(friends);
                       // friend = mFriendDao.queryBuilder().where(FriendDao.Properties.DisplayName.like("%"+mDisplayName+"%")).list();
                    }
                    if (callback != null) {
                        callback.onCallback("更新成功  李三-》李三2； 张四-》张四2");
                    }
                }
            });
        }
    }

    /**
     * 同步添加一个好友
     *
     * @return
     */

    public Long  addOneFriend(){
        Friend friend =  new Friend("wangwu","name王五",null,null,"王五");
         return mFriendDao.insertOrReplace(friend);
    }


    /**
     * 异步添加一组好友
     * @param
     */
    public void addFriendList(final ResultCallback<String> callback) {
        Friend friend1=  new Friend("qianliu","name钱六",null,null,"钱六");
        Friend friend2 =  new Friend("gaoqi","name高七",null,null,"高七");
        final List<Friend> ll = new ArrayList<>();
        ll.add(friend1);
        ll.add(friend2);
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                if(ll == null || ll.size()==0){
                    if(callback != null){
                        callback.onFail("添加失败：好友数组为空或列表长度为0");

                    }
                }else{
                    mFriendDao.insertOrReplaceInTx(ll);
                    if(callback != null){
                        callback.onSuccess("添加好友列表成功");
                    }

                }
            }
        });
    }

    /**
     * 泛型类，主要用于 API 中功能的回调处理。
     *
     * @param <T> 声明一个泛型 T。
     */
    public static abstract class ResultCallback<T> {

        public static class Result<T> {
            public T t;
        }

        public ResultCallback() {

        }

        /**
         * 成功时回调。
         *
         * @param t 已声明的类型。
         */
        public abstract void onSuccess(T t);

        /**
         * 错误时回调。
         *
         * @param errString 错误提示
         */
        public abstract void onError(String errString);


        public void onFail(final String errString) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    JUtils.Toast("onFail 线程 "+ Thread.currentThread().getName()+"  "+Thread.currentThread().getId());
                    onError(errString);
                }
            });
        }

        public void onCallback(final T t) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    JUtils.Toast("onCallback 线程 "+ Thread.currentThread().getName()+"  "+Thread.currentThread().getId());
                    onSuccess(t);
                }
            });
        }
    }


    private void onCallBackFail(ResultCallback<?> callback) {
        if (callback != null) {
            callback.onFail(null);
        }

    }
}
