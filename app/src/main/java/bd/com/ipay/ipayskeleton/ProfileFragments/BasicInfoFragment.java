package bd.com.ipay.ipayskeleton.ProfileFragments;

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

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetParentInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetOccupationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Occupation;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.OccupationRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class BasicInfoFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private HttpRequestGetAsyncTask mGetParentInfoTask = null;
    private GetParentInfoResponse mGetParentInfoResponse;

    private HttpRequestGetAsyncTask mGetOccupationTask = null;
    private GetOccupationResponse mGetOccupationResponse;

    private List<Occupation> mOccupationList;

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
    private String occupation = "";

    private int mOccupation = 0;
    private String mGender;
    private String mSignUpTime = "";
    private String mVerificationStatus = null;

    private ImageButton mContactEditButton;
    private ImageButton mParentInfoEditButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_basic_info, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        getActivity().setTitle(R.string.basic_info);


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
        mDateOfBirth = pref.getString(Constants.BIRTHDAY, "");
        mProgressDialog = new ProgressDialog(getActivity());
        mContactEditButton = (ImageButton) v.findViewById(R.id.button_edit_contact_information);
        mParentInfoEditButton = (ImageButton) v.findViewById(R.id.button_edit_parent_information);

        if (ProfileInfoCacheManager.isAccountVerified())
            mContactEditButton.setVisibility(View.GONE);
        else mContactEditButton.setVisibility(View.VISIBLE);

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
        }

        getParentInfo();
    }

    private void launchEditFragment() {
        Bundle bundle = new Bundle();

        bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
        bundle.putString(Constants.NAME, mName);
        bundle.putString(Constants.DATE_OF_BIRTH, mDateOfBirth);
        bundle.putString(Constants.PROFILE_PICTURE, mProfileImageUrl);
        bundle.putString(Constants.GENDER, mGender);
        bundle.putInt(Constants.OCCUPATION, mOccupation);
        bundle.putParcelableArrayList(Constants.OCCUPATION_LIST, new ArrayList<>(mOccupationList));
        ((ProfileActivity) getActivity()).switchToEditBasicInfoFragment(bundle);
    }


    private void launchEditParentInfoFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.FATHERS_NAME, mFathersName);
        bundle.putString(Constants.MOTHERS_NAME, mMothersName);
        bundle.putString(Constants.FATHERS_MOBILE, ContactEngine.formatMobileNumberBD(mFathersMobile));
        bundle.putString(Constants.MOTHERS_MOBILE, ContactEngine.formatMobileNumberBD(mMothersMobile));

        ((ProfileActivity) getActivity()).switchToEditParentInfoFragment(bundle);
    }

    private void setProfileInformation() {

        mMobileNumberView.setText(mMobileNumber);
        mNameView.setText(mName);

        if(mGender!= null)
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

        setContentShown(false);

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
            mGetOccupationTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_PROFILE_INFO_REQUEST)) {

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
        }
        if (result.getApiCommand().equals(Constants.COMMAND_GET_PARENT_INFO_REQUEST)) {

            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mGetParentInfoResponse = gson.fromJson(result.getJsonString(), GetParentInfoResponse.class);

                    if (mGetParentInfoResponse.getFatherName() != null) {
                        mFathersName = mGetParentInfoResponse.getFatherName();
                        mFathersNameView.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mFathersNameView.setText(mFathersName);
                    }
                    if (mGetParentInfoResponse.getFatherMobileNumber() != null) {
                        mFathersMobile = mGetParentInfoResponse.getFatherMobileNumber();
                        mFathersMobileView.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mFathersMobileView.setText(mFathersMobile);
                    }

                    if (mGetParentInfoResponse.getMotherName() != null) {
                        mMothersName = mGetParentInfoResponse.getMotherName();
                        mMothersNameView.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mMothersNameView.setText(mMothersName);
                    }
                    if (mGetParentInfoResponse.getMotherMobileNumber() != null) {
                        mMothersMobile = mGetParentInfoResponse.getMotherMobileNumber();
                        mMothersMobileView.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mMothersMobileView.setText(mMothersMobile);
                    }

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
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_OCCUPATIONS_REQUEST)) {

            try {
                mGetOccupationResponse = gson.fromJson(result.getJsonString(), GetOccupationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mOccupationList = mGetOccupationResponse.getOccupations();
                    if (mGetOccupationResponse.getOccupation(mOccupation) != null) {
                        occupation = mGetOccupationResponse.getOccupation(mOccupation);
                        mOccupationView.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mOccupationView.setText(occupation);
                    }
                }
                setContentShown(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mGetOccupationTask = null;
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

        if (mGetProfileInfoResponse.getGender() != null) {
            mGender = mGetProfileInfoResponse.getGender();
            mGenderView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

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
