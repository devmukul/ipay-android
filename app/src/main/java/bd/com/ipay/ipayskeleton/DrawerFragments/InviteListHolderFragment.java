package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.ContactsFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InviteListHolderFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetInviteInfoTask = null;
    public static GetInviteInfoResponse mGetInviteInfoResponse;

    private CheckBox mAllContactsSelector;
    private CheckBox mInvitedContactsSelector;

    private ContactsFragment miPayAllContactsFragment;
    private ContactsFragment miPayMemberContactsFragment;

    private TextView mContactCount;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invite_holder, container, false);

        mContactCount = (TextView) v.findViewById(R.id.contact_count);

        mAllContactsSelector = (CheckBox) v.findViewById(R.id.checkbox_contacts_all);
        mInvitedContactsSelector = (CheckBox) v.findViewById(R.id.checkbox_contacts_invited);

        mAllContactsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(getActivity());
                switchToAllContacts();
            }
        });

        mInvitedContactsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(getActivity());
                switchToInvitedContacts();
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());

        getInviteInfo();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switchToAllContacts();
    }

    private void getInviteInfo() {
        if (mGetInviteInfoTask == null) {
            mGetInviteInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INVITE_INFO,
                    Constants.BASE_URL_MM + Constants.URL_GET_INVITE_INFO, getActivity(), this);
            mGetInviteInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void switchToAllContacts() {
        mAllContactsSelector.setChecked(true);
        mInvitedContactsSelector.setChecked(false);

        mAllContactsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));
        mInvitedContactsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));

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
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.HIDE_STATUSES, true);
                bundle.putBoolean(Constants.SHOW_NON_INVITED_NON_MEMBERS_ONLY, true);
                miPayAllContactsFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, miPayAllContactsFragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchToInvitedContacts() {
        mAllContactsSelector.setChecked(false);
        mInvitedContactsSelector.setChecked(true);

        mAllContactsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));
        mInvitedContactsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));

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
                bundle.putBoolean(Constants.HIDE_STATUSES, true);
                bundle.putBoolean(Constants.SHOW_INVITED_ONLY, true);
                miPayMemberContactsFragment.setArguments(bundle);
            }
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, miPayMemberContactsFragment).commit();
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

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (isAdded()) {
            mProgressDialog.dismiss();
        }

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetInviteInfoTask = null;
            if (getContext() != null)
                Toast.makeText(getContext(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_INVITE_INFO)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    InviteListHolderFragment.mGetInviteInfoResponse = gson.fromJson(result.getJsonString(), GetInviteInfoResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mGetInviteInfoTask = null;

        }
    }
}