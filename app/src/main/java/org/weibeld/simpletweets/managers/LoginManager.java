package org.weibeld.simpletweets.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.misc.MyApplication;
import org.weibeld.simpletweets.models.User;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dw on 09/03/17.
 */

public class LoginManager {

    private static final String LOG_TAG = LoginManager.class.getSimpleName();

    private static LoginManager instance;
    private User mCurrentUser;
    private Context mContext = MyApplication.getContext();
    private SharedPreferences mPrefs = MyApplication.getPrefs();

    public static synchronized  LoginManager getInstance() {
        if (instance == null) instance = new LoginManager();
        return instance;
    }

    public boolean hasAuthenticatedUser() {
        return mCurrentUser != null;
    }

    public User getAuthenticatedUser() {
        return mCurrentUser;
    }

    // Call when wishing to determine the currently authenticated user
    public void determineAuthenticatedUser(DetermineUserCallback callback) {
        OfflineModeManager m = OfflineModeManager.getInstance();

        if (!m.isOfflineMode()) {
            // Read current user from API
            MyApplication.getTwitterClient().getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mCurrentUser = new User(response);
                    writeUserToPrefs(mCurrentUser);
                    callback.success(mCurrentUser);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    callback.httpFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
        else {
            // Read saved current user from SharedPreferences
            mCurrentUser = readUserFromPrefs();
            callback.success(mCurrentUser);
        }
    }

    public interface DetermineUserCallback {
        void success(User user);
        void httpFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response);
    }

    private void writeUserToPrefs(User user) {
        Gson gson = new Gson();
        mPrefs.edit().putString(mContext.getString(R.string.pref_current_user), gson.toJson(user)).apply();
    }

    private User readUserFromPrefs() {
        Gson gson = new Gson();
        String json = mPrefs.getString(mContext.getString(R.string.pref_current_user), gson.toJson(new User()));
        return gson.fromJson(json, User.class);
    }

//    public void authentication(String username, String password, LoginManagerInterface callback) {
//
//        //AsyncTask
//        mCurrentUser = new User();
//        callback.success();
//
//        EventBus.getDefault().post(new AuthEvent());
//    }
}
