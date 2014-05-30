package net.mediavrog.qr_code_business_card;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.mediavrog.qr_code_business_card.util.L;
import net.mediavrog.qr_code_business_card.util.Utils;
import net.mediavrog.qr_code_business_card.util.VCardMapper;
import net.mediavrog.qr_code_business_card.util.VCardStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;


public class SetupActivity extends ActionBarActivity implements View.OnClickListener {
    public final static String TAG = SetupActivity.class.getSimpleName();
    private static final int CONTACT_PICKER_RESULT = 1001;
    private VCard card;
    private VCardMapper mapper;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private boolean extraFieldsShown = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        View view = getLayoutInflater().inflate(R.layout.actionbar_edit_mode, null);
        ab.setCustomView(view);
        ab.setDisplayShowCustomEnabled(true);
        toggleExtraFields();

        findViewById(R.id.contact_picker).setOnClickListener(this);
        findViewById(R.id.action_bar_button_cancel).setOnClickListener(this);
        findViewById(R.id.action_bar_button_ok).setOnClickListener(this);

        mapper = new VCardMapper((ViewGroup) findViewById(R.id.container));
        card = VCardStorage.getVCard(this, "main");
        if (card != null) mapper.fromVCard(card);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        switch (id) {
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_picker:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
                break;
            case R.id.action_bar_button_cancel:
                onBackPressed();
                break;
            case R.id.action_bar_button_ok:
                // TODO use AsyncTask
                VCard card = mapper.toVCard();
                String cardId = "main";
                // save vcard
                VCardStorage.saveVCard(this, cardId, card);
                // create qr code
                String charset = "UTF-8";
                DisplayMetrics screenSize = Utils.getScreenDimensions(this);
                int qrCodeSize = Math.min(screenSize.heightPixels, screenSize.widthPixels);
                try {
                    Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
                    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

                    BitMatrix matrix = new MultiFormatWriter().encode(
                            new String(Ezvcard.write(card).version(VCardVersion.V4_0).go().getBytes(charset), charset)
                            , BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);

                    int width = matrix.getWidth();
                    int height = matrix.getHeight();
                    int[] pixels = new int[width * height];
                    for (int y = 0; y < height; y++) {
                        int offset = y * width;
                        for (int x = 0; x < width; x++) {
                            pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                        }
                    }

                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                    FileOutputStream out = new FileOutputStream(new File(getCacheDir(), cardId + ".png"));
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e(TAG, "FUCK");
                }
                // notice and move move move
                Toast.makeText(this, "Update vcard and move to next style screen", Toast.LENGTH_SHORT);
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
//                    String id = data.getData().getLastPathSegment();
//                    Cursor cursor = getContentResolver().query(
//                            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
//                            new String[]{id}, null);
//                    if (cursor.moveToFirst()) {
//                        String columns[] = cursor.getColumnNames();
//                        email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//
//                        VCard vcard = new VCard();
//
//                        StructuredName n = new StructuredName();
//                        n.setFamily("Doe");
//                        n.setGiven("Jonathan");
//                        n.addPrefix("Mr");
//                        vcard.setStructuredName(n);
//
//                        vcard.setFormattedName("John Doe");
//                        updateFields(card);
//                    }
//                    cursor.close();
                    break;
            }

        } else {
            // gracefully handle failure
            L.w(TAG, "Warning: activity result not ok");
        }
    }

    private void toggleExtraFields() {
        for (int id : VCardMapper.extra_fields) {
            findViewById(id).setVisibility(extraFieldsShown ? View.GONE : View.VISIBLE);
        }
        extraFieldsShown = !extraFieldsShown;
    }
}
