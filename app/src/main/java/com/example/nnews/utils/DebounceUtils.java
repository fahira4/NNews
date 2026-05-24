package com.example.nnews.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Utility untuk debounce — menunda eksekusi hingga
 * user berhenti mengetik selama durasi tertentu.
 */
public class DebounceUtils {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingRunnable;
    private final long delayMillis;

    public DebounceUtils(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    /**
     * Jalankan runnable setelah delay.
     * Jika dipanggil lagi sebelum delay habis,
     * timer di-reset dari awal.
     */
    public void debounce(Runnable runnable) {
        // Batalkan runnable sebelumnya
        if (pendingRunnable != null) {
            handler.removeCallbacks(pendingRunnable);
        }
        pendingRunnable = runnable;
        handler.postDelayed(pendingRunnable, delayMillis);
    }

    /**
     * Batalkan semua pending runnable.
     * Panggil di onDestroyView untuk cegah memory leak.
     */
    public void cancel() {
        if (pendingRunnable != null) {
            handler.removeCallbacks(pendingRunnable);
            pendingRunnable = null;
        }
    }
}