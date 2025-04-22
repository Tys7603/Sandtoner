package com.wanyue.shop.bean;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.wanyue.common.bean.commit.CommitEntity;
import com.wanyue.common.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Address commit bean.
 */
public class AddressCommitBean extends CommitEntity {
    private int id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String area;
    private String address;
    private String cityInfo;
    private PccInfo pcc;
    private int isDefault;
    private String cityId;

    @Override
    public boolean observerCondition() {
        return fieldNotEmpty(name)&&fieldNotEmpty(phone)&&fieldNotEmpty(area)&&fieldNotEmpty(address);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
        observer();
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
        observer();
    }

    /**
     * Gets area.
     *
     * @return the area
     */
    public String getArea() {
        return area;
    }

    /**
     * Sets area.
     *
     * @param area the area
     */
    public void setArea(String area) {
        this.area = area;
        observer();
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets province.
     *
     * @return the province
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets province.
     *
     * @param province the province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
        observer();
    }

    /**
     * Gets city info.
     *
     * @return the city info
     */
    public String getCityInfo() {
        cityInfo= StringUtil.contact(province,",","\t",city,",","\t",area,"\t");
        return cityInfo;
    }


    /**
     * Gets pcc.
     *
     * @return the pcc
     */
    public PccInfo getPcc() {
        if(pcc==null){
            pcc=new PccInfo();
            pcc.setCity(city);
            pcc.setProvince(province);
            pcc.setDistrict(area);
            pcc.setCity_id(cityId);
         /*   pcc=StringUtil.contact("{","province:",province, "\t",
                    "city:",city, "\t",
                    "district:",area
                    ,"}"
                    );*//*
            Map<String,String>map=new HashMap<>();
            map.put("province",province);
            map.put("city",city);
            map.put("district",area);
            map.put("city_id",cityId);
            pcc= map;*/
        }
        return pcc;
    }


    /**
     * Gets is default.
     *
     * @return the is default
     */
    public int getIsDefault() {
        return isDefault;
    }


    /**
     * Gets city id.
     *
     * @return the city id
     */
    public String getCityId() {
        return cityId;
    }

    /**
     * Sets is default.
     *
     * @param isDefault the is default
     */
    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;

    }

    /**
     * Copy.
     *
     * @param addressInfoBean the address info bean
     */
    public void copy(AddressInfoBean addressInfoBean){
        id=addressInfoBean.getId();
        name=addressInfoBean.getName();
        phone=addressInfoBean.getPhone();
        province=addressInfoBean.getProvince();
        city=addressInfoBean.getCity();
        area=addressInfoBean.getArea();
        address=addressInfoBean.getAddress();
        isDefault=addressInfoBean.getIsDefault();
    }

    /**
     * Sets city id.
     *
     * @param cityId the city id
     */
    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

}
