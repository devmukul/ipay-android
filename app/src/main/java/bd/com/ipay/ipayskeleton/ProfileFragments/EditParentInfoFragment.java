package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.SetParentInfoRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.SetParentInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EditParentInfoFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSetParentInfoTask = null;
    private SetParentInfoResponse mSetParentInfoResponse;

    private EditText mFathersNameEditText;
    private EditText mMothersNameEditText;

    private EditText mFathersMobileEditText;
    private EditText mMothersMobileEditText;

    private ProgressDialog mProgressDialog;

    private String mFathersName = "";
    private String mMothersName = "";

    private String mFathersMobile = "";
    private String mMothersMobile = "";

    private Button mInfoSaveButton;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_parent_info, container, false);

        getActivity().setTitle(getString(R.string.edit_parent_info));
        Bundle bundle = getArguments();

        mFathersName = bundle.getString(Constants.FATHERS_NAME);
        mMothersName = bundle.getString(Constants.MOTHERS_NAME);
        mFathersMobile = bundle.getString(Constants.FATHERS_MOBILE);
        mMothersMobile = bundle.getString(Constants.MOTHERS_MOBILE);

        mFathersNameEditText = (EditText) v.findViewById(R.id.fathers_name);
        mMothersNameEditText = (EditText) v.findViewById(R.id.mothers_name);
        mFathersMobileEditText = (EditText) v.findViewById(R.id.fathers_mobile);
        mMothersMobileEditText = (EditText) v.findViewById(R.id.mothers_mobile);

        mInfoSaveButton = (Button) v.findViewById(R.id.button_save);

        mProgressDialog = new ProgressDialog(getActivity());


        setParentInformation();


        mInfoSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    Utilities.hideKeyboard(getActivity());
                    attemptSaveParentInfo();
                }
            }
        });

        return v;
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        mFathersName = mFathersNameEditText.getText().toString().trim();
        mMothersName = mMothersNameEditText.getText().toString().trim();

        mFathersMobile = mFathersMobileEditText.getText().toString().trim();
        mMothersMobile = mMothersMobileEditText.getText().toString().trim();

        if (mFathersName.isEmpty()) {
            mFathersNameEditText.setError(getString(R.string.error_invalid_first_name));
            focusView = mFathersNameEditText;
            cancel = true;
        } else if (mFathersName.length() < 5 ) {
            mFathersNameEditText.setError(getString(R.string.error_invalid_parent_name));
            focusView = mFathersNameEditText;
            cancel = true;
        }

        if (mMothersName.isEmpty()) {
            mMothersNameEditText.setError(getString(R.string.error_invalid_first_name));
            focusView = mMothersNameEditText;
            cancel = true;
        } else if (mMothersName.length() < 5 ) {
            mMothersNameEditText.setError(getString(R.string.error_invalid_parent_name));
            focusView = mMothersNameEditText;
            cancel = true;
        }

        if (mFathersMobile.isEmpty()) mFathersMobile = null;
        else if ( !ContactEngine.isValidNumber(mFathersMobile)) {
            mFathersMobileEditText.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mFathersMobileEditText;
            cancel = true;
        }

        if (mMothersMobile.isEmpty()) mMothersMobile = null;
        else if (!ContactEngine.isValidNumber(mMothersMobile)) {
            mMothersMobileEditText.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mMothersMobileEditText;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void attemptSaveParentInfo() {
        mProgressDialog.setMessage(getString(R.string.saving_parent_information));
        mProgressDialog.show();

        Gson gson = new Gson();

        SetParentInfoRequest setParentInfoRequest = new SetParentInfoRequest(mFathersMobile, mMothersMobile, mFathersName, mMothersName);

        String parentInfoJson = gson.toJson(setParentInfoRequest);
        mSetParentInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PARENT_INFO_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_SET_PARENT_INFO_REQUEST, parentInfoJson, getActivity(), this);
        mSetParentInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setParentInformation() {

        mFathersNameEditText.setText(mFathersName);
        mMothersNameEditText.setText(mMothersName);
        mFathersMobileEditText.setText(mFathersMobile);
        mMothersMobileEditText.setText(mMothersMobile);

    }

    public void httpResponseReceiver(HttpResponseObject result) {
        mProgressDialog.dismiss();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mSetParentInfoTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_SET_PARENT_INFO_REQUEST:

                try {
                    mSetParentInfoResponse = gson.fromJson(result.getJsonString(), SetParentInfoResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), mSetParentInfoResponse.getMessage(), Toast.LENGTH_LONG).show();

                            getActivity().onBackPressed();
                        }
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.parent_info_save_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.parent_info_save_failed, Toast.LENGTH_SHORT).show();
                }

                mSetParentInfoTask = null;
                break;

        }
    }
}
