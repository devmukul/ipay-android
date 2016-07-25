package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.FriendPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpReviewActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.OperatorClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopUpPackageClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MobileTopupFragment extends Fragment implements HttpResponseListener {

    private EditText mMobileNumberEditText;
    private EditText mAmountEditText;
    private EditText mPackageEditText;
    private EditText mOperatorEditText;
    private ImageView mSelectReceiverButton;
    private Button mRechargeButton;
    private TextView mMobileTopUpInfoTextView;

    private ProgressDialog mProgressDialog;
    private SharedPreferences pref;

    private TopUpPackageClass aPackage;
    private OperatorClass mOperator;

    private List<TopUpPackageClass> mpackageList;
    private List<OperatorClass> moperatorList;
    private List<String> mArraypackages;
    private List<String> mArrayoperators;

    private ResourceSelectorDialog<TopUpPackageClass> packageClassResourceSelectorDialog;
    private ListView pop_up_list;
    private final int PICK_CONTACT_REQUEST = 100;
    private static final int MOBILE_TOPUP_REVIEW_REQUEST = 101;
    private int mSelectedPackageTypeId = -1;
    private int mSelectedOperatorTypeId = 0;


    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mobile_topup, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mPackageEditText = (EditText) v.findViewById(R.id.package_type);
        mOperatorEditText = (EditText) v.findViewById(R.id.operator);
        mSelectReceiverButton = (ImageView) v.findViewById(R.id.select_receiver_from_contacts);
        mRechargeButton = (Button) v.findViewById(R.id.button_recharge);
        mMobileTopUpInfoTextView = (TextView) v.findViewById(R.id.text_view_mobile_restriction_info);

        getOperatorandPackage();

        //Set adapter for package type
        setPackageTypeAdapter();

        int mobileNumberType = pref.getInt(Constants.MOBILE_NUMBER_TYPE, Constants.MOBILE_TYPE_PREPAID);
        if (mobileNumberType == Constants.MOBILE_TYPE_PREPAID) {
            mPackageEditText.setText(mArraypackages.get(Constants.MOBILE_TYPE_PREPAID - 1));
        } else {
            mPackageEditText.setText(mArraypackages.get(Constants.MOBILE_TYPE_POSTPAID - 1));
        }

        mPackageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                packageClassResourceSelectorDialog.show();
            }
        });

        mOperatorEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showOperatorDialog();
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.recharging_balance));


        mRechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // For now, we are directly sending the money without going through any send money query
                    // sendMoneyQuery();
                    if (verifyUserInputs()) {
                        launchReviewPage();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        String verificationStatus = ProfileInfoCacheManager.getVerificationStatus();
        String userMobileNumber = ProfileInfoCacheManager.getMobileNumber();

        mMobileNumberEditText.setText(userMobileNumber);
        setOperator(userMobileNumber);

        if (!verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
            mMobileNumberEditText.setEnabled(false);
            mMobileNumberEditText.setFocusable(false);

            mOperatorEditText.setEnabled(false);
            mSelectReceiverButton.setVisibility(View.GONE);
            mAmountEditText.requestFocus();
            mMobileTopUpInfoTextView.setVisibility(View.VISIBLE);


        } else {
            mSelectReceiverButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FriendPickerDialogActivity.class);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                }
            });

            mMobileNumberEditText.requestFocus();

            mMobileNumberEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setOperator(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_TOP_UP);

        return v;
    }

    private void getOperatorandPackage() {
        mpackageList = new ArrayList<>();
        moperatorList = new ArrayList<>();
        mArraypackages = Arrays.asList(getResources().getStringArray(R.array.package_type));
        mArrayoperators = Arrays.asList(getResources().getStringArray(R.array.mobile_operators));

        for (String mpackage : mArraypackages) {
            aPackage = new TopUpPackageClass();
            aPackage.setName(mpackage);
            mpackageList.add(aPackage);
        }
        for (String moperator : mArrayoperators) {
            mOperator = new OperatorClass(moperator);
            moperatorList.add(mOperator);
        }
    }

    private void setOperator(String phoneNumber) {
        phoneNumber = ContactEngine.trimPrefix(phoneNumber);

        final String[] OPERATOR_PREFIXES = {"17", "18", "16", "19", "15"};
        for (int i = 0; i < OPERATOR_PREFIXES.length; i++) {
            if (phoneNumber.startsWith(OPERATOR_PREFIXES[i])) {
                mOperatorEditText.setText(mArrayoperators.get(i));
                mSelectedOperatorTypeId = i;
                break;
            }
        }
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        if (mAmountEditText.getText().toString().trim().length() == 0) {
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            focusView = mAmountEditText;
            cancel = true;
        } else if ((mAmountEditText.getText().toString().trim().length() > 0)
                && Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                && Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {

            String error_message = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmountEditText.getText().toString()),
                    TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                    TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

            if (error_message != null) {
                focusView = mAmountEditText;
                mAmountEditText.setError(error_message);
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
                if (mobileNumber != null)
                    mMobileNumberEditText.setText(mobileNumber);
                mMobileNumberEditText.setError(null);
            }
        } else if (requestCode == MOBILE_TOPUP_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    private void launchReviewPage() {

        double amount = Double.parseDouble(mAmountEditText.getText().toString().trim());
        String mobileNumber = mMobileNumberEditText.getText().toString();

        int mobileNumberType;
        if (mSelectedPackageTypeId > 0)
            mobileNumberType = Constants.MOBILE_TYPE_POSTPAID;
        else
            mobileNumberType = Constants.MOBILE_TYPE_PREPAID;
        pref.edit().putInt(Constants.MOBILE_NUMBER_TYPE, mobileNumberType).apply();

        int operatorCode = mSelectedOperatorTypeId + 1;
        String countryCode = "+88"; // TODO: For now Bangladesh Only

        Intent intent = new Intent(getActivity(), TopUpReviewActivity.class);
        intent.putExtra(Constants.MOBILE_NUMBER, mobileNumber);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.MOBILE_NUMBER_TYPE, mobileNumberType);
        intent.putExtra(Constants.OPERATOR_CODE, operatorCode);
        intent.putExtra(Constants.COUNTRY_CODE, countryCode);

        startActivityForResult(intent, MOBILE_TOPUP_REVIEW_REQUEST);
    }

    private void showOperatorDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_listview);
        pop_up_list = (ListView) dialog.findViewById(R.id.custom_list);
        OperatorAdapter adapter = new OperatorAdapter(getActivity(), moperatorList);
        pop_up_list.setAdapter(adapter);
        pop_up_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedOperatorTypeId = i;
                mOperatorEditText.setText(moperatorList.get(i).getName());
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true); //  TO NOT DISMISS THE DIALOG
        dialog.show();

    }

    private void attemptGetBusinessRule(int serviceID) {

        if (mGetBusinessRuleTask != null) {
            return;
        }

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    Gson gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    for (BusinessRule rule : businessRuleArray) {
                        if (rule.getRuleID().equals(Constants.SERVICE_RULE_TOP_UP_MAX_AMOUNT_PER_PAYMENT)) {
                            TopUpActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());

                        } else if (rule.getRuleID().equals(Constants.SERVICE_RULE_TOP_UP_MIN_AMOUNT_PER_PAYMENT)) {
                            TopUpActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
            }

            mGetBusinessRuleTask = null;
        }
    }

    private void setPackageTypeAdapter() {
        packageClassResourceSelectorDialog = new ResourceSelectorDialog(getActivity(),getString(R.string.select_a_package), mpackageList, mSelectedPackageTypeId);
        packageClassResourceSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mPackageEditText.setText(name);
                mSelectedPackageTypeId = mArraypackages.indexOf(name);
            }
        });
    }


    public class OperatorAdapter extends ArrayAdapter<OperatorClass> {

        private final LayoutInflater inflater;

        public OperatorAdapter(Context context, List<OperatorClass> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final OperatorClass operator = getItem(position);

            View view = convertView;
            if (view == null)
                view = inflater.inflate(R.layout.list_item_operator, null);


            ImageView operatorimageView = (ImageView) view.findViewById(R.id.operator_imageView);
            TextView operatorNameView = (TextView) view.findViewById(R.id.textview_operator_name);


            //Setting the correct image based on Operator
            int[] images = {
                    R.drawable.ic_gp,
                    R.drawable.ic_robi,
                    R.drawable.ic_airtel,
                    R.drawable.ic_banglalink,
                    R.drawable.ic_teletalk,

            };

            operatorimageView.setImageResource(images[position]);
            operatorNameView.setText(operator.getName());


            return view;
        }
    }
}
