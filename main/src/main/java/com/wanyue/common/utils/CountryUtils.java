package com.wanyue.common.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.wanyue.main.R;
import com.wanyue.common.model.Country;

import java.util.ArrayList;
import java.util.List;

public class CountryUtils {

    public static List<Country> getCountries(Context context) {
        List<Country> countries = new ArrayList<>();
        
        countries.add(new Country("Việt Nam", "VN", "+84", R.drawable.flag_vn, "^(0|\\+84)(3|5|7|8|9)\\d{8}$"));
        countries.add(new Country("United States", "US", "+1", R.drawable.flag_us, "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$"));
        countries.add(new Country("China", "CN", "+86", R.drawable.flag_cn, "^(\\+?86)?1[3-9]\\d{9}$"));
        countries.add(new Country("Japan", "JP", "+81", R.drawable.flag_jp, "^(\\+?81|0)\\d{9,10}$"));
        countries.add(new Country("South Korea", "KR", "+82", R.drawable.flag_kr, "^(\\+?82|0)?1[0-9]{1,2}[0-9]{7,8}$"));
        countries.add(new Country("Singapore", "SG", "+65", R.drawable.flag_sg, "^(\\+?65)?[689]\\d{7}$"));
        countries.add(new Country("Malaysia", "MY", "+60", R.drawable.flag_my, "^(\\+?60|0)?1[0-46-9][0-9]{7,8}$"));
        countries.add(new Country("Thailand", "TH", "+66", R.drawable.flag_th, "^(\\+?66|0)?[689]\\d{8}$"));
        countries.add(new Country("Indonesia", "ID", "+62", R.drawable.flag_id, "^(\\+?62|0)8[1-9][0-9]{6,9}$"));
        countries.add(new Country("Philippines", "PH", "+63", R.drawable.flag_ph, "^(\\+?63|0)?9[0-9]{9}$"));
        countries.add(new Country("India", "IN", "+91", R.drawable.flag_in, "^(\\+?91|0)?[6789]\\d{9}$"));
        countries.add(new Country("United Kingdom", "GB", "+44", R.drawable.flag_gb, "^(\\+?44|0)?7\\d{9}$"));
        countries.add(new Country("Germany", "DE", "+49", R.drawable.flag_de, "^(\\+?49|0)?1[567]\\d{8,10}$"));
        countries.add(new Country("France", "FR", "+33", R.drawable.flag_fr, "^(\\+?33|0)?[67]\\d{8}$"));
        countries.add(new Country("Australia", "AU", "+61", R.drawable.flag_au, "^(\\+?61|0)?4\\d{8}$"));
        countries.add(new Country("Canada", "CA", "+1", R.drawable.flag_ca, "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$"));

        return countries;
    }
    
    public static Country getCountryByCode(Context context, String code) {
        List<Country> countries = getCountries(context);
        for (Country country : countries) {
            if (country.getCode().equalsIgnoreCase(code)) {
                return country;
            }
        }
        return countries.get(0);
    }
    
    public static Country getCountryByDialCode(Context context, String dialCode) {
        List<Country> countries = getCountries(context);
        for (Country country : countries) {
            if (country.getDialCode().equals(dialCode)) {
                return country;
            }
        }
        return countries.get(0);
    }
}