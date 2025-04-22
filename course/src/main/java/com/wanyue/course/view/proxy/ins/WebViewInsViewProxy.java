package com.wanyue.course.view.proxy.ins;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.wanyue.common.HtmlConfig;
import com.wanyue.common.business.HtmlHelper;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.BitmapUtil;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ViewUtil;

import com.wanyue.course.R2;
import com.wanyue.course.bean.ProjectBean;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public abstract class WebViewInsViewProxy<T extends ProjectBean> extends BaseInsViewProxy<T> {
    @BindView(R2.id.web_container)
    protected ViewGroup mWebContainer;
    protected WebView mWebView;

    private void initWebView() {
        if (mWebView != null) {
            return;
        }
        mWebView = ViewUtil.createWebView(mWebContainer.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,DpUtil.dp2px(230));
        mWebContainer.addView(mWebView, params);
    }

    @Override
    protected void setData(final T data) {
        super.setData(data);
        Observable.timer(300, TimeUnit.MILLISECONDS).compose(this.<Long>bindUntilOnDestoryEvent()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        String url = data.getInfoUrl();
                        initWebView();
                        mWebView.loadUrl(url);
                    }
                });
    }


    protected CharSequence getTypeSPanTag(String content, int id) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(content + "\t");
        SpannableString spannableString = new SpannableString("\t \t");
        int dpSize = DpUtil.dp2px(15);
        Drawable drawable = BitmapUtil.zoomDrawable(getResources(), id, dpSize);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(imageSpan, 1, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(spannableString);
        return ssb;
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
            mWebView.destroy();
        }

        mWebView = null;
    }

}
