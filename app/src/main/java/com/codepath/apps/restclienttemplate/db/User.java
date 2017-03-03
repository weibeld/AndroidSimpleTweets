package com.codepath.apps.restclienttemplate.db;

import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;

import com.codepath.apps.restclienttemplate.R;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dw on 01/03/17.
 */
@Table(database = MyDatabase.class)
public class User extends BaseModel implements Serializable {

    @PrimaryKey
    @Column
    public Long id;

    @Column
    public String name = "";

    @Column
    public String screenName = "";

    @Column
    public String profileImageUrl = "";

    // Empty default constructor (required by DBFlow)
    public User() {
        super();
    }

    // Create a user from the "user" JSON object returned by the Twitter API
    public User(JSONObject object) {
        super();
        try {
            this.id = object.getLong("id");
            this.name = object.getString("name");
            this.screenName = "@" + object.getString("screen_name");
            // Provided: "mini": 24x24px, "normal": 48x48px, "bigger": 73x73px, "original": WxH
            this.profileImageUrl = object.getString("profile_image_url").replace("normal", "bigger");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Delete all rows of this table
    public static void clearTable() {
        SQLite.delete().from(Tweet.class).query();
    }

    // Load the profile image of a user into the passed ImageView
    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Log.d("User", "Loading image " + url);
        Picasso.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder_profile_image_75)
                .into(view);
    }
}
