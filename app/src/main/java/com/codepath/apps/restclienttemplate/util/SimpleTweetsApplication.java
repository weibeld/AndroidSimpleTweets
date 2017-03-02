package com.codepath.apps.restclienttemplate.util;

import android.app.Application;
import android.content.Context;

import com.codepath.apps.restclienttemplate.api.TwitterClient;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = SimpleTweetsApplication.getTwitterClient();
 *     // use client to send requests to API
 *
 */
public class SimpleTweetsApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

		Stetho.initializeWithDefaults(this);

		FlowManager.init(new FlowConfig.Builder(this).build());
		FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

		SimpleTweetsApplication.context = this;
	}

	public static TwitterClient getTwitterClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, SimpleTweetsApplication.context);
	}
}