package com.pawlowski.trackyouractivity.account;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseAuthHelper {

    private final FirebaseAuth mFirebaseAuth;
    private final List<SignInUpProcessCompleteListener> listeners = new ArrayList<>();

    public FirebaseAuthHelper() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void registerListener(SignInUpProcessCompleteListener listener)
    {
        listeners.add(listener);
    }

    public void unregisterListener(SignInUpProcessCompleteListener listener)
    {
        listeners.remove(listener);
    }

    public boolean isSignedIn()
    {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    public void signOut()
    {
        mFirebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser()
    {
        return mFirebaseAuth.getCurrentUser();
    }

    public void signInWithMailAndPassword(@NonNull String mail, @NonNull String password)
    {
        mFirebaseAuth.signInWithEmailAndPassword(mail, password).addOnSuccessListener(new OnSuccessListener<>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                notifyListeners(true, null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notifyListeners(false, e.getMessage());
            }
        });
    }

    public void signUpWithMailAndPassword(@NonNull String mail, @NonNull String password)
    {
        mFirebaseAuth.createUserWithEmailAndPassword(mail, password).addOnSuccessListener(new OnSuccessListener<>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                notifyListeners(true, null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notifyListeners(false, e.getMessage());
            }
        });
    }

    public void notifyListeners(boolean isSuccess, @Nullable String errorText)
    {
        for(SignInUpProcessCompleteListener l:listeners)
        {
            if(isSuccess)
                l.onSuccess();
            else {
                assert errorText != null;
                l.onFailure(errorText);

            }
        }
    }



    public interface SignInUpProcessCompleteListener
    {
        void onSuccess();
        void onFailure(@NonNull String errorText);
    }


}
