package bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.HelpAndSupportActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.GetEmailResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.CreateTicketRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.CreateTicketResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateTicketFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCreateTicketTask = null;
    private CreateTicketResponse mCreateTicketResponse;

    private HttpRequestGetAsyncTask mGetEmailsTask = null;
    private GetEmailResponse mGetEmailResponse;

    private EditText mMessageEditText;
    private EditText mSubjectEditText;
    private Button mCreateTicketButton;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_ticket, container, false);

        mSubjectEditText = (EditText) v.findViewById(R.id.subject);
        mMessageEditText = (EditText) v.findViewById(R.id.message);
        mCreateTicketButton = (Button) v.findViewById(R.id.button_create_ticket);

        mProgressDialog = new ProgressDialog(getActivity());

        mCreateTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    Utilities.hideKeyboard(getActivity());
                    createTicket();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE))
            getEmails();
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE);

            if (json == null)
                getEmails();
            else {
                processGetEmailListResponse(json);
            }
        }

    }

    private void processGetEmailListResponse(String json) {
        try {
            Gson gson = new Gson();
            mGetEmailResponse = gson.fromJson(json, GetEmailResponse.class);

            if (mGetEmailResponse.getEmailAdressList().isEmpty()) {
                MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
                dialog
                        .title(R.string.no_email_added)
                        .content(R.string.dialog_add_new_email)
                        .cancelable(false)
                        .positiveText(R.string.add_email)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                launchEmailPage();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ((HelpAndSupportActivity) getActivity()).switchToTicketListFragment();
                            }
                        })
                        .show();
            } else {
                String primaryEmail = mGetEmailResponse.getVerifiedEmail();
                if (primaryEmail == null) {
                    MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
                    dialog
                            .title(R.string.no_primary_email)
                            .content(R.string.dialog_verify_email)
                            .positiveText(R.string.verify_email)
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    launchEmailPage();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    ((HelpAndSupportActivity) getActivity()).switchToTicketListFragment();
                                }
                            })
                            .show();
                } else {

                }
            }

            setContentShown(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchEmailPage() {
        getActivity().onBackPressed();
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(Constants.TARGET_FRAGMENT, ProfileCompletionPropertyConstants.VERIFIED_EMAIL);
        startActivity(intent);
    }

    private void showCreateTicketSuccessDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .title(R.string.ticket_created)
                .content(R.string.ticket_created_dialog_text)
                .cancelable(false)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getActivity().onBackPressed();
                    }
                })
                .show();
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        if (mSubjectEditText.getText().toString().isEmpty()) {
            cancel = true;
            focusView = mSubjectEditText;
            mSubjectEditText.setError(getString(R.string.failed_empty_subject));
        }

        if (mMessageEditText.getText().toString().isEmpty()) {
            cancel = true;
            focusView = mMessageEditText;
            mMessageEditText.setError(getString(R.string.failed_empty_message));
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void createTicket() {
        if (mCreateTicketTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.creating_ticket));
        mProgressDialog.show();

        CreateTicketRequest createTicketRequest = new CreateTicketRequest(mSubjectEditText.getText().toString(), mMessageEditText.getText().toString());

        Gson gson = new Gson();
        String json = gson.toJson(createTicketRequest);

        mCreateTicketTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CREATE_TICKET,
                Constants.BASE_URL_ADMIN + Constants.URL_CREATE_TICKET, json, getActivity(), this);
        mCreateTicketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getEmails() {
        if (mGetEmailsTask != null) {
            return;
        }

        setContentShown(false);

        mGetEmailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_EMAILS,
                Constants.BASE_URL_MM + Constants.URL_GET_EMAIL, getActivity(), this);
        mGetEmailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (getActivity() != null)
            mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mCreateTicketTask = null;
            mGetEmailsTask = null;

            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.failed_request, Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                getActivity().onBackPressed();
            }

            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_CREATE_TICKET:
                try {
                    mCreateTicketResponse = gson.fromJson(result.getJsonString(), CreateTicketResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.ticket_created, Toast.LENGTH_LONG).show();
                            showCreateTicketSuccessDialog();
                        }
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_PAYMENT_REQUIRED) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.no_email_added, Toast.LENGTH_LONG).show();
                            launchEmailPage();
                        }
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), mCreateTicketResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mCreateTicketResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                mCreateTicketTask = null;
                break;
            case Constants.COMMAND_GET_EMAILS:
                try {
                    mGetEmailResponse = gson.fromJson(result.getJsonString(), GetEmailResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        processGetEmailListResponse(result.getJsonString());

                        DataHelper dataHelper = DataHelper.getInstance(getActivity());
                        dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE, result.getJsonString());

                        PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE, false);
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }
                mGetEmailsTask = null;
                break;
        }
    }
}
