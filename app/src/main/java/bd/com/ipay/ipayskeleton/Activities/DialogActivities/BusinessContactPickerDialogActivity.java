package bd.com.ipay.ipayskeleton.Activities.DialogActivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.BusinessContactsFragment;
import bd.com.ipay.ipayskeleton.R;

public class BusinessContactPickerDialogActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_picker);

        BusinessContactsFragment fragment = new BusinessContactsFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }
}

