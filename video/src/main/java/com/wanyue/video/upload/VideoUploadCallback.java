package com.wanyue.video.upload;

/**
 *  on 2018/5/21.
 */

public interface VideoUploadCallback {
    void onSuccess(VideoUploadBean bean);

    void onFailure();
}
