package bd.com.ipay.ipayskeleton.Activities.DialogActivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.FriendPickerFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Use this activity to pick an iPay friend. After launching the activity, get the value of
 * Constants.MOBILE_NUMBER from the intent in onActivityResult.
 *
 * If want to show only verified users, pass (Constants.VERIFIED_USERS_ONLY, false) in the intent
 * while starting the activity.
 */
public class FriendPickerDialogActivity extends FragmentActivity {

    private boolean mShowVerifiedUsersOnly;
    private boolean miPayMembersOnly;
    private boolean mBusinessMembersOnly;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_picker);

        mShowVerifiedUsersOnly = getIntent().getBooleanExtra(Constants.VERIFIED_USERS_ONLY, false);
        miPayMembersOnly = getIntent().getBooleanExtra(Constants.IPAY_MEMBERS_ONLY, false);
        mBusinessMembersOnly = getIntent().getBooleanExtra(Constants.BUSINESS_ACCOUNTS_ONLY, false);

        FriendPickerFragment fragment = new FriendPickerFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.VERIFIED_USERS_ONLY, mShowVerifiedUsersOnly);
        bundle.putBoolean(Constants.IPAY_MEMBERS_ONLY, miPayMembersOnly);
        bundle.putBoolean(Constants.BUSINESS_ACCOUNTS_ONLY, mBusinessMembersOnly);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

}
