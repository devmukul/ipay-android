package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.FriendPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetIntroducedListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetIntroducerListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetRecommendationRequestsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.Introduced;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.Introducer;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.RecommendationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.AskForIntroductionResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetIntroductionRequestsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroduceActionResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroductionRequestClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IdentificationFragment extends ProgressFragment implements HttpResponseListener {

    private final int PICK_CONTACT_REQUEST = 100;
    private int MINIMUM_INTRODUCER_COUNT;           // Default value

    private GetIntroducerListResponse mIntroducerListResponse;
    private HttpRequestGetAsyncTask mGetIntroducersTask = null;

    private GetIntroducedListResponse mIntroducedListResponse;
    private HttpRequestGetAsyncTask mGetIntroducedTask = null;

    private GetRecommendationRequestsResponse mSentRequestListResponse;
    private HttpRequestGetAsyncTask mGetSentRequestTask = null;

    private HttpRequestPostAsyncTask mAskForRecommendationTask = null;
    private AskForIntroductionResponse mAskForIntroductionResponse;

    private HttpRequestGetAsyncTask mGetRecommendationRequestsTask = null;
    private GetIntroductionRequestsResponse mRecommendationRequestsResponse;

    private HttpRequestPostAsyncTask mRecommendActionTask = null;
    private IntroduceActionResponse mIntroduceActionResponse;

    private List<IntroductionRequestClass> mRecommendationRequestList;
    private List<RecommendationRequest> mSentRequestList;
    private List<Introduced> mIntroducedList;
    private List<Introducer> mIntroducerList;
    //private List<BaseIdentification> mBaseList;

    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private TextView mEmptyListTextView;
    private RelativeLayout mCompleteIntroducerHeaderLayout;
    private TextView mIntroducerStatusTextView;
    private Button mButtonAskForRecommendation;
    private IntroduceAdapter mIntroduceAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_introducer_requests, container, false);
        (getActivity()).setTitle(R.string.profile_introducers);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.list_introducer_requests);
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mCompleteIntroducerHeaderLayout = (RelativeLayout) v.findViewById(R.id.complete_introduction_header);
        mIntroducerStatusTextView = (TextView) v.findViewById(R.id.intoduce_status);
        mButtonAskForRecommendation = (Button) v.findViewById(R.id.button_ask_for_recommendation);

        mProgressDialog = new ProgressDialog(getActivity());

        if (Utilities.isConnectionAvailable(getActivity())) {
            getIntroducerList();
            getIntroducedList();
            getSentRequestList();
            getRecommendationRequestsList();
        }

        mIntroduceAdapter = new IntroduceAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mIntroduceAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void getIntroducerList() {
        if (mGetIntroducersTask != null) {
            return;
        }

        mGetIntroducersTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INTRODUCER_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_UPSTREAM_APPROVED_INTRODUCTION_REQUESTS, getActivity());
        mGetIntroducersTask.mHttpResponseListener = this;
        mGetIntroducersTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getIntroducedList() {
        if (mGetIntroducedTask != null) {
            return;
        }

        mGetIntroducedTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INTRODUCED_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_DOWNSTREAM_APPROVED_INTRODUCTION_REQUESTS, getActivity());
        mGetIntroducedTask.mHttpResponseListener = this;
        mGetIntroducedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getSentRequestList() {
        if (mGetSentRequestTask != null) {
            return;
        }

        mGetSentRequestTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SENT_REQUEST_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_UPSTREAM_NOT_APPROVED_INTRODUCTION_REQUESTS, getActivity());
        mGetSentRequestTask.mHttpResponseListener = this;
        mGetSentRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendRecommendationRequest(String mobileNumber) {
        if (mAskForRecommendationTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_send_for_recommendation));
        mProgressDialog.show();
        mAskForRecommendationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ASK_FOR_RECOMMENDATION,
                Constants.BASE_URL_MM + Constants.URL_ASK_FOR_INTRODUCTION + mobileNumber, null, getActivity());
        mAskForRecommendationTask.mHttpResponseListener = this;
        mAskForRecommendationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getRecommendationRequestsList() {
        if (mGetRecommendationRequestsTask != null) {
            return;
        }

        mGetRecommendationRequestsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_RECOMMENDATION_REQUESTS,
                Constants.BASE_URL_MM + Constants.URL_GET_DOWNSTREAM_NOT_APPROVED_INTRODUCTION_REQUESTS, getActivity());
        mGetRecommendationRequestsTask.mHttpResponseListener = this;
        mGetRecommendationRequestsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptSetRecommendationStatus(long requestID, String recommendationStatus) {
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

    private void refreshIntroductionRequestList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            getRecommendationRequestsList();
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) throws RuntimeException {


        mGetRecommendationRequestsTask = null;
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetIntroducersTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_INTRODUCER_LIST)) {
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mIntroducerListResponse = gson.fromJson(result.getJsonString(), GetIntroducerListResponse.class);

                    if (mIntroducerList == null) {
                        mIntroducerList = mIntroducerListResponse.getIntroducers();
                        MINIMUM_INTRODUCER_COUNT = mIntroducerListResponse.getRequiredForProfileCompletion();

                        if (mIntroducerList.size() < MINIMUM_INTRODUCER_COUNT) {
                            mCompleteIntroducerHeaderLayout.setVisibility(View.VISIBLE);
                            mIntroducerStatusTextView.setText(getString(R.string.you_need_to_have) + MINIMUM_INTRODUCER_COUNT
                                    + getString(R.string.introducers_to_complete_the_account_verification_process));
                        } else mCompleteIntroducerHeaderLayout.setVisibility(View.GONE);

                        mButtonAskForRecommendation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), FriendPickerDialogActivity.class);
                                intent.putExtra(Constants.VERIFIED_USERS_ONLY, true);                   // Get the verified iPay users only.
                                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                            }
                        });

                    } else {
                        List<Introducer> tempIntroducerClasses;
                        tempIntroducerClasses = mIntroducerListResponse.getIntroducers();
                        mIntroducerList.clear();
                        mIntroducerList.addAll(tempIntroducerClasses);
                    }
                    //mBaseList.addAll(mIntroducerList);
                    mIntroduceAdapter.notifyDataSetChanged();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }


        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_INTRODUCED_LIST)) {
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mIntroducedListResponse = gson.fromJson(result.getJsonString(), GetIntroducedListResponse.class);

                    if (mIntroducedList == null) {
                        mIntroducedList = mIntroducedListResponse.getIntroducedList();
                    } else {
                        List<Introduced> tempIntroducedClasses;
                        tempIntroducedClasses = mIntroducedListResponse.getIntroducedList();
                        mIntroducedList.clear();
                        mIntroducedList.addAll(tempIntroducedClasses);
                    }
                    //mBaseList.addAll(mIntroducedList);
                    mIntroduceAdapter.notifyDataSetChanged();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_SENT_REQUEST_LIST)) {
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mSentRequestListResponse = gson.fromJson(result.getJsonString(), GetRecommendationRequestsResponse.class);

                    if (mSentRequestList == null) {
                        mSentRequestList = mSentRequestListResponse.getSentRequestList();
                    } else {
                        List<RecommendationRequest> tempIntroducerClasses;
                        tempIntroducerClasses = mSentRequestListResponse.getSentRequestList();
                        mSentRequestList.clear();
                        mSentRequestList.addAll(tempIntroducerClasses);
                    }
                    //mBaseList.addAll(mSentRequestList);
                    mIntroduceAdapter.notifyDataSetChanged();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }

        } else if (result.getApiCommand().equals(Constants.COMMAND_ASK_FOR_RECOMMENDATION)) {
            try {

                mAskForIntroductionResponse = gson.fromJson(result.getJsonString(), AskForIntroductionResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.ask_for_recommendation_sent, Toast.LENGTH_LONG).show();
                    }
                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), mAskForIntroductionResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_asking_recommendation, Toast.LENGTH_LONG).show();
                }
            }
            mProgressDialog.dismiss();
            mAskForRecommendationTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_RECOMMENDATION_REQUESTS)) {

            if (this.isAdded()) setContentShown(true);
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mRecommendationRequestsResponse = gson.fromJson(result.getJsonString(), GetIntroductionRequestsResponse.class);

                    if (mRecommendationRequestList == null) {
                        mRecommendationRequestList = mRecommendationRequestsResponse.getVerificationRequestList();
                    } else {
                        List<IntroductionRequestClass> tempRecommendationRequestsClasses;
                        tempRecommendationRequestsClasses = mRecommendationRequestsResponse.getVerificationRequestList();
                        mRecommendationRequestList.clear();
                        mRecommendationRequestList.addAll(tempRecommendationRequestsClasses);
                    }

                    if (mRecommendationRequestList != null)
                        mIntroduceAdapter.notifyDataSetChanged();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mGetRecommendationRequestsTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_INTRODUCE_ACTION)) {

            try {
                mIntroduceActionResponse = gson.fromJson(result.getJsonString(), IntroduceActionResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mIntroduceActionResponse.getMessage(), Toast.LENGTH_LONG).show();

                    // Refresh recommendation requests list
                    if (mRecommendationRequestList != null)
                        mRecommendationRequestList.clear();
                    mRecommendationRequestList = null;
                    refreshIntroductionRequestList();
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
        }

        try {
            if (isAdded())
                setContentShown(true);
            if (mIntroducerList != null && mIntroducerList.size() == 0 && mIntroducedList != null
                    && mIntroducedList.size() == 0 && mSentRequestList != null && mSentRequestList.size() == 0)
                mEmptyListTextView.setVisibility(View.VISIBLE);
            else mEmptyListTextView.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class IntroduceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int INTRODUCER_LIST_ITEM_VIEW = 1;
        private static final int INTRODUCER_LIST_HEADER_VIEW = 2;
        private static final int INTRODUCED_LIST_ITEM_VIEW = 3;
        private static final int INTRODUCED_LIST_HEADER_VIEW = 4;
        private static final int SENT_REQUEST_LIST_ITEM_VIEW = 5;
        private static final int SENT_REQUEST_LIST_HEADER_VIEW = 6;
        private static final int RECOMMENDATION_ITEM_VIEW = 7;
        private static final int RECOMMENDATION_HEADER_VIEW = 8;

        public IntroduceAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mPortraitTextView;

            private TextView mIntroducerName;
            private TextView mIntroducerMobileNumber;
            private RoundedImageView mIntroducerProfilePictureView;

            private TextView mIntroducedName;
            private TextView mIntroducedMobileNumber;
            private RoundedImageView mIntroducedProfilePictureView;

            private TextView mRequestedName;
            private TextView mRequestedMobileNumber;
            private RoundedImageView mRequestedProfilePictureView;
            private ImageView mSentRequestStatus;
            private TextView mTimeView;

            private LinearLayout optionsLayout;
            private TextView mSenderName;
            private TextView mSenderMobileNumber;
            private TextView mDate;
            private Button verifyButton;
            private Button rejectRecommendationButton;
            private Button markAsSpamRecommendationButton;
            private ProfileImageView mProfileImageView;

            private View divider;

            public ViewHolder(final View itemView) {
                super(itemView);

                mPortraitTextView = (TextView) itemView.findViewById(R.id.portraitTxt);

                mIntroducerName = (TextView) itemView.findViewById(R.id.introducer_name);
                mIntroducerMobileNumber = (TextView) itemView.findViewById(R.id.introducer_mobile_number);
                mIntroducerProfilePictureView = (RoundedImageView) itemView.findViewById(R.id.portrait);

                mIntroducedName = (TextView) itemView.findViewById(R.id.introduced_name);
                mIntroducedMobileNumber = (TextView) itemView.findViewById(R.id.introduced_mobile_number);
                mIntroducedProfilePictureView = (RoundedImageView) itemView.findViewById(R.id.portrait);

                mRequestedName = (TextView) itemView.findViewById(R.id.requested_name);
                mRequestedMobileNumber = (TextView) itemView.findViewById(R.id.requested_mobile_number);
                mRequestedProfilePictureView = (RoundedImageView) itemView.findViewById(R.id.portrait);
                mSentRequestStatus = (ImageView) itemView.findViewById(R.id.request_status);
                mTimeView = (TextView) itemView.findViewById(R.id.time);

                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                mSenderName = (TextView) itemView.findViewById(R.id.textview_title);
                mSenderMobileNumber = (TextView) itemView.findViewById(R.id.textview_description);
                mDate = (TextView) itemView.findViewById(R.id.textview_time);
                verifyButton = (Button) itemView.findViewById(R.id.verify_button);
                rejectRecommendationButton = (Button) itemView.findViewById(R.id.reject_button);
                markAsSpamRecommendationButton = (Button) itemView.findViewById(R.id.mark_as_spam_button);

                divider = itemView.findViewById(R.id.divider);

            }

            private void setProfilePicture(String url, RoundedImageView pictureView, String name) {

                int position = getAdapterPosition();
                final int randomColor = position % 10;

                if (name.startsWith("+") && name.length() > 1)
                    mPortraitTextView.setText(String.valueOf(name.substring(1).charAt(0)).toUpperCase());
                else mPortraitTextView.setText(String.valueOf(name.charAt(0)).toUpperCase());


                if (randomColor == 0)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle);
                else if (randomColor == 1)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_blue);
                else if (randomColor == 2)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_brightpink);
                else if (randomColor == 3)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_cyan);
                else if (randomColor == 4)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_megenta);
                else if (randomColor == 5)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_orange);
                else if (randomColor == 6)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_red);
                else if (randomColor == 7)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_springgreen);
                else if (randomColor == 8)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_violet);
                else if (randomColor == 9)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_yellow);
                else
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_azure);

                if (url != null) {
                    url = Constants.BASE_URL_FTP_SERVER + url;
                    Glide.with(getActivity())
                            .load(url)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(pictureView);
                } else {
                    Glide.with(getActivity())
                            .load(android.R.color.transparent)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(pictureView);
                }

            }

            public void bindViewForIntroducerList(int pos) {

                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                if(pos==0) {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_upper_round_white));

                } else if(pos== mIntroducerList.size() -1) {
                    divider.setVisibility(View.GONE);
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_lower_round_white));

                } else {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_no_round_white));

                }

                //Toast.makeText(getActivity(), "list er size : " + mBaseList.size() , Toast.LENGTH_LONG).show();

                final String introducerName = mIntroducerList.get(pos).getName();
                final String introducerMobileNumber = mIntroducerList.get(pos).getMobileNumber();
                final long introducedTime = mIntroducerList.get(pos).getIntroducedDate();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy").format(mIntroducerList.get(pos).getIntroducedDate());
                String imageUrl = mIntroducerList.get(pos).getProfilePictureUrl();
                setProfilePicture(imageUrl, mIntroducerProfilePictureView, introducerName);
                mIntroducerName.setText(introducerName);
                mIntroducerMobileNumber.setText(introducerMobileNumber);
                if (introducedTime == 0) mTimeView.setVisibility(View.GONE);
                else mTimeView.setText("Introduced on: " + time);
            }

            public void bindViewForIntroducedList(int pos) {

                if (mIntroducerList == null) pos = pos - 1;
                else {
                    if (mIntroducerList.size() == 0) pos = pos - 1;
                    else pos = pos - mIntroducerList.size() - 2;
                }

                if(pos== 0) {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_upper_round_white));

                } else if(pos== mIntroducedList.size() -1) {
                    divider.setVisibility(View.GONE);
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_lower_round_white));

                } else {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_no_round_white));

                }

                final String introducedName = mIntroducedList.get(pos).getName();
                final String introducedMobileNumber = mIntroducedList.get(pos).getMobileNumber();
                String imageUrl = mIntroducedList.get(pos).getProfilePictureUrl();
                setProfilePicture(imageUrl, mIntroducedProfilePictureView, introducedName);
                mIntroducedName.setText(introducedName);
                mIntroducedMobileNumber.setText(introducedMobileNumber);
            }

            public void bindViewForSentRequestList(int pos) {

                if (mIntroducerList == null && mIntroducedList == null) pos = pos - 1;
                else if (mIntroducedList == null) {
                    if (mIntroducerList.size() == 0) pos = pos - 1;
                    else pos = pos - mIntroducerList.size() - 2;
                } else if (mIntroducerList == null) {
                    if (mIntroducedList.size() == 0) pos = pos - 1;
                    else pos = pos - mIntroducedList.size() - 2;
                } else {
                    if (mIntroducedList.size() == 0 && mIntroducerList.size() == 0) pos = pos - 1;
                    else if (mIntroducedList.size() == 0) pos = pos - mIntroducerList.size() - 2;
                    else if (mIntroducerList.size() == 0) pos = pos - mIntroducedList.size() - 2;
                    else pos = pos - mIntroducedList.size() - mIntroducerList.size() - 3;
                }

                if(pos==0) {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_upper_round_white));

                } else if(pos== mSentRequestList.size() -1) {
                    divider.setVisibility(View.GONE);
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_lower_round_white));

                } else {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_no_round_white));

                }

                final String RequestedName = mSentRequestList.get(pos).getName();
                final String RequestedMobileNumber = mSentRequestList.get(pos).getMobileNumber();
                final String requestStatus = mSentRequestList.get(pos).getStatus();
                String imageUrl = mSentRequestList.get(pos).getProfilePictureUrl();
                setProfilePicture(imageUrl, mRequestedProfilePictureView, RequestedName);
                mRequestedName.setText(RequestedName);
                mRequestedMobileNumber.setText(RequestedMobileNumber);

                if (requestStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_PENDING)) {
                    mSentRequestStatus.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                } else if (requestStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_APPROVED)) {
                    mSentRequestStatus.setImageResource(R.drawable.ic_verified3x);
                } else if (requestStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_SPAM)) {
                    mSentRequestStatus.setImageResource(R.drawable.ic_error_black_24dp);
                } else {
                    // INTRODUCTION_REQUEST_STATUS_REJECTED
                    mSentRequestStatus.setImageResource(R.drawable.ic_notverified3x);
                }

            }

            public void bindViewRecommendationList(int pos) {

                if (mIntroducerList == null && mIntroducedList == null && mSentRequestList == null) pos = pos - 1;
                else if (mIntroducerList == null && mIntroducedList == null) {
                    if (mSentRequestList.size() == 0) pos = pos - 1;
                    else pos = pos - mSentRequestList.size() - 2;
                } else if (mIntroducerList == null && mSentRequestList == null) {
                    if (mIntroducedList.size() == 0) pos = pos - 1;
                    else pos = pos - mIntroducedList.size() - 2;
                } else if (mIntroducedList == null && mSentRequestList == null) {
                    if (mIntroducerList.size() == 0) pos = pos - 1;
                    else pos = pos - mIntroducerList.size() - 2;
                } else if (mIntroducedList == null) {
                    if (mIntroducerList.size() == 0 && mSentRequestList.size() == 0 ) pos = pos - 1;
                    else if(mIntroducerList.size() == 0) pos = pos - mSentRequestList.size() - 2;
                    else if(mSentRequestList.size() == 0) pos = pos - mIntroducerList.size() - 2;
                    else pos = pos - mIntroducerList.size() - mSentRequestList.size() - 3;
                } else if (mIntroducerList == null) {
                    if (mIntroducedList.size() == 0 && mSentRequestList.size() == 0 ) pos = pos - 1;
                    else if(mIntroducedList.size() == 0) pos = pos - mSentRequestList.size() - 2;
                    else if(mSentRequestList.size() == 0) pos = pos - mIntroducedList.size() - 2;
                    else pos = pos - mIntroducedList.size() - mSentRequestList.size() - 3;
                } else if (mSentRequestList == null) {
                    if (mIntroducerList.size() == 0 && mIntroducedList.size() == 0 ) pos = pos - 1;
                    else if(mIntroducerList.size() == 0) pos = pos - mIntroducedList.size() - 2;
                    else if(mIntroducedList.size() == 0) pos = pos - mIntroducerList.size() - 2;
                    else pos = pos - mIntroducerList.size() - mIntroducedList.size() - 3;
                } else {
                    if (mIntroducedList.size() == 0 && mIntroducerList.size() == 0 && mSentRequestList.size() == 0) pos = pos - 1;
                    else if (mIntroducedList.size() == 0 && mSentRequestList.size() == 0) pos = pos - mIntroducerList.size() - 2;
                    else if (mIntroducerList.size() == 0 && mSentRequestList.size() == 0) pos = pos - mIntroducedList.size() - 2;
                    else if (mIntroducerList.size() == 0 && mIntroducedList.size() == 0) pos = pos - mSentRequestList.size() - 2;
                    else if (mIntroducerList.size() == 0) {
                        pos = pos - mIntroducedList.size() - mSentRequestList.size() - 3;
                    } else if (mIntroducedList.size() == 0) {
                        pos = pos - mIntroducerList.size() - mSentRequestList.size() - 3;
                    } else if (mSentRequestList.size() == 0) {
                        pos = pos - mIntroducerList.size() - mIntroducedList.size() - 3;
                    }
                    else pos = pos - mIntroducedList.size() - mIntroducerList.size() - mSentRequestList.size() - 4;
                }

                if (mRecommendationRequestList.size() == 1) {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_upper_round_white));

                } else if (pos == 0) {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_upper_round_white));

                } else if(pos == mRecommendationRequestList.size() -1) {
                    divider.setVisibility(View.GONE);
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_lower_round_white));

                } else {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_no_round_white));

                }

                final String imageUrl = mRecommendationRequestList.get(pos).getProfilePictureUrl();
                final long requestID = mRecommendationRequestList.get(pos).getId();
                final String senderName = mRecommendationRequestList.get(pos).getSenderName();
                final String senderMobileNumber = mRecommendationRequestList.get(pos).getSenderMobileNumber();
                final String recommendationStatus = mRecommendationRequestList.get(pos).getStatus();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(mRecommendationRequestList.get(pos).getDate());

                mSenderName.setText(senderName);
                mSenderMobileNumber.setText(senderMobileNumber);
                mDate.setText(time);


                //mProfileImageView.setProfilePicture( imageUrl, false);

                optionsLayout.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.INTRODUCTION_REQUEST_STATUS_PENDING)) {
                            if (optionsLayout.getVisibility() == View.VISIBLE) {
                                optionsLayout.setVisibility(View.GONE);
                            }
                            else optionsLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });

                verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.INTRODUCTION_REQUEST_STATUS_PENDING))
                            new android.app.AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.are_you_sure)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            attemptSetRecommendationStatus(requestID, Constants.INTRODUCTION_REQUEST_ACTION_APPROVE);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                        }
                                    })
                                    .show();
                    }
                });

                rejectRecommendationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.INTRODUCTION_REQUEST_STATUS_PENDING))
                            new android.app.AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.are_you_sure)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            attemptSetRecommendationStatus(requestID, Constants.INTRODUCTION_REQUEST_ACTION_REJECT);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                        }
                                    })
                                    .show();
                    }
                });

                markAsSpamRecommendationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.INTRODUCTION_REQUEST_STATUS_PENDING))
                            new android.app.AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.are_you_sure)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            attemptSetRecommendationStatus(requestID, Constants.INTRODUCTION_REQUEST_ACTION_MARK_AS_SPAM);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                        }
                                    })
                                    .show();
                    }
                });
            }
        }

        private class IntroducerListHeaderViewHolder extends ViewHolder {
            public IntroducerListHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class IntroducerListItemViewHolder extends ViewHolder {
            public IntroducerListItemViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class IntroducedListHeaderViewHolder extends ViewHolder {
            public IntroducedListHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class IntroducedListItemViewHolder extends ViewHolder {
            public IntroducedListItemViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class SentRequestListHeaderViewHolder extends ViewHolder {
            public SentRequestListHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class SentRequestListItemViewHolder extends ViewHolder {
            public SentRequestListItemViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class RecommendationListHeaderViewHolder extends ViewHolder {
            public RecommendationListHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class RecommendationRequestViewHolder extends ViewHolder {
            public RecommendationRequestViewHolder(View itemView) {
                super(itemView);
            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == INTRODUCER_LIST_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introducer_list, parent, false);
                IntroducerListItemViewHolder vh = new IntroducerListItemViewHolder(v);
                return vh;

            } else if (viewType == INTRODUCER_LIST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introducer_list_header, parent, false);
                IntroducerListHeaderViewHolder vh = new IntroducerListHeaderViewHolder(v);
                return vh;
            } else if (viewType == INTRODUCED_LIST_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduced_list, parent, false);
                IntroducedListItemViewHolder vh = new IntroducedListItemViewHolder(v);
                return vh;

            } else if (viewType == INTRODUCED_LIST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduced_list_header, parent, false);
                IntroducedListHeaderViewHolder vh = new IntroducedListHeaderViewHolder(v);
                return vh;

            } else if (viewType == SENT_REQUEST_LIST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduction_request_from_me_header, parent, false);
                SentRequestListHeaderViewHolder vh = new SentRequestListHeaderViewHolder(v);
                return vh;

            } else if (viewType == RECOMMENDATION_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduction_requests, parent, false);
                RecommendationRequestViewHolder vh = new RecommendationRequestViewHolder(v);
                return vh;

            } else if (viewType == RECOMMENDATION_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recommendation_requests_header, parent, false);
                RecommendationListHeaderViewHolder vh = new RecommendationListHeaderViewHolder(v);
                return vh;

            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduction_request_from_me, parent, false);
                SentRequestListItemViewHolder vh = new SentRequestListItemViewHolder(v);
                return vh;

            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof SentRequestListItemViewHolder) {
                    SentRequestListItemViewHolder vh = (SentRequestListItemViewHolder) holder;
                    vh.bindViewForSentRequestList(position);

                } else if (holder instanceof SentRequestListHeaderViewHolder) {
                    SentRequestListHeaderViewHolder vh = (SentRequestListHeaderViewHolder) holder;

                } else if (holder instanceof IntroducedListItemViewHolder) {
                    IntroducedListItemViewHolder vh = (IntroducedListItemViewHolder) holder;
                    vh.bindViewForIntroducedList(position);

                } else if (holder instanceof IntroducedListHeaderViewHolder) {
                    IntroducedListHeaderViewHolder vh = (IntroducedListHeaderViewHolder) holder;

                } else if (holder instanceof IntroducerListHeaderViewHolder) {
                    IntroducerListHeaderViewHolder vh = (IntroducerListHeaderViewHolder) holder;

                } else if (holder instanceof IntroducerListItemViewHolder) {
                    IntroducerListItemViewHolder vh = (IntroducerListItemViewHolder) holder;
                    vh.bindViewForIntroducerList(position);

                } else if (holder instanceof RecommendationRequestViewHolder) {
                    RecommendationRequestViewHolder vh = (RecommendationRequestViewHolder) holder;
                    vh.bindViewRecommendationList(position);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {

            int introducerListSize = 0;
            int introducedListSize = 0;
            int sentRequestsListSize = 0;
            int recommendationRequestListSize = 0;

            if (mIntroducerList == null && mSentRequestList == null && mIntroducedList == null && mRecommendationRequestList == null )
                return 0;


            // Get the sizes of the lists
            if (mSentRequestList != null)
                sentRequestsListSize = mSentRequestList.size();
            if (mIntroducerList != null)
                introducerListSize = mIntroducerList.size();
            if (mIntroducedList != null)
                introducedListSize = mIntroducedList.size();
            if(mRecommendationRequestList != null)
                recommendationRequestListSize = mRecommendationRequestList.size();


            if (introducerListSize > 0 && introducedListSize > 0 && sentRequestsListSize > 0 && recommendationRequestListSize == 0)
                return 1 + introducerListSize + 1 + introducedListSize + 1 + sentRequestsListSize;
            else if (introducerListSize > 0 && introducedListSize > 0 && sentRequestsListSize > 0 && recommendationRequestListSize > 0)
                return 1 + introducerListSize + 1 + introducedListSize + 1 + sentRequestsListSize + 1 +recommendationRequestListSize ;
            else if (introducerListSize > 0 && introducedListSize > 0 && sentRequestsListSize == 0 && recommendationRequestListSize == 0)
                return 1 + introducerListSize + 1 + introducedListSize;
            else if (introducerListSize > 0 && introducedListSize > 0 && sentRequestsListSize == 0 && recommendationRequestListSize > 0)
                return 1 + introducerListSize + 1 + introducedListSize + 1 + recommendationRequestListSize;
            else if (introducerListSize > 0 && introducedListSize == 0 && sentRequestsListSize > 0 && recommendationRequestListSize == 0)
                return 1 + introducerListSize + 1 + sentRequestsListSize;
            else if (introducerListSize > 0 && introducedListSize == 0 && sentRequestsListSize > 0 && recommendationRequestListSize > 0)
                return 1 + introducerListSize + 1 + sentRequestsListSize + 1 + recommendationRequestListSize;
            else if (introducerListSize == 0 && introducedListSize > 0 && sentRequestsListSize > 0 && recommendationRequestListSize == 0)
                return 1 + sentRequestsListSize + 1 + introducedListSize;
            else if (introducerListSize == 0 && introducedListSize > 0 && sentRequestsListSize > 0 && recommendationRequestListSize > 0)
                return 1 + sentRequestsListSize + 1 + introducedListSize + 1 + recommendationRequestListSize;
            else if (introducerListSize > 0 && introducedListSize == 0 && sentRequestsListSize == 0 && recommendationRequestListSize == 0)
                return 1 + introducerListSize;
            else if (introducerListSize > 0 && introducedListSize == 0 && sentRequestsListSize == 0 && recommendationRequestListSize > 0)
                return 1 + introducerListSize + 1 + recommendationRequestListSize;
            else if (introducerListSize == 0 && introducedListSize > 0 && sentRequestsListSize == 0 && recommendationRequestListSize == 0)
                return 1 + introducedListSize;
            else if (introducerListSize == 0 && introducedListSize > 0 && sentRequestsListSize == 0 && recommendationRequestListSize > 0)
                return 1 + introducedListSize + 1 + recommendationRequestListSize;
            else if (introducerListSize == 0 && introducedListSize == 0 && sentRequestsListSize > 0 && recommendationRequestListSize == 0)
                return 1 + sentRequestsListSize;
            else if (introducerListSize == 0 && introducedListSize == 0 && sentRequestsListSize > 0 && recommendationRequestListSize > 0)
                return 1 + sentRequestsListSize +1 + recommendationRequestListSize;
            else return 0;

        }

        @Override
        public int getItemViewType(int position) {

            int introducerListSize = 0;
            int introducedListSize = 0;
            int sentRequestsListSize = 0;
            int recommendationRequestListSize = 0;

            if (mSentRequestList == null && mIntroducerList == null && mIntroducedList == null && mRecommendationRequestList == null)
                return super.getItemViewType(position);

            if (mSentRequestList != null)
                sentRequestsListSize = mSentRequestList.size();
            if (mIntroducerList != null)
                introducerListSize = mIntroducerList.size();
            if (mIntroducedList != null)
                introducedListSize = mIntroducedList.size();
            if(mRecommendationRequestList != null)
                recommendationRequestListSize = mRecommendationRequestList.size();

            if (sentRequestsListSize > 0 && introducerListSize > 0 && introducedListSize > 0 && recommendationRequestListSize == 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return INTRODUCED_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1 && position < introducerListSize + 1 + introducedListSize + 1)
                    return INTRODUCED_LIST_ITEM_VIEW;
                else if (position == introducerListSize + 1 + introducedListSize + 1)
                    return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1 + introducedListSize + 1)
                    return SENT_REQUEST_LIST_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize > 0 && introducerListSize > 0 && introducedListSize > 0 && recommendationRequestListSize > 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return INTRODUCED_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1 && position < introducerListSize + 1 + introducedListSize + 1)
                    return INTRODUCED_LIST_ITEM_VIEW;
                else if (position == introducerListSize + 1 + introducedListSize + 1)
                    return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1 + introducedListSize + 1 && position < introducerListSize + introducedListSize + sentRequestsListSize + 3)
                    return SENT_REQUEST_LIST_ITEM_VIEW;
                else if (position == introducerListSize + 1 + introducedListSize + 1 + sentRequestsListSize +1)
                    return RECOMMENDATION_HEADER_VIEW;
                else if (position > introducerListSize + introducedListSize + sentRequestsListSize + 3)
                    return RECOMMENDATION_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize > 0 && introducerListSize > 0 && introducedListSize == 0  && recommendationRequestListSize == 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1)
                    return SENT_REQUEST_LIST_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;

            }  else if (sentRequestsListSize > 0 && introducerListSize > 0 && introducedListSize == 0  && recommendationRequestListSize > 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1 && position < introducerListSize + sentRequestsListSize + 2)
                    return SENT_REQUEST_LIST_ITEM_VIEW;
                else if (position == introducerListSize + 1 + sentRequestsListSize + 1)
                    return RECOMMENDATION_HEADER_VIEW;
                else if (position > introducerListSize + sentRequestsListSize + 2)
                    return RECOMMENDATION_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize > 0 && introducerListSize == 0 && introducedListSize > 0  && recommendationRequestListSize == 0) {
                if (position == 0) return INTRODUCED_LIST_HEADER_VIEW;
                else if (position == introducedListSize + 1)
                    return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position > introducedListSize + 1)
                    return SENT_REQUEST_LIST_ITEM_VIEW;
                else return INTRODUCED_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize > 0 && introducerListSize == 0 && introducedListSize > 0  && recommendationRequestListSize > 0) {
                if (position == 0) return INTRODUCED_LIST_HEADER_VIEW;
                else if (position == introducedListSize + 1)
                    return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position > introducedListSize + 1 && position < introducerListSize + introducedListSize + 2)
                    return SENT_REQUEST_LIST_ITEM_VIEW;
                else if (position == sentRequestsListSize + introducedListSize + 2)
                    return RECOMMENDATION_HEADER_VIEW;
                else if (position > sentRequestsListSize + introducedListSize + 2)
                    return RECOMMENDATION_ITEM_VIEW;
                else return INTRODUCED_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize == 0 && introducerListSize > 0 && introducedListSize > 0  && recommendationRequestListSize == 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return INTRODUCED_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1)
                    return INTRODUCED_LIST_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize == 0 && introducerListSize > 0 && introducedListSize > 0  && recommendationRequestListSize > 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return INTRODUCED_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1 && position < introducerListSize + introducedListSize + 2)
                    return INTRODUCED_LIST_ITEM_VIEW;
                else if (position == introducerListSize + introducedListSize + 2)
                    return RECOMMENDATION_HEADER_VIEW;
                else if (position > introducerListSize + introducedListSize + 2)
                    return RECOMMENDATION_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize > 0 && introducerListSize == 0 && introducedListSize == 0  && recommendationRequestListSize == 0) {
                if (position == 0) return SENT_REQUEST_LIST_HEADER_VIEW;
                else return SENT_REQUEST_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize > 0 && introducerListSize == 0 && introducedListSize == 0  && recommendationRequestListSize > 0) {
                if (position == 0) return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position == sentRequestsListSize + 1)
                    return RECOMMENDATION_HEADER_VIEW;
                else if (position > sentRequestsListSize + 1)
                    return RECOMMENDATION_ITEM_VIEW;
                else return SENT_REQUEST_LIST_ITEM_VIEW;

            } else if (sentRequestsListSize == 0 && introducerListSize > 0 && introducedListSize == 0  && recommendationRequestListSize == 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;
            } else if (sentRequestsListSize == 0 && introducerListSize > 0 && introducedListSize == 0  && recommendationRequestListSize > 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return RECOMMENDATION_HEADER_VIEW;
                else if (position > introducerListSize + 1)
                    return RECOMMENDATION_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;
            } else if (sentRequestsListSize == 0 && introducerListSize == 0 && introducedListSize > 0  && recommendationRequestListSize == 0) {
                if (position == 0) return INTRODUCED_LIST_HEADER_VIEW;
                else return INTRODUCED_LIST_ITEM_VIEW;
            } else if (sentRequestsListSize == 0 && introducerListSize == 0 && introducedListSize > 0  && recommendationRequestListSize > 0) {
                if (position == 0) return INTRODUCED_LIST_HEADER_VIEW;
                else if (position == introducedListSize + 1)
                    return RECOMMENDATION_HEADER_VIEW;
                else if (position > introducedListSize + 1)
                    return RECOMMENDATION_ITEM_VIEW;
                else return INTRODUCED_LIST_ITEM_VIEW;
            }
            return super.getItemViewType(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {

            String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);

            if (mobileNumber != null) sendRecommendationRequest(mobileNumber);
            else
                Toast.makeText(getActivity(), R.string.could_not_fetch_the_number, Toast.LENGTH_SHORT).show();

        }
    }
}
