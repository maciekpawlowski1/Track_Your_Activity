package com.pawlowski.trackyouractivity.base;

import android.app.Dialog;
import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;

import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.composition.ActivityCompositionRoot;
import com.pawlowski.trackyouractivity.composition.PresentationCompositionRoot;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected Dialog mProgressDialog;
    protected ActivityCompositionRoot activityCompositionRoot = null;

    protected void showProgressDialog(String text)
    {
        mProgressDialog = new Dialog(this);
        mProgressDialog.setContentView(R.layout.progress_dialog);
        ((TextView) mProgressDialog.findViewById(R.id.progress_text_progress_dialog)).setText(text);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    protected void hideProgressDialog()
    {
        if(mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
    }

    protected void isDialogShowing()
    {
        if(mProgressDialog != null)
        {
            mProgressDialog.isShowing();
        }
    }

    public PresentationCompositionRoot getCompositionRoot() {
        if(activityCompositionRoot == null)
        {
            activityCompositionRoot = new ActivityCompositionRoot(BaseActivity.this);
        }
        return new PresentationCompositionRoot(activityCompositionRoot);
    }


    public ActivityCompositionRoot getActivityCompositionRoot() {
        if(activityCompositionRoot == null)
        {
            activityCompositionRoot = new ActivityCompositionRoot(BaseActivity.this);
        }
        return activityCompositionRoot;
    }

    protected void hideNotificationBar()
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
}
