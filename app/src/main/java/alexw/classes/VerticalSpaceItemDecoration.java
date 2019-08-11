package alexw.classes;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by alexw on 9/6/2017.
 */

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    //Class for creating spaces in between recycler view item view
    private final int verticalSpaceHeight;
    private final int margin;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight, int margin) {
        //Constructor
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        //Method called when object is attached to a recycler view
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = verticalSpaceHeight;
        }
        outRect.bottom = verticalSpaceHeight;
        outRect.left = margin;
        outRect.right = margin;
    }
}
