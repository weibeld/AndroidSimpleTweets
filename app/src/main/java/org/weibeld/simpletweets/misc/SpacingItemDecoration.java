package org.weibeld.simpletweets.misc;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by dw on 07/03/17.
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public SpacingItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = verticalSpaceHeight;
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
            outRect.bottom = verticalSpaceHeight;
    }
}
