package com.wanyue.main.view.activity;


import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.bean.DataListner;
import com.wanyue.common.business.TimeModel;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.ValidatePhoneUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;
import com.wanyue.main.R2;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.RegisterCommitBean;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.wanyue.common.utils.CountryUtils;
import com.wanyue.common.model.Country;
import com.wanyue.common.dialog.CountryPickerDialog;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.CommonAppConfig;

/**
 * The type Register activity.
 */
public class RegisterActivity extends BaseActivity implements TimeModel.TimeListner {
    /**
     * The constant REGISTER.
     */
    public static final int REGISTER=1;
    /**
     * The M tv phone.
     */
    @BindView(R2.id.tv_phone)
    EditText mTvPhone;
    /**
     * The M tv code.
     */
    @BindView(R2.id.tv_code)
    EditText mTvCode;
    /**
     * The M btn get code.
     */
    @BindView(R2.id.btn_get_code)
    TextView mBtnGetCode;
    /**
     * The M tv pwd.
     */
    @BindView(R2.id.tv_pwd)
    EditText mTvPwd;
    /**
     * The M btn commit.
     */
    @BindView(R2.id.btn_commit)
    TextView mBtnCommit;
    /**
     * The M tv title.
     */
    @BindView(R2.id.tv_title)
    TextView mTvTitle;

    private TimeModel mTimeModel;
    /**
     * The M register commit bean.
     */
    RegisterCommitBean mRegisterCommitBean;

    private LinearLayout countryPickerContainer;
    private TextView tvCountryCode;

    private Country mSelectedCountry;
    private String selectedRegionCode = "86";

