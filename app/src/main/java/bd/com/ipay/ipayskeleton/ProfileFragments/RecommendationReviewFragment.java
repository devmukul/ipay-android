package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.AddFriendAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.Friend.AddFriendRequest;
import bd.com.ipay.ipayskeleton.Model.Friend.InfoAddFriend;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.IntroductionAndInvite.IntroduceActionResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.District;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.DistrictRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Thana;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.ThanaRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class RecommendationReviewFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetThanaListAsyncTask = null;
    private GetThanaResponse mGetThanaResponse;

    private HttpRequestGetAsyncTask mGetDistrictListAsyncTask = null;
    private GetDistrictResponse mGetDistrictResponse;

    private HttpRequestPostAsyncTask mRecommendActionTask = null;
    private IntroduceActionResponse mIntroduceActionResponse;

    private ProgressDialog mProgressDialog;

    private long mRequestID;
    private String mSenderName;
    private String mSenderMobileNumber;
    private String mPhotoUri;
    private String mFathersName = null;
    private String mMothersname = null;
    private String mIntroductionMessage;

    private AddressClass mAddress;

    private boolean isInContacts;

    private List<Thana> mThanaList;
    private List<District> mDistrictList;


    private ProfileImageView mProfileImageView;
    private TextView mSenderNameView;
    private TextView mSenderMobileNumberView;
    private TextView mFathersNameView;
    private TextView mMothersNameView;
    private TextView mAddressView;

    private Button mRejectButton;
    private Button mAcceptButton;
    private Button mSpamButton;

    private CheckBox mAddInContactsCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recommendation_review, container, false);

        getActivity().setTitle(R.string.Introducer);
        Bundle bundle = getArguments();

        mRequestID = bundle.getLong(Constants.REQUEST_ID);
        mSenderName = bundle.getString(Constants.NAME);
        mSenderMobileNumber = bundle.getString(Constants.MOBILE_NUMBER);
        mPhotoUri = bundle.getString(Constants.PHOTO_URI);
        mMothersname = bundle.getString(Constants.MOTHERS_NAME);
        mFathersName = bundle.getString(Constants.FATHERS_NAME);
        mAddress = (AddressClass) getArguments().getSerializable(Constants.ADDRESS);
        isInContacts = bundle.getBoolean(Constants.IS_IN_CONTACTS, false);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mSenderNameView = (TextView) v.findViewById(R.id.textview_name);
        mSenderMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mFathersNameView = (TextView) v.findViewById(R.id.textview_fathers_name);
        mMothersNameView = (TextView) v.findViewById(R.id.textview_mothers_name);
        mAddressView = (TextView) v.findViewById(R.id.textview_present_address);
        mAddInContactsCheckBox = (CheckBox) v.findViewById(R.id.add_in_contacts);

        mAcceptButton = (Button) v.findViewById(R.id.button_accept);
        mRejectButton = (Button) v.findViewById(R.id.button_reject);
        mSpamButton = (Button) v.findViewById(R.id.button_spam);

        mProgressDialog = new ProgressDialog(getActivity());

        mProfileImageView.setProfilePicture(mPhotoUri, false);

        if (mSenderName == null || mSenderName.isEmpty()) {
            mSenderNameView.setVisibility(View.GONE);
        } else {
            mSenderNameView.setText(mSenderName);
        }

        if (!(mFathersName == null || mFathersName.isEmpty())) {
            mFathersNameView.setText(mFathersName);
        }

        if (!(mMothersname == null || mMothersname.isEmpty())) {
            mMothersNameView.setText(mMothersname);
        }

        if (!isInContacts) {
            mAddInContactsCheckBox.setVisibility(View.VISIBLE);
            mAddInContactsCheckBox.setChecked(true);
        }

        mSenderMobileNumberView.setText(mSenderMobileNumber);

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIntroductionMessage = getString(R.string.introduction_request_review_dialog_content);
                mIntroductionMessage = mIntroductionMessage.replace(getString(R.string.this_person), mSenderName);

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.are_you_sure)
                        .content(mIntroductionMessage)
                        .positiveText(R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                attemptSetRecommendationStatus(mRequestID, Constants.INTRODUCTION_REQUEST_ACTION_APPROVE);
                            }
                        })
                        .negativeText(R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // Do nothing
                            }
                        })
                        .show();

            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.are_you_sure)
                        .content(R.string.introduction_request_reject_dialog_content)
                        .positiveText(R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                attemptSetRecommendationStatus(mRequestID, Constants.INTRODUCTION_REQUEST_ACTION_REJECT);
                            }
                        })
                        .negativeText(R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // Do nothing
                            }
                        })
                        .show();

            }
        });

        mSpamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.are_you_sure)
                        .content(R.string.introduction_request_spam_dialog_content)
                        .positiveText(R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                attemptSetRecommendationStatus(mRequestID, Constants.INTRODUCTION_REQUEST_ACTION_MARK_AS_SPAM);
                            }
                        })
                        .negativeText(R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // Do nothing
                            }
                        })
                        .show();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAddress != null)
            getDistrictList();
        else setContentShown(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void attemptSetRecommendationStatus(long requestID, String recommendationStatus) {
        if (mAddInContactsCheckBox.isChecked()) {
            addFriend(mSenderName, mSenderMobileNumber, null);
        }
        if (requestID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.verifying_user));
        mProgressDialog.show();
        mRecommendActionTask = new HttpRequestPostAsyncTask(Constants.COMMAND_INTRODUCE_ACTION,
                Constants.BASE_URL_MM + Constants.URL_INTRODUCE_ACTION + requestID + "/" + recommendationStatus, null, getActivity());
        mRecommendActionTask.mHttpResponseListener = this;
        mRecommendActionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void getDistrictList() {
        mGetDistrictListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DISTRICT_LIST,
                new DistrictRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetDistrictListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getThanaList() {
        mGetThanaListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_THANA_LIST,
                new ThanaRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetThanaListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadAddresses() {
        if (mAddress == null)
            mAddressView.setVisibility(View.GONE);
        else
            mAddressView.setText(mAddress.toString(mThanaList, mDistrictList));

        if (this.isAdded()) setContentShown(true);
    }

    private void addFriend(String name, String phoneNumber, String relationship) {
        List<InfoAddFriend> newFriends = new ArrayList<>();
        newFriends.add(new InfoAddFriend(ContactEngine.formatMobileNumberBD(phoneNumber), name, relationship));

        AddFriendRequest addFriendRequest = new AddFriendRequest(newFriends);
        Gson gson = new Gson();
        String json = gson.toJson(addFriendRequest);

        new AddFriendAsyncTask(Constants.COMMAND_ADD_FRIENDS,
                Constants.BASE_URL_FRIEND + Constants.URL_ADD_FRIENDS, json, getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetThanaListAsyncTask = null;
            mGetDistrictListAsyncTask = null;
            mRecommendActionTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {

            case Constants.COMMAND_INTRODUCE_ACTION:

                try {
                    mIntroduceActionResponse = gson.fromJson(result.getJsonString(), IntroduceActionResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mIntroduceActionResponse.getMessage(), Toast.LENGTH_LONG).show();

                        getActivity().onBackPressed();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mIntroduceActionResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mRecommendActionTask = null;
                break;
            case Constants.COMMAND_GET_THANA_LIST:
                try {
                    mGetThanaResponse = gson.fromJson(result.getJsonString(), GetThanaResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mThanaList = mGetThanaResponse.getThanas();
                        loadAddresses();

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

}

