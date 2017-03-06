package org.weibeld.simpletweets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.api.TwitterClient;
import org.weibeld.simpletweets.databinding.ActivityLoginBinding;

// TODO: can we get a reference to the authenticated user in LoginActivity (so we can pass it to TimelineActivity)?
public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    ActivityLoginBinding b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		b = DataBindingUtil.setContentView(this, R.layout.activity_login);
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
        Log.d(LOG_TAG, "Login successful");
        b.progressBar.setVisibility(View.GONE);
		 Intent i = new Intent(this, TimelineActivity.class);
		 startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
        Log.d(LOG_TAG, "Login failed");
        b.progressBar.setVisibility(View.GONE);
		e.printStackTrace();
	}

	// Click handler method for the button_background used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button_background used to login
	public void loginToRest(View view) {
        b.progressBar.setVisibility(View.VISIBLE);
		getClient().connect();
	}

}
