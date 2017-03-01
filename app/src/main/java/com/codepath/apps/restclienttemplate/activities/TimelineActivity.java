package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    ActivityTimelineBinding b;

    ArrayList<Tweet> mTweets;
    TweetAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        Util.verifyStoragePermissions(this);

        mTweets = new ArrayList<>();
        mAdapter = new TweetAdapter(mTweets, this);
        b.recyclerView.setAdapter(mAdapter);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        b.recyclerView.setLayoutManager(lm);
        b.recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadHomeTimelineTweets(page);
            }
        });

        loadHomeTimelineTweets(1);
    }

    private void loadHomeTimelineTweets(int page) {
        Log.d(LOG_TAG, "Loading page " + page);
        b.progressBar.setVisibility(View.VISIBLE);
        TwitterApplication.getTwitterClient().getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                b.progressBar.setVisibility(View.GONE);
                Util.writeToFile("log.json", response.toString());
                int oldSize = mTweets.size();
                ArrayList<Tweet> fetchedTweets = Tweet.fromJson(response);
                mTweets.addAll(fetchedTweets);
                mAdapter.notifyItemRangeInserted(oldSize, fetchedTweets.size());
            }
        });
    }
}
