//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.19
//
package com.Temoc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.StartupAuthErrorDetails;
import com.amazonaws.mobile.auth.core.StartupAuthResult;
import com.amazonaws.mobile.auth.core.StartupAuthResultHandler;

/**
 * Splash Activity is the start-up activity that appears until a delay is expired
 * or the user taps the screen.  When the splash activity starts, various app
 * initialization operations are performed.
 */
public class SplashActivity extends Activity implements StartupAuthResultHandler {

    private static final String LOG_TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final IdentityManager identityManager = IdentityManager.getDefaultIdentityManager();

        identityManager.doStartupAuth(this, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Touch event bypasses waiting for the splash timeout to expire.
        IdentityManager.getDefaultIdentityManager().expireSignInTimeout();
        return true;
    }

    @Override
    public void onComplete(StartupAuthResult authResult) {
            if (authResult.isUserAnonymous()) {
                Log.d(LOG_TAG, "Continuing with unauthenticated (guest) identity.");
            } else {
                final StartupAuthErrorDetails errors = authResult.getErrorDetails();
                Log.e(LOG_TAG, "No Identity could be obtained. Continuing with no identity.",
                        errors.getUnauthenticatedErrorException());
            }
        this.startActivity(new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        this.finish();
    }
}
