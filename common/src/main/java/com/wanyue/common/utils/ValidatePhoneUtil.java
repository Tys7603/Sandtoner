package com.wanyue.common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidatePhoneUtil {

    // South African phone number regex that accepts:
    // 1. NSN format (9 digits starting with 6-8)
    // 2. National format (starting with 0)
    // 3. International format (starting with +27 or 27)
    private static final String MOBILE_NUM_REGEX = "^((\\+?27[6-8][0-9]{8})|(0[6-8][0-9]{8})|([6-8][0-9]{8}))$";

    /**
     * Validate South African phone number
     * Accepts:
     * - NSN format: 739022705 (9 digits starting with 6-8)
     * - National format: 0739022705 (starting with 0)
     * - International format: +27739022705 or 27739022705
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
