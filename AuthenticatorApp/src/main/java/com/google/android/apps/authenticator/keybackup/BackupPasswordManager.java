package com.google.android.apps.authenticator.keybackup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.apps.authenticator2.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by andrew.zhao on 9/15/16.
 */
public class BackupPasswordManager {

    private final String PASSWORD_PREFERENCE = "backupPassword";

    private final SharedPreferences mPreferences;

    public BackupPasswordManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean backupEnabled() {
        return mPreferences.contains(PASSWORD_PREFERENCE)
                && mPreferences.getString(PASSWORD_PREFERENCE, null).length() > 0;
    }

    public void disableBackups() {
        mPreferences.edit().putString(PASSWORD_PREFERENCE, "").commit();
    }

    public void updatePassword(String password) {
        if(password.length() == 0) {
            disableBackups();
        } else {
            mPreferences.edit().putString(PASSWORD_PREFERENCE, hash(password)).commit();
        }
    }

    public boolean verifyPassword(String password) {
        return hash(password).equals(mPreferences.getString(PASSWORD_PREFERENCE, null));
    }

    public boolean passwordSet() {
        return mPreferences.contains(PASSWORD_PREFERENCE);
    }

    public String hash(String password) {
        //salt what is salt?
        //yeah whatever this is probably good enough
        //be happy I'm not doing this in plaintext
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            //this should never happen bc all androids have sha
            Log.e("one true otp", "friggin rekt");
            return "";
        }
        try {
            digest.update(password.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            //this should also never happen
            Log.e("one true otp", "friggin rekt");
            return "";
        }
        byte[] data = digest.digest();
        String result = Base64.encodeToString(data, 0, data.length, Base64.DEFAULT);
        return result;
    }

}
