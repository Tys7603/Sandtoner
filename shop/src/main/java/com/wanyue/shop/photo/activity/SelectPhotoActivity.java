package com.wanyue.shop.photo.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.interfaces.CommonCallback;
import com.wanyue.common.interfaces.ImageResultCallback;
import com.wanyue.common.utils.ProcessImageUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.imnew.bean.ChatChooseImageBean;
import com.wanyue.imnew.util.ImageUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.photo.adapter.SelectPhotoAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectPhotoActivity extends BaseActivity implements View.OnClickListener{
    public static final int SELECT_PHOTO=1;

    private RecyclerView reclyView;
    private TextView tvRightTitle;
    private SelectPhotoAdapter adapter;
    private ImageUtil mImageUtil;
    private ProcessImageUtil processImageUtil;

    private int mMaxLength;

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_photo;
    }
    @Override
    public void init() {
        setTabTitle(getString(R.string.all_photo));
        tvRightTitle = findViewById(R.id.tv_right_title);
        reclyView =findViewById(R.id.reclyView);
        tvRightTitle.setOnClickListener(this);

        mMaxLength=getIntent().getIntExtra(Constants.POSITION,8);
        RxRefreshView.ReclyViewSetting reclyViewSetting= RxRefreshView.ReclyViewSetting.createGridSetting(this,4,3);
        reclyViewSetting.settingRecyclerView(reclyView);

        ArrayList<String> selectList=getIntent().getStringArrayListExtra(Constants.DATA);
        adapter=new SelectPhotoAdapter(null,selectList,this);
        adapter.setMaxLength(mMaxLength);

        adapter.setDataChangeLisnter(new SelectPhotoAdapter.DataChangeLisnter() {
            @Override
            public void change() {
                setPhotoNum();
            }
        });
        setPhotoNum();
        reclyView.setAdapter(adapter);
        mImageUtil=new ImageUtil();
        mImageUtil.getLocalImageList(new CommonCallback<List<ChatChooseImageBean>>() {
            @Override
            public void callback(List<ChatChooseImageBean> list) {
                adapter.setData(list);
            }
        });
        ImageView headerView= new ImageView(this);
        ImgLoader.display(this,R.drawable.icon_take_picture,headerView);
        adapter.setHeaderViewAsFlow(true);
        adapter.addHeaderView(headerView);
        headerView.setOnClickListener(this);
        processImageUtil=new ProcessImageUtil(this);
        processImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {
            }
            @Override
            public void onSuccess(File file) {
                ChatChooseImageBean chatChooseImageBean= new ChatChooseImageBean(file);
                chatChooseImageBean.setChecked(true);
                adapter.addData(chatChooseImageBean);
                compelete();
            }
            @Override
            public void onFailure() {
            }
        });
    }

    private void setPhotoNum() {
        int size= adapter.getSelectImagePathList()==null?0:adapter.getSelectImagePathList().size();
        if(size==0){
            tvRightTitle.setText(WordUtil.getString(R.string.compelete));
            tvRightTitle.setEnabled(false);
        }else{
            tvRightTitle.setText(WordUtil.getString(R.string.photo_compelete,size, mMaxLength));
            tvRightTitle.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.tv_right_title){
            compelete();
        }else{
            openCamera();
        }
    }
    private void openCamera() {
        if(!adapter.isLimit()){
            processImageUtil.getImageByCamera();
        }
    }

    private void compelete() {
        ArrayList<String> list=adapter.getSelectImagePathList();
        Intent intent=getIntent();
        intent.putExtra(Constants.DATA,list);
        setResult(RESULT_OK,intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageUtil.release();
    }

    public static void forward(Activity context, ArrayList<String>list, int position){
        Intent intent = new Intent(context, SelectPhotoActivity.class);
        intent.putExtra(Constants.DATA, list);
        intent.putExtra(Constants.POSITION, position);
        context.startActivityForResult(intent, SelectPhotoActivity.SELECT_PHOTO);
    }

}
