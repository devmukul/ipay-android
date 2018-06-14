package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Api.ContactApi.AddContactAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentRevertRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionDetailsFragment extends BaseFragment implements HttpResponseListener {

    public static MandatoryBusinessRules mMandatoryBusinessRules;
    private TransactionHistory transactionHistory;
    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private HttpRequestPostAsyncTask mPaymentRevertTask = null;
    private PaymentRevertRequest mPaymentRevertRequest;
    private CustomProgressDialog mCustomProgressDialog;


    private TextView descriptionTextView;
    private TextView timeTextView;
    private TextView amountTextView;
    private TextView feeTextView;
    private TextView transactionIDTextView;
    private TextView netAmountTextView;
    private TextView balanceTextView;
    private TextView purposeTextView;
    private TextView statusTextView;
    private TextView mobileNumberTextView;
    private ProfileImageView mProfileImageView;
    private ImageView otherImageView;
    private TextView mMobileNumberView;
    private TextView mNameView;
    private Button mAddInContactsButton;
    private Button mRevertTransactionButton;
    private String otherPartyNumber;
    private String otherPartyName;
    private String purpose;
    private int serviceId;
    private Integer statusCode;
    private String status;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_details, container, false);

        transactionHistory = getArguments().getParcelable(Constants.TRANSACTION_DETAILS);
        serviceId = transactionHistory.getServiceID();
        purpose = transactionHistory.getPurpose();

        otherPartyNumber = transactionHistory.getAdditionalInfo().getNumber();
        otherPartyName = transactionHistory.getAdditionalInfo().getName();

        statusCode = transactionHistory.getStatusCode();
        status = transactionHistory.getStatus();

        descriptionTextView = (TextView) v.findViewById(R.id.description);
        timeTextView = (TextView) v.findViewById(R.id.time);
        amountTextView = (TextView) v.findViewById(R.id.amount);
        feeTextView = (TextView) v.findViewById(R.id.fee);
        transactionIDTextView = (TextView) v.findViewById(R.id.transaction_id);
        netAmountTextView = (TextView) v.findViewById(R.id.netAmount);
        balanceTextView = (TextView) v.findViewById(R.id.balance);
        purposeTextView = (TextView) v.findViewById(R.id.purpose);
        statusTextView = (TextView) v.findViewById(R.id.status);
        mobileNumberTextView = (TextView) v.findViewById(R.id.your_number);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        otherImageView = (ImageView) v.findViewById(R.id.other_image);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mAddInContactsButton = (Button) v.findViewById(R.id.add_in_contacts);
        mRevertTransactionButton = (Button) v.findViewById(R.id.button_revert);
        mCustomProgressDialog = new CustomProgressDialog(getContext());

        if (transactionHistory.getDescription() != null)
            descriptionTextView.setText(transactionHistory.getDescription());
        timeTextView.setText(Utilities.formatDateWithTime(transactionHistory.getTime()));
        amountTextView.setText(Utilities.formatTaka(transactionHistory.getAmount()));
        feeTextView.setText(Utilities.formatTaka(transactionHistory.getFee()));
        transactionIDTextView.setText(transactionHistory.getTransactionID());
        netAmountTextView.setText(Utilities.formatTaka(transactionHistory.getNetAmount()));
        if (transactionHistory.getAccountBalance() != null)
            balanceTextView.setText(Utilities.formatTaka(transactionHistory.getAccountBalance()));
        mobileNumberTextView.setText(ProfileInfoCacheManager.getMobileNumber());


        if (serviceId == Constants.TRANSACTION_HISTORY_SEND_MONEY || serviceId == Constants.TRANSACTION_HISTORY_REQUEST_MONEY) {
            if (!new ContactSearchHelper(getActivity()).searchMobileNumber(transactionHistory.getAdditionalInfo().getNumber())) {
                mAddInContactsButton.setVisibility(View.VISIBLE);
            }
        }

        mAddInContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.ADD_CONTACTS)
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.are_you_sure)
                        .setMessage(getString(R.string.confirmation_add_to_contacts))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAddInContactsButton.setVisibility(View.GONE);

                                addContact(transactionHistory.getAdditionalInfo().getName(),
                                        transactionHistory.getAdditionalInfo().getNumber(), null);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);

                dialog.show();
            }
        });

        if (transactionHistory.getAdditionalInfo().getType().equalsIgnoreCase(Constants.TRANSACTION_TYPE_USER)) {
            String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();
            otherImageView.setVisibility(View.GONE);
            mProfileImageView.setVisibility(View.VISIBLE);
            mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
        } else {
            int iconId = transactionHistory.getAdditionalInfo().getImageWithType(getContext());
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(iconId);
        }

        mNameView.setText(otherPartyName);
        mMobileNumberView.setText(otherPartyNumber);
        purposeTextView.setText(purpose);
        statusTextView.setText(status);

        if (statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {
            statusTextView.setTextColor(getResources().getColor(R.color.bottle_green));
        } else if (statusCode == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
            statusTextView.setTextColor(getResources().getColor(R.color.colorAmber));
        } else {
            statusTextView.setTextColor(getResources().getColor(R.color.background_red));
        }

        if (ProfileInfoCacheManager.isBusinessAccount()
                && (serviceId == Constants.TRANSACTION_HISTORY_MAKE_PAYMENT
                || serviceId == Constants.TRANSACTION_HISTORY_REQUEST_PAYMENT)
                && statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {

            mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.PAYMENT_REVERT);
            mRevertTransactionButton.setVisibility(View.VISIBLE);
            attemptGetBusinessRule(Constants.SERVICE_ID_TRANSACTION_REVERT);

        }

        mRevertTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // For now, we are directly sending the money without going through any send money query
                    // sendMoneyQuery();
                    if (verifyUserInputs()) {
                        attemptPaymentRevertWithPinCheck();
                    }
                } else if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG);
            }
        });


        return v;
    }


    private boolean verifyUserInputs() {

        boolean cancel = false;

        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());


            final BigDecimal amount = new BigDecimal(transactionHistory.getAmount());
            if (amount.compareTo(balance) > 0) {
                String errorMessage = getString(R.string.insufficient_balance);
                Toaster.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG);
            }
        }

        if (cancel) {

            return false;
        } else {
            return true;
        }
    }

    private void attemptGetBusinessRule(int serviceID) {
        if (mGetBusinessRuleTask != null) {
            return;
        }
        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this, true);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptPaymentRevertWithPinCheck() {

        if (mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptPaymentRevert(pin);
                }
            });
        } else {
            attemptPaymentRevert(null);
        }
    }

    private void attemptPaymentRevert(String pin) {
        if (mPaymentRevertTask != null) {
            return;
        }

        mCustomProgressDialog.setLoadingMessage(getString(R.string.progress_dialog_text_sending_money));
        mCustomProgressDialog.showDialog();

        mPaymentRevertRequest = new PaymentRevertRequest(null, transactionHistory.getTransactionID(), pin);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentRevertRequest);
        mPaymentRevertTask = new HttpRequestPostAsyncTask(Constants.COMMAND_PAYMENT_REVERT,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_REVERT, json, getActivity(), false);
        mPaymentRevertTask.mHttpResponseListener = this;
        mPaymentRevertTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_transaction_details));
    }

    @ValidateAccess
    private void addContact(String name, String phoneNumber, String relationship) {
        AddContactRequestBuilder addContactRequestBuilder = new
                AddContactRequestBuilder(name, phoneNumber, relationship);

        new AddContactAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                addContactRequestBuilder.generateUri(), addContactRequestBuilder.getAddContactRequest(),
                getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        Gson gson = new Gson();
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {

            mGetBusinessRuleTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {

                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_PAYMENT_PIN_REQUIRED)) {
                                mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                            }
                        }
                        BusinessRuleCacheManager.setBusinessRules(Constants.PAYMENT_REVERT, mMandatoryBusinessRules);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            mGetBusinessRuleTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_PAYMENT_REVERT)) {

            try {
                SendMoneyResponse mSendMoneyResponse = gson.fromJson(result.getJsonString(), SendMoneyResponse.class);
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:

                        mCustomProgressDialog.showSuccessAnimationAndMessage(mSendMoneyResponse.getMessage());

                        break;
                    case Constants.HTTP_RESPONSE_STATUS_ACCEPTED:
                    case Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED:
                        mCustomProgressDialog.dismissDialog();
                        Toast.makeText(getActivity(), mSendMoneyResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        break;
                    case Constants.HTTP_RESPONSE_STATUS_BLOCKED:
                        if (getActivity() != null) {
                            mCustomProgressDialog.showFailureAnimationAndMessage(mSendMoneyResponse.getMessage());
                            ((MyApplication) getActivity().getApplication()).launchLoginPage("");

                        }
                        break;
                    default:
                        if (getActivity() != null) {


                            Toast.makeText(getActivity(), mSendMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();

                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
                mCustomProgressDialog.showFailureAnimationAndMessage(getResources().getString(R.string.service_not_available));
            }
            mPaymentRevertTask = null;
        }
    }
}
