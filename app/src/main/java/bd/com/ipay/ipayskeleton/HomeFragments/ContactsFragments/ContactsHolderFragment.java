package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetFriendsAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.Friend.AddFriendRequest;
import bd.com.ipay.ipayskeleton.Model.Friend.InfoAddFriend;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.IntroductionAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ContactsHolderFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetInviteInfoTask = null;
    public static GetInviteInfoResponse mGetInviteInfoResponse;

    private BottomSheetLayout mBottomSheetLayout;
    private CheckBox mAllContactsSelector;
    private CheckBox miPayContactsSelector;

    private ContactsFragment miPayAllContactsFragment;
    private ContactsFragment miPayMemberContactsFragment;

    private TextView mContactCount;
    private FloatingActionButton mAddContactButton;

    private HttpRequestPostAsyncTask mAddFriendAsyncTask;

    private EditText nameView;
    private EditText mobileNumberView;

    private ProgressDialog mProgressDialog;

    private EditText mEditTextRelationship;
    private ResourceSelectorDialog mResourceSelectorDialog;

    private String mName;
    private String mMobileNumber;
    private String mRelationship;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_contact_holder, container, false);

        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);
        mContactCount = (TextView) v.findViewById(R.id.contact_count);
        mAddContactButton = (FloatingActionButton) v.findViewById(R.id.fab_add_contact);

        mAllContactsSelector = (CheckBox) v.findViewById(R.id.checkbox_contacts_all);
        miPayContactsSelector = (CheckBox) v.findViewById(R.id.checkbox_contacts_ipay);

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switchToiPayContacts();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(true);

        if (menu.findItem(R.id.action_filter_by_service) != null)
            menu.findItem(R.id.action_filter_by_service).setVisible(false);
        if (menu.findItem(R.id.action_filter_by_date) != null)
            menu.findItem(R.id.action_filter_by_date).setVisible(false);
    }

    private void showAddFriendDialog() {
        MaterialDialog.Builder addFriendDialog = new MaterialDialog.Builder(getActivity());
        addFriendDialog
                .title(R.string.add_a_friend)
                .autoDismiss(false)
                .customView(R.layout.dialog_add_friend, false)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel);

        View dialogView = addFriendDialog.build().getCustomView();

        nameView = (EditText) dialogView.findViewById(R.id.edit_text_name);
        mobileNumberView = (EditText) dialogView.findViewById(R.id.edit_text_mobile_number);
        mEditTextRelationship = (EditText) dialogView.findViewById(R.id.edit_text_relationship);
        mRelationship = null;

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        mResourceSelectorDialog = new ResourceSelectorDialog(getActivity(), getString(R.string.relationship), CommonData.getRelationshipList());
        mResourceSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int selectedIndex, String mRelation) {
                mEditTextRelationship.setError(null);
                mEditTextRelationship.setText(mRelation);
                mRelationship = mRelation;
            }
        });

        mEditTextRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResourceSelectorDialog.show();
            }
        });

        addFriendDialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (verifyUserInputs()) {
                    mName = nameView.getText().toString();
                    mMobileNumber = mobileNumberView.getText().toString();
                    mProgressDialog.setMessage(getString(R.string.progress_dialog_adding_friend));

                    if (mRelationship != null)
                        mRelationship = mRelationship.toUpperCase();

                    if (new ContactSearchHelper(getActivity()).searchMobileNumber(mMobileNumber))
                        Toast.makeText(getContext(), R.string.contact_already_exists, Toast.LENGTH_LONG).show();
                    else
                        addFriend(mName, mMobileNumber, mRelationship);

                    Utilities.hideKeyboard(getActivity(), nameView);
                    Utilities.hideKeyboard(getActivity(), mobileNumberView);

                    dialog.dismiss();
                }
            }
        });

        addFriendDialog.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Utilities.hideKeyboard(getActivity(), nameView);
                Utilities.hideKeyboard(getActivity(), mobileNumberView);

                dialog.dismiss();
            }
        });

        addFriendDialog.show();
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

    private void addFriend(String name, String phoneNumber, String relationship) {
        if (mAddFriendAsyncTask != null) {
            return;
        }

        List<InfoAddFriend> newFriends = new ArrayList<>();
        newFriends.add(new InfoAddFriend(ContactEngine.formatMobileNumberBD(phoneNumber), name, relationship));

        AddFriendRequest addFriendRequest = new AddFriendRequest(newFriends);
        Gson gson = new Gson();
        String json = gson.toJson(addFriendRequest);

        mAddFriendAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_FRIENDS,
                Constants.BASE_URL_FRIEND + Constants.URL_ADD_CONTACTS, json, getActivity(), this);
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
        mAllContactsSelector.setChecked(true);
        miPayContactsSelector.setChecked(false);

        mAllContactsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));
        miPayContactsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));

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

    private void switchToiPayContacts() {
        mAllContactsSelector.setChecked(false);
        miPayContactsSelector.setChecked(true);

        mAllContactsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));
        miPayContactsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));

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

    private void setContactCount(final int contactCount) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContactCount.setText(getString(R.string.contacts) + " (" + contactCount + ")");
            }
        });
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
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