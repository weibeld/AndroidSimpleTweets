package com.codepath.apps.restclienttemplate.db;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

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
            this.profileImageUrl = object.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Picasso.with(view.getContext())
                .load(url)
                .into(view);
    }
}
