package com.wanyue.live.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.AbsActivity;
import com.wanyue.common.adapter.RefreshAdapter;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.custom.CommonRefreshView;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.interfaces.OnItemClickListener;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.live.R;
import com.wanyue.live.adapter.LiveShutUpAdapter;
import com.wanyue.live.bean.LiveShutUpBean;
import com.wanyue.live.http.LiveHttpConsts;
import com.wanyue.live.http.LiveHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 2019/4/27.
 * 直播间禁言用户列表
 */
public class LiveShutUpActivity extends AbsActivity implements OnItemClickListener<LiveShutUpBean> {

    /**
     * Forward.
     *
     * @param context the context
     * @param liveUid the live uid
     */
    public static void forward(Context context, String liveUid) {
        Intent intent = new Intent(context, LiveShutUpActivity.class);
        intent.putExtra(Constants.LIVE_UID, liveUid);
        context.startActivity(intent);
    }

    private CommonRefreshView mRefreshView;
    private LiveShutUpAdapter mAdapter;
    private String mLiveUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shut_up;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.live_user_shut_up_list));
        mLiveUid = getIntent().getStringExtra(Constants.LIVE_UID);
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_shut_up);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveShutUpBean>() {
            @Override
            public RefreshAdapter<LiveShutUpBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LiveShutUpAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveShutUpActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                LiveHttpUtil.getLiveShutUpList(mLiveUid, p, callback);
            }

            @Override
            public List<LiveShutUpBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveShutUpBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveShutUpBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveShutUpBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    public void onItemClick(final LiveShutUpBean bean, int position) {
        LiveHttpUtil.liveCancelShutUp(mLiveUid, bean.getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (isSuccess(code)) {
                    if (mAdapter != null) {
                        mAdapter.removeItem(bean.getUid());
                    }
                }
                ToastUtil.show(msg);
            }

            @Override
            public void onError(int code, String msg) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_SHUT_UP_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.LIVE_CANCEL_SHUT_UP);
        super.onDestroy();
    }
}
