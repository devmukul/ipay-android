package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

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

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceFetchApi.GetBusinessTypesAsyncTask;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Employee.GetBusinessInformationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.GetUserAddressResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.District;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.DistrictRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetOccupationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Occupation;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.OccupationRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Thana;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.ThanaRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefConstants;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToastandLogger.ToastWrapper;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class BusinessInformationFragment extends ProgressFragment implements HttpResponseListener {
    private HttpRequestGetAsyncTask mGetBusinessInformationAsyncTask;
    private GetBusinessInformationResponse mGetBusinessInformationResponse;

    private GetBusinessTypesAsyncTask mGetBusinessTypesAsyncTask;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private HttpRequestGetAsyncTask mGetOccupationTask = null;
    private GetOccupationResponse mGetOccupationResponse;

    private HttpRequestGetAsyncTask mGetUserAddressTask = null;
    private GetUserAddressResponse mGetUserAddressResponse = null;

    private HttpRequestGetAsyncTask mGetThanaListAsyncTask = null;
    private GetThanaResponse mGetThanaResponse;

    private HttpRequestGetAsyncTask mGetDistrictListAsyncTask = null;
    private GetDistrictResponse mGetDistrictResponse;

    private TextView mBusinessNameView;
    private TextView mBusinessMobileNumberView;
    private TextView mBusinessTypeView;

    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mOccupationView;
    private TextView mOrganizationNameView;
    private TextView mVerificationStatusView;
    private TextView mSignUpTimeView;

    private TextView mPresentAddressView;
    private View mPresentAddressHolder;
    private AddressClass mPresentAddress;

    private ImageButton mPresentAddressEditButton;
    private ImageButton mContactInfoEditButton;
    private ImageButton mOfficeInfoEditButton;

    private String mName = "";
    private String mMobileNumber = "";
    private String mProfileImageUrl = "";
    private String mDateOfBirth = "";

    private int mOccupation = 0;
    private String mOrganizationName = "";
    private String mGender;
    private String mSignUpTime = "";
    private String mVerificationStatus = null;

    private List<Thana> mThanaList;
    private List<District> mDistrictList;
    private List<BusinessType> mBusinessTypes;
    private List<Occupation> mOccupationList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_information, container, false);
        getActivity().setTitle(R.string.basic_info);

        mBusinessNameView = (TextView) view.findViewById(R.id.textview_business_name);
        mBusinessMobileNumberView = (TextView) view.findViewById(R.id.textview_business_mobile_number);
        mBusinessTypeView = (TextView) view.findViewById(R.id.textview_business_type);
        mNameView = (TextView) view.findViewById(R.id.textview_name);
        mOccupationView = (TextView) view.findViewById(R.id.textview_occupation);
        mOrganizationNameView = (TextView) view.findViewById(R.id.textview_organization_name);
        mMobileNumberView = (TextView) view.findViewById(R.id.textview_mobile_number);
        mVerificationStatusView = (TextView) view.findViewById(R.id.textview_verification_status);
        mSignUpTimeView = (TextView) view.findViewById(R.id.textview_signup);
        mPresentAddressView = (TextView) view.findViewById(R.id.textview_present_address);
        mPresentAddressHolder = view.findViewById(R.id.present_address_holder);

        mOfficeInfoEditButton = (ImageButton) view.findViewById(R.id.button_edit_office_information);
        mPresentAddressEditButton = (ImageButton) view.findViewById(R.id.button_edit_present_address);
        mContactInfoEditButton = (ImageButton) view.findViewById(R.id.button_edit_contact_information);

        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();

        if (ProfileInfoCacheManager.isAccountVerified()) {
            mOfficeInfoEditButton.setVisibility(View.GONE);
            mContactInfoEditButton.setVisibility(View.GONE);
        } else {
            mOfficeInfoEditButton.setVisibility(View.VISIBLE);
            mContactInfoEditButton.setVisibility(View.VISIBLE);
        }

        mOfficeInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditBusinessInformationFragment();
            }
        });

        mContactInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditContactInformationFragment();
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);

        getBusinessInformation();

        if (PushNotificationStatusHolder.isUpdateNeeded(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE)
                || PushNotificationStatusHolder.isUpdateNeeded(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE)) {
            getProfileInfo();
        } else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE);

            if (json == null)
                getProfileInfo();
            else {
                processProfileInfoResponse(json);
            }
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
        if (mPresentAddress != null) {
            presentAddressBundle.putSerializable(Constants.ADDRESS, mPresentAddress);
        }

        mPresentAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToEditAddressFragment(presentAddressBundle);
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


    private void launchEditContactInformationFragment() {
        Bundle bundle = new Bundle();

        bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
        bundle.putString(Constants.NAME, mName);
        bundle.putString(Constants.DATE_OF_BIRTH, mDateOfBirth);
        bundle.putString(Constants.PROFILE_PICTURE, mProfileImageUrl);
        bundle.putString(Constants.GENDER, mGender);
        bundle.putInt(Constants.OCCUPATION, mOccupation);
        bundle.putString(Constants.ORGANIZATION_NAME, mOrganizationName);
        bundle.putParcelableArrayList(Constants.OCCUPATION_LIST, new ArrayList<>(mOccupationList));
        ((ProfileActivity) getActivity()).switchToEditBasicInfoFragment(bundle);
    }

    private void setProfileInformation() {
        mMobileNumberView.setText(getString(R.string.phone_number) + ": " + mMobileNumber);
        mNameView.setText(getString(R.string.name) + ": " + mName);
        mSignUpTimeView.setText(getString(R.string.member_since) + ": " + mSignUpTime);

        if (mOrganizationName != null && !mOrganizationName.isEmpty())
            mOrganizationNameView.setText(getString(R.string.organization_name) + ": " + mOrganizationName);
        else
            mOrganizationNameView.setText(getString(R.string.organization_name) + ": " + getString(R.string.not_available));

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

    private void getOccupationList() {
        if (mGetOccupationTask != null) {
            return;
        }

        mGetOccupationTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_OCCUPATIONS_REQUEST,
                new OccupationRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetOccupationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchEditBusinessInformationFragment() {
        Bundle bundle = new Bundle();

        if (mGetBusinessInformationResponse == null || mBusinessTypes == null) {
            Toast.makeText(getActivity(), R.string.please_wait_until_information_loading, Toast.LENGTH_LONG).show();
            return;
        }

        bundle.putString(Constants.BUSINESS_NAME, mGetBusinessInformationResponse.getBusinessName());
        bundle.putString(Constants.BUSINESS_MOBILE_NUMBER, mGetBusinessInformationResponse.getMobileNumber());
        bundle.putInt(Constants.BUSINESS_TYPE, mGetBusinessInformationResponse.getBusinessType());
        bundle.putParcelableArrayList(Constants.BUSINESS_TYPE_LIST, new ArrayList<>(mBusinessTypes));

        ((ProfileActivity) getActivity()).switchToEditBusinessInformationFragment(bundle);
    }

    private void getBusinessInformation() {
        if (mGetBusinessInformationAsyncTask != null)
            return;

        mGetBusinessInformationAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_INFORMATION,
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_INFORMATION, getActivity(), this);
        mGetBusinessInformationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void processBusinessInformationResponse() {
        mBusinessNameView.setText(mGetBusinessInformationResponse.getBusinessName());
        mBusinessMobileNumberView.setText(getString(R.string.phone_number) + ": " + mGetBusinessInformationResponse.getMobileNumber());
        mBusinessTypeView.setText(R.string.loading);

        // Load business types, then extract the name of the business type from businessTypeId
        mGetBusinessTypesAsyncTask = new GetBusinessTypesAsyncTask(getActivity(), new GetBusinessTypesAsyncTask.BusinessTypeLoadListener() {
            @Override
            public void onLoadSuccess(List<BusinessType> businessTypes) {
                mBusinessTypes = businessTypes;

                for (BusinessType businessType : businessTypes) {
                    if (businessType.getId() == mGetBusinessInformationResponse.getBusinessType())
                        mBusinessTypeView.setText(businessType.getName());
                }
            }

            @Override
            public void onLoadFailed() {
                mBusinessTypeView.setText(R.string.failed_loading_business_type);
            }
        });
        mGetBusinessTypesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void processProfileInfoResponse(String json) {
        try {
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
            mOrganizationName = mGetProfileInfoResponse.getOrganizationName();
            mVerificationStatus = mGetProfileInfoResponse.getVerificationStatus();

            mProfileImageUrl = Utilities.getImage(mGetProfileInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);

            setProfileInformation();
            getOccupationList();
            getDistrictList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {

            mGetBusinessInformationAsyncTask = null;
            mGetProfileInfoTask = null;
            mGetOccupationTask = null;
            mGetUserAddressTask = null;
            mGetDistrictListAsyncTask = null;
            mGetThanaListAsyncTask = null;

            if (getActivity() != null) {
                ToastWrapper.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
                ((ProfileActivity) getActivity()).switchToProfileFragment();
            }

            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_BUSINESS_INFORMATION:
                try {
                    mGetBusinessInformationResponse = gson.fromJson(result.getJsonString(), GetBusinessInformationResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        processBusinessInformationResponse();
                    } else {
                        if (getActivity() != null) {
                            ToastWrapper.makeText(getActivity(), R.string.failed_loading_business_information, Toast.LENGTH_LONG);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (getActivity() != null) {
                        ToastWrapper.makeText(getActivity(), R.string.failed_loading_business_information, Toast.LENGTH_LONG);
                    }
                }

                mGetBusinessInformationAsyncTask = null;
                break;

            case Constants.COMMAND_GET_OCCUPATIONS_REQUEST:
                try {
                    mGetOccupationResponse = gson.fromJson(result.getJsonString(), GetOccupationResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mOccupationList = mGetOccupationResponse.getOccupations();
                        String occupation = mGetOccupationResponse.getOccupation(mOccupation);
                        if (occupation != null)
                            mOccupationView.setText(getString(R.string.occupation) + ": " + occupation);
                        else
                            mOccupationView.setText(getString(R.string.occupation) + ": " + getString(R.string.not_available));
                    } else {
                        mOccupationView.setText(getString(R.string.occupation) + ": " + getString(R.string.not_available));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mOccupationView.setText(getString(R.string.occupation) + ": " + getString(R.string.not_available));
                }

                mGetOccupationTask = null;
                break;

            case Constants.COMMAND_GET_PROFILE_INFO_REQUEST:
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        processProfileInfoResponse(result.getJsonString());

                        DataHelper dataHelper = DataHelper.getInstance(getActivity());
                        dataHelper.updatePushEvents(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE, result.getJsonString());

                        PushNotificationStatusHolder.setUpdateNeeded(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE, false);
                        PushNotificationStatusHolder.setUpdateNeeded(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, false);

                    } else {
                        if (getActivity() != null)
                            ToastWrapper.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        ToastWrapper.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT);
                }

                mGetProfileInfoTask = null;
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
                            ToastWrapper.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT);
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        ToastWrapper.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT);
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
                            ToastWrapper.makeText(getActivity(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG);
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        ToastWrapper.makeText(getActivity(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG);
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
                            ToastWrapper.makeText(getActivity(), R.string.failed_loading_district_list, Toast.LENGTH_LONG);
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        ToastWrapper.makeText(getActivity(), R.string.failed_loading_district_list, Toast.LENGTH_LONG);
                        getActivity().onBackPressed();
                    }
                }

                mGetDistrictListAsyncTask = null;
                break;
        }
    }
}
