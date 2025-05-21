package com.wanyue.shop.evaluate.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.api.CommonAPI;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.server.RequestFactory;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.upload.ImageUploader;
import com.wanyue.common.utils.BitmapUtil;
import com.wanyue.common.utils.DebugUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ProcessResultUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.R2;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.ShopCartBean;
import com.wanyue.shop.photo.adapter.SelectImageAdapter;
import com.wanyue.shop.evaluate.bean.EvaluateCommitBean;
import com.wanyue.shop.photo.bean.SelectPhotoBean;
import com.wanyue.shop.model.OrderModel;
import com.wanyue.shop.photo.activity.SelectPhotoActivity;
import com.wanyue.shop.photo.util.Util;
import com.wanyue.shop.view.dialog.GalleryDialogFragment;
import com.wanyue.shop.view.widet.RatingStar;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/*发布评价*/
public class PublishEvaluateActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseMutiRecyclerAdapter.OnItemChildClickListener2<SelectPhotoBean> {
    private static final int MAX_PHOTO_LENGTH=8;

    @BindView(R2.id.img_thumb)
    RoundedImageView mImgThumb;
    @BindView(R2.id.tv_title)
    TextView mTvTitle;
    @BindView(R2.id.tv_goods_num)
    TextView mTvGoodsNum;
    @BindView(R2.id.tv_price)
    TextView mTvPrice;
    @BindView(R2.id.star_quality)
    RatingStar mStarQuality;
    @BindView(R2.id.tv_quality_result)
    TextView mTvQualityResult;
    @BindView(R2.id.star_service)
    RatingStar mStarService;
    @BindView(R2.id.tv_service_result)
    TextView mTvServiceResult;

    @BindView(R2.id.star_wuliu)
    RatingStar mStarWuliu;
    @BindView(R2.id.tv_wuliu_result)
    TextView mTvWuliuResult;


    @BindView(R2.id.tv_comment)
    EditText mTvComment;
    @BindView(R2.id.reclyView)
    RecyclerView mReclyView;
    private EvaluateCommitBean mEvaluateCommitBean;
    private SelectImageAdapter mSelectImageAdapter;
    private ProcessResultUtil mProcessResultUtil;
    private Disposable mDisposable;
    private ImageUploader mImageUploader;
    private String mOrderId;

