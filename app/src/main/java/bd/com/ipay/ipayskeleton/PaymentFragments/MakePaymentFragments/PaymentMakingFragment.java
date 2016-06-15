package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ReviewDialogFinishListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ReviewMakePaymentDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetNotificationsRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetNotificationsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.NotificationClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PaymentMakingFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetAllNotificationsTask = null;
    private GetNotificationsResponse mGetNotificationsResponse;

    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private RecyclerView mNotificationsRecyclerView;
    private NotificationListAdapter mNotificationListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<NotificationClass> moneyRequestList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressDialog mProgressDialog;

    private int pageCount = 0;
    private boolean hasNext = false;

    // These variables hold the information needed to populate the review dialog
    private List<ItemList> mItemList;
    private BigDecimal mAmount;
    private BigDecimal mVat;
    private BigDecimal mServiceCharge;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long mMoneyRequestId;
    private String mTitle;
    private String mDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_money_requests, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mNotificationsRecyclerView = (RecyclerView) v.findViewById(R.id.list_notification);
        mProgressDialog = new ProgressDialog(getActivity());

        mNotificationListAdapter = new NotificationListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationsRecyclerView.setAdapter(mNotificationListAdapter);

        // Refresh balance each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getMakePaymentRequests();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNotificationList();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void refreshNotificationList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            if (moneyRequestList != null)
                moneyRequestList.clear();
            getMakePaymentRequests();
        }
    }


    private void getMakePaymentRequests() {
        if (mGetAllNotificationsTask != null) {
            return;
        }

        GetNotificationsRequest mTransactionHistoryRequest = new GetNotificationsRequest(
                pageCount, Constants.SERVICE_ID_REQUEST_INVOICE);
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

        if (result.getApiCommand().equals(Constants.COMMAND_GET_MONEY_REQUESTS)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mGetNotificationsResponse = gson.fromJson(result.getJsonString(), GetNotificationsResponse.class);

                    if (moneyRequestList == null || moneyRequestList.size() == 0) {
                        moneyRequestList = mGetNotificationsResponse.getAllNotifications();
                    } else {
                        List<NotificationClass> tempNotificationList;
                        tempNotificationList = mGetNotificationsResponse.getAllNotifications();
                        moneyRequestList.addAll(tempNotificationList);
                    }

                    hasNext = mGetNotificationsResponse.isHasNext();
                    mNotificationListAdapter.notifyDataSetChanged();

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

        } else if (result.getApiCommand().equals(Constants.COMMAND_REJECT_REQUESTS_MONEY)) {

            try {
                mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                        RequestMoneyAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        refreshNotificationList();
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

        }
    }

    private class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;
        private static final int MONEY_REQUEST_HEADER_VIEW = 5;

        public NotificationListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mDescriptionView;
            private TextView mTitleView;
            private TextView mTimeView;
            private TextView loadMoreTextView;

            private TextView headerView;
            private RoundedImageView mPortrait;

            private LinearLayout optionsLayout;
            private Button acceptButton;
            private Button rejectButton;
            private ProfileImageView mProfileImageView;

            public ViewHolder(final View itemView) {
                super(itemView);

                // Money request list items
                mDescriptionView = (TextView) itemView.findViewById(R.id.description);
                mTimeView = (TextView) itemView.findViewById(R.id.time);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mTitleView = (TextView) itemView.findViewById(R.id.title);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                headerView = (TextView) itemView.findViewById(R.id.money_request_header);

                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                acceptButton = (Button) itemView.findViewById(R.id.accept_button);
                rejectButton = (Button) itemView.findViewById(R.id.reject_button);
            }

            public void bindViewMoneyRequestList(int pos) {
                final NotificationClass moneyRequest = moneyRequestList.get(pos - 1);

                final long id = moneyRequest.getId();
                final String imageUrl = moneyRequest.getOriginatorProfile().getUserProfilePicture();
                final String name = moneyRequest.originatorProfile.getUserName();
                final String mobileNumber = moneyRequest.originatorProfile.getUserMobileNumber();
                final String description = moneyRequest.getDescription();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(moneyRequest.getRequestTime());
                final String title = moneyRequest.getTitle();
                final BigDecimal amount = moneyRequest.getAmount();
                final BigDecimal vat = moneyRequest.getVat();
                final List<ItemList> itemList = moneyRequest.getItemList();

                mDescriptionView.setText(description);
                mTimeView.setText(time);

                if (title != null && !title.equals("")) {
                    mTitleView.setVisibility(View.VISIBLE);
                    mTitleView.setText(title);

                } else mTitleView.setVisibility(View.GONE);

                mProfileImageView.setInformation(Constants.BASE_URL_FTP_SERVER + imageUrl, name);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (optionsLayout.getVisibility() == View.VISIBLE)
                            optionsLayout.setVisibility(View.GONE);
                        else optionsLayout.setVisibility(View.VISIBLE);
                    }
                });

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mMoneyRequestId = id;
                        mAmount = amount;
                        mReceiverName = name;
                        mReceiverMobileNumber = mobileNumber;
                        mPhotoUri = imageUrl;
                        mTitle = title;
                        mDescription = description;
                        mVat = vat;
                        mItemList = itemList;

                        ReviewMakePaymentDialog dialog = new ReviewMakePaymentDialog(getActivity(), mMoneyRequestId, mReceiverMobileNumber,
                                mReceiverName, mPhotoUri, mAmount, mTitle , Constants.SERVICE_ID_REQUEST_MONEY, mVat, mItemList,
                                new ReviewDialogFinishListener() {
                                    @Override
                                    public void onReviewFinish() {
                                        refreshNotificationList();
                                    }
                                });
                        dialog.show();
                    }
                });


                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                });

            }

            public void bindViewHeader() {
                headerView.setText(R.string.invoice_requests_header);
            }

            public void bindViewFooter() {
                if (hasNext)
                    loadMoreTextView.setText(R.string.load_more);
                else
                    loadMoreTextView.setText(R.string.no_more_results);
            }
        }

        public class FooterViewHolder extends ViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasNext) {
                            pageCount = pageCount + 1;
                            getMakePaymentRequests();
                        }
                    }
                });

                TextView loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                if (hasNext)
                    loadMoreTextView.setText(R.string.load_more);
                else
                    loadMoreTextView.setText(R.string.no_more_results);
            }
        }

        private class MoneyRequestHeaderViewHolder extends ViewHolder {
            public MoneyRequestHeaderViewHolder(View itemView) {
                super(itemView);
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
                FooterViewHolder vh = new FooterViewHolder(v);
                return vh;

            } else if (viewType == MONEY_REQUEST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_money_requests_header, parent, false);
                MoneyRequestHeaderViewHolder vh = new MoneyRequestHeaderViewHolder(v);
                return vh;

            } else {
                // MONEY_REQUEST_ITEM_VIEW
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_money_request, parent, false);
                MoneyRequestViewHolder vh = new MoneyRequestViewHolder(v);
                return vh;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof MoneyRequestViewHolder) {
                    MoneyRequestViewHolder vh = (MoneyRequestViewHolder) holder;
                    vh.bindViewMoneyRequestList(position);

                } else if (holder instanceof MoneyRequestHeaderViewHolder) {
                    MoneyRequestHeaderViewHolder vh = (MoneyRequestHeaderViewHolder) holder;
                    vh.bindViewHeader();

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
            if (moneyRequestList == null) {
                return 0;
            } else {
                return 1 + moneyRequestList.size() + 1; // header, money requests list, footer
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (moneyRequestList == null)
                return super.getItemViewType(position);

            if (position == 0)
                return MONEY_REQUEST_HEADER_VIEW;
            else if (position == getItemCount() - 1)
                return FOOTER_VIEW;
            else
                return MONEY_REQUEST_ITEM_VIEW;
        }
    }

}
