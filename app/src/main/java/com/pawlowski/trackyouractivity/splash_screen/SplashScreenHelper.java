package com.pawlowski.trackyouractivity.splash_screen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import com.pawlowski.trackyouractivity.base.BaseObservable;

import androidx.annotation.NonNull;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreenViewProvider;

public class SplashScreenHelper extends BaseObservable<SplashScreenHelper.SplashListener> {

    private final Activity mActivity;
    private boolean mIsReadyForFinish = false;

    public SplashScreenHelper(Activity activity) {
        mActivity = activity;
    }

    public void initSplash()
    {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(mActivity);
        splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
            @Override
            public boolean shouldKeepOnScreen() {
                return !mIsReadyForFinish;
            }
        });

        splashScreen.setOnExitAnimationListener(new SplashScreen.OnExitAnimationListener() {
            @Override
            public void onSplashScreenExit(@NonNull SplashScreenViewProvider splashScreenViewProvider) {
                animate(splashScreenViewProvider);
            }
        });
    }

    private void animate(@NonNull SplashScreenViewProvider splashScreenViewProvider)
    {
        final View splashScreenView = splashScreenViewProvider.getView();
        final ObjectAnimator disappearAnimation = ObjectAnimator.ofFloat(
                splashScreenView,
                View.ALPHA,
                1f,
                0f
        );
        disappearAnimation.setInterpolator(new AnticipateInterpolator());
        disappearAnimation.setDuration(600L);

        // Call SplashScreenView.remove at the end of your custom animation.
        disappearAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                notifyListeners(splashScreenViewProvider);
            }
        });

        // Run your animation.
        disappearAnimation.start();
    }

    public void makeReadyForFinish()
    {
        mIsReadyForFinish = true;
    }

    @Override
    protected void notifyListeners() {
    }

    private void notifyListeners(@NonNull SplashScreenViewProvider splashScreenViewProvider)
    {
        for(SplashListener l:listeners)
        {
            l.onSplashScreenEnd(splashScreenViewProvider);
        }
    }


    public interface SplashListener
    {
        void onSplashScreenEnd(@NonNull SplashScreenViewProvider splashScreenViewProvider);
    }
}
