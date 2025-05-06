package com.wanyue.main.bean;

import com.wanyue.common.bean.commit.CommitEntity;
import com.wanyue.common.model.Country;

public class RegisterCommitBean extends CommitEntity {
    private String phone;
    private String pwd;
    private String code;
    private String spread;
    private Country selectedCountry;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        observer();
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
        observer();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        observer();
    }

    public String getSpread() {
        return spread;
    }

    public void setSpread(String spread) {
        this.spread = spread;
        observer();
    }

    public void setSelectedCountry(Country country) {
        this.selectedCountry = country;
        observer();
    }

    @Override
    public boolean observerCondition() {
        boolean isValidPhone = selectedCountry != null && selectedCountry.isValidPhoneNumber(phone);
        return isValidPhone && fieldNotEmpty(pwd) && fieldNotEmpty(code);
    }
}
