package bd.com.ipay.ipayskeleton.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetFriendsAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DrawerFragments.InviteListHolderFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.ContactsHolderFragment;
import bd.com.ipay.ipayskeleton.Model.Friend.AddFriendRequest;
import bd.com.ipay.ipayskeleton.Model.Friend.InfoAddFriend;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InviteActivity extends BaseActivity {

    private FloatingActionButton mSendInviteButton;

    private EditText nameView;
    private EditText mobileNumberView;
    private String mMobileNumber;

    private ProgressDialog mProgressDialog;

    private HttpRequestPostAsyncTask mAddFriendAsyncTask;
    private HttpRequestPostAsyncTask mSendInviteTask = null;
    private SendInviteResponse mSendInviteResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        mProgressDialog = new ProgressDialog(this);

        mSendInviteButton = (FloatingActionButton) findViewById(R.id.fab_invite);

        mSendInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFriendDialog();
            }
        });

        switchToInviteListHolderFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void switchToInviteListHolderFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InviteListHolderFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showAddFriendDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(this);
        dialog
                .title(R.string.invite_a_friend)
                .autoDismiss(false)
                .customView(R.layout.dialog_add_friend, false)
                .positiveText(R.string.invite)
                .negativeText(R.string.cancel);

        View dialogView = dialog.build().getCustomView();

        nameView = (EditText) dialogView.findViewById(R.id.edit_text_name);
        mobileNumberView = (EditText) dialogView.findViewById(R.id.edit_text_mobile_number);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (verifyUserInputs()) {

                    mMobileNumber = ContactEngine.formatMobileNumberBD(mobileNumberView.getText().toString());
                    mProgressDialog.setMessage(getString(R.string.progress_dialog_sending_invite));

                    addFriend(nameView.getText().toString(), mobileNumberView.getText().toString());
                    Utilities.hideKeyboard(InviteActivity.this, nameView);
                    Utilities.hideKeyboard(InviteActivity.this, mobileNumberView);

                    dialog.dismiss();
                }

            }
        });

        dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Utilities.hideKeyboard(InviteActivity.this, nameView);
                Utilities.hideKeyboard(InviteActivity.this, mobileNumberView);
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
                Constants.BASE_URL_FRIEND + Constants.URL_ADD_FRIENDS, json, this, this);
        mAddFriendAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void sendInvite(String phoneNumber) {
        int numberOfInvitees = ContactsHolderFragment.mGetInviteInfoResponse.invitees.size();
        if (numberOfInvitees >= ContactsHolderFragment.mGetInviteInfoResponse.totalLimit) {
            Toast.makeText(this, R.string.invitaiton_limit_exceeded,
                    Toast.LENGTH_LONG).show();
        } else {
            mProgressDialog.setMessage(this.getString(R.string.progress_dialog_sending_invite));
            mProgressDialog.show();

            mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                    Constants.BASE_URL_MM + Constants.URL_SEND_INVITE + phoneNumber, null, this, this);
            mSendInviteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected Context setContext() {
        return InviteActivity.this;
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mAddFriendAsyncTask = null;
            mSendInviteTask = null;

           Toast.makeText(this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ADD_FRIENDS)) {
            try {

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    new GetFriendsAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    sendInvite(mMobileNumber);
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(this, R.string.failed_invite_friend, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.failed_invite_friend, Toast.LENGTH_LONG).show();
            }

            mAddFriendAsyncTask = null;
        } else  if (result.getApiCommand().equals(Constants.COMMAND_SEND_INVITE)) {
            mProgressDialog.dismiss();
            try {
                mSendInviteResponse = gson.fromJson(result.getJsonString(), SendInviteResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        Toast.makeText(this, R.string.invitation_sent, Toast.LENGTH_LONG).show();

                    if (ContactsHolderFragment.mGetInviteInfoResponse != null)
                        ContactsHolderFragment.mGetInviteInfoResponse.invitees.add(mMobileNumber);

                } else  {
                    Toast.makeText(this, mSendInviteResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.failed_sending_invitation, Toast.LENGTH_LONG).show();

            }

            mSendInviteTask = null;

        }

    }
}
