package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.GetUserAddressResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetParentInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.District;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.DistrictRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetOccupationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.OccupationRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Thana;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.ThanaRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class BusinessContactFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private HttpRequestGetAsyncTask mGetParentInfoTask = null;
    private GetParentInfoResponse mGetParentInfoResponse;

    private HttpRequestGetAsyncTask mGetOccupationTask = null;
    private GetOccupationResponse mGetOccupationResponse;

    private ProgressDialog mProgressDialog;

    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mVerificationStatusView;

    private TextView mFathersNameView;
    private TextView mFathersMobileView;
    private TextView mMothersNameView;
    private TextView mMothersMobileView;

    private TextView mDateOfBirthView;
    private TextView mOccupationView;
    private TextView mGenderView;
    private TextView mSignUpTimeView;

    private SharedPreferences pref;

    private String mName = "";
    private String mMobileNumber = "";
    private String mProfileImageUrl = "";

    private String mDateOfBirth = "";

    private String mFathersName = "";
    private String mMothersName = "";
    private String mFathersMobile = "";
    private String mMothersMobile = "";

    private int mOccupation = 0;
    private String mGender = "";
    private String mSignUpTime = "";
    private String mVerificationStatus = null;

    private HttpRequestGetAsyncTask mGetUserAddressTask = null;
    private GetUserAddressResponse mGetUserAddressResponse = null;

    private HttpRequestGetAsyncTask mGetThanaListAsyncTask = null;
    private GetThanaResponse mGetThanaResponse;

    private HttpRequestGetAsyncTask mGetDistrictListAsyncTask = null;
    private GetDistrictResponse mGetDistrictResponse;

    private AddressClass mPresentAddress;

    private TextView mPresentAddressView;

    private View mPresentAddressHolder;

    private ImageButton mPresentAddressEditButton;
    private ImageButton mContactEditButton;
    private ImageButton mParentInfoEditButton;

    private List<Thana> mThanaList;
    private List<District> mDistrictList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_business_contact, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        getActivity().setTitle(R.string.business_contact);

        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mVerificationStatusView = (TextView) v.findViewById(R.id.textview_verification_status);
        mFathersNameView = (TextView) v.findViewById(R.id.textview_fathers_name);
        mFathersMobileView = (TextView) v.findViewById(R.id.textview_fathers_mobile);
        mMothersNameView = (TextView) v.findViewById(R.id.textview_mothers_name);
        mMothersMobileView = (TextView) v.findViewById(R.id.textview_mothers_mobile);
        mDateOfBirthView = (TextView) v.findViewById(R.id.textview_dob);
        mOccupationView = (TextView) v.findViewById(R.id.textview_occupation);
        mGenderView = (TextView) v.findViewById(R.id.textview_gender);
        mSignUpTimeView = (TextView) v.findViewById(R.id.textview_signup);
        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        mGender = pref.getString(Constants.GENDER, "");
        mDateOfBirth = pref.getString(Constants.BIRTHDAY, "");
        mProgressDialog = new ProgressDialog(getActivity());

        mPresentAddressView = (TextView) v.findViewById(R.id.textview_present_address);

        mPresentAddressEditButton = (ImageButton) v.findViewById(R.id.button_edit_present_address);
        mContactEditButton = (ImageButton) v.findViewById(R.id.button_edit_contact_information);
        mParentInfoEditButton = (ImageButton) v.findViewById(R.id.button_edit_parent_information);

        mPresentAddressHolder = v.findViewById(R.id.present_address_holder);

        if (ProfileInfoCacheManager.isAccountVerified()) {
            mContactEditButton.setVisibility(View.GONE);
        } else {
            mContactEditButton.setVisibility(View.VISIBLE);
        }

        getDistrictList();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);
        if (PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE)
                || PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE)) {
            getProfileInfo();
        } else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE);

            if (json == null)
                getProfileInfo();
            else {
                processProfileInfoResponse(json);
            }

            getParentInfo();
        }

    }

    private void loadAddresses() {

        if (mPresentAddress == null) {
            mPresentAddressView.setVisibility(View.GONE);
        } else {
            mPresentAddressHolder.setVisibility(View.VISIBLE);
            mPresentAddressView.setText(mPresentAddress.toString(mThanaList, mDistrictList));
        }

        final Bundle presentAddressBundle = new Bundle();
        presentAddressBundle.putString(Constants.ADDRESS_TYPE, Constants.ADDRESS_TYPE_PRESENT);
        presentAddressBundle.putString(Constants.EDIT_ADDRESS_SOURCE, "BUSINESS_PRESENT");
        if (mPresentAddress != null) {
            presentAddressBundle.putSerializable(Constants.ADDRESS, mPresentAddress);
        }

        mPresentAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToEditAddressFragment(presentAddressBundle);
            }
        });

        mContactEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditFragment();
            }
        });

        mParentInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditParentInfoFragment();
            }
        });
    }

    private void getThanaList() {
        mGetThanaListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_THANA_LIST,
                new ThanaRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetThanaListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getDistrictList() {
        mGetDistrictListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DISTRICT_LIST,
                new DistrictRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetDistrictListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getUserAddress() {
        if (mGetUserAddressTask != null) {
            return;
        }

        mGetUserAddressTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_ADDRESS_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_USER_ADDRESS_REQUEST, getActivity(), this);
        mGetUserAddressTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void launchEditFragment() {
        Bundle bundle = new Bundle();

        bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
        bundle.putString(Constants.NAME, mName);
        bundle.putString(Constants.FATHERS_NAME, mFathersName);
        bundle.putString(Constants.MOTHERS_NAME, mMothersName);
        bundle.putString(Constants.DATE_OF_BIRTH, mDateOfBirth);
        bundle.putString(Constants.PROFILE_PICTURE, mProfileImageUrl);
        bundle.putString(Constants.GENDER, mGender);
        bundle.putInt(Constants.OCCUPATION, mOccupation);

        ((ProfileActivity) getActivity()).switchToEditBasicInfoFragment(bundle);
    }

    private void launchEditParentInfoFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.FATHERS_NAME, mFathersName);
        bundle.putString(Constants.MOTHERS_NAME, mMothersName);
        bundle.putString(Constants.FATHERS_MOBILE, mFathersMobile);
        bundle.putString(Constants.MOTHERS_MOBILE, mMothersMobile);

        ((ProfileActivity) getActivity()).switchToEditParentInfoFragment(bundle);
    }

    private void setParentInfo() {
        mFathersNameView.setText(mFathersName);
        mMothersNameView.setText(mMothersName);
        mFathersMobileView.setText(mFathersMobile);
        mMothersMobileView.setText(mMothersMobile);
    }

    private void setProfileInformation() {

        mMobileNumberView.setText(mMobileNumber);
        mNameView.setText(mName);

        mGenderView.setText(mGender);
        mDateOfBirthView.setText(mDateOfBirth);

        mSignUpTimeView.setText(mSignUpTime);

        if (GenderList.genderCodeToNameMap.containsKey(mGender))
            mGenderView.setText(GenderList.genderCodeToNameMap.get(mGender));

        if (mVerificationStatus != null) {
            if (mVerificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                mVerificationStatusView.setBackgroundResource(R.drawable.background_verified);
                mVerificationStatusView.setText(R.string.verified);
            } else {
                mVerificationStatusView.setBackgroundResource(R.drawable.background_not_verified);
                mVerificationStatusView.setText(R.string.unverified);
            }
        }
    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_PROFILE_INFO_REQUEST, getActivity(), this);
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getParentInfo() {
        if (mGetParentInfoTask != null) {
            return;
        }
        mGetParentInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PARENT_INFO_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_PARENT_INFO_REQUEST, getActivity(), this);
        mGetParentInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getOccupationList() {
        if (mGetOccupationTask != null) {
            return;
        }

        mOccupationView.setText(getString(R.string.please_wait));

        mGetOccupationTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_OCCUPATIONS_REQUEST,
                new OccupationRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetOccupationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetProfileInfoTask = null;
            mGetParentInfoTask = null;
            mGetOccupationTask = null;
            mGetUserAddressTask = null;
            mGetDistrictListAsyncTask = null;
            mGetThanaListAsyncTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_OCCUPATIONS_REQUEST:

                try {
                    mGetOccupationResponse = gson.fromJson(result.getJsonString(), GetOccupationResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        String occupation = mGetOccupationResponse.getOccupation(mOccupation);
                        if (occupation != null)
                            mOccupationView.setText(occupation);
                        else
                            mOccupationView.setText("");
                    } else {
                        mOccupationView.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mOccupationView.setText("");
                }

                mGetOccupationTask = null;
                break;

            case Constants.COMMAND_GET_PROFILE_INFO_REQUEST:

                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        processProfileInfoResponse(result.getJsonString());

                        DataHelper dataHelper = DataHelper.getInstance(getActivity());
                        dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE, result.getJsonString());

                        PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE, false);
                        PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, false);

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                }

                mGetProfileInfoTask = null;
                break;
            case Constants.COMMAND_GET_PARENT_INFO_REQUEST:

                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mGetParentInfoResponse = gson.fromJson(result.getJsonString(), GetParentInfoResponse.class);

                        if (mGetParentInfoResponse.getFatherName() != null)
                            mFathersName = mGetParentInfoResponse.getFatherName();
                        if (mGetParentInfoResponse.getFatherMobileNumber() != null)
                            mFathersMobile = mGetParentInfoResponse.getFatherMobileNumber();

                        if (mGetParentInfoResponse.getMotherName() != null)
                            mMothersName = mGetParentInfoResponse.getMotherName();
                        if (mGetParentInfoResponse.getMotherMobileNumber() != null)
                            mMothersMobile = mGetParentInfoResponse.getMotherMobileNumber();

                        setParentInfo();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                }

                mGetParentInfoTask = null;
                break;

            case Constants.COMMAND_GET_USER_ADDRESS_REQUEST:
                try {
                    mGetUserAddressResponse = gson.fromJson(result.getJsonString(), GetUserAddressResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mPresentAddress = mGetUserAddressResponse.getPresentAddress();

                        loadAddresses();
                        setContentShown(true);
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetUserAddressTask = null;

                break;
            case Constants.COMMAND_GET_THANA_LIST:
                try {
                    mGetThanaResponse = gson.fromJson(result.getJsonString(), GetThanaResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mThanaList = mGetThanaResponse.getThanas();
                        getUserAddress();

                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetThanaListAsyncTask = null;
                break;
            case Constants.COMMAND_GET_DISTRICT_LIST:
                try {
                    mGetDistrictResponse = gson.fromJson(result.getJsonString(), GetDistrictResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mDistrictList = mGetDistrictResponse.getDistricts();
                        getThanaList();

                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_loading_district_list, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_district_list, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetDistrictListAsyncTask = null;
                break;
        }
    }

    private void processProfileInfoResponse(String json) {
        Gson gson = new Gson();
        mGetProfileInfoResponse = gson.fromJson(json, GetProfileInfoResponse.class);

        if (mGetProfileInfoResponse.getName() != null)
            mName = mGetProfileInfoResponse.getName();
        if (mGetProfileInfoResponse.getMobileNumber() != null)
            mMobileNumber = mGetProfileInfoResponse.getMobileNumber();

        if (mGetProfileInfoResponse.getDateOfBirth() != null)
            mDateOfBirth = mGetProfileInfoResponse.getDateOfBirth();

        if (mGetProfileInfoResponse.getGender() != null)
            mGender = mGetProfileInfoResponse.getGender();

        if (mGetProfileInfoResponse.getSignUpTime() != null) {
            mSignUpTime = mGetProfileInfoResponse.getSignUpTime();
        }

        mOccupation = mGetProfileInfoResponse.getOccupation();
        mVerificationStatus = mGetProfileInfoResponse.getVerificationStatus();

        mProfileImageUrl = Utilities.getImage(mGetProfileInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);

        ProfileInfoCacheManager.updateCache(mName, mMobileNumber, mProfileImageUrl, mVerificationStatus);

        setProfileInformation();
        getOccupationList();
    }
}
