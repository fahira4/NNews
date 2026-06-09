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
 * Swipe dibatasi maksimal 40% lebar item.
 */
public abstract class SwipeToDeleteCallback
        extends ItemTouchHelper.SimpleCallback {

    private final Paint paint = new Paint();
    private final Drawable deleteIcon;
    private final int iconMargin;

    // Batas maksimal swipe — 40% dari lebar item
    private static final float SWIPE_THRESHOLD = 0.4f;

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

    /**
     * Override threshold — item dianggap "swiped"
     * setelah digeser 40% dari lebarnya.
     */
    @Override
    public float getSwipeThreshold(
            @NonNull RecyclerView.ViewHolder viewHolder) {
        return SWIPE_THRESHOLD;
    }

    /**
     * Batasi seberapa jauh item bisa digeser
     * maksimal 40% dari lebar item.
     */
    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 3f;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return defaultValue * 0.7f;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState,
                            boolean isCurrentlyActive) {

        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            super.onChildDraw(canvas, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive);
            return;
        }

        // Hitung batas maksimal geser (40% lebar item)
        float maxSwipe = viewHolder.itemView.getWidth()
                * SWIPE_THRESHOLD;

        // Clamp dX agar tidak melebihi batas
        float clampedDx;
        if (dX > 0) {
            clampedDx = Math.min(dX, maxSwipe);
        } else {
            clampedDx = Math.max(dX, -maxSwipe);
        }

        float itemTop = viewHolder.itemView.getTop();
        float itemBottom = viewHolder.itemView.getBottom();
        float itemLeft = viewHolder.itemView.getLeft();
        float itemRight = viewHolder.itemView.getRight();
        float cornerRadius = 12f;
        float padding = iconMargin / 2f;

        if (clampedDx > 0) {
            // Swipe kanan → background merah di kiri
            RectF background = new RectF(
                    itemLeft,
                    itemTop + padding,
                    itemLeft + clampedDx + cornerRadius,
                    itemBottom - padding
            );
            canvas.drawRoundRect(background, cornerRadius,
                    cornerRadius, paint);

            // Icon delete
            if (deleteIcon != null) {
                int iconSize = deleteIcon.getIntrinsicHeight();
                int iconTop = (int) (itemTop
                        + (itemBottom - itemTop - iconSize) / 2);
                int iconLeft = (int) (itemLeft + iconMargin);
                int iconRight = iconLeft
                        + deleteIcon.getIntrinsicWidth();
                int iconBottom = iconTop + iconSize;

                // Tampilkan icon hanya jika ada ruang cukup
                if (clampedDx > iconMargin + iconSize) {
                    deleteIcon.setBounds(
                            iconLeft, iconTop, iconRight, iconBottom
                    );
                    deleteIcon.draw(canvas);
                }
            }

        } else if (clampedDx < 0) {
            // Swipe kiri → background merah di kanan
            RectF background = new RectF(
                    itemRight + clampedDx - cornerRadius,
                    itemTop + padding,
                    itemRight,
                    itemBottom - padding
            );
            canvas.drawRoundRect(background, cornerRadius,
                    cornerRadius, paint);

            // Icon delete
            if (deleteIcon != null) {
                int iconSize = deleteIcon.getIntrinsicHeight();
                int iconTop = (int) (itemTop
                        + (itemBottom - itemTop - iconSize) / 2);
                int iconRight = (int) (itemRight - iconMargin);
                int iconLeft = iconRight
                        - deleteIcon.getIntrinsicWidth();
                int iconBottom = iconTop + iconSize;

                // Tampilkan icon hanya jika ada ruang cukup
                if (Math.abs(clampedDx) > iconMargin + iconSize) {
                    deleteIcon.setBounds(
                            iconLeft, iconTop, iconRight, iconBottom
                    );
                    deleteIcon.draw(canvas);
                }
            }
        }

        // Gunakan clampedDx agar animasi item mengikuti batas
        super.onChildDraw(canvas, recyclerView, viewHolder,
                clampedDx, dY, actionState, isCurrentlyActive);
    }
}