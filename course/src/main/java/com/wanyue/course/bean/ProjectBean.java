package com.wanyue.course.bean;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import com.wanyue.course.busniess.CourseConstants;
import com.wanyue.course.busniess.UIFactory;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.course.R;

import java.util.List;

/*
*  @TODO 因为有多种类型的project(课程,直播,内容),避免相互之间的属性污染,进行了抽象
* @TODO 但是注意json序列化解析的时候,会统一处理成project所以一定要进行相关解析转型,
* @TODO com.wanyue.inside.busniess.data.ProjectDataHelper 在这个类里面进行了分类型解析,不可避免的牺牲一部分性能
* @TODO 但是结构上更加清晰,不会出现在内容里面会含有直播的相关字段,有利于拓展
 */


public class ProjectBean implements MultiItemEntity, Parcelable {
    private String id;

    @SerializedName("sort")
    @JSONField(name="sort")
    private int projectType;

    @SerializedName("payval")
    @JSONField(name="payval")
    protected String price;

    @SerializedName("name")
    @JSONField(name="name")
    private String title;

    private String thumb;

    @SerializedName("views")
    @JSONField(name="views")
    private int studyCount;

    @SerializedName("des")
    @JSONField(name="des")
    private String introduce;

    @SerializedName("ifbuy")
    @JSONField(name="ifbuy")
    private int isBuy2;//这两个都是后台返回的，当其中一个返回当是1就好

    @SerializedName("isbuy")
    @JSONField(name="isbuy")
    private int isBuy;//这个是实际判断

    protected int paytype;

    @SerializedName("userinfo")
    @JSONField(name="userinfo")
    protected LecturerBean lecturerBean;

    @SerializedName("avatar")
    @JSONField(name="avatar")
    protected String lecturerAvatar;

    @SerializedName("uid")
    @JSONField(name="uid")
    protected String lecturerUid;

    @SerializedName("user_nickname")
    @JSONField(name="user_nickname")
    protected String lecturerNickName;

    private float star;

    @SerializedName("lessionid")
    @JSONField(name="lessionid")
    protected String lessionId;
    protected CharSequence handlePrice;

    @SerializedName("ismaterial")
    @JSONField(name="ismaterial")
    private int isMaterial; //是否有教材
    private int ispack;
    private int iscart;

    private List<LecturerBean> tutor;
    private int isvip;
    @SerializedName("isshowvip")
    @JSONField(name="isshowvip")
    private int isShowVip;//是否显示会员

    @SerializedName("money_vip")
    @JSONField(name="money_vip")
    private String vipMoney;//会员价

    //实际支付价格
    @SerializedName("money")
    @JSONField(name="money")
    protected String payMoney;

    //折扣
    private String discount;
    //是否显示实际支付价格
    protected boolean showActualPrice;



/***************秒杀*********************/
    private int isseckill;//是否为秒杀
    @SerializedName("money_seckill")
    @JSONField(name="money_seckill")
    private String seckillPrice;//秒杀价
    @SerializedName("seckill_nums")
    @JSONField(name="seckill_nums")
    private int seckillNums;//秒杀数量
    @SerializedName("seckill_wait")
    @JSONField(name="seckill_wait")
    private int seckillTime;//等待剩余时长(S)
    @SerializedName("seckill_ing")
    @JSONField(name="seckill_ing")
    private int seckillIngTime;//活动剩余时长(S)

    /***************拼团*********************/

    @SerializedName("ispink")
    @JSONField(name="ispink")
    private int isGroupWork;//是否是拼团

    @SerializedName("pink_status")
    @JSONField(name="pink_status")
    private int isGroupWorkStatus;//是否参加了拼团

    @SerializedName("money_pink")
    @JSONField(name="money_pink")
    private String moneyPink;//拼团最低价



    @SerializedName("pink_nums")
    @JSONField(name="pink_nums")
    private int pinkNum; //拼团人数

    private String pinkid;

    /***************优惠券*********************/
    @SerializedName("coupon")
    @JSONField(name="coupon")
    private String[] couponTitle;

    //按钮状态
    @SerializedName("btn_status")
    @JSONField(name="btn_status")
    private int btnStatus;

    private int isfission;

    //原始数据
    private JSONObject extra;
    private int isyouhui;

    private int iscollect;
    private int noteid;

    @SerializedName("info_url")
    @JSONField(name="info_url")
    private String  infoUrl;


    public ProjectBean(){

    }

