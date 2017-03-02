package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.db.Tweet;
import com.codepath.apps.restclienttemplate.db.Tweet_Table;
import com.codepath.apps.restclienttemplate.db.User;
import com.codepath.apps.restclienttemplate.util.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.util.SimpleTweetsApplication;
import com.codepath.apps.restclienttemplate.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    ActivityTimelineBinding b;

    TimelineActivity mActivity;
    ArrayList<Tweet> mTweets;
    TweetAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    boolean mIsPaginationEnabled = false;

    // TODO: get currently authenticated user and save it as a field; display username in app bar and pass User to ComposeActivity
    // TODO: save currently authenticated user in separate table in database (so that it can be restored if offline)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        mActivity = this;

        // Check if we have permissions for writing files (just for debugging)
        Util.verifyStoragePermissions(this);

        if (Util.hasActiveNetworkInterface(this) && Util.hasInternetConnection())
            Log.d(LOG_TAG, "Internet available");
        else
            Log.d(LOG_TAG, "No internet connection");

        mTweets = new ArrayList<>();
        mAdapter = new TweetAdapter(mTweets, this);
        b.recyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        b.recyclerView.setLayoutManager(mLayoutManager);
        b.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHomeTimelineTweets(1, false);
            }
        });
        b.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Normal (online) mode
        if (Util.hasActiveNetworkInterface(this) && Util.hasInternetConnection()) {
            enablePagination();
            loadHomeTimelineTweets(1, true);

        }
        // Offline mode
        else {
            // TODO: indicate offline mode (e.g. text or icon in app bar)
            ArrayList<Tweet> savedTweets = (ArrayList<Tweet>) SQLite.select().from(Tweet.class).orderBy(Tweet_Table.id, false).queryList();
            mTweets.addAll(savedTweets);
            mAdapter.notifyItemRangeInserted(0, savedTweets.size());
            //Util.toastLong(this, "It seems you have no internet connection. Please connect your device to the Internet and try again.");

        }
    }

    private void enablePagination() {
        mIsPaginationEnabled = true;
        b.recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadHomeTimelineTweets(page, true);
            }
        });
    }

    private void loadHomeTimelineTweets(int page, final boolean showProgressBar) {
        Log.d(LOG_TAG, "Loading page " + page);
        b.serverError.setVisibility(View.GONE);

        if (showProgressBar) b.progressBar.setVisibility(View.VISIBLE);

        SimpleTweetsApplication.getTwitterClient().getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                turnOffLoadingIndicator();
                if (!showProgressBar) {
                    int oldSize = mTweets.size();
                    mTweets.clear();
                    mAdapter.notifyItemRangeRemoved(0, oldSize);
                    // Delete all tweets from the database
                    Tweet.clearTable();
                    User.clearTable();
                    if (!mIsPaginationEnabled) enablePagination();
                }
                // Convert the returned JSON array of JSON tweet objects to an ArrayList<Tweet>
                ArrayList<Tweet> fetchedTweets = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = new Tweet(response.getJSONObject(i));
                        fetchedTweets.add(tweet);
                        // Save each Tweet in the database (automatically creates User entries)
                        tweet.save();
                    } catch (JSONException e) { e.printStackTrace(); }

                }
                int oldSize = mTweets.size();
                mTweets.addAll(fetchedTweets);
                mAdapter.notifyItemRangeInserted(oldSize, fetchedTweets.size());
                //Util.writeToFile("log.json", response.toString());
            }

            // TODO: change failure handling (probably just display toasts if there's a failure)
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                turnOffLoadingIndicator();
                b.serverError.setVisibility(View.VISIBLE);

                //java.net.UnknownHostException if there is no Internet connection (and DNS query failed)
                String msg = "Couldn't load timeline:\n";
                if (errorResponse == null)
                    msg += throwable.getClass().getSimpleName();
                // TODO: check error response spec of Twitter and extract and display the error messages of the JSON response
                else
                    msg += errorResponse.toString() + "\n(" +  throwable.getClass().getSimpleName() + ")";
                Util.toastLong(mActivity, msg);
                Log.d(LOG_TAG, msg);
                throwable.printStackTrace();
            }

            private void turnOffLoadingIndicator() {
                if (showProgressBar) b.progressBar.setVisibility(View.GONE);
                else b.swipeContainer.setRefreshing(false);
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
                Intent i = new Intent(this, ComposeActivity.class);
                startActivity(i);
                return true;
        }
        return false;
    }
}
