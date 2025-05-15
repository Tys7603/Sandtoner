package com.wanyue.main.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.Constants;
import com.wanyue.common.HtmlConfig;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.bean.ConfigBean;
import com.wanyue.common.bean.DataListner;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.business.TimeModel;
import com.wanyue.common.business.acmannger.ActivityMannger;
import com.wanyue.common.dialog.CountryPickerDialog;
import com.wanyue.common.http.BaseHttpCallBack;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.interfaces.OnItemClickListener;
import com.wanyue.common.mob.LoginData;
import com.wanyue.common.mob.MobBean;
import com.wanyue.common.mob.MobCallback;
import com.wanyue.common.mob.MobLoginUtil;
import com.wanyue.common.model.Country;
import com.wanyue.common.utils.CountryUtils;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.ValidatePhoneUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.live.activity.LiveAnchorActivity;
import com.wanyue.main.R;
import com.wanyue.main.R2;
import com.wanyue.main.adapter.LoginTypeAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.LoginCommitBean;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Route(path = RouteUtil.PATH_LOGIN)
public class LoginActivity extends BaseActivity implements TimeModel.TimeListner, OnItemClickListener<MobBean> {

    @BindView(R2.id.country_picker_container)
    LinearLayout mCountryPickerContainer;
    
    @BindView(R2.id.tv_country_code)
    TextView mTvCountryCode;

