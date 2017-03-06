package org.weibeld.simpletweets.api;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class TwitterClient extends OAuthBaseClient {

    private static final String LOG_TAG = TwitterClient.class.getSimpleName();

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "71wT27WT7PvEw5YW4Sn63d5Yf";
	public static final String REST_CONSUMER_SECRET = "Dvhxb9MbBluzl7N3B9UKXtFpB4YNv4ZU8jSQhzaCn6uXxeJWAN";
	public static final String REST_CALLBACK_URL = "x-oauthflow-twitter://codepath.com";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    // Returns array of Tweets.
	// https://api.twitter.com/1.1/statuses/home_timeline.json
	public void getHomeTimeline(int page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
        Log.d(LOG_TAG, "Api Url:" + apiUrl);
		RequestParams params = new RequestParams();
		params.put("page", String.valueOf(page));
		getClient().get(apiUrl, params, handler);
	}

    public void getCurrentUser(AsyncHttpResponseHandler handler) {
        Log.d(LOG_TAG, "getCurrentUser");
        String currentUserApiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("skip_status", String.valueOf(true));
        client.get(currentUserApiUrl, handler);
    }

    public void postTweet(String text, AsyncHttpResponseHandler handler) {
        Log.d(LOG_TAG, "postTweet");
        Log.d(LOG_TAG, "POsting: " + text);
        String postTweetApiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", text);
        client.post(postTweetApiUrl, params, handler);
    }


//    public void getMentionsTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
//        Log.d(LOG_TAG, "getMentionsTimeline");
//        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
//        Log.d(LOG_TAG, "Api Url:" + apiUrl);
//        if (params != null) {
//            Log.d(LOG_TAG, "RequestParams:" + params.toString());
//        }
//        client.get(apiUrl, params, handler);
//    }
//
//    public void getUserTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
//        Log.d(LOG_TAG, "getUserTimeline");
//        String currentUserApiUrl = getApiUrl("statuses/user_timeline.json");
//        Log.d(LOG_TAG, "Api Url:" + currentUserApiUrl);
//        client.get(currentUserApiUrl, params, handler);
//    }
//
//    public void getUserInfo(AsyncHttpResponseHandler handler, long uid) {
//        Log.d(LOG_TAG, "getUserInfo");
//        String userApiUrl = getApiUrl("users/show.json");
//        RequestParams params = new RequestParams();
//        params.put("user_id", String.valueOf(uid));
//        client.get(userApiUrl, params, handler);
//    }
//
//    public void getUserInfo(AsyncHttpResponseHandler handler, String screen_name) {
//        Log.d(LOG_TAG, "getUserInfo");
//        String userApiUrl = getApiUrl("users/show.json");
//        RequestParams params = new RequestParams();
//        params.put("screen_name", screen_name);
//        client.get(userApiUrl, params, handler);
//    }

}
