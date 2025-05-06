package com.wanyue.common.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.main.R;
import com.wanyue.common.model.Country;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CountryUtils {

    public interface CountryFetchCallback {
        void onResult(List<Country> countries);
        void onError(Exception e);
    }

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
    
    public static void fetchCountriesFromApi(Context context, CountryFetchCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://system.sandtoner.com/api/region_codes")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(getCountries(context)));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(json);
                    JSONArray data = jsonObject.getJSONArray("data");
                    List<Country> countries = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        String name = obj.getString("region_english_name");
                        String code = obj.getString("region_three_code");
                        String dialCode = "+" + obj.getString("region_code");
                        int flagResId = getFlagResIdByRegionThreeCode(context, code);
                        String phoneRegex = getPhoneRegexByCode(code, dialCode);
                        countries.add(new Country(name, code, dialCode, flagResId, phoneRegex));
                    }
                    new Handler(Looper.getMainLooper()).post(() -> callback.onResult(countries));
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onResult(getCountries(context)));
                }
            }
        });
    }
    
    private static int getFlagResIdByRegionThreeCode(Context context, String regionThreeCode) {
        if (regionThreeCode == null) return 0;
        String resName = "flag_" + regionThreeCode.toLowerCase();
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }
    
    private static String getPhoneRegexByCode(String code, String dialCode) {
        switch (code) {
            case "VN": return "^(0|\\+84)(3|5|7|8|9)\\d{8}$";
            case "US": return "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$";
            case "CN": return "^(\\+?86)?1[3-9]\\d{9}$";
            case "JP": return "^(\\+?81|0)\\d{9,10}$";
            case "KR": return "^(\\+?82|0)?1[0-9]{1,2}[0-9]{7,8}$";
            case "SG": return "^(\\+?65)?[689]\\d{7}$";
            case "MY": return "^(\\+?60|0)?1[0-46-9][0-9]{7,8}$";
            case "TH": return "^(\\+?66|0)?[689]\\d{8}$";
            case "ID": return "^(\\+?62|0)8[1-9][0-9]{6,9}$";
            case "PH": return "^(\\+?63|0)?9[0-9]{9}$";
            case "IN": return "^(\\+?91|0)?[6789]\\d{9}$";
            case "GB": return "^(\\+?44|0)?7\\d{9}$";
            case "DE": return "^(\\+?49|0)?1[567]\\d{8,10}$";
            case "FR": return "^(\\+?33|0)?[67]\\d{8}$";
            case "AU": return "^(\\+?61|0)?4\\d{8}$";
            case "CA": return "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$";
            default: return ".*"; // fallback
        }
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