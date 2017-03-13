package org.weibeld.simpletweets.misc;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.weibeld.simpletweets.R;

/**
 * Created by dw on 07/03/17.
 */

public class DataBindingUtils {

    private static final String LOG_TAG = DataBindingUtils.class.getSimpleName();

    // Calculate the relative timestamp of the passed time and set it to the passed TextView
    @BindingAdapter({"bind:relativeTimestamp"})
    public static void setRelativeTimestamp(TextView tv, String createdAt) {
        tv.setText(Util.getRelativeTimeAgo(createdAt));
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
