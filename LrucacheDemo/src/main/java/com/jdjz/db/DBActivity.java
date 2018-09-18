package com.jdjz.db;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jdjz.SealUserInfoManager;
import com.jdjz.lrucachedemo.R;
import com.jude.utils.JUtils;

import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DBActivity extends AppCompatActivity {
    @BindView(R.id.btn_friends)
    Button btnFriends;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.btn_tv)
    Button btnTv;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.btn_getonefriendasync)
    Button btnGetonefriendasync;
    @BindView(R.id.btn_getonefriendsync)
    Button btnGetonefriendsync;
    @BindView(R.id.btn_mhfriendasync)
    Button btnMhfriendasync;
    @BindView(R.id.btn_mhfriendsync)
    Button btnMhfriendsync;
    String name2;
    List<Friend> fr2 = new ArrayList<>();
    @BindView(R.id.btn_delonefriendasync)
    Button btnDelonefriendasync;
    @BindView(R.id.btn_delonefriendsync)
    Button btnDelonefriendsync;
    @BindView(R.id.btn_delfriendsasync)
    Button btnDelfriendsasync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_friends, R.id.btn_getfriendsasync, R.id.btn_getfriendssync, R.id.btn_tv, R.id.btn_getonefriendasync, R.id.btn_getonefriendsync, R.id.btn_mhfriendasync, R.id.btn_mhfriendsync,R.id.btn_delonefriendasync, R.id.btn_delonefriendsync, R.id.btn_delfriendsasync})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_friends:
                SealUserInfoManager.getInstance().getAllUserInfo();
                break;
            case R.id.btn_getfriendsasync:  //异步获取所有好友

                SealUserInfoManager.getInstance().getFriends(new SealUserInfoManager.ResultCallback<List<Friend>>() {
                    @Override
                    public void onSuccess(List<Friend> friendsList) {
                  //      setText(friendsList, "");
                        if (friendsList!=null && friendsList.size()>0){
                            setText(friendsList, "");
                        }else{
                            setText(null, "异步获取所有好友为空");
                        }

                    }

                    @Override
                    public void onError(String errString) {
                        setText(null, errString);
                    }
                });

                break;

            case R.id.btn_getfriendssync:  //同步获取所有好友

                List<Friend> list = SealUserInfoManager.getInstance().syncGetFriends();
                if (list!=null && list.size()>0){
                    setText(list, "");
                }else{
                    setText(null, "同步获取所有好友为空");
                }


                break;
            case R.id.btn_tv:
                tv1.setText("");
                break;

            case R.id.btn_getonefriendasync: //异步
                String name = etName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    JUtils.Toast("请输入名字");
                    return;
                }
                SealUserInfoManager.getInstance().getFriendByName(name, new SealUserInfoManager.ResultCallback<Friend>() {
                    @Override
                    public void onSuccess(Friend friend) {
                        if (friend != null) {
                            tv1.setText(friend.getDisplayName());
                        }
                    }

                    @Override
                    public void onError(String errString) {
                        tv1.setText(errString);
                    }
                });

                break;
            case R.id.btn_getonefriendsync:  //同步
                String name1 = etName.getText().toString();
                if (TextUtils.isEmpty(name1)) {
                    JUtils.Toast("请输入名字");
                    return;
                }
                Friend fr = SealUserInfoManager.getInstance().getFriendByName(name1);
                if (fr != null) {
                    tv1.setText(fr.getDisplayName());
                } else {
                    JUtils.Toast("没有找到这个人");
                }
                break;

            case R.id.btn_mhfriendasync://模糊查找异步
                name2 = etName.getText().toString();
                if (TextUtils.isEmpty(name2)) {
                    JUtils.Toast("请输入名字");
                    return;
                }
                SealUserInfoManager.getInstance().getMhFriendByName(name2, new SealUserInfoManager.ResultCallback<List<Friend>>() {
                    @Override
                    public void onSuccess(List<Friend> friend) {
                        if (friend != null && fr2.size() > 0) {
                            setText(friend, "");
                        } else {
                            tv1.setText("异步模糊查询没有匹配任何数据");
                        }
                    }

                    @Override
                    public void onError(String errString) {
                        tv1.setText(errString);
                    }
                });


                break;
            case R.id.btn_mhfriendsync://模糊查找同步

                name2 = etName.getText().toString();
                if (TextUtils.isEmpty(name2)) {
                    JUtils.Toast("请输入名字");
                    return;
                }
                fr2 = SealUserInfoManager.getInstance().getMhFriendByName(name2);
                if (fr2 != null && fr2.size() > 0) {
                    //tv1.setText(fr.getDisplayName());
                    for (int i = 0; i < fr2.size(); i++) {
                        setText(fr2, "");
                    }
                } else {
                    tv1.setText("同步模糊查询没有匹配任何数据");
                }
                break;

            case R.id.btn_delonefriendasync:  //异步删除一个好友
                break;
            case R.id.btn_delonefriendsync:   //同步删除一个好友
                  //MovieCollect movieCollect;
                //mMovieCollectDao.delete(movieCollect);
                if(!TextUtils.isEmpty(etName.getText().toString())){
                    SealUserInfoManager.getInstance().deleteOneFriends(etName.getText().toString());

                }else{
                    JUtils.Toast("请输入userid");
                }
                break;
            case R.id.btn_delfriendsasync:    //异步删除所有好友
                SealUserInfoManager.getInstance().deleteFriends();
                break;
        }
    }

    private void setText(List<Friend> list, String errString) {
        String str = "";
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                str = str + list.get(i).getDisplayName() + ",  ";
            }
            tv1.setText(str);
        } else {
            tv1.setText(errString);
        }
    }

}
