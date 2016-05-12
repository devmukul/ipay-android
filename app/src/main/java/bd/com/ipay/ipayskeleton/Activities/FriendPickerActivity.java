package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments.FriendPickerFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments.IPayContactsFragment;
import bd.com.ipay.ipayskeleton.R;

public class FriendPickerActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_picker);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new FriendPickerFragment()).commit();
    }
}
