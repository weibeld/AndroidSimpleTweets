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
import com.codepath.apps.restclienttemplate.api.TwitterApplication;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.db.Tweet;
import com.codepath.apps.restclienttemplate.util.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    ActivityTimelineBinding b;

    TimelineActivity mActivity;
    ArrayList<Tweet> mTweets;
    TweetAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        mActivity = this;

        Util.verifyStoragePermissions(this);

        if (Util.hasActiveNetworkInterface(this) && Util.hasInternetConnection())
            Log.d(LOG_TAG, "Internet available");
        else
            Log.d(LOG_TAG, "No internet connection");

        mTweets = new ArrayList<>();
        mAdapter = new TweetAdapter(mTweets, this);
        b.recyclerView.setAdapter(mAdapter);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        b.recyclerView.setLayoutManager(lm);
        b.recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadHomeTimelineTweets(page, true);
            }
        });

        b.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int oldSize = mTweets.size();
                mTweets.clear();
                mAdapter.notifyItemRangeRemoved(0, oldSize);
                loadHomeTimelineTweets(1, false);
            }
        });
        b.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        loadHomeTimelineTweets(1, true);
    }

    private void loadHomeTimelineTweets(int page, final boolean showProgressBar) {
        Log.d(LOG_TAG, "Loading page " + page);
        b.serverError.setVisibility(View.GONE);
        if (showProgressBar) b.progressBar.setVisibility(View.VISIBLE);

        TwitterApplication.getTwitterClient().getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                turnOffLoadingIndicator();
                int oldSize = mTweets.size();
                ArrayList<Tweet> fetchedTweets = Tweet.fromJson(response);
                mTweets.addAll(fetchedTweets);
                mAdapter.notifyItemRangeInserted(oldSize, fetchedTweets.size());
                Util.writeToFile("log.json",    response.toString());
            }

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
