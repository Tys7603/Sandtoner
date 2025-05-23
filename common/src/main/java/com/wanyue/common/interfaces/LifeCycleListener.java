package com.wanyue.common.interfaces;

/**
 * 2018/9/26.
 */

public interface LifeCycleListener {

    void onCreate();

    void onStart();

    void onReStart();

    void onResume();

    void onPause();

    void onStop();

    void releaseActivty();

    void onDestroy();
}
