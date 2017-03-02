package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.api.TwitterApplication;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.db.User;
import com.codepath.apps.restclienttemplate.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private static final String LOG_TAG = ComposeActivity.class.getSimpleName();

    ComposeActivity mActivity;
    ActivityComposeBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_compose);
        ActionBar a = getSupportActionBar();
        a.setTitle(R.string.title_compose_activity);
        a.setDisplayHomeAsUpEnabled(true);
        a.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        mActivity = this;

        b.progressBar.setVisibility(View.VISIBLE);
        TwitterApplication.getTwitterClient().getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                b.progressBar.setVisibility(View.GONE);
                User user = new User(response);
                b.setUser(user);
            }
        });

        b.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(Util.hasActiveNetworkInterface(mActivity) && Util.hasInternetConnection())) {
                    Util.toastLong(mActivity, "It seems you have no internet connection. Please connect your device to the Internet and try again.");
                    return;
                }
                String text = b.etCompose.getText().toString();
                TwitterApplication.getTwitterClient().postTweet(text, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(LOG_TAG, "Tweet posted successfully");
                        Util.toast(mActivity, "Tweet posted");
                        startActivity(new Intent(mActivity, TimelineActivity.class));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        String msg = "Couldn't publish tweet:\n";
                        if (errorResponse == null)
                            msg += throwable.getClass().getSimpleName();
                        // TODO: check error response spec of Twitter and extract and display the error messages of the JSON response
                        else
                            msg += errorResponse.toString() + "\n(" +  throwable.getClass().getSimpleName() + ")";
                        Util.toastLong(mActivity, msg);
                        Log.d(LOG_TAG, msg);
                        throwable.printStackTrace();
                    }
                });
            }
        });

        // Count number of entered characters and display it in the character count TextView
        b.etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int remaining = 140 - s.toString().length();
                b.tvCharacterCount.setText("" + remaining);

                if (remaining >= 0) {
                    b.tvCharacterCount.setTextColor(Color.BLACK);
                    if (!b.btnTweet.isEnabled()) b.btnTweet.setEnabled(true);
                }
                else {
                    b.tvCharacterCount.setTextColor(Color.RED);
                    if (b.btnTweet.isEnabled()) b.btnTweet.setEnabled(false);
                }

            }
        });
    }

    // When clicking the navigation icon, just restart parent activity, rather than recreating it
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
