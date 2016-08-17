package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetFriendsAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.ContactsHolderFragment;
import bd.com.ipay.ipayskeleton.Model.Friend.AddFriendRequest;
import bd.com.ipay.ipayskeleton.Model.Friend.InfoAddFriend;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class InviteDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private EditText nameView;
    private EditText mobileNumberView;
    private String mMobileNumber;

    private final Context context;
    private ProgressDialog mProgressDialog;

    private HttpRequestPostAsyncTask mAddFriendAsyncTask;
    private HttpRequestPostAsyncTask mSendInviteTask = null;
    private SendInviteResponse mSendInviteResponse;

    private EditText mEditTextRelationship;
    private CustomSelectorDialog mCustomSelectorDialog;
    private List<String> mRelationshipList;

    private String mRelationship;
    private int mSelectedRelationId = -1;

    private FinishCheckerListener mFinishCheckerListener;

    public InviteDialog(Context context, String mMobileNumber) {
        super(context);
        this.context = context;
        this.mMobileNumber = mMobileNumber;
        mProgressDialog = new ProgressDialog(context);
        showAddFriendDialog();
    }

    public void setFinishCheckerListener(FinishCheckerListener finishChceckerListener) {
        mFinishCheckerListener = finishChceckerListener;
    }

    private void showAddFriendDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context);
        dialog
                .title(R.string.invite_a_friend)
                .autoDismiss(false)
                .customView(R.layout.dialog_add_friend, true)
                .positiveText(R.string.invite)
                .negativeText(R.string.cancel);

        View dialogView = dialog.build().getCustomView();

        nameView = (EditText) dialogView.findViewById(R.id.edit_text_name);
        mobileNumberView = (EditText) dialogView.findViewById(R.id.edit_text_mobile_number);
        mobileNumberView.setText(mMobileNumber);

        mEditTextRelationship = (EditText) dialogView.findViewById(R.id.edit_text_relationship);

        Utilities.showKeyboard(context);

        mRelationshipList = Arrays.asList(context.getResources().getStringArray(R.array.relationship));
        mCustomSelectorDialog = new CustomSelectorDialog(context, context.getResources().getString(R.string.relationship), mRelationshipList);

        mEditTextRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomSelectorDialog.show();
            }
        });

        mCustomSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int selectedIndex, String mRelation) {
                mEditTextRelationship.setText(mRelation);
                mSelectedRelationId = selectedIndex;
                mRelationship = mRelation;
            }
        });

        dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (verifyUserInputs()) {

                    mMobileNumber = ContactEngine.formatMobileNumberBD(mobileNumberView.getText().toString());
                    mProgressDialog.setMessage(context.getResources().getString(R.string.progress_dialog_sending_invite));

                    addFriend(nameView.getText().toString(), mobileNumberView.getText().toString(), mRelationship.toUpperCase());
                    Utilities.hideKeyboard(context, nameView);
                    Utilities.hideKeyboard(context, mobileNumberView);

                    dialog.dismiss();
                }

            }
        });

        dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Utilities.hideKeyboard(context, nameView);
                Utilities.hideKeyboard(context, mobileNumberView);
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
            nameView.setError(context.getResources().getString(R.string.error_invalid_name));
            error = true;
        }

        if (!ContactEngine.isValidNumber(mobileNumber)) {
            mobileNumberView.setError(context.getResources().getString(R.string.error_invalid_mobile_number));
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
                Constants.BASE_URL_FRIEND + Constants.URL_ADD_FRIENDS, json, context, this);
        mAddFriendAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void sendInvite(String phoneNumber) {
        int numberOfInvitees = ContactsHolderFragment.mGetInviteInfoResponse.invitees.size();
        if (numberOfInvitees >= ContactsHolderFragment.mGetInviteInfoResponse.totalLimit) {
            Toast.makeText(context, R.string.invitaiton_limit_exceeded, Toast.LENGTH_LONG).show();
        } else {
            mProgressDialog.setMessage(context.getResources().getString(R.string.progress_dialog_sending_invite));
            mProgressDialog.show();

            mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                    Constants.BASE_URL_MM + Constants.URL_SEND_INVITE + phoneNumber, null, context, this);
            mSendInviteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mAddFriendAsyncTask = null;
            mSendInviteTask = null;

            Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ADD_FRIENDS)) {
            try {

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    new GetFriendsAsyncTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    sendInvite(mMobileNumber);
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(context, R.string.failed_invite_friend, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.failed_invite_friend, Toast.LENGTH_LONG).show();
            }

            mAddFriendAsyncTask = null;
        } else  if (result.getApiCommand().equals(Constants.COMMAND_SEND_INVITE)) {
            mProgressDialog.dismiss();
            try {
                mSendInviteResponse = gson.fromJson(result.getJsonString(), SendInviteResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Toast.makeText(context, R.string.invitation_sent, Toast.LENGTH_LONG).show();

                    if (ContactsHolderFragment.mGetInviteInfoResponse != null)
                        ContactsHolderFragment.mGetInviteInfoResponse.invitees.add(mMobileNumber);

                    if (mFinishCheckerListener != null) {
                        mFinishCheckerListener.ifFinishNeeded();
                    }

                } else  {
                    Toast.makeText(context, mSendInviteResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.failed_sending_invitation, Toast.LENGTH_LONG).show();

            }
            mSendInviteTask = null;
        }
    }

    public interface FinishCheckerListener {
        void ifFinishNeeded();
    }
}
