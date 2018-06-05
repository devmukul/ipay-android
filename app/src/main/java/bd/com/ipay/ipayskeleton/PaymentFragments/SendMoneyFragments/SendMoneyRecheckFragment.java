package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Selection;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyRecheckFragment extends Fragment implements HttpResponseListener {
    private Button mContinueButton;
    private TextView mNameTextView;
    private ProfileImageView mProfileImageView;
    private TextView mIpayBalanceTextView;
    private EditText mAmountEditText;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private Bundle bundle;

    private String imageUrl;
    private String name;
    private String mMobileNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_money_recheck, container, false);
        attemptGetBusinessRule(ServiceIdConstants.SEND_MONEY);
        SendMoneyActivity.mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.SEND_MONEY);
        ((SendMoneyActivity) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarSendMoney));
        ((SendMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.VISIBLE);
        Drawable mBackButtonIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back);
        mBackButtonIcon.setColorFilter(new
                PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
        ((SendMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.GONE);
        ((SendMoneyActivity) getActivity()).hideTitle();
        setUpViews(view);
        return view;
    }

    private void setUpViews(View view) {
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mIpayBalanceTextView = (TextView) view.findViewById(R.id.ipay_balance_text_view);
        mProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_image_view);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);
        mAmountEditText = (EditText) view.findViewById(R.id.amount_edit_text);
        mAmountEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                
                return false;
            }
        });

        Selection.setSelection(mAmountEditText.getText(), mAmountEditText.getText().length());

        if (getArguments() != null) {
            bundle = getArguments();
            name = bundle.getString("name");
            mNameTextView.setText(name);
            imageUrl = bundle.getString("imageUrl");
            mMobileNumber = bundle.getString("number");
            mProfileImageView.setProfilePicture(bundle.getString("imageUrl"), false);
        }
        mIpayBalanceTextView.setText("Tk. " + SharedPrefManager.getUserBalance());
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (true) {
                    String amount = Utilities.formatTakaFromString(mAmountEditText.getText().toString());
                    amount = amount.replaceAll("[^\\d.]", "");
                    bundle.putString("name", mNameTextView.getText().toString());
                    bundle.putString("imageUrl", imageUrl);
                    bundle.putString("amount", amount);
                    bundle.putString("number", mMobileNumber);
                    ((SendMoneyActivity) getActivity()).switchToSendMoneyConfirmFragment(bundle);
                }
            }
        });
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

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        boolean cancel = false;
        View focusView = null;
        String errorMessage;

        if (!Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            return false;
        } else if (SendMoneyActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
            return false;
        }

        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

            if (TextUtils.isEmpty(mAmountEditText.getText())) {
                errorMessage = getString(R.string.please_enter_amount);

            } else if (!InputValidator.isValidDigit(mAmountEditText.getText().toString().trim())) {
                errorMessage = getString(R.string.please_enter_amount);
            } else {
                final BigDecimal sendMoneyAmount = new BigDecimal(mAmountEditText.getText().toString());
                if (sendMoneyAmount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                } else {
                    final BigDecimal minimumSendMoneyAmount = SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumSendMoneyAmount = SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);

                    errorMessage = InputValidator.isValidAmount(getActivity(), sendMoneyAmount, minimumSendMoneyAmount, maximumSendMoneyAmount);
                }
            }
        } else {
            errorMessage = getString(R.string.balance_not_available);
        }

        if (errorMessage != null) {
            focusView = mAmountEditText;
            mAmountEditText.setError(errorMessage);
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetBusinessRuleTask = null;
            return;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {

                    BusinessRule[] businessRuleArray = new Gson().fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {

                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_VERIFICATION_REQUIRED)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_PIN_REQUIRED)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                            }
                        }
                        BusinessRuleCacheManager.setBusinessRules(Constants.SEND_MONEY, SendMoneyActivity.mMandatoryBusinessRules);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
                }

            } else {
                if (getActivity() != null)
                    DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            }
        }
    }
}
