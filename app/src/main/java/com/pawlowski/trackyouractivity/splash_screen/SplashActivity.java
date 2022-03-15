package com.pawlowski.trackyouractivity.splash_screen;

import androidx.annotation.NonNull;
import androidx.core.splashscreen.SplashScreenViewProvider;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.account.sign_in.SignInActivity;
import com.pawlowski.trackyouractivity.base.BaseActivity;

public class SplashActivity extends BaseActivity implements SplashScreenHelper.SplashListener {
    private SplashScreenHelper mSplashScreenHelper;
    private FirebaseAuthHelper mFirebaseAuthHelper;
    private boolean mIsSignedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSplashScreenHelper = getCompositionRoot().getSplashScreenHelper();
        mSplashScreenHelper.registerListener(this);
        mSplashScreenHelper.initSplash();
        super.onCreate(savedInstanceState);
        mFirebaseAuthHelper = getCompositionRoot().getFirebaseAuthHelper();

        mIsSignedIn = mFirebaseAuthHelper.isSignedIn();

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance(), true);

        mSplashScreenHelper.makeReadyForFinish();
    }



    @Override
    public void onSplashScreenEnd(@NonNull SplashScreenViewProvider splashScreenViewProvider) {
        if(mIsSignedIn)
        {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
        else
        {
            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
        }
        finish();


    }
}