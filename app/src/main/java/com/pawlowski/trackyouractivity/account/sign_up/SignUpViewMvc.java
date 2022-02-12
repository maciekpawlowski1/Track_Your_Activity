package com.pawlowski.trackyouractivity.account.sign_up;

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

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SignUpViewMvc extends BaseObservableViewMvc<SignUpViewMvc.SignUpButtonsClickListener> {
    private final Button mSubmitButton;
    private final ImageButton mBackButton;
    private final ImageButton mGoogleButton;
    private final ImageButton mFacebookButton;
    private final TextInputEditText mMailInput;
    private final TextInputLayout mMailInputLayout;
    private final TextInputEditText mPasswordInput;
    private final TextInputLayout mPasswordInputLayout;
    private final TextInputEditText mPasswordRepeatInput;
    private final TextInputLayout mPasswordRepeatInputLayout;

    public SignUpViewMvc(LayoutInflater inflater, @Nullable ViewGroup parent) {
        rootView = inflater.inflate(R.layout.activity_sign_up, parent, false);
        mSubmitButton = findViewById(R.id.submit_button_sign_up);
        mBackButton = findViewById(R.id.back_button_back_panel);
        mGoogleButton = findViewById(R.id.google_login_button_sign_up);
        mFacebookButton = findViewById(R.id.facebook_login_button_sign_up);
        mMailInput = findViewById(R.id.mail_input_edit_text_sign_up);
        mMailInputLayout = findViewById(R.id.mail_input_layout_sign_up);
        mPasswordInput = findViewById(R.id.password_input_edit_text_sign_up);
        mPasswordInputLayout = findViewById(R.id.password_input_layout_sign_up);
        mPasswordRepeatInput = findViewById(R.id.password_repeat_input_edit_text_sign_up);
        mPasswordRepeatInputLayout = findViewById(R.id.password_repeat_input_layout_sign_up);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(SignUpViewMvc.WhichSignUpButton.SUBMIT_BUTTON.ordinal());
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichSignUpButton.BACK_BUTTON.ordinal());
            }
        });

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(SignUpViewMvc.WhichSignUpButton.GOOGLE_BUTTON.ordinal());
            }
        });

        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(SignUpViewMvc.WhichSignUpButton.FACEBOOK_BUTTON.ordinal());
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

        mPasswordRepeatInput.addTextChangedListener(new TextWatcher() {
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

    public void setMailError(@Nullable String errorText)
    {
        mMailInputLayout.setError(errorText);
    }

    public void setPasswordError(@Nullable String errorText)
    {
        mPasswordInputLayout.setError(errorText);
    }

    public void setPasswordRepeatError(@Nullable String errorText)
    {
        mPasswordRepeatInputLayout.setError(errorText);
    }

    public @NonNull
    String getMailInputValue()
    {
        return Objects.requireNonNull(mMailInput.getText()).toString();
    }

    public @NonNull String getPasswordInputValue()
    {
        return Objects.requireNonNull(mPasswordInput.getText()).toString();
    }
    public @NonNull String getPasswordRepeatInputValue()
    {
        return Objects.requireNonNull(mPasswordRepeatInput.getText()).toString();
    }


    @Override
    protected void notifyListeners(@Nullable Integer which) {
        if(which == null)
            return;

        if(which == SignUpViewMvc.WhichSignUpButton.SUBMIT_BUTTON.ordinal())
        {
            for(SignUpViewMvc.SignUpButtonsClickListener l:listeners)
            {
                l.onSubmitButtonClick();
            }
        }
        else if(which == WhichSignUpButton.BACK_BUTTON.ordinal())
        {
            for(SignUpViewMvc.SignUpButtonsClickListener l:listeners)
            {
                l.onBackButtonClick();
            }
        }
        else if(which == SignUpViewMvc.WhichSignUpButton.GOOGLE_BUTTON.ordinal())
        {
            for(SignUpViewMvc.SignUpButtonsClickListener l:listeners)
            {
                l.onGoogleButtonClick();
            }
        }
        else if(which == SignUpViewMvc.WhichSignUpButton.FACEBOOK_BUTTON.ordinal())
        {
            for(SignUpViewMvc.SignUpButtonsClickListener l:listeners)
            {
                l.onFacebookButtonClick();
            }
        }
    }

    interface SignUpButtonsClickListener
    {
        void onSubmitButtonClick();
        void onBackButtonClick();
        void onGoogleButtonClick();
        void onFacebookButtonClick();
    }

    enum WhichSignUpButton
    {
        SUBMIT_BUTTON,
        BACK_BUTTON,
        GOOGLE_BUTTON,
        FACEBOOK_BUTTON
    }
}
