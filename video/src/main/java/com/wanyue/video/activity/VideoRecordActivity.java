package com.wanyue.video.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.VideoCapture;
import androidx.camera.view.CameraView;

import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.viewanimator.AnimationBuilder;
import com.wanyue.common.custom.viewanimator.ViewAnimator;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.video.R;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class VideoRecordActivity extends BaseActivity {
    private static final int MAX_RECORD_TIME=60;
    private static final int GO_PHOTO=1;
    private CameraView mCamera;
    private View mRecordView;
    private TextView mTvTime;



    private Dialog mLoadingDialog;

    private Disposable mRecordDisposable;
    private ViewAnimator mViewAnimator;


    @SuppressLint("MissingPermission")
    @Override
    public void init() {
        mCamera = findViewById(R.id.camera);
        mCamera.bindToLifecycle(this);
        mCamera.setCaptureMode(CameraView.CaptureMode.VIDEO);
        mRecordView = (View) findViewById(R.id.record_view);
        mTvTime = (TextView) findViewById(R.id.tv_time);

    }

    private void stopRecordAnim(){
        if(mViewAnimator!=null){
           mViewAnimator.cancel();
           mViewAnimator=null;
        }

    }
    private void startRecordAnim() {
        if(mViewAnimator!=null){
           mViewAnimator.cancel();
            mViewAnimator=null;
        }
        AnimationBuilder builder = ViewAnimator.animate(mRecordView);
        builder.scale(1,1.1F,0.9F,1.0F)
                .duration(900)
        ;
        mViewAnimator = builder.repeatCount(100000).start();
    }


    public void recordClick(View view){
        if(mCamera.isRecording()){
            endRecord();
        }else {
            startRecord();
        }
    }


    private void showLoadingDialog(String text) {
        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()){
           TextView textView= mLoadingDialog.findViewById(R.id.text);
            textView.setText(text);
            return;
        }
        mLoadingDialog= DialogUitl.loadingDialog(this,text,false);
        mLoadingDialog.show();
    }

    private void dismissDialog(String title){
        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()){
           mLoadingDialog.dismiss();
           mLoadingDialog=null;
        }
        ToastUtil.show(title);
    }


    private void startRecord() {
         cancleRecordDelay();
         startRecordDelay();
        startRecordAnim();
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(),
                System.currentTimeMillis() + ".mp4");
        mCamera.startRecording(file, Executors.newSingleThreadExecutor(), new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(@NonNull File file) {
                onFileSaved(file);
            }
            @Override
            public void onError(int i, @NonNull String s, @Nullable Throwable throwable) {
                dismissDialog("视频生成失败");
            }
        });
    }

    private void cancleRecordDelay() {
        if(mRecordDisposable!=null&&!mRecordDisposable.isDisposed()){
           mRecordDisposable.dispose();
        }


    }

    private void startRecordDelay() {

        mRecordDisposable= Observable.interval(0, 1, TimeUnit.SECONDS).take(MAX_RECORD_TIME).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        //mTvTime.setText(aLong+"s");
                        mTvTime.setText((aLong+1)+"s");
                        if(aLong==MAX_RECORD_TIME-1){
                          endRecord();
                        }
                    }
                });

    }

    private void onFileSaved(File file) {
        final File outFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(),
                System.currentTimeMillis() + ".mp4");
        String text = "ffmpeg -y -i "+file.getAbsolutePath()+ " -b 2097k -r 30 -vcodec libx264 -preset superfast "+ outFile.getAbsolutePath();
        String[] commands = text.split(" ");
        //开始执行FFmpeg命令
        RxFFmpegInvoke.getInstance()
                .runCommandRxJava(commands)
                .subscribe(new RxFFmpegSubscriber() {
                    @Override
                    public void onFinish() {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outFile)));
                        String path=outFile.getAbsolutePath();
                        dismissDialog("处理成功");
                        VideoPublishActivity.forward(VideoRecordActivity.this,path, Constants.VIDEO_SAVE_SAVE_AND_PUB,0);
                        finish();
                    }
                    @Override
                    public void onProgress(int i, long l) {
                        if(i<=0){
                          return;
                        }
                        showLoadingDialog("处理中:"+i+"%");
                    }
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onError(String s) {
                        L.e("错误=="+s);
                        dismissDialog(s);
                    }
                });
    }

    private void endRecord() {
        if(mRecordDisposable!=null&&!mRecordDisposable.isDisposed()){
            mRecordDisposable.dispose();
        }
        if(mTvTime!=null){
           mTvTime.setText("");
        }
        stopRecordAnim();
        if(mCamera!=null){
           mCamera.stopRecording();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GO_PHOTO&&resultCode==RESULT_OK){
            String path=data.getStringExtra(Constants.VIDEO_PATH);
            if(!TextUtils.isEmpty(path)){
               onFileSaved(new File(path));
            }
        }
    }

    public void openPhoto(View view){
        Intent intent=new Intent(this,VideoChooseActivity.class);
        startActivityForResult(intent,GO_PHOTO);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_record2;
    }

    public void toggleFront(View view) {
        if (mCamera!=null){
            mCamera.toggleCamera();
        }
    }
}