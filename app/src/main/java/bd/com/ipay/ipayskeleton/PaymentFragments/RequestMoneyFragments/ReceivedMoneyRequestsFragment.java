package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.ReceivedRequestReviewActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.RequestMoneyReviewDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ReviewDialogFinishListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetMoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetMoneyAndPaymentRequestResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.MoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ReceivedMoneyRequestsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetAllNotificationsTask = null;
    private GetMoneyAndPaymentRequestResponse mGetMoneyAndPaymentRequestResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private RecyclerView mNotificationsRecyclerView;
    private ReceivedMoneyRequestListAdapter mReceivedMoneyRequestListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MoneyAndPaymentRequest> moneyRequestList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressDialog mProgressDialog;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean clearListAfterLoading;

    // These variables hold the information needed to populate the review dialog
    private BigDecimal mAmount;
    private BigDecimal mServiceCharge;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long mMoneyRequestId;
    private String mTitle;
    private String mDescription;
    private TextView mEmptyListTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_received_money_requests, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mNotificationsRecyclerView = (RecyclerView) v.findViewById(R.id.list_notification);
        mProgressDialog = new ProgressDialog(getActivity());

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mReceivedMoneyRequestListAdapter = new ReceivedMoneyRequestListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationsRecyclerView.setAdapter(mReceivedMoneyRequestListAdapter);

        // Refresh balance each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getMoneyRequests();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMoneyRequestList();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void refreshMoneyRequestList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            clearListAfterLoading = true;
            getMoneyRequests();
        }
    }

    private void getMoneyRequests() {
        if (mGetAllNotificationsTask != null) {
            return;
        }

        GetMoneyAndPaymentRequest mTransactionHistoryRequest = new GetMoneyAndPaymentRequest(pageCount,
                Constants.SERVICE_ID_REQUEST_MONEY);
        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mGetAllNotificationsTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_MONEY_REQUESTS,
                Constants.BASE_URL_SM + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mGetAllNotificationsTask.mHttpResponseListener = this;
        mGetAllNotificationsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void rejectRequestMoney(long id) {
        if (mRejectRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_rejecting));
        mProgressDialog.show();
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mRejectRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REJECT_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_CANCEL_NOTIFICATION_REQUEST, json, getActivity());
        mRejectRequestTask.mHttpResponseListener = this;
        mRejectRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showReviewDialog() {
        RequestMoneyReviewDialog dialog = new RequestMoneyReviewDialog(getActivity(), mMoneyRequestId, mReceiverMobileNumber,
                mReceiverName, mPhotoUri, mAmount, mServiceCharge, mTitle, mDescription, Constants.SERVICE_ID_REQUEST_MONEY,
                new ReviewDialogFinishListener() {
                    @Override
                    public void onReviewFinish() {
                        refreshMoneyRequestList();
                    }
                });
        dialog.show();
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mRejectRequestTask = null;
            mGetAllNotificationsTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
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
                        mReceivedMoneyRequestListAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG).show();
                }

                if (this.isAdded()) setContentShown(true);
                mGetAllNotificationsTask = null;
                mSwipeRefreshLayout.setRefreshing(false);

                break;
            case Constants.COMMAND_REJECT_REQUESTS_MONEY:

                try {
                    mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                            RequestMoneyAcceptRejectOrCancelResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            refreshMoneyRequestList();
                        }

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mRequestMoneyAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mRejectRequestTask = null;

                break;
        }
        if (moneyRequestList != null && moneyRequestList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private class ReceivedMoneyRequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;

        private final int ACTION_ACCEPT = 0;
        private final int ACTION_REJECT = 1;

        private final int REQUEST_MONEY_REVIEW_REQUEST = 101;

        public class MoneyRequestViewHolder extends RecyclerView.ViewHolder {
            private final TextView mDescriptionView;
            private final TextView mTitleView;
            private final TextView mTimeView;
            private final ProfileImageView mProfileImageView;

            private CustomSelectorDialog mCustomSelectorDialog;
            private List<String> mReceivedRequestActionList;

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
                final String description = moneyRequest.getDescription();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(moneyRequest.getRequestTime());
                final String title = moneyRequest.getTitle();
                final BigDecimal amount = moneyRequest.getAmount();

                mDescriptionView.setText(Utilities.formatTaka(amount));
                mTimeView.setText(time);

                mTitleView.setText(name);

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mReceivedRequestActionList = Arrays.asList(getResources().getStringArray(R.array.invoice_action));
                        mCustomSelectorDialog = new CustomSelectorDialog(getActivity(), name, mReceivedRequestActionList);
                        mCustomSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
                            @Override
                            public void onResourceSelected(int selectedIndex, String action) {
                                if (selectedIndex == ACTION_ACCEPT) {
                                    mMoneyRequestId = id;
                                    mAmount = amount;
                                    mReceiverName = name;
                                    mReceiverMobileNumber = mobileNumber;
                                    mPhotoUri = Constants.BASE_URL_FTP_SERVER + imageUrl;
                                    mTitle = title;
                                    mDescription = description;

                                   // attemptGetServiceCharge();
                                    launchReviewPage();

                                } else if (selectedIndex == ACTION_REJECT) {
                                    MaterialDialog.Builder rejectDialog = new MaterialDialog.Builder(getActivity());
                                    rejectDialog.content(R.string.confirm_request_rejection);
                                    rejectDialog.positiveText(R.string.yes);
                                    rejectDialog.negativeText(R.string.no);
                                    rejectDialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            rejectRequestMoney(id);
                                        }
                                    });
                                    rejectDialog.show();
                                }
                            }
                        });
                        mCustomSelectorDialog.show();
                    }
                });

            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            private TextView mLoadMoreTextView;

            public FooterViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasNext) {
                            pageCount = pageCount + 1;
                            getMoneyRequests();
                        }
                    }
                });

                mLoadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
            }

            public void bindView() {

                if (hasNext)
                    mLoadMoreTextView.setText(R.string.load_more);
                else
                    mLoadMoreTextView.setText(R.string.no_more_results);
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
                    vh.bindView();
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

            Intent intent = new Intent(getActivity(), ReceivedRequestReviewActivity.class);
            intent.putExtra(Constants.AMOUNT, mAmount);
            intent.putExtra(Constants.INVOICE_RECEIVER_TAG, ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
            intent.putExtra(Constants.INVOICE_DESCRIPTION_TAG, mDescription);
            intent.putExtra(Constants.INVOICE_TITLE_TAG, mTitle);
            intent.putExtra(Constants.MONEY_REQUEST_ID, mMoneyRequestId);
            intent.putExtra(Constants.NAME, mReceiverName);
            intent.putExtra(Constants.PHOTO_URI, mPhotoUri);

            startActivityForResult(intent, REQUEST_MONEY_REVIEW_REQUEST);
        }
    }


}
