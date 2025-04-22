package com.wanyue.live.model;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import com.wanyue.live.bean.LiveBean;

/**
 * The type Live model.
 */
public class LiveModel extends ViewModel {
    private LiveBean mLiveBean;
    private String mLiveUid;
    private String mStream;
    private int mLikeNum;
    private boolean mIsShutUp;

    /**
     * Gets live bean.
     *
     * @return the live bean
     */
    public LiveBean getLiveBean() {
        return mLiveBean;
    }

    /**
     * Sets live bean.
     *
     * @param liveBean the live bean
     */
    public void setLiveBean(LiveBean liveBean) {
        mLiveBean = liveBean;
        if(mLiveBean!=null){
           mLiveUid=mLiveBean.getUid();
        }
    }

    /**
     * Is shut up boolean.
     *
     * @return the boolean
     */
    public boolean isShutUp() {
        return mIsShutUp;
    }

    /**
     * Sets shut up.
     *
     * @param shutUp the shut up
     */
    public void setShutUp(boolean shutUp) {
        mIsShutUp = shutUp;
    }

    /**
     * Gets live uid.
     *
     * @return the live uid
     */
    public String getLiveUid() {
        return mLiveUid;
    }

    /**
     * Sets live uid.
     *
     * @param liveUid the live uid
     */
    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    /**
     * Sets stream.
     *
     * @param stream the stream
     */
    public void setStream(String stream) {
        mStream = stream;
    }

    /**
     * Gets stream.
     *
     * @return the stream
     */
    public String getStream() {
        return mStream;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLiveBean=null;
        mLiveUid=null;
    }


    /**
     * Get context shut up boolean.
     *
     * @param appCompatActivity the app compat activity
     * @return the boolean
     */
    public static boolean getContextShutUp(FragmentActivity appCompatActivity){
        if(appCompatActivity==null){
            return false;
        }
        LiveModel liveModel= ViewModelProviders.of(appCompatActivity).get(LiveModel.class);
        if(liveModel!=null){
            return liveModel.isShutUp();
        }
        return false;
    }


    /**
     * Set shut up context.
     *
     * @param appCompatActivity the app compat activity
     * @param isShutUp          the is shut up
     */
    public static void setShutUpContext(FragmentActivity appCompatActivity,boolean isShutUp){
        if(appCompatActivity==null){
            return ;
        }
        LiveModel liveModel= ViewModelProviders.of(appCompatActivity).get(LiveModel.class);
        if(liveModel!=null){
            liveModel.setShutUp(isShutUp);
        }
    }


    /**
     * Get context live bean live bean.
     *
     * @param appCompatActivity the app compat activity
     * @return the live bean
     */
    public static LiveBean getContextLiveBean(FragmentActivity appCompatActivity){
       if(appCompatActivity==null){
           return null;
       }
       LiveModel liveModel= ViewModelProviders.of(appCompatActivity).get(LiveModel.class);
       if(liveModel!=null){
           return liveModel.getLiveBean();
       }
       return null;
   }

    /**
     * Get context live uid string.
     *
     * @param appCompatActivity the app compat activity
     * @return the string
     */
    public static String getContextLiveUid(FragmentActivity appCompatActivity){
        if(appCompatActivity==null){
            return null;
        }
        LiveModel liveModel= ViewModelProviders.of(appCompatActivity).get(LiveModel.class);
        if(liveModel!=null){
            return liveModel.getLiveUid();
        }
        return null;
    }

    /**
     * Get context stream string.
     *
     * @param appCompatActivity the app compat activity
     * @return the string
     */
    public static String getContextStream(FragmentActivity appCompatActivity){
        if(appCompatActivity==null){
            return null;
        }
        LiveModel liveModel= ViewModelProviders.of(appCompatActivity).get(LiveModel.class);
        if(liveModel!=null){
            return liveModel.getStream();
        }
        return null;
    }

}
