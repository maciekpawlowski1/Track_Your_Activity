package com.pawlowski.trackyouractivity.account.sign_in_with_password;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.base.BaseObservableViewMvc;

import androidx.annotation.Nullable;

public class SignInWithPasswordViewMvc extends BaseObservableViewMvc<SignInWithPasswordViewMvc.SignInWithPasswordButtonsClickListener> {
    private final Button mSubmitButton;
    private final ImageButton mMailButton;
    private final ImageButton mGoogleButton;
    private final ImageButton mFacebookButton;
    private final TextInputEditText mMailInput;
    private final TextInputEditText mPasswordInput;
    private final TextInputLayout mMailInputLayout;
    private final TextInputLayout mPasswordInputLayout;

    public SignInWithPasswordViewMvc(LayoutInflater inflater, @Nullable ViewGroup parent) {
        rootView = inflater.inflate(R.layout.activity_sign_in_with_password, parent, false);
        mSubmitButton = findViewById(R.id.submit_button_sign_in_with_mail);
        mMailButton = findViewById(R.id.mail_login_button_sign_in_with_mail);
        mGoogleButton = findViewById(R.id.google_login_button_sign_in_with_mail);
        mFacebookButton = findViewById(R.id.facebook_login_button_sign_in_with_mail);
        mMailInput = findViewById(R.id.mail_input_edit_text_sign_in_with_mail);
        mPasswordInput = findViewById(R.id.password_input_edit_text_sign_in_with_mail);
        mMailInputLayout = findViewById(R.id.mail_input_layout_sign_in_with_mail);
        mPasswordInputLayout = findViewById(R.id.password_input_layout_sign_in_with_mail);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichSignInWithPasswordButton.SUBMIT_BUTTON.ordinal());
            }
        });

        mMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichSignInWithPasswordButton.REGISTER_WITH_MAIL_BUTTON.ordinal());
            }
        });

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(SignInWithPasswordViewMvc.WhichSignInWithPasswordButton.GOOGLE_BUTTON.ordinal());
            }
        });

        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(SignInWithPasswordViewMvc.WhichSignInWithPasswordButton.FACEBOOK_BUTTON.ordinal());
            }
        });

        mMailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mMailInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPasswordInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void setMailError(String errorText)
    {
        mMailInputLayout.setError(errorText);
    }

    public void setPasswordError(String errorText)
    {
        mPasswordInputLayout.setError(errorText);
    }


    @Override
    protected void notifyListeners(@Nullable Integer which) {
        if(which == null)
            return;

        if(which == WhichSignInWithPasswordButton.SUBMIT_BUTTON.ordinal())
        {
            for(SignInWithPasswordViewMvc.SignInWithPasswordButtonsClickListener l:listeners)
            {
                l.onSubmitButtonClick();
            }
        }
        else if(which == WhichSignInWithPasswordButton.REGISTER_WITH_MAIL_BUTTON.ordinal())
        {
            for(SignInWithPasswordViewMvc.SignInWithPasswordButtonsClickListener l:listeners)
            {
                l.onRegisterWithMailButtonClick();
            }
        }
        else if(which == SignInWithPasswordViewMvc.WhichSignInWithPasswordButton.GOOGLE_BUTTON.ordinal())
        {
            for(SignInWithPasswordViewMvc.SignInWithPasswordButtonsClickListener l:listeners)
            {
                l.onGoogleButtonClick();
            }
        }
        else if(which == SignInWithPasswordViewMvc.WhichSignInWithPasswordButton.FACEBOOK_BUTTON.ordinal())
        {
            for(SignInWithPasswordViewMvc.SignInWithPasswordButtonsClickListener l:listeners)
            {
                l.onFacebookButtonClick();
            }
        }
    }

    interface SignInWithPasswordButtonsClickListener
    {
        void onSubmitButtonClick();
        void onRegisterWithMailButtonClick();
        void onGoogleButtonClick();
        void onFacebookButtonClick();
    }

    enum WhichSignInWithPasswordButton
    {
        SUBMIT_BUTTON,
        REGISTER_WITH_MAIL_BUTTON,
        GOOGLE_BUTTON,
        FACEBOOK_BUTTON
    }
}
