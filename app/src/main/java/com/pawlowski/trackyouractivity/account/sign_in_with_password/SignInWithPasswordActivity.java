package com.pawlowski.trackyouractivity.account.sign_in_with_password;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pawlowski.trackyouractivity.R;

public class SignInWithPasswordActivity extends AppCompatActivity implements SignInWithPasswordViewMvc.SignInWithPasswordButtonsClickListener {

    SignInWithPasswordViewMvc mViewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new SignInWithPasswordViewMvc(getLayoutInflater(), null);
        setContentView(mViewMvc.getRootView());
        mViewMvc.registerListener(this);
    }

    @Override
    public void onSubmitButtonClick() {

    }

    @Override
    public void onRegisterWithMailButtonClick() {

    }

    @Override
    public void onGoogleButtonClick() {

    }

    @Override
    public void onFacebookButtonClick() {

    }
}