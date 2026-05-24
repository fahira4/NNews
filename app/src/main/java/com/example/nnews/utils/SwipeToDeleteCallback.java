package com.example.nnews.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nnews.R;

/**
 * Callback untuk swipe-to-delete di RecyclerView.
 * Menampilkan background merah dengan icon delete saat swipe.
 */
public abstract class SwipeToDeleteCallback
        extends ItemTouchHelper.SimpleCallback {

    private final Paint paint = new Paint();
    private final Drawable deleteIcon;
    private final int iconMargin;

    public SwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

        deleteIcon = ContextCompat.getDrawable(
                context, R.drawable.ic_delete
        );
        iconMargin = (int) context.getResources()
                .getDimension(R.dimen.spacing_md);
        paint.setColor(
                ContextCompat.getColor(context, R.color.colorError)
        );
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false; // Tidak support drag & drop
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float itemTop = viewHolder.itemView.getTop();
            float itemBottom = viewHolder.itemView.getBottom();
            float itemLeft = viewHolder.itemView.getLeft();
            float itemRight = viewHolder.itemView.getRight();
            float cornerRadius = 12f;

            if (dX > 0) {
                // Swipe kanan → background merah di kiri
                RectF background = new RectF(
                        itemLeft,
                        itemTop + iconMargin / 2f,
                        dX + cornerRadius,
                        itemBottom - iconMargin / 2f
                );
                canvas.drawRoundRect(background, cornerRadius,
                        cornerRadius, paint);

                // Icon delete
                if (deleteIcon != null) {
                    int iconTop = (int) (itemTop + (itemBottom - itemTop
                            - deleteIcon.getIntrinsicHeight()) / 2);
                    int iconLeft = (int) (itemLeft + iconMargin);
                    int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                    deleteIcon.setBounds(iconLeft, iconTop,
                            iconRight, iconBottom);
                    deleteIcon.draw(canvas);
                }

            } else if (dX < 0) {
                // Swipe kiri → background merah di kanan
                RectF background = new RectF(
                        itemRight + dX - cornerRadius,
                        itemTop + iconMargin / 2f,
                        itemRight,
                        itemBottom - iconMargin / 2f
                );
                canvas.drawRoundRect(background, cornerRadius,
                        cornerRadius, paint);

                // Icon delete
                if (deleteIcon != null) {
                    int iconTop = (int) (itemTop + (itemBottom - itemTop
                            - deleteIcon.getIntrinsicHeight()) / 2);
                    int iconRight = (int) (itemRight - iconMargin);
                    int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                    deleteIcon.setBounds(iconLeft, iconTop,
                            iconRight, iconBottom);
                    deleteIcon.draw(canvas);
                }
            }
        }

        super.onChildDraw(canvas, recyclerView, viewHolder,
                dX, dY, actionState, isCurrentlyActive);
    }
}