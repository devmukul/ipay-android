package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SentReceivedRequestReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.GetMoneyAndPaymentRequestResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.MoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.GetMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ReceivedMoneyRequestsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetMoneyRequestTask = null;
    private GetMoneyAndPaymentRequestResponse mGetMoneyAndPaymentRequestResponse;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private RecyclerView mNotificationsRecyclerView;
    private ReceivedMoneyRequestListAdapter mReceivedMoneyRequestListAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<MoneyAndPaymentRequest> moneyRequestList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean isLoading = false;
    private boolean clearListAfterLoading;
    private boolean mIsScrolled = false;
    private int mTotalItemCount =0;
    private  int mPastVisiblesItems;
    private  int mVisibleItem;

    // These variables hold the information needed to populate the review dialog
    private BigDecimal mAmount;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long mMoneyRequestId;
    private String mDescription;
    private TextView mEmptyListTextView;

    private View mProgressContainer;
    private View mContentContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_money_requests, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mNotificationsRecyclerView = (RecyclerView) view.findViewById(R.id.list_notification);

        mEmptyListTextView = (TextView) view.findViewById(R.id.empty_list_text);
        mProgressContainer = view.findViewById(R.id.progress_container);
        mContentContainer = view.findViewById(R.id.content_container);

        mReceivedMoneyRequestListAdapter = new ReceivedMoneyRequestListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationsRecyclerView.setAdapter(mReceivedMoneyRequestListAdapter);

        implementScrollListener();

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getMoneyRequestList(true);
                    }
                });
            }
        });

        return view;
    }

    private void getMoneyRequestList(boolean showWarningDialog) {
        if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.RECEIVED_REQUEST)) {
            if (showWarningDialog) DialogUtils.showServiceNotAllowedDialog(getContext());

            mProgressContainer.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
            mEmptyListTextView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
        } else if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            clearListAfterLoading = true;
            getMoneyRequests();
        }
    }


    public void onResume() {
        super.onResume();
        getMoneyRequestList(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void getMoneyRequests() {
        if (mGetMoneyRequestTask != null) {
            return;
        }
        GetMoneyRequest mMoneyRequest = new GetMoneyRequest(pageCount,
                Constants.SERVICE_ID_REQUEST_MONEY, Constants.MONEY_REQUEST_STATUS_PROCESSING);
        Gson gson = new Gson();
        String json = gson.toJson(mMoneyRequest);
        mGetMoneyRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_MONEY_REQUESTS,
                Constants.BASE_URL_SM + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mGetMoneyRequestTask.mHttpResponseListener = this;
        mGetMoneyRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void implementScrollListener() {
        mNotificationsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisibleItem = recyclerView.getChildCount();
                mTotalItemCount =mLayoutManager.getItemCount();
                mPastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                if (mIsScrolled
                        && (mVisibleItem + mPastVisiblesItems) == mTotalItemCount && hasNext) {
                    isLoading = true;
                    mIsScrolled = false;
                    pageCount = pageCount + 1;
                    mReceivedMoneyRequestListAdapter.notifyDataSetChanged();
                    getMoneyRequests();
                }

            }

        });

    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (this.isAdded()) setContentShown(true);
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetMoneyRequestTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null) {
                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
            }
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_MONEY_REQUESTS:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    try {
                        mGetMoneyAndPaymentRequestResponse = gson.fromJson(result.getJsonString(), GetMoneyAndPaymentRequestResponse.class);

                        if (clearListAfterLoading || moneyRequestList == null) {
                            moneyRequestList = mGetMoneyAndPaymentRequestResponse.getAllMoneyAndPaymentRequests();
                            clearListAfterLoading = false;
                        } else {
                            List<MoneyAndPaymentRequest> tempNotificationList;
                            tempNotificationList = mGetMoneyAndPaymentRequestResponse.getAllMoneyAndPaymentRequests();
                            moneyRequestList.addAll(tempNotificationList);
                        }

                        hasNext = mGetMoneyAndPaymentRequestResponse.isHasNext();
                        if (isLoading) isLoading = false;
                        mReceivedMoneyRequestListAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG);
                    }

                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG);
                }

                mGetMoneyRequestTask = null;
                mSwipeRefreshLayout.setRefreshing(false);

                break;
        }
        if (moneyRequestList != null && moneyRequestList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private class ReceivedMoneyRequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;

        private final int REQUEST_MONEY_REVIEW_REQUEST = 101;

        public class MoneyRequestViewHolder extends RecyclerView.ViewHolder {
            private final TextView mDescriptionView;
            private final TextView mTitleView;
            private final TextView mTimeView;
            private final ProfileImageView mProfileImageView;


            public MoneyRequestViewHolder(final View itemView) {
                super(itemView);

                // Money request list items
                mDescriptionView = (TextView) itemView.findViewById(R.id.textview_description);
                mTimeView = (TextView) itemView.findViewById(R.id.textview_time);
                mTitleView = (TextView) itemView.findViewById(R.id.textview_title);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
            }

            public void bindView(int pos) {
                final MoneyAndPaymentRequest moneyRequest = moneyRequestList.get(pos);

                final long id = moneyRequest.getId();
                final String imageUrl = moneyRequest.getOriginatorProfile().getUserProfilePicture();
                final String name = moneyRequest.originatorProfile.getUserName();
                final String mobileNumber = moneyRequest.originatorProfile.getUserMobileNumber();
                final String description = moneyRequest.getDescriptionofRequest();
                final String time = Utilities.formatDateWithTime(moneyRequest.getRequestTime());
                final String title = moneyRequest.getTitle();
                final BigDecimal amount = moneyRequest.getAmount();

                mDescriptionView.setText(Utilities.formatTaka(amount));
                mTimeView.setText(time);

                mTitleView.setText(name);

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mMoneyRequestId = id;
                        mAmount = amount;
                        mReceiverName = name;
                        mReceiverMobileNumber = mobileNumber;
                        mPhotoUri = Constants.BASE_URL_FTP_SERVER + imageUrl;
                        mDescription = description;

                        launchReviewPage();
                    }
                });

            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            private TextView mLoadMoreTextView;
            private ProgressBar mLoadMoreProgressBar;

            public FooterViewHolder(View itemView) {
                super(itemView);

                mLoadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mLoadMoreProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            }

            public void bindViewFooter() {
                setItemVisibilityOfFooterView();
            }

            private void setItemVisibilityOfFooterView() {
                if (isLoading) {
                    mLoadMoreProgressBar.setVisibility(View.VISIBLE);
                    mLoadMoreTextView.setVisibility(View.GONE);
                } else {
                    mLoadMoreProgressBar.setVisibility(View.GONE);
                    mLoadMoreTextView.setVisibility(View.VISIBLE);

                    if (hasNext)
                        mLoadMoreTextView.setText(R.string.load_more);
                    else
                        mLoadMoreTextView.setText(R.string.no_more_results);
                }
            }

            private void showLoadingInFooter() {
                isLoading = true;
                notifyDataSetChanged();
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_load_more_footer, parent, false);
                return new FooterViewHolder(v);

            } else {
                // MONEY_REQUEST_ITEM_VIEW
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_money_and_make_payment_request, parent, false);
                return new MoneyRequestViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof MoneyRequestViewHolder) {
                    MoneyRequestViewHolder vh = (MoneyRequestViewHolder) holder;
                    vh.bindView(position);

                } else if (holder instanceof FooterViewHolder) {
                    FooterViewHolder vh = (FooterViewHolder) holder;
                    vh.bindViewFooter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (moneyRequestList == null || moneyRequestList.size() == 0) {
                return 0;
            } else {
                return 1 + moneyRequestList.size(); // header, money requests list, footer
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (position == getItemCount() - 1) {
                return FOOTER_VIEW;
            } else
                return MONEY_REQUEST_ITEM_VIEW;

        }

        private void launchReviewPage() {

            Intent intent = new Intent(getActivity(), SentReceivedRequestReviewActivity.class);
            intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_RECEIVED_REQUEST);
            intent.putExtra(Constants.AMOUNT, mAmount);
            intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
            intent.putExtra(Constants.DESCRIPTION_TAG, mDescription);
            intent.putExtra(Constants.MONEY_REQUEST_ID, mMoneyRequestId);
            intent.putExtra(Constants.NAME, mReceiverName);
            intent.putExtra(Constants.PHOTO_URI, mPhotoUri);
            intent.putExtra(Constants.IS_IN_CONTACTS, new ContactSearchHelper(getActivity()).searchMobileNumber(mReceiverMobileNumber));

            startActivityForResult(intent, REQUEST_MONEY_REVIEW_REQUEST);
        }
    }
}
