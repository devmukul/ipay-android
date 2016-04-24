package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.AddNewEmailRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.DeleteEmailResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.Email;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.EmailVerificationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.GetEmailResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.AddNewEmailResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email.MakePrimaryEmailResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class EmailFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetEmailsTask = null;
    private GetEmailResponse mGetEmailResponse;

    private HttpRequestPostAsyncTask mAddNewEmailTask = null;
    private AddNewEmailResponse mNewEmailResponse;

    private HttpRequestDeleteAsyncTask mDeleteEmailTask = null;
    private DeleteEmailResponse mDeleteEmailResponse;

    private HttpRequestPostAsyncTask mMakePrimaryEmailTask = null;
    private MakePrimaryEmailResponse makePrimaryEmailResponse;

    private HttpRequestPostAsyncTask mEmailVerificationTask = null;
    private EmailVerificationResponse mEmailVerificationResponse;

    private List<Email> mEmails;
    private EmailListAdapter mEmailListAdapter;

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

        mEmailListRecyclerView = (RecyclerView) v.findViewById(R.id.list_email);

        mProgressDialog = new ProgressDialog(getActivity());

        mEmailListAdapter = new EmailListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mEmailListRecyclerView.setLayoutManager(mLayoutManager);
        mEmailListRecyclerView.setAdapter(mEmailListAdapter);

        loadEmails();

        return v;
    }

    private void loadEmails() {
        if (mGetEmailsTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_loading_emails));
        mProgressDialog.show();

        mGetEmailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_EMAILS,
                Constants.BASE_URL + Constants.URL_GET_EMAIL, getActivity(), this);
        mGetEmailsTask.execute();
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
                Constants.BASE_URL + Constants.URL_POST_EMAIL, json, getActivity(), this);
        mAddNewEmailTask.execute();
    }

    private void deleteEmail(long id) {
        if (mDeleteEmailTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_delete_email));
        mProgressDialog.show();

        mDeleteEmailTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_DELETE_EMAIL,
                Constants.BASE_URL + Constants.URL_DELETE_EMAIL + id, getActivity(), this);
        mDeleteEmailTask.execute();
    }



    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();

            mGetEmailsTask = null;
            mAddNewEmailTask = null;
            mAddNewEmailTask = null;
            mEmailVerificationTask = null;
            mMakePrimaryEmailTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_EMAILS)) {
            try {
                mGetEmailResponse = gson.fromJson(resultList.get(2), GetEmailResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mEmails = mGetEmailResponse.getEmailAdressList();

                    mEmailListAdapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mGetEmailResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ((HomeActivity) getActivity()).switchToDashBoard();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Failed to load emails", Toast.LENGTH_LONG).show();
                    ((HomeActivity) getActivity()).switchToDashBoard();
                }
            }
        }

        mProgressDialog.dismiss();
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
            private View verifyDivider;

            public ViewHolder(final View itemView) {
                super(itemView);

                mEmailView = (TextView) itemView.findViewById(R.id.textview_email);
                mIsPrimaryView = (TextView) itemView.findViewById(R.id.textview_is_primary);
                mVerificationStatus = (ImageView) itemView.findViewById(R.id.email_verification_status);

                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                removeButton = (Button) itemView.findViewById(R.id.remove_button);
                verifyButton = (Button) itemView.findViewById(R.id.verify_button);
                makePrimaryButton = (Button) itemView.findViewById(R.id.button_make_primary);
            }

            public void bindView(int pos) {

                final Email email = mEmails.get(pos);

                final String verificationStatus = email.getVerificationStatus();

                if (verificationStatus.equals(Constants.EMAIL_VERIFICATION_STATUS_VERIFIED)) {
                    makePrimaryButton.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.GONE);
                } else if (verificationStatus.equals(Constants.BANK_ACCOUNT_STATUS_NOT_VERIFIED)) {
                    makePrimaryButton.setVisibility(View.GONE);
                    verifyButton.setVisibility(View.VISIBLE);
                } else {
                    makePrimaryButton.setVisibility(View.GONE);
                    verifyButton.setVisibility(View.GONE);
                }

                if (email.isPrimary()) {
                    mIsPrimaryView.setVisibility(View.VISIBLE);
                }

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

//                        if (verificationStatus.equals(Constants.BANK_ACCOUNT_STATUS_PENDING))
//                            new MaterialShowcaseView.Builder(getActivity())
//                                    .setTarget(verifyButton)
//                                    .setDismissText(R.string.got_it)
//                                    .setContentText(Html.fromHtml(getString(R.string.bank_verification_help_html)))
//                                    .setDelay(100) // optional but starting animations immediately in onCreate can make them choppy
//                                    .singleUse(email.getEmailId() + "") // provide a unique ID used to ensure it is only shown once // TODO: removed for now. Comment out later
//                                    .show();
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
