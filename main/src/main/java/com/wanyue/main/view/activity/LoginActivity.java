package com.wanyue.main.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wanyue.common.dialog.CountryPickerDialog;
import com.wanyue.common.model.Country;
import com.wanyue.common.utils.CountryUtils;
import com.wanyue.main.R;
import com.wanyue.main.view.activity.MainActivity;

import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout countryPickerContainer;
    private ImageView imgCountryFlag;
    private TextView tvCountryCode;
    private EditText tvPhone;
    private EditText tvCode;
    private Button btnLogin;
    private TextView btnGetCode;
    
    private Country selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initView();
        initData();
        initListener();
    }
    
    private void initView() {
        countryPickerContainer = findViewById(R.id.country_picker_container);
        imgCountryFlag = findViewById(R.id.img_country_flag);
        tvCountryCode = findViewById(R.id.tv_country_code);
        tvPhone = findViewById(R.id.tv_phone);
        tvCode = findViewById(R.id.tv_code);
        btnLogin = findViewById(R.id.btn_login);
        btnGetCode = findViewById(R.id.btn_get_code);
    }
    
    private void initData() {
        selectedCountry = CountryUtils.getCountryByCode(this, "CN");
        updateCountryUI(selectedCountry);
    }
    
    private void initListener() {
        countryPickerContainer.setOnClickListener(v -> showCountryPickerDialog());
        
        tvPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhoneNumber();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        tvCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        btnGetCode.setOnClickListener(v -> {
            if (validatePhoneNumber()) {
                requestVerificationCode();
            }
        });
        
        btnLogin.setOnClickListener(v -> {
            if (validateForm()) {
                login();
            }
        });
    }
    
    private void showCountryPickerDialog() {
        CountryPickerDialog dialog = new CountryPickerDialog(this, country -> {
            selectedCountry = country;
            updateCountryUI(country);
            validatePhoneNumber();
        });
        dialog.show();
    }
    
    private void updateCountryUI(Country country) {
        if (country != null) {
            imgCountryFlag.setImageResource(country.getFlagResId());
            tvCountryCode.setText(country.getDialCode());
        }
    }
    
    private boolean validatePhoneNumber() {
        String phoneNumber = tvPhone.getText().toString().trim();
        boolean isValid = false;
        
        if (!TextUtils.isEmpty(phoneNumber) && selectedCountry != null) {
            isValid = selectedCountry.isValidPhoneNumber(phoneNumber);
        }
        
        btnGetCode.setEnabled(isValid);
        
        if (!TextUtils.isEmpty(phoneNumber) && !isValid) {
            tvPhone.setError("Invalid phone number for country " + selectedCountry.getName());
        } else {
            tvPhone.setError(null);
        }
        
        validateForm();
        return isValid;
    }
    
    private boolean validateForm() {
        String phoneNumber = tvPhone.getText().toString().trim();
        String code = tvCode.getText().toString().trim();
        
        boolean isPhoneValid = !TextUtils.isEmpty(phoneNumber) && selectedCountry != null && 
                               selectedCountry.isValidPhoneNumber(phoneNumber);
        boolean isCodeValid = !TextUtils.isEmpty(code) && code.length() >= 4;
        
        btnLogin.setEnabled(isPhoneValid && isCodeValid);
        
        return isPhoneValid && isCodeValid;
    }
    
    private void requestVerificationCode() {
        // TODO: Gửi yêu cầu mã xác nhận đến số điện thoại
        // Đây là nơi bạn sẽ gọi API để gửi SMS chứa mã xác nhận
        
        // Giả lập đếm ngược thời gian chờ
        btnGetCode.setEnabled(false);
        btnGetCode.setText("60s");
        
        Toast.makeText(this, "A confirmation code has been sent to your phone number", Toast.LENGTH_SHORT).show();
    }
    
    private void login() {
        String phoneNumber = tvPhone.getText().toString().trim();
        String code = tvCode.getText().toString().trim();
        
        Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
        
        // Sửa lại cách khai báo Intent với package name đầy đủ
        Intent intent = new Intent();
        intent.setClassName(this, "com.wanyue.main.view.activity.MainActivity");
        // Hoặc nếu MainActivity nằm trong package khác, thay đổi package name tương ứng
        
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
