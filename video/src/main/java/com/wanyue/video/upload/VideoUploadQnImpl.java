package com.wanyue.video.upload;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.wanyue.common.bean.ConfigBean;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.utils.DecryptUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.video.api.VideoHttpConsts;
import com.wanyue.video.api.VideoAPI;

import org.json.JSONObject;

import java.io.File;

/**
 *  on 2018/5/21.
 * 视频上传 七牛云实现类
 */

public class VideoUploadQnImpl implements VideoUploadStrategy {

    private static final String TAG = "VideoUploadQnImpl";
    private VideoUploadBean mVideoUploadBean;
    private VideoUploadCallback mVideoUploadCallback;
    private String mToken;
    private UploadManager mUploadManager;
    private UpCompletionHandler mVideoUpCompletionHandler;//视频上传回调
    private UpCompletionHandler mImageUpCompletionHandler;//封面图片上传回调
    private String mQiNiuHost;
    private String mRegion;


    public VideoUploadQnImpl(ConfigBean configBean) {
        mQiNiuHost = configBean.getVideoQiNiuHost();//服务器返回七牛云分配的地址拼接回调返回的路径
        mQiNiuHost="";
        mVideoUpCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (mVideoUploadBean == null) {
                    return;
                }
                String videoResultUrl = mQiNiuHost + mVideoUploadBean.getVideoFile().getName();
                L.e(TAG, "视频上传结果-------->" + videoResultUrl);
                mVideoUploadBean.setResultVideoUrl(videoResultUrl);
                uploadFile(mVideoUploadBean.getImageFile(), mImageUpCompletionHandler);
            }
        };
        mImageUpCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (mVideoUploadBean == null) {
                    return;
                }
                String imageResultUrl = mQiNiuHost + mVideoUploadBean.getImageFile().getName();
                L.e(TAG, "图片上传结果-------->" + imageResultUrl);
                mVideoUploadBean.setResultImageUrl(imageResultUrl);
                if (mVideoUploadCallback != null) {
                    mVideoUploadCallback.onSuccess(mVideoUploadBean);
                }
            }
        };
    }

    @Override
    public void upload(VideoUploadBean bean, VideoUploadCallback callback) {
        if (bean == null || callback == null) {
            return;
        }
        mVideoUploadBean = bean;
        mVideoUploadCallback = callback;

        //向内部服务器获取七牛云的token


        VideoAPI.getQiNiuToken(new ParseHttpCallback<com.alibaba.fastjson.JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, com.alibaba.fastjson.JSONObject info) {
                mToken = info.getString("token");
                mToken = StringUtil.decryptUrl(mToken);
                uploadFile(mVideoUploadBean.getVideoFile(), mVideoUpCompletionHandler);
                mRegion=info.getString("region");
            }
        });
    }

    /**
     * 上传文件
     */
    private void uploadFile(File file, UpCompletionHandler handler) {
        if (TextUtils.isEmpty(mToken)) {
            return;
        }
        Zone zone=getZone();
        if (mUploadManager == null) {
         Configuration configuration = new Configuration.Builder().zone(zone).build();
         mUploadManager = new UploadManager(configuration);
        }
        mUploadManager.put(file, file.getName(), mToken, handler, null);
    }



    private Zone getZone() {
        if(StringUtil.equals("z0",mRegion)){
            return FixedZone.zone0;
        }else if(StringUtil.equals("z1",mRegion)){
            return FixedZone.zone1;
        } else if(StringUtil.equals("z1",mRegion)){
            return FixedZone.zone1;
        }else if(StringUtil.equals("z2",mRegion)){
            return FixedZone.zone2;
        }else if(StringUtil.equals("na0",mRegion)){
            return FixedZone.zoneNa0;
        }
        else if(StringUtil.equals("as0",mRegion)){
            return FixedZone.zoneAs0;
        }else if(StringUtil.equals("cn-east-2",mRegion)){
            return FixedZone.zoneFogCnEast1;
        }
        return FixedZone.zone0;
    }


    @Override
    public void cancel() {
        VideoAPI.cancel(VideoHttpConsts.GET_QI_NIU_TOKEN);
        mVideoUploadCallback = null;
    }

}
