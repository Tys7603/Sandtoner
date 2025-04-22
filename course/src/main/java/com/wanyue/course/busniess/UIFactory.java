package com.wanyue.course.busniess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.CommonApplication;
import com.wanyue.common.custom.NoScrollWebView;
import com.wanyue.common.utils.BitmapUtil;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.SpannableStringUtils;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.course.R;
import com.wanyue.course.bean.ProjectBean;

import java.text.DecimalFormat;


public class UIFactory {
    public static final String UNIT="¥";
   
    private static int colorPwd=-1;
    private static int colorFree=-1;
    private static int colorCharge=-1;

    /*获取价格标签颜色*/
    public static int getPriceViewColor(int payType){
        switch (payType){
            
            case CourseConstants.TYPE_PAY_PWD:
                if(colorPwd==-1){
                   colorPwd= ResourceUtil.getColor(R.color.blue2);
                }
                return colorPwd ;
            case CourseConstants.TYPE_PAY_FREE:
                if(colorFree==-1){
                   colorFree= ResourceUtil.getColor(R.color.global);
                }
                return colorFree ;
        }
        if(colorCharge==-1){
           colorCharge= ResourceUtil.getColor(R.color.red20);
        }
        return colorCharge ;
    }

    public static int getLessonTagName(int contenType){
        switch (contenType){
            case CourseConstants.LESSON_TYPE_COTENT_IMG_TEXT:
                return R.string.img_text ;
            case CourseConstants.LESSON_TYPE_COTENT_VIDEO:
                return R.string.video ;
            case CourseConstants.LESSON_TYPE_COTENT_AUDIO:
                return R.string.audio;
            default:
                return R.string.live;
        }
    }



    public static String getLiveTypeName(int contenType){
        switch (contenType){
            case CourseConstants.LESSON_TYPE_LIVE_PPT:
                return"ppt讲解";
            case CourseConstants.LESSON_TYPE_LIVE_AUDIO:
                return"语音讲解";
            case CourseConstants.LESSON_TYPE_LIVE_VIDEO:
                return"视频讲解";
            case CourseConstants.LESSON_TYPE_LIVE_TEACHING:
                return"白板互动";
            case CourseConstants.LESSON_TYPE_LIVE_NORMAL:
                return"普通直播";
            default:
                return null;
        }
    }


    /*获取内容类型*/
    public static String getContentName(int contenType){
        switch (contenType){
            case CourseConstants.CONTENT_IMAGE_TEXT:
                return "图文自学" ;
            case CourseConstants.CONTENT_VIDEO:
                return "视频自学" ;
            case CourseConstants.CONTENT_AUDIO:
                return "语音自学";
        }
        return null ;
    }

    private static String mAttentionedString;
    private static String mNoAttentionedString;
    private static int mAttentionedColor;
    private static int mNoAttentionedolor;

    /*设置关注label的样式*/
    public static void setFollowTextView(TextView textView,int isFollow){
        if(textView==null){
            return;
        }
        if(mAttentionedString==null){
           mAttentionedString= WordUtil.getString(R.string.following);
        }
        if(mNoAttentionedString==null){
           mNoAttentionedString= WordUtil.getString(R.string.follow_add);
        }
        if(mAttentionedColor==0){
            mAttentionedColor= ResourceUtil.getColor(R.color.gray1);
        }
        if(mNoAttentionedolor==0){
            mNoAttentionedolor= ResourceUtil.getColor(R.color.global);
        }
        if(isFollow==0){
            textView.setText(mNoAttentionedString);
            textView.setTextColor(mNoAttentionedolor);
        }else{
            textView.setText(mAttentionedString);
            textView.setTextColor(mAttentionedColor);
        }
    }



    public static CharSequence getTypeSPanTag(CharSequence content,String tag,int textSize,int id){
        SpannableStringBuilder ssb = new SpannableStringBuilder(content+"\t");
        SpannableString spannableString=new SpannableString("\t \t \t");
        int dpSize= DpUtil.dp2px(textSize);
        Drawable drawable= BitmapUtil.zoomDrawable(CommonApplication.sInstance.getResources(),id, dpSize);
        L.e("drawable width=="+drawable.getIntrinsicWidth());
        L.e("drawable height=="+drawable.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(drawable,ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(imageSpan,1,spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(spannableString);
        return ssb;
    }


    public static String appendUnit(double price){
       return UNIT+price;
    }


    public static String appendUnit(String price){
        return UNIT+price;
    }

    /*设置textview*/
    public static void setTextCount(TextView textView,int count){
        if(textView==null){
            return;
        }
        textView.setText(Integer.toString(count));
        if(count<=0){
           textView.setVisibility(View.INVISIBLE);
        }else{
           textView.setVisibility(View.VISIBLE);
        }
    }
    private static DecimalFormat df;

    public static String getFormatPrice(double price) {
        if(price==0){
            return "0";
        }
        if(df==null){
           df = new DecimalFormat("#0.00");
        }
        String formatPrice="¥"+df.format(price);
        return formatPrice;
    }

    public static String getFormatPrice(float price) {
        if(price==0){
            return "0";
        }
        if(df==null){
            df = new DecimalFormat("#0.00");
        }
        ProjectBean projectBean;

        String formatPrice="¥"+df.format(price);
        return formatPrice;
    }


    public  static CharSequence createPrice(String price){
       CharSequence formatPrice  = SpannableStringUtils.getBuilder(UIFactory.UNIT).append(" ").setTextSize(11).append(price).setTextSize(15).create();
        return formatPrice;
    }



    public static String getFormatPrice(String money) {
        return "¥"+money;
    }
}
