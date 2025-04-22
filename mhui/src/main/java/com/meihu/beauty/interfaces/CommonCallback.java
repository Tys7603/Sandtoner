package com.meihu.beauty.interfaces;

/**
 * on 2017/8/11.
 *
 * @param <T> the type parameter
 */
public abstract class CommonCallback<T> {
    /**
     * Callback.
     *
     * @param bean the bean
     */
    public abstract void callback(T bean);
}
