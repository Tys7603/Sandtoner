package com.wanyue.shop.view.activty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


import com.wanyue.shop.R;

public class PaymentWebViewActivity extends AppCompatActivity {
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view2);

        String url = getIntent().getStringExtra("payment_url");

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("WEBVIEW_URL", url);

                if (url.contains("/api/paystack/verify")) {
                    Uri uri = Uri.parse(url);
                    String trxref = uri.getQueryParameter("trxref");
                    String reference = uri.getQueryParameter("reference");

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("trxref", trxref);
                    resultIntent.putExtra("reference", reference);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    return true;
                }

                return false;
            }
        });
        webView.loadUrl(url);
    }
}
