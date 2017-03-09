package org.weibeld.simpletweets.models;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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
            this.id = object.getLong("id");
            this.name = object.getString("name");
            this.screenName = "@" + object.getString("screen_name");
            this.description = object.has("description") ? object.getString("description") : "";
            // Provided image sizes: "mini": 24x24px, "normal": 48x48px, "bigger": 73x73px, "original": WxH
            this.profileImageUrlMini = object.has("profile_image_url") ? object.getString("profile_image_url").replace("_normal", "_mini") : "";
            this.profileImageUrlNormal = object.has("profile_image_url") ? object.getString("profile_image_url") : "";
            this.profileImageUrlBigger = object.has("profile_image_url") ? object.getString("profile_image_url").replace("_normal", "_bigger") : "";
            this.profileImageUrlOriginal = object.has("profile_image_url") ? object.getString("profile_image_url").replace("_normal", "") : "";
            this.followersCount = object.has("followers_count") ? object.getInt("followers_count") : -1;
            this.followingCount = object.has("friends_count") ? object.getInt("friends_count") : -1;
            this.tweetsCount = object.has("statuses_count") ? object.getInt("statuses_count") : -1;
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
        if (url != null && !url.isEmpty()) {
            Log.d(LOG_TAG, "Loading image " + url);
            Picasso.with(view.getContext())
                    .load(url)
                    .placeholder(R.drawable.placeholder_profile_image_75)
                    .transform(new RoundedTransformation(8, 0))
                    .into(view);
        }
        else
            Log.d(LOG_TAG, "Trying to load user profile image from empty URL");
    }


    // enables hardware accelerated rounded corners
    // original idea here : http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/
    public static class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin;  // dp

        // radius is corner radii in dp
        // margin is the board in dp
        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded(radius=" + radius + ", margin=" + margin + ")";
        }
    }
}
