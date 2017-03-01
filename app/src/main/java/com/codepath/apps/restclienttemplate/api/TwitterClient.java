package com.codepath.apps.restclienttemplate.api;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
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

    // Returns array of Tweets. EAch
	// https://api.twitter.com/1.1/statuses/home_timeline.json
	public void getHomeTimeline(int page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
        Log.d(LOG_TAG, "Api Url:" + apiUrl);
		RequestParams params = new RequestParams();
		params.put("page", String.valueOf(page));
		getClient().get(apiUrl, params, handler);
	}

    public void getMentionsTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
        Log.d(LOG_TAG, "getMentionsTimeline");
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        Log.d(LOG_TAG, "Api Url:" + apiUrl);
        if (params != null) {
            Log.d(LOG_TAG, "RequestParams:" + params.toString());
        }
        client.get(apiUrl, params, handler);
    }

    public void postTweets(String tweetString, AsyncHttpResponseHandler handler) {
        Log.d(LOG_TAG, "postTweets");
        String postTweetApiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweetString);
        client.post(postTweetApiUrl, params, handler);
    }

    public void getCurrentUser(AsyncHttpResponseHandler handler) {
        Log.d(LOG_TAG, "getCurrentUser");
        String currentUserApiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("skip_status", String.valueOf(true));
        client.get(currentUserApiUrl, handler);
    }

    public void getUserTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
        Log.d(LOG_TAG, "getUserTimeline");
        String currentUserApiUrl = getApiUrl("statuses/user_timeline.json");
        Log.d(LOG_TAG, "Api Url:" + currentUserApiUrl);
        client.get(currentUserApiUrl, params, handler);
    }

    public void getUserInfo(AsyncHttpResponseHandler handler, long uid) {
        Log.d(LOG_TAG, "getUserInfo");
        String userApiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("user_id", String.valueOf(uid));
        client.get(userApiUrl, params, handler);
    }

    public void getUserInfo(AsyncHttpResponseHandler handler, String screen_name) {
        Log.d(LOG_TAG, "getUserInfo");
        String userApiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screen_name);
        client.get(userApiUrl, params, handler);
    }

}
