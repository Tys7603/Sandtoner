package com.wanyue.common.mob;

/**
 * 2018/9/21.
 */

public interface MobCallback {
    void onSuccess(Object data);

    void onError();

    void onCancel();

    void onFinish();
}
