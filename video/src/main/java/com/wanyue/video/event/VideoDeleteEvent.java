package com.wanyue.video.event;

/**
 *  on 2018/12/15.
 */

public class VideoDeleteEvent {

    private String mVideoId;

    public VideoDeleteEvent(String videoId) {
        mVideoId = videoId;
    }

    public String getVideoId() {
        return mVideoId;
    }
}
