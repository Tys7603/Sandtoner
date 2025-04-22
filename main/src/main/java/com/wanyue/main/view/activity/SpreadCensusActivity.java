package com.wanyue.main.view.activity;


import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.utils.JsonUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.SpreadManAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.SpreadManBean;
import com.wanyue.main.view.proxy.SearchSpreadViewProxy;
import com.wanyue.main.view.proxy.SortSpreadViewProxy;
import com.wanyue.shop.view.view.SearchViewProxy;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * The type Spread census activity.
 */
public class SpreadCensusActivity extends BaseActivity implements SearchViewProxy.SeacherListner, SortSpreadViewProxy.OnSortArgsChangeListnter, RadioGroup.OnCheckedChangeListener {

    private ImageView mImgBg;
    private TextView mTvHeadTitle;
    private TextView mTvNumTip;
    private RadioButton mBtnLevel1;
    private RadioButton mBtnLevel2;

    private ViewGroup mVpSearchContainer;
    private ViewGroup mVpSortContainer;
    private RadioGroup mBtnLevelGroup;
    private RxRefreshView<SpreadManBean> mRefreshView;
    private SpreadManAdapter mSpreadManAdapter;

    private String mKeyWard;
    private String mSortArg;
    private int mGrade;
    @Override
    public void init() {
        setTabTitle(R.string.spread_tip10);
        mImgBg = (ImageView) findViewById(R.id.img_bg);
        mTvHeadTitle = (TextView) findViewById(R.id.tv_head_title);
        mTvNumTip = (TextView) findViewById(R.id.tv_num_tip);
        mBtnLevel1 = (RadioButton) findViewById(R.id.btn_level1);
        mBtnLevel2 = (RadioButton) findViewById(R.id.btn_level2);
        mVpSearchContainer =  findViewById(R.id.vp_search_container);
        mVpSortContainer =  findViewById(R.id.vp_sort_container);
        mRefreshView =  findViewById(R.id.refreshView);
        mBtnLevelGroup = findViewById(R.id.btn_level_group);

        mSpreadManAdapter=new SpreadManAdapter(null);
        mRefreshView.setAdapter(mSpreadManAdapter);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,1));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<SpreadManBean>() {
            @Override
            public Observable<List<SpreadManBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<SpreadManBean> data) {

            }
            @Override
            public void error(Throwable e) {
                e.printStackTrace();
            }
        });
        SearchSpreadViewProxy searchViewProxy=new SearchSpreadViewProxy();
        searchViewProxy.setEnableAutoSearch(true);
        searchViewProxy.setSeacherListner(this);
        getViewProxyMannger().addViewProxy(mVpSearchContainer,searchViewProxy,searchViewProxy.getDefaultTag());

        SortSpreadViewProxy sortSpreadViewProxy=new SortSpreadViewProxy();
        sortSpreadViewProxy.setArgsChangeListnter(this);
        getViewProxyMannger().addViewProxy(mVpSortContainer,sortSpreadViewProxy,sortSpreadViewProxy.getDefaultTag());
        mBtnLevelGroup.setOnCheckedChangeListener(this);
    }

    private Observable<List<SpreadManBean>> getData(int p) {
        return MainAPI.getSpreadPeopleList(p,mGrade,mKeyWard,mSortArg).map(new Function<JSONObject, List<SpreadManBean>>() {
            @Override
            public List<SpreadManBean> apply(JSONObject jsonObject) throws Exception {
                int totalCount=jsonObject.getIntValue("total");
                int totalLevelCount=jsonObject.getIntValue("totalLevel");
                int allCount=totalCount+totalLevelCount;
                mTvNumTip.setText(Integer.toString(allCount));

                String total=Integer.toString(totalCount);
                String totalLevel=Integer.toString(totalLevelCount);
                mBtnLevel1.setText(getString(R.string.spread_tip_11,total));
                mBtnLevel2.setText(getString(R.string.spread_tip_12,totalLevel));
                JSONArray jsonArray=jsonObject.getJSONArray("list");
                List<SpreadManBean>list= JsonUtil.getData(jsonArray,SpreadManBean.class);
                return list;
            }
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_spread_census;
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }


    @Override
    public void search(String keyward) {
        this.mKeyWard=keyward;
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }


    @Override
    public void sortChange(String arg) {
        this.mSortArg=arg;
        if(mRefreshView!=null){
          mRefreshView.initData();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId==R.id.btn_level1){
                mGrade=0;
            }else if(checkedId==R.id.btn_level2){
                mGrade=1;
            }
        if(mRefreshView!=null){
            mRefreshView.initData();
        }
    }
}
