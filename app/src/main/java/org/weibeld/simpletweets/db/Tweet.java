package org.weibeld.simpletweets.db;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.weibeld.simpletweets.util.Util;

/**
 * Model for a Twitter tweet.
 */
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {

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

    // Empty default constructor (required by DBFlow)
	public Tweet() {
		super();
	}

    // Create a tweet from the "status" object returned by Twitter API (as an array of "statuses")
	public Tweet(JSONObject object){
		super();
		try {
            id = object.getLong("id");
			user = new User(object.getJSONObject("user"));
			createdAt = object.getString("created_at");
			text = object.getString("text");
		} catch (JSONException e) { e.printStackTrace(); }
	}

    // Delete all rows of this table
    public static void clearTable() {
        SQLite.delete().from(Tweet.class).query();
    }

	// Calculate the relative timestamp of the passed time and set it to the passed TextView
	@BindingAdapter({"bind:relativeTimestamp"})
	public static void setRelativeTimestamp(TextView tv, String createdAt) {
		tv.setText(Util.getRelativeTimeAgo(createdAt));
	}

}
