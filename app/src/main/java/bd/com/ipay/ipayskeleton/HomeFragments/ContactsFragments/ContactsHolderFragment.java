package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetFriendsAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.Friend.AddFriendRequest;
import bd.com.ipay.ipayskeleton.Model.Friend.AddFriendResponse;
import bd.com.ipay.ipayskeleton.Model.Friend.InfoAddFriend;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
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
    private FloatingActionButton mAddContactButton;

    private HttpRequestPostAsyncTask mAddFriendAsyncTask;
    private AddFriendResponse mAddFriendResponse;

    private EditText nameView;
    private EditText mobileNumberView;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_holder, container, false);

        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);
        mContactCount = (TextView) v.findViewById(R.id.contact_count);
        mAddContactButton = (FloatingActionButton) v.findViewById(R.id.fab_add_contact);

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

        mAddContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFriendDialog();
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());

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

    private void showAddFriendDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .title(R.string.add_a_friend)
                .autoDismiss(false)
                .customView(R.layout.dialog_add_friend, false)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel);

        View dialogView = dialog.build().getCustomView();

        nameView = (EditText) dialogView.findViewById(R.id.edit_text_name);
        mobileNumberView = (EditText) dialogView.findViewById(R.id.edit_text_mobile_number);

        dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (verifyUserInputs()) {
                            mProgressDialog.setMessage(getString(R.string.progress_dialog_adding_friend));

                            addFriend(nameView.getText().toString(), mobileNumberView.getText().toString());

                            Utilities.hideKeyboard(getActivity(), nameView);
                            Utilities.hideKeyboard(getActivity(), mobileNumberView);

                            dialog.dismiss();
                        }

                    }
                });

        dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Utilities.hideKeyboard(getActivity(), nameView);
                Utilities.hideKeyboard(getActivity(), mobileNumberView);

                dialog.dismiss();
            }
        });

        dialog.show();
        nameView.requestFocus();

    }

    private boolean verifyUserInputs() {

        boolean error = false;

        String name = nameView.getText().toString();
        String mobileNumber = mobileNumberView.getText().toString();

        if (name.isEmpty()) {
            nameView.setError(getString(R.string.error_invalid_name));
            error = true;
        }

        if (!ContactEngine.isValidNumber(mobileNumber)) {
            mobileNumberView.setError(getString(R.string.error_invalid_mobile_number));
            error = true;
        }

        return !error;
    }

    private void addFriend(String name, String phoneNumber) {
        if (mAddFriendAsyncTask != null) {
            return;
        }

        List<InfoAddFriend> newFriends = new ArrayList<>();
        newFriends.add(new InfoAddFriend(ContactEngine.formatMobileNumberBD(phoneNumber), name));

        AddFriendRequest addFriendRequest = new AddFriendRequest(newFriends);
        Gson gson = new Gson();
        String json = gson.toJson(addFriendRequest);

        mAddFriendAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_FRIENDS,
                Constants.BASE_URL_FRIEND + Constants.URL_ADD_FRIENDS, json, getActivity(), this);
        mAddFriendAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
        if (isAdded()) {
            mProgressDialog.dismiss();
        }

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetInviteInfoTask = null;
            mAddFriendAsyncTask = null;
            if (getContext() != null)
                Toast.makeText(getContext(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_INVITE_INFO)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    ContactsHolderFragment.mGetInviteInfoResponse = gson.fromJson(result.getJsonString(), GetInviteInfoResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mGetInviteInfoTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_ADD_FRIENDS)) {
            try {
                mAddFriendResponse = gson.fromJson(result.getJsonString(), AddFriendResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.add_friend_successful, Toast.LENGTH_LONG).show();
                        new GetFriendsAsyncTask(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_add_friend, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_add_friend, Toast.LENGTH_LONG).show();
            }

            mAddFriendAsyncTask = null;
        }

    }
}