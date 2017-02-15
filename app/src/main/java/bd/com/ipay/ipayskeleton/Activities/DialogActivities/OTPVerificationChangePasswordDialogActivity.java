package bd.com.ipay.ipayskeleton.Activities.DialogActivities;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.OTPVerificationChangePasswordFragment;

public class OTPVerificationChangePasswordDialogActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_picker);

       /* FragmentManager fm = getFragmentManager();
        OTPVerificationChangePasswordFragment dialogFragment = new OTPVerificationChangePasswordFragment ();
        dialogFragment.show(fm,"hsh");*/
    }
}
