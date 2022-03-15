package com.pawlowski.trackyouractivity.account.sign_in;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.account.BaseAccountActivity;
import com.pawlowski.trackyouractivity.account.sign_in_with_password.SignInWithPasswordActivity;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpActivity;

import androidx.annotation.NonNull;

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
        SignUpActivity.launch(this);
        finish();
    }

    @Override
    public void onMailButtonClick() {
        SignInWithPasswordActivity.launch(this);
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
        MainActivity.launch(this, false);
        finish();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    public static void launch(Context context)
    {
        context.startActivity(new Intent(context, SignInActivity.class));
    }

}