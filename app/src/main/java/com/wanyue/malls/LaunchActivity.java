package com.wanyue.malls;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.activity.AbsActivity;
import com.wanyue.common.api.CommonAPI;
import com.wanyue.common.bean.ConfigBean;
import com.wanyue.common.custom.viewanimator.ViewAnimator;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.main.view.activity.LoginActivity;
import com.wanyue.main.view.activity.MainActivity;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

@Route(path = RouteUtil.PATH_LAUNCHER)
public class LaunchActivity extends AbsActivity {

    private ImageView mImgCover;
    private Disposable mDisposable;
    private ViewAnimator mViewAnimator;

    @Override
    protected void main() {
        super.main();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFinishing()) {
            return;
        }
        main();
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
        if (mViewAnimator != null) {
            mViewAnimator.cancel();
            mViewAnimator = null;
        }
        if (mImgCover != null) {
            mImgCover.setImageDrawable(null);
            mImgCover = null;
        }
        super.onDestroy();
    }

    @Override
    public void init() {
        if (isFinishing()) {
            return;
        }
        mImgCover = findViewById(R.id.img_cover);
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        ImgLoader.display(this,R.mipmap.screen,mImgCover);
        startLauncherAnim();

        mDisposable = Observable.timer(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(new Function<Long, ObservableSource<ConfigBean>>() {
                @Override
                public ObservableSource<ConfigBean> apply(Long aLong) throws Exception {
                    return CommonAPI.getConfig();
                }
            })
            .subscribe(new Consumer<ConfigBean>() {
                @Override
                public void accept(ConfigBean configBean) throws Exception {
                    if (isFinishing()) {
                        return;
                    }
                    //初始化极光推送
                    CommonAppConfig.setConfig(configBean);
                    InitHelper.initIM(configBean.getTxSdkappid());
                    String beautyKey = StringUtil.decryptUrl(configBean.getBeautyKey());
                    InitHelper.startNowInit(mContext, beautyKey);
                    startLauncher();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) {
                    if (!isFinishing()) {
                        startLauncher();
                    }
                }
            });
    }

    private void startLauncher() {
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                Intent intent;
                if (CommonAppConfig.isLogin()) {
                    intent = new Intent(LaunchActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(LaunchActivity.this, LoginActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void startLauncherAnim() {
        mViewAnimator=ViewAnimator.animate(mImgCover).alpha(0,1).duration(500).start();
    }

    /*开始启动方法*/
//    private void startLauncher() {
//        if (isFinishing()) {
//            return;
//        }
//        /*倒计时开始*/
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (CommonAppConfig.isLogin()) {
//                    startActivity(new Intent(LaunchActivity.this, MainActivity.class));
//                } else {
//                    startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
//                }
//                finish();
//            }
//        });
//    }

    @Override
    public void finish() {
        if (!isFinishing()) {
            releaseActivty();
            super.finish();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_launch;
    }
}