    protected ProjectBean(Parcel in) {
        id = in.readString();
        projectType = in.readInt();
        price = in.readString();
        title = in.readString();
        thumb = in.readString();
        studyCount = in.readInt();
        introduce = in.readString();
        isBuy = in.readInt();
        paytype = in.readInt();
        lecturerBean = in.readParcelable(LecturerBean.class.getClassLoader());
        lecturerAvatar = in.readString();
        lecturerUid = in.readString();
        lecturerNickName = in.readString();
        lessionId= in.readString();
        isMaterial=in.readInt();
        iscart=in.readInt();
        tutor = in.createTypedArrayList(LecturerBean.CREATOR);
        isvip=in.readInt();
        isShowVip=in.readInt();
        vipMoney= in.readString();
        discount= in.readString();
        payMoney=in.readString();
        isseckill=in.readInt();
        btnStatus=in.readInt();

        seckillPrice= in.readString();
        seckillNums=in.readInt();
        seckillTime=in.readInt();
        seckillIngTime=in.readInt();
        isGroupWork=in.readInt();
        isfission=in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(projectType);
        dest.writeString(price);
        dest.writeString(title);
        dest.writeString(thumb);
        dest.writeInt(studyCount);
        dest.writeString(introduce);
        dest.writeInt(isBuy);
        dest.writeInt(paytype);
        dest.writeParcelable(lecturerBean, flags);
        dest.writeString(lecturerAvatar);
        dest.writeString(lecturerUid);
        dest.writeString(lecturerNickName);
        dest.writeString(lessionId);
        dest.writeInt(isMaterial);
        dest.writeInt(iscart);
        dest.writeTypedList(tutor);
        dest.writeInt(isvip);
        dest.writeInt(isShowVip);
        dest.writeString(vipMoney);
        dest.writeString(discount);
        dest.writeString(payMoney);
        dest.writeInt(isseckill);
        dest.writeInt(btnStatus);

        dest.writeString(seckillPrice);
        dest.writeInt(seckillNums);
        dest.writeInt(seckillTime);
        dest.writeInt(seckillIngTime);

        dest.writeInt(isGroupWork);
        dest.writeInt(isfission);

    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProjectBean> CREATOR = new Creator<ProjectBean>() {
        @Override
        public ProjectBean createFromParcel(Parcel in) {
            return new ProjectBean(in);
        }

        @Override
        public ProjectBean[] newArray(int size) {
            return new ProjectBean[size];
        }
    };

    public int getProjectType() {
        return projectType;
    }


    public void setProjectType(int projectType) {
        this.projectType = projectType;
    }

    public String getPrice() {
        return price;
    }


    public double getNumberPrice() {
        try {
            return Double.parseDouble(payMoney);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    
    /*获取处理后的价格标签*/
    public CharSequence getHandlePrice(){
        if(!TextUtils.isEmpty(handlePrice)){
            return handlePrice;
        }
        if(paytype== CourseConstants.TYPE_PAY_CHARGE&&ifBuy()){
            handlePrice= WordUtil.getString(R.string.buyed);
        }
        else if(paytype==CourseConstants.TYPE_PAY_CHARGE&&!ifBuy()){
            String money=null;
            if(showActualPrice){
                money=payMoney;
            }else{
                money=price;
            }
           handlePrice= UIFactory.createPrice(money);
        }else{
            handlePrice=getPrice();
        }
        return handlePrice;
    }




    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
    public LecturerBean getLecturerBean() {
        return lecturerBean;
    }
    public void setLecturerBean(LecturerBean lecturerBean) {
        this.lecturerBean = lecturerBean;
    }

    public int getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(int studyCount) {
        this.studyCount = studyCount;
    }

    @Override
    public int getItemType() {
        return projectType;
    }


    public List<LecturerBean> getTutor() {
        return tutor;
    }

    public void setTutor(List<LecturerBean> tutor) {
        this.tutor = tutor;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }


    public String getLecturerAvatar() {
        if(lecturerAvatar==null&&lecturerBean!=null){
            return lecturerBean.getAvatar();
        }
        return lecturerAvatar;
    }

    public void setLecturerAvatar(String lecturerAvatar) {
        this.lecturerAvatar = lecturerAvatar;
    }

    public String getLecturerUid() {
        return lecturerUid;
    }

    public void setLecturerUid(String lecturerUid) {
        this.lecturerUid = lecturerUid;
    }

    public String getLecturerNickName() {
        if(lecturerNickName==null&&lecturerBean!=null){
            lecturerNickName=lecturerBean.getUserNiceName();
        }
        return lecturerNickName;
    }

    public void setLecturerNickName(String lecturerNickName) {
        this.lecturerNickName = lecturerNickName;
    }

    public float getStar() {
        return star;
    }
    public void setStar(float star) {
        this.star = star;
    }

    public int getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(int isBuy) {
        this.isBuy = isBuy;
    }

    public boolean ifBuy() {
        if(paytype== CourseConstants.TYPE_PAY_FREE){
           return true;
        }
       return isBuy==1||isBuy2==1;
    }


    public String getLessionId() {
        if(TextUtils.isEmpty(lessionId)){
            lessionId="0";
        }
        return lessionId;
    }

    public void setLessionId(String sessionId) {
        this.lessionId = sessionId;
    }

    public int getIsMaterial() {
        return isMaterial;
    }

    public void setIsMaterial(int isMaterial) {
        this.isMaterial = isMaterial;
    }

    public int getIscart() {
        return iscart;
    }

    public void setIscart(int iscart) {
        this.iscart = iscart;
    }

    public ProjectBean copyBaseInfo(ProjectBean projectBean){
        id=projectBean.id;
        thumb=projectBean.thumb;
        price=projectBean.price;
        title=projectBean.title;
        studyCount=projectBean.studyCount;
        projectType=projectBean.projectType;
        isBuy=projectBean.isBuy;
        lecturerAvatar=projectBean.lecturerAvatar;
        lecturerBean=projectBean.lecturerBean;
        star=projectBean.star;
        introduce=projectBean.introduce;

        return this;
    }

    public int getIsBuy2() {
        return isBuy2;
    }
    public void setIsBuy2(int isBuy2) {
        this.isBuy2 = isBuy2;
    }

    public int getIsvip() {
        return isvip;
    }
    public void setIsvip(int isvip) {
        this.isvip = isvip;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null && obj instanceof ProjectBean ){
            ProjectBean projectBean= (ProjectBean) obj;
            return StringUtil.equals(projectBean.id,id);
        }
        return super.equals(obj);
    }
    public int getIspack() {
        return ispack;
    }

    public void setIspack(int ispack) {
        this.ispack = ispack;
    }

    public int getIsShowVip() {
        return isShowVip;
    }
    public void setIsShowVip(int isShowVip) {
        this.isShowVip = isShowVip;
    }
    public String getVipMoney() {
        return vipMoney;
    }
    public void setVipMoney(String vipMoney) {
        this.vipMoney = vipMoney;
    }
    public String getDiscount() {
        return discount;
    }
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPayMoney() {
        return payMoney;
    }
    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public void setShowActualPrice(boolean showActualPrice) {
        this.showActualPrice = showActualPrice;
    }
    public int getIsseckill() {
        return isseckill;
    }
    public void setIsseckill(int isseckill) {
        this.isseckill = isseckill;
    }

    public int getBtnStatus() {
        return btnStatus;
    }
    public void setBtnStatus(int btnStatus) {
        this.btnStatus = btnStatus;
    }

    public void setHandlePrice(String handlePrice) {
        this.handlePrice = handlePrice;
    }

    public String getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(String seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public int getSeckillNums() {
        return seckillNums;
    }

    public void setSeckillNums(int seckillNums) {
        this.seckillNums = seckillNums;
    }

    public int getSeckillTime() {
        return seckillTime;
    }
    public void setSeckillTime(int seckillTime) {
        this.seckillTime = seckillTime;
    }
    public int getSeckillIngTime() {
        return seckillIngTime;
    }
    public void setSeckillIngTime(int seckillIngTime) {
        this.seckillIngTime = seckillIngTime;
    }


    public String[] getCouponTitle() {
        return couponTitle;
    }
    public void setCouponTitle(String[] couponTitle) {
        this.couponTitle = couponTitle;
    }



    public int getIsGroupWork() {
        return isGroupWork;
    }
    public void setIsGroupWork(int isGroupWork) {
        this.isGroupWork = isGroupWork;
    }

    public String getMoneyPink() {
        return moneyPink;
    }

    public void setMoneyPink(String moneyPink) {
        this.moneyPink = moneyPink;
    }

    public JSONObject getExtra() {
        return extra;
    }
    public void setExtra(JSONObject extra) {
        this.extra = extra;
    }


    public int getIsGroupWorkStatus() {
        return isGroupWorkStatus;
    }

    public void setIsGroupWorkStatus(int isGroupWorkStatus) {
        this.isGroupWorkStatus = isGroupWorkStatus;
    }

    public int getPinkNum() {
        return pinkNum;
    }

    public void setPinkNum(int pinkNum) {
        this.pinkNum = pinkNum;
    }

    public String getPinkid() {
        return pinkid;
    }

    public void setPinkid(String pinkid) {
        this.pinkid = pinkid;
    }

    public int getIsfission() {
        return isfission;
    }

    public void setIsfission(int isfission) {
        this.isfission = isfission;
    }


    public int getNoteid() {
        return noteid;
    }

    public void setNoteid(int noteid) {
        this.noteid = noteid;
    }

    public int getIsyouhui() {
        return isyouhui;
    }

    public void setIsyouhui(int isyouhui) {
        this.isyouhui = isyouhui;
    }

    public int getIscollect() {
        return iscollect;
    }

    public void setIscollect(int iscollect) {
        this.iscollect = iscollect;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }
}
