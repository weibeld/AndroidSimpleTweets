package org.weibeld.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.api.TwitterClient;

// TODO: can we get a reference to the authenticated user in LoginActivity (so we can pass it to TimelineActivity)?
public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	private static final String LOG_TAG = LoginActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
        Log.d(LOG_TAG, "Login successful");
		 Intent i = new Intent(this, TimelineActivity.class);
		 startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
        Log.d(LOG_TAG, "Login failed");
		e.printStackTrace();
	}

	// Click handler method for the button_background used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button_background used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
