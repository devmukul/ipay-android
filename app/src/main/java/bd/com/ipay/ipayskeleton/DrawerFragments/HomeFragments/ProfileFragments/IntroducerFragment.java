package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.FriendPickerActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetIntroducedListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetIntroducerListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetRecommendationRequestsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.Introduced;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.Introducer;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.RecommendationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.AskForIntroductionResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IntroducerFragment extends ProgressFragment implements HttpResponseListener {

    private final int PICK_CONTACT_REQUEST = 100;
    private int MINIMUM_INTRODUCER_COUNT = 2;           // Default value

    private GetIntroducerListResponse mIntroducerListResponse;
    private HttpRequestGetAsyncTask mGetIntroducersTask = null;

    private GetIntroducedListResponse mIntroducedListResponse;
    private HttpRequestGetAsyncTask mGetIntroducedTask = null;

    private GetRecommendationRequestsResponse mSentRequestListResponse;
    private HttpRequestGetAsyncTask mGetSentRequestTask = null;

    private HttpRequestPostAsyncTask mAskForRecommendationTask = null;
    private AskForIntroductionResponse mAskForIntroductionResponse;

    private List<RecommendationRequest> mRecommendationRequestList;
    private List<Introduced> mIntroducedList;
    private List<Introducer> mIntroducerList;

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

    @Override
    public void httpResponseReceiver(HttpResponseObject result) throws RuntimeException {
        if (result == null) {
            mProgressDialog.dismiss();
            mGetIntroducersTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
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
                                Intent intent = new Intent(getActivity(), FriendPickerActivity.class);
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

                    if (mRecommendationRequestList == null) {
                        mRecommendationRequestList = mSentRequestListResponse.getSentRequestList();
                    } else {
                        List<RecommendationRequest> tempIntroducerClasses;
                        tempIntroducerClasses = mSentRequestListResponse.getSentRequestList();
                        mRecommendationRequestList.clear();
                        mRecommendationRequestList.addAll(tempIntroducerClasses);
                    }
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
        }

        try {
            setContentShown(true);
            if (mIntroducerList != null && mIntroducerList.size() == 0 && mIntroducedList != null
                    && mIntroducedList.size() == 0 && mRecommendationRequestList != null && mRecommendationRequestList.size() == 0)
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
                    url = Constants.BASE_URL_IMAGE_SERVER + url;
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

                final String introducerName = mIntroducerList.get(pos).getName();
                final String introducerMobileNumber = mIntroducerList.get(pos).getMobileNumber();
                String imageUrl = mIntroducerList.get(pos).getprofilePictureUrl();
                setProfilePicture(imageUrl, mIntroducerProfilePictureView, introducerName);
                mIntroducerName.setText(introducerName);
                mIntroducerMobileNumber.setText(introducerMobileNumber);
            }

            public void bindViewForIntroducedList(int pos) {

                if (mIntroducerList == null) pos = pos - 1;
                else {
                    if (mIntroducerList.size() == 0) pos = pos - 1;
                    else pos = pos - mIntroducerList.size() - 2;
                }

                final String introducedName = mIntroducedList.get(pos).getName();
                final String introducedMobileNumber = mIntroducedList.get(pos).getMobileNumber();
                String imageUrl = mIntroducedList.get(pos).getprofilePictureUrl();
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

                final String RequestedName = mRecommendationRequestList.get(pos).getName();
                final String RequestedMobileNumber = mRecommendationRequestList.get(pos).getMobileNumber();
                final String requestStatus = mRecommendationRequestList.get(pos).getStatus();
                String imageUrl = mRecommendationRequestList.get(pos).getProfilePictureUrl();
                setProfilePicture(imageUrl, mRequestedProfilePictureView, RequestedName);
                mRequestedName.setText(RequestedName);
                mRequestedMobileNumber.setText(RequestedMobileNumber);

                if (requestStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_PENDING)) {
                    mSentRequestStatus.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                } else if (requestStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_APPROVED)) {
                    mSentRequestStatus.setImageResource(R.drawable.ic_verified_user_black_24dp);
                } else if (requestStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_SPAM)) {
                    mSentRequestStatus.setImageResource(R.drawable.ic_error_black_24dp);
                } else {
                    // INTRODUCTION_REQUEST_STATUS_REJECTED
                    mSentRequestStatus.setImageResource(R.drawable.ic_warning_black_24dp);
                }

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

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {

            int introducerListSize = 0;
            int introducedListSize = 0;
            int recommendationRequestsListSize = 0;

            if (mIntroducerList == null && mRecommendationRequestList == null && mIntroducedList == null)
                return 0;


            // Get the sizes of the lists
            if (mRecommendationRequestList != null)
                recommendationRequestsListSize = mRecommendationRequestList.size();
            if (mIntroducerList != null)
                introducerListSize = mIntroducerList.size();
            if (mIntroducedList != null)
                introducedListSize = mIntroducedList.size();


            if (introducerListSize > 0 && introducedListSize > 0 && recommendationRequestsListSize > 0)
                return 1 + introducerListSize + 1 + introducedListSize + 1 + recommendationRequestsListSize;
            else if (introducerListSize > 0 && introducedListSize > 0 && recommendationRequestsListSize == 0)
                return 1 + introducerListSize + 1 + introducedListSize;
            else if (introducerListSize > 0 && introducedListSize == 0 && recommendationRequestsListSize > 0)
                return 1 + introducerListSize + 1 + recommendationRequestsListSize;
            else if (introducerListSize == 0 && introducedListSize > 0 && recommendationRequestsListSize > 0)
                return 1 + recommendationRequestsListSize + 1 + introducedListSize;
            else if (introducerListSize > 0 && introducedListSize == 0 && recommendationRequestsListSize == 0)
                return 1 + introducerListSize;
            else if (introducerListSize == 0 && introducedListSize > 0 && recommendationRequestsListSize == 0)
                return 1 + introducedListSize;
            else if (introducerListSize == 0 && introducedListSize == 0 && recommendationRequestsListSize > 0)
                return 1 + recommendationRequestsListSize;
            else return 0;

        }

        @Override
        public int getItemViewType(int position) {

            int introducerListSize = 0;
            int introducedListSize = 0;
            int recommendationRequestsListSize = 0;

            if (mRecommendationRequestList == null && mIntroducerList == null && mIntroducedList == null)
                return super.getItemViewType(position);

            if (mRecommendationRequestList != null)
                recommendationRequestsListSize = mRecommendationRequestList.size();
            if (mIntroducerList != null)
                introducerListSize = mIntroducerList.size();
            if (mIntroducedList != null)
                introducedListSize = mIntroducedList.size();

            if (recommendationRequestsListSize > 0 && introducerListSize > 0 && introducedListSize > 0) {
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

            } else if (recommendationRequestsListSize > 0 && introducerListSize > 0 && introducedListSize == 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1)
                    return SENT_REQUEST_LIST_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;

            } else if (recommendationRequestsListSize > 0 && introducerListSize == 0 && introducedListSize > 0) {
                if (position == 0) return INTRODUCED_LIST_HEADER_VIEW;
                else if (position == introducedListSize + 1)
                    return SENT_REQUEST_LIST_HEADER_VIEW;
                else if (position > introducedListSize + 1)
                    return SENT_REQUEST_LIST_ITEM_VIEW;
                else return INTRODUCED_LIST_ITEM_VIEW;

            } else if (recommendationRequestsListSize == 0 && introducerListSize > 0 && introducedListSize > 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else if (position == introducerListSize + 1)
                    return INTRODUCED_LIST_HEADER_VIEW;
                else if (position > introducerListSize + 1)
                    return INTRODUCED_LIST_ITEM_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;

            } else if (recommendationRequestsListSize > 0 && introducerListSize == 0 && introducedListSize == 0) {
                if (position == 0) return SENT_REQUEST_LIST_HEADER_VIEW;
                else return SENT_REQUEST_LIST_ITEM_VIEW;

            } else if (recommendationRequestsListSize == 0 && introducerListSize > 0 && introducedListSize == 0) {
                if (position == 0) return INTRODUCER_LIST_HEADER_VIEW;
                else return INTRODUCER_LIST_ITEM_VIEW;
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
