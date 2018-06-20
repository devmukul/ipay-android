package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyConfirmActivity;
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
    private TextView mAmountTextView;
    private EditText mDummy;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private Bundle bundle;

    private String imageUrl;
    private String name;
    private String mMobileNumber;

    private String before;
    private String after;

    int l = 0;

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
        ((SendMoneyActivity) getActivity()).backButton.setImageDrawable(null);
        ((SendMoneyActivity) getActivity()).backButton.setImageDrawable(mBackButtonIcon);
        ((SendMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.GONE);
        ((SendMoneyActivity) getActivity()).hideTitle();
        setUpViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SendMoneyActivity.mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.SEND_MONEY);
        ((SendMoneyActivity) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarSendMoney));
        ((SendMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.VISIBLE);
        Drawable mBackButtonIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back);
        mBackButtonIcon.setColorFilter(new
                PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
        ((SendMoneyActivity) getActivity()).backButton.setImageDrawable(null);
        ((SendMoneyActivity) getActivity()).backButton.setImageDrawable(mBackButtonIcon);
        ((SendMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.GONE);
        ((SendMoneyActivity) getActivity()).hideTitle();
    }

    private void setUpViews(View view) {
        before = "00.00";
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mIpayBalanceTextView = (TextView) view.findViewById(R.id.ipay_balance_text_view);
        mProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_image_view);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);
        mAmountTextView = (TextView) view.findViewById(R.id.amount_edit_text);
        mDummy = (EditText) view.findViewById(R.id.amount_dummy_edit_text);
        String setString = "";
        mDummy.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() != keyEvent.ACTION_DOWN) {
                    if (keyEvent.getKeyCode() == keyEvent.KEYCODE_BACK) {
                        getActivity().onBackPressed();
                    }
                    return true;
                } else {
                    if (i == keyEvent.KEYCODE_DEL) {
                        double set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set / 10.00;
                        if (set < 10) {
                            BigDecimal bigDecimal = new BigDecimal(set);
                            bigDecimal = bigDecimal.setScale(2, RoundingMode.DOWN);
                            mAmountTextView.setText("0" + String.valueOf(bigDecimal));
                        } else {
                            BigDecimal bigDecimal = new BigDecimal(set);
                            bigDecimal = bigDecimal.setScale(2, RoundingMode.DOWN);

                            mAmountTextView.setText(String.valueOf(bigDecimal));
                        }
                    }
                    return false;
                }
            }
        });
        mDummy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                before = charSequence.toString();
                l = i;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double set = Double.parseDouble(mAmountTextView.getText().toString());
                if (editable.toString().length() < before.length()) {

                } else {
                    char inserted = editable.toString().charAt(l);
                    if (inserted == '1') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 1.0 / 100.00;
                    }
                    if (inserted == '2') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 2.0 / 100.00;
                    }
                    if (inserted == '3') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 3.0 / 100.00;
                    }
                    if (inserted == '4') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 4.0 / 100.00;
                    }
                    if (inserted == '5') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 5.0 / 100.00;
                    }
                    if (inserted == '0') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 0.0 / 100.00;
                    }
                    if (inserted == '6') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 6.0 / 100.00;
                    }
                    if (inserted == '7') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 7.0 / 100.00;
                    }
                    if (inserted == '8') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 8.0 / 100.00;
                    }
                    if (inserted == '9') {
                        set = Double.parseDouble((mAmountTextView.getText().toString()));
                        set = set * 10.00 + 9.0 / 100.00;
                    }
                    if (set < 10) {
                        String setString = "0" + String.format("%.2f", set);

                        mAmountTextView.setText(setString);

                    } else {
                        String setString = String.format("%.2f", set);
                        mAmountTextView.setText(setString);
                    }
                }

            }
        });

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
                if (verifyUserInputs()) {
                    String amount = Utilities.formatTakaFromString(mAmountTextView.getText().toString());
                    amount = amount.replaceAll("[^\\d.]", "");
                    Intent intent = new Intent(getActivity(), SendMoneyConfirmActivity.class);
                    intent.putExtra("name", mNameTextView.getText().toString());
                    intent.putExtra("imageUrl", imageUrl);
                    intent.putExtra("amount", amount);
                    intent.putExtra("number", mMobileNumber);
                    startActivity(intent);
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
        mDummy.setError(null);
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

            if (TextUtils.isEmpty(mAmountTextView.getText())) {
                errorMessage = getString(R.string.please_enter_amount);

            } else if (!InputValidator.isValidDigit(mAmountTextView.getText().toString().trim())) {
                errorMessage = getString(R.string.please_enter_amount);
            } else {
                final BigDecimal sendMoneyAmount = new BigDecimal(mAmountTextView.getText().toString());
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
            focusView = mAmountTextView;
            mDummy.setError(errorMessage);
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
