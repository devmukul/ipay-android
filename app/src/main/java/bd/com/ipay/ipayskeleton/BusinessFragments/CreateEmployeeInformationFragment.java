package bd.com.ipay.ipayskeleton.BusinessFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.FriendPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedEditText;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class CreateEmployeeInformationFragment extends Fragment implements HttpResponseListener {

    private final int PICK_CONTACT_REQUEST = 100;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private IconifiedEditText mMobileNumberEditText;
    private ImageView mSelectMobileNumberFromContactsButton;
    private IconifiedEditText mDesignationEditText;

    private Button mContinueButton;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_information, container, false);

        mMobileNumberEditText = (IconifiedEditText) v.findViewById(R.id.mobile_number);
        mDesignationEditText = (IconifiedEditText) v.findViewById(R.id.designation);

        mSelectMobileNumberFromContactsButton = (ImageView) v.findViewById(R.id.select_mobile_number_from_contacts);
        mContinueButton = (Button) v.findViewById(R.id.button_continue);

        mSelectMobileNumberFromContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendPickerDialogActivity.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    String mobileNumber = ContactEngine.formatMobileNumberBD(
                            mMobileNumberEditText.getText().toString().trim());
                    getProfileInfo(mobileNumber);
                }
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());

        return v;
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        String mobileNumber = mMobileNumberEditText.getText().toString().trim();
        String designation = mDesignationEditText.getText().toString().trim();

        if (!ContactEngine.isValidNumber(mobileNumber)) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            cancel = true;
        }

        if (designation.isEmpty()) {
            focusView = mDesignationEditText;
            mDesignationEditText.setError(getString(R.string.please_enter_designation));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.preparing));
        mProgressDialog.show();

        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getActivity(), this);

        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchPermissionPage(String mobileNumber, String name, String profilePicture, String designation) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MOBILE_NUMBER, mobileNumber);
        bundle.putString(Constants.DESIGNATION, designation);
        bundle.putString(Constants.NAME, name);
        bundle.putString(Constants.PROFILE_PICTURE, profilePicture);

        ((BusinessActivity) getActivity()).switchToEmployeePrivilegeFragment(bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
            if (mobileNumber != null)
                mMobileNumberEditText.setText(mobileNumber);
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetProfileInfoTask = null;
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String name = mGetUserInfoResponse.getName();
                    String profilePicture = null;
                    if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                        profilePicture = mGetUserInfoResponse
                                .getProfilePictures().get(0).getUrl();
                    }

                    String mobileNumber = ContactEngine.formatMobileNumberBD(mMobileNumberEditText.getText().toString());
                    String designation = mDesignationEditText.getText().toString();
                    launchPermissionPage(mobileNumber, name, profilePicture, designation);

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    Toast.makeText(getActivity(), R.string.user_has_no_ipay_account, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getActivity(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
            }

            mGetProfileInfoTask = null;
        }
    }

}
