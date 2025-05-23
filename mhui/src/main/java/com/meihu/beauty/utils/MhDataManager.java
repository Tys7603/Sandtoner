package com.meihu.beauty.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.meihu.beauty.bean.MeiYanDataBean;
import com.meihu.beauty.bean.MeiYanValueBean;
import com.meihu.beauty.bean.TieZhiBean;
import com.meihu.beauty.interfaces.CommonCallback;
import com.meihu.beauty.interfaces.IBeautyEffectListener;
import com.meihu.beauty.views.BeautyViewHolder;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.constant.ResourceUrl;
import com.meihu.beautylibrary.interfaces.OnFaceDetectListener;
import com.meihu.beautylibrary.interfaces.OnStickerActionListener;
import com.meihu.beautylibrary.manager.MHBeautyManager;

import java.util.List;

/**
 * The type Mh data manager.
 */
public class MhDataManager {

    private final  String  TAG = MhDataManager.class.getName();
    private static MhDataManager sInstance;
    private MHBeautyManager mMhManager;
    private MeiYanValueBean mInputValue;//美狐sdk当前使用的数值
    private MeiYanValueBean mUseValue;//当前使用的是美颜美型还是一键美颜
    private MeiYanValueBean mMeiYanValue;//用户设置的美颜，美型数值
    private MeiYanValueBean mOneKeyValue;//用户设置的一键美颜数值
    private IBeautyEffectListener mMeiYanChangedListener;
    private Context mContext;
    private SPUtil mSPUtil;
    private String mActionStickerName = "";

    private boolean isShowCapture = false;
    private BeautyViewHolder mBeautyViewHolder;
    private MeiYanDataBean mMeiYanDataBean;

    private int mFilterId;
    private int mHahaName;
    private boolean mHahaTexiao;
    private String mTieZhiName;
    private int  mTieZhiAction;
    private boolean  mTieZhiShow;
    private boolean  mTieZhiIsAction;
    private String  mTieZhiKey;
    private Bitmap mWaterBitmap;
    private int mWaterRes;
    private int mWaterPosition;
    private int mTeXiaoId;

    private boolean mMakeupLipstick;
    private boolean mMakeupEyelash;
    private boolean mMakeupEyeliner;
    private boolean mMakeupEyebrow;
    private boolean mMakeupBlush;

    private MhDataManager() {

    }

