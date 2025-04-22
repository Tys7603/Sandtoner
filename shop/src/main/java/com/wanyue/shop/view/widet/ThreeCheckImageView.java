package com.wanyue.shop.view.widet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * The type Three check image view.
 */
public class ThreeCheckImageView extends AppCompatImageView {
    private  boolean enableClick;
    private  OnCheckChangeClickListner mOnCheckChangeClickListner;

    /**
     * The constant UN_CHECKED.
     */
    public static final int UN_CHECKED=1;
    /**
     * The constant INDETERMINATE_CHECKED.
     */
    public static final int INDETERMINATE_CHECKED=2;
    /**
     * The constant CHECKED.
     */
    public static final int CHECKED=3;

    private int mCheck=UN_CHECKED;

    private Drawable[]mDrawableList;

    /**
     * Instantiates a new Three check image view.
     *
     * @param context the context
     */
    public ThreeCheckImageView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Instantiates a new Three check image view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ThreeCheckImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseXml(context,attrs);
        init(context);

    }

    /**
     * Instantiates a new Three check image view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ThreeCheckImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseXml(context,attrs);

        init(context);
    }

    private void parseXml(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, com.wanyue.common.R.styleable.CheckImageView);
        Drawable drawable=ta.getDrawable(com.wanyue.common.R.styleable.CheckImageView_deault_image);
        if(drawable!=null) {
            addDrawable(drawable,0);
        }
        drawable=ta.getDrawable(com.wanyue.common.R.styleable.CheckImageView_indeterminate_image);
        if(drawable!=null) {
            addDrawable(drawable,1);
        }
        drawable= ta.getDrawable(com.wanyue.common.R.styleable.CheckImageView_select_image);
        if(drawable!=null) {
           addDrawable(drawable,2);
        }
        enableClick=ta.getBoolean(com.wanyue.common.R.styleable.CheckImageView_enable_click,true);
    }

    /**
     * Add drawable.
     *
     * @param drawable the drawable
     * @param index    the index
     */
    public void addDrawable(Drawable drawable,int index){
        if(mDrawableList==null){
           mDrawableList=new Drawable[3];
        }
           mDrawableList[index]=drawable;
    }

    /**
     * Gets image drawable.
     *
     * @param index the index
     * @return the image drawable
     */
    public Drawable getImageDrawable(int index) {
        if(mDrawableList!=null){
            return mDrawableList[index];
        }
        return null;
    }


    /**
     * Set checked.
     *
     * @param check the check
     */
    /*设置选中状态*/
    public void setChecked(int check){
        setCheckedNotifyChange(check,true);
    }


    /**
     * Set checked notify change.
     *
     * @param check      the check
     * @param needNotify the need notify
     */
    public void setCheckedNotifyChange(int check,boolean needNotify){
        mCheck=check;
        if(mOnCheckChangeClickListner!=null&&needNotify){
           mOnCheckChangeClickListner.onCheckChange(this,mCheck);
        }
        refeshUI();
    }

    /**
     * Sets on check change click listner.
     *
     * @param onCheckChangeClickListner the on check change click listner
     */
    public void setOnCheckChangeClickListner(OnCheckChangeClickListner onCheckChangeClickListner) {
        mOnCheckChangeClickListner = onCheckChangeClickListner;
    }

    /**
     * Refesh ui.
     */
    public void refeshUI() {
        if(mDrawableList==null){
            return;
        }
        if(mCheck==UN_CHECKED){
            setImageDrawable(mDrawableList[0]);
        }else if(mCheck==INDETERMINATE_CHECKED){
            setImageDrawable(mDrawableList[1]);
        }else if(mCheck==CHECKED){
            setImageDrawable(mDrawableList[2]);
        }
    }

    /**
     * Gets check.
     *
     * @return the check
     */
    public int getCheck() {
        return mCheck;
    }

    private void init(Context context){
        refeshUI();
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(enableClick){
                   change();
               }
            }
        });
    }

    /**
     * Change.
     */
    public void change() {
        if(mCheck==UN_CHECKED){
           mCheck=INDETERMINATE_CHECKED;
        }else if(mCheck==INDETERMINATE_CHECKED){
            mCheck=CHECKED;
        }else if(mCheck==CHECKED){
            mCheck=UN_CHECKED;
        }
        if(mOnCheckChangeClickListner!=null){
            mOnCheckChangeClickListner.onCheckChange(this,mCheck);
        }
        refeshUI();
    }

    /**
     * The interface On check change click listner.
     */
    public  interface OnCheckChangeClickListner{
        /**
         * On check change.
         *
         * @param view  the view
         * @param state the state
         */
        public void onCheckChange(View view, int state);
    }

}
