package org.weibeld.simpletweets.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.weibeld.simpletweets.db.MyDatabase;

import java.util.ArrayList;

/**
 * Model for a Twitter tweet.
 */
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {

	public static int TYPE_HOME = 0;
	public static int TYPE_MENTIONS = 1;
	public static int TYPE_USER = 2;

	@PrimaryKey
	@Column
	public Long id;

	@Column
    @ForeignKey(saveForeignKeyModel = true)
    public User user;

    // TODO: save date as Date (see https://guides.codepath.com/android/DBFlow-Guide#common-questions)
	@Column
    public String createdAt;

	@Column
    public String text;

	@Column
	public int type;

    // Empty default constructor (required by DBFlow)
	public Tweet() {
		super();
	}

    // Create a tweet from the "status" object returned by Twitter API (as an array of "statuses")
	public Tweet(JSONObject object, int type) {
		super();
		try {
            id = object.getLong("id");
			user = new User(object.getJSONObject("user"));
			createdAt = object.getString("created_at");
			text = object.getString("text");
			this.type = type;
		} catch (JSONException e) { e.printStackTrace(); }
	}

    // Delete all rows of this table
    public static void clearTable() {
        SQLite.delete().from(Tweet.class).query();
    }

    public static ArrayList<Tweet> getHomeTimeline() {
        return getTimeline(TYPE_HOME);
    }

    public static ArrayList<Tweet> getMentionsTimeline() {
        return getTimeline(TYPE_MENTIONS);
    }

    private static ArrayList<Tweet> getTimeline(int type) {
        return (ArrayList<Tweet>) SQLite.select().from(Tweet.class).where(Tweet_Table.type.is(type)).orderBy(Tweet_Table.id, false).queryList();
    }



}
