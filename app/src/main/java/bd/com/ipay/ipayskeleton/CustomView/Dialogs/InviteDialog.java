package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
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
import bd.com.ipay.ipayskeleton.Model.Friend.InviteFriend;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class InviteDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private EditText mEditTextname;
    private EditText mEditTextMobileNumber;
    private EditText mEditTextRelationship;
    private CheckBox mIntroduceCheckbox;

    private String mName;
    private String mMobileNumber;
    private String mRelationship;

    private final Context context;
    private ProgressDialog mProgressDialog;

    private HttpRequestPostAsyncTask mAddFriendAsyncTask;
    private HttpRequestPostAsyncTask mSendInviteTask = null;
    private SendInviteResponse mSendInviteResponse;

    private CustomSelectorDialog mCustomSelectorDialog;
    private List<String> mRelationshipList;

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
                .customView(R.layout.dialog_invite_friend, true)
                .positiveText(R.string.invite)
                .negativeText(R.string.cancel);

        View dialogView = dialog.build().getCustomView();

        mEditTextname = (EditText) dialogView.findViewById(R.id.edit_text_name);
        mEditTextMobileNumber = (EditText) dialogView.findViewById(R.id.edit_text_mobile_number);
        mEditTextMobileNumber.setText(mMobileNumber);
        mEditTextRelationship = (EditText) dialogView.findViewById(R.id.edit_text_relationship);
        mIntroduceCheckbox = (CheckBox) dialogView.findViewById(R.id.introduceCheckbox);

        mRelationship = null;

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
                mEditTextRelationship.setError(null);
                mEditTextRelationship.setText(mRelation);
                mRelationship = mRelation;
            }
        });

        dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (verifyUserInputs()) {
                    mName = mEditTextname.getText().toString();
                    mMobileNumber = ContactEngine.formatMobileNumberBD(mEditTextMobileNumber.getText().toString());

                    if (mRelationship != null)
                        mRelationship = mRelationship.toUpperCase();

                    addFriend(mName, mMobileNumber, mRelationship);

                    Utilities.hideKeyboard(context, mEditTextname);
                    Utilities.hideKeyboard(context, mEditTextMobileNumber);

                    dialog.dismiss();
                }

            }
        });

        dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Utilities.hideKeyboard(context, mEditTextname);
                Utilities.hideKeyboard(context, mEditTextMobileNumber);
                dialog.dismiss();
            }
        });

        dialog.show();
        mEditTextname.requestFocus();

    }


    private boolean verifyUserInputs() {

        boolean error = false;

        String name = mEditTextname.getText().toString();
        String mobileNumber = mEditTextMobileNumber.getText().toString();

        if (name.isEmpty()) {
            mEditTextname.setError(context.getResources().getString(R.string.error_invalid_name));
            error = true;
        }

        if (!ContactEngine.isValidNumber(mobileNumber)) {
            mEditTextMobileNumber.setError(context.getResources().getString(R.string.error_invalid_mobile_number));
            error = true;
        }
        return !error;
    }


    private void addFriend(String name, String phoneNumber, String relationship) {
        if (mAddFriendAsyncTask != null) {
            return;
        }
        mProgressDialog.setMessage(context.getResources().getString(R.string.processing));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

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
            mProgressDialog.dismiss();
            Toast.makeText(context, R.string.invitaiton_limit_exceeded, Toast.LENGTH_LONG).show();
        } else {
            mProgressDialog.setMessage(context.getResources().getString(R.string.progress_dialog_sending_invite));

            boolean wantToIntroduce = mIntroduceCheckbox.isChecked();

            InviteFriend inviteFriend = new InviteFriend(phoneNumber, wantToIntroduce);
            Gson gson = new Gson();
            String json = gson.toJson(inviteFriend, InviteFriend.class);
            mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                    Constants.BASE_URL_MM + Constants.URL_SEND_INVITE, json, context, this);
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
                mProgressDialog.dismiss();
                Toast.makeText(context, R.string.failed_invite_friend, Toast.LENGTH_LONG).show();
            }

            mAddFriendAsyncTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_SEND_INVITE)) {
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

                } else {
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
