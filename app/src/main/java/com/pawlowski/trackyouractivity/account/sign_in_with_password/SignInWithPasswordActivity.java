package com.pawlowski.trackyouractivity.account.sign_in_with_password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.account.sign_in.SignInActivity;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpActivity;

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
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onGoogleButtonClick() {

    }

    @Override
    public void onFacebookButtonClick() {

    }

    @Override
    public void onBackButtonClick() {
        Intent i = new Intent(this, SignInActivity.class);
        startActivity(i);
        finish();
    }
}