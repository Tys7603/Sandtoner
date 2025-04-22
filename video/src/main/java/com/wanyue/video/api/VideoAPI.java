package com.wanyue.video.api;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.bean.CatBean;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.http.BaseHttpCallBack;
import com.wanyue.common.http.CommonHttpUtil;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.HttpClient;
import com.wanyue.common.server.MapBuilder;
import com.wanyue.common.server.RequestFactory;
import com.wanyue.common.utils.JsonUtil;
import com.wanyue.common.utils.MD5Util;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.video.bean.VideoBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 *  on 2018/9/17.
 */

public class VideoAPI {
    public static final String SHOP_SHELF_LIST ="shoplist" ;//上架商品

    private static final String VIDEO_SALT = "#2hgfk85cm23mk58vncsark";

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }



    //获取视频分类
    public static Observable<List<CatBean>> getCat(int type){
        Map<String,Object> map= MapBuilder.factory().
                put("type",type).
                build();
        return RequestFactory.getRequestManager().get("videocat",map,CatBean.class,false);
    }

    //获取视频列表
    public static Observable<List<VideoBean>> getVideoList(String catid,int page){
        Map<String,Object> map= MapBuilder.factory().
                put("catid",catid).
                put("page",page).
                build();
        return RequestFactory.getRequestManager().get("videolist",map,VideoBean.class,false);
    }

    //获取视频列表
    public static Observable<List<VideoBean>> getMyVideoList(int page){
        Map<String,Object> map= MapBuilder.factory().
                put("page",page).
                build();
        return RequestFactory.getRequestManager().get("myvideo",map,VideoBean.class,false);
    }


    /**
     * 获取首页视频列表
     */
    public static void getHomeVideoList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Video.GetVideoList", VideoHttpConsts.GET_HOME_VIDEO_LIST)
                .params("uid", CommonAppConfig.getUid())
                .params("token", CommonAppConfig.getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 视频点赞
     */
    public static void setVideoLike( String videoid,int type, BaseHttpCallBack callback) {
        HttpClient.getInstance().post("videolike","videolike")
                .params("vid",videoid)
                .params("type",type)
                .execute(callback);
    }

    public static void getVideoDetail( String videoid, BaseHttpCallBack callback) {
        HttpClient.getInstance().get("videodetail","videodetail")
                .params("videoid",videoid)
                .execute(callback);
    }


    /**
     * 获取视频评论
     */

    public static void getVideoCommentList(String videoid,String cid, String lastid, HttpCallback callback) {
        HttpClient.getInstance().get("videocomment", VideoHttpConsts.GET_VIDEO_COMMENT_LIST)
                .params("vid", videoid)
                .params("cid", cid)
                .params("lastid", lastid)
                .execute(callback);
    }

    /**
     * 获取评论回复
     */

    public static void getCommentReply(String videoid, String cid,String lastId, HttpCallback callback) {
        HttpClient.getInstance().get("videocomment", VideoHttpConsts.GET_COMMENT_REPLY)
                .params("vid", videoid)
                .params("cid", cid)
                .params("lastid", lastId)
                .execute(callback);
    }




    /**
     * 评论点赞
     */
    public static void setCommentLike(String commentid,int type, HttpCallback callback) {
        HttpClient.getInstance().post("videocommentlike", VideoHttpConsts.SET_COMMENT_LIKE)
                .params("cid", commentid)
                .params("type",type)
                .execute(callback);
    }

    /**
     * 发表评论
     */

    public static void setComment(String toUid, String videoId, String content, String commentId, String parentId, BaseHttpCallBack callback) {
        HttpClient.getInstance().post("videosetcommnet", VideoHttpConsts.SET_COMMENT)
                .params("touid", toUid)
                .params("vid", videoId)
                .params("cid", commentId)
                .params("pid", parentId)
                .params("content", content)
                .params("at_info", "")
                .execute(callback);
    }















    /**
     * 上传视频，获取七牛云token的接口
     */

    public static void getQiNiuToken(BaseHttpCallBack callback) {
        HttpClient.getInstance().get("getupload", VideoHttpConsts.GET_QI_NIU_TOKEN)
                .execute(callback);
    }




    public static void saveUploadVideoInfo(String catid,String title, String thumb, String url, String goodsId, BaseHttpCallBack callback) {
        HttpClient.getInstance().post("setvideo", VideoHttpConsts.SAVE_UPLOAD_VIDEO_INFO)
                .params("catid",catid)
                .params("name", title)
                .params("thumb", thumb)
                .params("url", url)
                .params("goodsid", goodsId)
                .execute(callback);
    }

    /**
     * 获取腾讯云储存上传签名
     */
    public static void getTxUploadCredential(StringCallback callback) {
        OkGo.<String>get("http://upload.qq163.iego.cn:8088/cam")
                .execute(callback);
    }

    /**
     * 获取某人发布的视频
     */
    public static void getHomeVideo(String toUid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Video.getHomeVideo", VideoHttpConsts.GET_HOME_VIDEO)
                .params("uid", CommonAppConfig.getUid())
                .params("token", CommonAppConfig.getToken())
                .params("touid", toUid)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取举报内容列表
     */
    public static void getVideoReportList(HttpCallback callback) {
        HttpClient.getInstance().get("Video.getReportContentlist", VideoHttpConsts.GET_VIDEO_REPORT_LIST)
                .execute(callback);
    }

    /**
     * 举报视频接口
     */
    public static void videoReport(String videoId, String reportId, String content, HttpCallback callback) {
        HttpClient.getInstance().get("Video.report", VideoHttpConsts.VIDEO_REPORT)
                .params("uid", CommonAppConfig.getUid())
                .params("token", CommonAppConfig.getToken())
                .params("videoid", videoId)
                .params("type", reportId)
                .params("content", content)
                .execute(callback);
    }

    /**
     * 删除自己的视频
     */
    public static void videoDelete(String videoid, HttpCallback callback) {
        HttpClient.getInstance().post("videodel", VideoHttpConsts.VIDEO_DELETE)
                .params("vid",videoid)
                .execute(callback);
    }



    public static void setVideoShare(String videoid, HttpCallback callback) {
        ArrayMap<String,Object>map=new ArrayMap<>();
        map.put("vid",videoid);
        String sign= StringUtil.createSign(map,"vid");
        HttpClient.getInstance().post("videoshare", VideoHttpConsts.SET_VIDEO_SHARE)
                .params("vid", videoid)
                .params("sign", sign)
                .execute(callback);
    }

    public static void setPlays(String videoid, HttpCallback callback) {
        ArrayMap<String,Object>map=new ArrayMap<>();
        map.put("vid",videoid);
        String sign= StringUtil.createSign(map,"vid");
        HttpClient.getInstance().post("setPlays","setPlays")
                .params("vid", videoid)
                .params("sign", sign)
                .execute(callback);
    }


    public static void getCon(HttpCallback callback) {
        HttpClient.getInstance().get("Video.getCon", VideoHttpConsts.GETCON)
                .params("uid", CommonAppConfig.getUid())
                .execute(callback);
    }




    /**
     * 开始观看视频的时候请求这个接口
     */
    public static void videoWatchStart(String videoUid, String videoId) {

    }

    /**
     * 完整观看完视频后请求这个接口
     */

    public static void videoWatchEnd(String videoUid, String videoId) {

    }





 /*
       上架商品列表
    * */

    public static void getShelfGoodsList(String keyword, int p,BaseHttpCallBack callBack){
        HttpClient.getInstance().get(SHOP_SHELF_LIST,SHOP_SHELF_LIST)
                .params("page",p)
                .params("keyword",keyword)
                .execute(callBack);
    }

}