    @Override
    public void init() {
        setTabTitle(R.string.evaluate_goods);
        mEvaluateCommitBean = new EvaluateCommitBean();
        String json = getIntent().getStringExtra(Constants.DATA);
        mOrderId=getIntent().getStringExtra(Constants.ORDER_ID);
        ShopCartBean shopCartBean = JSON.parseObject(json, ShopCartBean.class);
        mEvaluateCommitBean.setUnique(shopCartBean.getUnique());
        setGoodsInfo(shopCartBean);
        int size = DpUtil.dp2px(17);
        Bitmap starNormal = BitmapUtil.thumbImageWithMatrix(getResources(), R.drawable.icon_evaluate_default, size, size);
        Bitmap starFocus = BitmapUtil.thumbImageWithMatrix(getResources(), R.drawable.icon_evaluate_select, size, size);

        mStarQuality.setNormalImg(starNormal);
        mStarQuality.setFocusImg(starFocus);
        mStarQuality.setEnableSelect(true);

        mStarService.setNormalImg(starNormal);
        mStarService.setFocusImg(starFocus);
        mStarService.setEnableSelect(true);

        mStarWuliu.setNormalImg(starNormal);
        mStarWuliu.setFocusImg(starFocus);
        mStarWuliu.setEnableSelect(true);

        mStarQuality.setOnRatioListner(new RatingStar.OnRatioListner() {
            @Override
            public void onCheck(int poisition) {
                if (mTvQualityResult != null) {
                    String tip = getCommentString(poisition);
                    mTvQualityResult.setText(tip);
                }
                if (mEvaluateCommitBean != null) {
                    mEvaluateCommitBean.setProduct_score(poisition + 1);
                }
            }
        });
        mStarService.setOnRatioListner(new RatingStar.OnRatioListner() {
            @Override
            public void onCheck(int poisition) {
                String tip = getCommentString(poisition);
                mTvServiceResult.setText(tip);
                if (mEvaluateCommitBean != null) {
                    mEvaluateCommitBean.setService_score(poisition + 1);
                }
            }
        });

        mStarWuliu.setOnRatioListner(new RatingStar.OnRatioListner() {
            @Override
            public void onCheck(int poisition) {
                String tip = getCommentString(poisition);
                mTvWuliuResult.setText(tip);
                if (mEvaluateCommitBean != null) {
                    mEvaluateCommitBean.setExpress_score(poisition + 1);
                }
            }
        });

        mProcessResultUtil = new ProcessResultUtil(this);
        List<SelectPhotoBean> list = ListUtil.asList(new SelectPhotoBean(true));
        mSelectImageAdapter = new SelectImageAdapter(list);
        mSelectImageAdapter.setOnItemChildClickListener2(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mReclyView.setAdapter(mSelectImageAdapter);
        mReclyView.setLayoutManager(gridLayoutManager);
        mSelectImageAdapter.setOnItemClickListener(this);

    }

    @OnTextChanged(value = R2.id.tv_comment, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchCommentTextChange(CharSequence sequence, int start, int before, int count){
        String codeString=sequence.toString();
        if(mEvaluateCommitBean!=null){
           mEvaluateCommitBean.setComment(codeString);
        }
    }

    private void setGoodsInfo(ShopCartBean shopCartBean) {
        GoodsBean goodsBean = shopCartBean.getProductInfo();
        if (goodsBean != null) {
            ImgLoader.display(this, goodsBean.getThumb(), mImgThumb);
            mTvTitle.setText(goodsBean.getName());
            mTvPrice.setText(goodsBean.getUnitPrice());

        }
        mTvGoodsNum.setText("x" + shopCartBean.getCartNum());
    }

    private String getCommentString(int poisition) {
        switch (poisition) {
            case 0:
                return getString(R.string.order_comment_tip_0);
            case 1:
                return getString(R.string.order_comment_tip_1);
            case 2:
                return getString(R.string.order_comment_tip_2);
            case 3:
                return getString(R.string.order_comment_tip_3);
            case 4:
                return getString(R.string.order_comment_tip_4);
            default:
                return "";
        }
    }

    /*评论*/
    @OnClick(R2.id.btn_commit)
    public void commit(){
        if(mEvaluateCommitBean==null||TextUtils.isEmpty(mEvaluateCommitBean.getUnique())){
            DebugUtil.sendException("mEvaluateCommitBean=="+mEvaluateCommitBean+"&&mEvaluateCommitBean.getUnique()="+mEvaluateCommitBean.getUnique());
            return;
        }
        if(mEvaluateCommitBean.getProduct_score()<=0){
            ToastUtil.show("Please rate the product");
            return;
        }
        if(mEvaluateCommitBean.getService_score()<=0){
            ToastUtil.show("Please rate the service");
            return;
        }

        if(mEvaluateCommitBean.getExpress_score()<=0){
            ToastUtil.show("Please rate logistics");
            return;
        }

        String comment=mEvaluateCommitBean.getComment();
        if(TextUtils.isEmpty(comment)){
            ToastUtil.show("Please fill in the evaluation content");
            return;
        }
        List<String> commitList= mSelectImageAdapter.getUsedImageList();
        if(ListUtil.haveData(commitList)){ //有图片先上传图片
            upLoadImage(commitList);
        }else{  //没有图片直接评价
            getRequest().subscribe(new DialogObserver<Boolean>(this) {
                @Override
                public void onNextTo(Boolean aBoolean) {
                    if(aBoolean){
                        ToastUtil.show("Thank you for your review!");
                        OrderModel.sendOrderChangeEvent(mOrderId);
                        finish();
                    }
                }
            });
        }
    }

    /*评价观察器*/
    private Observable<Boolean> getRequest() {
        return ShopAPI.orderComment(mEvaluateCommitBean);
    }

    private void upLoadImage(List<String> commitList) {
        if(mImageUploader==null){
            mImageUploader=new ImageUploader(this);
        }
        final Dialog dialog=DialogUitl.loadingDialog(this);
        dialog.show();
        Observable<String>observable= mImageUploader.uploadFileArraycompress(commitList);
        mDisposable=mImageUploader.collect(observable).subscribe(new Consumer<StringBuilder>() {
            @Override
            public void accept(StringBuilder stringBuilder) throws Exception {
                DialogUitl.dismissDialog(dialog);
                if(stringBuilder.length()>0){
                   stringBuilder.deleteCharAt(stringBuilder.length()-1);
                }
                if(mEvaluateCommitBean!=null){
                   mEvaluateCommitBean.setPics(stringBuilder.toString());
                }
                getRequest().subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        DialogUitl.dismissDialog(dialog);
                    }
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        DialogUitl.dismissDialog(dialog);
                    }
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(aBoolean){
                            ToastUtil.show("Thank you for your review!");
                            finish();
                            OrderModel.sendOrderChangeEvent(mOrderId);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_publish_evaluate_goods;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mSelectImageAdapter == null) {
            return;
        }
        SelectPhotoBean photoBean = mSelectImageAdapter.getItem(position);
        if (photoBean == null) {
            return;
        }
        if (photoBean.isBtn()) {
            goToSelectPhoto();
        } else {
            openPhotoDialog(position);
        }
    }

    private void openPhotoDialog(int position) {
        List<String> list = Util.tranformData(mSelectImageAdapter.getArray());
        GalleryDialogFragment galleryDialogFragment = new GalleryDialogFragment();
        galleryDialogFragment.setGalleryViewProxy(list, position, getViewProxyMannger());
        galleryDialogFragment.show(getSupportFragmentManager());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDisposable!=null&&!mDisposable.isDisposed()){
           mDisposable.dispose();
        }
        if(mImageUploader!=null){
           mImageUploader.release();
           mImageUploader=null;
        }
        RequestFactory.getRequestManager().cancle(CommonAPI.UP_IMAGE);
        RequestFactory.getRequestManager().cancle(ShopAPI.ORDER_COMMENT);
    }

    @Override
    public void onItemClick(int position, SelectPhotoBean selectPhotoBean, View view) {
        if (mSelectImageAdapter != null) {
            mSelectImageAdapter.remove(position);
            mSelectImageAdapter.checkDataNeedButton();
        }
    }

    /*先验证权限*/
    private void judgePermisson(Runnable runnable) {
        String[] permissonArray = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO};
        mProcessResultUtil.requestPermissions(permissonArray
                , runnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != SelectPhotoActivity.SELECT_PHOTO) {
            return;
        }
        List<String> photoPathArray = data.getStringArrayListExtra(Constants.DATA);
        List<SelectPhotoBean> list = Util.formatData(photoPathArray,MAX_PHOTO_LENGTH);
        if (mSelectImageAdapter != null) {
            mSelectImageAdapter.setData(list);
        }
        if(mEvaluateCommitBean!=null){
           mEvaluateCommitBean.setPhotoList(list);
        }
    }

    @Override
    public void onBackPressed() {
        if(mEvaluateCommitBean!=null&&mEvaluateCommitBean.isEdit()){
            openTipDialog();
        }else{
            finish();
        }
    }

    private void openTipDialog(){
        Dialog dialog= new DialogUitl.Builder(this)
                .setTitle("")
                .setContent("The review content will be cleared after you leave. Do you still want to leave?")
                .setCancelable(false)
                .setBackgroundDimEnabled(true)
                .setConfrimString("Leave")
                .setCancelString("Continue to rate")
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        finish();
                    }
                })
                .build();
        dialog.setCancelable(false);
        dialog.show();
    }


    public static void forward(Context context, ShopCartBean shopCartBean,String orderId) {
        if (shopCartBean == null) {
            DebugUtil.sendException("ShopCartBean cannot be null");
            return;
        }
        Intent intent = new Intent(context, PublishEvaluateActivity.class);
        intent.putExtra(Constants.DATA, JSON.toJSONString(shopCartBean));
        intent.putExtra(Constants.ORDER_ID,orderId);
        context.startActivity(intent);
    }


    /*选择照片*/
    private void goToSelectPhoto() {
        judgePermisson(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> list = null;
                if (mSelectImageAdapter != null && mSelectImageAdapter.size() > 0) {
                    list = Util.tranformData(mSelectImageAdapter.getArray());
                }
                SelectPhotoActivity.forward(PublishEvaluateActivity.this,list,MAX_PHOTO_LENGTH);
            }
        });
    }



}
