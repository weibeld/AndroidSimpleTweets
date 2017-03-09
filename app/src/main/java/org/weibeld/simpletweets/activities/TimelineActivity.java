package org.weibeld.simpletweets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.adapters.TimelineFragmentPagerAdapter;
import org.weibeld.simpletweets.databinding.ActivityTimelineBinding;
import org.weibeld.simpletweets.fragments.HomeTimelineFragment;
import org.weibeld.simpletweets.fragments.MentionsTimelineFragment;
import org.weibeld.simpletweets.fragments.TimelineFragment;
import org.weibeld.simpletweets.managers.LoginManager;
import org.weibeld.simpletweets.misc.MyApplication;
import org.weibeld.simpletweets.misc.Util;
import org.weibeld.simpletweets.models.User;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements TimelineFragment.TimelineFragmentListener {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();
    public static final String EXTRA_IS_OFFLINE = "offline" ;

    ActivityTimelineBinding b;

    TimelineActivity mActivity;
    boolean mIsOfflineMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        setSupportActionBar(b.toolbar);
        mActivity = this;

        getSupportActionBar().setSubtitle(" ");

        MyApplication.getTwitterClient().getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user = new User(response);
                LoginManager.getInstance().setAuthenticatedUser(user);
                MyApplication.getPrefs().edit().putString(getString(R.string.pref_current_user), (new Gson()).toJson(user)).apply();
                getSupportActionBar().setSubtitle(user.screenName);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    String errorMsg = Util.extractJsonErrorMsg(errorResponse);
                    Util.toastLong(mActivity, String.format(getString(R.string.toast_server_error_timeline_user), errorMsg));
                }
                else
                    Util.toastLong(mActivity, getString(R.string.toast_network_error_timeline));
                throwable.printStackTrace();
//            }
            }
        });

        // Online mode
        if (Util.hasActiveNetworkInterface(this) && Util.hasInternetConnection()) {
        }
        // Offline mode
        else {

        }

        // Create the fragments with the different timelines for the ViewPager
        ArrayList<TimelineFragment> frags = new ArrayList<TimelineFragment>();
        frags.add(HomeTimelineFragment.newInstance(mIsOfflineMode));
        frags.add(MentionsTimelineFragment.newInstance(mIsOfflineMode));
        b.viewpager.setAdapter(new TimelineFragmentPagerAdapter(getSupportFragmentManager(), frags));
        b.tabLayout.setupWithViewPager(b.viewpager);
    }




//    private void loadTweets(final int page, final boolean isRefreshing) {
//        if (!isRefreshing) b.progressBar.setVisibility(View.VISIBLE);
//        MyApplication.getTwitterClient().getHomeTimeline(page, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                // Turn off progress bar or pull-to-refresh wheel
//                turnOffLoadingIndicator();
//                // If fetching first page (either on activity creation or pull-to-refresh)
//                if (page == 1) {
//                    // Clear all existing data
//                    mAdapter.clear();
//                    DbUtils.clearTables();
//                    // Save the "last update" time in the SharedPreferences
//                    long ts = GregorianCalendar.getInstance().getTimeInMillis();
//                    MyApplication.getPrefs()
//                            .edit()
//                            .putLong(getString(R.string.pref_last_update), ts)
//                            .apply();
//                }
//                // If we were previously in offline mode, now we have Internet again
//                if (mIsOfflineMode) disableOfflineMode();
//                // Convert the returned JSON array of JSON tweet objects to an ArrayList<Tweet>
//                ArrayList<Tweet> tweets = new ArrayList<>();
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        Tweet tweet = new Tweet(response.getJSONObject(i));
//                        tweets.add(tweet);
//                        // Save each Tweet in the database (automatically creates User entries)
//                        tweet.save();
//                    } catch (JSONException e) { e.printStackTrace(); }
//
//                }
//                mAdapter.append(tweets);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                // Turn off progress bar or pull-to-refresh wheel
//                turnOffLoadingIndicator();
//                if (errorResponse != null) {
//                    String errorMsg = Util.extractJsonErrorMsg(errorResponse);
//                    Util.toastLong(mActivity, String.format(getString(R.string.toast_server_error_timeline_tweets), errorMsg));
//                }
//                else
//                    Util.toastLong(mActivity, getString(R.string.toast_network_error_timeline));
//                throwable.printStackTrace();
//            }
//
//            private void turnOffLoadingIndicator() {
//                if (isRefreshing) b.swipeContainer.setRefreshing(false);
//                else b.progressBar.setVisibility(View.GONE);
//            }
//        });
//    }

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
        }
        return false;
    }

    @Override
    public void onFabClicked() {
        Intent intent = new Intent(this, ComposeActivity.class);
        intent.putExtra(EXTRA_IS_OFFLINE, mIsOfflineMode);
        startActivity(intent);
    }
}

