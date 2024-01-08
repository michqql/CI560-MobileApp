package me.mp1282.shoppinglist.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import me.mp1282.shoppinglist.R;

import java.util.function.Consumer;

public class SwipeLeftDeleteHelper extends ItemTouchHelper.SimpleCallback {

    private final Context context;
    private final Consumer<Integer> leftAction;

    public SwipeLeftDeleteHelper(Context context, Consumer<Integer> leftAction) {
        super(0, (leftAction != null ? ItemTouchHelper.LEFT : 0));
        this.context = context;
        this.leftAction = leftAction;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT && leftAction != null) {
            leftAction.accept(viewHolder.getAdapterPosition());
        }
    }

    /*
     * dX is the horizontal offset of how far the user has swiped the view holder
     */
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(dX < 0) {
            // Swiping left
            c.clipRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(),
                    viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());

            final GradientDrawable background = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT,
                    new int[] { context.getResources().getColor(R.color.deleteRed, null), 0x00000000 });
            // Here we want to plus dX to the left position, because dX is negative
            background.setBounds(viewHolder.itemView.getLeft() + (int) dX, viewHolder.itemView.getTop(),
                    viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
            background.draw(c);

            final Drawable icon = ContextCompat.getDrawable(context, R.drawable.baseline_delete_24);
            if(icon != null) {
                int iconSize = icon.getIntrinsicHeight();
                int top = viewHolder.itemView.getTop()
                        + ((viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) / 2
                                - (iconSize / 2));
                int left = viewHolder.itemView.getRight() - iconSize;

                icon.setBounds(left - 50, top, viewHolder.itemView.getRight() - 50, top + iconSize);
                icon.draw(c);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
