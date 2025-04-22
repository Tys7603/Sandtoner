package com.wanyue.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.main.R;
import com.wanyue.main.find.api.FindAPI;
import com.wanyue.main.find.bean.FindBean;
import com.wanyue.main.find.view.proxy.NormalFindListProxy;
import java.util.List;
import io.reactivex.Observable;

@Route(path = RouteUtil.PATH_GOODS_LIVE)
public class GoodsConnectActivity extends BaseActivity {
    private ViewGroup mContainer;

    @Override
    public void init() {
        setTabTitle("商品关联直播");
        final String id=getIntent().getStringExtra(Constants.KEY_ID);
        mContainer = findViewById(R.id.container);
        NormalFindListProxy findListProxy=new NormalFindListProxy(){
            @Override
            public Observable<List<FindBean>> getData(int p) {
                return FindAPI.getLiveAboutGoods(p,id).compose(this.<List<FindBean>>bindToLifecycle());
            }
        };
        findListProxy.setTitle("暂无关联直播");
        findListProxy.setIconId(R.mipmap.icon_default_no_data);
        findListProxy.setLevel(1);
        getViewProxyMannger().addViewProxy(mContainer,findListProxy,findListProxy.getDefaultTag());
        findListProxy.initData();
    }

    public static void forward(Context context, String id){
        Intent intent=new Intent(context,GoodsConnectActivity.class);
        intent.putExtra(Constants.KEY_ID,id);
        context.startActivity(intent);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_goods_connect;
    }
}