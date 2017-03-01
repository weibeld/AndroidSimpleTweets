package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.api.TwitterApplication;
import com.codepath.apps.restclienttemplate.api.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.db.Tweet;
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
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // Get the home timeline
        TwitterClient client = TwitterApplication.getTwitterClient();
        client.getHomeTimeline(1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Util.writeToFile("log.json", response.toString());
                ArrayList<Tweet> fetchedTweets = Tweet.fromJson(response);
                mTweets.addAll(fetchedTweets);
                mAdapter.notifyItemRangeInserted(0, fetchedTweets.size());
                Log.d(LOG_TAG, "Number of fetched tweets: " + fetchedTweets.size());
            }
        });

    }
}
