package com.wanyue.course.evaluate.view.proxy;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.BitmapUtil;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.course.R;
import com.wanyue.course.R2;
import com.wanyue.course.api.CourseAPI;
import com.wanyue.course.bean.EvaluateBean;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.evaluate.adapter.EvaluateListAdapter;
import com.wanyue.course.evaluate.view.activity.PublishEvaluateActivity;
import com.wanyue.course.widet.RatingStar;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Function;


/*评论列表*/
public class EvaluateListViewProxy extends RxViewProxy {
    @BindView(R2.id.rating_star)
    RatingStar mRatingStar;

    @BindView(R2.id.tv_all_star)
    TextView mTvAllStar;

    @BindView(R2.id.btn_evaluate)
    TextView mBtnEvaluate;

    @BindView(R2.id.refreshView)
    RxRefreshView<EvaluateBean> mRefreshView;

    EvaluateListAdapter mEvaluateListAdapter;

    private Bitmap mNormalStarBitmap;
    private Bitmap mSelectStarBitmap;

    private String mProjectId;
    private ProjectBean mProjectBean;
    @Override
    public int getLayoutId() {
        return R.layout.view_course_evaluate_list;
    }
    @Override
    public void onStart() {
        super.onStart();
        initData();
    }
    private void initData() {
     if(mRefreshView!=null){
        mRefreshView.initData();
      }
        checkEvaluateButtonVisible();
    }

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mProjectId= (String) getArgMap().get(Constants.KEY_ID);

        int size = DpUtil.dp2px(17);
        mNormalStarBitmap = BitmapUtil.thumbImageWithMatrix(getResources(), R.drawable.icon_star_unselect, size, size);
        mSelectStarBitmap = BitmapUtil.thumbImageWithMatrix(getResources(), R.drawable.icon_star_select, size, size);
        mRatingStar.setNormalImg(mNormalStarBitmap);
        mRatingStar.setFocusImg(mSelectStarBitmap);

        mEvaluateListAdapter=new EvaluateListAdapter(null);
        mEvaluateListAdapter.setNormalStarBitmap(mNormalStarBitmap);
        mEvaluateListAdapter.setSelectStarBitmap(mSelectStarBitmap);

        mRefreshView.setAdapter(mEvaluateListAdapter);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity()));

        mRefreshView.setIconId(R.mipmap.icon_default_no_data);
        mRefreshView.setEmptyLevel(1);
        mRefreshView.setNoDataTip(getString(R.string.no_evaluate));

        mRefreshView.setDataListner(new RxRefreshView.DataListner<EvaluateBean>() {
            @Override
            public Observable<List<EvaluateBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<EvaluateBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
    }

    /*获取数据*/
    private Observable<List<EvaluateBean>> getData(int p) {
        return CourseAPI.getEvaluateList(mProjectId,p).map(new Function<JSONObject, List<EvaluateBean>>() {
            @Override
            public List<EvaluateBean> apply(@NonNull JSONObject jsonObject) throws Exception {
                float star=jsonObject.getFloat("star");
                setTotalData(star);
                String json=jsonObject.getJSONArray("list").toJSONString();
                List<EvaluateBean>list= JSON.parseArray(json,EvaluateBean.class);
                return list;
            }
        }).compose(this.<List<EvaluateBean>>bindUntilOnDestoryEvent());
    }

    @OnClick(R2.id.btn_evaluate)
    public void toEvaluate() {
        if(mProjectBean!=null){
            PublishEvaluateActivity.forward((BaseActivity) getActivity(),mProjectBean);
        }
    }

    public void setTotalData(float totalStar) {
        int number=(int) totalStar-1;
        if(number<0){
           number=0;
        }
        mRatingStar.setNumber((int) totalStar);
        mRatingStar.setPosition(number);
        if(totalStar==0){
            mTvAllStar.setText("0.0");
        }else{
          mTvAllStar.setText(Float.toString(totalStar));
        }
    }
    public void setProjectBean(ProjectBean projectBean) {
        mProjectBean = projectBean;
        checkEvaluateButtonVisible();
    }


    private void checkEvaluateButtonVisible() {
       boolean ifBuy=mProjectBean==null?false:mProjectBean.ifBuy();
       if(ifBuy){
          ViewUtil.setVisibility(mBtnEvaluate, View.VISIBLE);
       }else{
           ViewUtil.setVisibility(mBtnEvaluate, View.INVISIBLE);
       }
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}
