package com.example.basiccontacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_READ_CONTACTS = 79;
    ListView list;
    ArrayList mobileArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllContactsWithPhone();
        } else {
            requestPermission();
        }
        list = findViewById(R.id.list);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, mobileArray);
        list.setAdapter(adapter);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContactsWithPhone();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    private ArrayList getAllContactsWithPhone() {
        ArrayList<String> nameList = new ArrayList<String>();
        String[] theColumns = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
                };
        int ix_ID, ix_DISPLAY_NAME_PRIMARY, ix_HAS_PHONE_NUMBER, ix_NUMBER;
        String strId, strDisplayNamePrimary, strPhoneNumber="";
        int hasPhoneNumbers, numPhoneNumbers;

        ContentResolver cr = getContentResolver();
        Uri theUri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cur = cr.query(theUri,theColumns, null, null, null);
        ix_ID = cur.getColumnIndex(ContactsContract.Contacts._ID);
        ix_DISPLAY_NAME_PRIMARY = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        ix_HAS_PHONE_NUMBER = cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        if (cur != null) {
            while (cur != null && cur.moveToNext()) {
                strId = cur.getString( ix_ID);
                strDisplayNamePrimary = cur.getString(ix_DISPLAY_NAME_PRIMARY);
                hasPhoneNumbers = cur.getInt(ix_HAS_PHONE_NUMBER);
                numPhoneNumbers=0;
                if (hasPhoneNumbers > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{strId}, null);
                    ix_NUMBER = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    if (pCur!=null) {
                        while (pCur.moveToNext()) {
                            numPhoneNumbers++;
                            if (numPhoneNumbers==1)
                                strPhoneNumber = pCur.getString(ix_NUMBER);
                        }
                        pCur.close();
                    }
                    numPhoneNumbers--;
                    if (numPhoneNumbers==0) {
                        nameList.add(strDisplayNamePrimary + " " + strPhoneNumber);
                    }
                    else{
                        nameList.add(strDisplayNamePrimary + " " + strPhoneNumber + " [+" + numPhoneNumbers + "]");
                    }
                }
            }
            cur.close();
        }

        return nameList;
    }
}