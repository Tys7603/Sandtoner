package com.wanyue.shop.view.activty;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.adapter.GoodsFootAdapter;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.FoodGoodsBean;
import com.wanyue.shop.bean.FootSectionBean;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class GoodsFootActivity extends BaseActivity {
    private RxRefreshView<FootSectionBean> mRefreshView;
    private GoodsFootAdapter mGoodsFootAdapter;

    @Override
    public void init() {
        setTabTitle("My Footprints");
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLevel(1);
        mRefreshView.setIconId(R.mipmap.icon_default_no_data);
        mRefreshView.setNoDataTip("暂无浏览记录，\n 赶快去挑选喜欢的东西吧～");
        mGoodsFootAdapter=new GoodsFootAdapter(null);
        mGoodsFootAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                FootSectionBean footSectionBean=mGoodsFootAdapter.getItem(i);
                if(footSectionBean.t!=null){
                   GoodsDetailActivity.forward(mContext,footSectionBean.t.getId());
                }
            }
        });
        mRefreshView.setAdapter(mGoodsFootAdapter);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<FootSectionBean>() {
            @Override
            public void before(List<FootSectionBean> data) {
                super.before(data);
            }
            @Override
            public Observable<List<FootSectionBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<FootSectionBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
      RxRefreshView.ReclyViewSetting reclyViewSetting= RxRefreshView.ReclyViewSetting.createGridSetting(this, 3, 5, new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if(mGoodsFootAdapter.getItem(i).isHeader){
                    return 3;
                }
                return 1;
            }
        });
        mRefreshView.setReclyViewSetting(reclyViewSetting);
        mRefreshView.initData();
    }

    private Observable<List<FootSectionBean>> getData(final int p) {
        String id= getLastId(p);
        return ShopAPI.getFootGoodsVideoList(id).map(new Function<List<FoodGoodsBean>, List<FootSectionBean>>() {
            @Override
            public List<FootSectionBean> apply(@NonNull List<FoodGoodsBean> list) throws Exception {
                List<FootSectionBean>data=new ArrayList<>();
                if(p<=1){
                   transByFresh(data,list);
                }else{
                   transByLoadMore(data,list);
                }
                return data;
            }
        });
    }

    private void transByLoadMore(List<FootSectionBean> data, List<FoodGoodsBean> list) {
       List<FootSectionBean>cacheData=mGoodsFootAdapter.getData();
        FoodGoodsBean foodGoodsBean= ListUtil.safeGetData(list,0);
        if(foodGoodsBean==null||foodGoodsBean.getGoodsList()==null){
            return;
        }
        boolean haveSameDate=false;
        FootSectionBean sectionBean=null;
        for(FootSectionBean item:cacheData){
            String id=item.header;
            if(StringUtil.equals(id,foodGoodsBean.getTitle())){
                haveSameDate=true;
                sectionBean=item;
                break;
            }
        }

        if(haveSameDate){
           list.remove(foodGoodsBean);
           int index= cacheData.indexOf(sectionBean);
           int size=ListUtil.size(sectionBean.getList());
           index=index+size;
           List<GoodsBean> listGoodsList=foodGoodsBean.getGoodsList();
            List<FootSectionBean>temp=new ArrayList<>();
            for(GoodsBean item:listGoodsList){
                sectionBean=new FootSectionBean(item);
                temp.add(sectionBean);
            }
            cacheData.addAll(index+1,temp);
        }
        transByFresh(data,list);
    }

    private void transByFresh(List<FootSectionBean> data, List<FoodGoodsBean> list) {
        for(FoodGoodsBean foodItem:list){
            String title=foodItem.getTitle();
            FootSectionBean sectionBean=new FootSectionBean(true,title);
            sectionBean.setList(foodItem.getGoodsList());
            data.add(sectionBean);
            List<GoodsBean>itemList=foodItem.getGoodsList();
            if(itemList==null){
                continue;
            }
            for(GoodsBean item:itemList){
                sectionBean=new FootSectionBean(item);
                data.add(sectionBean);
            }
        }
    }

    private String getLastId(int p) {
        if(p<=1){
          return null;
        }
        FootSectionBean footSectionBean=ListUtil.getLastData(mGoodsFootAdapter.getData());
        GoodsBean goodsBean=footSectionBean.t;
        if(goodsBean==null){
           return null;
        }
        return goodsBean.getAddtime();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_goods_foot;
    }
}