package org.weibeld.simpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.db.DbUtils;
import org.weibeld.simpletweets.misc.MyApplication;
import org.weibeld.simpletweets.misc.Util;
import org.weibeld.simpletweets.models.Tweet;
import org.weibeld.simpletweets.models.User;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import cz.msebera.android.httpclient.Header;

import static org.weibeld.simpletweets.models.Tweet.TYPE_USER;

/**
 * Created by dw on 13/03/17.
 */

public class UserTimelineFragment extends TimelineFragment {

    private static final String ARG_USER = "user";

    private User mUser;
    private long mLowestId;

    public static Fragment newInstance(User user) {
        UserTimelineFragment f = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (User) getArguments().getSerializable(ARG_USER);
    }

    @Override
    protected String getLastUpdatePrefKey() {
        return getString(R.string.pref_last_update_user_timeline);
    }

    @Override
    protected int getType() {
        return TYPE_USER;
    }

    @Override
    public String getTitle() {
        return "User";
    }

    @Override
    protected void getTweetsFromApi(int page, boolean isRefreshing) {
        if (!isRefreshing) b.progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        params.add("screen_name", mUser.screenName);
        params.add("count", "20");
        if (page > 1) params.add("max_id", Long.toString(mLowestId));
        MyApplication.getTwitterClient().getUserTimeline(params, new JsonHttpResponseHandler() {
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
                    MyApplication.getPrefs()
                            .edit()
                            .putLong(getString(R.string.pref_last_update_user_timeline), ts)
                            .apply();
                }
                // If we were previously in offline mode, now we have Internet again
//                if (mIsOfflineMode) disableOfflineMode();
                // Convert the returned JSON array of JSON tweet objects to an ArrayList<Tweet>
                ArrayList<Tweet> tweets = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = new Tweet(response.getJSONObject(i), TYPE_USER);
                        tweets.add(tweet);
                        // Save each Tweet in the database (automatically creates User entries)
                        tweet.save();
                    } catch (JSONException e) { e.printStackTrace(); }

                }
                if (tweets.size() > 0) {
                    mAdapter.append(tweets);
                    // The lowest (oldest) ID fetched so far
                    mLowestId = tweets.get(tweets.size() - 1).id - 1;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Turn off progress bar or pull-to-refresh wheel
                turnOffLoadingIndicator();
                if (errorResponse != null) {
                    String errorMsg = Util.extractJsonErrorMsg(errorResponse);
                    Util.toastLong(getActivity(), String.format(getString(R.string.toast_server_error_timeline_tweets), errorMsg));
                }
                else
                    Util.toastLong(getActivity(), getString(R.string.toast_network_error_timeline));
                throwable.printStackTrace();
            }

            private void turnOffLoadingIndicator() {
                if (isRefreshing) b.swipeContainer.setRefreshing(false);
                else b.progressBar.setVisibility(View.GONE);
            }
        });
    }
}
