package com.m.freemovie.Utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
@SuppressWarnings("deprecation")
public class WindowUtils {
    public WindowUtils (Activity activity,boolean isSeries){
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        if(isSeries){
            ViewCompat.setOnApplyWindowInsetsListener(
                    activity.getWindow().getDecorView(),
                    (view, windowInsets) -> {
                        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            windowInsetsController.show(WindowInsetsCompat.Type.statusBars());
                            activity.getWindow().setStatusBarColor(Color.parseColor("#000000"));
                        }else{
                            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
                        }
                        return ViewCompat.onApplyWindowInsets(view, windowInsets);
                    });
        }else{
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        }

    }
}
