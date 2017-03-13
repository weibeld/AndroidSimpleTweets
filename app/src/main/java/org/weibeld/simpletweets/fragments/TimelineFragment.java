package org.weibeld.simpletweets.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.adapters.TweetAdapter;
import org.weibeld.simpletweets.databinding.FragmentTimelineBinding;
import org.weibeld.simpletweets.events.OfflineToOnlineEvent;
import org.weibeld.simpletweets.managers.OfflineModeManager;
import org.weibeld.simpletweets.misc.EndlessRecyclerViewScrollListener;
import org.weibeld.simpletweets.misc.MyApplication;
import org.weibeld.simpletweets.misc.Util;
import org.weibeld.simpletweets.models.Tweet;
import org.weibeld.simpletweets.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by dw on 07/03/17.
 */

public abstract class TimelineFragment extends Fragment {

    private static final String LOG_TAG = TimelineFragment.class.getSimpleName();

    FragmentTimelineBinding b;

    protected ArrayList<Tweet> mData;
    protected TweetAdapter mAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected EndlessRecyclerViewScrollListener mScrollListener;
    protected SimpleDateFormat mFormatAnyDay = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.UK);
    protected SimpleDateFormat mFormatToday = new SimpleDateFormat("'today at' HH:mm", Locale.UK);
    protected SimpleDateFormat mFormatYesterday = new SimpleDateFormat("'yesterday at' HH:mm", Locale.UK);
    protected OfflineModeManager mOfflineMgr;
    protected TimelineFragmentListener mGeneralListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TimelineFragmentListener) {
            mGeneralListener = (TimelineFragmentListener) context;
        } else {
            throw new ClassCastException(context.getClass().getSimpleName() + " must implement " + TimelineFragmentListener.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);

        mData = new ArrayList();
        mAdapter = new TweetAdapter(mData);
        mAdapter.setOnProfileImageClickListener(position -> {
            mGeneralListener.onProfileImageClicked(mData.get(position).user);
        });
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        b.recyclerView.setAdapter(mAdapter);
        b.recyclerView.setLayoutManager(mLayoutManager);
        //b.recyclerView.addItemDecoration(new SpacingItemDecoration(26));
        b.recyclerView.addItemDecoration(new DividerItemDecoration(b.recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        b.swipeContainer.setOnRefreshListener(() -> getTweetsFromApi(1, true));
        b.swipeContainer.setColorSchemeResources(R.color.twitterStrong, R.color.twitterLight);
        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getTweetsFromApi(page, false);
            }
        };

        mOfflineMgr = OfflineModeManager.getInstance();
        if (!mOfflineMgr.isOfflineMode()) {
            b.recyclerView.addOnScrollListener(mScrollListener);
            getTweetsFromApi(1, false);
        }
        else {
            // Set up offline indicator
            Long ts = MyApplication.getPrefs().getLong(getLastUpdatePrefKey(), 0);
            Calendar date = GregorianCalendar.getInstance();
            date.setTimeInMillis(ts);
            String dateStr;
            if (Util.isToday(date))
                dateStr = mFormatToday.format(new Date(ts));
            else if (Util.isYesterday(date))
                dateStr = mFormatYesterday.format(new Date(ts));
            else
                dateStr = mFormatAnyDay.format(new Date(ts));
            String text = String.format(getString(R.string.offline_timeline), dateStr);
            SpannableString msg = new SpannableString(text);
            msg.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, 0);
            msg.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 8, msg.length(), 0);
            b.tvOffline.setText(msg);
            b.tvOffline.setVisibility(View.VISIBLE);

            // Disable endless scrolling
            b.recyclerView.removeOnScrollListener(mScrollListener);

            // Clear data in memory (if any) and load data from database
            mAdapter.clear();
            ArrayList<Tweet> dbTweets = getTweetsFromDb();
            mAdapter.append(dbTweets);
        }

        return b.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected abstract ArrayList<Tweet> getTweetsFromDb();

    protected abstract String getLastUpdatePrefKey();

    protected abstract void getTweetsFromApi(int page, boolean isRefreshing);

    // Get the title of this fragment
    public abstract String getTitle();

    public interface TimelineFragmentListener {
        void onProfileImageClicked(User user);
    }

    // Called by OfflineModeManager through EventBus when switching from offline to online mode
    @Subscribe
    protected void onOfflineToOnlineEvent(OfflineToOnlineEvent e) {
        b.tvOffline.setVisibility(View.GONE);
        b.recyclerView.addOnScrollListener(mScrollListener);
    }
}
