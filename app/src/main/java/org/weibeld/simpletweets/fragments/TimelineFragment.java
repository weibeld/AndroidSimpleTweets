package org.weibeld.simpletweets.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.weibeld.simpletweets.R;
import org.weibeld.simpletweets.adapters.TweetAdapter;
import org.weibeld.simpletweets.databinding.FragmentTimelineBinding;
import org.weibeld.simpletweets.managers.OfflineModeManager;
import org.weibeld.simpletweets.misc.EndlessRecyclerViewScrollListener;
import org.weibeld.simpletweets.misc.MyApplication;
import org.weibeld.simpletweets.misc.SpacingItemDecoration;
import org.weibeld.simpletweets.misc.Util;
import org.weibeld.simpletweets.models.Tweet;
import org.weibeld.simpletweets.models.Tweet_Table;

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

    ArrayList<Tweet> mData;
    TweetAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    EndlessRecyclerViewScrollListener mScrollListener;
    SimpleDateFormat mFormatAnyDay = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.UK);
    SimpleDateFormat mFormatToday = new SimpleDateFormat("'today at' HH:mm", Locale.UK);
    SimpleDateFormat mFormatYesterday = new SimpleDateFormat("'yesterday at' HH:mm", Locale.UK);

    protected TimelineFragmentListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TimelineFragmentListener) {
            mListener = (TimelineFragmentListener) context;
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
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        b.recyclerView.setAdapter(mAdapter);
        b.recyclerView.setLayoutManager(mLayoutManager);
        b.recyclerView.addItemDecoration(new SpacingItemDecoration(26));
        b.swipeContainer.setOnRefreshListener(() -> getTweetsFromApi(1, true));
        b.swipeContainer.setColorSchemeResources(R.color.twitterStrong, R.color.twitterLight);
        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getTweetsFromApi(page, false);
            }
        };
        b.fab.setOnClickListener(v -> mListener.onFabClicked());

        OfflineModeManager modeManager = OfflineModeManager.getInstance();
        if (!modeManager.isOfflineMode()) {
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

    private ArrayList<Tweet> getTweetsFromDb() {
        return (ArrayList<Tweet>) SQLite.select().from(Tweet.class).where(Tweet_Table.type.is(getType())).orderBy(Tweet_Table.id, false).queryList();
    }

    protected abstract String getLastUpdatePrefKey();

    protected abstract int getType();

    protected abstract void getTweetsFromApi(int page, boolean isRefreshing);

    // Get the title of this fragment
    public abstract String getTitle();

    public interface TimelineFragmentListener {
        void onFabClicked();
    }

//    private void disableOfflineMode() {
//        b.tvOffline.setVisibility(View.GONE);
//        b.recyclerView.addOnScrollListener(mScrollListener);
//        mIsOfflineMode = false;
//    }
}
