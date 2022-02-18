package com.pawlowski.trackyouractivity.account;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.base.BaseActivity;

import androidx.annotation.Nullable;

public abstract class BaseAccountActivity extends BaseActivity implements OnSuccessListener<AuthResult>, OnFailureListener {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 62;



    protected void initGoogleLogin()
    {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        showProgressDialog("Please wait...");

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, this)
                .addOnFailureListener(this, this);
    }

    protected void signInByGoogle()
    {
        assert mGoogleSignInClient != null;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_SIGN_IN_REQUEST_CODE && data != null)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            }
            else
            {
                Log.w("SignInActivity", "Google sign in failed");
            }
        }
    }
}
