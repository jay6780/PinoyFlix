package com.m.freemovie.Utils;

import android.app.Activity;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

@SuppressWarnings("deprecation")
public class GlobalWindowUtils {
    public GlobalWindowUtils(Activity activity) {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(
                () -> applyWindowState(windowInsetsController));
    }

    private void applyWindowState(WindowInsetsControllerCompat controller) {
        controller.hide(WindowInsetsCompat.Type.navigationBars());
    }
}
