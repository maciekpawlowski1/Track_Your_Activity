package com.pawlowski.trackyouractivity.account.sign_in;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.account.sign_in_with_password.SignInWithPasswordActivity;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpActivity;

public class SignInActivity extends AppCompatActivity implements SignInViewMvc.SignInButtonsClickListener {

    SignInViewMvc mViewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new SignInViewMvc(getLayoutInflater(), null);
        mViewMvc.registerListener(this);
        setContentView(mViewMvc.getRootView());

        hideNotificationBar();

        //startActivity(new Intent(this, MainActivity.class));
    }

    private void hideNotificationBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
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