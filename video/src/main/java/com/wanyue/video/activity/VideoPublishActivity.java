package com.wanyue.video.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.Constants;
import com.wanyue.common.HtmlConfig;
import com.wanyue.common.activity.AbsActivity;
import com.wanyue.common.bean.ConfigBean;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.mob.MobCallback;
import com.wanyue.common.mob.MobShareUtil;
import com.wanyue.common.mob.ShareData;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.JsonUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.video.R;
import com.wanyue.video.adapter.VideoPubShareAdapter;
import com.wanyue.video.api.VideoHttpConsts;
import com.wanyue.video.api.VideoAPI;
import com.wanyue.video.event.VideoShareEvent;
import com.wanyue.video.upload.VideoUploadBean;
import com.wanyue.video.upload.VideoUploadCallback;
import com.wanyue.video.upload.VideoUploadQnImpl;
import com.wanyue.video.upload.VideoUploadStrategy;
import com.wanyue.video.upload.VideoUploadTxImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

import static com.wanyue.common.Constants.CLASS_ID;
import static com.wanyue.common.Constants.GOODS;

import org.greenrobot.eventbus.EventBus;

/**
 *  on 2018/12/10.
 * 视频发布
 */

public class VideoPublishActivity extends AbsActivity implements ITXLivePlayListener, View.OnClickListener {
    public static final int SEL_VIDEO_CLASS=1;
    public static final int SEL_GOODS=2;

    private View mBtnGoodsAdd;

