package org.weibeld.simpletweets.misc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.weibeld.simpletweets.api.TwitterClient;

public class MyApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

		Stetho.initializeWithDefaults(this);

		FlowManager.init(new FlowConfig.Builder(this).build());
		FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

		MyApplication.context = this;
	}

	public static TwitterClient getTwitterClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, MyApplication.context);
	}

	public static SharedPreferences getPrefs() {
		return context.getSharedPreferences("main", MODE_PRIVATE);
	}

	public static Context getContext() {
		return context;
	}
}