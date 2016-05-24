package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.AddNewEmailRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.DeleteEmailResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.Email;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.EmailVerificationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.EmailVerificationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.GetEmailResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.AddNewEmailResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.MakePrimaryEmailResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.MakePrimaryRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EmailFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetEmailsTask = null;
    private GetEmailResponse mGetEmailResponse;

    private HttpRequestPostAsyncTask mAddNewEmailTask = null;
    private AddNewEmailResponse mAddNewEmailResponse;

    private HttpRequestDeleteAsyncTask mDeleteEmailTask = null;
    private DeleteEmailResponse mDeleteEmailResponse;

    private HttpRequestPostAsyncTask mMakePrimaryEmailTask = null;
    private MakePrimaryEmailResponse makePrimaryEmailResponse;

    private HttpRequestPostAsyncTask mEmailVerificationTask = null;
    private EmailVerificationResponse mEmailVerificationResponse;

    private List<Email> mEmails;
    private EmailListAdapter mEmailListAdapter;

    private Button mAddNewEmailButton;
    private RecyclerView mEmailListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_email, container, false);

        getActivity().setTitle(R.string.email);

        mAddNewEmailButton = (Button) v.findViewById(R.id.button_add_email);
        mEmailListRecyclerView = (RecyclerView) v.findViewById(R.id.list_email);

        mProgressDialog = new ProgressDialog(getActivity());

        mEmailListAdapter = new EmailListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mEmailListRecyclerView.setLayoutManager(mLayoutManager);
        mEmailListRecyclerView.setAdapter(mEmailListAdapter);

        mAddNewEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewEmailDialog();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
        if (pushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE))
            getEmails();
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE);
            dataHelper.closeDbOpenHelper();

            if (json == null)
                getEmails();
            else {
                processGetEmailListResponse(json);
            }
        }

    }

    private void showAddNewEmailDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.add_an_email)
                .customView(R.layout.dialog_add_new_email, true)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .build();

        View view = dialog.getCustomView();

        final EditText emailView = (EditText) view.findViewById(R.id.edit_text_email);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String email = emailView.getText().toString().trim();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailView.getWindowToken(), 0);

                if (!Utilities.isValidEmail(email)) {
                    Toast.makeText(getActivity(), R.string.enter_valid_email, Toast.LENGTH_LONG).show();
                } else {
                    addNewEmail(email);
                }
            }
        });

        dialog.show();
    }

    private void showDeleteEmailConfirmationDialog(final Email email) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.are_you_sure)
                .setMessage(getString(R.string.confirmation_remove_email_address))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEmail(email.getEmailId());
                    }
                })
                .setNegativeButton(android.R.string.no, null);

        dialog.show();
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

    private void addNewEmail(String email) {
        if (mAddNewEmailTask != null) {
            return;
        }

        AddNewEmailRequest addNewEmailRequest = new AddNewEmailRequest(email);
        Gson gson = new Gson();
        String json = gson.toJson(addNewEmailRequest);

        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_email));
        mProgressDialog.show();

        mAddNewEmailTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_NEW_EMAIL,
                Constants.BASE_URL_MM + Constants.URL_POST_EMAIL, json, getActivity(), this);
        mAddNewEmailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void deleteEmail(long id) {
        if (mDeleteEmailTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_delete_email));
        mProgressDialog.show();

        mDeleteEmailTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_DELETE_EMAIL,
                Constants.BASE_URL_MM + Constants.URL_DELETE_EMAIL + id, getActivity(), this);
        mDeleteEmailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void verifyEmail(long id) {
        if (mEmailVerificationTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_sending_verification_mail));
        mProgressDialog.show();

        EmailVerificationRequest emailVerificationRequest = new EmailVerificationRequest();
        Gson gson = new Gson();
        String json = gson.toJson(emailVerificationRequest);

        mEmailVerificationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_EMAIL_VERIFICATION,
                Constants.BASE_URL_MM + Constants.URL_POST_EMAIL + id + Constants.URL_MAKE_EMAIL_VERIFIED,
                json, getActivity(), this);
        mEmailVerificationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void makeEmailPrimary(long id) {
        if (mMakePrimaryEmailTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_make_primary_email));
        mProgressDialog.show();

        MakePrimaryRequest makePrimaryRequest = new MakePrimaryRequest();
        Gson gson = new Gson();
        String json = gson.toJson(makePrimaryRequest);

        mMakePrimaryEmailTask = new HttpRequestPostAsyncTask(Constants.COMMAND_EMAIL_MAKE_PRIMARY,
                Constants.BASE_URL_MM + Constants.URL_POST_EMAIL + id + Constants.URL_MAKE_PRIMARY_EMAIL,
                json, getActivity(), this);
        mMakePrimaryEmailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        mProgressDialog.dismiss();

        if (result == null) {
            mGetEmailsTask = null;
            mAddNewEmailTask = null;
            mAddNewEmailTask = null;
            mEmailVerificationTask = null;
            mMakePrimaryEmailTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_EMAILS)) {
            try {
                mGetEmailResponse = gson.fromJson(result.getJsonString(), GetEmailResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    processGetEmailListResponse(result.getJsonString());
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mGetEmailResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ((HomeActivity) getActivity()).switchToDashBoard();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_loading_emails, Toast.LENGTH_LONG).show();
                    ((HomeActivity) getActivity()).switchToDashBoard();
                }
            }

            mGetEmailsTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_ADD_NEW_EMAIL)) {
            try {
                mAddNewEmailResponse = gson.fromJson(result.getJsonString(), AddNewEmailResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    getEmails();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mAddNewEmailResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        // Send the verification status
                        long emailID = mAddNewEmailResponse.getId();
                        verifyEmail(emailID);

                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mAddNewEmailResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_add_email, Toast.LENGTH_LONG).show();
                }
            }

            mAddNewEmailTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_DELETE_EMAIL)) {
            try {
                mDeleteEmailResponse = gson.fromJson(result.getJsonString(), DeleteEmailResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    getEmails();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mDeleteEmailResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mDeleteEmailResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_delete_email, Toast.LENGTH_LONG).show();
                }
            }

            mDeleteEmailTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_EMAIL_VERIFICATION)) {
            try {
                mEmailVerificationResponse = gson.fromJson(result.getJsonString(), EmailVerificationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    getEmails();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mEmailVerificationResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mEmailVerificationResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_sending_verification_request, Toast.LENGTH_LONG).show();
                }
            }

            mEmailVerificationTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_EMAIL_MAKE_PRIMARY)) {
            try {
                makePrimaryEmailResponse = gson.fromJson(result.getJsonString(), MakePrimaryEmailResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    getEmails();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), makePrimaryEmailResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), makePrimaryEmailResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_make_primary, Toast.LENGTH_LONG).show();
                }
            }

            mMakePrimaryEmailTask = null;
        }

    }

    private void processGetEmailListResponse(String json) {
        Gson gson = new Gson();
        mGetEmailResponse = gson.fromJson(json, GetEmailResponse.class);

        mEmails = mGetEmailResponse.getEmailAdressList();

        Collections.sort(mEmails, new Comparator<Email>() {
            @Override
            public int compare(Email lhs, Email rhs) {

                if ((lhs.isPrimary() && !rhs.isPrimary()) || (!lhs.isPrimary() && rhs.isPrimary())) {
                    if (lhs.isPrimary())
                        return -1;
                    else
                        return 1;
                } else {
                    return (int) (lhs.getEmailId() - rhs.getEmailId());
                }
            }
        });

        setContentShown(true);

        mEmailListAdapter.notifyDataSetChanged();

        PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
        pushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, false);

    }


    public class EmailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public EmailListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mEmailView;
            private TextView mIsPrimaryView;
            private ImageView mVerificationStatus;
            private LinearLayout optionsLayout;
            private Button removeButton;
            private Button verifyButton;
            private Button makePrimaryButton;
            private View divider;

            public ViewHolder(final View itemView) {
                super(itemView);

                mEmailView = (TextView) itemView.findViewById(R.id.textview_email);
                mIsPrimaryView = (TextView) itemView.findViewById(R.id.textview_is_primary);
                mVerificationStatus = (ImageView) itemView.findViewById(R.id.email_verification_status);

                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                divider = itemView.findViewById(R.id.divider);

                removeButton = (Button) itemView.findViewById(R.id.button_remove);
                verifyButton = (Button) itemView.findViewById(R.id.button_verify);
                makePrimaryButton = (Button) itemView.findViewById(R.id.button_make_primary);
            }

            public void bindView(int pos) {

                final Email email = mEmails.get(pos);

                final String verificationStatus = email.getVerificationStatus();

                if (verificationStatus.equals(Constants.EMAIL_VERIFICATION_STATUS_VERIFIED)) {
                    mVerificationStatus.setImageResource(R.drawable.ic_verified);
                    mVerificationStatus.setColorFilter(null);

                    makePrimaryButton.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.GONE);
                } else if (verificationStatus.equals(Constants.EMAIL_VERIFICATION_STATUS_VERIFICATION_IN_PROGRESS)) {
                    mVerificationStatus.setImageResource(R.drawable.ic_cached_black_24dp);
                    mVerificationStatus.setColorFilter(Color.GRAY);

                    makePrimaryButton.setVisibility(View.GONE);
                    verifyButton.setVisibility(View.GONE);
                    divider.setVisibility(View.GONE);
                } else {
                    mVerificationStatus.setImageResource(R.drawable.ic_error_black_24dp);
                    mVerificationStatus.setColorFilter(Color.RED);

                    makePrimaryButton.setVisibility(View.GONE);
                    verifyButton.setVisibility(View.VISIBLE);
                }

                if (email.isPrimary()) {
                    mIsPrimaryView.setVisibility(View.VISIBLE);

                    optionsLayout.setVisibility(View.GONE);
                }

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteEmailConfirmationDialog(email);
                    }
                });

                verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyEmail(email.getEmailId());
                    }
                });

                makePrimaryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeEmailPrimary(email.getEmailId());
                    }
                });

                mEmailView.setText(email.getEmailAddress());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!email.isPrimary()) {
                            if (optionsLayout.getVisibility() == View.VISIBLE) {
                                optionsLayout.setVisibility(View.GONE);
                            } else {
                                optionsLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_email,
                    parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                ViewHolder vh = (ViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mEmails != null)
                return mEmails.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
