package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.db.User;
import com.codepath.apps.restclienttemplate.util.MyApplication;
import com.codepath.apps.restclienttemplate.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Intent.EXTRA_USER;

public class ComposeActivity extends AppCompatActivity {

    private static final String LOG_TAG = ComposeActivity.class.getSimpleName();

    ActivityComposeBinding b;

    ComposeActivity mActivity;
    User mCurrentUser;
    boolean mIsOfflineMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_compose);

        mActivity = this;

        Intent intent = getIntent();
        mCurrentUser = (User) intent.getSerializableExtra(EXTRA_USER);
        mIsOfflineMode = intent.getBooleanExtra(TimelineActivity.EXTRA_IS_OFFLINE, false);

        b.setUser(mCurrentUser);

        ActionBar a = getSupportActionBar();
        a.setTitle(R.string.title_compose_activity);
        a.setDisplayHomeAsUpEnabled(true);
        a.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        // Enable offline mode (note: ComposeActivity can't transition from offline to online mode)
        if (mIsOfflineMode) {
            // Disable input widgets
            b.etCompose.setEnabled(false);
            b.btnTweet.setEnabled(false);
            // Display offline mode indicator
            SpannableString msg = new SpannableString(getString(R.string.offline_compose));
            msg.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, 0);
            msg.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 8, msg.length(), 0);
            b.tvOffline.setText(msg);
            b.tvOffline.setVisibility(View.VISIBLE);
        }

        // On clicking button "Tweet"
        b.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = b.etCompose.getText().toString();
                MyApplication.getTwitterClient().postTweet(text, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Util.toast(mActivity, getString(R.string.toast_tweet_posted));
                        startActivity(new Intent(mActivity, TimelineActivity.class));
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Util.toastLong(mActivity, getString(R.string.toast_error_publish));
                        throwable.printStackTrace();
                    }
                });
            }
        });

        // Character count
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
