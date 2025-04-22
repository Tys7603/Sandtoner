package com.wanyue.live.music;

/**
 * 2018/10/22.
 */
public class LrcBean {

    private int index;
    private long startTime;//歌词的起始时间
    private long endTime;//歌词的结束时间
    private float duration;//歌词的持续时长
    private String lrc;//歌词
    private float progress;//歌词进度

    /**
     * Instantiates a new Lrc bean.
     *
     * @param startTime the start time
     * @param lrc       the lrc
     */
    public LrcBean(long startTime, String lrc) {
        this.startTime = startTime;
        this.lrc = lrc;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets index.
     *
     * @param index the index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets lrc.
     *
     * @return the lrc
     */
    public String getLrc() {
        return lrc;
    }

    /**
     * Sets lrc.
     *
     * @param lrc the lrc
     */
    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    /**
     * Gets end time.
     *
     * @return the end time
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Sets end time.
     *
     * @param endTime the end time
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
        this.duration = endTime - this.startTime;
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Gets progress.
     *
     * @return the progress
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Sets progress.
     *
     * @param progress the progress
     */
    public void setProgress(float progress) {
        this.progress = progress;
    }
}
