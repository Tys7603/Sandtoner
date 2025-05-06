package com.wanyue.common.model;

import java.util.regex.Pattern;

public class Country {
    private String name;
    private String code;
    private String dialCode;
    private int flagResId;
    private String phoneRegex;

    public Country(String name, String code, String dialCode, int flagResId, String phoneRegex) {
        this.name = name;
        this.code = code;
        this.dialCode = dialCode;
        this.flagResId = flagResId;
        this.phoneRegex = phoneRegex;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDialCode() {
        return dialCode;
    }

    public int getFlagResId() {
        return flagResId;
    }

    public String getPhoneRegex() {
        return phoneRegex;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return Pattern.matches(phoneRegex, phoneNumber);
    }

    public String getRegionCodeNumber() {
        return dialCode != null ? dialCode.replace("+", "") : "";
    }
}