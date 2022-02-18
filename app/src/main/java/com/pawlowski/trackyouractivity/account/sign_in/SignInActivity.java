package com.pawlowski.trackyouractivity.account.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.pawlowski.trackyouractivity.MainActivity;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.account.BaseAccountActivity;
import com.pawlowski.trackyouractivity.account.FirebaseAuthHelper;
import com.pawlowski.trackyouractivity.account.sign_in_with_password.SignInWithPasswordActivity;
import com.pawlowski.trackyouractivity.account.sign_up.SignUpActivity;
import com.pawlowski.trackyouractivity.base.BaseActivity;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SignInActivity extends BaseAccountActivity implements SignInViewMvc.SignInButtonsClickListener {

    SignInViewMvc mViewMvc;
    FirebaseAuthHelper mFirebaseAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMvc = new SignInViewMvc(getLayoutInflater(), null);
        mViewMvc.registerListener(this);
        setContentView(mViewMvc.getRootView());
        mFirebaseAuthHelper = new FirebaseAuthHelper();

        hideNotificationBar();

        if(mFirebaseAuthHelper.isSignedIn())    //TODO: Create splash screen and move it there
        {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else
        {
            initGoogleLogin();
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
        signInByGoogle();
    }

    @Override
    public void onFacebookButtonClick() {

    }


    @Override
    public void onSuccess(AuthResult authResult) {
        hideProgressDialog();
        Log.d("SignInActivity", "signInWithCredential:success");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        hideProgressDialog();
        Log.w("SignInActivity", "signInWithCredential:failure", e);
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }
}