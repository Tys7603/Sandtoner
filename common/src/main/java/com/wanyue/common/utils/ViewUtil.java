package com.wanyue.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tencent.cos.xml.utils.StringUtils;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.custom.NoScrollWebView;

public class ViewUtil {

  public static void setTextNoContentHide(TextView textView,String txt){
      if(textView==null) {
          return;
      }
      if (!TextUtils.isEmpty(txt)){
          textView.setText(txt);
          textView.setVisibility(View.VISIBLE);
      }
      else{
          textView.setVisibility(View.INVISIBLE);
      }
  }

    public static boolean equalsView(View view1,View view2){
      if(view1==null||view2==null||view1!=view2){
          return false;
      }
      return true;
    }




    public static void setTextNoContentGone(TextView textView,String txt){
        if(textView==null) {
            return;
        }
        if (!TextUtils.isEmpty(txt)){
            textView.setText(txt);
            textView.setVisibility(View.VISIBLE);
        }
        else{
            textView.setVisibility(View.GONE);
        }
    }


    public static void removeToParent(View view){
      if(view==null||view.getParent()==null){
          return;
      }
      ViewGroup vp= (ViewGroup) view.getParent();
      vp.removeView(view);
    }


    public static void scaleContents(Activity context,View paramView1,float scale) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        // 获得屏幕的宽高
        int i = localDisplayMetrics.widthPixels;
        int j = localDisplayMetrics.heightPixels;

        scaleViewAndChildren(paramView1, scale);
        Log.i("notcloud", "Scaling Factor=" + scale);
    }

    public static void scaleViewAndChildren(View paramView, float paramFloat) {

        ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
        if ((localLayoutParams.width != -1) && (localLayoutParams.width != -2)) {
            int width= (int) (paramFloat * localLayoutParams.width);
            localLayoutParams.width = width;
            L.e("size&& width=="+width);
        }
        if ((localLayoutParams.height != -1)
                && (localLayoutParams.height != -2)) {
            int height=(int) (paramFloat * localLayoutParams.height);
            L.e("size&& height=="+height);
            localLayoutParams.height = height;
        }

        if ((localLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
            ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams) localLayoutParams;
            localMarginLayoutParams.leftMargin = ((int) (paramFloat * localMarginLayoutParams.leftMargin));
            localMarginLayoutParams.rightMargin = ((int) (paramFloat * localMarginLayoutParams.rightMargin));
            localMarginLayoutParams.topMargin = ((int) (paramFloat * localMarginLayoutParams.topMargin));
            localMarginLayoutParams.bottomMargin = ((int) (paramFloat * localMarginLayoutParams.bottomMargin));
        }
        paramView.setLayoutParams(localLayoutParams);
        paramView.setPadding((int) (paramFloat * paramView.getPaddingLeft()),
                (int) (paramFloat * paramView.getPaddingTop()),
                (int) (paramFloat * paramView.getPaddingRight()),
                (int) (paramFloat * paramView.getPaddingBottom()));
        if ((paramView instanceof TextView)) {
            TextView localTextView = (TextView) paramView;
            Log.d("Calculator",
                    "Scaling text size from " + localTextView.getTextSize()
                            + " to " + paramFloat * localTextView.getTextSize());
            localTextView.setTextSize(paramFloat * localTextView.getTextSize());
        }
        ViewGroup localViewGroup = null;
        if ((paramView instanceof ViewGroup)) {
            localViewGroup = (ViewGroup) paramView;
        }

        if (localViewGroup != null) {
            System.out.println("子元素的数量" + localViewGroup.getChildCount());
            for (int i = 0;; i++) {
                if (i >= localViewGroup.getChildCount()) {
                    break;
                }
                scaleViewAndChildren(localViewGroup.getChildAt(i), paramFloat);
            }
        }
    }


    public static void setChecked(CompoundButton button,boolean isCheck){
        if(button==null||button.isChecked()==isCheck){
            return;
        }
        button.setChecked(isCheck);
    }

    public static void setVisibility(View view,int visibly){
        if(view==null){
            return;
        }
        int state=view.getVisibility();
        if(state==visibly){
            return;
        }
        view.setVisibility(visibly);
    }

    public static void setTextAndViewsible(TextView view,String text){
        if(view==null){
            return;
        }


        view.setVisibility(View.VISIBLE);
        view.setText(text);
    }

    public static void setTextAndViewsibleByNumber(TextView view,Integer number){
        if(number==null){
            return;
        }
        if(number==0){
            setVisibility(view,View.INVISIBLE);
        }else{
            view.setText(Integer.toString(number));
            setVisibility(view,View.VISIBLE);
        }
    }




    public static void setEditextEnable(EditText editText){
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.requestFocusFromTouch();
    }

    public static void setEditextEnable2(EditText editText){
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);

    }

    public static <T> T getTag(View v,Class<T>cs) {
       Object object=v.getTag();
       if(object!=null&&object.getClass()==cs){
           T t= (T) object;
           return t;
       }
       return null;
    }

    public static void setAlpha(View view,float alpha) {
        if(view==null||view.getAlpha()==alpha){
            return;
        }
        if(alpha>1){   //大于1过滤
           alpha=1F;
        }
        view.setAlpha(alpha);
    }

    public static void addToViewGroup(ViewGroup contentView, View view, int position) {
        if(contentView==null||view==null){
            DebugUtil.sendException("contentView=="+contentView+"&&view=="+view);
            return;
        }
        removeToParent(view);
        if(position==-1){
            contentView.addView(view);
        }else{
            contentView.addView(view,position);
        }
    }

    public static void setViewOutlineProvider(View  view, final int radius) {
        view.setClipToOutline(true);
        view.setOutlineProvider( new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });

    }


    /*生成webview*/
    public static WebView createWebView(Context context){
        NoScrollWebView webView=new NoScrollWebView(context);
        settingWebView(webView);
        return webView;
    }

    public static void settingWebView(final WebView webView) {

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//设置能够解析Javascript
        webSettings.setDomStorageEnabled(true);//设置适应Html5的一些方法
        webView.setWebChromeClient(new WebChromeClient());
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setDatabaseEnabled(false);
        //webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                } else {
                    view.loadUrl(request.toString());
                }
                return true;
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                String width = CommonAppConfig.getWindowWidth()+"";


                final ViewGroup.LayoutParams  params =  webView.getLayoutParams();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams=view.getLayoutParams();
                        layoutParams.height= ViewGroup.LayoutParams.WRAP_CONTENT;
                        view.setLayoutParams(layoutParams);
                    }
                });
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
    }


    static final String mimeType = "text/html";
    static final String encoding = "utf-8";
    public static void loadHtml(WebView webView,String html){

        if(webView!=null){
            html = html.replace("<img", "<img style=\"display:        ;max-width:100%;\"");
            webView.loadData( html, mimeType,
                    encoding);
        }
    }


}
