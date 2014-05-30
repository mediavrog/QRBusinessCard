package net.mediavrog.qr_code_business_card.util;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.mediavrog.qr_code_business_card.R;

import ezvcard.VCard;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.StructuredName;

/**
 * Created by maikvlcek on 5/31/14.
 */
public class VCardMapper {
    public static final String TAG = VCardMapper.class.getSimpleName();

    public TextView surname, firstname, phone, mobile, web, email, street, city, zip, country, company, position, department;

    public static final int[] default_fields = new int[]{
            R.id.f_surname, R.id.f_firstname
            , R.id.f_phone, R.id.f_mobile, R.id.f_web
            , R.id.f_street, R.id.f_city, R.id.f_zip, R.id.f_country
            , R.id.f_company, R.id.f_position};

    public static final int[] extra_fields = new int[]{
            R.id.f_dept
    };

    public VCardMapper(ViewGroup g) {
        // basic fields
        surname = (TextView) g.findViewById(R.id.f_surname);
        firstname = (TextView) g.findViewById(R.id.f_firstname);
        phone = (TextView) g.findViewById(R.id.f_phone);
        mobile = (TextView) g.findViewById(R.id.f_mobile);
        email = (TextView) g.findViewById(R.id.f_mail);
        web = (TextView) g.findViewById(R.id.f_web);
        street = (TextView) g.findViewById(R.id.f_street);
        city = (TextView) g.findViewById(R.id.f_city);
        zip = (TextView) g.findViewById(R.id.f_zip);
        country = (TextView) g.findViewById(R.id.f_country);
        company = (TextView) g.findViewById(R.id.f_company);
        position = (TextView) g.findViewById(R.id.f_position);

        // extended fields
        department = (TextView) g.findViewById(R.id.f_dept);
    }

    public void fromVCard(VCard card) {
        if (card.getStructuredName() != null) {
            setTextFor(surname, card.getStructuredName().getFamily());
            setTextFor(firstname, card.getStructuredName().getGiven());
        } else if (card.getFormattedName() != null) {
            String[] splitName = TextUtils.split(card.getFormattedName().getValue(), " ");
            setTextFor(surname, splitName[0]);
            if (splitName.length > 1) setTextFor(firstname, splitName[1]);
        }
//        setTextFor(company, card.);
//        setTextFor(department, );

        // TODO error check
        Address address = card.getAddresses().get(0);
        setTextFor(street, address.getStreetAddress());
        setTextFor(city, address.getLocality());
        setTextFor(zip, address.getPostalCode());
        setTextFor(country, address.getCountry());

        // TODO proper reading
        setTextFor(phone, card.getTelephoneNumbers().get(0).getText());
        setTextFor(mobile, card.getTelephoneNumbers().get(1).getText());

        setTextFor(email, card.getEmails().get(0).getValue());
        setTextFor(web, card.getUrls().get(0).getValue());

        // extended properties
    }

    public VCard toVCard() {
        VCard vcard = new VCard();

        // basic properties
//        vcard.setFormattedName(getTextFor(surname) + " " + getTextFor(firstname));
        StructuredName name = new StructuredName();
        name.setFamily(getTextFor(surname));
        name.setGiven(getTextFor(firstname));

        vcard.setOrganization(getTextFor(company), getTextFor(department));

        Address adr = new Address();
        adr.setStreetAddress(getTextFor(street));
        adr.setLocality(getTextFor(city));
//        adr.setRegion("NY");
        adr.setPostalCode(getTextFor(zip));
        adr.setCountry(getTextFor(country));
        //adr.setLabel(adr.getStreetAddress() + "\n" + adr.Y 12345\nUSA");
        adr.addType(AddressType.WORK);
        vcard.addAddress(adr);

        vcard.addTelephoneNumber(getTextFor(phone), TelephoneType.WORK);
        vcard.addTelephoneNumber(getTextFor(mobile), TelephoneType.WORK, TelephoneType.CELL);
        vcard.addEmail(getTextFor(email), EmailType.WORK);
        vcard.addUrl(getTextFor(web));

        // extended properties
//        vcard.setKind(Kind.individual());
//        vcard.setGender(Gender.male());
//        vcard.addLanguage("en-US");
//
//        StructuredName n = new StructuredName();
//        n.setFamily("Doe");
//        n.setGiven("Jonathan");
//        n.addPrefix("Mr");
//        vcard.setStructuredName(n);
//
//        vcard.setNickname("John", "Jonny");
//        vcard.addTitle("Widget Engineer");
//
//        adr = new Address();
//        adr.setStreetAddress("123 Main St.");
//        adr.setLocality("Albany");
//        adr.setRegion("NY");
//        adr.setPostalCode("54321");
//        adr.setCountry("USA");
//        adr.setLabel("123 Main St.\nAlbany, NY 54321\nUSA");
//        adr.addType(AddressType.HOME);
//        vcard.addAddress(adr);
//
//        vcard.addEmail("johndoe@hotmail.com", EmailType.HOME);
//
//        vcard.setCategories("widgetphile", "biker", "vCard expert");

//        vcard.setGeo(37.6, -95.67);
//
//        java.util.TimeZone tz = java.util.TimeZone.getTimeZone("America/New_York");
//        vcard.setTimezone(new Timezone(tz));
//
//        File file = new File("portrait.jpg");
//        Photo photo = new Photo(file, ImageType.JPEG);
//        vcard.addPhoto(photo);
//
//        file = new File("pronunciation.ogg");
//        Sound sound = new Sound(file, SoundType.OGG);
//        vcard.addSound(sound);
//
//        vcard.setUid(Uid.random());
//
//        vcard.setRevision(Revision.now());

        return vcard;
    }

    private String getTextFor(TextView t) {
        if (t.getVisibility() == View.VISIBLE && t.getText() != null && t.getText().length() > 0) {
            return String.valueOf(t.getText());
        } else {
            return null;
        }
    }

    private void setTextFor(TextView t, String text) {
        t.setText(text);
    }
}