    @Override
    public void init() {
        mRegisterCommitBean = new RegisterCommitBean();
        mRegisterCommitBean.setDataListner(new DataListner() {
            @Override
            public void compelete(boolean isCompelete) {
                mBtnCommit.setEnabled(isCompelete);
            }
        });

        countryPickerContainer = findViewById(R.id.country_picker_container);
        tvCountryCode = findViewById(R.id.tv_country_code);

        // Set default country to South Africa (ZAF)
        mSelectedCountry = CountryUtils.getCountryByCode(this, "ZAF");
        if (mSelectedCountry != null) {
            updateCountryUI(mSelectedCountry);
            selectedRegionCode = mSelectedCountry.getRegionCodeNumber();
            mRegisterCommitBean.setSelectedCountry(mSelectedCountry);
        }

        countryPickerContainer.setOnClickListener(v -> {
            CountryPickerDialog dialog = new CountryPickerDialog(this, country -> {
                mSelectedCountry = country;
                updateCountryUI(mSelectedCountry);
                selectedRegionCode = mSelectedCountry.getRegionCodeNumber();
                mRegisterCommitBean.setSelectedCountry(mSelectedCountry);
            });
            dialog.show();
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    /**
     * Commit.
     *
     * @param view the view
     */
    @OnClick(R2.id.btn_commit)
    public void commit (View view){
        if(mRegisterCommitBean==null){
            return;
        }
        commit();
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }

    /**
     * Watch spread code.
     *
     * @param sequence the sequence
     * @param start    the start
     * @param before   the before
     * @param count    the count
     */
    /*监听推广码号输入框信息*/
    @OnTextChanged(value = R2.id.tv_spread_code, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchSpreadCode(CharSequence sequence, int start, int before, int count){
        String codeString=sequence.toString();
        mRegisterCommitBean.setSpread(codeString);
    }

    /**
     * Commit.
     */
    protected void commit() {
        if(!ClickUtil.canClick()){
            return;
        }
        MainAPI.register(mRegisterCommitBean).subscribe(new DialogObserver<Boolean>(this) {
            @Override
            public void onNextTo(Boolean aBoolean) {
                if(aBoolean){
                    // Sau khi đăng ký thành công, tự động đăng nhập
                    String phone = mRegisterCommitBean.getPhone();
                    String pwd = mRegisterCommitBean.getPwd();
                    MainAPI.loginByCode(phone, mRegisterCommitBean.getCode(), new ParseHttpCallback<JSONObject>() {
                        @Override
                        public void onSuccess(int code, String msg, JSONObject info) {
                            if (isSuccess(code)) {
                                String token = info.getString("token");
                                String sign = info.getString("usersig");
                                CommonAppConfig.setLoginInfo("0", token, true);
                                SpUtil.getInstance().setStringValue(SpUtil.TX_IM_USER_SIGN, sign);
                                
                                Intent intent = getIntent();
                                intent.putExtra(Constants.DATA, phone);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                ToastUtil.show(msg);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtil.show(e.getMessage());
                        }
                    });
                }
            }
        });
    }


    /**
     * Gets login code.
     */
    @OnClick(R2.id.btn_get_code)
    public void getLoginCode() {
        final String phoneNum = mTvPhone.getText().toString();
        if (mSelectedCountry == null) {
            ToastUtil.show("Please select country");
            return;
        }
        if (!mSelectedCountry.isValidPhoneNumber(phoneNum)) {
            ToastUtil.show("Invalid phone number for " + mSelectedCountry.getName());
            return;
        }
        MainAPI.getVerifyKey(new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if (info != null) {
                    String key = info.getString("key");
                    sendVerificationCode(phoneNum, mSelectedCountry.getRegionCodeNumber(), key);
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

    private void sendVerificationCode(String phone, String regionCode, String key) {
        MainAPI.getVerifyCode(phone, "register", key, new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                ToastUtil.show(msg);
                if (isSuccess(code)) {
                    getRegisterCodeSucc(code, msg, info);
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

    private void getRegisterCodeSucc(int code, String msg, JSONObject info) {
        if(code!=200){
            return;
        }

        initTimeModel();
        mTimeModel.start();
        mBtnGetCode.setEnabled(false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimeModel!=null){
            mTimeModel.clear();
        }
    }

    /**
     * Watch phone text change.
     *
     * @param sequence the sequence
     * @param start    the start
     * @param before   the before
     * @param count    the count
     */
    /*监听手机号输入框信息*/
    @OnTextChanged(value = R2.id.tv_phone, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchPhoneTextChange(CharSequence sequence, int start, int before, int count){
        String phoneString = sequence.toString();
        mRegisterCommitBean.setPhone(phoneString);

        boolean isValid = false;
        if (mSelectedCountry != null && !phoneString.isEmpty()) {
            isValid = mSelectedCountry.isValidPhoneNumber(phoneString);
        }
        mBtnGetCode.setEnabled(isValid);
        if (!isValid && !phoneString.isEmpty()) {
            mTvPhone.setError("Invalid phone number for " + (mSelectedCountry != null ? mSelectedCountry.getName() : ""));
        } else {
            mTvPhone.setError(null);
        }
    }

    /**
     * Watch code text change.
     *
     * @param sequence the sequence
     * @param start    the start
     * @param before   the before
     * @param count    the count
     */
    /*监听验证码号输入框信息*/
    @OnTextChanged(value = R2.id.tv_code, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchCodeTextChange(CharSequence sequence, int start, int before, int count){
        String codeString=sequence.toString();
        mRegisterCommitBean.setCode(codeString);
    }

    /**
     * Watch pwd text change.
     *
     * @param sequence the sequence
     * @param start    the start
     * @param before   the before
     * @param count    the count
     */
    /*监听密码输入框信息*/
    @OnTextChanged(value = R2.id.tv_pwd, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void watchPwdTextChange(CharSequence sequence, int start, int before, int count){
        String pwdString=sequence.toString();
        mRegisterCommitBean.setPwd(pwdString);
    }

    private void initTimeModel() {
        if(mTimeModel==null){
            mTimeModel=new TimeModel()
                    .setTotalUseTime(60)
                    .setState(TimeModel.COUNT_DOWN)
                    .setAfterString("s");
            mTimeModel.addTimeListner(this);
        }
    }

    @Override
    public void time(String string) {
        mBtnGetCode.setText(string);
    }

    @Override
    public void compelete() {
        mBtnGetCode.setEnabled(true);
        mBtnGetCode.setText(R.string.reg_get_code);
    }

    private void updateCountryUI(Country country) {
        if (country != null) {
            tvCountryCode.setText(country.getDialCode());
        }
    }
}