    /**
     * Get show capture boolean.
     *
     * @return the boolean
     */
    public boolean getShowCapture(){
        return isShowCapture;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MhDataManager getInstance() {
        if (sInstance == null) {
            synchronized (MhDataManager.class) {
                if (sInstance == null) {
                    sInstance = new MhDataManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * Get context context.
     *
     * @return the context
     */
    public Context getContext(){
        return mContext;
    }

    /**
     * Get mh beauty manager mh beauty manager.
     *
     * @return the mh beauty manager
     */
    public MHBeautyManager getMHBeautyManager(){
        return mMhManager;
    }

    /**
     * Set beauty view holder.
     *
     * @param beautyViewHolder the beauty view holder
     */
    public void setBeautyViewHolder(BeautyViewHolder beautyViewHolder){
        mBeautyViewHolder = beautyViewHolder;
    }

    /**
     * Create mh data manager.
     *
     * @param context the context
     * @return the mh data manager
     */
    public MhDataManager create(Context context) {

        mContext = context;
        mSPUtil = new SPUtil(context);
        mInputValue = new MeiYanValueBean();
        mUseValue = null;
        mMeiYanValue = null;
        mOneKeyValue = null;
        try {
            mMhManager = new MHBeautyManager(context,true);
            mMhManager.setSmooth(0.08f);
            mMhManager.setAlwaysTrack(true);
            mMhManager.setOnFaceDetectListener(new OnFaceDetectListener() {
                @Override
                public void OnFaceDetect(boolean hasFace) {
                    Log.e(TAG, "OnFaceDetect: " + hasFace);
                }
            });
        } catch (Exception e) {
            mMhManager = null;
            e.printStackTrace();
        }

        MeiYanValueBean  meiYanValueBean = new MeiYanValueBean();

        if (mMeiYanDataBean == null){
            meiYanValueBean.setMeiBai(7);
            meiYanValueBean.setMoPi(7);
            meiYanValueBean.setHongRun(5);
            meiYanValueBean.setLiangDu(57);

            meiYanValueBean.setDaYan(30);
            meiYanValueBean.setMeiMao(50);
            meiYanValueBean.setYanJu(50);
            meiYanValueBean.setYanJiao(50);
            meiYanValueBean.setShouLian(30);
            meiYanValueBean.setZuiXing(50);
            meiYanValueBean.setShouBi(50);
            meiYanValueBean.setXiaBa(50);
            meiYanValueBean.setETou(50);
            meiYanValueBean.setChangBi(50);
            meiYanValueBean.setXueLian(0);
            meiYanValueBean.setKaiYanJiao(50);
        }else{
            meiYanValueBean  = mMeiYanDataBean.getMeiYanValueBean();
        }

        setMeiYanValue(meiYanValueBean)
                .useMeiYan().notifyLiangDuChanged();
        setMeiYanValue(meiYanValueBean)
                .useMeiYan().notifyMeiYanChanged();

        requestActionStickers();

        mMhManager.setOnStickerActionListener(new OnStickerActionListener() {
            @Override
            public void OnStickerAction(int action) {
                 if (mBeautyViewHolder != null){
                     mBeautyViewHolder.hideTip();
                     mTieZhiShow  = true;
                 }
            }
        });

        restoreMeiYanData();

        return this;
    }

    private void saveMeiYanData(){

        if (mMeiYanDataBean == null){
            mMeiYanDataBean = new MeiYanDataBean();
        }

        if (mMeiYanValue !=null){

            MeiYanValueBean meiYanValueBean =  MeiYanValueBean.copy(mMeiYanValue);
            mMeiYanDataBean.setMeiYanValueBean(meiYanValueBean);
            mMeiYanDataBean.setTieZhiName(mTieZhiName);
            mMeiYanDataBean.setTieZhiAction(mTieZhiAction);
            mMeiYanDataBean.setTieZhiShow(mTieZhiShow);
            mMeiYanDataBean.setTieZhiIsAction(mTieZhiIsAction);
            mMeiYanDataBean.setTieZhiKey(mTieZhiKey);
            mMeiYanDataBean.setFilterId(mFilterId);
            mMeiYanDataBean.setTeXiaoId(mTeXiaoId);
            mMeiYanDataBean.setWaterBitmap(mWaterBitmap);
            mMeiYanDataBean.setWaterRes(mWaterRes);
            mMeiYanDataBean.setWaterposition(mWaterPosition);
            mMeiYanDataBean.setHahaName(mHahaName);
            mMeiYanDataBean.setHahaTeXiao(mHahaTexiao);

            mMeiYanDataBean.setMakeupLipstick(mMakeupLipstick);
            mMeiYanDataBean.setMakeupEyelash(mMakeupEyelash);
            mMeiYanDataBean.setMakeupEyeliner(mMakeupEyeliner);
            mMeiYanDataBean.setMakeupEyebrow(mMakeupEyebrow);
            mMeiYanDataBean.setMakeupBlush(mMakeupBlush);

            int[] useFaces = new int[mMhManager.getUseFaces().length];
            for (int i = 0; i < mMhManager.getUseFaces().length ; i++) {
                useFaces[i] = mMhManager.getUseFaces()[i];
            }
            mMeiYanDataBean.setUseFaces(useFaces);
        }
    }


    private void restoreMeiYanData(){
        if (mMeiYanDataBean != null && mMhManager != null){

            mMhManager.setUseFaces(mMeiYanDataBean.getUseFaces());
            mMhManager.setSticker(mMeiYanDataBean.getTieZhiName(),mMeiYanDataBean.getTieZhiAction(),mMeiYanDataBean.getTieZhiShow(),mMeiYanDataBean.getTieZhiIsAction(),mMeiYanDataBean.getTieZhiKey());
            mMhManager.setFilter(mMeiYanDataBean.getFilterId());
            mMhManager.setSpeciallyEffect(mMeiYanDataBean.getTeXiaoId());

            Bitmap bitmap = null;
            if (mMeiYanDataBean.getWaterRes() == 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
            } else {
                bitmap = BitmapFactory.decodeResource(MhDataManager.getInstance().getContext().getResources(),mMeiYanDataBean.getWaterRes());
            }
            if (bitmap != null) {
                mMhManager.setWaterMark(bitmap,mMeiYanDataBean.getWaterposition());
            }
            mMhManager.setDistortionEffect(mMeiYanDataBean.getHahaName(),mMeiYanDataBean.getHahaTeXiao());

            mMhManager.setMakeup(MHSDK.MAKEUP_LIPSTICK,mMeiYanDataBean.isMakeupLipstick());
            mMhManager.setMakeup(MHSDK.MAKEUP_EYELASH,mMeiYanDataBean.isMakeupEyelash());
            mMhManager.setMakeup(MHSDK.MAKEUP_EYELINER,mMeiYanDataBean.isMakeupEyeliner());
            mMhManager.setMakeup(MHSDK.MAKEUP_EYEBROW,mMeiYanDataBean.isMakeupEyebrow());
            mMhManager.setMakeup(MHSDK.MAKEUP_BLUSH,mMeiYanDataBean.isMakeupBlush());

        }
    }

    /**
     * Get action sticker name string.
     *
     * @return the string
     */
    public String getActionStickerName(){
//        if (TextUtils.isEmpty(mActionStickerName)){
//            mActionStickerName =  mSPUtil.getString(Constants.TIEZHI_ACTION_NAME,"");
//        }
        return mActionStickerName;
    }

    /**
     * Set action sticker name.
     *
     * @param actionStickerName the action sticker name
     */
    public void setActionStickerName(String actionStickerName){
        mActionStickerName = actionStickerName;
//        mSPUtil.commitString(Constants.TIEZHI_ACTION_NAME,actionStickerName);
    }


    private void requestActionStickers(){
        MhDataManager.getTieZhiList(ResourceUrl.STICKER_THUMB_LIST_URL5, new CommonCallback<String>() {
            @Override
            public void callback(String jsonStr) {
                if (TextUtils.isEmpty(jsonStr)) {
                    return;
                }
                try {
                    JSONObject obj = JSON.parseObject(jsonStr);
                    List<TieZhiBean> list = JSON.parseArray(obj.getString("list"), TieZhiBean.class);
                    if (list != null && list.size() > 0) {
                        if (list.size() == 1){
                            String actionStickerName = list.get(0).getName();
                            String resource = list.get(0).getResource();
                            setActionStickerName(actionStickerName);
                            downloadActionStickers(actionStickerName,resource);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void downloadActionStickers(String stickerName,String resource){
        downloadTieZhi(stickerName,resource, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean isSuccess) {
                if (isSuccess) {

                } else {

                }
            }
        });
    }


    /**
     * Sets mei yan changed listener.
     *
     * @param meiYanChangedListener the mei yan changed listener
     */
    public void setMeiYanChangedListener(IBeautyEffectListener meiYanChangedListener) {
        mMeiYanChangedListener = meiYanChangedListener;
    }

    /**
     * Release.
     */
    public void release() {
        saveMeiYanData();
        OkGo.getInstance().cancelTag(ResourceUrl.GET_TIEZHI);
        OkGo.getInstance().cancelTag(ResourceUrl.DOWNLOAD_TIEZHI);
        mMeiYanChangedListener = null;
        mInputValue = null;
        mUseValue = null;
        mMeiYanValue = null;
        mOneKeyValue = null;
        if (mMhManager != null) {
            try {
//                mMhManager.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        mMhManager = null;

    }

    /**
     * Destroy.
     */
    public void destroy(){
        mMeiYanDataBean = null;
    }

    /**
     * Render int.
     *
     * @param texture the texture
     * @param width   the width
     * @param height  the height
     * @return the int
     */
    public int render(int texture, int width, int height) {
        if (mMhManager != null) {
            try {
                texture = mMhManager.render12(texture, width, height,2,1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "render: ");
        }
        return texture;
    }

    /**
     * Render int.
     *
     * @param texture      the texture
     * @param width        the width
     * @param height       the height
     * @param faceScale    the face scale
     * @param textureScale the texture scale
     * @return the int
     */
    public int render(int texture, int width, int height,int faceScale, int textureScale) {
        if (mMhManager != null) {
            try {
                texture = mMhManager.render12(texture, width, height,faceScale,textureScale);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "render: ");
        }
        return texture;
    }

    /**
     * Save beauty value.
     */
    public void saveBeautyValue() {
        if (mMeiYanValue != null) {
        }
    }

    /**
     * Gets mei yan value.
     *
     * @return the mei yan value
     */
    public MeiYanValueBean getMeiYanValue() {
        return mMeiYanValue;
    }

    /**
     * Gets one key value.
     *
     * @return the one key value
     */
    public MeiYanValueBean getOneKeyValue() {
        return mOneKeyValue;
    }

    /**
     * Sets mei yan value.
     *
     * @param meiYanValue the mei yan value
     * @return the mei yan value
     */
    public MhDataManager setMeiYanValue(MeiYanValueBean meiYanValue) {
        mMeiYanValue = meiYanValue;
        return this;
    }

    /**
     * Sets one key value.
     *
     * @param oneKeyValue the one key value
     * @return the one key value
     */
    public MhDataManager setOneKeyValue(MeiYanValueBean oneKeyValue) {
        mOneKeyValue = oneKeyValue;
        return this;
    }


    /**
     * Use mei yan mh data manager.
     *
     * @return the mh data manager
     */
    public MhDataManager useMeiYan() {
        mUseValue = mMeiYanValue;
        return this;
    }

    /**
     * Use one key mh data manager.
     *
     * @return the mh data manager
     */
    public MhDataManager useOneKey() {
        mUseValue = mOneKeyValue;
        return this;
    }


    /**
     * Sets mei bai.
     *
     * @param meiBai the mei bai
     */
    public void setMeiBai(int meiBai) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setMeiBai(meiBai);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets mo pi.
     *
     * @param moPi the mo pi
     */
    public void setMoPi(int moPi) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setMoPi(moPi);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets hong run.
     *
     * @param hongRun the hong run
     */
    public void setHongRun(int hongRun) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setHongRun(hongRun);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets liang du.
     *
     * @param liangDu the liang du
     */
    public void setLiangDu(int liangDu) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setLiangDu(liangDu);
        }
        useMeiYan().notifyLiangDuChanged();
    }


    /**
     * Sets da yan.
     *
     * @param daYan the da yan
     */
    public void setDaYan(int daYan) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setDaYan(daYan);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets mei mao.
     *
     * @param meiMao the mei mao
     */
    public void setMeiMao(int meiMao) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setMeiMao(meiMao);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets yan ju.
     *
     * @param yanJu the yan ju
     */
    public void setYanJu(int yanJu) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setYanJu(yanJu);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    /**
     * Sets yan jiao.
     *
     * @param yanJiao the yan jiao
     */
    public void setYanJiao(int yanJiao) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setYanJiao(yanJiao);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets shou lian.
     *
     * @param shouLian the shou lian
     */
    public void setShouLian(int shouLian) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setShouLian(shouLian);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets zui xing.
     *
     * @param zuiXing the zui xing
     */
    public void setZuiXing(int zuiXing) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setZuiXing(zuiXing);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    /**
     * Sets shou bi.
     *
     * @param shouBi the shou bi
     */
    public void setShouBi(int shouBi) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setShouBi(shouBi);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    /**
     * Sets xia ba.
     *
     * @param xiaBa the xia ba
     */
    public void setXiaBa(int xiaBa) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setXiaBa(xiaBa);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets e tou.
     *
     * @param ETou the e tou
     */
    public void setETou(int ETou) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setETou(ETou);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * Sets chang bi.
     *
     * @param changBi the chang bi
     */
    public void setChangBi(int changBi) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setChangBi(changBi);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    /**
     * Sets xue lian.
     *
     * @param xueLian the xue lian
     */
    public void setXueLian(int xueLian) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setXueLian(xueLian);
        }
        useMeiYan().notifyMeiYanChanged();
    }

    /**
     * Sets kai yan jiao.
     *
     * @param kaiYanJiao the kai yan jiao
     */
    public void setKaiYanJiao(int kaiYanJiao) {
        if (mMeiYanValue != null) {
            mMeiYanValue.setKaiYanJiao(kaiYanJiao);
        }
        useMeiYan().notifyMeiYanChanged();
    }


    /**
     * 哈哈镜
     *
     * @param hahaName the haha name
     * @param isTeXiao the is te xiao
     */
    public void setHaHa(int hahaName,boolean isTeXiao) {
        if (mMhManager != null) {
            mMhManager.setDistortionEffect(hahaName,isTeXiao);
            mHahaName = hahaName;
            mHahaTexiao =  isTeXiao;
        }
    }

    /**
     * 贴纸是否可用
     *
     * @return the boolean
     */
    public boolean isTieZhiEnable() {
        return mMeiYanChangedListener == null || mMeiYanChangedListener.isTieZhiEnable();
    }

    /**
     * 贴纸
     *
     * @param tieZhiName the tie zhi name
     * @param tieZhiKey  the tie zhi key
     */
    public void setTieZhi(String tieZhiName,String tieZhiKey) {
        if (mMhManager != null) {
            boolean isShow = true;
            boolean isAction = false;
            String key = tieZhiKey;
            mMhManager.setSticker(tieZhiName,MHSDK.TE_XIAO_ACTION_NONE,isShow,isAction,key);
            mTieZhiName = tieZhiName;
            mTieZhiAction = 0;
            mTieZhiShow  = isShow;
            mTieZhiIsAction = isAction;
            mTieZhiKey = key;
        }
    }

    /**
     * 贴纸
     *
     * @param tieZhiName   the tie zhi name
     * @param tieZhiAction the tie zhi action
     */
    public void setTieZhi(String tieZhiName,int tieZhiAction) {
        if (mMhManager != null) {
            boolean isShow = false;
            boolean isAction = true;
            String key = null;
            mMhManager.setSticker(tieZhiName,tieZhiAction,isShow,isAction,key);
            mTieZhiName = tieZhiName;
            mTieZhiAction = tieZhiAction;
            mTieZhiShow  = isShow;
            mTieZhiIsAction = isAction;
            mTieZhiKey = key;
        }
    }

    /**
     * 水印
     *
     * @param waterRes the water res
     * @param position the position
     */
    public void setWater(int waterRes, int position) {
        if (mMhManager != null) {
            Bitmap bitmap = null;
            if (waterRes == 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
            } else {
                bitmap = BitmapFactory.decodeResource(MhDataManager.getInstance().getContext().getResources(),waterRes);
            }
            if (bitmap != null) {
                mMhManager.setWaterMark(bitmap,position);
            }
            mWaterBitmap = bitmap;
            mWaterRes = waterRes;
            mWaterPosition = position;
        }
    }


    /**
     * 特效
     *
     * @param teXiaoId the te xiao id
     */
    public void setTeXiao(int teXiaoId) {
        if (mMhManager != null) {
            mMhManager.setSpeciallyEffect(teXiaoId);
            mTeXiaoId = teXiaoId;
        }
    }


    /**
     * 滤镜
     *
     * @param filterId the filter id
     */
    public void setFilter(int filterId) {
        if (mMhManager != null) {
            mMhManager.setFilter(filterId);
            mFilterId = filterId;
        }
    }

    /**
     * Sets makeup.
     *
     * @param makeupId the makeup id
     * @param enable   the enable
     */
    public void setMakeup(int makeupId,boolean enable) {
        if (mMhManager != null) {

            switch (makeupId){
                case MHSDK.MAKEUP_NONE:
                    mMakeupLipstick = false;
                    mMakeupEyelash = false;
                    mMakeupEyeliner = false;
                    mMakeupEyebrow = false;
                    mMakeupBlush = false;
                    break;
                case MHSDK.MAKEUP_LIPSTICK:
                    mMakeupLipstick = enable;
                    break;
                case MHSDK.MAKEUP_EYELASH:
                    mMakeupEyelash = enable;
                    break;
                case MHSDK.MAKEUP_EYELINER:
                    mMakeupEyeliner = enable;
                    break;
                case MHSDK.MAKEUP_EYEBROW:
                    mMakeupEyebrow = enable;
                    break;
                case MHSDK.MAKEUP_BLUSH:
                    mMakeupBlush = enable;
                    break;
            }

            mMhManager.setMakeup(makeupId,enable);
        }
    }


    private void notifyLiangDuChanged() {
        if (mMhManager == null || mInputValue == null || mUseValue == null) {
            return;
        }
        //亮度
        if (mInputValue.getLiangDu() != mUseValue.getLiangDu()) {
            mInputValue.setLiangDu(mUseValue.getLiangDu());
            mMhManager.setBrightness(mUseValue.getLiangDu());
        }
    }

    /**
     * Notify mei yan changed.
     */
    public void notifyMeiYanChanged() {
        if (mMhManager == null || mInputValue == null || mUseValue == null) {
            return;
        }
        MeiYanValueBean input = mInputValue;
        MeiYanValueBean use = mUseValue;

        if (mMeiYanChangedListener != null) {
            boolean meiBaiChanged = false;
            boolean moPiChanged = false;
            boolean hongRunChanged = false;
            if (input.getMeiBai() != use.getMeiBai()) {
                input.setMeiBai(use.getMeiBai());
                meiBaiChanged = true;
            }
            if (input.getMoPi() != use.getMoPi()) {
                input.setMoPi(use.getMoPi());
                moPiChanged = true;
            }
            if (input.getHongRun() != use.getHongRun()) {
                input.setHongRun(use.getHongRun());
                hongRunChanged = true;
            }
            mMeiYanChangedListener.onMeiYanChanged(input.getMeiBai(), meiBaiChanged, input.getMoPi(), moPiChanged, input.getHongRun(), hongRunChanged);

        } else {
            //美白
            if (input.getMeiBai() != use.getMeiBai()) {
                input.setMeiBai(use.getMeiBai());
                mMhManager.setSkinWhiting(input.getMeiBai());
            }
            //磨皮
            if (input.getMoPi() != use.getMoPi()) {
                input.setMoPi(use.getMoPi());
                mMhManager.setSkinSmooth(input.getMoPi());

            }
            //红润
            if (input.getHongRun() != use.getHongRun()) {
                input.setHongRun(use.getHongRun());
                mMhManager.setSkinTenderness(input.getHongRun());
            }
        }

        //大眼
        if (input.getDaYan() != use.getDaYan()) {
            input.setDaYan(use.getDaYan());
            mMhManager.setBigEye(input.getDaYan());
        }
        //眉毛
        if (input.getMeiMao() != use.getMeiMao()) {
            input.setMeiMao(use.getMeiMao());
            mMhManager.setEyeBrow(input.getMeiMao());
        }
        //眼距
        if (input.getYanJu() != use.getYanJu()) {
            input.setYanJu(use.getYanJu());
            mMhManager.setEyeLength(input.getYanJu());
        }
        //眼角
        if (input.getYanJiao() != use.getYanJiao()) {
            input.setYanJiao(use.getYanJiao());
            mMhManager.setEyeCorner(input.getYanJiao());
        }
        //瘦脸
        if (input.getShouLian() != use.getShouLian()) {
            input.setShouLian(use.getShouLian());
            mMhManager.setFaceLift(input.getShouLian());
        }
        //嘴型
        if (input.getZuiXing() != use.getZuiXing()) {
            input.setZuiXing(use.getZuiXing());
            mMhManager.setMouseLift(input.getZuiXing());
        }
        //瘦鼻
        if (input.getShouBi() != use.getShouBi()) {
            input.setShouBi(use.getShouBi());
            mMhManager.setNoseLift(input.getShouBi());
        }
        //下巴
        if (input.getXiaBa() != use.getXiaBa()) {
            input.setXiaBa(use.getXiaBa());
            mMhManager.setChinLift(input.getXiaBa());
        }
        //额头
        if (input.getETou() != use.getETou()) {
            input.setETou(use.getETou());
            mMhManager.setForeheadLift(input.getETou());
        }
        //长鼻
        if (input.getChangBi() != use.getChangBi()) {
            input.setChangBi(use.getChangBi());
            mMhManager.setLengthenNoseLift(input.getChangBi());
        }
        //削脸
        if (input.getXueLian() != use.getXueLian()) {
            input.setXueLian(use.getXueLian());
            mMhManager.setFaceShave(input.getXueLian());
        }
        //开眼角
        if (input.getKaiYanJiao() != use.getKaiYanJiao()) {
            input.setKaiYanJiao(use.getKaiYanJiao());
            mMhManager.setEyeAlat(input.getKaiYanJiao());
        }
    }



    /**
     * 获取贴纸列表
     */
//    public static void getTieZhiList(int id, final CommonCallback<String> commonCallback) {
//        MHSDK.getTieZhiList(id, new MHSDK.TieZhiListCallback() {
//            @Override
//            public void getTieZhiList(String data) {
//                if (commonCallback != null) {
//                    commonCallback.callback(data);
//                }
//            }
//        });
//    }

    /**
     * 获取贴纸列表
     *
     * @param url            the url
     * @param commonCallback the common callback
     */
    public static void getTieZhiList(String url, final CommonCallback<String> commonCallback) {
        MHSDK.getTieZhiList(url, new MHSDK.TieZhiListCallback() {
            @Override
            public void getTieZhiList(String data) {
                if (commonCallback != null) {
                    commonCallback.callback(data);
                }
            }
        });
    }

    /**
     * 下载贴纸
     *
     * @param tieZhiName     the tie zhi name
     * @param resource       the resource
     * @param commonCallback the common callback
     */
    public static void downloadTieZhi(String tieZhiName,String resource, final CommonCallback<Boolean> commonCallback) {
        MHSDK.downloadSticker(tieZhiName,resource, new MHSDK.TieZhiDownloadCallback() {
            @Override
            public void tieZhiDownload(String tieZhiName, boolean success) {
                if (success) {
                    if (commonCallback != null) {
                        commonCallback.callback(true);
                    }
                } else {
                    if (commonCallback != null) {
                        commonCallback.callback(false);
                    }
                }
            }
        });

    }


    /**
     * 贴纸是否下载了
     *
     * @param name the name
     * @return the boolean
     */
    public static boolean isTieZhiDownloaded(String name) {
        return MHSDK.isTieZhiDownloaded(name);
    }


}
