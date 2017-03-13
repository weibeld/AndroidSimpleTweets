package org.weibeld.simpletweets.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.weibeld.simpletweets.db.MyDatabase;

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
        SQLite.delete().from(User.class).query();
    }


}
