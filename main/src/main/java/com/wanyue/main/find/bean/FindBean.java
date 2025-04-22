package com.wanyue.main.find.bean;

import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.business.list.ListBean;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.SpannableStringUtils;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.main.R;
import com.wanyue.shop.bean.StoreBean;

import java.util.List;

public class FindBean implements MultiItemEntity, ListBean {

    public static final int TYPE_NEW=2; //上新
    public static final int TYPE_RECOMMEND=1;//种草
    public static final int TYPE_LIVE=3;//直播

    protected int type;
    private String id;


    @JSONField(name = "likes")
    @SerializedName( "likes")
    private int zanNum;


    @JSONField(name = "islike")
    @SerializedName( "islike")
    private int isZan;


    @JSONField(name = "views")
    @SerializedName( "views")
    private int lookNum;


    protected String title;


    @JSONField(name = "thumbs")
    @SerializedName( "thumbs")
    private List<String>photoList;

    @JSONField(name = "goods")
    @SerializedName( "goods")
    protected List<GoodsBean>goodsList;
    private CharSequence titleTip;


    @JSONField(name = "add_time")
    @SerializedName( "add_time")
    private String addTime;


    @JSONField(name = "mer_id")
    @SerializedName( "mer_id")
    protected String storeId;


    @JSONField(name = "nickname")
    @SerializedName( "nickname")
    protected String storeName;

    @JSONField(name = "avatar")
    @SerializedName( "avatar")
    protected String storeAvatar;

    @JSONField(name = "isattent")
    @SerializedName( "isattent")
    private int  isattent;


    private String content;

    private int shoptype;



    @Override
    public int getItemType() {
        return type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }



    public void setId(String id) {
        this.id = id;
    }

    public int getZanNum() {
        return zanNum;
    }


    public void setZanNum(int zanNum) {
        this.zanNum = zanNum;
    }

    public int getIsZan() {
        return isZan;
    }

    public void setIsZan(int isZan) {
        this.isZan = isZan;
    }

    public int getLookNum() {
        return lookNum;
    }

    public void setLookNum(int lookNum) {
        this.lookNum = lookNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public List<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
    }

    public List<GoodsBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsBean> goodsList) {
        this.goodsList = goodsList;
    }



    private Drawable getTypeDrawable() {
        switch (type) {
            case FindBean.TYPE_NEW:
                return ResourceUtil.getDrawable(R.drawable.icon_find_shangxin, true);
            case FindBean.TYPE_RECOMMEND:
                return ResourceUtil.getDrawable(R.drawable.icon_find_zhongcao, true);
            case FindBean.TYPE_LIVE:
                return ResourceUtil.getDrawable(R.drawable.icon_find_live, true);
            default:
        }
        return null;
    }


    public CharSequence getContentTip() {

        if(titleTip==null){
            Drawable drawable = getTypeDrawable();
            drawable.setBounds(0, 0, DpUtil.dp2px(28), DpUtil.dp2px(15));
            SpannableStringUtils.Builder builder= SpannableStringUtils.getBuilder(" ").setDrawable(drawable).
                    append("  "+title);
            titleTip=builder.create();
        }
        return titleTip;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }





    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAvatar() {
        return storeAvatar;
    }

    public void setStoreAvatar(String storeAvatar) {
        this.storeAvatar = storeAvatar;
    }

    public int getIsattent() {
        return isattent;
    }

    public void setIsattent(int isattent) {
        this.isattent = isattent;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getShoptype() {
        return shoptype;
    }

    public void setShoptype(int shoptype) {
        this.shoptype = shoptype;
    }
}
