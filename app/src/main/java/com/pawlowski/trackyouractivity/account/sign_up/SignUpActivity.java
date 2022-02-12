package com.pawlowski.trackyouractivity.account.sign_up;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pawlowski.trackyouractivity.R;

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

    }

    @Override
    public void onGoogleButtonClick() {

    }

    @Override
    public void onFacebookButtonClick() {

    }
}