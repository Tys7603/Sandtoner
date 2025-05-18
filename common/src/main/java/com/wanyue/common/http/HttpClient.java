package com.wanyue.common.http;

import android.text.TextUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.BodyRequest;
import com.lzy.okgo.request.base.Request;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.CommonApplication;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.LanguageUtil;
import com.wanyue.common.utils.SystemUtil;
import com.wanyue.common.utils.CertificateUtil;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.CertificatePinner;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 2018/9/17.
 */

public class HttpClient {

    public static final String SALT = "123456";
    private static final int TIMEOUT = 10000;
    private static HttpClient sInstance;
    private OkHttpClient mOkHttpClient;

    public static final String mJsonContentType="application/json";
    public static final String mFormContentType="x-www-form-urlencoded";

    private HttpClient() {
    }
    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }

    private CertificatePinner createCertificatePinner() {
        CertificatePinner.Builder builder = new CertificatePinner.Builder();
        
        // Danh sách các domain cần pin certificate
        String[] domains = {
            "https://system.sandtoner.com",
            "https://cdn-za1.sandtoner.com",
            "https://cdn-za2.sandtoner.com"
        };
        
        // Lấy và thêm hash cho từng domain
        for (String domain : domains) {
            String hash = CertificateUtil.getCertificateHash(domain);
            if (hash != null) {
                String host = domain.replace("https://", "");
                builder.add(host, hash);
                L.e("Added certificate pin for " + host + ": " + hash);
            } else {
                L.e("Failed to get certificate hash for " + domain);
            }
        }
        
        return builder.build();
    }

    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        
        // Cấu hình timeout - tăng timeout cho kết nối đến Nam Phi
        builder.connectTimeout(15000, TimeUnit.MILLISECONDS);
        builder.readTimeout(15000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(15000, TimeUnit.MILLISECONDS);
        
        // Cấu hình DNS và CDN
        builder.dns(hostname -> {
            // Ưu tiên sử dụng CDN cho các domain chính
            if (hostname.contains("system.sandtoner.com")) {
                try {
                    // Thêm các CDN endpoint cho Nam Phi
                    return Arrays.asList(
                        InetAddress.getByName("cdn-za1.sandtoner.com"),
                        InetAddress.getByName("cdn-za2.sandtoner.com")
                    );
                } catch (Exception e) {
                    L.e("DNS resolution failed: " + e.getMessage());
                    return Arrays.asList(InetAddress.getAllByName(hostname));
                }
            }
            return Arrays.asList(InetAddress.getAllByName(hostname));
        });

        // Cấu hình cookie
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);

        // Cấu hình SSL/TLS
        try {
            // Tạo TrustManager
            TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Kiểm tra certificate chain
                        for (X509Certificate cert : chain) {
                            try {
                                cert.checkValidity();
                            } catch (CertificateException e) {
                                throw new CertificateException("Certificate không hợp lệ: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Kiểm tra certificate chain
                        for (X509Certificate cert : chain) {
                            try {
                                cert.checkValidity();
                            } catch (CertificateException e) {
                                throw new CertificateException("Certificate không hợp lệ: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
            };

            // Khởi tạo SSLContext với TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);

            // Tạo CertificatePinner
            CertificatePinner certificatePinner = createCertificatePinner();

            // Sử dụng TrustManager đã tạo
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                   .certificatePinner(certificatePinner);

        } catch (Exception e) {
            L.e("SSL/TLS configuration failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Logging cho debug mode
        if(SystemUtil.isApkInDebug(CommonApplication.sInstance)){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(getDataIns());
        }

        mOkHttpClient = builder.build();
        OkGo.getInstance().init(CommonApplication.sInstance)
                .setOkHttpClient(mOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(1);
    }

    public GetRequest<JsonBean> get(String serviceName, String tag) {
        GetRequest<JsonBean> getRequest=OkGo.<JsonBean>get(UrlMap.getUrl(serviceName))
                .tag(tag)
                .params("version", CommonAppConfig.APP_VERSION)
                .params("model", CommonAppConfig.SYSTEM_MODEL)
                .params("system", CommonAppConfig.SYSTEM_RELEASE);

        addDefaultHeader(getRequest);
        return getRequest;
    }

    public PostRequest<JsonBean> post(String serviceName, String tag) {
       PostRequest<JsonBean> postRequest=OkGo.<JsonBean>post(UrlMap.getUrl(serviceName))
                .tag(tag)
                .params("model", CommonAppConfig.SYSTEM_MODEL)
                .params("system", CommonAppConfig.SYSTEM_RELEASE)
               ;
        addDefaultHeader(postRequest);

        return postRequest;
    }

    public void cancel(String tag) {
        OkGo.cancelTag(mOkHttpClient,tag);
    }

    public static void addDefaultHeader(Request bodyRequest){
        // Header cơ bản
        bodyRequest.headers("Connection", "keep-alive");
        bodyRequest.headers("Content-Type", mJsonContentType);
        
        // Header bảo mật
        bodyRequest.headers("X-Content-Type-Options", "nosniff");
        bodyRequest.headers("X-Frame-Options", "DENY");
        bodyRequest.headers("X-XSS-Protection", "1; mode=block");
        bodyRequest.headers("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        
        // Token xác thực
        String token = CommonAppConfig.getToken();
        L.e("token=="+token);
        if(!TextUtils.isEmpty(token)){
            bodyRequest.headers("Authorization", "Bearer "+token);
        }
    }


    /**
     * @Description 网络拦截器
     **/
    private Interceptor getDataIns() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                okhttp3.Request request = chain.request();
                Response response = chain.proceed(request);
                ResponseBody responseBody = response.body();
                long contentLength = responseBody.contentLength();

                if (!bodyEncoded(response.headers())) {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    Charset charset = UTF8;
                    MediaType contentType = responseBody.contentType();
                    if (contentType != null) {
                        try {
                            charset = contentType.charset(UTF8);
                        } catch (UnsupportedCharsetException e) {
                            return response;
                        }
                    }
                    if (!isPlaintext(buffer)) {
                        return response;
                    }
                    if (contentLength != 0) {
                        String result = buffer.clone().readString(charset);
                        L.e("response.url():"+response.request().url());
                        L.e("response.body():"+result);
                        //得到所需的string，开始判断是否异常
                        //***********************do something*****************************
                    }
                }
                return response;
            }
        };
        return interceptor;
    }


    private static final Charset UTF8 = Charset.forName("UTF-8");

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }



}
