package com.wanyue.course.evaluate.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.server.observer.LockClickObserver;
import com.wanyue.common.utils.BitmapUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.course.R;
import com.wanyue.course.R2;
import com.wanyue.course.api.CourseAPI;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.evaluate.EvaluateCommitBean;
import com.wanyue.course.widet.RatingStar;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PublishEvaluateActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView mTvTitle;
    @BindView(R2.id.img_avator)
    RoundedImageView mImgAvator;
    @BindView(R2.id.tv_name)
    TextView mTvName;
    @BindView(R2.id.rating_star)
    RatingStar mRatingStar;
    @BindView(R2.id.et_evaluate)
    EditText mEtEvaluate;
    @BindView(R2.id.btn_commit)
    Button mBtnCommit;
    @BindView(R2.id.tv_evaluate_result)
    TextView mTvEvaluateResult;

    private ProjectBean mProjectBean;
    private EvaluateCommitBean mEvaluateCommitBean;

    @Override
    public void init() {
        setTabTitle(getString(R.string.evaluate));
        mProjectBean=getIntent().getParcelableExtra(Constants.DATA);
        if(mProjectBean==null){
            finish();
            return;
        }

        int size = DpUtil.dp2px(17);
        Bitmap starNormal = BitmapUtil.thumbImageWithMatrix(getResources(), R.drawable.icon_star_unselect, size, size);
        Bitmap starFocus = BitmapUtil.thumbImageWithMatrix(getResources(), R.drawable.icon_star_select, size, size);

        mRatingStar.setFocusImg(starFocus);
        mRatingStar.setNormalImg(starNormal);
        mRatingStar.setEnableSelect(true);
        mRatingStar.setOnRatioListner(new RatingStar.OnRatioListner() {
            @Override
            public void onCheck(int poisition) {
                mTvEvaluateResult.setText(getEvaluteString(poisition));
                mEvaluateCommitBean.setPosition(poisition);
            }
        });
        mEvaluateCommitBean=new EvaluateCommitBean();
        pushDataToProject();
    }


    private int getEvaluteString(int poisition) {
        switch (poisition){
            case 0:
                return R.string.order_comment_tip_5;
            case 1:
                return R.string.order_comment_tip_6;
            case 2:
                return R.string.order_comment_tip_2;
            case 3:
                return R.string.order_comment_tip_7;
            case 4:
               return R.string.order_comment_tip_8;
               default:
                  return R.string.empty_tag;
        }
    }

    /*监听评价文本变化*/
    @OnTextChanged(value = R2.id.et_evaluate, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchEvaluteTextChange(CharSequence sequence, int start, int before, int count) {
            if(mEvaluateCommitBean!=null){
               mEvaluateCommitBean.setContent(sequence.toString());
            }
    }

    private void pushDataToProject() {
        if (mProjectBean == null) {
            return;
        }
        mTvTitle.setText(mProjectBean.getTitle());
        UserBean lecturerBean = mProjectBean.getLecturerBean();
        if (lecturerBean != null) {
            mTvName.setText(lecturerBean.getUserNiceName());
            ImgLoader.display(this,lecturerBean.getAvatar(),mImgAvator);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_publish_evaluate;
    }

    /*发布评价*/
    @OnClick(R2.id.btn_commit)
    public void pubEvaluate(View view) {
        if(!mEvaluateCommitBean.isSelectStar()){
            ToastUtil.show(getString(R.string.please_select_star));
            return;
        }else if(!mEvaluateCommitBean.isSelectContent()){
            ToastUtil.show(getString(R.string.please_input_content));
            return;
        }
        


        CourseAPI.addComment(
                mProjectBean.getId(),
                mEvaluateCommitBean.getPosition()+1,
                mEvaluateCommitBean.getContent()).compose(this.<Boolean>bindUntilOnDestoryEvent())
                .subscribe(new LockClickObserver<Boolean>(view) {
                    @Override
                    public void onSucc(Boolean aBoolean) {
                        if(aBoolean){
                            finish();
                        }
                    }
                });
    }


    public static void forward(Context context,ProjectBean projectBean){
        Intent intent=new Intent(context,PublishEvaluateActivity.class);
        intent.putExtra(Constants.DATA,projectBean);
        context.startActivity(intent);
    }

    @Override
    public void clickBack() {
        if(mEvaluateCommitBean!=null&&mEvaluateCommitBean.isEdit()){
            openTipDialog();
        }else{
            finish();
        }
    }

    private void openTipDialog(){
        Dialog dialog= new DialogUitl.Builder(this)
                .setTitle("")
                .setContent("离开后评价内容将清空，仍要离开吗？")
                .setCancelable(false)
                .setBackgroundDimEnabled(true)
                .setLayoutId(R.layout.dialog_simple_evaluate)
                .setConfrimString("离开")
                .setCancelString("继续评价")
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

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}
