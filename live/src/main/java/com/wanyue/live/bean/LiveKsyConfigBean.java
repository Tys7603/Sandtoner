package com.wanyue.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 2019/5/6.
 */
public class LiveKsyConfigBean implements Parcelable{

    private int mEncodeMethod;//编码模式
    private int mTargetResolution;////推流分辨率
    private int mTargetFps;//推流采集帧率
    private int mTargetGop;//推流采集gop
    private int mVideoKBitrate;//视频码率
    private int mVideoKBitrateMax;//最大视频码率
    private int mVideoKBitrateMin;//最小视频码率
    private int mAudioKBitrate;//音频码率
    private int mPreviewFps;//预览采集帧率
    private int mPreviewResolution;//预览分辨率

    /**
     * Instantiates a new Live ksy config bean.
     */
    public LiveKsyConfigBean() {
    }


    /**
     * Gets encode method.
     *
     * @return the encode method
     */
    @JSONField(name = "codingmode")
    public int getEncodeMethod() {
        return mEncodeMethod;
    }

    /**
     * Sets encode method.
     *
     * @param encodeMethod the encode method
     */
    @JSONField(name = "codingmode")
    public void setEncodeMethod(int encodeMethod) {
        mEncodeMethod = encodeMethod;
    }

    /**
     * Gets target resolution.
     *
     * @return the target resolution
     */
    @JSONField(name = "resolution")
    public int getTargetResolution() {
        return mTargetResolution;
    }

    /**
     * Sets target resolution.
     *
     * @param targetResolution the target resolution
     */
    @JSONField(name = "resolution")
    public void setTargetResolution(int targetResolution) {
        mTargetResolution = targetResolution;
    }

    /**
     * Gets target fps.
     *
     * @return the target fps
     */
    @JSONField(name = "fps")
    public int getTargetFps() {
        return mTargetFps;
    }

    /**
     * Sets target fps.
     *
     * @param targetFps the target fps
     */
    @JSONField(name = "fps")
    public void setTargetFps(int targetFps) {
        mTargetFps = targetFps;
    }

    /**
     * Gets target gop.
     *
     * @return the target gop
     */
    @JSONField(name = "gop")
    public int getTargetGop() {
        return mTargetGop;
    }

    /**
     * Sets target gop.
     *
     * @param targetGop the target gop
     */
    @JSONField(name = "gop")
    public void setTargetGop(int targetGop) {
        mTargetGop = targetGop;
    }

    /**
     * Gets video k bitrate.
     *
     * @return the video k bitrate
     */
    @JSONField(name = "bitrate")
    public int getVideoKBitrate() {
        return mVideoKBitrate;
    }

    /**
     * Sets video k bitrate.
     *
     * @param videoKBitrate the video k bitrate
     */
    @JSONField(name = "bitrate")
    public void setVideoKBitrate(int videoKBitrate) {
        mVideoKBitrate = videoKBitrate;
    }

    /**
     * Gets video k bitrate max.
     *
     * @return the video k bitrate max
     */
    @JSONField(name = "bitrate_max")
    public int getVideoKBitrateMax() {
        return mVideoKBitrateMax;
    }

    /**
     * Sets video k bitrate max.
     *
     * @param videoKBitrateMax the video k bitrate max
     */
    @JSONField(name = "bitrate_max")
    public void setVideoKBitrateMax(int videoKBitrateMax) {
        mVideoKBitrateMax = videoKBitrateMax;
    }

    /**
     * Gets video k bitrate min.
     *
     * @return the video k bitrate min
     */
    @JSONField(name = "bitrate_min")
    public int getVideoKBitrateMin() {
        return mVideoKBitrateMin;
    }

    /**
     * Sets video k bitrate min.
     *
     * @param videoKBitrateMin the video k bitrate min
     */
    @JSONField(name = "bitrate_min")
    public void setVideoKBitrateMin(int videoKBitrateMin) {
        mVideoKBitrateMin = videoKBitrateMin;
    }

    /**
     * Gets audio k bitrate.
     *
     * @return the audio k bitrate
     */
    @JSONField(name = "audiobitrate")
    public int getAudioKBitrate() {
        return mAudioKBitrate;
    }

    /**
     * Sets audio k bitrate.
     *
     * @param audioKBitrate the audio k bitrate
     */
    @JSONField(name = "audiobitrate")
    public void setAudioKBitrate(int audioKBitrate) {
        mAudioKBitrate = audioKBitrate;
    }

    /**
     * Gets preview fps.
     *
     * @return the preview fps
     */
    @JSONField(name = "preview_fps")
    public int getPreviewFps() {
        return mPreviewFps;
    }

    /**
     * Sets preview fps.
     *
     * @param previewFps the preview fps
     */
    @JSONField(name = "preview_fps")
    public void setPreviewFps(int previewFps) {
        mPreviewFps = previewFps;
    }

    /**
     * Gets preview resolution.
     *
     * @return the preview resolution
     */
    @JSONField(name = "preview_resolution")
    public int getPreviewResolution() {
        return mPreviewResolution;
    }

    /**
     * Sets preview resolution.
     *
     * @param previewResolution the preview resolution
     */
    @JSONField(name = "preview_resolution")
    public void setPreviewResolution(int previewResolution) {
        mPreviewResolution = previewResolution;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mEncodeMethod);
        dest.writeInt(mTargetResolution);
        dest.writeInt(mTargetFps);
        dest.writeInt(mTargetGop);
        dest.writeInt(mVideoKBitrate);
        dest.writeInt(mVideoKBitrateMax);
        dest.writeInt(mVideoKBitrateMin);
        dest.writeInt(mAudioKBitrate);
        dest.writeInt(mPreviewFps);
        dest.writeInt(mPreviewResolution);
    }

    /**
     * Instantiates a new Live ksy config bean.
     *
     * @param in the in
     */
    public LiveKsyConfigBean(Parcel in) {
        mEncodeMethod = in.readInt();
        mTargetResolution = in.readInt();
        mTargetFps = in.readInt();
        mTargetGop = in.readInt();
        mVideoKBitrate = in.readInt();
        mVideoKBitrateMax = in.readInt();
        mVideoKBitrateMin = in.readInt();
        mAudioKBitrate = in.readInt();
        mPreviewFps = in.readInt();
        mPreviewResolution = in.readInt();
    }

    /**
     * The constant CREATOR.
     */
    public static final Creator<LiveKsyConfigBean> CREATOR = new Creator<LiveKsyConfigBean>() {
        @Override
        public LiveKsyConfigBean createFromParcel(Parcel in) {
            return new LiveKsyConfigBean(in);
        }

        @Override
        public LiveKsyConfigBean[] newArray(int size) {
            return new LiveKsyConfigBean[size];
        }
    };
}
