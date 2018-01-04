package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.DeviceTrustFragments.RemoveTrustedDeviceFragment;
import bd.com.ipay.ipayskeleton.R;

public class DeviceTrustActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_trust);

        switchToRemoveTrustedDeviceFragment();
    }

    public void switchToHomeActivity() {
        Intent intent = new Intent(DeviceTrustActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    public void switchToRemoveTrustedDeviceFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RemoveTrustedDeviceFragment()).commit();
    }

    public void switchToProfileCompletionHelperActivity() {
        Intent intent = new Intent(DeviceTrustActivity.this, ProfileVerificationHelperActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    public Context setContext() {
        return DeviceTrustActivity.this;
    }
}



