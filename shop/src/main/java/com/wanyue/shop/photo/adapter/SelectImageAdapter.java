package com.wanyue.shop.photo.adapter;

import androidx.annotation.NonNull;
import com.wanyue.common.Constants;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.photo.bean.SelectPhotoBean;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Select image adapter.
 */
public class SelectImageAdapter extends BaseRecyclerAdapter<SelectPhotoBean, BaseReclyViewHolder> {
   private int mMaxLengh=Constants.MAX_PHOTO_LENGTH;
   private int mBtnIcon=R.drawable.icon_up_photo;

    /**
     * Instantiates a new Select image adapter.
     *
     * @param data the data
     */
    public SelectImageAdapter(List<SelectPhotoBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_evaluate_image;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, SelectPhotoBean item) {
        if(item.isBtn()){
           helper.setImageDrawable(R.id.image,ResourceUtil.getDrawable(mBtnIcon,true));
           helper.setVisible(R.id.btn_delete,false);
        }else{
            helper.setImageUrl(item.getPhoto(),R.id.image);
            helper.setVisible(R.id.btn_delete,true);
        }
        helper.setOnChildClickListner(R.id.btn_delete,mOnClickListener);
    }

    /**
     * Check data need button.
     */
    public void checkDataNeedButton(){
        int size=size();
        if(size<mMaxLengh){
            boolean haveBtn=false;
           for(int i=0;i<size;i++){
               if(getItem(i).isBtn()){
                   haveBtn=true;
                   break;
               }
           }
           if(!haveBtn){
              addData(new SelectPhotoBean(true));
           }
        }
    }


    /**
     * Sets max lengh.
     *
     * @param maxLengh the max lengh
     */
    public void setMaxLengh(int maxLengh) {
        mMaxLengh = maxLengh;
    }

    /**
     * Get used image list list.
     *
     * @return the list
     */
    public List<String> getUsedImageList(){
        int size=size();
        if(size==0){
           return null;
        }
        List<String>list=new ArrayList<>();
        for(int i=0;i<size;i++){
            SelectPhotoBean photoBean=getItem(i);
            if(!photoBean.isBtn()){
              list.add(photoBean.getPhoto());
            }
        }
        return list;
    }


    /**
     * Sets btn icon.
     *
     * @param btnIcon the btn icon
     */
    public void setBtnIcon(int btnIcon) {
        mBtnIcon = btnIcon;
    }


    @Override
    public void addData(@NonNull SelectPhotoBean data) {
        if(mData==null){
           mData=new ArrayList<>();
        }
        super.addData(data);

    }
}
