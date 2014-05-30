package net.mediavrog.qr_code_business_card.util;

import android.util.Log;

import net.mediavrog.qr_code_business_card.BuildConfig;


/**
 * Created by Maik on 03.03.14.
 */
public class L {
    public static void w(String tag, Object message) {
        if (BuildConfig.LOG) {
            Log.w(tag, String.valueOf(message));
        }
    }

    public static void d(String tag, Object message) {
        if (BuildConfig.LOG) {
            Log.d(tag, String.valueOf(message));
        }
    }

    public static void i(String tag, Object message) {
        if (BuildConfig.LOG) {
            Log.i(tag, String.valueOf(message));
        }
    }

    public static void e(String tag, Object message) {
        e(tag, message, null);
    }

    public static void e(String tag, Object message, Throwable e) {
        if (BuildConfig.LOG) {
            Log.e(tag, String.valueOf(message), e);
        }
    }
}
