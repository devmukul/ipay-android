package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class TopUpEnterNumberFragment extends Fragment implements HttpResponseListener, View.OnClickListener {


    private EditText mNumberEditText;
    private TextView mMyNumberTopUpTextView;
    private ImageView mContactImageView;
    private RadioGroup mTypeSelector;

    private final int PICK_CONTACT_REQUEST = 100;


    private String mMobileNumber;
    private String mName;
    private String mProfileImageUrl;
    private int mOperatorType;
    private String mOperator;

    private Button mContinueButton;


    private ImageView gpImageView;
    private ImageView airtelImageView;
    private ImageView robiImageView;
    private ImageView teletalkImageView;
    private ImageView banglalinkImageView;

    private int operatorCode;

    private HttpRequestGetAsyncTask mGetProfileInfoTask;

    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_up_enter_amount, container, false);
        mProgressDialog = new ProgressDialog(getContext());
        setUpView(view);
        mOperator = "";
        mOperatorType = -1;
        operatorCode = -1;
        return view;
    }

    private void setUpView(View view) {
        mNumberEditText = (EditText) view.findViewById(R.id.number_edit_text);
        mMyNumberTopUpTextView = (TextView) view.findViewById(R.id.my_number_topup_text_view);
        mContactImageView = (ImageView) view.findViewById(R.id.contact_image_view);
        mTypeSelector = (RadioGroup) view.findViewById(R.id.type_selector);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);
        gpImageView = (ImageView) view.findViewById(R.id.gp);
        airtelImageView = (ImageView) view.findViewById(R.id.airtel);
        robiImageView = (ImageView) view.findViewById(R.id.robi);
        teletalkImageView = (ImageView) view.findViewById(R.id.teletalk);
        banglalinkImageView = (ImageView) view.findViewById(R.id.banglalink);
        gpImageView.setOnClickListener(this);
        airtelImageView.setOnClickListener(this);
        banglalinkImageView.setOnClickListener(this);
        teletalkImageView.setOnClickListener(this);
        robiImageView.setOnClickListener(this);
        setUpButtonActions();
    }

    private void setUpButtonActions() {
        mMyNumberTopUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = ContactEngine.formatMobileNumberBD(ProfileInfoCacheManager.getMobileNumber());
                mMobileNumber = mobileNumber;
                mMobileNumber = mobileNumber.substring(0, 4) + "-" + mobileNumber.substring(4, mobileNumber.length());
                mNumberEditText.setText(mMobileNumber);
                mName = ProfileInfoCacheManager.getUserName();
                mProfileImageUrl = ProfileInfoCacheManager.getProfileImageUrl();
            }
        });
        mContactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });
        mTypeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.prepaid) {
                    mOperatorType = 1;
                } else if (i == R.id.post_paid) {
                    mOperatorType = 2;
                }
            }
        });
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    if (mName == null || mName.equals("")) {
                        getProfileInfo(ContactEngine.formatLocalMobileNumber(mMobileNumber));
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                        bundle.putString(Constants.NAME, mName);
                        bundle.putInt(Constants.OPERATOR_CODE, operatorCode);
                        bundle.putInt(Constants.OPERATOR_TYPE, mOperatorType);
                        bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, ServiceIdConstants.TOP_UP);
                        if (!mProfileImageUrl.toLowerCase().contains(Constants.BASE_URL_FTP_SERVER.toLowerCase())) {
                            bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mProfileImageUrl);
                        } else {
                            bundle.putString(Constants.PHOTO_URI, mProfileImageUrl);
                        }
                        ((IPayTransactionActionActivity) (getActivity())).switchToAmountInputFragment(bundle);
                    }
                }
            }
        });
        mNumberEditText.setSelection(mNumberEditText.getText().length());
        mNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mNumberEditText.getText().toString().equals("+880-1")) {
                        mNumberEditText.setSelection(6);
                    } else {
                        mNumberEditText.setSelection(mNumberEditText.getText().length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 6) {
                    mNumberEditText.setText("+880-1");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mNumberEditText.getText().toString().equals("+880-1")) {
                    mNumberEditText.setSelection(6);
                } else {
                    mNumberEditText.setSelection(mNumberEditText.getText().length());
                }

            }
        });
    }

    private void showErrorMessage(String errorMessage) {
        if (getActivity() != null && getView() != null) {
            Snackbar snackbar = Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
            ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.value50);
            snackbarView.setLayoutParams(layoutParams);
            TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ActivityCompat.getColor(getActivity(), android.R.color.white));
            snackbar.show();
        }
    }

    private boolean verifyUserInputs() {
        if (mNumberEditText.getText() == null) {
            showErrorMessage("Please enter a mobile number");
            return false;
        } else if (mNumberEditText.getText().toString() == null || mNumberEditText.getText().toString().equals("")) {
            showErrorMessage("Please enter a mobile number");
            return false;
        } else {
            mMobileNumber = mNumberEditText.getText().toString();
            mMobileNumber = mMobileNumber.replaceAll("[^0-9.]", "");
            if (!mMobileNumber.matches(InputValidator.MOBILE_NUMBER_REGEX)) {
                showErrorMessage("Please enter a valid mobile number");
                return false;
            } else if (mOperatorType != 1 && mOperatorType != 2) {
                showErrorMessage("Please select Prepaid/Postpaid");
                return false;
            } else if (operatorCode != 1 && operatorCode != 3 && operatorCode != 4 && operatorCode != 5 && operatorCode != 6) {
                showErrorMessage("Please select an operator");
                return false;
            }
        }

        return true;
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }
        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(ContactEngine.formatMobileNumberBD(mobileNumber));

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getContext(), this, false);
        mProgressDialog.setMessage(getString(R.string.fetching_user_info));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mMobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
                mName = data.getStringExtra(Constants.NAME);
                mProfileImageUrl = data.getStringExtra(Constants.PROFILE_PICTURE);
                if (mMobileNumber != null) {
                    mMobileNumber = mMobileNumber.substring(0, 4) + "-" + mMobileNumber.substring(4, mMobileNumber.length());
                    mNumberEditText.setText(mMobileNumber);
                }
            }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        try {
            if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
                mGetProfileInfoTask = null;
                mProgressDialog.dismiss();
                return;
            }
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                GetUserInfoResponse getUserInfoResponse =
                        new Gson().fromJson(result.getJsonString(), GetUserInfoResponse.class);
                mName = getUserInfoResponse.getName();
                mProfileImageUrl = getUserInfoResponse.getProfilePictures().get(0).getUrl();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.NAME, mName);
                bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                bundle.putInt(Constants.OPERATOR_CODE, operatorCode);
                bundle.putInt(Constants.OPERATOR_TYPE, mOperatorType);
                bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, ServiceIdConstants.TOP_UP);
                if (!mProfileImageUrl.toLowerCase().contains(Constants.BASE_URL_FTP_SERVER.toLowerCase())) {
                    bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mProfileImageUrl);
                } else {
                    bundle.putString(Constants.PHOTO_URI, mProfileImageUrl);
                }
                ((IPayTransactionActionActivity) (getActivity())).switchToAmountInputFragment(bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                ((IPayTransactionActionActivity) (getActivity())).switchToAmountInputFragment(bundle);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAppropriateOperatorIconSelected(int operatorCode) {
        switch (operatorCode) {
            case 1:
                gpImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightGray), android.graphics.PorterDuff.Mode.MULTIPLY);
                robiImageView.clearColorFilter();
                airtelImageView.clearColorFilter();
                banglalinkImageView.clearColorFilter();
                teletalkImageView.clearColorFilter();
                break;
            case 3:
                robiImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightGray), android.graphics.PorterDuff.Mode.MULTIPLY);
                airtelImageView.clearColorFilter();
                banglalinkImageView.clearColorFilter();
                teletalkImageView.clearColorFilter();
                gpImageView.clearColorFilter();
                break;
            case 4:
                airtelImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightGray), android.graphics.PorterDuff.Mode.MULTIPLY);
                banglalinkImageView.clearColorFilter();
                teletalkImageView.clearColorFilter();
                gpImageView.clearColorFilter();
                robiImageView.clearColorFilter();
                break;
            case 5:
                banglalinkImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightGray), android.graphics.PorterDuff.Mode.MULTIPLY);
                airtelImageView.clearColorFilter();
                robiImageView.clearColorFilter();
                teletalkImageView.clearColorFilter();
                gpImageView.clearColorFilter();
                break;
            case 6:
                teletalkImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightGray), android.graphics.PorterDuff.Mode.MULTIPLY);
                airtelImageView.clearColorFilter();
                banglalinkImageView.clearColorFilter();
                gpImageView.clearColorFilter();
                robiImageView.clearColorFilter();
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.airtel:
                operatorCode = 4;
                setAppropriateOperatorIconSelected(4);
                break;
            case R.id.teletalk:
                operatorCode = 6;
                setAppropriateOperatorIconSelected(6);
                break;
            case R.id.banglalink:
                operatorCode = 5;
                setAppropriateOperatorIconSelected(5);
                break;
            case R.id.gp:
                operatorCode = 1;
                setAppropriateOperatorIconSelected(1);
                break;
            case R.id.robi:
                operatorCode = 3;
                setAppropriateOperatorIconSelected(3);
                break;
        }
    }
}
