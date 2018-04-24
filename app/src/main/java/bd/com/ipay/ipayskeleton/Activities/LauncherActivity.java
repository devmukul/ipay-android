package bd.com.ipay.ipayskeleton.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.Utilities.AppInstance.AppInstanceUtilities;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeepLinkAction;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class LauncherActivity extends AppCompatActivity {

    private boolean firstLaunch = false;
    private static final String TAG = LauncherActivity.class.getSimpleName();
    boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loggedIn = ProfileInfoCacheManager.getLoggedInStatus(true);

        String activityAction = getIntent().getAction();
        if (activityAction == null)
            activityAction = Intent.ACTION_MAIN;
        switch (activityAction) {
            case Intent.ACTION_VIEW:
                Uri uri = getIntent().getData();
                if (uri != null)
                    Logger.logD(TAG, uri.toString());
                DeepLinkAction deepLinkAction = Utilities.parseUriForDeepLinkingAction(uri);
                if (SharedPrefManager.isRememberMeActive() && loggedIn) {
                    Utilities.performDeepLinkAction(this, deepLinkAction);
                } else {
                    launchSigninOrLoginActivity(deepLinkAction);
                }
                break;
            case Intent.ACTION_MAIN:
            default:
                if (!AppInstanceUtilities.isAppAlreadyLaunched(this)) {
                    firstLaunch = SharedPrefManager.getFirstLaunch();
                    startApplication();
                }
                break;
        }


    }

    private void startApplication() {
        if (firstLaunch) {
            launchTourActivity();
        } else {
            if (SharedPrefManager.isRememberMeActive() && loggedIn) {
                launchHomeActivity();
            } else {
                launchSigninOrLoginActivity(null);
            }
        }
    }

    private void launchTourActivity() {
        Intent intent;
        intent = new Intent(LauncherActivity.this, TourActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchHomeActivity() {
        if (ProfileInfoCacheManager.isAccountSwitched()) {
            TokenManager.setOnAccountId(ProfileInfoCacheManager.getOnAccountId());
        }
        Intent intent;
        intent = new Intent(LauncherActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void launchSigninOrLoginActivity(DeepLinkAction parcelable) {
        ProfileInfoCacheManager.updateProfileInfoCache(Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()));
        Intent intent;
        intent = new Intent(LauncherActivity.this, SignupOrLoginActivity.class);
        intent.putExtra(Constants.TARGET_FRAGMENT, Constants.SIGN_IN);
        if (parcelable != null)
            intent.putExtra(Constants.DEEP_LINK_ACTION, parcelable);
        startActivity(intent);
        finish();
    }
}
