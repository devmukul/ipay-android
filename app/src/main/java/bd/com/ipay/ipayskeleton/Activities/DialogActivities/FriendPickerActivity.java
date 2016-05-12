package bd.com.ipay.ipayskeleton.Activities.DialogActivities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments.FriendPickerFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments.IPayContactsFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FriendPickerActivity extends FragmentActivity {

    private boolean mShowVerifiedUsersOnly;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_picker);

        mShowVerifiedUsersOnly = getIntent().getBooleanExtra(Constants.VERIFIED_USERS_ONLY, false);

        FriendPickerFragment fragment = new FriendPickerFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.VERIFIED_USERS_ONLY, mShowVerifiedUsersOnly);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }
}
