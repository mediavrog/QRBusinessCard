package net.mediavrog.qr_code_business_card.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by maikvlcek on 5/31/14.
 */
public class Utils {
    public static final String TAG = Utils.class.getSimpleName();

    public static DisplayMetrics getScreenDimensions(Activity act) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }
}
