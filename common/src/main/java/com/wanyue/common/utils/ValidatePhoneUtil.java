package com.wanyue.common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidatePhoneUtil {

    //判断手机号码的正则表达式
    //private static final String MOBILE_NUM_REGEX = "^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9])|(16[0-9])|(19[0-9]))\\d{8}$";

    // South African phone number regex (accepts +27, 27, or 0)
    private static final String MOBILE_NUM_REGEX = "^(\\+?27[6-8][0-9]{8}|0[6-8][0-9]{8})$";

    /**
     * Validate South African phone number
     * Format: +27 or 0 followed by 9 digits
     * Total length: 10-11 digits (excluding country code)
     *
     * @param mobileNumber
     * @return true if valid South African phone number
     */
    public static boolean validateMobileNumber(String mobileNumber) {
        if(TextUtils.isEmpty(mobileNumber)){
            return false;
        }
        Pattern p = Pattern.compile(MOBILE_NUM_REGEX);
        Matcher m = p.matcher(mobileNumber);
        return m.matches();
    }


}
