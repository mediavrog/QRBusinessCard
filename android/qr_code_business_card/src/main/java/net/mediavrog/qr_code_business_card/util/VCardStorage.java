package net.mediavrog.qr_code_business_card.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;

/**
 * Created by maikvlcek on 5/31/14.
 */
public class VCardStorage {
    public static final String TAG = VCardStorage.class.getSimpleName();
    public static final String PREFS_KEY = "vcards";

    public static VCard getVCard(Context ctx, String id) {
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_KEY, 0);
        String serializedCard = settings.getString(id, null);
        return serializedCard != null ? Ezvcard.parse(serializedCard).first() : null;
    }

    public static void saveVCard(Context ctx, String id, VCard vCard) {
        String serializedCard = Ezvcard.write(vCard).version(VCardVersion.V4_0).go();
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_KEY, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            settings.edit().putString(id, serializedCard).apply();
        } else {
            settings.edit().putString(id, serializedCard).commit();
        }
    }
}
