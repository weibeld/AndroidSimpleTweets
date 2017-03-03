package org.weibeld.simpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.weibeld.simpletweets.databinding.ItemTweetBinding;
import org.weibeld.simpletweets.db.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dw on 01/03/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private static final String LOG_TAG = TweetAdapter.class.getSimpleName();

    Context mContext;
    ArrayList<Tweet> mData;

    public TweetAdapter(ArrayList<Tweet> data, Context context) {
        mData = data;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(LOG_TAG, "onCreateViewHolder: viewType == " + viewType);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemTweetBinding binding = ItemTweetBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mData.get(position);
        ItemTweetBinding binding = holder.getBinding();
        binding.setTweet(tweet);
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Remove all items from the adapter
    public void clear() {
        int oldSize = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, oldSize);
    }

    // Append a list of items to the end of the adapter
    public void append(List<Tweet> tweets) {
        int oldSize = mData.size();
        mData.addAll(tweets);
        notifyItemRangeInserted(oldSize, tweets.size());
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTweetBinding b;
        ViewHolder(ItemTweetBinding binding) {
            super(binding.getRoot());
            b = binding;
        }
        ItemTweetBinding getBinding() {
            return b;
        }
    }
}
