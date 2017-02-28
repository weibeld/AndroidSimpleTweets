package com.codepath.apps.restclienttemplate.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the DBFlow wiki for more details:
 * https://github.com/codepath/android_guides/wiki/DBFlow-Guide
 *
 * Note: All models **must extend from** `BaseModel` as shown below.
 * 
 */
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {

	@PrimaryKey
	@Column
	Long id;

	@Column
	String userId;

	@Column
	String userHandle;

	@Column
	String timestamp;

	@Column
	String body;

	public Tweet() {
		super();
	}

	// Add a constructor that creates an object from the JSON response
	public Tweet(JSONObject object){
		super();

		try {
			this.userId = object.getString("user_id");
			this.userHandle = object.getString("user_username");
			this.timestamp = object.getString("timestamp");
			this.body = object.getString("body");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.save();
            tweets.add(tweet);
        }
        return tweets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

	/* The where class in this code below will be marked red until you first compile the project, since the code 
	 * for the SampleModel_Table class is generated at compile-time.
	 */
	
	// Record Finders
	public static Tweet byId(long id) {
		return new Select().from(Tweet.class).where(Tweet_Table.id.eq(id)).querySingle();
	}

//	public static List<Tweet> recentItems() {
//		return new Select().from(Tweet.class).orderBy(SampleModel_Table.id, false).limit(300).queryList();
//	}
}
