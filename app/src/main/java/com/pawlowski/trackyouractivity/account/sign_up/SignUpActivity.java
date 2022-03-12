package com.pawlowski.trackyouractivity.account.sign_up;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.account.BaseAccountActivity;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.account.sign_in.SignInActivity;
import com.pawlowski.trackyouractivity.consts.ConstAndStaticMethods;

import androidx.annotation.NonNull;

public class SignUpActivity extends BaseAccountActivity implements SignUpViewMvc.SignUpButtonsClickListener, FirebaseAuthHelper.SignInUpProcessCompleteListener{

    SignUpViewMvc mViewMvc;
    FirebaseAuthHelper mFirebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = getCompositionRoot().getViewMvcFactory().getSignUpViewMvc(null);
        setContentView(mViewMvc.getRootView());
        mViewMvc.registerListener(this);
        mFirebaseAuthHelper = getCompositionRoot().getFirebaseAuthHelper();
        mFirebaseAuthHelper.registerListener(this);

        hideNotificationBar();

        initGoogleLogin();
    }

    @Override
    public void onSubmitButtonClick() {
        String mail = mViewMvc.getMailInputValue();
        String password = mViewMvc.getPasswordInputValue();
        String repeatedPassword = mViewMvc.getPasswordRepeatInputValue();
        boolean correct = true;


        if(mail.length() == 0)
        {
            correct = false;
            mViewMvc.setMailError("This field is required!");
        }
        else if(!ConstAndStaticMethods.isMailCorrect(mail))
        {
            correct = false;
            mViewMvc.setMailError("Incorrect mail!");
        }

        if(password.length() == 0)
        {
            correct = false;
            mViewMvc.setPasswordError("This field is required!");
        }
        else if(!ConstAndStaticMethods.isPasswordCorrect(password))
        {
            correct = false;
            mViewMvc.setPasswordError("Password is to weak! It should contain minimum 6 letters, one small, one big, and one decimal character!");
        }

        if(repeatedPassword.length() == 0)
        {
            correct = false;
            mViewMvc.setPasswordRepeatError("This field is required!!");
        }
        else if(!password.equals(repeatedPassword))
        {
            correct = false;
            mViewMvc.setPasswordRepeatError("Passwords are not the same!");
        }

        if(correct)
        {
            showProgressDialog("Please wait...");
            mFirebaseAuthHelper.signUpWithMailAndPassword(mail, password);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAuthHelper.unregisterListener(this);
    }

    @Override
    public void onBackButtonClick() {
        Intent i = new Intent(this, SignInActivity.class);
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
    public void onSuccess() {
        hideProgressDialog();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("start_with_settings", true);
        startActivity(i);
        finish();
    }

    @Override
    public void onFailure(@NonNull String errorText) {
        hideProgressDialog();
        Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
    }

    //Google Sign in
    @Override
    public void onFailure(@NonNull Exception e) {
        hideProgressDialog();
        Log.w("SignInActivity", "signInWithCredential:failure", e);
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    //Google Sign in
    @Override
    public void onSuccess(AuthResult authResult) {
        onSuccess();
    }
}