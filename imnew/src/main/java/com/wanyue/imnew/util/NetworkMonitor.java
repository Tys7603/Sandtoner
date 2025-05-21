package com.wanyue.imnew.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

public class NetworkMonitor {

    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final ConnectivityManager.NetworkCallback networkCallback;

    public interface NetworkListener {
        void onNetworkAvailable();
        void onNetworkLost();
    }

    public NetworkMonitor(Context context, final NetworkListener listener) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                listener.onNetworkAvailable();
            }

            @Override
            public void onLost(@NonNull Network network) {
                listener.onNetworkLost();
            }
        };
    }

    public void startMonitoring() {
        NetworkRequest request = new
                NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    public void stopMonitoring() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
