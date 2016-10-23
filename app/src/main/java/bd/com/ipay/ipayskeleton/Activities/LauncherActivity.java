package bd.com.ipay.ipayskeleton.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class LauncherActivity extends AppCompatActivity {

    private boolean firstLaunch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences(Constants.ApplicationTag, MODE_PRIVATE);
        firstLaunch = pref.getBoolean(Constants.FIRST_LAUNCH, true);

        if (checkDrawOverlayPermission()) {
            startApplication();
        }

        finish();
    }

    private void startApplication() {
        Intent intent;

        if (firstLaunch) {
            intent = new Intent(LauncherActivity.this, TourActivity.class);
            startActivity(intent);
            finish();
        } else {
            intent = new Intent(LauncherActivity.this, SignupOrLoginActivity.class);
            intent.putExtra(Constants.TARGET_FRAGMENT, Constants.SIGN_IN);
            startActivity(intent);
            finish();
        }
    }

    private void showErrorDialog() {

        new MaterialDialog.Builder(LauncherActivity.this)
                .title(R.string.attention)
                .content(R.string.allow_overlay_permission)
                .cancelable(false)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        checkDrawOverlayPermission();
                    }
                })
                .show();
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                startApplication();
            } else
                showErrorDialog();  // Request the permission again
        }
    }
}