    private static final String TAG = "VideoPublishActivity";
    private TextView mNum;
    private TextView mLocation;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mPlayer;
    private String mVideoPath;
    private boolean mPlayStarted;//播放是否开始了
    private boolean mPaused;//生命周期暂停
    private RecyclerView mRecyclerView;
    private ConfigBean mConfigBean;
    private VideoPubShareAdapter mAdapter;
    private VideoUploadStrategy mUploadStrategy;
    private EditText mInput;
    private String mVideoTitle;//视频标题
    private Dialog mLoading;
    private MobShareUtil mMobShareUtil;
    private int mSaveType;
    private int mMusicId;
    private View mBtnPub;
    private int mVideoLastProgress;
    private CheckBox mCheckBox;
    private TextView mGoodsName;
    private GoodsBean mSelGoodsBean;
    private FrameLayout mBtnClassfiy;
    private TextView mTvClassfiyName;




    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_publish;
    }

    @Override
    protected void main() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle(WordUtil.getString(R.string.video_pub));
        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra(Constants.VIDEO_PATH);
        mSaveType = intent.getIntExtra(Constants.VIDEO_SAVE_TYPE, Constants.VIDEO_SAVE_SAVE_AND_PUB);
        if (TextUtils.isEmpty(mVideoPath)) {
            return;
        }
        mMusicId = intent.getIntExtra(Constants.VIDEO_MUSIC_ID, 0);
        mBtnPub = findViewById(R.id.btn_pub);
        mBtnPub.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.recyclerView);

        mBtnClassfiy = (FrameLayout) findViewById(R.id.btn_classfiy);
        mTvClassfiyName = (TextView) findViewById(R.id.tv_classfiy_name);



        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        CommonAppConfig.getConfig();
        mConfigBean =  new ConfigBean();
        if (mRecyclerView != null) {
            mAdapter = new VideoPubShareAdapter(mContext, mConfigBean);
            mRecyclerView.setAdapter(mAdapter);
        }
        mNum = (TextView) findViewById(R.id.num);
        mInput = (EditText) findViewById(R.id.input);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mNum != null) {
                    mNum.setText(s.length() + "/50");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLocation = findViewById(R.id.location);
        mLocation.setText(CommonAppConfig.getCity());
        mCheckBox = findViewById(R.id.checkbox);
        mCheckBox.setOnClickListener(this);
        mBtnGoodsAdd=findViewById(R.id.btn_goods_add);

        judgeHaveStore();

        mGoodsName = findViewById(R.id.goods_name);
        mTXCloudVideoView = findViewById(R.id.video_view);
        mPlayer = new TXLivePlayer(mContext);
        mPlayer.setConfig(new TXLivePlayConfig());
        mPlayer.setPlayerView(mTXCloudVideoView);
        mPlayer.enableHardwareDecode(false);
        mPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer.setPlayListener(this);
        int result = mPlayer.startPlay(mVideoPath, TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO);
        if (result == 0) {
            mPlayStarted = true;
        }
    }

    private void judgeHaveStore() {
        mBtnGoodsAdd.setOnClickListener(VideoPublishActivity.this);
    }

    @Override
    public void onPlayEvent(int e, Bundle bundle) {
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_END://播放结束
                onReplay();
                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION:
                onVideoSizeChanged(bundle.getInt("EVT_PARAM1", 0), bundle.getInt("EVT_PARAM2", 0));
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                int progress = bundle.getInt("EVT_PLAY_PROGRESS_MS");
                if (mVideoLastProgress == progress) {
                    onReplay();
                } else {
                    mVideoLastProgress = progress;
                }
                break;
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**
     * 获取到视频宽高回调
     */
    public void onVideoSizeChanged(float videoWidth, float videoHeight) {
        if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTXCloudVideoView.getLayoutParams();
            if (videoWidth / videoHeight > 0.5625f) {//横屏 9:16=0.5625
                params.height = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
                params.gravity = Gravity.CENTER;
                mTXCloudVideoView.requestLayout();
            }
        }
    }

    /**
     * 循环播放
     */
    private void onReplay() {
        if (mPlayStarted && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mPlayStarted && mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused && mPlayStarted && mPlayer != null) {
            mPlayer.resume();
        }
        mPaused = false;
    }

    public void release() {
        VideoAPI.cancel(VideoHttpConsts.SAVE_UPLOAD_VIDEO_INFO);
        mPlayStarted = false;
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        if (mUploadStrategy != null) {
            mUploadStrategy.cancel();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        mPlayer = null;
        mUploadStrategy = null;
        mMobShareUtil = null;
    }

    @Override
    public void onBackPressed() {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.video_give_up_pub), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (mSaveType == Constants.VIDEO_SAVE_PUB) {
                    if (!TextUtils.isEmpty(mVideoPath)) {
                        File file = new File(mVideoPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                release();
                VideoPublishActivity.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
        VideoAPI.cancel(VideoHttpConsts.GETCON);
        L.e(TAG, "-------->onDestroy");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_pub) {
            publishVideo();
        } else if (i == R.id.checkbox) {
            clickCheckBox();
        } else if (i == R.id.btn_goods_add) {
            forwardVideoGoodsAddActivity();
        }
    }

    private void forwardVideoGoodsAddActivity() {
        Intent intent=new Intent(mContext, VideoGoodsAddActivity.class);
        if(mSelGoodsBean!=null){
          intent.putExtra(GOODS,mSelGoodsBean);
        }
        startActivityForResult(intent,SEL_GOODS);
    }





    private void clickCheckBox() {
        if (mCheckBox == null || mLocation == null) {
            return;
        }
        if (mCheckBox.isChecked()) {
            mLocation.setEnabled(true);
            mLocation.setText(CommonAppConfig.getCity());
        } else {
            mLocation.setEnabled(false);
            mLocation.setText("位置");
        }
    }

    private String mClassfyId;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SEL_GOODS&&resultCode==RESULT_OK){
            GoodsBean goodsBean=data.getParcelableExtra(GOODS);
            setSelGoods(goodsBean);
        }else if(requestCode==SEL_VIDEO_CLASS&&resultCode==RESULT_OK){
            String classId=data.getStringExtra(Constants.CLASS_ID);
            String className=data.getStringExtra(Constants.CLASS_NAME);
            mClassfyId=classId;
            if(mTvClassfiyName!=null){
               mTvClassfiyName.setText(className);
            }

        }
    }


    private void setSelGoods(GoodsBean goodsBean) {
        this.mSelGoodsBean=goodsBean;
        if(goodsBean==null){
            mGoodsName.setText("");
        }else{
            mGoodsName.setText(goodsBean.getName());
        }

    }

    /**
     * 发布视频
     */
    private void publishVideo() {
        if (mConfigBean == null) {
            return;
        }

        String title = mInput.getText().toString().trim();

       if (TextUtils.isEmpty(title)) {
           ToastUtil.show(R.string.video_title_empty);
            return;
        }else if(mClassfyId==null){
           ToastUtil.show("请选择直播分类");
           return;
       }


        mVideoTitle = title;
        if (TextUtils.isEmpty(mVideoPath)) {
            return;
        }
        mLoading = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.video_pub_ing));
        mLoading.show();
        Bitmap bitmap = null;
        //生成视频封面图
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mVideoPath);
            bitmap = mmr.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            bitmap = null;
            e.printStackTrace();
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
        if (bitmap == null) {
            ToastUtil.show(R.string.video_cover_img_failed);
            onFailed();
            return;
        }
        final String coverImagePath = mVideoPath.replace(".mp4", ".jpg");
        File imageFile = new File(coverImagePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            imageFile = null;
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (imageFile == null) {
            ToastUtil.show(R.string.video_cover_img_failed);
            onFailed();
            return;
        }
        final File finalImageFile = imageFile;
        //用鲁班压缩图片
        Luban.with(this)
                .load(finalImageFile)
                .setFocusAlpha(false)
                .ignoreBy(8)//8k以下不压缩
                .setTargetDir(CommonAppConfig.VIDEO_PATH)
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
                        return filePath.replace(".jpg", "_c.jpg");
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        if (finalImageFile.exists()) {
                            finalImageFile.delete();
                        }
                        uploadVideoFile(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        uploadVideoFile(finalImageFile);
                    }
                }).launch();
    }

    private void onFailed() {
        DialogUitl.dismissDialog(mLoading);

        if (mBtnPub != null) {
            mBtnPub.setEnabled(true);
        }
    }

    /**
     * 上传封面图片
     */

    private void uploadVideoFile(File imageFile) {
        mBtnPub.setEnabled(false);
        mUploadStrategy = new VideoUploadQnImpl(mConfigBean);
        mUploadStrategy.upload(new VideoUploadBean(new File(mVideoPath), imageFile), new VideoUploadCallback() {
            @Override
            public void onSuccess(VideoUploadBean bean) {
                if (mSaveType == Constants.VIDEO_SAVE_PUB) {
                    bean.deleteFile();
                }
                saveUploadVideoInfo(bean);
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.video_pub_failed);
                onFailed();
            }
        });
    }

    public void openClassfiy(View view){
        Intent intent=new Intent(mContext, VideoClassifyActivity.class);
        if(mClassfyId!=null){
            intent.putExtra(CLASS_ID,mClassfyId);
        }
        startActivityForResult(intent,SEL_VIDEO_CLASS);
    }

    /**
     * 把视频上传后的信息保存在服务器
     */

    private void saveUploadVideoInfo(VideoUploadBean bean) {
        String goodsId=mSelGoodsBean!=null?mSelGoodsBean.getId():null;
        VideoAPI.saveUploadVideoInfo(mClassfyId, mVideoTitle, bean.getResultImageUrl(), bean.getResultVideoUrl(), goodsId, new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                DialogUitl.dismissDialog(mLoading);
                if(!isSuccess(code)){
                    if (mBtnPub != null) {
                        mBtnPub.setEnabled(true);
                    }
                    ToastUtil.show(msg);
                    return;
                }
                ToastUtil.show(R.string.video_pub_success);
                if (mAdapter != null) {
                    String shareType = mAdapter.getShareType();
                    if (shareType!= null) {
                        shareVideoPage(shareType, info.getString("id"), info.getString("thumb"));
                    }else{
                        finish();
                    }
                }else{
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {
                DialogUitl.dismissDialog(mLoading);
                if (mBtnPub != null) {
                    mBtnPub.setEnabled(true);
                }
                ToastUtil.show(R.string.video_pub_failed);
            }
        });
    }

    /**
     * 分享页面链接
     */

    public void shareVideoPage(String shareType,final  String videoId, String videoImageUrl) {
        ShareData data = new ShareData();
        data.setTitle(mVideoTitle);
        data.setImgUrl(videoImageUrl);
        String webUrl = CommonAppConfig.HOST +"/appapi/video/index?id="+videoId;
        data.setWebUrl(webUrl);
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        mMobShareUtil.execute(shareType, data, new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                addShareNum(videoId);

            }
            @Override
            public void onError() {
                finish();
            }
            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onFinish() {
                finish();
            }
        });
    }

    private void addShareNum(final String id ) {
        VideoAPI.setVideoShare(id, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (isSuccess(code)) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    EventBus.getDefault().post(new VideoShareEvent(id, obj.getString("shares")));
                }
                finish();
            }

            @Override
            public void onError() {
                super.onError();
                finish();
            }
        });

    }


    public static void forward(Context context, String videoPath, int saveType, int musicId) {
        Intent intent = new Intent(context, VideoPublishActivity.class);
        intent.putExtra(Constants.VIDEO_PATH, videoPath);
        intent.putExtra(Constants.VIDEO_SAVE_TYPE, saveType);
        intent.putExtra(Constants.VIDEO_MUSIC_ID, musicId);
        context.startActivity(intent);
    }


}
