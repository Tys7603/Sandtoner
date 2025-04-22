package com.wanyue.main.integral.api;

import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.server.MapBuilder;
import com.wanyue.common.server.RequestFactory;
import com.wanyue.main.integral.bean.SignRecordBean;
import com.wanyue.main.integral.bean.WeekSignBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.reactivex.Observable;

public class SignAPI {

    //开始签名
    public static Observable<JSONObject> startSign() {
        Map<String,Object> map= MapBuilder.factory()
                .build();
        return RequestFactory.getRequestManager().valuePost("setSign",map,JSONObject.class,false);
    }

    //获取签名记录
    public static Observable<SignRecordBean>getSignRecord(){
        Map<String,Object> map= MapBuilder.factory()
                .build();
        return RequestFactory.getRequestManager().valueGet("getSign",map,SignRecordBean.class,false);
    }
}


