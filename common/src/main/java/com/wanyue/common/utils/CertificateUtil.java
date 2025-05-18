package com.wanyue.common.utils;

import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class CertificateUtil {
    private static final String TAG = "CertificateUtil";

    /**
     * Lấy hash SHA-256 của certificate từ URL
     */
    public static String getCertificateHash(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            
            // Lấy certificate chain
            java.security.cert.Certificate[] certs = connection.getServerCertificates();
            if (certs == null || certs.length == 0) {
                Log.e(TAG, "Không tìm thấy certificate");
                return null;
            }

            // Lấy certificate đầu tiên (leaf certificate)
            X509Certificate cert = (X509Certificate) certs[0];
            
            // Tính hash SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] der = cert.getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            
            // Chuyển đổi sang hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return "sha256/" + hexString.toString();
            
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy certificate hash: " + e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra certificate có hợp lệ không
     */
    public static boolean isValidCertificate(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            
            // Lấy certificate chain
            java.security.cert.Certificate[] certs = connection.getServerCertificates();
            if (certs == null || certs.length == 0) {
                return false;
            }

            // Kiểm tra từng certificate trong chain
            for (java.security.cert.Certificate cert : certs) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    try {
                        x509Cert.checkValidity();
                    } catch (CertificateException e) {
                        Log.e(TAG, "Certificate không hợp lệ: " + e.getMessage());
                        return false;
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi kiểm tra certificate: " + e.getMessage());
            return false;
        }
    }
} 