package com.pawlowski.trackyouractivity.account.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.account.BaseAccountActivity;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.account.sign_in_with_password.SignInWithPasswordActivity;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpActivity;

import androidx.annotation.NonNull;

public class SignInActivity extends BaseAccountActivity implements SignInViewMvc.SignInButtonsClickListener {

    SignInViewMvc mViewMvc;
    FirebaseAuthHelper mFirebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new SignInViewMvc(getLayoutInflater(), null);
        mViewMvc.registerListener(this);
        setContentView(mViewMvc.getRootView());
        mFirebaseAuthHelper = new FirebaseAuthHelper();

        hideNotificationBar();

        if(mFirebaseAuthHelper.isSignedIn())    //TODO: Create splash screen and move it there
        {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else
        {
            initGoogleLogin();
        }

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