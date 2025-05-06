package com.wanyue.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wanyue.main.R;
import com.wanyue.common.adapter.CountryAdapter;
import com.wanyue.common.model.Country;
import com.wanyue.common.utils.CountryUtils;

import java.util.List;

public class CountryPickerDialog extends Dialog {

    private Context context;
    private CountryAdapter adapter;
    private CountryAdapter.OnCountrySelectedListener listener;

    public CountryPickerDialog(@NonNull Context context, CountryAdapter.OnCountrySelectedListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_country_picker);

        RecyclerView recyclerView = findViewById(R.id.rv_countries);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        android.view.View progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) progressBar.setVisibility(android.view.View.VISIBLE);

        CountryUtils.fetchCountriesFromApi(context, new CountryUtils.CountryFetchCallback() {
            @Override
            public void onResult(List<Country> countries) {
                adapter = new CountryAdapter(context, countries, country -> {
                    if (listener != null) {
                        listener.onCountrySelected(country);
                    }
                    dismiss();
                });
                recyclerView.setAdapter(adapter);
                if (progressBar != null) progressBar.setVisibility(android.view.View.GONE);
            }
            @Override
            public void onError(Exception e) {
                if (progressBar != null) progressBar.setVisibility(android.view.View.GONE);
            }
        });

        EditText etSearch = findViewById(R.id.et_search_country);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}