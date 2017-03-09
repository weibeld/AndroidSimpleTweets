package org.weibeld.simpletweets.misc;

import android.databinding.BindingAdapter;
import android.widget.TextView;

/**
 * Created by dw on 07/03/17.
 */

public class DataBindingUtils {

    // Calculate the relative timestamp of the passed time and set it to the passed TextView
    @BindingAdapter({"bind:relativeTimestamp"})
    public static void setRelativeTimestamp(TextView tv, String createdAt) {
        tv.setText(Util.getRelativeTimeAgo(createdAt));
    }
}
