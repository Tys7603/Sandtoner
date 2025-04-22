package com.wanyue.shop.view.view;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.R2;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

/**
 * The type Search view proxy.
 */
public class SearchViewProxy extends RxViewProxy {

    /**
     * The M et search.
     */
    @BindView(R2.id.et_search)
    EditText mEtSearch;
    /**
     * The M btn clear.
     */
    @BindView(R2.id.btn_clear)
    ImageView mBtnClear;

    private String mKeyward;
    private SeacherListner mSeacherListner;
    private MyHandler mHandler;
    private String mHint;
    private boolean mIsAuto=true;
    /**
     * The M im mannger.
     */
    InputMethodManager mImMannger;

    @Override
    public int getLayoutId() {
        return R.layout.item_search;
    }

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        if(mHint!=null){
           mEtSearch.setHint(mHint);
        }
        if(mIsAuto){
          mHandler = new MyHandler(this);
        }
        if(mKeyward!=null){
           mEtSearch.setText(mKeyward);
        }
        ViewUtil.setEditextEnable2(mEtSearch);
    }

    /**
     * Set enable auto search.
     *
     * @param isAuto the is auto
     */
    public void setEnableAutoSearch(boolean isAuto){
        mIsAuto=isAuto;
    }

    /**
     * Watch text change.
     *
     * @param sequence the sequence
     * @param start    the start
     * @param before   the before
     * @param count    the count
     */
    /*原来的文本监听*/
    @OnTextChanged(value = R2.id.et_search, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchTextChange(CharSequence sequence, int start, int before, int count) {
         if(TextUtils.isEmpty(sequence)){
             mBtnClear.setVisibility(View.INVISIBLE);
         }else{
             mBtnClear.setVisibility(View.VISIBLE);
         }
         if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessageDelayed(0, 500);
        }
    }

    /**
     * Sets hint.
     *
     * @param hint the hint
     */
    /*设置隐藏的显示*/
    public void setHint(String hint) {
        mHint = hint;
    }

    /**
     * Watch editor action boolean.
     *
     * @param v        the v
     * @param actionId the action id
     * @param event    the event
     * @return the boolean
     */
    /*监听输入框事件*/
    @OnEditorAction(value = R2.id.et_search)
    public boolean watchEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search();
            return true;
        }

        return false;
    }

    /**
     * Search.
     */
    public void search() {
        mKeyward = mEtSearch.getText().toString();
        if (mSeacherListner != null) {
            mSeacherListner.search(mKeyward);
        }
        if(mImMannger==null){
           mImMannger = (InputMethodManager) getActivity()
           .getSystemService(getActivity().INPUT_METHOD_SERVICE);
        }
        mImMannger.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);
        mEtSearch.clearFocus();
    }

    /**
     * Sets seacher listner.
     *
     * @param seacherListner the seacher listner
     */
    public void setSeacherListner(SeacherListner seacherListner) {
        mSeacherListner = seacherListner;
    }

    /**
     * Clear.
     */
    @OnClick(R2.id.btn_clear)
    public void clear() {
        mEtSearch.setText("");
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        mKeyward=title;
        if(mEtSearch!=null){
           mEtSearch.setText(mKeyward);
        }
    }

    /**
     * The interface Seacher listner.
     */
    public static interface SeacherListner {
        /**
         * Search.
         *
         * @param keyward the keyward
         */
        public void search(String keyward);
    }

    private static class MyHandler extends Handler {
        private SearchViewProxy mSearchViewProxy;

        /**
         * Instantiates a new My handler.
         *
         * @param seacherListner the seacher listner
         */
        public MyHandler(SearchViewProxy seacherListner) {
            mSearchViewProxy = seacherListner;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mSearchViewProxy != null) {
                mSearchViewProxy.search();
            }
        }

        /**
         * Release.
         */
        public void release() {
            mSearchViewProxy = null;
        }
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSeacherListner = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.release();
        }
        mHandler = null;
    }
}
