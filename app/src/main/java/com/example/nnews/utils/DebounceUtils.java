package com.example.nnews.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Utility debounce — menunda eksekusi hingga user
 * berhenti mengetik selama durasi yang ditentukan.
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
     * Setiap kali dipanggil, timer di-reset.
     */
    public void debounce(Runnable runnable) {
        cancel();
        pendingRunnable = runnable;
        handler.postDelayed(pendingRunnable, delayMillis);
    }

    /**
     * Batalkan pending runnable.
     */
    public void cancel() {
        if (pendingRunnable != null) {
            handler.removeCallbacks(pendingRunnable);
            pendingRunnable = null;
        }
    }
}