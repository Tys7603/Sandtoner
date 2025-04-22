package com.meihu.beauty.bean;

/**
 * The type Mei yan one key value.
 */
public class MeiYanOneKeyValue {

    private Value mMeiBai;
    private Value mMoPi;
    private Value mHongRun;
    private Value mDaYan;
    private Value mMeiMao;
    private Value mYanJu;
    private Value mYanJiao;
    private Value mShouLian;
    private Value mZuiXing;
    private Value mShouBi;
    private Value mXiaBa;
    private Value mETou;
    private Value mChangBi;
    private Value mXueLian;
    private Value mKaiYanJiao;

    /**
     * Instantiates a new Mei yan one key value.
     *
     * @param meiBai     the mei bai
     * @param moPi       the mo pi
     * @param hongRun    the hong run
     * @param daYan      the da yan
     * @param meiMao     the mei mao
     * @param yanJu      the yan ju
     * @param yanJiao    the yan jiao
     * @param shouLian   the shou lian
     * @param zuiXing    the zui xing
     * @param shouBi     the shou bi
     * @param xiaBa      the xia ba
     * @param eTou       the e tou
     * @param changBi    the chang bi
     * @param xueLian    the xue lian
     * @param kaiYanJiao the kai yan jiao
     */
    public MeiYanOneKeyValue(
            Value meiBai,
            Value moPi,
            Value hongRun,
            Value daYan,
            Value meiMao,
            Value yanJu,
            Value yanJiao,
            Value shouLian,
            Value zuiXing,
            Value shouBi,
            Value xiaBa,
            Value eTou,
            Value changBi,
            Value xueLian,
            Value kaiYanJiao) {
        mMeiBai = meiBai;
        mMoPi = moPi;
        mHongRun = hongRun;
        mDaYan = daYan;
        mMeiMao = meiMao;
        mYanJu = yanJu;
        mYanJiao = yanJiao;
        mShouLian = shouLian;
        mZuiXing = zuiXing;
        mShouBi = shouBi;
        mXiaBa = xiaBa;
        mETou = eTou;
        mChangBi = changBi;
        mXueLian = xueLian;
        mKaiYanJiao = kaiYanJiao;
    }

    /**
     * Calculate value.
     *
     * @param resultValue the result value
     * @param rate        the rate
     */
    public void calculateValue(MeiYanValueBean resultValue, float rate) {
        resultValue.setMeiBai(mMeiBai.getValue(rate));
        resultValue.setMoPi(mMoPi.getValue(rate));
        resultValue.setHongRun(mHongRun.getValue(rate));
        resultValue.setDaYan(mDaYan.getValue(rate));
        resultValue.setMeiMao(mMeiMao.getValue(rate));
        resultValue.setYanJu(mYanJu.getValue(rate));
        resultValue.setYanJiao(mYanJiao.getValue(rate));
        resultValue.setShouLian(mShouLian.getValue(rate));
        resultValue.setZuiXing(mZuiXing.getValue(rate));
        resultValue.setShouBi(mShouBi.getValue(rate));
        resultValue.setXiaBa(mXiaBa.getValue(rate));
        resultValue.setETou(mETou.getValue(rate));
        resultValue.setChangBi(mChangBi.getValue(rate));
        resultValue.setXueLian(mXueLian.getValue(rate));
        resultValue.setKaiYanJiao(mKaiYanJiao.getValue(rate));
    }


    /**
     * The type Value.
     */
    public static class Value {
        private int minValue;
        private int maxValue;

        /**
         * Instantiates a new Value.
         *
         * @param minValue the min value
         * @param maxValue the max value
         */
        public Value(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        /**
         * Gets value.
         *
         * @param rate the rate
         * @return the value
         */
        int getValue(float rate) {
            return minValue + (int) ((maxValue - minValue) * rate);
        }
    }


}
