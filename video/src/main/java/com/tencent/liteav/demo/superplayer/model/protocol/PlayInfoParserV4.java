package com.tencent.liteav.demo.superplayer.model.protocol;

import android.text.TextUtils;

import com.tencent.liteav.demo.superplayer.model.entity.EncryptedStreamingInfo;
import com.tencent.liteav.demo.superplayer.model.entity.PlayImageSpriteInfo;
import com.tencent.liteav.demo.superplayer.model.entity.PlayKeyFrameDescInfo;
import com.tencent.liteav.demo.superplayer.model.entity.ResolutionName;
import com.tencent.liteav.demo.superplayer.model.entity.VideoQuality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * V4视频协议解析实现类
 *
 * 负责解析V4视频信息协议请求响应的Json数据
 */
public class PlayInfoParserV4 implements IPlayInfoParser {

    private static final String TAG = "TCPlayInfoParserV4";

    private JSONObject mResponse;   // 协议请求返回的Json数据
    private String     mName;       // 视频名称
    private String     mURL;        // 未加密视频播放url
    private String     mToken;      // DRM token

    private List<EncryptedStreamingInfo> mEncryptedStreamingInfoList;// 加密视频播放url 数组
    private PlayImageSpriteInfo          mImageSpriteInfo;           // 雪碧图信息
    private List<PlayKeyFrameDescInfo>   mKeyFrameDescInfo;          // 关键帧信息
    private List<ResolutionName>         mResolutionNameList;        // 自适应码流画质名称匹配信息

    public PlayInfoParserV4(JSONObject response) {
        mResponse = response;
        parsePlayInfo();
    }

    private void parseSubStreams(JSONArray substreams) throws JSONException {
        if (substreams != null && substreams.length() > 0) {
            mResolutionNameList = new ArrayList<>();
            for (int i = 0; i < substreams.length(); i++) {
                JSONObject jsonObject = substreams.getJSONObject(i);
                ResolutionName resolutionName = new ResolutionName();
                int width = jsonObject.optInt("width");
                int height = jsonObject.optInt("height");
                resolutionName.width = width;
                resolutionName.height = height;
                resolutionName.name = jsonObject.optString("resolutionName");
                resolutionName.type = jsonObject.optString("type");
                mResolutionNameList.add(resolutionName);
            }
        }
    }

    /**
     * 从视频信息协议请求响应的Json数据中解析出视频信息
     */
    private void parsePlayInfo() {
        try {
            JSONObject media = mResponse.getJSONObject("media");
            if (media != null) {
                //解析视频名称
                JSONObject basicInfo = media.optJSONObject("basicInfo");
                if (basicInfo != null) {
                    mName = basicInfo.optString("name");
                }
                //解析视频播放url
                JSONObject streamingInfo = media.getJSONObject("streamingInfo");
                if (streamingInfo != null) {
                    JSONObject plainoutObj = streamingInfo.optJSONObject("plainOutput");//未加密的输出
                    if (plainoutObj != null) {
                        mURL = plainoutObj.optString("url");//未加密直接解析出视频url
                        parseSubStreams(plainoutObj.optJSONArray("subStreams"));
                    }
                    JSONArray drmoutputobj = streamingInfo.optJSONArray("drmOutput");//加密输出
                    if (drmoutputobj != null && drmoutputobj.length() > 0) {
                        mEncryptedStreamingInfoList = new ArrayList<>();
                        for (int i = 0; i < drmoutputobj.length(); i++) {
                            JSONObject jsonObject = drmoutputobj.optJSONObject(i);
                            String drmType = jsonObject.optString("type");
                            String url = jsonObject.optString("url");
                            EncryptedStreamingInfo info = new EncryptedStreamingInfo();
                            info.drmType = drmType;
                            info.url = url;
                            mEncryptedStreamingInfoList.add(info);
                            parseSubStreams(jsonObject.optJSONArray("subStreams"));
                        }
                    }
                    mToken = streamingInfo.optString("drmToken");
                }
                //解析雪碧图信息
                JSONObject imageSpriteInfo = media.optJSONObject("imageSpriteInfo");
                if (imageSpriteInfo != null) {
                    mImageSpriteInfo = new PlayImageSpriteInfo();
                    mImageSpriteInfo.webVttUrl = imageSpriteInfo.getString("webVttUrl");
                    JSONArray jsonArray = imageSpriteInfo.optJSONArray("imageUrls");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        List<String> imageUrls = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String url = jsonArray.getString(i);
                            imageUrls.add(url);
                        }
                        mImageSpriteInfo.imageUrls = imageUrls;
                    }
                }
                //解析关键帧信息
                JSONObject keyFrameDescInfo = media.optJSONObject("keyFrameDescInfo");
                if (keyFrameDescInfo != null) {
                    mKeyFrameDescInfo = new ArrayList<>();
                    JSONArray keyFrameDescList = keyFrameDescInfo.optJSONArray("keyFrameDescList");
                    if (keyFrameDescList != null && keyFrameDescList.length() > 0) {
                        for (int i = 0; i < keyFrameDescList.length(); i++) {
                            JSONObject jsonObject = keyFrameDescList.getJSONObject(i);
                            PlayKeyFrameDescInfo info = new PlayKeyFrameDescInfo();
                            info.time = jsonObject.optLong("timeOffset");
                            info.content = jsonObject.optString("content");
                            mKeyFrameDescInfo.add(info);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            //TXCLog.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * 获取视频播放url
     *
     * @return url字符串
     */
    @Override
    public String getURL() {
        String url = mURL;
        if (!TextUtils.isEmpty(mToken)) {
            url = getEncryptedURL(PlayInfoConstant.EncryptedURLType.SIMPLEAES);
        }
        return url;
    }

    @Override
    public String getEncryptedURL(PlayInfoConstant.EncryptedURLType type) {
        for (EncryptedStreamingInfo info : mEncryptedStreamingInfoList) {
            if (info.drmType != null && info.drmType.equalsIgnoreCase(type.getValue())) {
                return info.url;
            }
        }
        return null;
    }

    @Override
    public String getToken() {
        return TextUtils.isEmpty(mToken) ? null : mToken;
    }

    /**
     * 获取视频名称
     *
     * @return 视频名称字符串
     */
    @Override
    public String getName() {
        return mName;
    }

    /**
     * 获取雪碧图信息
     *
     * @return 雪碧图信息对象
     */
    @Override
    public PlayImageSpriteInfo getImageSpriteInfo() {
        return mImageSpriteInfo;
    }

    /**
     * 获取关键帧信息
     *
     * @return 关键帧信息数组
     */
    @Override
    public List<PlayKeyFrameDescInfo> getKeyFrameDescInfo() {
        return mKeyFrameDescInfo;
    }

    /**
     * 获取画质信息
     *
     * @return 画质信息数组
     */
    @Override
    public List<VideoQuality> getVideoQualityList() {
        return null;
    }

    /**
     * 获取默认画质信息
     *
     * @return 默认画质信息对象
     */
    @Override
    public VideoQuality getDefaultVideoQuality() {
        return null;
    }

    /**
     * 获取视频画质别名列表
     *
     * @return 画质别名数组
     */
    @Override
    public List<ResolutionName> getResolutionNameList() {
        return mResolutionNameList;
    }
}
