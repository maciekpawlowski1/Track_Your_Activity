package com.pawlowski.trackyouractivity.account.sign_in;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.account.sign_in_with_password.SignInWithPasswordActivity;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpActivity;
import com.pawlowski.trackyouractivity.base.BaseActivity;

public class SignInActivity extends BaseActivity implements SignInViewMvc.SignInButtonsClickListener {

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

    }

    @Override
    public void onFacebookButtonClick() {

    }
}