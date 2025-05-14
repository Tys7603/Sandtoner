package com.wanyue.common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidatePhoneUtil {

    // Flexible phone number regex that accepts various international formats
    // Allows numbers with optional + prefix and 7-15 digits
    private static final String MOBILE_NUM_REGEX = "^[+]?[0-9]{7,15}$";

    /**
     * Validate phone number with flexible international format
     * Accepts various phone number formats from different countries
     *
     * @param mobileNumber
     * @return true if valid phone number format
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