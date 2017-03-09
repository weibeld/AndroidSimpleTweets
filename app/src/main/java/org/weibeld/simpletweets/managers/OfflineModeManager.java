package org.weibeld.simpletweets.managers;

import android.util.Log;

import org.weibeld.simpletweets.misc.Util;

/**
 * Created by dw on 09/03/17.
 */

public class OfflineModeManager {

    private static final String LOG_TAG = OfflineModeManager.class.getSimpleName();

    private static OfflineModeManager instance;
    private Boolean mIsOfflineMode;

    public static synchronized  OfflineModeManager getInstance() {
        if (instance == null) instance = new OfflineModeManager();
        return instance;
    }

    public Boolean isOfflineMode() {
        return mIsOfflineMode;
    }

    // Call when wishing to set the mode (online or offline), i.e. on app start
    public void determineMode() {
        if (Util.hasInternetConnection()) {
            Log.d(LOG_TAG, "Enabling online mode");
            mIsOfflineMode = false;
        }
        else {
            Log.d(LOG_TAG, "Enabling offline mode");
            mIsOfflineMode = true;
        }
    }

    // TODO: emit event when switching from offline to online mode
    // Currently, while the app is running, it's possible to switch from offline to online, but not from online to offline



}
