package org.weibeld.simpletweets.models;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.db.MyDatabase;
import org.weibeld.simpletweets.misc.RoundedTransformation;

import java.io.Serializable;

/**
 * Created by dw on 01/03/17.
 */
@Table(database = MyDatabase.class)
public class User extends BaseModel implements Serializable {

    private static final String LOG_TAG = User.class.getSimpleName();

    @PrimaryKey
    @Column
    public Long id;

    @Column
    public String name = "";

    @Column
    public String screenName = "";

    @Column
    public String description = "";

    @Column
    public String profileImageUrlMini = "";

    @Column
    public String profileImageUrlNormal = "";

    @Column
    public String profileImageUrlBigger = "";

    @Column
    public String profileImageUrlOriginal = "";

    @Column
    public String profileBannerUrl = "";

    @Column
    public int followersCount = -1;

    @Column
    public int followingCount = -1;

    @Column
    public int tweetsCount = -1;

    // Empty default constructor (required by DBFlow)
    public User() {
        super();
    }

    // Create a user from the "user" JSON object returned by the Twitter API
    public User(JSONObject object) {
        super();
        try {
            id = object.getLong("id");
            name = object.getString("name");
            screenName = "@" + object.getString("screen_name");
            description = object.has("description") ? object.getString("description") : "";
            // Provided image sizes: "mini": 24x24px, "normal": 48x48px, "bigger": 73x73px, "original": WxH
            profileImageUrlMini = object.has("profile_image_url") ? object.getString("profile_image_url").replace("_normal", "_mini") : "";
            profileImageUrlNormal = object.has("profile_image_url") ? object.getString("profile_image_url") : "";
            profileImageUrlBigger = object.has("profile_image_url") ? object.getString("profile_image_url").replace("_normal", "_bigger") : "";
            profileImageUrlOriginal = object.has("profile_image_url") ? object.getString("profile_image_url").replace("_normal", "") : "";
            profileBannerUrl = object.has("profile_banner_url") ? object.getString("profile_banner_url") : "";
            followersCount = object.has("followers_count") ? object.getInt("followers_count") : -1;
            followingCount = object.has("friends_count") ? object.getInt("friends_count") : -1;
            tweetsCount = object.has("statuses_count") ? object.getInt("statuses_count") : -1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Delete all rows of this table
    public static void clearTable() {
        SQLite.delete().from(Tweet.class).query();
    }

    // Load the profile image of a user into the passed ImageView
    @BindingAdapter({"bind:profileImageUrl"})
    public static void loadProfileImage(ImageView view, String url) {
        if (url == null || url.isEmpty())
            url = Uri.parse("R.drawable.placeholder_profile_image_360").toString();
        Log.d(LOG_TAG, "Loading image " + url);
        Picasso.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder_profile_image_360)
                .transform(new RoundedTransformation(0.0833, 0))
                .into(view);
    }

    // Load the profile image of a user into the passed ImageView
    @BindingAdapter({"bind:profileBannerUrl"})
    public static void loadProfileBanner(ImageView view, String url) {
        if (url == null || url.isEmpty())
            url = Uri.parse("R.drawable.placeholder_banner").toString();
        Log.d(LOG_TAG, "Loading banner " + url);
        Picasso.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder_banner)
                .into(view);
    }


}
