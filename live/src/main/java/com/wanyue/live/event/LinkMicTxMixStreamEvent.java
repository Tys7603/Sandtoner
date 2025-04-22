package com.wanyue.live.event;

/**
 * 2019/3/25.
 * 腾讯连麦的时候 主播混流
 */
public class LinkMicTxMixStreamEvent {

    private int mType;
    private String mToStream;

    /**
     * Instantiates a new Link mic tx mix stream event.
     *
     * @param type     the type
     * @param toStream the to stream
     */
    public LinkMicTxMixStreamEvent(int type, String toStream) {
        mType = type;
        mToStream = toStream;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public int getType() {
        return mType;
    }

    /**
     * Gets to stream.
     *
     * @return the to stream
     */
    public String getToStream() {
        return mToStream;
    }
}
