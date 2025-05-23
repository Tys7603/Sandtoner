package com.tencent.liteav.demo.superplayer.model;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.liteav.demo.superplayer.SuperPlayerCode;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.SuperPlayerGlobalConfig;
import com.tencent.liteav.demo.superplayer.SuperPlayerModel;
import com.tencent.liteav.demo.superplayer.SuperPlayerVideoId;
import com.tencent.liteav.demo.superplayer.model.entity.PlayImageSpriteInfo;
import com.tencent.liteav.demo.superplayer.model.entity.PlayKeyFrameDescInfo;
import com.tencent.liteav.demo.superplayer.model.entity.ResolutionName;
import com.tencent.liteav.demo.superplayer.model.entity.VideoQuality;
import com.tencent.liteav.demo.superplayer.model.net.LogReport;
import com.tencent.liteav.demo.superplayer.model.protocol.IPlayInfoProtocol;
import com.tencent.liteav.demo.superplayer.model.protocol.IPlayInfoRequestCallback;
import com.tencent.liteav.demo.superplayer.model.protocol.PlayInfoParams;
import com.tencent.liteav.demo.superplayer.model.protocol.PlayInfoProtocolV2;
import com.tencent.liteav.demo.superplayer.model.protocol.PlayInfoProtocolV4;
import com.tencent.liteav.demo.superplayer.model.utils.VideoQualityUtils;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXBitrateItem;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wanyue.common.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SuperPlayerImpl implements SuperPlayer, ITXVodPlayListener, ITXLivePlayListener {

    private static final String TAG = "SuperPlayerImpl";

    private Context mContext;
    private TXCloudVideoView mVideoView;        // 腾讯云视频播放view

    private IPlayInfoProtocol mCurrentProtocol; // 当前视频信息协议类
    private TXVodPlayer       mVodPlayer;       // 点播播放器
    private TXVodPlayConfig   mVodPlayConfig;   // 点播播放器配置
    private TXLivePlayer      mLivePlayer;      // 直播播放器
    private TXLivePlayConfig  mLivePlayConfig;  // 直播播放器配置

    private SuperPlayerModel    mCurrentModel;  // 当前播放的model
    private SuperPlayerObserver mObserver;
    private VideoQuality        mVideoQuality;

    private SuperPlayerDef.PlayerType mCurrentPlayType   = SuperPlayerDef.PlayerType.VOD;       // 当前播放类型
    private SuperPlayerDef.PlayerMode mCurrentPlayMode   = SuperPlayerDef.PlayerMode.WINDOW;    // 当前播放模式
    private SuperPlayerDef.PlayerState mCurrentPlayState = SuperPlayerDef.PlayerState.PLAYING;  // 当前播放状态

    private String mCurrentPlayVideoURL;    // 当前播放的URL

    private int mSeekPos;                   // 记录切换硬解时的播放时间

    private long mReportLiveStartTime = -1; // 直播开始时间，用于上报使用时长
    private long mReportVodStartTime = -1;  // 点播开始时间，用于上报使用时长
    private long mMaxLiveProgressTime;      // 观看直播的最大时长

    private boolean mIsMultiBitrateStream;  // 是否是多码流url播放
    private boolean mIsPlayWithFileId;      // 是否是腾讯云fileId播放
    private boolean mDefaultQualitySet;     // 标记播放多码流url时是否设置过默认画质
    private boolean mChangeHWAcceleration;  // 切换硬解后接收到第一个关键帧前的标记位

    public SuperPlayerImpl(Context context, TXCloudVideoView videoView) {
        initialize(context, videoView);
    }

    /**
     * 直播播放器事件回调
     *
     * @param event
     * @param param
     */
    @Override
    public void onPlayEvent(int event, Bundle param) {
        L.e("event=="+event+"&&param=="+param);
        if (event != TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            String playEventLog = "TXLivePlayer onPlayEvent event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
            TXCLog.d(TAG, playEventLog);
        }
        switch (event) {
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                int width=param.getInt("EVT_PARAM1") ;
                int height=param.getInt("EVT_PARAM2");
                videoSizeChange(width,height);
                break;
            case TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED: //视频播放开始
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                updatePlayerState(SuperPlayerDef.PlayerState.PLAYING);
                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                if (mCurrentPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {  // 直播时移失败，返回直播
                    mLivePlayer.resumeLive();
                    updatePlayerType(SuperPlayerDef.PlayerType.LIVE);
                    onError(SuperPlayerCode.LIVE_SHIFT_FAIL, "时移失败,返回直播");
                    updatePlayerState(SuperPlayerDef.PlayerState.PLAYING);
                } else {
                    stop();
                    updatePlayerState(SuperPlayerDef.PlayerState.END);
                    if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                        onError(SuperPlayerCode.NET_ERROR, "网络不给力,点击重试");
                    } else {
                        onError(SuperPlayerCode.LIVE_PLAY_END, param.getString(TXLiveConstants.EVT_DESCRIPTION));
                    }
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
//            case TXLiveConstants.PLAY_WARNING_RECONNECT:  //暂时去掉，回调该状态时，播放画面可能是正常的，loading 状态只在 TXLiveConstants.PLAY_EVT_PLAY_LOADING 处理
                updatePlayerState(SuperPlayerDef.PlayerState.LOADING);
                break;

            case TXLiveConstants.PLAY_EVT_STREAM_SWITCH_SUCC:
                updateStreamEndStatus(true, SuperPlayerDef.PlayerType.LIVE, mVideoQuality);
                break;
            case TXLiveConstants.PLAY_ERR_STREAM_SWITCH_FAIL:
                updateStreamEndStatus(false, SuperPlayerDef.PlayerType.LIVE, mVideoQuality);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS);
                mMaxLiveProgressTime = progress > mMaxLiveProgressTime ? progress : mMaxLiveProgressTime;
                updatePlayProgress(progress / 1000, mMaxLiveProgressTime / 1000);
                break;
            default:
                break;
        }
    }

    public void videoSizeChange(int width, int height) {
    }

    /**
     * 直播播放器网络状态回调
     *
     * @param bundle
     */
    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**
     * 点播播放器事件回调
     *
     * @param player
     * @param event
     * @param param
     */
    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        if (event != TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            String playEventLog = "TXVodPlayer onPlayEvent event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
            TXCLog.d(TAG, playEventLog);
        }
        switch (event) {
            case TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED://视频播放开始
                updatePlayerState(SuperPlayerDef.PlayerState.PLAYING);
                if (mIsMultiBitrateStream) {
                    List<TXBitrateItem> bitrateItems = mVodPlayer.getSupportedBitrates();
                    if (bitrateItems == null || bitrateItems.size() == 0) {
                        return;
                    }
                    Collections.sort(bitrateItems); //masterPlaylist多清晰度，按照码率排序，从低到高
                    List<VideoQuality> videoQualities = new ArrayList<>();
                    int size = bitrateItems.size();
                    List<ResolutionName> resolutionNames = (mCurrentProtocol != null) ? mCurrentProtocol.getResolutionNameList() : null;
                    for (int i = 0; i < size; i++) {
                        TXBitrateItem bitrateItem = bitrateItems.get(i);
                        VideoQuality quality;
                        if (resolutionNames != null) {
                            quality = VideoQualityUtils.convertToVideoQuality(bitrateItem, mCurrentProtocol.getResolutionNameList());
                        } else {
                            quality = VideoQualityUtils.convertToVideoQuality(bitrateItem, i);
                        }
                        videoQualities.add(quality);
                    }
                    if (!mDefaultQualitySet) {
                        mVodPlayer.setBitrateIndex(bitrateItems.get(bitrateItems.size() - 1).index); //默认播放码率最高的
                        mDefaultQualitySet = true;
                    }
                    updateVideoQualityList(videoQualities, null);
                }
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                if (mChangeHWAcceleration) { //切换软硬解码器后，重新seek位置
                    TXCLog.i(TAG, "seek pos:" + mSeekPos);
                    seek(mSeekPos);
                    mChangeHWAcceleration = false;
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                updatePlayerState(SuperPlayerDef.PlayerState.END);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS);
                int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS);
                updatePlayProgress(progress / 1000, duration / 1000);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                updatePlayerState(SuperPlayerDef.PlayerState.PLAYING);
                break;
            default:
                break;
        }
        if (event < 0) {// 播放点播文件失败
            mVodPlayer.stopPlay(true);
            updatePlayerState(SuperPlayerDef.PlayerState.PAUSE);
            onError(SuperPlayerCode.VOD_PLAY_FAIL, param.getString(TXLiveConstants.EVT_DESCRIPTION));
        }
    }

    /**
     * 点播播放器网络状态回调
     *
     * @param player
     * @param bundle
     */
    @Override
    public void onNetStatus(TXVodPlayer player, Bundle bundle) {

    }

    private void initialize(Context context, TXCloudVideoView videoView) {
        mContext = context;
        mVideoView = videoView;
        initLivePlayer(mContext);
        initVodPlayer(mContext);
    }

    /**
     * 初始化点播播放器
     *
     * @param context
     */
    private void initVodPlayer(Context context) {
        mVodPlayer = new TXVodPlayer(context);
        TXVodPlayConfig mConfig = new TXVodPlayConfig();

//指定本地最多缓存多少文件，避免缓存太多数据
        mConfig.setMaxCacheItems(100);
        SuperPlayerGlobalConfig config = SuperPlayerGlobalConfig.getInstance();
        mVodPlayConfig = new TXVodPlayConfig();
        mVodPlayer.setConfig(mVodPlayConfig);
        File sdcardDir = context.getExternalFilesDir(null);
        //获取内部存储的cache目录
        //在cacahe目录中创建一个文件

        File myCacheFile = new File(sdcardDir,".txcache");
        try {
          if (!sdcardDir.exists()) {
              myCacheFile.createNewFile();
          }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (sdcardDir != null) {
           // String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            String path=myCacheFile.getAbsolutePath();
            mVodPlayConfig.setCacheFolderPath(path + "/txcache");
        }
        mVodPlayConfig.setMaxCacheItems(config.maxCacheItem);
        mVodPlayer.setConfig(mVodPlayConfig);
        mVodPlayer.setRenderMode(config.renderMode);
        mVodPlayer.setVodListener(this);
        mVodPlayer.enableHardwareDecode(config.enableHWAcceleration);
    }

    /**
     * 初始化直播播放器
     *
     * @param context
     */
    private void initLivePlayer(Context context) {
        mLivePlayer = new TXLivePlayer(context);
        SuperPlayerGlobalConfig config = SuperPlayerGlobalConfig.getInstance();
        mLivePlayConfig = new TXLivePlayConfig();
        mLivePlayer.setConfig(mLivePlayConfig);
        mLivePlayer.setRenderMode(config.renderMode);
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mLivePlayer.setPlayListener(this);
        mLivePlayer.enableHardwareDecode(config.enableHWAcceleration);
    }

    /**
     * 播放视频
     *
     * @param model
     */
    public void playWithModel(final SuperPlayerModel model) {
        mCurrentModel = model;
        stop();

        PlayInfoParams params = new PlayInfoParams();
        params.appId = model.appId;
        if (model.videoId != null) {
            params.fileId = model.videoId.fileId;
            params.videoId = model.videoId;
            mCurrentProtocol = new PlayInfoProtocolV4(params);
        } else if (model.videoIdV2 != null) {
            params.fileId = model.videoIdV2.fileId;
            params.videoIdV2 = model.videoIdV2;
            mCurrentProtocol = new PlayInfoProtocolV2(params);
        } else {
            mCurrentProtocol = null;    // 当前播放的是非v2和v4协议视频，将其置空
        }
        if (model.videoId != null || model.videoIdV2 != null) { // 根据FileId播放
            mCurrentProtocol.sendRequest(new IPlayInfoRequestCallback() {
                @Override
                public void onSuccess(IPlayInfoProtocol protocol, PlayInfoParams param) {
                    TXCLog.i(TAG, "onSuccess: protocol params = " + param.toString());
                    mReportVodStartTime = System.currentTimeMillis();
                    mVodPlayer.setPlayerView(mVideoView);
                    playModeVideo(mCurrentProtocol);
                    updatePlayerType(SuperPlayerDef.PlayerType.VOD);
                    updatePlayProgress(0, 0);
                    updateVideoImageSpriteAndKeyFrame(mCurrentProtocol.getImageSpriteInfo(), mCurrentProtocol.getKeyFrameDescInfo());


                }

                @Override
                public void onError(int errCode, String message) {
                    TXCLog.i(TAG, "onFail: errorCode = " + errCode + " message = " + message);
                    SuperPlayerImpl.this.onError(SuperPlayerCode.VOD_REQUEST_FILE_ID_FAIL, "播放视频文件失败 code = " + errCode + " msg = " + message);
                }
            });
        } else { // 根据URL播放
            String videoURL = null;
            List<VideoQuality> videoQualities = new ArrayList<>();
            VideoQuality defaultVideoQuality = null;
            if (model.multiURLs != null && !model.multiURLs.isEmpty()) {// 多码率URL播放
                int i = 0;
                for (SuperPlayerModel.SuperPlayerURL superPlayerURL : model.multiURLs) {
                    if (i == model.playDefaultIndex) {
                        videoURL = superPlayerURL.url;
                    }
                    videoQualities.add(new VideoQuality(i++, superPlayerURL.qualityName, superPlayerURL.url));
                }
                defaultVideoQuality = videoQualities.get(model.playDefaultIndex);
            } else if (!TextUtils.isEmpty(model.url)) { // 传统URL模式播放
                videoURL = model.url;
            }

            if (TextUtils.isEmpty(videoURL)) {
                onError(SuperPlayerCode.PLAY_URL_EMPTY, "播放视频失败，播放链接为空");
                return;
            }
            if (isRTMPPlay(videoURL)) { // 直播播放器：普通RTMP流播放
                mReportLiveStartTime = System.currentTimeMillis();
                mLivePlayer.setPlayerView(mVideoView);
                playLiveURL(videoURL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
            } else if (isFLVPlay(videoURL)) { // 直播播放器：直播FLV流播放
                mReportLiveStartTime = System.currentTimeMillis();
                mLivePlayer.setPlayerView(mVideoView);
                playTimeShiftLiveURL(model.appId, videoURL);
                if (model.multiURLs != null && !model.multiURLs.isEmpty()) {
                    startMultiStreamLiveURL(videoURL);
                }
            } else { // 点播播放器：播放点播文件
                mReportVodStartTime = System.currentTimeMillis();
                mVodPlayer.setPlayerView(mVideoView);
                playVodURL(videoURL);
            }
            boolean isLivePlay = (isRTMPPlay(videoURL) || isFLVPlay(videoURL));
            updatePlayerType(isLivePlay ? SuperPlayerDef.PlayerType.LIVE : SuperPlayerDef.PlayerType.VOD);
            updatePlayProgress(0, 0);
            updateVideoQualityList(videoQualities, defaultVideoQuality);
        }
    }

    /**
     * 播放v2或v4协议视频
     *
     * @param protocol
     */
    private void playModeVideo(IPlayInfoProtocol protocol) {
        playVodURL(protocol.getUrl());
        List<VideoQuality> videoQualities = protocol.getVideoQualityList();
        mIsMultiBitrateStream = videoQualities == null;
        VideoQuality defaultVideoQuality = protocol.getDefaultVideoQuality();
        updateVideoQualityList(videoQualities, defaultVideoQuality);
    }

    /**
     * 播放非v2和v4协议视频
     *
     * @param model
     */
    private void playModeVideo(SuperPlayerModel model) {
        if (model.multiURLs != null && !model.multiURLs.isEmpty()) {// 多码率URL播放
            for (int i = 0; i < model.multiURLs.size(); i++) {
                if (i == model.playDefaultIndex) {
                    playVodURL(model.multiURLs.get(i).url);
                }
            }
        } else if (!TextUtils.isEmpty(model.url)) {
            playVodURL(model.url);
        }
    }

    /**
     * 播放直播URL
     */
    private void playLiveURL(String url, int playType) {
        mCurrentPlayVideoURL = url;
        if (mLivePlayer != null) {
            mLivePlayer.setPlayListener(this);
            int result = mLivePlayer.startPlay(url, playType); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
            if (result != 0) {
                TXCLog.e(TAG, "playLiveURL videoURL:" + url + ",result:" + result);
            } else {
                updatePlayerState(SuperPlayerDef.PlayerState.PLAYING);
            }
        }
    }

    /**
     * 播放点播url
     */
    private void playVodURL(String url) {
        if (url == null || "".equals(url)) {
            return;
        }
        mCurrentPlayVideoURL = url;
        if (url.contains(".m3u8")) {
            mIsMultiBitrateStream = true;
        }
        if (mVodPlayer != null) {
            mDefaultQualitySet = false;
            mVodPlayer.setStartTime(0);
            mVodPlayer.setAutoPlay(true);
            mVodPlayer.setVodListener(this);
            if (mCurrentProtocol != null) {
                TXCLog.d(TAG, "TOKEN: " + mCurrentProtocol.getToken());
                mVodPlayer.setToken(mCurrentProtocol.getToken());
            } else {
                mVodPlayer.setToken(null);
            }
            int ret = mVodPlayer.startPlay(url);
            if (ret == 0) {
                updatePlayerState(SuperPlayerDef.PlayerState.PLAYING);
            }
        }
        mIsPlayWithFileId = false;
    }

    /**
     * 播放时移直播url
     */
    private void playTimeShiftLiveURL(int appId, String url) {
        final String bizid = url.substring(url.indexOf("//") + 2, url.indexOf("."));
        final String domian = SuperPlayerGlobalConfig.getInstance().playShiftDomain;
        final String streamid = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        TXCLog.i(TAG, "bizid:" + bizid + ",streamid:" + streamid + ",appid:" + appId);
        playLiveURL(url, TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        int bizidNum = -1;
        try {
            bizidNum = Integer.parseInt(bizid);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            TXCLog.e(TAG, "playTimeShiftLiveURL: bizidNum error = " + bizid);
        }
        mLivePlayer.prepareLiveSeek(domian, bizidNum);
    }

    /**
     * 配置多码流url
     *
     * @param url
     */
    private void startMultiStreamLiveURL(String url) {
        mLivePlayConfig.setAutoAdjustCacheTime(false);
        mLivePlayConfig.setMaxAutoAdjustCacheTime(5);
        mLivePlayConfig.setMinAutoAdjustCacheTime(5);
        mLivePlayer.setConfig(mLivePlayConfig);
        if (mObserver != null) {
            mObserver.onPlayTimeShiftLive(mLivePlayer, url);
        }
    }

    /**
     * 上报播放时长
     */
    private void reportPlayTime() {
        if (mReportLiveStartTime != -1) {
            long reportEndTime = System.currentTimeMillis();
            long diff = (reportEndTime - mReportLiveStartTime) / 1000;
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_LIVE_TIME, diff, 0);
            mReportLiveStartTime = -1;
        }
        if (mReportVodStartTime != -1) {
            long reportEndTime = System.currentTimeMillis();
            long diff = (reportEndTime - mReportVodStartTime) / 1000;
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VOD_TIME, diff, mIsPlayWithFileId ? 1 : 0);
            mReportVodStartTime = -1;
        }
    }

    /**
     * 更新播放进度
     *
     * @param current  当前播放进度(秒)
     * @param duration 总时长(秒)
     */
    private void updatePlayProgress(long current, long duration) {
        if (mObserver != null) {
            mObserver.onPlayProgress(current, duration);
        }
    }

    /**
     * 更新播放类型
     *
     * @param playType
     */
    private void updatePlayerType(SuperPlayerDef.PlayerType playType) {
        if (playType != mCurrentPlayType) {
            mCurrentPlayType = playType;
        }
        if (mObserver != null) {
            mObserver.onPlayerTypeChange(playType);
        }
    }

    /**
     * 更新播放状态
     *
     * @param playState
     */

    private void updatePlayerState(SuperPlayerDef.PlayerState playState) {
        mCurrentPlayState = playState;
        if (mObserver == null) {
            return;
        }
        switch (playState) {
            case PLAYING:
                mObserver.onPlayBegin(getPlayName());
                break;
            case PAUSE:
                mObserver.onPlayPause();
                break;
            case LOADING:
                mObserver.onPlayLoading();
                break;
            case END:
                mObserver.onPlayStop();
                break;
        }
    }

    private void updateStreamStartStatus(boolean success, SuperPlayerDef.PlayerType playerType, VideoQuality quality) {
        if (mObserver != null) {
            mObserver.onSwitchStreamStart(success, playerType, quality);
        }
    }

    private void updateStreamEndStatus(boolean success, SuperPlayerDef.PlayerType playerType, VideoQuality quality) {
        if (mObserver != null) {
            mObserver.onSwitchStreamEnd(success, playerType, quality);
        }
    }

    private void updateVideoQualityList(List<VideoQuality> videoQualities, VideoQuality defaultVideoQuality) {
        if (mObserver != null) {
            mObserver.onVideoQualityListChange(videoQualities, defaultVideoQuality);
        }
    }

    private void updateVideoImageSpriteAndKeyFrame(PlayImageSpriteInfo info, List<PlayKeyFrameDescInfo> list) {
        if (mObserver != null) {
            mObserver.onVideoImageSpriteAndKeyFrameChanged(info, list);
        }
    }

    private void onError(int code, String message) {
        if (mObserver != null) {
            mObserver.onError(code, message);
        }
    }

    private String getPlayName() {
        String title = "";
        if (mCurrentModel != null && !TextUtils.isEmpty(mCurrentModel.title)) {
            title = mCurrentModel.title;
        } else if (mCurrentProtocol != null && !TextUtils.isEmpty(mCurrentProtocol.getName())) {
            title = mCurrentProtocol.getName();
        }
        return title;
    }

    /**
     * 是否是RTMP协议
     *
     * @param videoURL
     * @return
     */
    private boolean isRTMPPlay(String videoURL) {
        return !TextUtils.isEmpty(videoURL) && videoURL.startsWith("rtmp");
    }

    /**
     * 是否是HTTP-FLV协议
     *
     * @param videoURL
     * @return
     */
    private boolean isFLVPlay(String videoURL) {
        return (!TextUtils.isEmpty(videoURL) && videoURL.startsWith("http://")
                || videoURL.startsWith("https://")) && videoURL.contains(".flv");
    }

    @Override
    public void play(String url) {
        SuperPlayerModel model = new SuperPlayerModel();
        model.url = url;
        playWithModel(model);
    }

    @Override
    public void play(int appId, String url) {
        SuperPlayerModel model = new SuperPlayerModel();
        model.appId = appId;
        model.url = url;
        playWithModel(model);
    }

    @Override
    public void play(int appId, String fileId, String psign) {
        SuperPlayerVideoId videoId = new SuperPlayerVideoId();
        videoId.fileId = fileId;
        videoId.pSign = psign;

        SuperPlayerModel model = new SuperPlayerModel();
        model.appId = appId;
        model.videoId = videoId;
        playWithModel(model);
    }

    @Override
    public void play(int appId, List<SuperPlayerModel.SuperPlayerURL> superPlayerURLS, int defaultIndex) {
        SuperPlayerModel model = new SuperPlayerModel();
        model.appId = appId;
        model.multiURLs = superPlayerURLS;
        model.playDefaultIndex = defaultIndex;
        playWithModel(model);
    }

    @Override
    public void reStart() {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.LIVE || mCurrentPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
            if (isRTMPPlay(mCurrentPlayVideoURL)) {
                playLiveURL(mCurrentPlayVideoURL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
            } else if (isFLVPlay(mCurrentPlayVideoURL)) {
                playTimeShiftLiveURL(mCurrentModel.appId, mCurrentPlayVideoURL);
                if (mCurrentModel.multiURLs != null && !mCurrentModel.multiURLs.isEmpty()) {
                    startMultiStreamLiveURL(mCurrentPlayVideoURL);
                }
            }
        } else {
            playVodURL(mCurrentPlayVideoURL);
        }
    }

    @Override
    public void pause() {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            mVodPlayer.pause();
        } else {
            mLivePlayer.pause();
        }
        updatePlayerState(SuperPlayerDef.PlayerState.PAUSE);
    }

    @Override
    public void pauseVod() {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            mVodPlayer.pause();
        }
        updatePlayerState(SuperPlayerDef.PlayerState.PAUSE);
    }

    @Override
    public void resume() {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            mVodPlayer.resume();
        } else {
            mLivePlayer.resume();
        }
        updatePlayerState(SuperPlayerDef.PlayerState.PLAYING);
    }

    @Override
    public void resumeLive() {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
            mLivePlayer.resumeLive();
        }
        updatePlayerType(SuperPlayerDef.PlayerType.LIVE);
    }

    @Override
    public void stop() {
        if (mVodPlayer != null) {
            mVodPlayer.stopPlay(false);
        }
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(false);
            if(mVideoView!=null){
               mVideoView.removeVideoView();
            }
        }
        updatePlayerState(SuperPlayerDef.PlayerState.END);
        reportPlayTime();
    }

    @Override
    public void destroy() {
        stop();
        setObserver(null);
    }

    @Override
    public void switchPlayMode(SuperPlayerDef.PlayerMode playerMode) {
        if (mCurrentPlayMode == playerMode) {
            return;
        }
        mCurrentPlayMode = playerMode;
    }

    @Override
    public void enableHardwareDecode(boolean enable) {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            mChangeHWAcceleration = true;
            mVodPlayer.enableHardwareDecode(enable);
            mSeekPos = (int) mVodPlayer.getCurrentPlaybackTime();
            TXCLog.i(TAG, "save pos:" + mSeekPos);
            stop();
            if (mCurrentProtocol == null) {    // 当protocol为空时，则说明当前播放视频为非v2和v4视频
                playModeVideo(mCurrentModel);
            } else {
                playModeVideo(mCurrentProtocol);
            }
        } else {
            mLivePlayer.enableHardwareDecode(enable);
            playWithModel(mCurrentModel);
        }
        // 硬件加速上报
        if (enable) {
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_HW_DECODE, 0, 0);
        } else {
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_SOFT_DECODE, 0, 0);
        }
    }

    @Override
    public void setPlayerView(TXCloudVideoView videoView) {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            mVodPlayer.setPlayerView(videoView);
        } else {
            mLivePlayer.setPlayerView(videoView);
        }
    }

    @Override
    public void seek(int position) {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            if (mVodPlayer != null) {
                mVodPlayer.seek(position);
            }
        } else {
            updatePlayerType(SuperPlayerDef.PlayerType.LIVE_SHIFT);
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_TIMESHIFT, 0, 0);
            if (mLivePlayer != null) {
                mLivePlayer.seek(position);
            }
        }
        if (mObserver != null) {
            mObserver.onSeek(position);
        }
    }

    @Override
    public void snapshot(TXLivePlayer.ITXSnapshotListener listener) {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            mVodPlayer.snapshot(listener);
        } else if (mCurrentPlayType == SuperPlayerDef.PlayerType.LIVE) {
            mLivePlayer.snapshot(listener);
        } else {
            listener.onSnapshot(null);
        }
    }

    @Override
    public void setRate(float speedLevel) {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            mVodPlayer.setRate(speedLevel);
        }
        //速度改变上报
        LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_CHANGE_SPEED, 0, 0);

    }

    @Override
    public void setMirror(boolean isMirror) {
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            mVodPlayer.setMirror(isMirror);
        }
        if (isMirror) {
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_MIRROR, 0, 0);
        }
    }

    @Override
    public void switchStream(VideoQuality quality) {
        mVideoQuality = quality;
        if (mCurrentPlayType == SuperPlayerDef.PlayerType.VOD) {
            if (mVodPlayer != null) {
                if (quality.url != null) { // br!=0;index=-1;url!=null   //br=0;index!=-1;url!=null
                    // 说明是非多bitrate的m3u8子流，需要手动seek
                    float currentTime = mVodPlayer.getCurrentPlaybackTime();
                    mVodPlayer.stopPlay(true);
                    TXCLog.i(TAG, "onQualitySelect quality.url:" + quality.url);
                    mVodPlayer.setStartTime(currentTime);
                    mVodPlayer.startPlay(quality.url);
                } else { //br!=0;index!=-1;url=null
                    TXCLog.i(TAG, "setBitrateIndex quality.index:" + quality.index);
                    // 说明是多bitrate的m3u8子流，会自动无缝seek
                    mVodPlayer.setBitrateIndex(quality.index);
                }
                updateStreamStartStatus(true, SuperPlayerDef.PlayerType.VOD, quality);
            }
        } else {
            boolean success = false;
            if (mLivePlayer != null && !TextUtils.isEmpty(quality.url)) {
                int result = mLivePlayer.switchStream(quality.url);
                success = result >= 0;
            }
            updateStreamStartStatus(success, SuperPlayerDef.PlayerType.LIVE, quality);
        }
        //清晰度上报
        LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_CHANGE_RESOLUTION, 0, 0);
    }

    @Override
    public String getPlayURL() {
        return mCurrentPlayVideoURL;
    }

    @Override
    public SuperPlayerDef.PlayerMode getPlayerMode() {
        return mCurrentPlayMode;
    }

    @Override
    public SuperPlayerDef.PlayerState getPlayerState() {
        return mCurrentPlayState;
    }

    @Override
    public SuperPlayerDef.PlayerType getPlayerType() {
        return mCurrentPlayType;
    }

    @Override
    public void setObserver(SuperPlayerObserver observer) {
        mObserver = observer;
    }
}
