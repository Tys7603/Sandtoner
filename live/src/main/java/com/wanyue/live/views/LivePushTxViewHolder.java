package com.wanyue.live.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meihu.beauty.utils.MhDataManager;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.Constants;
import com.wanyue.common.interfaces.CommonCallback;
import com.wanyue.common.utils.BitmapUtil;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.live.R;
import com.wanyue.live.activity.LiveActivity;
import com.wanyue.live.bean.LiveReceiveGiftBean;
import com.wanyue.live.http.LiveHttpConsts;
import com.wanyue.live.http.LiveHttpUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 2018/10/7.
 * 腾讯云直播推流
 */

public class LivePushTxViewHolder extends AbsLivePushViewHolder implements ITXLivePushListener {

    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private int mMeiBaiVal;//基础美颜 美白
    private int mMoPiVal;//基础美颜 磨皮
    private int mHongRunVal;//基础美颜 红润
    private Bitmap mFilterBmp;
    //    private String mBgmPath;//背景音乐路径
    private boolean mIsPlayGiftSticker;//主播是否在播放道具礼物
    private Handler mHandler;
    private ConcurrentLinkedQueue<LiveReceiveGiftBean> mQueue;
    private String mCurStickerName;
    private String mNoStickerName;


    protected int[] txBeautyData; //美颜参数数组

    public LivePushTxViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_push_tx;
    }

    @Override
    public void init() {
        super.init();
        mLivePusher = new TXLivePusher(mContext);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoFPS(15);//视频帧率
        mLivePushConfig.setVideoEncodeGop(1);//GOP大小
        // mLivePushConfig.setVideoBitrate(1800);
        mLivePushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
        mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_HARDWARE);//硬件加速
        mLivePushConfig.setCustomModeType(TXLiveConstants.CUSTOM_MODE_VIDEO_PREPROCESS);
        Bitmap bitmap = decodeResource(mContext.getResources(), R.mipmap.bg_live_tx_pause);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setTouchFocus(false);//自动对焦
        //mLivePushConfig.setANS(true);//噪声抑制
//        mLivePushConfig.enableAEC(true);//开启回声消除：连麦时必须开启，非连麦时不要开启
        mLivePushConfig.setAudioSampleRate(48000);
        mLivePushConfig.setAudioChannels(1);//声道数量
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setMirror(true);
        mLivePusher.setPushListener(this);
        mLivePusher.setBGMVolume(1f);
        mLivePusher.setMicVolume(4f);
        if (
                CommonAppConfig.isTiBeautyEnable()) {
                mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
                @Override
                public int onTextureCustomProcess(int texture, int width, int height) {
                    int textureId = MhDataManager.getInstance().render(texture, width, height);
                    return textureId;
                }

                @Override
                public void onDetectFacePoints(float[] floats) {
                }

                @Override
                public void onTextureDestoryed() {

                }
            });

            String beautyKey =
                    CommonAppConfig.getBeautyKey();
            if (!TextUtils.isEmpty(beautyKey)) {

            }
