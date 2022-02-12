package com.pawlowski.trackyouractivity.account.sign_in;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.base.BaseObservableViewMvc;

import androidx.annotation.Nullable;

public class SignInViewMvc extends BaseObservableViewMvc<SignInViewMvc.SignInButtonsClickListener> {

    private final Button mRegisterButton;
    private final ImageButton mMailButton;
    private final ImageButton mGoogleButton;
    private final ImageButton mFacebookButton;

    public SignInViewMvc(LayoutInflater inflater, @Nullable ViewGroup parent) {
        rootView = inflater.inflate(R.layout.activity_sign_in, parent, false);
        mRegisterButton = findViewById(R.id.sign_up_button_sign_in);
        mMailButton = findViewById(R.id.mail_login_button_sign_in);
        mGoogleButton = findViewById(R.id.google_login_button_sign_in);
        mFacebookButton = findViewById(R.id.facebook_login_button_sign_in);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichSignInButton.REGISTER_BUTTON.ordinal());
            }
        });

        mMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichSignInButton.MAIL_BUTTON.ordinal());
            }
        });

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichSignInButton.GOOGLE_BUTTON.ordinal());
            }
        });

        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(WhichSignInButton.FACEBOOK_BUTTON.ordinal());
            }
        });
    }


    @Override
    protected void notifyListeners(@Nullable Integer which) {
        if(which == null)
            return;

        if(which == WhichSignInButton.REGISTER_BUTTON.ordinal())
        {
            for(SignInButtonsClickListener l:listeners)
            {
                l.onRegisterButtonClick();
            }
        }
        else if(which == WhichSignInButton.MAIL_BUTTON.ordinal())
        {
            for(SignInButtonsClickListener l:listeners)
            {
                l.onMailButtonClick();
            }
        }
        else if(which == WhichSignInButton.GOOGLE_BUTTON.ordinal())
        {
            for(SignInButtonsClickListener l:listeners)
            {
                l.onGoogleButtonClick();
            }
        }
        else if(which == WhichSignInButton.FACEBOOK_BUTTON.ordinal())
        {
            for(SignInButtonsClickListener l:listeners)
            {
                l.onFacebookButtonClick();
            }
        }
    }

    interface SignInButtonsClickListener
    {
        void onRegisterButtonClick();
        void onMailButtonClick();
        void onGoogleButtonClick();
        void onFacebookButtonClick();
    }

    enum WhichSignInButton
    {
        REGISTER_BUTTON,
        MAIL_BUTTON,
        GOOGLE_BUTTON,
        FACEBOOK_BUTTON
    }
}
