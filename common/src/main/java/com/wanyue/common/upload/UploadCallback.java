package com.wanyue.common.upload;

import java.util.List;

/**
 * 2019/4/16.
 */

@Deprecated
public interface UploadCallback {
    void onFinish(List<UploadBean> list, boolean success);
}
