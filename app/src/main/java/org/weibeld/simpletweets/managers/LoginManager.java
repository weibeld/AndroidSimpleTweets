package org.weibeld.simpletweets.managers;

import org.weibeld.simpletweets.models.User;

/**
 * Created by dw on 09/03/17.
 */

public class LoginManager {

    private static LoginManager instance;
    private User mCurrentUser;

    public static synchronized  LoginManager getInstance() {
        if(instance == null) instance = new LoginManager();
        return instance;
    }

    public User getAuthenticatedUser() {
        return mCurrentUser;
    }

    public boolean hasAuthenticatedUser() {
        return (mCurrentUser != null);
    }

    public void setAuthenticatedUser(User user) {
        mCurrentUser = user;
    }

    public interface LoginManagerInterface {
        User getAuthenticatedUser();
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