    @BindView(R2.id.tv_phone)
    EditText mTvPhone;
    @BindView(R2.id.tv_code)
    EditText mTvCode;
    @BindView(R2.id.btn_get_code)
    TextView mBtnGetCode;
    @BindView(R2.id.btn_login)
    Button mBtnLogin;
    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R2.id.btn_tip)
    TextView mBtnTip;
    @BindView(R2.id.tip_group)
    LinearLayout mTipGroup;
    @BindView(R2.id.img_launcher)
    ImageView mImgLauncher;

    private CheckBox mCheckboxPrivacy;
    private TextView mTvPrivacyText;
    private FrameLayout mPrivacyPolicyContainer;
    private CheckBox mCheckboxAgree;
    private Button mBtnClosePrivacy;
    private ScrollView mScrollPrivacy;

    private TimeModel mTimeModel;
    private LoginCommitBean mLoginCommitBean;
    private MobLoginUtil mLoginUtil;
    private Country mSelectedCountry;

    private LinearLayout countryPickerContainer;
    private TextView tvCountryCode;
    private ImageView imgCountryFlag;
    private EditText tvPhone, tvCode;
    private TextView btnGetCode;
    private Button btnLogin;

    private String selectedRegionCode = "86";
    private String selectedCountryFlag = "flag_cn";

    private static final long CLICK_DEBOUNCE_TIME = 1000; // 1 second debounce time
    private long mLastClickTime = 0;

    private boolean hasReadCompletePrivacy = false;

    @Override
    public void init() {
        mBtnTip.setText(getString(R.string.login_tip_2));
        initCommitData();
        initThirdData();
        mImgLauncher.setImageResource(CommonAppConfig.getAppIconRes());

        mSelectedCountry = CountryUtils.getCountryByCode(this, "ZAF");
        if (mSelectedCountry != null) {
            updateCountryUI(mSelectedCountry);
            selectedRegionCode = mSelectedCountry.getRegionCodeNumber();
        }

        countryPickerContainer = findViewById(R.id.country_picker_container);
        tvCountryCode = findViewById(R.id.tv_country_code);
        tvPhone = findViewById(R.id.tv_phone);
        tvCode = findViewById(R.id.tv_code);
        btnGetCode = findViewById(R.id.btn_get_code);
        btnLogin = findViewById(R.id.btn_login);

        countryPickerContainer.setOnClickListener(v -> {
            CountryPickerDialog dialog = new CountryPickerDialog(this, country -> {
                mSelectedCountry = country;
                updateCountryUI(mSelectedCountry);
                selectedRegionCode = mSelectedCountry.getRegionCodeNumber();
            });
            dialog.show();
        });

        btnGetCode.setOnClickListener(v -> {
            String phone = tvPhone.getText().toString().trim();
            if (mSelectedCountry == null) {
                ToastUtil.show("Please select country");
                return;
            }
            if (mSelectedCountry != null && !mSelectedCountry.isValidPhoneNumber(phone)) {
                ToastUtil.show("Invalid phone number for " + mSelectedCountry.getName());
                return;
            }
            MainAPI.getVerifyKey(new ParseHttpCallback<JSONObject>() {
                @Override
                public void onSuccess(int code, String msg, JSONObject info) {
                    if (info != null) {
                        String key = info.getString("key");
                        sendVerificationCode(phone, mSelectedCountry.getRegionCodeNumber(), key);
                    } else {
                        ToastUtil.show(msg);
                    }
                }
                @Override
                public void onError(Throwable e) {
                    if (e != null) {
                        ToastUtil.show(e.getMessage());
                    }
                }
            });
        });

        tvPhone.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(15) });

        // Initialize the privacy controls using findViewById
        mCheckboxPrivacy = findViewById(R.id.checkbox_privacy);
        mTvPrivacyText = findViewById(R.id.tv_privacy_text);
        mPrivacyPolicyContainer = findViewById(R.id.privacy_policy_container);
        mCheckboxAgree = findViewById(R.id.checkbox_agree);
        mBtnClosePrivacy = findViewById(R.id.btn_close_privacy);
        mScrollPrivacy = findViewById(R.id.scroll_privacy);

        // Set up the privacy policy checkbox and dialog
        setupPrivacyPolicy();
    }

    private void initCommitData() {
        mLoginCommitBean = new LoginCommitBean();
        mLoginCommitBean.setDataListner(isCompelete -> mBtnLogin.setEnabled(isCompelete));
    }

    private void initThirdData() {
        List<MobBean> list = MobBean.getLoginTypeList(Constants.MOB_WX);
        if (ListUtil.haveData(list)) {
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            LoginTypeAdapter adapter = new LoginTypeAdapter(mContext, list);
            adapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(adapter);
            mLoginUtil = new MobLoginUtil();
        }
    }

    private void updateCountryUI(Country country) {
        if (country != null) {

            mTvCountryCode.setText(country.getDialCode());
        }
    }

    @OnTextChanged(value = R2.id.tv_phone, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchPhoneTextChange(CharSequence sequence, int start, int before, int count) {
        String phoneString = sequence.toString();
        mLoginCommitBean.setPhoneString(phoneString);
        validatePhoneNumber();
    }

    @OnTextChanged(value = R2.id.tv_code, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchCodeTextChange(CharSequence sequence, int start, int before, int count) {
        String codeString = sequence.toString();
        mLoginCommitBean.setCheckString(codeString);
        validatePhoneNumber();
    }

    @OnClick(R2.id.btn_get_code)
    public void getCode() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime < CLICK_DEBOUNCE_TIME) {
            return; // Ignore rapid clicks
        }
        mLastClickTime = currentTime;

        final String phoneNum = mTvPhone.getText().toString();
        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
            mTvPhone.setError(WordUtil.getString(R.string.login_phone_error));
            mTvPhone.requestFocus();
            return;
        }
        MainAPI.getVerifyKey(new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if (info != null) {
                    String key = info.getString("key");
                    getVerifyCode(phoneNum, key);
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e != null) {
                    ToastUtil.show(e.getMessage());
                }
            }
        });
    }

    private void getVerifyCode(String phoneNum, String key) {
        MainAPI.getVerifyCode(phoneNum, "login", key, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
                if (isSuccess(code)) {
                    getLoginCodeSucc();
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }

    private void getLoginCodeSucc() {
        initTimeModel();
        mTimeModel.start();
        mBtnGetCode.setEnabled(false);
    }

    @OnClick(R2.id.btn_register)
    public void toRegister() {
        startActivityForResult(RegisterActivity.class, RegisterActivity.REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RegisterActivity.REGISTER && resultCode == RESULT_OK) {
            String phone = data.getStringExtra(Constants.DATA);
            mTvPhone.setText(phone);
        }
    }

    @OnClick(R2.id.btn_login)
    public void login() {
        if (mLoginCommitBean == null) {
            return;
        }
        String phoneString = mLoginCommitBean.getPhoneString();
        String codeString = mLoginCommitBean.getCheckString();

        MainAPI.loginByCode(phoneString, codeString, mParseHttpCallback);
    }

    private ParseHttpCallback<JSONObject> mParseHttpCallback = new ParseHttpCallback<JSONObject>() {
        @Override
        public void onSuccess(int code, String msg, JSONObject info) {
            if (isSuccess(code)) {
                ToastUtil.show(R.string.login_auth_success);
                loginingSucc(info);
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public void onError(Throwable e) {
            if (e != null) {
                ToastUtil.show(e.getMessage());
            }
        }
    };

    private void loginingSucc(JSONObject jsonObject) {
        Log.d("longgg", "loginingSucc: " + jsonObject);
        String token = jsonObject.getString("token");
        CommonAppConfig.setLoginInfo("0", token, true);
        String sign = jsonObject.getString("usersig");
        Log.d("longgg", "sign: " + sign);
        SpUtil.getInstance().setStringValue(SpUtil.TX_IM_USER_SIGN, sign);
        askUserInfo();
    }

    private void askUserInfo() {
        MainAPI.getBaseInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (BaseHttpCallBack.isSuccess(code) && info.length > 0) {
                    String json = info[0];
                    UserBean userBean = JSON.parseObject(json, UserBean.class);
                    CommonAppConfig.setUserBean(userBean, json);
                    Activity activity = ActivityMannger.getInstance().getBaseActivity();
                    if (activity == null) {
                        startActivity(MainActivity.class);
                    }
                    finish();
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }

    private void initTimeModel() {
        if (mTimeModel == null) {
            mTimeModel = new TimeModel()
                    .setTotalUseTime(60)
                    .setState(TimeModel.COUNT_DOWN)
                    .setAfterString("s Re-acquire after");
            mTimeModel.addTimeListner(this);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimeModel != null) {
            mTimeModel.clear();
        }
    }

    private long mLastClickBackTime;

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }
        ActivityMannger.getInstance().clearAllActivity();
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }

    @Override
    public void time(String string) {
        mBtnGetCode.setText(string);
    }

    @Override
    public void compelete() {
        mBtnGetCode.setEnabled(true);
        mBtnGetCode.setText(R.string.login_get_code);
    }

    @Override
    public void onItemClick(final MobBean bean, int position) {
        if (mLoginUtil == null) {
            return;
        }
        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
        dialog.show();
        mLoginUtil.execute(bean.getType(), new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    loginByThird(bean.getType(), (LoginData) data);
                }
            }

            @Override
            public void onError() {
                disMissLoadingDialog(dialog);
            }

            @Override
            public void onCancel() {
                disMissLoadingDialog(dialog);
            }

            @Override
            public void onFinish() {
                disMissLoadingDialog(dialog);
            }
        });
    }

    private void disMissLoadingDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @OnClick(R2.id.btn_tip)
    public void clickTip() {
        WebViewActivity.forward(this, HtmlConfig.LOGIN_PRIVCAY, false);
    }

    private void loginByThird(String loginType, LoginData data) {
        MainAPI.loginByThird(data, mParseHttpCallback);
    }

    private void fetchRegionCodes() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url("https://system.sandtoner.com/api/region_codes")
            .get()
            .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    ToastUtil.show("Get list of failed area codes: " + e.getMessage());
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = JSON.parseObject(json);
                        int status = jsonObject.getIntValue("status");
                        String msg = jsonObject.getString("msg");
                        if (status == 200) {
                            ToastUtil.show("Get list of area codes successfully");
                            // Có thể parse và cập nhật UI ở đây nếu cần
                            // JSONArray data = jsonObject.getJSONArray("data");
                            // Log.d("RegionCodes", data.toJSONString());
                        } else {
                            ToastUtil.show("Get list of failed area codes: " + msg);
                        }
                    } catch (Exception ex) {
                        ToastUtil.show("Error processing area code response: " + ex.getMessage());
                    }
                });
            }
        });
    }

    private void updateRegionCode(String regionCode, String flagResource) {
        runOnUiThread(() -> {
            tvCountryCode.setText("+" + regionCode);
            selectedRegionCode = regionCode;
            int resId = getResources().getIdentifier(flagResource, "drawable", getPackageName());
            if (resId != 0) imgCountryFlag.setImageResource(resId);
        });
    }

    private void validatePhoneNumber() {
        String phoneNumber = tvPhone.getText().toString().trim();
        boolean isValid = false;
        if (!TextUtils.isEmpty(phoneNumber) && mSelectedCountry != null) {
            isValid = mSelectedCountry.isValidPhoneNumber(phoneNumber);
        }
        btnGetCode.setEnabled(isValid);
        String code = tvCode.getText().toString().trim();

        // Điều kiện để enable checkbox privacy
        boolean canEnablePrivacy = isValid && !TextUtils.isEmpty(code);
        mCheckboxPrivacy.setEnabled(canEnablePrivacy);
        if (!canEnablePrivacy) {
            mCheckboxPrivacy.setChecked(false);
        }

        // Điều kiện để enable nút login
        boolean isPrivacyChecked = mCheckboxPrivacy.isChecked();
        btnLogin.setEnabled(isValid && !TextUtils.isEmpty(code) && isPrivacyChecked);

        if (!TextUtils.isEmpty(phoneNumber) && !isValid) {
            tvPhone.setError("Invalid phone number for " + mSelectedCountry.getName());
        } else {
            tvPhone.setError(null);
        }
    }

    private void sendVerificationCode(String phone, String regionCode, String key) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
            .add("phone", phone)
            .add("type", "login")
            .add("key", key)
            .add("region_code", regionCode)
            .build();
        Request request = new Request.Builder()
            .url("https://system.sandtoner.com/api/register/verify")
            .post(formBody)
            .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    ToastUtil.show("Send verification code failed: " + e.getMessage());
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        ToastUtil.show("Server error: " + response.code());
                    });
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody == null) {
                    runOnUiThread(() -> {
                        ToastUtil.show("Empty response from server");
                    });
                    return;
                }

                Log.d("SMS_API", "Response: " + responseBody);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = JSON.parseObject(responseBody);
                        if (jsonObject == null) {
                            ToastUtil.show("Invalid JSON response from server");
                            return;
                        }

                        int status = jsonObject.getIntValue("status");
                        String msg = jsonObject.getString("msg");
                        
                        if (status == 200) {
                            String code = "";
                            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)$").matcher(msg);
                            if (matcher.find()) {
                                code = matcher.group(1);
                                ToastUtil.show("Verification code sent successfully: " + code);
                            } else {
                                ToastUtil.show("Verification code sent successfully, but code not found in msg!");
                            }
                            getLoginCodeSucc(); // Start countdown timer
                        } else {
                            ToastUtil.show("Failed to send verification code: " + msg);
                        }
                    } catch (Exception ex) {
                        Log.e("SMS_API", "Error parsing response", ex);
                        ToastUtil.show("Error processing server response");
                    }
                });
            }
        });
    }

    private void setupPrivacyPolicy() {
        // Set Login button disabled by default
        mBtnLogin.setEnabled(false);
        
        // Add click listener to privacy policy text to show the dialog
        mTvPrivacyText.setOnClickListener(v -> {
            showPrivacyPolicyDialog();
        });
        
        // Add click listener to privacy checkbox
        mCheckboxPrivacy.setOnClickListener(v -> {
            if (mCheckboxPrivacy.isChecked()) {
                showPrivacyPolicyDialog();
            } else {
                // If unchecked, disable login button
                validatePhoneNumber();
            }
        });
        
        // Add listener for the agreement checkbox
        mCheckboxAgree.setOnClickListener(v -> {
            if (mCheckboxAgree.isChecked()) {
                // User has agreed, validate phone to potentially enable login
                validatePhoneNumber();
            }
        });
        
        // Set up the privacy policy dialog
        mBtnClosePrivacy.setOnClickListener(v -> {
            mPrivacyPolicyContainer.setVisibility(View.GONE);
            // Không tự động check mCheckboxPrivacy, chỉ đóng dialog
        });
        
        // Improved scroll listener: enable/disable checkbox based on scroll position
        mScrollPrivacy.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = mScrollPrivacy.getChildAt(0);
                if (view != null) {
                    int diff = view.getBottom() - (mScrollPrivacy.getHeight() + mScrollPrivacy.getScrollY());
                    if (diff <= 10) { // Allow a small threshold
                        hasReadCompletePrivacy = true;
                        mCheckboxAgree.setEnabled(true);
                    } else {
                        hasReadCompletePrivacy = false;
                        mCheckboxAgree.setEnabled(false);
                        mCheckboxAgree.setChecked(false);
                    }
                }
            }
        });
        
        // Disable agree checkbox initially until user scrolls to the bottom
        mCheckboxAgree.setEnabled(false);
    }
    
    private void showPrivacyPolicyDialog() {
        mPrivacyPolicyContainer.setVisibility(View.VISIBLE);
        // Reset scroll position
        mScrollPrivacy.scrollTo(0, 0);
        // Reset flag and checkbox
        hasReadCompletePrivacy = false;
        mCheckboxAgree.setChecked(false);
        mCheckboxAgree.setEnabled(false);
    }
}
