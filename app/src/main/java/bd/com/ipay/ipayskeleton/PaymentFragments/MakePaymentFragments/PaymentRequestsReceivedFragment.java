package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SentReceivedRequestPaymentReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.GetMoneyAndPaymentRequestResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.MoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.GetMoneyRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PaymentRequestsReceivedFragment extends ProgressFragment implements HttpResponseListener {
    private final int REQUEST_PAYMENT_REVIEW_REQUEST = 101;

    private HttpRequestPostAsyncTask mGetAllNotificationsTask = null;
    private GetMoneyAndPaymentRequestResponse mGetMoneyAndPaymentRequestResponse;

    private RecyclerView mRequestPaymentRecyclerView;
    private RequestPaymentListAdapter mRequestPaymentListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MoneyAndPaymentRequest> moneyRequestList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean isLoading = false;
    private boolean clearListAfterLoading;

    private TextView mEmptyListTextView;

    private Tracker mTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment_request_received, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mRequestPaymentRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mRequestPaymentListAdapter = new RequestPaymentListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRequestPaymentRecyclerView.setLayoutManager(mLayoutManager);
        mRequestPaymentRecyclerView.setAdapter(mRequestPaymentListAdapter);

        // Refresh each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getMakePaymentRequests();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCount = 0;
                clearListAfterLoading = true;
                refreshNotificationList();
            }
        });

        return v;
    }


    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.payments);
        if (Utilities.isConnectionAvailable(getActivity())) {
            refreshNotificationList();
        }

        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_payment_request_received));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void refreshNotificationList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            clearListAfterLoading = true;
            getMakePaymentRequests();
        }
    }


    private void getMakePaymentRequests() {
        if (mGetAllNotificationsTask != null) {
            return;
        }

        GetMoneyRequest mMoneyRequest = new GetMoneyRequest(pageCount,
                Constants.SERVICE_ID_REQUEST_PAYMENT, Constants.MONEY_REQUEST_STATUS_PROCESSING);
        Gson gson = new Gson();
        String json = gson.toJson(mMoneyRequest);
        mGetAllNotificationsTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_MONEY_REQUESTS,
                Constants.BASE_URL_SM + Constants.URL_GET_NOTIFICATIONS, json, getActivity(), false);
        mGetAllNotificationsTask.mHttpResponseListener = this;
        mGetAllNotificationsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (this.isAdded()) setContentShown(true);
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetAllNotificationsTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_MONEY_REQUESTS:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    try {
                        mGetMoneyAndPaymentRequestResponse = gson.fromJson(result.getJsonString(), GetMoneyAndPaymentRequestResponse.class);

                        if (moneyRequestList == null || clearListAfterLoading || moneyRequestList.size() == 0) {
                            moneyRequestList = mGetMoneyAndPaymentRequestResponse.getAllMoneyAndPaymentRequests();
                            clearListAfterLoading = false;
                        } else {
                            List<MoneyAndPaymentRequest> tempNotificationList;
                            tempNotificationList = mGetMoneyAndPaymentRequestResponse.getAllMoneyAndPaymentRequests();
                            moneyRequestList.addAll(tempNotificationList);
                        }

                        hasNext = mGetMoneyAndPaymentRequestResponse.isHasNext();
                        if (isLoading) isLoading = false;
                        mRequestPaymentListAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG);
                    }

                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG);
                }

                mGetAllNotificationsTask = null;
                mSwipeRefreshLayout.setRefreshing(false);

                break;
        }

        if (moneyRequestList != null && moneyRequestList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private class RequestPaymentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;

        public RequestPaymentListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mDescriptionView;
            private final TextView mTitleView;
            private final TextView mTimeView;
            private final ProfileImageView mProfileImageView;
            private BigDecimal mAmount;
            private String mReceiverName;
            private String mReceiverMobileNumber;
            private String mPhotoUri;
            private long mMoneyRequestId;
            private String mDescription;
            private int mStatus;
            private String mTime;

            public ViewHolder(final View itemView) {
                super(itemView);

                // Money request list items
                mDescriptionView = (TextView) itemView.findViewById(R.id.textview_description);
                mTimeView = (TextView) itemView.findViewById(R.id.textview_time);
                mTitleView = (TextView) itemView.findViewById(R.id.textview_title);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
            }

            public void bindViewMoneyRequestList(int pos) {
                final MoneyAndPaymentRequest moneyRequest = moneyRequestList.get(pos);
                mMoneyRequestId = moneyRequest.getId();
                mPhotoUri = moneyRequest.getOriginatorProfile().getUserProfilePicture();
                mReceiverName = moneyRequest.originatorProfile.getUserName();
                mReceiverMobileNumber = moneyRequest.originatorProfile.getUserMobileNumber();
                mDescription = moneyRequest.getDescriptionofRequest();
                mTime = Utilities.formatDateWithTime(moneyRequest.getRequestTime());
                mStatus = moneyRequest.getStatus();
                mAmount = moneyRequest.getAmount();

                mDescriptionView.setText(Utilities.formatTaka(mAmount));
                mTimeView.setText(mTime);
                mTitleView.setText(mReceiverName);

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mPhotoUri, false);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SentReceivedRequestPaymentReviewActivity.class);
                        intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_RECEIVED_REQUEST);
                        intent.putExtra(Constants.AMOUNT, mAmount);
                        intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
                        intent.putExtra(Constants.DESCRIPTION_TAG, mDescription);
                        intent.putExtra(Constants.MONEY_REQUEST_ID, mMoneyRequestId);
                        intent.putExtra(Constants.STATUS, mStatus);
                        intent.putExtra(Constants.NAME, mReceiverName);
                        intent.putExtra(Constants.PHOTO_URI, mPhotoUri);
                        intent.putExtra(Constants.IS_IN_CONTACTS, new ContactSearchHelper(getActivity()).searchMobileNumber(mReceiverMobileNumber));

                        startActivityForResult(intent, REQUEST_PAYMENT_REVIEW_REQUEST);
                    }
                });
            }
        }

        public class FooterViewHolder extends ViewHolder {
            private TextView mLoadMoreTextView;
            private ProgressBar mLoadMoreProgressBar;

            public FooterViewHolder(View itemView) {
                super(itemView);

                mLoadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mLoadMoreProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            }

            public void bindViewFooter() {
                setItemVisibilityOfFooterView();

                mLoadMoreTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasNext) {
                            pageCount = pageCount + 1;
                            showLoadingInFooter();
                            getMakePaymentRequests();
                        }
                    }
                });
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

        private class MoneyRequestViewHolder extends ViewHolder {
            public MoneyRequestViewHolder(View itemView) {
                super(itemView);
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
                    vh.bindViewMoneyRequestList(position);

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
                return moneyRequestList.size() + 1; // money requests list, footer
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (moneyRequestList == null)
                return super.getItemViewType(position);

            else if (position == getItemCount() - 1)
                return FOOTER_VIEW;
            else
                return MONEY_REQUEST_ITEM_VIEW;
        }
    }
}