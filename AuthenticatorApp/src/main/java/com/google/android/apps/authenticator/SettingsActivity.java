/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.apps.authenticator;

import com.google.android.apps.authenticator.keybackup.BackupPasswordManager;
import com.google.android.apps.authenticator.keybackup.DisplayKeyActivity;
import com.google.android.apps.authenticator.testability.DependencyInjector;
import com.google.android.apps.authenticator.testability.TestablePreferenceActivity;
import com.google.android.apps.authenticator2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Top-level preferences Activity.
 *
 * @author klyubin@google.com (Alex Klyubin)
 */
public class SettingsActivity extends TestablePreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        final BackupPasswordManager backupPasswordManager = DependencyInjector.getBackupPasswordManager();
        final PreferenceScreen screen = getPreferenceScreen();
        final Preference disableExportButton = findPreference("disableExport");
        final Preference changePasswordButton = findPreference("updatePassword");

        if (backupPasswordManager.backupEnabled()) {
            final SettingsActivity thisActivity = this;
            disableExportButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //code for what you want it to do
                    new AlertDialog.Builder(thisActivity)
                            .setTitle("disable export")
                            .setMessage("disabling export cannot be reversed without clearing app data")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            backupPasswordManager.disableBackups();
                                            screen.removePreference(disableExportButton);
                                        }
                                    })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                    return true;
                }
            });


            changePasswordButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    verifyAndUpdatePassword();
                    return true;
                }
            });
        } else {
            screen.removePreference(disableExportButton);
            screen.removePreference(changePasswordButton);
        }
    }


    private void verifyAndUpdatePassword() {
        //update callback hell
        final BackupPasswordManager manager = DependencyInjector.getBackupPasswordManager();
        final View frames = getLayoutInflater().inflate(R.layout.rename,
                (ViewGroup) findViewById(R.id.rename_root));
        final EditText nameEdits = (EditText) frames.findViewById(R.id.rename_edittext);
        final Activity thisActivity = this;
        new AlertDialog.Builder(this)
                .setTitle("Enter old password")
                .setView(frames)
                .setPositiveButton("continue",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (manager.verifyPassword(nameEdits.getText().toString())) {
                                    updatePassword();
                                } else {
                                    Toast.makeText(thisActivity, "Incorrect Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void updatePassword() {
        //update calback hell
        final BackupPasswordManager manager = DependencyInjector.getBackupPasswordManager();
        final View frames = getLayoutInflater().inflate(R.layout.rename,
                (ViewGroup) findViewById(R.id.rename_root));
        final EditText nameEdits = (EditText) frames.findViewById(R.id.rename_edittext);
        new AlertDialog.Builder(this)
                .setTitle("Enter new password")
                .setView(frames)
                .setPositiveButton("continue",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                manager.updatePassword(nameEdits.getText().toString());
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
