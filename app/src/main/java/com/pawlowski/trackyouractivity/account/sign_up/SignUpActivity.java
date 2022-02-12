package com.pawlowski.trackyouractivity.account.sign_up;

import android.content.Intent;
import android.os.Bundle;

import com.pawlowski.trackyouractivity.account.sign_in.SignInActivity;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements SignUpViewMvc.SignUpButtonsClickListener{

    SignUpViewMvc mViewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new SignUpViewMvc(getLayoutInflater(), null);
        setContentView(mViewMvc.getRootView());
        mViewMvc.registerListener(this);
    }

    @Override
    public void onSubmitButtonClick() {

    }

    @Override
    public void onBackButtonClick() {
        Intent i = new Intent(this, SignInActivity.class);
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