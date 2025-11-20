package com.m.freemovie.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

public class WindowUtils {
    public WindowUtils (Activity activity){
        View decorView = activity.getWindow().getDecorView();
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);

        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        decorView.setSystemUiVisibility(flags);

    }
}
