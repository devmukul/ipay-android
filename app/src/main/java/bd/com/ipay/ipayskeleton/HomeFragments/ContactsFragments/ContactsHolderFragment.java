package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ContactsHolderFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetInviteInfoTask = null;
    public static GetInviteInfoResponse mGetInviteInfoResponse;

    private BottomSheetLayout mBottomSheetLayout;
    private Button mAllContactsSelector;
    private Button miPayContactsSelector;

    private ContactsFragment miPayAllContactsFragment;
    private ContactsFragment miPayMemberContactsFragment;

    private TextView mContactCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_holder, container, false);
        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);
        mContactCount = (TextView) v.findViewById(R.id.contact_count);

        mAllContactsSelector = (Button) v.findViewById(R.id.button_contacts_all);
        miPayContactsSelector = (Button) v.findViewById(R.id.button_contacts_ipay);

        mAllContactsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(getActivity());
                switchToAllContacts();
            }
        });

        miPayContactsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(getActivity());
                switchToiPayContacts();
            }
        });

        getInviteInfo();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switchToiPayContacts();
    }

    private void setEnabled(Button button, boolean isEnabled, boolean isLeftButton) {
        if (isEnabled) {
            button.setBackgroundResource(isLeftButton ?
                    R.drawable.background_contact_selector_active_left :
                    R.drawable.background_contact_selector_active_right);
            button.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        } else {
            button.setBackgroundResource(R.drawable.background_contact_selector_inactive);
            button.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTextPrimary));
        }
    }

    private void getInviteInfo() {
        if (mGetInviteInfoTask == null) {
            mGetInviteInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INVITE_INFO,
                    Constants.BASE_URL_MM + Constants.URL_GET_INVITE_INFO, getActivity(), this);
            mGetInviteInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void switchToAllContacts() {
        setEnabled(mAllContactsSelector, true, true);
        setEnabled(miPayContactsSelector, false, true);

        try {
            if (getActivity() != null) {
                if (miPayAllContactsFragment == null) {
                    miPayAllContactsFragment = new ContactsFragment();
                    miPayAllContactsFragment.setContactLoadFinishListener(new ContactsFragment.ContactLoadFinishListener() {
                        @Override
                        public void onContactLoadFinish(int contactCount) {
                            setContactCount(contactCount);
                        }
                    });
                }
                miPayAllContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, miPayAllContactsFragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setContactCount(final int contactCount) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContactCount.setText("Contacts (" + contactCount + ")");
            }
        });
    }

    private void switchToiPayContacts() {
        setEnabled(miPayContactsSelector, true, false);
        setEnabled(mAllContactsSelector, false, false);

        try {
            if (miPayMemberContactsFragment == null) {
                miPayMemberContactsFragment = new ContactsFragment();
                miPayMemberContactsFragment.setContactLoadFinishListener(new ContactsFragment.ContactLoadFinishListener() {
                    @Override
                    public void onContactLoadFinish(int contactCount) {
                        setContactCount(contactCount);
                    }
                });

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IPAY_MEMBERS_ONLY, true);
                miPayMemberContactsFragment.setArguments(bundle);
            }
            miPayMemberContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, miPayMemberContactsFragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        try {
            Gson gson = new Gson();

            if (result.getApiCommand().equals(Constants.COMMAND_GET_INVITE_INFO)) {
                try {
                    ContactsHolderFragment.mGetInviteInfoResponse = gson.fromJson(result.getJsonString(), GetInviteInfoResponse.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mGetInviteInfoTask = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}