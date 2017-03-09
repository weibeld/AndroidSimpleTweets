package org.weibeld.simpletweets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.databinding.ActivityProfileBinding;
import org.weibeld.simpletweets.models.User;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding b;
    ProfileActivity mActivity;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mActivity = this;

        // The user whose profile to display
        mUser = (User) getIntent().getSerializableExtra(Intent.EXTRA_USER);
        b.setUser(mUser);

    }
}
