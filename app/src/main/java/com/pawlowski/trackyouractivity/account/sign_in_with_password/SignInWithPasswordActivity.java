package com.pawlowski.trackyouractivity.account.sign_in_with_password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.account.BaseAccountActivity;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.account.sign_in.SignInActivity;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpActivity;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;

import androidx.annotation.NonNull;

public class SignInWithPasswordActivity extends BaseAccountActivity implements SignInWithPasswordViewMvc.SignInWithPasswordButtonsClickListener, FirebaseAuthHelper.SignInUpProcessCompleteListener {

    SignInWithPasswordViewMvc mViewMvc;
    FirebaseAuthHelper mFirebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = getCompositionRoot().getViewMvcFactory().getSignInWithPasswordViewMvc(null);
        setContentView(mViewMvc.getRootView());
        mViewMvc.registerListener(this);
        mFirebaseAuthHelper = getCompositionRoot().getFirebaseAuthHelper();
        mFirebaseAuthHelper.registerListener(this);

        hideNotificationBar();
        initGoogleLogin();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAuthHelper.unregisterListener(this);
    }

    @Override
    public void onSubmitButtonClick() {
        String mail = mViewMvc.getMailInputValue();
        String password = mViewMvc.getPasswordInputValue();
        boolean correct = true;
        if(!ConstAndStaticMethods.isMailCorrect(mail))
        {
            correct = false;
            mViewMvc.setMailError("Incorrect mail!");
        }
        if(password.length() == 0)
        {
            correct = false;
            mViewMvc.setPasswordError("This field is required!");
        }

        if(correct)
        {
            showProgressDialog("Please wait...");
            mFirebaseAuthHelper.signInWithMailAndPassword(mail, password);
        }

    }

    @Override
    public void onRegisterWithMailButtonClick() {
        SignUpActivity.launch(this);
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
    public void onBackButtonClick() {
        SignInActivity.launch(this);
        finish();
    }

    @Override
    public void onSuccess() {
        hideProgressDialog();
        MainActivity.launch(this, false);
        finish();
    }

    @Override
    public void onFailure(@NonNull String errorText) {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
    }


    //Google Sign In
    @Override
    public void onFailure(@NonNull Exception e) {
        hideProgressDialog();
        Log.w("SignInActivity", "signInWithCredential:failure", e);
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }
    //Google Sign In
    @Override
    public void onSuccess(AuthResult authResult) {
        onSuccess();
    }

    public static void launch(Context context)
    {
        context.startActivity(new Intent(context, SignInWithPasswordActivity.class));
    }
}