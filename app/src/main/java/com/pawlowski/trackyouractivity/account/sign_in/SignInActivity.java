package com.pawlowski.trackyouractivity.account.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.account.BaseAccountActivity;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.account.sign_in_with_password.SignInWithPasswordActivity;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpActivity;

import androidx.annotation.NonNull;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreenViewProvider;

public class SignInActivity extends BaseAccountActivity implements SignInViewMvc.SignInButtonsClickListener {

    SignInViewMvc mViewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = getCompositionRoot().getViewMvcFactory().getSignInViewMvc(null);
        mViewMvc.registerListener(this);
        setContentView(mViewMvc.getRootView());

        hideNotificationBar();
        initGoogleLogin();

    }




    @Override
    public void onRegisterButtonClick() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onMailButtonClick() {
        Intent i = new Intent(this, SignInWithPasswordActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onGoogleButtonClick() {
        signInByGoogle();
    }

    @Override
    public void onFacebookButtonClick() {

    }


    @Override
    public void onSuccess(AuthResult authResult) {
        hideProgressDialog();
        Log.d("SignInActivity", "signInWithCredential:success");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        hideProgressDialog();
        Log.w("SignInActivity", "signInWithCredential:failure", e);
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }


}