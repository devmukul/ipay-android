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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class TopUpEnterNumberFragment extends Fragment implements HttpResponseListener {


    private EditText mNumberEditText;
    private TextView mMyNumberTopUpTextView;
    private ImageView mContactImageView;
    private RadioGroup mTypeSelector;

    private final int PICK_CONTACT_REQUEST = 100;


    private String mMobileNumber;
    private String mName;
    private String mProfileImageUrl;
    private String mOperatorType;

    private HttpRequestGetAsyncTask mGetProfileInfoTask;

    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_up_enter_amount, container, false);
        mProgressDialog = new ProgressDialog(getContext());
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        mNumberEditText = (EditText) view.findViewById(R.id.number_edit_text);
        mMyNumberTopUpTextView = (TextView) view.findViewById(R.id.my_number_topup_text_view);
        mContactImageView = (ImageView) view.findViewById(R.id.contact_image_view);
        mTypeSelector = (RadioGroup) view.findViewById(R.id.type_selector);
        setUpButtonActions();
    }

    private void setUpButtonActions() {
        mMyNumberTopUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = ContactEngine.formatMobileNumberBD(ProfileInfoCacheManager.getMobileNumber());
                mMobileNumber = mobileNumber;
                mNumberEditText.setText(mMobileNumber);
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
                    mOperatorType = "PREPAID";
                } else if (i == R.id.post_paid) {
                    mOperatorType = "POSTPAID";
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

    private boolean verifyUserInputs(){
        if(mNumberEditText.getText() == null){
            showErrorMessage("Please enter a mobile number");
            return false;
        }
        else if(mNumberEditText.getText().toString()== null || mNumberEditText.getText().toString().equals("")){
            showErrorMessage("Please enter a mobile number");
            return false;
        }
        else if(mOperatorType.equals("")){
            showErrorMessage("Please select Prepaid/Postpaid");
            return false;
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
                    mNumberEditText.setText(mMobileNumber);
                }
            }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

    }
}
