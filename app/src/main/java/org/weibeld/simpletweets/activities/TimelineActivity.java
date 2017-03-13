package org.weibeld.simpletweets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;
import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.adapters.TimelineFragmentPagerAdapter;
import org.weibeld.simpletweets.databinding.ActivityTimelineBinding;
import org.weibeld.simpletweets.fragments.HomeTimelineFragment;
import org.weibeld.simpletweets.fragments.MentionsTimelineFragment;
import org.weibeld.simpletweets.fragments.TimelineFragment;
import org.weibeld.simpletweets.managers.LoginManager;
import org.weibeld.simpletweets.managers.OfflineModeManager;
import org.weibeld.simpletweets.misc.MyApplication;
import org.weibeld.simpletweets.misc.Util;
import org.weibeld.simpletweets.models.User;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements TimelineFragment.TimelineFragmentListener {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    ActivityTimelineBinding b;
    TimelineActivity mActivity;
    LoginManager mLoginMgr = LoginManager.getInstance();
    OfflineModeManager mOfflineMgr = OfflineModeManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        mActivity = this;
        setSupportActionBar(b.toolbar);
        getSupportActionBar().setSubtitle(" ");

        // Determine whether to start the app in online or offline mode
        mOfflineMgr.determineMode();

        // Determine the currently authenticated user
        mLoginMgr.determineAuthenticatedUser(new LoginManager.DetermineUserCallback() {
            @Override
            public void success(User user) {
                getSupportActionBar().setSubtitle(String.format(getString(R.string.subtitle_app_bar), user.screenName));
            }
            @Override
            public void httpFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                if (response != null) {
                    String errorMsg = Util.extractJsonErrorMsg(response);
                    Util.toastLong(mActivity, String.format(getString(R.string.toast_server_error_timeline_user), errorMsg));
                } else
                    Util.toastLong(mActivity, getString(R.string.toast_network_error_timeline));
                throwable.printStackTrace();
            }
        });

        // Create the fragments with the different timelines for the ViewPager
        ArrayList<TimelineFragment> frags = new ArrayList<>();
        frags.add(new HomeTimelineFragment());
        frags.add(new MentionsTimelineFragment());
        b.viewpager.setAdapter(new TimelineFragmentPagerAdapter(getSupportFragmentManager(), frags));
        b.tabLayout.setupWithViewPager(b.viewpager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                MyApplication.getTwitterClient().clearAccessToken();
                startActivity(new Intent(this, LoginActivity.class));
                finish();  // Destroy this activity so that it's not kept on the back stack
                return true;
            case R.id.action_profile:
                showUserProfile(mLoginMgr.getAuthenticatedUser());
                return true;
            case R.id.action_compose:
                startActivity(new Intent(this, ComposeActivity.class));
                return true;
        }
        return false;
    }

    @Override
    public void onProfileImageClicked(User user) {
        showUserProfile(user);
    }

    private void showUserProfile(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(Intent.EXTRA_USER, user);
        startActivity(intent);
    }
}

