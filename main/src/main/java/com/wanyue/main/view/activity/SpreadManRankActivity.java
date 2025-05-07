package com.wanyue.main.view.activity;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.SpreadManRankAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.SpreadManRankBean;
import java.util.List;
import io.reactivex.Observable;

public class SpreadManRankActivity extends BaseActivity {
    private static final String TYPE_WEEK="week";
    private static final String TYPE_MONTH="month";

    private RadioGroup mRadioGroupRank;

  private SpreadManRankAdapter mSpreadManRankAdapter;
    private ImageView mImgAvatar2;
    private TextView mTvName2;
    private TextView mTvUserNum2;
    private ImageView mImgAvatar1;
    private TextView mTvName1;
    private TextView mTvUserNum1;
    private ImageView mImgAvatar3;
    private TextView mTvName3;
    private TextView mTvUserNum3;

    private ViewGroup mVpRank1;
    private ViewGroup mVpRank2;
    private ViewGroup mVpRank3;

    private RxRefreshView<SpreadManRankBean>mRefreshView;
    private String mType=TYPE_WEEK;

    @Override
    public void init() {
        setTabTitle(R.string.spread_tip5);
        mRadioGroupRank = (RadioGroup) findViewById(R.id.radio_group_rank);
        mImgAvatar2 = findViewById(R.id.img_avatar_2);
        mTvName2 = (TextView) findViewById(R.id.tv_name_2);
        mTvUserNum2 = (TextView) findViewById(R.id.tv_user_num_2);
        mImgAvatar1 =  findViewById(R.id.img_avatar_1);
        mTvName1 = (TextView) findViewById(R.id.tv_name_1);
        mTvUserNum1 = (TextView) findViewById(R.id.tv_user_num_1);
        mImgAvatar3 =  findViewById(R.id.img_avatar_3);
        mTvName3 = (TextView) findViewById(R.id.tv_name_3);
        mTvUserNum3 = (TextView) findViewById(R.id.tv_user_num_3);
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mVpRank2 = (ViewGroup) findViewById(R.id.vp_rank_2);
        mVpRank1 = (ViewGroup) findViewById(R.id.vp_rank_1);
        mVpRank3 = (ViewGroup) findViewById(R.id.vp_rank_3);

        mRadioGroupRank.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radio_button_week){
                    selectType(TYPE_WEEK);
                }else if(checkedId==R.id.radio_button_month){
                    selectType(TYPE_MONTH);
                }
            }
        });
        mSpreadManRankAdapter=new SpreadManRankAdapter(null);
        mRefreshView.setAdapter(mSpreadManRankAdapter);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,0));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<SpreadManRankBean>() {
            @Override
            public Observable<List<SpreadManRankBean>> loadData(int p) {
                return getData(p);
            }

            @Override
            public void isRefresh(List<SpreadManRankBean> data, boolean isRefresh) {
               if(!isRefresh){
                   return;
               }
               int size= ListUtil.getSize(data);

               if(size==0){
                 setTopThree(null,null,null);
               }else if(size==1){
                 setTopThree( data.remove(0),null,null);
               }else if(size==2){
                 setTopThree(data.remove(0),data.remove(0),null);
               }else if(size>=3){
                 setTopThree(data.remove(0),data.remove(0),data.remove(0));
               }
            }

            @Override
            public void compelete(List<SpreadManRankBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
        mRadioGroupRank =  findViewById(R.id.radio_group_rank);
        mRadioGroupRank.setClipToOutline(true);

        mRadioGroupRank.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int radius= DpUtil.dp2px(15);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
    }

    private void setTopThree(SpreadManRankBean rankBean1, SpreadManRankBean rankBean2, SpreadManRankBean rankBean3) {
        if(rankBean1!=null){
            mTvName1.setText(rankBean1.getNickname());
            ImgLoader.display(this,rankBean1.getAvatar(),mImgAvatar1);
            mTvUserNum1.setText(rankBean2.getCount()+"Agents");
            ViewUtil.setVisibility(mVpRank1,View.VISIBLE);
        }else{
            ViewUtil.setVisibility(mVpRank1,View.INVISIBLE);
        }
        if(rankBean2!=null){
            mTvName2.setText(rankBean2.getNickname());
            ImgLoader.display(this,rankBean2.getAvatar(),mImgAvatar2);
            mTvUserNum2.setText(rankBean2.getCount()+"Agents");
            ViewUtil.setVisibility(mVpRank2,View.VISIBLE);
        }else{
            ViewUtil.setVisibility(mVpRank2,View.INVISIBLE);
        }
        if(rankBean3!=null){
            mTvName3.setText(rankBean3.getNickname());
            ImgLoader.display(this,rankBean3.getAvatar(),mImgAvatar3);
            mTvUserNum3.setText(rankBean3.getCount()+"Agents");
            ViewUtil.setVisibility(mVpRank3,View.VISIBLE);
        }else{
            ViewUtil.setVisibility(mVpRank3,View.INVISIBLE);
        }
    }

    private void selectType(String type) {
        if(StringUtil.equals(mType,type)){
            return;
        }
        mType=type;
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }

    private Observable<List<SpreadManRankBean>> getData(int p) {
        return MainAPI.getPersonRankList(p,mType);
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        if(mRefreshView!=null){
            mRefreshView.initData();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_spread_man_rank;
    }
}
