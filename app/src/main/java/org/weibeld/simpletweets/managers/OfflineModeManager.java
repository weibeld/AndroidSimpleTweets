package org.weibeld.simpletweets.managers;

import org.greenrobot.eventbus.EventBus;
import org.weibeld.simpletweets.events.OfflineToOnlineEvent;
import org.weibeld.simpletweets.events.OnlineToOfflineEvent;
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

    // Call this on app initialisation
    public void determineMode() {
        if (Util.hasInternetConnection()) {
            mIsOfflineMode = false;
        }
        else {
            mIsOfflineMode = true;
        }
    }

    public Boolean isOfflineMode() {
        return mIsOfflineMode;
    }

    public void switchToOnlineMode() {
        if (mIsOfflineMode) {
            EventBus.getDefault().post(new OfflineToOnlineEvent());
            mIsOfflineMode = false;
        }
    }

    // Currently, while the app is running, only offline to online is possible, but not online to offline
    public void switchToOfflineMode() {
        if (!mIsOfflineMode) {
            EventBus.getDefault().post(new OnlineToOfflineEvent());
            mIsOfflineMode = true;
        }
    }
}
