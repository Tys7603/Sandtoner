package com.wanyue.video.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.HtmlConfig;
import com.wanyue.common.bean.ConfigBean;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.mob.MobCallback;
import com.wanyue.common.mob.MobShareUtil;
import com.wanyue.common.mob.ShareData;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.DateFormatUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.DownloadUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.VideoLocalUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.video.R;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.event.VideoDeleteEvent;
import com.wanyue.video.event.VideoShareEvent;
import com.wanyue.video.api.VideoHttpConsts;
import com.wanyue.video.api.VideoAPI;
import com.wanyue.video.utils.VideoStorge;
import com.wanyue.video.views.VideoScrollViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 *  on 2019/2/28.
 */

public abstract class AbsVideoPlayActivity extends AbsVideoCommentActivity {

    protected VideoScrollViewHolder mVideoScrollViewHolder;
    private Dialog mDownloadVideoDialog;
    private ClipboardManager mClipboardManager;
    private MobCallback mMobCallback;
    private MobShareUtil mMobShareUtil;
    private DownloadUtil mDownloadUtil;
    private ConfigBean mConfigBean;
    private VideoBean mShareVideoBean;
    protected String mVideoKey;
    private boolean mPaused;


    @Override
    protected void main() {
        super.main();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        CommonAppConfig.getObserverConfig().subscribe(new DefaultObserver<ConfigBean>() {
            @Override
            public void onNext(ConfigBean configBean) {
                mConfigBean = configBean;
            }
        });
    }


    /**
     * 复制视频链接
     */
    public void copyLink(VideoBean videoBean) {
        if (videoBean == null) {
            return;
        }
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("text", videoBean.getHref());
        mClipboardManager.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }

    /**
     * 分享页面链接
     */
    public void shareVideoPage(String type, VideoBean videoBean) {
        if (videoBean == null || mConfigBean == null) {
            return;
        }
        if (mMobCallback == null) {
            mMobCallback = new MobCallback() {

                @Override
                public void onSuccess(Object data) {
                    if (mShareVideoBean == null) {
                        return;
                    }
                    VideoAPI.setVideoShare(mShareVideoBean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (isSuccess(code)&& mShareVideoBean != null) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                EventBus.getDefault().post(new VideoShareEvent(mShareVideoBean.getId(), obj.getString("shares")));
                            } else {
                                ToastUtil.show(msg);
                            }
                        }

                        @Override
                        public void onError(int code, String msg) {

                        }
                    });
                }
                @Override
                public void onError() {

                }
                @Override
                public void onCancel() {

                }

                @Override
                public void onFinish() {

                }
            };
        }
        mShareVideoBean = videoBean;
        ShareData data = new ShareData();
        data.setTitle(mShareVideoBean.getTitle());
        //data.setDes(mConfigBean.getVideoShareDes());
        data.setImgUrl(videoBean.getThumb());
        String webUrl = CommonAppConfig.HOST +"/appapi/video/index?id="+videoBean.getId();
        data.setWebUrl(webUrl);
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        mMobShareUtil.execute(type, data, mMobCallback);
    }

    /**
     * 下载视频
     */


    public void downloadVideo(final VideoBean videoBean) {
        if (mProcessResultUtil == null || videoBean == null || TextUtils.isEmpty(videoBean.getHref())) {
            return;
        }
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, new Runnable() {
            @Override
            public void run() {
                mDownloadVideoDialog = DialogUitl.loadingDialog(mContext);
                mDownloadVideoDialog.show();
                if (mDownloadUtil == null) {
                    mDownloadUtil = new DownloadUtil();
                }
                String fileName = "WY_VIDEO_" + videoBean.getTitle() + "_" + DateFormatUtil.getCurTimeString() + ".mp4";
                mDownloadUtil.download(videoBean.getTag(), CommonAppConfig.VIDEO_PATH, fileName, videoBean.getHref(), new DownloadUtil.Callback() {
                    @Override
                    public void onSuccess(File file) {
                        ToastUtil.show(R.string.video_download_success);
                        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
                            mDownloadVideoDialog.dismiss();
                        }
                        mDownloadVideoDialog = null;
                        String path = file.getAbsolutePath();
                        try {
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(path);
                            String d = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            if (StringUtil.isInt(d)) {
                                long duration = Long.parseLong(d);
                                VideoLocalUtil.saveVideoInfo(mContext, path, duration);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show(R.string.video_download_failed);
                        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
                            mDownloadVideoDialog.dismiss();
                        }
                        mDownloadVideoDialog = null;
                    }
                });
            }
        });
    }

    /**
     * 删除视频
     */
    public void deleteVideo(final VideoBean videoBean) {
        VideoAPI.videoDelete(videoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (isSuccess(code)) {
                    if (mVideoScrollViewHolder != null) {
                        EventBus.getDefault().post(new VideoDeleteEvent(videoBean.getId()));
                        mVideoScrollViewHolder.deleteVideo(videoBean);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }


    public boolean isPaused() {
        return mPaused;
    }

    @Override
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
    }

    @Override
    public void release() {
        super.release();
        VideoAPI.cancel(VideoHttpConsts.SET_VIDEO_SHARE);
        VideoAPI.cancel(VideoHttpConsts.VIDEO_DELETE);
        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
            mDownloadVideoDialog.dismiss();
        }
        if (mVideoScrollViewHolder != null) {
            mVideoScrollViewHolder.release();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        VideoStorge.getInstance().removeDataHelper(mVideoKey);
        mDownloadVideoDialog = null;
        mVideoScrollViewHolder = null;
        mMobShareUtil = null;
    }


    public void setVideoScrollViewHolder(VideoScrollViewHolder videoScrollViewHolder) {
        mVideoScrollViewHolder = videoScrollViewHolder;
    }
}
