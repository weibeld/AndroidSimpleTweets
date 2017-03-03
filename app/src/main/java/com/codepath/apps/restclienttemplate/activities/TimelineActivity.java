package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.db.DbUtils;
import com.codepath.apps.restclienttemplate.db.Tweet;
import com.codepath.apps.restclienttemplate.db.Tweet_Table;
import com.codepath.apps.restclienttemplate.db.User;
import com.codepath.apps.restclienttemplate.util.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.util.MyApplication;
import com.codepath.apps.restclienttemplate.util.Util;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import cz.msebera.android.httpclient.Header;

import static android.content.Intent.EXTRA_USER;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();
    public static final String EXTRA_IS_OFFLINE = "offline" ;

    ActivityTimelineBinding b;

    TimelineActivity mActivity;
    ArrayList<Tweet> mData;
    TweetAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    User mCurrentUser;
    EndlessRecyclerViewScrollListener mScrollListener;
    boolean mIsOfflineMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        mActivity = this;
        mData = new ArrayList<>();
        mAdapter = new TweetAdapter(mData, this);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        b.recyclerView.setAdapter(mAdapter);
        b.recyclerView.setLayoutManager(mLayoutManager);
        b.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTweets(1, true);
            }
        });
        b.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadTweets(page, false);
            }
        };

        // Set placeholder for subtitle of ActionBar (will be replaced by username of current user)
        getSupportActionBar().setSubtitle(" ");

        // Normal (online) mode
        if (Util.hasActiveNetworkInterface(this) && Util.hasInternetConnection()) {
            // Get currently logged in user and save it in SharedPreferences
            MyApplication.getTwitterClient().getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mCurrentUser= new User(response);
                    setAppBarSubtitle();
                    MyApplication.getSharedPreferences()
                            .edit()
                            .putString(getString(R.string.pref_current_user), (new Gson()).toJson(mCurrentUser))
                            .apply();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Util.toastLong(mActivity, getString(R.string.server_error));
                    mCurrentUser = new User();  // Create empty fake user
                }
            });
            b.recyclerView.addOnScrollListener(mScrollListener);
            loadTweets(1, false);

        }
        // Offline mode
        else {
            mCurrentUser = Util.getCurrentUserFromPrefs(this);
            setAppBarSubtitle();
            enableOfflineMode();
        }
    }

    private void setAppBarSubtitle() {
        String s = String.format(getString(R.string.subtitle_app_bar), mCurrentUser.screenName);
        getSupportActionBar().setSubtitle(s);
    }

    private void enableOfflineMode() {
        // Set up offline indicator
        SharedPreferences prefs = MyApplication.getSharedPreferences();
        long ts = prefs.getLong(getString(R.string.pref_last_update), 0);
        String text = String.format(getString(R.string.offline_timeline_activity), ts + "");
        SpannableString msg = new SpannableString(text);
        msg.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, 0);
        msg.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 8, msg.length(), 0);
        b.tvOffline.setText(msg);
        b.tvOffline.setVisibility(View.VISIBLE);

        // Disable endless scrolling
        b.recyclerView.removeOnScrollListener(mScrollListener);

        // Clear data in memory (if any) and load data from database
        mAdapter.clear();
        ArrayList<Tweet> dbTweets = (ArrayList<Tweet>) SQLite.select().from(Tweet.class).orderBy(Tweet_Table.id, false).queryList();
        mAdapter.append(dbTweets);

        mIsOfflineMode = true;
    }

    private void disableOfflineMode() {
        b.tvOffline.setVisibility(View.GONE);
        b.recyclerView.addOnScrollListener(mScrollListener);
        mIsOfflineMode = false;
    }

    private void loadTweets(final int page, final boolean isRefreshing) {
        if (!isRefreshing) b.progressBar.setVisibility(View.VISIBLE);
        MyApplication.getTwitterClient().getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Turn off progress bar or pull-to-refresh wheel
                turnOffLoadingIndicator();
                // If fetching first page (either on activity creation or pull-to-refresh)
                if (page == 1) {
                    // Clear all existing data
                    mAdapter.clear();
                    DbUtils.clearTables();
                    // Save the "last update" time in the SharedPreferences
                    long ts = GregorianCalendar.getInstance().getTimeInMillis();
                    MyApplication.getSharedPreferences()
                            .edit()
                            .putLong(getString(R.string.pref_last_update), ts)
                            .apply();
                }
                // If we were previously in offline mode, now we have Internet again
                if (mIsOfflineMode) disableOfflineMode();
                // Convert the returned JSON array of JSON tweet objects to an ArrayList<Tweet>
                ArrayList<Tweet> tweets = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = new Tweet(response.getJSONObject(i));
                        tweets.add(tweet);
                        // Save each Tweet in the database (automatically creates User entries)
                        tweet.save();
                    } catch (JSONException e) { e.printStackTrace(); }

                }
                mAdapter.append(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Turn off progress bar or pull-to-refresh wheel
                turnOffLoadingIndicator();
                Util.toastLong(mActivity, getString(R.string.server_error));
            }

            private void turnOffLoadingIndicator() {
                if (isRefreshing) b.swipeContainer.setRefreshing(false);
                else b.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_compose:
                Intent intent = new Intent(this, ComposeActivity.class);
                intent.putExtra(EXTRA_USER, mCurrentUser);
                intent.putExtra(EXTRA_IS_OFFLINE, mIsOfflineMode);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                MyApplication.getTwitterClient().clearAccessToken();
                startActivity(new Intent(this, LoginActivity.class));
                finish();  // Destroy this activity so that it's not kept on the back stack
                return true;
        }
        return false;
    }
}
