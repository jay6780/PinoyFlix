package com.m.freemovie.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class WindowUtils {
    public WindowUtils (Activity activity,boolean isSeries){
        if(isSeries){
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
            ViewCompat.setOnApplyWindowInsetsListener(
                    activity.getWindow().getDecorView(),
                    (view, windowInsets) -> {
                        if (windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())) {
                            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
                        }
                        return ViewCompat.onApplyWindowInsets(view, windowInsets);
                    });
        }else{
            View decorView = activity.getWindow().getDecorView();
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

            decorView.setSystemUiVisibility(flags);
        }

    }
}
