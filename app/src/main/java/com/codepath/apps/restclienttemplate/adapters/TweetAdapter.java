package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.db.Tweet;

import java.util.ArrayList;

/**
 * Created by dw on 01/03/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private static final String LOG_TAG = TweetAdapter.class.getSimpleName();

//    private static final int VIEW_TYPE_REGULAR = 0;
//    private static final int VIEW_TYPE_LAST = 1;


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

//    @Override
//    public int getItemViewType(int position) {
//        if (position < mData.size()-1)
//            return VIEW_TYPE_REGULAR;
//        else
//            return VIEW_TYPE_LAST;
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivProfileImage;
//        TextView tvUsername;
//        TextView tvText;

        private ItemTweetBinding b;

        // Create a viewHolder for the passed view (item view)
//        ViewHolder(View view) {
//            super(view);
//            ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
//            tvUsername = (TextView) view.findViewById(R.id.tvUsername);
//            tvText = (TextView) view.findViewById(R.id.tvText);
//        }
        ViewHolder(ItemTweetBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        ItemTweetBinding getBinding() {
            return b;
        }


    }
}
