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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.apps.authenticator2.R;

import net.glxn.qrgen.android.QRCode;


/**
 * The page of the "How it works" that explains that occasionally the user might need to enter a
 * verification code generated by this application. The user simply needs to click the Next button
 * to go to the next page.
 *
 * @author klyubin@google.com (Alex Klyubin)
 */
public class DisplayKeyActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.display_key);

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    String user = extras.getString("user");
    String key = extras.getString("secret");

    Bitmap myBitmap = QRCode.from("otpauth://totp/" + user + "?secret=" + key).withSize(500, 500).bitmap();
    ImageView myImage = (ImageView) findViewById(R.id.code_qr);
    myImage.setImageBitmap(myBitmap);

    TextView keyTest = (TextView) findViewById(R.id.code_text);
    keyTest.setText(key);
  }
}