//            mNoStickerName = WordUtil.getString("");
//            mCurStickerName = mNoStickerName;
        }
        mPreView = findViewById(R.id.camera_preview);
        mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);
        setBeautyByConfig();
    }

    /*
     根据后台参数设置美颜
     */
    private void setBeautyByConfig() {
        if (CommonAppConfig.isTiBeautyEnable()) {

        }
    }

    private boolean setTxFilter() {
        return mLivePusher.setBeautyFilter(TXLiveConstants.BEAUTY_STYLE_SMOOTH, txBeautyData[0], txBeautyData[1], txBeautyData[2]);
    }

    @Override
    public void changeToLeft() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = mPreView.getWidth() / 2;
            params.height = DpUtil.dp2px(250);
            params.topMargin = DpUtil.dp2px(130);
            mPreView.setLayoutParams(params);
        }
    }

    @Override
    public void changeToBig() {
        if (mPreView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPreView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mPreView.setLayoutParams(params);
        }
    }


    /**
     * 切换镜头
     */
    @Override
    public void toggleCamera() {
        if (mLivePusher != null) {
            if (mFlashOpen) {
                toggleFlash();
            }
            mLivePusher.switchCamera();
            mCameraFront = !mCameraFront;
            mLivePusher.setMirror(mCameraFront);
        }
    }

    /**
     * 打开关闭闪光灯
     */
    @Override
    public void toggleFlash() {
        if (mCameraFront) {
            ToastUtil.show(R.string.live_open_flash);
            return;
        }
        if (mLivePusher != null) {
            boolean open = !mFlashOpen;
            if (mLivePusher.turnOnFlashLight(open)) {
                mFlashOpen = open;
            }
        }
    }

    /**
     * 开始推流
     *
     * @param pushUrl 推流地址
     */
    @Override
    public void startPush(String pushUrl) {
        if (mLivePusher != null) {
            mLivePusher.startPusher(pushUrl);
        }
        startCountDown();
    }


    @Override
    public void onPause() {
        mPaused = true;
        if (mStartPush && mLivePusher != null) {
            mLivePusher.pauseBGM();
            mLivePusher.pausePusher();
        }
    }

    @Override
    public void onResume() {
        if (mPaused && mStartPush && mLivePusher != null) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
        mPaused = false;
    }

    @Override
    public void startBgm(String path) {
        if (mLivePusher != null) {
//            boolean result = mLivePusher.playBGM(path);
//            if (result) {
//                mBgmPath = path;
//            }
            mLivePusher.playBGM(path);
        }
    }

    @Override
    public void pauseBgm() {
        if (mLivePusher != null) {
            mLivePusher.pauseBGM();
        }
    }

    @Override
    public void resumeBgm() {
        if (mLivePusher != null) {
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void stopBgm() {
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
        }
//        mBgmPath = null;
    }



    @Override
    protected void onCameraRestart() {
        if (mLivePusher != null && mPreView != null) {
            mLivePusher.startCameraPreview((TXCloudVideoView) mPreView);
        }
    }

//
//    @Override
//    public DefaultBeautyEffectListener getDefaultEffectListener() {
//        return null;
//    }
//
//    @Override
//    public SimpleBeautyEffectListener getSimpleBeautyEffectListener() {
//        return null;
//    }

    @Override
    public void release() {
        super.release();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        LiveHttpUtil.cancel(LiveHttpConsts.LINK_MIC_TX_MIX_STREAM);
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
            mLivePusher.stopPusher();
            mLivePusher.stopScreenCapture();
            mLivePusher.stopCameraPreview(false);
            mLivePusher.setVideoProcessListener(null);
            mLivePusher.setBGMNofify(null);
            mLivePusher.setPushListener(null);
        }
        mLivePusher = null;
        if (mLivePushConfig != null) {
            mLivePushConfig.setPauseImg(null);
        }
        mLivePushConfig = null;
    }

    @Override
    public void onPushEvent(int e, Bundle bundle) {
        L.e("e==" + e + "&&&+bundle==" + bundle.toString());
        if (e == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
            ToastUtil.show(R.string.live_push_failed_1);

        } else if (e == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
            ToastUtil.show(R.string.live_push_failed_2);

        } else if (e == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || e == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
            L.e(TAG, "网络断开，推流失败------>");

        } else if (e == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            L.e(TAG, "不支持硬件加速------>");
            if (mLivePushConfig != null && mLivePusher != null) {
                mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
                mLivePusher.setConfig(mLivePushConfig);
            }
        } else if (e == TXLiveConstants.PUSH_EVT_FIRST_FRAME_AVAILABLE) {//预览成功
            L.e(TAG, "mStearm--->初始化完毕");
            if (mLivePushListener != null) {
                mLivePushListener.onPreviewStart();
            }
        } else if (e == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {//推流成功
            L.e(TAG, "mStearm--->推流成功");
            if (!mStartPush) {
                mStartPush = true;
                if (mLivePushListener != null) {
                    mLivePushListener.onPushStart();
                }
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * 腾讯sdk连麦时候主播混流
     *
     * @param linkMicType 混流类型 1主播与主播连麦  0 用户与主播连麦
     * @param toStream    对方的stream
     */
    public void onLinkMicTxMixStreamEvent(int linkMicType, String toStream) {
        String txAppId = getTxAppId();
        String selfAnchorStream = getLiveStream();
        if (TextUtils.isEmpty(txAppId) || TextUtils.isEmpty(selfAnchorStream)) {
            return;
        }
        String mixParams = null;
        if (linkMicType == Constants.LINK_MIC_TYPE_NORMAL) {
            mixParams = createMixParams(txAppId, selfAnchorStream, toStream);
        } else if (linkMicType == Constants.LINK_MIC_TYPE_ANCHOR) {
            mixParams = createMixParams2(txAppId, selfAnchorStream, toStream);
        }
        if (TextUtils.isEmpty(mixParams)) {
            return;
        }
        LiveHttpUtil.linkMicTxMixStream(mixParams);
    }


    /**
     * 计算混流参数 观众与主播连麦
     *
     * @param txAppId          腾讯云appId
     * @param selfAnchorStream 自己主播的stream
     * @param toStream         对方的stream
     * @return
     */
    private String createMixParams(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject obj = new JSONObject();
        long timestamp = System.currentTimeMillis() / 1000;
        obj.put("timestamp", timestamp);
        obj.put("eventId", timestamp);
        JSONObject interFace = new JSONObject();
        interFace.put("interfaceName", "Mix_StreamV2");
        JSONObject para = new JSONObject();
        para.put("app_id", txAppId);
        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
        para.put("mix_stream_session_id", selfAnchorStream);
        para.put("output_stream_id", selfAnchorStream);
        JSONArray array = new JSONArray();
        JSONObject mainAnchor = new JSONObject();//大主播
        mainAnchor.put("input_stream_id", selfAnchorStream);
        JSONObject mainLayoutParams = new JSONObject();
        mainLayoutParams.put("image_layer", 1);
        mainAnchor.put("layout_params", mainLayoutParams);
        array.add(mainAnchor);

        if (!TextUtils.isEmpty(toStream)) {
            JSONObject smallAnchor = new JSONObject();//小主播
            smallAnchor.put("input_stream_id", toStream);
            JSONObject smallLayoutParams = new JSONObject();
            smallLayoutParams.put("image_layer", 2);
            smallLayoutParams.put("image_width", 0.25);
            smallLayoutParams.put("image_height", 0.21);
            smallLayoutParams.put("location_x", 0.75);
            smallLayoutParams.put("location_y", 0.6);
            smallAnchor.put("layout_params", smallLayoutParams);
            array.add(smallAnchor);
        }

        para.put("input_stream_list", array);
        interFace.put("para", para);
        obj.put("interface", interFace);
        return obj.toString();
    }


    /**
     * 计算混流参数 主播与主播连麦
     *
     * @param txAppId          腾讯云appId
     * @param selfAnchorStream 自己主播的stream
     * @param toStream         对方的stream
     * @return
     */
    private String createMixParams2(String txAppId, String selfAnchorStream, String toStream) {
        JSONObject obj = new JSONObject();
        long timestamp = System.currentTimeMillis() / 1000;
        obj.put("timestamp", timestamp);
        obj.put("eventId", timestamp);
        JSONObject interFace = new JSONObject();
        interFace.put("interfaceName", "Mix_StreamV2");
        JSONObject para = new JSONObject();
        para.put("app_id", txAppId);
        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
        para.put("mix_stream_session_id", selfAnchorStream);
        para.put("output_stream_id", selfAnchorStream);
        JSONArray array = new JSONArray();


        if (!TextUtils.isEmpty(toStream)) {

            JSONObject bg = new JSONObject();//背景
            bg.put("input_stream_id", "canvas1");//背景的id,这个字符串随便写
            JSONObject bgLayoutParams = new JSONObject();
            bgLayoutParams.put("image_layer", 1);
            bgLayoutParams.put("input_type", 3);
            bg.put("layout_params", bgLayoutParams);
            array.add(bg);

            JSONObject selfAnchor = new JSONObject();//自己主播
            selfAnchor.put("input_stream_id", selfAnchorStream);
            JSONObject selfLayoutParams = new JSONObject();
            selfLayoutParams.put("image_layer", 2);
            selfLayoutParams.put("image_width", 0.5);
            selfLayoutParams.put("image_height", 0.5);
            selfLayoutParams.put("location_x", 0);
            selfLayoutParams.put("location_y", 0.25);
            selfAnchor.put("layout_params", selfLayoutParams);
            array.add(selfAnchor);

            JSONObject toAnchor = new JSONObject();//对方主播
            toAnchor.put("input_stream_id", toStream);
            JSONObject toLayoutParams = new JSONObject();
            toLayoutParams.put("image_layer", 3);
            toLayoutParams.put("image_width", 0.5);
            toLayoutParams.put("image_height", 0.5);
            toLayoutParams.put("location_x", 0.5);
            toLayoutParams.put("location_y", 0.25);
            toAnchor.put("layout_params", toLayoutParams);
            array.add(toAnchor);
        } else {
            JSONObject mainAnchor = new JSONObject();//大主播
            mainAnchor.put("input_stream_id", selfAnchorStream);
            JSONObject mainLayoutParams = new JSONObject();
            mainLayoutParams.put("image_layer", 1);
            mainAnchor.put("layout_params", mainLayoutParams);
            array.add(mainAnchor);
        }

        para.put("input_stream_list", array);
        interFace.put("para", para);
        obj.put("interface", interFace);
        return obj.toString();
    }

    private String getLiveStream() {
        return ((LiveActivity) mContext).getStream();
    }

    private String getTxAppId() {
        return ((LiveActivity) mContext).getTxAppId();
    }


//    private StickerServiceBean findStickerBean(String stickerId) {
//        if (TextUtils.isEmpty(stickerId)) {
//            return null;
//        }
//        for (StickerServiceBean bean : mStickerList) {
//            if (stickerId.equals(bean.getId())) {
//                return bean;
//            }
//        }
//        return null;
//    }

    /**
     * 道具礼物贴纸
     */
    public void setLiveStickerGift(LiveReceiveGiftBean receiveGiftBean) {
//        if (receiveGiftBean == null
//                || receiveGiftBean.getPlayTime() <= 0
//                || mMhSDKManager == null
//                || mStickerList == null
//                || mStickerList.size() == 0) {
//            return;
//        }
//        StickerServiceBean targetBean = findStickerBean(receiveGiftBean.getStickerId());
//        if (targetBean == null) {
//            return;
//        }
//        if (mIsPlayGiftSticker) {
//            if (mQueue == null) {
//                mQueue = new ConcurrentLinkedQueue<>();
//            }
//            mQueue.offer(receiveGiftBean);
//        } else {
////            showStickerBean(targetBean, (long) (receiveGiftBean.getPlayTime() * 1000));
//        }
    }


    /**
     * 道具礼物贴纸
     */
    private void setStickerTimeout(long timeout) {
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    getNextStickerGift();
                }
            };
        }
        mHandler.sendEmptyMessageDelayed(0, timeout);
    }


    /**
     * 获取下一个道具礼物贴纸
     */
    private void getNextStickerGift() {
        if (mQueue != null) {
            LiveReceiveGiftBean nextGiftBean = mQueue.poll();
            if (nextGiftBean == null) {
                endSticker();
            } else {
                if (nextGiftBean.getPlayTime() <= 0) {
                    getNextStickerGift();
                } else {
//                    StickerServiceBean nextStickerBean = findStickerBean(nextGiftBean.getStickerId());
//                    long playTime = (long) (nextGiftBean.getPlayTime() * 1000);
//                    if (nextStickerBean == null) {
//                        getNextStickerGift();
//                    } else {
//                        String nextStickerName = nextStickerBean.getName();
//                        if (TextUtils.isEmpty(nextStickerName)) {
//                            getNextStickerGift();
//                        } else {
//                            if (!nextStickerName.equals(mCurStickerName)) {
//                                showStickerBean(nextStickerBean, playTime);
//                            } else {
//                                mIsPlayGiftSticker = true;
//                                setStickerTimeout(playTime);
//                            }
//                        }
//                    }
                }
            }
        } else {
            endSticker();
        }
    }


    private void endSticker() {
//        if (mIsPlayGiftSticker) {
//            mIsPlayGiftSticker = false;
//            String orignStickerName = BeautyDataModel.getInstance().getStickerName();
//            if (TextUtils.isEmpty(orignStickerName)) {
//                setSticker(mNoStickerName);
//            } else {
//                setSticker(orignStickerName);
//            }
//        }
    }


    /**
     * 道具礼物贴纸
     */
    private void setSticker(String stickerName) {
//        if (!TextUtils.isEmpty(stickerName) && !stickerName.equals(mCurStickerName)) {
//            mCurStickerName = stickerName;
//            if (mMhSDKManager != null) {
//                mMhSDKManager.setSticker(stickerName);
//            }
//        }
    }


    public boolean isPlayGiftSticker() {
        return mIsPlayGiftSticker;
    }



}
