package org.weibeld.simpletweets.activities;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.databinding.ActivityProfileBinding;
import org.weibeld.simpletweets.fragments.TimelineFragment;
import org.weibeld.simpletweets.fragments.UserTimelineFragment;
import org.weibeld.simpletweets.models.User;


public class ProfileActivity extends AppCompatActivity implements TimelineFragment.TimelineFragmentListener {

    ActivityProfileBinding b;
    ProfileActivity mActivity;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mActivity = this;

        setSupportActionBar(b.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_profile_activity );

        // The user whose profile to display
        mUser = (User) getIntent().getSerializableExtra(Intent.EXTRA_USER);
        b.setUser(mUser);

        // Show UserTimelineFragment
        Fragment frag = UserTimelineFragment.newInstance(mUser);
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragmentContainer, frag);
        trans.commit();
    }

    // Do not recreate parent activity, but just restart it on clicking the navigation item
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true; }

    @Override
    public void onProfileImageClicked(User user) {}
}
