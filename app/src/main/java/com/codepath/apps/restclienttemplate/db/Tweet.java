package com.codepath.apps.restclienttemplate.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
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
	public Long id;

	@Column
    @ForeignKey(saveForeignKeyModel = false)
    public User user;

	@Column
    public String createdAt;

	@Column
    public String text;

	public Tweet() {
		super();
	}

	// Add a constructor that creates an object from the JSON response
	public Tweet(JSONObject object){
		super();
		try {
            this.id = object.getLong("id");
			this.user = new User(object.getJSONObject("user"));
			this.createdAt = object.getString("created_at");
			this.text = object.getString("text");
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

	/* The where class in this code below will be marked red until you first compile the project, since the code 
	 * for the SampleModel_Table class is generated at compile-time.
	 */
	
	// Record Finders
//	public static Tweet byId(long id) {
//		return new Select().from(Tweet.class).where(Tweet_Table.id.eq(id)).querySingle();
//	}
//
//	public static List<Tweet> recentItems() {
//		return new Select().from(Tweet.class).orderBy(Tweet_Table.id, false).limit(300).queryList();
//	}
}
