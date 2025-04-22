package com.wanyue.course.api;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.bean.CatBean;
import com.wanyue.common.bean.CoinPayBean;
import com.wanyue.common.http.BaseHttpCallBack;
import com.wanyue.common.http.HttpClient;
import com.wanyue.common.server.MapBuilder;
import com.wanyue.common.server.RequestFactory;
import com.wanyue.common.server.entity.BaseOriginalResponse;
import com.wanyue.common.utils.JsonUtil;
import com.wanyue.course.bean.CourseBean;
import com.wanyue.course.bean.CourseLevel1Bean;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.bean.RecordBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class CourseAPI {

    //课程分类
    public static Observable<List<CatBean>> getCat(){
        Map<String,Object> map= MapBuilder.factory().
                build();
        return RequestFactory.getRequestManager().valueGet("courseindex",map, JSONObject.class,false).map(new Function<JSONObject, List<CatBean>>() {
            @Override
            public List<CatBean> apply(@NonNull JSONObject jsonObject) throws Exception {
                JSONArray jsonArray =jsonObject.getJSONArray("cat");
                List<CatBean>list= JsonUtil.getData(jsonArray,CatBean.class);
                return list;
            }
        });
    }


    //课程列表
    public static Observable<List<ProjectBean>> get(String catid, int page){
        Map<String,Object> map= MapBuilder.factory().
                put("catid",catid).
                put("page",page).
                build();
        return RequestFactory.getRequestManager().get("courselist",map, CourseBean.class,false)
                .map(new Function<List<CourseBean>, List<ProjectBean>>() {
                    @Override
                    public List<ProjectBean> apply(@NonNull List<CourseBean> courseBeans) throws Exception {
                       List<ProjectBean> data=new ArrayList<>();
                       for(ProjectBean value:courseBeans){
                           data.add(value);
                       }
                       return data;
                    }
                });
    }

    /*提交评论*/
    public static Observable<Boolean>addComment(String courseid,float star,String content){
        Map<String,Object> parm= MapBuilder.factory()
                .put("courseid",courseid)
                .put("star",star)
                .put("content",content)
                .build();
        return RequestFactory.getRequestManager().commit("coursesetcomment",parm,true);
    }


    /*评论列表*/
    public static Observable<JSONObject>getEvaluateList(String projectId,int p){
        Map<String,Object> parm= MapBuilder.factory()
                .put("courseid",projectId)
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().valueGet("coursecomment",parm,JSONObject.class,false);
    }


    /*获取项目详情*/
    public static <T extends ProjectBean> Observable<T>getProjectDetail(String projectId,final Class<T>cs){
        Map<String,Object> parm= MapBuilder.factory()
                .put("courseid",projectId)
                .build();

        return RequestFactory.getRequestManager().valueGet("coursedetail",parm,JSONObject.class,false)
                .map(new Function<JSONObject, T>() {
                    @Override
                    public T apply(@NonNull JSONObject jsonObject) throws Exception {
                        T t=jsonObject.toJavaObject(cs);
                        t.setExtra(jsonObject);
                        return t;
                    }
                });

    }

    /*获取课程目录*/
    public static Observable<List<CourseLevel1Bean>>getLessonList(String courseid){
        Map<String,Object> parm= MapBuilder.factory()
                .put("courseid",courseid)
                .build();
        return RequestFactory.getRequestManager().get("lessonlist",parm, CourseLevel1Bean.class,false);
    }



    /*获取课程目录*/
    public static Observable<List<RecordBean>>getRecordList(int p){
        Map<String,Object> parm= MapBuilder.factory()
                .put("page",p)
                .build();
        return RequestFactory.getRequestManager().get("courseorder",parm, RecordBean.class,false);
    }


    //获取课时详情
    public static void getLessonDetail(String courseid,BaseHttpCallBack baseHttpCallBack){
        HttpClient.getInstance().get("lessondetail","lessondetail")
                .params("lessonid",courseid)
                .execute(baseHttpCallBack);
    }

    //订单支付
    public static void pay(String id, String paytype, BaseHttpCallBack baseHttpCallBack){
        HttpClient.getInstance().post("coursebuy","coursebuy")
                .params("courseid",id)
                .params("payType",paytype)
                .params("from","android")
                .execute(baseHttpCallBack);
    }

    public static Observable<List<ProjectBean>> getMyCourse(int p) {
        Map<String,Object> map= MapBuilder.factory().
                put("page",p).
                build();
        return RequestFactory.getRequestManager().get("mycourse",map, CourseBean.class,false)
                .map(new Function<List<CourseBean>, List<ProjectBean>>() {
                    @Override
                    public List<ProjectBean> apply(@NonNull List<CourseBean> courseBeans) throws Exception {
                        List<ProjectBean> data=new ArrayList<>();
                        for(ProjectBean value:courseBeans){
                            data.add(value);
                        }
                        return data;
                    }
                });
    }
}


