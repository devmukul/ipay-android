package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ProfileCompletionHelperActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.AddressInputView;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.SetUserAddressRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.SetUserAddressResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.SetProfileInfoRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.SetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetOccupationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Occupation;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.OccupationRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OnBoardAddBasicInfoFragment extends BaseFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetOccupationTask = null;
    private GetOccupationResponse mGetOccupationResponse;
    private HttpRequestPostAsyncTask mSetProfileInfoTask = null;

    private HttpRequestPostAsyncTask mSetUserAddressTask = null;

    private ResourceSelectorDialog<Occupation> mOccupationTypeResourceSelectorDialog;
    private AddressClass mPresentAddress;
    private AddressInputView mAddressInputView;
    private ProgressDialog mProgressDialog;

    private EditText mOccupationEditText;
    private EditText mOrganizationNameEditText;

    private String mName = null;
    private String mDateOfBirth =null;
    private String mGender = null;
    private String mOrganizationName=null;
    private int mOccupation = -1;
    private List<Occupation> mOccupationList;

    private Button mSkipButton;
    ImageView back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_onboard_add_basic_info, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mAddressInputView = (AddressInputView) v.findViewById(R.id.input_address);
        Button mSaveButton = (Button) v.findViewById(R.id.button_upload_profile_pic);

        mSkipButton = (Button) v.findViewById(R.id.button_skip);

        mOccupationEditText = (EditText) v.findViewById(R.id.occupationEditText);
        mOrganizationNameEditText = (EditText) v.findViewById(R.id.organizationNameEditText);
        back  = (ImageView) v.findViewById(R.id.back);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAddressInputView.clearFocus();
        //mOrganizationNameEditText.requestFocus();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyUserInputs() && mAddressInputView.verifyUserInputs()) {
                    mPresentAddress = mAddressInputView.getInformation();
                    attemptSaveBasicInfo();
                }
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();
            }
        });

        if (getActivity().getSupportFragmentManager().getBackStackEntryCount()<=1){
            back.setVisibility(View.INVISIBLE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        getOccupationList();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void setUserAddress() {
        mProgressDialog.setMessage(getString(R.string.saving_profile_information));
        mProgressDialog.show();

        SetUserAddressRequest userAddressRequest = new SetUserAddressRequest(Constants.ADDRESS_TYPE_PRESENT, mPresentAddress);

        Gson gson = new Gson();
        String addressJson = gson.toJson(userAddressRequest, SetUserAddressRequest.class);
        mSetUserAddressTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_USER_ADDRESS_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_SET_USER_ADDRESS_REQUEST, addressJson, getActivity(), this);
        mSetUserAddressTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptSaveBasicInfo() {
        Gson gson = new Gson();

        SetProfileInfoRequest setProfileInfoRequest = new SetProfileInfoRequest(ProfileInfoCacheManager.getUserName(), mGender, ProfileInfoCacheManager.getBirthday(),
                mOccupation, mOrganizationName);

        String profileInfoJson = gson.toJson(setProfileInfoRequest);

        System.out.println("Test Result "+profileInfoJson);

        mSetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_SET_PROFILE_INFO_REQUEST, profileInfoJson, getActivity(), this);
        mSetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mSetUserAddressTask = null;
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        System.out.println("Test Result "+result.toString());

        if (result.getApiCommand().equals(Constants.COMMAND_SET_USER_ADDRESS_REQUEST)) {

            try {
                SetUserAddressResponse mSetUserAddressResponse = gson.fromJson(result.getJsonString(), SetUserAddressResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Toast.makeText(getActivity(), mSetUserAddressResponse.getMessage(), Toast.LENGTH_LONG).show();
                    //getActivity().onBackPressed();

                    ProfileInfoCacheManager.addBasicInfo(true);
                    ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetUserAddressResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
            }

            mSetUserAddressTask = null;
        }else if (result.getApiCommand().equals(Constants.COMMAND_GET_OCCUPATIONS_REQUEST)) {

            try {
                mGetOccupationResponse = gson.fromJson(result.getJsonString(), GetOccupationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mOccupationList = mGetOccupationResponse.getOccupations();

                    setOccupationAdapter();
                    setOccupation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mGetOccupationTask = null;
        }else if(result.getApiCommand().equals(Constants.COMMAND_SET_PROFILE_INFO_REQUEST)){
            try {
                SetProfileInfoResponse mSetProfileInfoResponse = gson.fromJson(result.getJsonString(), SetProfileInfoResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
//                        Toast.makeText(getActivity(), mSetProfileInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                        setUserAddress();

//                        if(!ProfileInfoCacheManager.isBankInfoAdded() && !ProfileCompletionHelperActivity.isFromSignUp){
//                            ((ProfileCompletionHelperActivity) getActivity()).switchToAddNewBankHelperFragment();
//                        }else if(!ProfileInfoCacheManager.isIntroductionAsked() && !ProfileCompletionHelperActivity.isFromSignUp){
//                            ((ProfileCompletionHelperActivity) getActivity()).switchToAskedIntroductionHelperFragment();
//                        }else {
//                            ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();
//                        }

                    }
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
            }

            mSetProfileInfoTask = null;
            mProgressDialog.dismiss();
        }
    }


    private void setOccupation() {

        for (int i = 0; i < mOccupationList.size(); i++) {
            if (mOccupationList.get(i).getId() == mOccupation) {
                String occupation = mOccupationList.get(i).getName();
                if (occupation != null) {
                    mOccupationEditText.setText(occupation);
                }

                break;
            }
        }
    }

    private void setOccupationAdapter() {
        mOccupationTypeResourceSelectorDialog = new ResourceSelectorDialog<>(getActivity(), getString(R.string.select_an_occupation), mOccupationList);
        mOccupationTypeResourceSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mOccupationEditText.setText(name);
                mOccupation = id;
            }
        });

        mOccupationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOccupationTypeResourceSelectorDialog.show();
            }
        });
    }


    private void getOccupationList() {
        if (mGetOccupationTask != null) {
            return;
        }
        mGetOccupationTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_OCCUPATIONS_REQUEST,
                new OccupationRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetOccupationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;
        mOrganizationName = mOrganizationNameEditText.getText().toString().trim();

        if (mOrganizationName.isEmpty())
            mOrganizationName = null;

        if (mOccupation < 0) {
            mOccupationEditText.setError(getString(R.string.please_enter_occupation));
            return false;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

}