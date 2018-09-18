package com.jdjz.server;

import android.content.Context;
import android.text.TextUtils;

import com.jdjz.db.response.UserRelationshipResponse;
import com.jdjz.server.network.http.HttpException;
import com.jude.utils.JUtils;

import java.time.temporal.JulianFields;

public class SealAction extends BaseAction {

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public SealAction(Context context) {
        super(context);
    }


    /**
     * 获取发生过用户关系的列表
     *
     * @throws HttpException
     */
    public UserRelationshipResponse getAllUserRelationship() throws HttpException {
        UserRelationshipResponse response = null;
        String result = "      {\n" +
                "            \"code\" : 200,\n" +
                "                \"result\" : [\n" +
                "            {\"displayName\":\"李三\",\"message\":\"手机号:18622222222昵称:的用户请求添加你为好友\",\n" +
                "                    \"status\":20,\"updatedAt\":\"2016-01-07T06:22:55.000Z\",\n" +
                "                    \"user\":{\"id\":\"lisan\",\"nickname\":\"lisan\",\"portraitUri\":\"\"}},\n" +
                "            {\"displayName\":\"张四\",\"message\":\"手机号:18966666666昵称:的用户请求添加你为好友\",\n" +
                "                    \"status\":20,\"updatedAt\":\"2016-01-07T06:22:55.000Z\",\n" +
                "                    \"user\":{\"id\":\"zhangsi\",\"nickname\":\"zhangsi\",\"portraitUri\":\"\"}},\n" +
                "\n" +
                " {\"displayName\":\"张三\",\"message\":\"手机号:18966666666昵称:的用户请求添加你为好友\",\n" +
                "                    \"status\":20,\"updatedAt\":\"2016-01-07T06:22:55.000Z\",\n" +
                "                    \"user\":{\"id\":\"zhangsan\",\"nickname\":\"zhangsan\",\"portraitUri\":\"\"}}\n" +
                "\n" +
                "]}";

       String result1="      {\n" +
               "            \"code\" : 200,\n" +
               "                \"result\" : [\n" +
               "            {\"displayName\":\"李三\",\"message\":\"手机号:18622222222昵称:的用户请求添加你为好友\",\n" +
               "                    \"status\":20,\"updatedAt\":\"2016-01-07T06:22:55.000Z\",\n" +
               "                    \"user\":{\"id\":\"i3gRfA1ml\",\"nickname\":\"lisan\",\"portraitUri\":\"\"}},\n" +
               "            {\"displayName\":\"张四\",\"message\":\"手机号:18966666666昵称:的用户请求添加你为好友\",\n" +
               "                    \"status\":20,\"updatedAt\":\"2016-01-07T06:22:55.000Z\",\n" +
               "                    \"user\":{\"id\":\"1i3gRfA1ml\",\"nickname\":\"zhangsi\",\"portraitUri\":\"\"}}]}";

        if(!TextUtils.isEmpty(result)){
            response = jsonToBean(result, UserRelationshipResponse.class);
            JUtils.Log("size;"+response.getResult().size());
        }
        return response;
        /*String url = getURL("friendship/all");
        String result = httpManager.get(url);
        UserRelationshipResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, UserRelationshipResponse.class);
        }
        return response;*/
    }
}
