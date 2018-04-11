package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.ProfileVerificationHelperActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.AddBankRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.AddBankResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BankBranch;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OnBoardConsentAgreementForBankFragment extends BaseFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAddBankTask = null;
    private AddBankResponse mAddBankResponse;

    private ProgressDialog mProgressDialog;

    private TextView mAccountNameTextView;
    private TextView mBankNameTextView;
    private TextView mBranchNameTextView;
    private TextView mBankAccountNumberTextView;

    private Button mAgreeButton;
    private Button mDisagreeButton;

    private String mAccountName;
    private String mBankName;
    private String mBranchName;
    private String mBankAccountNumber;
    private BankBranch mBankBranch;

    private boolean startedFromProfileCompletion = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboard_add_bank_agreement, container, false);
        getActivity().setTitle(R.string.bank_consent_agreement);

        initializeViews(view);
        initializeBankInfo(getArguments());
        populateViews();
        setOnClickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_consent_agreement_for_bank) );
    }

    private void setOnClickListeners() {
        mAgreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddBank(mBankBranch.getRoutingNumber(), 0,
                        mAccountName, mBankAccountNumber);
            }
        });

        mDisagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void populateViews() {
        mAccountNameTextView.setText(mAccountName);
        mBankNameTextView.setText(mBankName);
        mBranchNameTextView.setText(mBranchName);
        mBankAccountNumberTextView.setText(mBankAccountNumber);
    }

    private void initializeBankInfo(Bundle bundle) {
        mBankName = bundle.getString(Constants.BANK_NAME);
        mBankBranch = bundle.getParcelable(Constants.BANK_BRANCH);
        mBranchName = mBankBranch.getName();
        mBankAccountNumber = bundle.getString(Constants.BANK_ACCOUNT_NUMBER);
        mAccountName = ProfileInfoCacheManager.getUserName();

        startedFromProfileCompletion = bundle.getBoolean(Constants.IS_STARTED_FROM_PROFILE_COMPLETION);
    }

    private void initializeViews(View view) {
        mProgressDialog = new ProgressDialog(getActivity());
        mAccountNameTextView = (TextView) view.findViewById(R.id.bank_account_name);
        mBankNameTextView = (TextView) view.findViewById(R.id.bank_name);
        mBranchNameTextView = (TextView) view.findViewById(R.id.bank_branch_name);
        mBankAccountNumberTextView = (TextView) view.findViewById(R.id.bank_account_number);
        mAgreeButton = (Button) view.findViewById(R.id.button_agree);
        mDisagreeButton = (Button) view.findViewById(R.id.button_disagree);
    }

    private void attemptAddBank(String branchRoutingNumber, int accountType, String accountName, String accountNumber) {

        mProgressDialog.setMessage(getString(R.string.adding_bank));
        mProgressDialog.show();
        AddBankRequest mAddBankRequest = new AddBankRequest(branchRoutingNumber, accountType, accountName, accountNumber);
        Gson gson = new Gson();
        String json = gson.toJson(mAddBankRequest);
        mAddBankTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_A_BANK,
                Constants.BASE_URL_MM + Constants.URL_ADD_A_BANK, json, getActivity());
        mAddBankTask.mHttpResponseListener = this;
        mAddBankTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result,getContext(),mProgressDialog)) {
            mProgressDialog.dismiss();
            mAddBankTask = null;
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ADD_A_BANK)) {

            try {
                mAddBankResponse = gson.fromJson(result.getJsonString(), AddBankResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.bank_successfully_placed_for_verification, Toast.LENGTH_LONG);

                    if(!ProfileInfoCacheManager.isIntroductionAsked() && !ProfileInfoCacheManager.isSwitchedFromSignup()){
                        ((ProfileVerificationHelperActivity) getActivity()).switchToAskedIntroductionHelperFragment();
                    }else {
                        ((ProfileVerificationHelperActivity) getActivity()).switchToHomeActivity();
                    }

                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mAddBankResponse.getMessage(), Toast.LENGTH_SHORT);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();
            mAddBankTask = null;
        }
    }
}