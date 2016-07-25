package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetIntroducedListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.Introduced;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetIntroductionRequestsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroduceActionResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroductionRequestClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IntroducedFragment extends ProgressFragment implements HttpResponseListener {
    ;

    private GetIntroducedListResponse mIntroducedListResponse;
    private HttpRequestGetAsyncTask mGetIntroducedTask = null;

    private HttpRequestGetAsyncTask mGetRecommendationRequestsTask = null;
    private GetIntroductionRequestsResponse mRecommendationRequestsResponse;

    private HttpRequestPostAsyncTask mRecommendActionTask = null;
    private IntroduceActionResponse mIntroduceActionResponse;

    private List<IntroductionRequestClass> mRecommendationRequestList;
    private List<Introduced> mIntroducedList;

    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private TextView mEmptyListTextView;
    private IntroducdAdapter mIntroduceAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_introduced_requests, container, false);
        (getActivity()).setTitle(R.string.profile_introducers);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.list_introduced_requests);
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);

        mProgressDialog = new ProgressDialog(getActivity());

        if (Utilities.isConnectionAvailable(getActivity())) {
            getIntroducedList();
            getRecommendationRequestsList();
        }

        mIntroduceAdapter = new IntroducdAdapter();
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

    private void getIntroducedList() {
        if (mGetIntroducedTask != null) {
            return;
        }

        mGetIntroducedTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INTRODUCED_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_DOWNSTREAM_APPROVED_INTRODUCTION_REQUESTS, getActivity());
        mGetIntroducedTask.mHttpResponseListener = this;
        mGetIntroducedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_INTRODUCED_LIST)) {
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
                Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
            }

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
            if (mIntroducedList != null && mIntroducedList.size() == 0 && mRecommendationRequestList != null && mRecommendationRequestList.size() == 0)
                mEmptyListTextView.setVisibility(View.VISIBLE);
            else mEmptyListTextView.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class IntroducdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int RECOMMENDATION_ITEM_VIEW = 1;
        private static final int RECOMMENDATION_HEADER_VIEW = 2;
        private static final int INTRODUCED_LIST_ITEM_VIEW = 3;
        private static final int INTRODUCED_LIST_HEADER_VIEW = 4;

        public IntroducdAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mPortraitTextView;


            private TextView mIntroducedName;
            private TextView mIntroducedMobileNumber;
            private RoundedImageView mIntroducedProfilePictureView;


            private LinearLayout optionsLayout;
            private TextView mSenderName;
            private TextView mSenderMobileNumber;
            private TextView mDate;
            private Button verifyButton;
            private Button rejectRecommendationButton;
            private Button markAsSpamRecommendationButton;

            private View divider;

            public ViewHolder(final View itemView) {
                super(itemView);

                mPortraitTextView = (TextView) itemView.findViewById(R.id.portraitTxt);

                mIntroducedName = (TextView) itemView.findViewById(R.id.introduced_name);
                mIntroducedMobileNumber = (TextView) itemView.findViewById(R.id.introduced_mobile_number);
                mIntroducedProfilePictureView = (RoundedImageView) itemView.findViewById(R.id.portrait);

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

            public void bindViewRecommendationList(int pos) {

                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

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

            public void bindViewForIntroducedList(int pos) {

                if (mRecommendationRequestList == null) pos = pos - 1;
                else {
                    if (mRecommendationRequestList.size() == 0) pos = pos - 1;
                    else pos = pos - mRecommendationRequestList.size() - 2;
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

            if (viewType == INTRODUCED_LIST_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduced_list, parent, false);
                IntroducedListItemViewHolder vh = new IntroducedListItemViewHolder(v);
                return vh;

            } else if (viewType == INTRODUCED_LIST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduced_list_header, parent, false);
                IntroducedListHeaderViewHolder vh = new IntroducedListHeaderViewHolder(v);
                return vh;

            }  else if (viewType == RECOMMENDATION_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduction_requests, parent, false);
                RecommendationRequestViewHolder vh = new RecommendationRequestViewHolder(v);
                return vh;

            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recommendation_requests_header, parent, false);
                RecommendationListHeaderViewHolder vh = new RecommendationListHeaderViewHolder(v);
                return vh;

            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof IntroducedListItemViewHolder) {
                    IntroducedListItemViewHolder vh = (IntroducedListItemViewHolder) holder;
                    vh.bindViewForIntroducedList(position);

                } else if (holder instanceof IntroducedListHeaderViewHolder) {
                    IntroducedListHeaderViewHolder vh = (IntroducedListHeaderViewHolder) holder;

                }else if (holder instanceof RecommendationRequestViewHolder) {
                    RecommendationRequestViewHolder vh = (RecommendationRequestViewHolder) holder;
                    vh.bindViewRecommendationList(position);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public int getItemCount() {

            int introducedListSize = 0;
            int recommendationRequestListSize = 0;

            if (mRecommendationRequestList == null && mIntroducedList == null )
                return 0;

            if (mRecommendationRequestList != null)
                recommendationRequestListSize = mRecommendationRequestList.size();
            if(mIntroducedList != null)
                introducedListSize = mIntroducedList.size();

            if (recommendationRequestListSize > 0 && introducedListSize > 0)
                return 1 + recommendationRequestListSize + 1 + introducedListSize ;
            else if (introducedListSize > 0 && recommendationRequestListSize == 0)
                return 1 + introducedListSize ;
            else if (introducedListSize == 0 && recommendationRequestListSize > 0)
                return 1 + recommendationRequestListSize;
            else return 0;

        }

        @Override
        public int getItemViewType(int position) {

            int recommendationRequestListSize = 0;
            int introducedListSize = 0;

            if (mRecommendationRequestList == null && mIntroducedList == null)
                return super.getItemViewType(position);

            if (mRecommendationRequestList != null)
                recommendationRequestListSize = mRecommendationRequestList.size();
            if(mIntroducedList != null)
                introducedListSize = mIntroducedList.size();

            if (recommendationRequestListSize > 0 && introducedListSize > 0) {
                if (position == 0) return RECOMMENDATION_HEADER_VIEW;
                else if (position == recommendationRequestListSize + 1)
                    return INTRODUCED_LIST_HEADER_VIEW;
                else if (position > recommendationRequestListSize + 1)
                    return INTRODUCED_LIST_ITEM_VIEW;
                else return RECOMMENDATION_ITEM_VIEW;

            } else if (introducedListSize > 0 && recommendationRequestListSize == 0) {
                if (position == 0) return INTRODUCED_LIST_HEADER_VIEW;
                else return INTRODUCED_LIST_ITEM_VIEW;

            } else if (introducedListSize == 0 && recommendationRequestListSize > 0) {
                if (position == 0) return RECOMMENDATION_HEADER_VIEW;
                else return RECOMMENDATION_ITEM_VIEW;
            }
            return super.getItemViewType(position);
        }
    }

}
