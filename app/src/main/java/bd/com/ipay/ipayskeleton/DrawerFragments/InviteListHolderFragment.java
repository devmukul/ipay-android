package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.ContactsFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class InviteListHolderFragment extends BaseFragment {

    private ContactsFragment miPayAllContactsFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite_holder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchToAllContacts();
    }

    private void switchToAllContacts() {
        try {
            if (getActivity() != null) {
                if (miPayAllContactsFragment == null) {
                    miPayAllContactsFragment = new ContactsFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.SHOW_ALL_MEMBERS, true);
                miPayAllContactsFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, miPayAllContactsFragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}