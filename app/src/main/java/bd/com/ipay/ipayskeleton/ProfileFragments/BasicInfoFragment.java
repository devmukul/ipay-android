package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetOccupationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.OccupationRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class BasicInfoFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private HttpRequestGetAsyncTask mGetOccupationTask = null;
    private GetOccupationResponse mGetOccupationResponse;

    private ProgressDialog mProgressDialog;

    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mVerificationStatusView;

    private TextView mFathersNameView;
    private TextView mMothersNameView;

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

    private int mOccupation = 0;
    private String mGender = "";
    private String mSignUpTime = "";
    private String mVerificationStatus = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!ProfileInfoCacheManager.getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED))
            inflater.inflate(R.menu.edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            launchEditFragment();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
        mMothersNameView = (TextView) v.findViewById(R.id.textview_mothers_name);
        mDateOfBirthView = (TextView) v.findViewById(R.id.textview_dob);
        mOccupationView = (TextView) v.findViewById(R.id.textview_occupation);
        mGenderView = (TextView) v.findViewById(R.id.textview_gender);
        mSignUpTimeView = (TextView) v.findViewById(R.id.textview_signup);
        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        mGender = pref.getString(Constants.GENDER, "");
        mDateOfBirth = pref.getString(Constants.BIRTHDAY, "");
        mProgressDialog = new ProgressDialog(getActivity());

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    private void setProfileInformation() {

        mMobileNumberView.setText(mMobileNumber);
        mNameView.setText(mName);

        mGenderView.setText(mGender);
        mDateOfBirthView.setText(mDateOfBirth);

        mFathersNameView.setText(mFathersName);
        mMothersNameView.setText(mMothersName);
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
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_OCCUPATIONS_REQUEST)) {

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
        }
    }

    private void processProfileInfoResponse(String json) {
        Gson gson = new Gson();
        mGetProfileInfoResponse = gson.fromJson(json, GetProfileInfoResponse.class);

        if (mGetProfileInfoResponse.getName() != null)
            mName = mGetProfileInfoResponse.getName();
        if (mGetProfileInfoResponse.getMobileNumber() != null)
            mMobileNumber = mGetProfileInfoResponse.getMobileNumber();

        if (mGetProfileInfoResponse.getFather() != null)
            mFathersName = mGetProfileInfoResponse.getFather();
        if (mGetProfileInfoResponse.getMother() != null)
            mMothersName = mGetProfileInfoResponse.getMother();

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

        setContentShown(true);
    }
}
