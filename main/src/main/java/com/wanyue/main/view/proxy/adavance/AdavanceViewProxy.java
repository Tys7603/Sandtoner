package com.wanyue.main.view.proxy.adavance;

import android.view.View;
import android.widget.EditText;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.DebugUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.main.R;
import com.wanyue.main.R2;
import com.wanyue.main.bean.CommissionBankBean;
import com.wanyue.main.bean.CommitAdavanceBean;
import butterknife.BindView;
import butterknife.OnTextChanged;

/**
 * The type Adavance view proxy.
 */
public abstract  class AdavanceViewProxy extends RxViewProxy {
    /**
     * The M data.
     */
    protected CommissionBankBean mData;
    /**
     * The M commit bundle.
     */
    protected CommitAdavanceBean mCommitBundle;

    /**
     * The M tv advance money.
     */
    @BindView(R2.id.tv_advance_money)
    EditText mTvAdvanceMoney;

    /**
     * Init bundle.
     */
    public void initBundle(){
        if(mCommitBundle==null){
           mCommitBundle=new CommitAdavanceBean();
           mCommitBundle.setExtract_type(getAdvanceType());
        }
    }

    /**
     * Gets commit bundle.
     *
     * @return the commit bundle
     */
    public CommitAdavanceBean getCommitBundle() {
        return mCommitBundle;
    }

    /**
     * Watch money.
     *
     * @param sequence the sequence
     * @param start    the start
     * @param before   the before
     * @param count    the count
     */
    @OnTextChanged(value = R2.id.tv_advance_money, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchMoney(CharSequence sequence, int start, int before, int count) {
        String string = sequence.toString();
        initBundle();
        if (mCommitBundle != null) {
            mCommitBundle.setMoney(string);
        }
    }


    /**
     * Check money boolean.
     *
     * @return the boolean
     */
    protected boolean checkMoney() {
        String priceString=mCommitBundle.getMoney();
        try {
            double price=Double.parseDouble(priceString);
            double currentCount=Double.parseDouble(mData.getCommissionCount());
            if(price>currentCount){
                ToastUtil.show("Cannot be greater than the withdrawable amount");
                return false;
            }
            double minPrice=Double.parseDouble(mData.getMinPrice());
            if(price<minPrice){
                ToastUtil.show("Cannot be less than the minimum withdrawal amount");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.show("The amount entered is in incorrect format");
            return false;
        }
        return true;
    }

    /**
     * Gone to parent.
     */
    public  void goneToParent(){
        if(mContentView!=null){
           ViewUtil.setVisibility(mContentView, View.GONE);
        }
    }

    /**
     * Show to parent.
     */
    public  void showToParent(){
        if(mContentView!=null){
          ViewUtil.setVisibility(mContentView, View.VISIBLE);
        }
    }

    /**
     * Set data.
     *
     * @param data the data
     */
    public void setData(CommissionBankBean data){
        if (data == null) {
            DebugUtil.sendException("CommissionBankBean==null");
            return;
        }
        mData=data;
        if (mTvAdvanceMoney != null) {
            mTvAdvanceMoney.setHint(getString(R.string.min_advance_money, mData.getMinPrice()));
        }
    }

    /**
     * Local check commit message boolean.
     *
     * @return the boolean
     */
    public abstract boolean localCheckCommitMessage();

    /**
     * Gets advance type.
     *
     * @return the advance type
     */
    public abstract String getAdvanceType();

}
