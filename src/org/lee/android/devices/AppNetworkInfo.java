package org.lee.android.devices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppNetworkInfo {

	public static boolean isNetworkAvailable(Context context) {
		boolean result = false;
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();
		if (ni != null) {
			result = ni.isAvailable();
		}
		return result;
	}
}
