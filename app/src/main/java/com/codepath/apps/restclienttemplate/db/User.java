package com.codepath.apps.restclienttemplate.db;

import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;

import com.codepath.apps.restclienttemplate.R;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dw on 01/03/17.
 */
@Table(database = MyDatabase.class)
public class User extends BaseModel {

    @PrimaryKey
    @Column
    public Long id;

    @Column
    public String name;

    @Column
    public String screenName;

    @Column
    public String profileImageUrl;

    public User() {
        super();
    }

    public User(JSONObject object) {
        super();
        try {
            this.id = object.getLong("id");
            this.name = object.getString("name");
            this.screenName = object.getString("screen_name");
            // Provided: "mini": 24x24px, "normal": 48x48px, "bigger": 73x73px, "original": WxH
            this.profileImageUrl = object.getString("profile_image_url").replace("normal", "bigger");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Log.d("User", "Loading image " + url);
        Picasso.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.placeholder_profile_image_75)
                .into(view);
    }
}
