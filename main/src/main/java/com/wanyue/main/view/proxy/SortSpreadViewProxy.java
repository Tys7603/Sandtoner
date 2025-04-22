package com.wanyue.main.view.proxy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.main.R;
import com.wanyue.shop.view.widet.ThreeCheckImageView;

public class SortSpreadViewProxy extends RxViewProxy implements View.OnClickListener {
    private ViewGroup mBtnTeam;
    private ViewGroup mBtnMoney;
    private ViewGroup mBtnOrder;

    private ThreeCheckImageView mImgMoney;
    private ThreeCheckImageView mImgTeam;
    private ThreeCheckImageView mImgOrder;

    private TextView mTvMoney;
    private TextView mTvTeam;
    private TextView mTvOrder;

    private String mSortArg="";

    private OnSortArgsChangeListnter mArgsChangeListnter;

    @Override
    public int getLayoutId() {
        return R.layout.vie_sort_spread;
    }

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mBtnTeam = (ViewGroup) findViewById(R.id.btn_team);
        mImgTeam = (ThreeCheckImageView) findViewById(R.id.img_team);
        mBtnMoney = (ViewGroup) findViewById(R.id.btn_money);
        mTvMoney = (TextView) findViewById(R.id.tv_money);
        mImgMoney = (ThreeCheckImageView) findViewById(R.id.img_money);
        mBtnOrder = (ViewGroup) findViewById(R.id.btn_order);
        mImgOrder = (ThreeCheckImageView) findViewById(R.id.img_order);
        mTvTeam = (TextView) findViewById(R.id.tv_team);
        mTvMoney = (TextView) findViewById(R.id.tv_money);
        mTvOrder = (TextView) findViewById(R.id.tv_order);

        mBtnTeam.setOnClickListener(this);
        mBtnMoney.setOnClickListener(this);
        mBtnOrder.setOnClickListener(this);

        mImgTeam.setOnCheckChangeClickListner(mOnCheckChangeClickListner);
        mImgMoney.setOnCheckChangeClickListner(mOnCheckChangeClickListner);
        mImgOrder.setOnCheckChangeClickListner(mOnCheckChangeClickListner);
    }

    private ThreeCheckImageView.OnCheckChangeClickListner mOnCheckChangeClickListner=new ThreeCheckImageView.OnCheckChangeClickListner() {
        @Override
        public void onCheckChange(View view, int state) {
            int id=view.getId();
            if(id== R.id.img_order){
                setSortOrder(state);
            }else if(id== R.id.img_money){
                setSortMoney(state);
            }else if(id==R.id.img_team){
                setSortTeam(state);
            }
        }
    };

    private void setSortTeam(int state) {
        if(state==ThreeCheckImageView.UN_CHECKED){
            mSortArg=null;
        }else if(state==ThreeCheckImageView.INDETERMINATE_CHECKED){
            mSortArg="childCount DESC";
            clearThreeCheckImageViewSelect(mImgMoney);
            clearThreeCheckImageViewSelect(mImgOrder);
        }else if(state==ThreeCheckImageView.CHECKED){
            mSortArg="childCount ASC";
            clearThreeCheckImageViewSelect(mImgMoney);
            clearThreeCheckImageViewSelect(mImgOrder);
        }
    }
    private void setSortMoney(int state) {
        if(state==ThreeCheckImageView.UN_CHECKED){
            mSortArg=null;
        }else if(state==ThreeCheckImageView.INDETERMINATE_CHECKED){
            mSortArg="numberCount DESC";
            clearThreeCheckImageViewSelect(mImgOrder);
            clearThreeCheckImageViewSelect(mImgTeam);
        }else if(state==ThreeCheckImageView.CHECKED){
            mSortArg="numberCount ASC";
            clearThreeCheckImageViewSelect(mImgOrder);
            clearThreeCheckImageViewSelect(mImgTeam);
        }
    }

    private void setSortOrder(int state) {
        if(state==ThreeCheckImageView.UN_CHECKED){
            mSortArg=null;
        }else if(state==ThreeCheckImageView.INDETERMINATE_CHECKED){
            mSortArg="orderCount DESC";
            clearThreeCheckImageViewSelect(mImgTeam);
            clearThreeCheckImageViewSelect(mImgMoney);
        }else if(state==ThreeCheckImageView.CHECKED){
            mSortArg="orderCount ASC";
            clearThreeCheckImageViewSelect(mImgTeam);
            clearThreeCheckImageViewSelect(mImgMoney);
        }

    }

    /*当选中一个的时候，清空其他条件的选中状态*/
    private void clearThreeCheckImageViewSelect(ThreeCheckImageView checkImageView) {
        if(checkImageView!=null){
            checkImageView.setCheckedNotifyChange(ThreeCheckImageView.UN_CHECKED,false);
        }
    }

    public void setArgsChangeListnter(OnSortArgsChangeListnter argsChangeListnter) {
        mArgsChangeListnter = argsChangeListnter;
    }

    public interface OnSortArgsChangeListnter{
        public void sortChange(String arg);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id== R.id.btn_team){
            if(mImgTeam!=null){
                mImgTeam.change();
                if(mArgsChangeListnter!=null){
                   mArgsChangeListnter.sortChange(mSortArg);
                }
            }
        }else if(id== R.id.btn_money){
            if( mImgMoney!=null){
                mImgMoney.change();
                if(mArgsChangeListnter!=null){
                    mArgsChangeListnter.sortChange(mSortArg);
                }
            }
        }else if(id== R.id.btn_order){
            if( mImgOrder!=null){
                mImgOrder.change();
                if(mArgsChangeListnter!=null){
                   mArgsChangeListnter.sortChange(mSortArg);
                }
            }
        }
    }
}
