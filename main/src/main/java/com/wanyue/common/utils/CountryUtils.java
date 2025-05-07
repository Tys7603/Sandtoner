package com.wanyue.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.model.Country;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CountryUtils {

    private static final String API_URL = "https://system.sandtoner.com/api/region_codes";
    private static List<Country> cachedCountries = null;
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    public interface CountryFetchCallback {
        void onResult(List<Country> countries);
        void onError(Exception e);
    }

    public static void fetchCountriesFromApi(Context context, CountryFetchCallback callback) {
        // Return cached countries if available
        if (cachedCountries != null) {
            callback.onResult(cachedCountries);
            return;
        }

        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(json);
                    JSONArray data = jsonObject.getJSONArray("data");
                    List<Country> countries = new ArrayList<>();
                    Country southAfrica = null;

                    // First pass: find South Africa and process other countries
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        String name = obj.getString("region_english_name");
                        String code = obj.getString("region_three_code");
                        String dialCode = "+" + obj.getString("region_code");
                        int flagResId = getFlagResIdByRegionThreeCode(context, code);
                        String phoneRegex = getPhoneRegexByCode(code, dialCode);

                        Country country = new Country(name, code, dialCode, flagResId, phoneRegex);

                        if ("ZAF".equals(code)) {
                            southAfrica = country;
                        } else {
                            countries.add(country);
                        }
                    }

                    // Add South Africa at the beginning if found
                    if (southAfrica != null) {
                        countries.add(0, southAfrica);
                    }

                    // Cache the countries list
                    cachedCountries = countries;

                    new Handler(Looper.getMainLooper()).post(() -> callback.onResult(countries));
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
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
        // Default regex pattern for phone numbers
        return "^(\\+?" + dialCode.substring(1) + ")?[0-9]{8,15}$";
    }

    public static Country getCountryByCode(Context context, String code) {
        if (cachedCountries == null) {
            // If no cached countries, return a default country
            return new Country("South Africa", "ZAF", "+27", 
                    getFlagResIdByRegionThreeCode(context, "ZAF"),
                    "^(\\+?27)?[0-9]{9}$");
        }

        for (Country country : cachedCountries) {
            if (country.getCode().equalsIgnoreCase(code)) {
                return country;
            }
        }
        return cachedCountries.get(0); // Return first country if not found
    }

    public static Country getCountryByDialCode(Context context, String dialCode) {
        if (cachedCountries == null) {
            // If no cached countries, return a default country
            return new Country("South Africa", "ZAF", "+27", 
                    getFlagResIdByRegionThreeCode(context, "ZAF"),
                    "^(\\+?27)?[0-9]{9}$");
        }

        for (Country country : cachedCountries) {
            if (country.getDialCode().equals(dialCode)) {
                return country;
            }
        }
        return cachedCountries.get(0); // Return first country if not found
    }

    // Method to clear cache if needed
    public static void clearCache() {
        cachedCountries = null;
    }
}