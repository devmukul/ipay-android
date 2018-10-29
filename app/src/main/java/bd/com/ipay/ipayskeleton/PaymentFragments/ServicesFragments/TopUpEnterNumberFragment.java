package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import bd.com.ipay.ipayskeleton.Widgets.IPaySnackbar;

public class TopUpEnterNumberFragment extends Fragment implements HttpResponseListener, View.OnClickListener {


    private EditText mNumberEditText;
    private TextView mMyNumberTopUpTextView;
    private ImageView mContactImageView;
    private RadioGroup mTypeSelector;

    private final int PICK_CONTACT_REQUEST = 100;


    private String mMobileNumber;
    private String mName;
    private String mProfileImageUrl;

    private Button mContinueButton;


    private LinearLayout gpLayout;
    private LinearLayout airtelLayout;
    private LinearLayout robiLayout;
    private LinearLayout teletalkLayout;
    private LinearLayout banglalinkLayout;

    private String operatorCode;

    private HttpRequestGetAsyncTask mGetProfileInfoTask;

    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_up_enter_number, container, false);
        mProgressDialog = new ProgressDialog(getContext());
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        mNumberEditText =  view.findViewById(R.id.number_edit_text);
        mMyNumberTopUpTextView =  view.findViewById(R.id.my_number_topup_text_view);
        mContactImageView =  view.findViewById(R.id.contact_image_view);
        mTypeSelector =  view.findViewById(R.id.type_selector);
        mContinueButton =  view.findViewById(R.id.continue_button);
        gpLayout =  view.findViewById(R.id.gp);
        airtelLayout =  view.findViewById(R.id.airtel);
        robiLayout =  view.findViewById(R.id.robi);
        teletalkLayout =  view.findViewById(R.id.teletalk);
        banglalinkLayout =  view.findViewById(R.id.banglalink);
        gpLayout.setOnClickListener(this);
        airtelLayout.setOnClickListener(this);
        banglalinkLayout.setOnClickListener(this);
        teletalkLayout.setOnClickListener(this);
        robiLayout.setOnClickListener(this);
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
                        bundle.putString(Constants.OPERATOR_CODE, operatorCode);
                        bundle.putInt(Constants.OPERATOR_TYPE, getOperatorType());
                        bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, ServiceIdConstants.TOP_UP);
                        if (mProfileImageUrl != null) {
                            if (!mProfileImageUrl.toLowerCase().contains(Constants.BASE_URL_FTP_SERVER.toLowerCase())) {
                                bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mProfileImageUrl);
                            } else {
                                bundle.putString(Constants.PHOTO_URI, mProfileImageUrl);
                            }
                        }
                        if (getActivity() instanceof  IPayTransactionActionActivity) {
	                        ((IPayTransactionActionActivity) (getActivity())).switchToAmountInputFragment(bundle);
                        }
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
                        Selection.setSelection(mNumberEditText.getText(), mNumberEditText.getText().length());
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
                if (s.toString().length() < 15) {
                    mName = "";
                    mMobileNumber = "";
                    mProfileImageUrl = "";
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
        if (getActivity() != null && mContinueButton != null) {
            IPaySnackbar.error(mContinueButton,errorMessage,IPaySnackbar.LENGTH_SHORT).show();
        }
    }

    private boolean verifyUserInputs() {
        if (mNumberEditText.getText() == null) {
            showErrorMessage("Please enter a mobile number");
            return false;
        } else if (TextUtils.isEmpty(mNumberEditText.getText())) {
            showErrorMessage("Please enter a mobile number");
            return false;
        } else {
            mMobileNumber = mNumberEditText.getText().toString();
            mMobileNumber = mMobileNumber.replaceAll("[^0-9.]", "");
            if (!mMobileNumber.matches(InputValidator.MOBILE_NUMBER_REGEX)) {
                showErrorMessage("Please enter a valid mobile number");
                return false;
            } else if (mTypeSelector.getCheckedRadioButtonId()==-1) {
                showErrorMessage("Please select Prepaid/Postpaid");
                return false;
            } else if (operatorCode == null || TextUtils.isEmpty(operatorCode)) {
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
                mUri, getContext(), this, true);
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
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                bundle.putString(Constants.OPERATOR_CODE, operatorCode);
                bundle.putInt(Constants.OPERATOR_TYPE, getOperatorType());
                ((IPayTransactionActionActivity) (getActivity())).switchToAmountInputFragment(bundle);
                return;
            }
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                GetUserInfoResponse getUserInfoResponse =
                        new Gson().fromJson(result.getJsonString(), GetUserInfoResponse.class);
                mName = getUserInfoResponse.getName();
                if (getUserInfoResponse.getProfilePictures() != null) {
                    if (getUserInfoResponse.getProfilePictures().size() > 0) {
                        mProfileImageUrl = getUserInfoResponse.getProfilePictures().get(0).getUrl();
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putString(Constants.NAME, mName);
                bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                bundle.putString(Constants.OPERATOR_CODE, operatorCode);
                bundle.putInt(Constants.OPERATOR_TYPE, getOperatorType());
                bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, ServiceIdConstants.TOP_UP);
                if (mProfileImageUrl != null) {
                    if (!mProfileImageUrl.toLowerCase().contains(Constants.BASE_URL_FTP_SERVER.toLowerCase())) {
                        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + mProfileImageUrl);
                    } else {
                        bundle.putString(Constants.PHOTO_URI, mProfileImageUrl);
                    }
                }
                ((IPayTransactionActionActivity) (getActivity())).switchToAmountInputFragment(bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                bundle.putString(Constants.OPERATOR_CODE, operatorCode);
                bundle.putInt(Constants.OPERATOR_TYPE, getOperatorType());
                ((IPayTransactionActionActivity) (getActivity())).switchToAmountInputFragment(bundle);

            }
            mGetProfileInfoTask = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getOperatorType() {
        switch (mTypeSelector.getCheckedRadioButtonId()){
            case R.id.prepaid:
                return  1;
            case R.id.post_paid:
                return 2;
            default:
                return 1;
        }
    }

    private void setAppropriateOperatorIconSelected(String operatorCode) {
        switch (operatorCode) {
            case "GP":
                ((ImageView) gpLayout.findViewById(R.id.gp_image)).setImageResource(R.drawable.selected_network);
                ((ImageView) airtelLayout.findViewById(R.id.airtel_image)).setImageResource(R.drawable.airtel_icon);
                ((ImageView) robiLayout.findViewById(R.id.robi_image)).setImageResource(R.drawable.robi_icon);
                ((ImageView) teletalkLayout.findViewById(R.id.teletalk_image)).setImageResource(R.drawable.teletalk_icon);
                ((ImageView) banglalinkLayout.findViewById(R.id.banglalink_image)).setImageResource(R.drawable.bl_icon);
                break;
            case "Robi":
                ((ImageView) robiLayout.findViewById(R.id.robi_image)).setImageResource(R.drawable.selected_network);
                ((ImageView) airtelLayout.findViewById(R.id.airtel_image)).setImageResource(R.drawable.airtel_icon);
                ((ImageView) gpLayout.findViewById(R.id.gp_image)).setImageResource(R.drawable.gp_icon);
                ((ImageView) teletalkLayout.findViewById(R.id.teletalk_image)).setImageResource(R.drawable.teletalk_icon);
                ((ImageView) banglalinkLayout.findViewById(R.id.banglalink_image)).setImageResource(R.drawable.bl_icon);
                break;
            case "Airtel":
                ((ImageView) airtelLayout.findViewById(R.id.airtel_image)).setImageResource(R.drawable.selected_network);
                ((ImageView) robiLayout.findViewById(R.id.robi_image)).setImageResource(R.drawable.robi_icon);
                ((ImageView) gpLayout.findViewById(R.id.gp_image)).setImageResource(R.drawable.gp_icon);
                ((ImageView) teletalkLayout.findViewById(R.id.teletalk_image)).setImageResource(R.drawable.teletalk_icon);
                ((ImageView) banglalinkLayout.findViewById(R.id.banglalink_image)).setImageResource(R.drawable.bl_icon);
                break;
            case "Banglalink":
                ((ImageView) banglalinkLayout.findViewById(R.id.banglalink_image)).setImageResource(R.drawable.selected_network);
                ((ImageView) airtelLayout.findViewById(R.id.airtel_image)).setImageResource(R.drawable.airtel_icon);
                ((ImageView) gpLayout.findViewById(R.id.gp_image)).setImageResource(R.drawable.gp_icon);
                ((ImageView) teletalkLayout.findViewById(R.id.teletalk_image)).setImageResource(R.drawable.teletalk_icon);
                ((ImageView) robiLayout.findViewById(R.id.robi_image)).setImageResource(R.drawable.robi_icon);
                break;
            case "Teletalk":
                ((ImageView) teletalkLayout.findViewById(R.id.teletalk_image)).setImageResource(R.drawable.selected_network);
                ((ImageView) airtelLayout.findViewById(R.id.airtel_image)).setImageResource(R.drawable.airtel_icon);
                ((ImageView) gpLayout.findViewById(R.id.gp_image)).setImageResource(R.drawable.gp_icon);
                ((ImageView) robiLayout.findViewById(R.id.robi_image)).setImageResource(R.drawable.robi_icon);
                ((ImageView) banglalinkLayout.findViewById(R.id.banglalink_image)).setImageResource(R.drawable.bl_icon);
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.airtel:
                operatorCode = Constants.OPERATOR_CODE_AIRTEL;
                setAppropriateOperatorIconSelected(Constants.OPERATOR_CODE_AIRTEL);
                break;
            case R.id.teletalk:
                operatorCode = Constants.OPERATOR_CODE_TELETALK;
                setAppropriateOperatorIconSelected(Constants.OPERATOR_CODE_TELETALK);
                break;
            case R.id.banglalink:
                operatorCode = Constants.OPERATOR_CODE_BANGLALINK;
                setAppropriateOperatorIconSelected(Constants.OPERATOR_CODE_BANGLALINK);
                break;
            case R.id.gp:
                operatorCode = Constants.OPERATOR_CODE_GP;
                setAppropriateOperatorIconSelected(Constants.OPERATOR_CODE_GP);
                break;
            case R.id.robi:
                operatorCode = Constants.OPERATOR_CODE_ROBI;
                setAppropriateOperatorIconSelected(Constants.OPERATOR_CODE_ROBI);
                break;
        }
    }
}
