package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Customview.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.Customview.Dialogs.RequestMoneyReviewDialog;
import bd.com.ipay.ipayskeleton.Customview.Dialogs.ReviewMakePaymentDialog;
import bd.com.ipay.ipayskeleton.Customview.Dialogs.ReviewDialogFinishListener;
import bd.com.ipay.ipayskeleton.Customview.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetNotificationsRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetNotificationsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.NotificationClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetIntroductionRequestsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroduceActionResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroductionRequestClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class NotificationFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetAllNotificationsTask = null;
    private GetNotificationsResponse mGetNotificationsResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private HttpRequestPostAsyncTask mRecommendActionTask = null;
    private IntroduceActionResponse mIntroduceActionResponse;

    private HttpRequestGetAsyncTask mGetRecommendationRequestsTask = null;
    private GetIntroductionRequestsResponse mRecommendationRequestsResponse;
    
    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;
    
    private HttpRequestPostAsyncTask mRejectPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private RecyclerView mNotificationsRecyclerView;
    private NotificationAndRecommendationListAdapter mNotificationAndRecommendationListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<NotificationClass> moneyRequestList;
    private List<IntroductionRequestClass> mRecommendationRequestList;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgressDialog;
    private TextView mEmptyListTextView;

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
    private int mServiceID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mNotificationsRecyclerView = (RecyclerView) v.findViewById(R.id.list_notification);
        mProgressDialog = new ProgressDialog(getActivity());
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);

        mNotificationAndRecommendationListAdapter = new NotificationAndRecommendationListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationsRecyclerView.setAdapter(mNotificationAndRecommendationListAdapter);

        // Refresh balance each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getNotifications();
            getRecommendationRequestsList();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshNotificationList();
                    refreshIntroductionRequestList();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    private void getNotifications() {
        if (mGetAllNotificationsTask != null) {
            return;
        }

        GetNotificationsRequest mTransactionHistoryRequest = new GetNotificationsRequest(pageCount);
        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mGetAllNotificationsTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_NOTIFICATIONS,
                Constants.BASE_URL_SM + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mGetAllNotificationsTask.mHttpResponseListener = this;
        mGetAllNotificationsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    protected void attemptGetServiceCharge(int serviceId) {

        if (mServiceChargeTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, getActivity().MODE_PRIVATE);

        int accountType = pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
        int accountClass = Constants.DEFAULT_USER_CLASS;

        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(serviceId, accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL_SM + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showReviewDialog() {
        RequestMoneyReviewDialog dialog = new RequestMoneyReviewDialog(getActivity(), mMoneyRequestId, mReceiverMobileNumber,
                mReceiverName, mPhotoUri, mAmount, mServiceCharge, mTitle, mDescription, mServiceID, new ReviewDialogFinishListener() {
            @Override
            public void onReviewFinish() {
                refreshNotificationList();
            }
        });
        dialog.show();
    }

    private void refreshNotificationList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            getNotifications();
        }
    }

    private void refreshIntroductionRequestList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            getRecommendationRequestsList();
        }
    }

    private void attemptSetRecommendationStatus(long requestID, String recommendationStatus) {
        if (requestID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.verifying_user));
        mProgressDialog.show();
        mRecommendActionTask = new HttpRequestPostAsyncTask(Constants.COMMAND_RECOMMEND_ACTION,
                Constants.BASE_URL_MM + Constants.URL_INTRODUCE_ACTION + requestID + "/" + recommendationStatus, null, getActivity());
        mRecommendActionTask.mHttpResponseListener = this;
        mRecommendActionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                Constants.BASE_URL_SM + Constants.URL_REJECT_NOTIFICATION_REQUEST, json, getActivity());
        mRejectRequestTask.mHttpResponseListener = this;
        mRejectRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void rejectPaymentRequest(long id) {

        if (mRejectPaymentTask != null) {
            return;
        }

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_rejecting));
        mProgressDialog.show();
        PaymentAcceptRejectOrCancelRequest mPaymentAcceptRejectOrCancelRequest =
                new PaymentAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentAcceptRejectOrCancelRequest);
        mRejectPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REJECT_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_CANCEL_NOTIFICATION_REQUEST, json, getActivity());
        mRejectPaymentTask.mHttpResponseListener = this;
        mRejectPaymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null) {
            mRejectRequestTask = null;
            mRejectPaymentTask = null;
            mGetAllNotificationsTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_NOTIFICATIONS)) {
            if (this.isAdded()) setContentShown(true);
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    mGetNotificationsResponse = gson.fromJson(result.getJsonString(), GetNotificationsResponse.class);

                    if (moneyRequestList != null)
                        moneyRequestList.clear();

                    if (moneyRequestList == null || moneyRequestList.size() == 0) {
                        moneyRequestList = mGetNotificationsResponse.getAllNotifications();
                    } else {
                        List<NotificationClass> tempNotificationList;
                        tempNotificationList = mGetNotificationsResponse.getAllNotifications();
                        moneyRequestList.addAll(tempNotificationList);
                    }

                    hasNext = mGetNotificationsResponse.isHasNext();
                    mNotificationAndRecommendationListAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mGetAllNotificationsTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_SERVICE_CHARGE)) {
            mProgressDialog.dismiss();
            try {
                mGetServiceChargeResponse = gson.fromJson(result.getJsonString(), GetServiceChargeResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (mGetServiceChargeResponse != null) {
                        mServiceCharge = mGetServiceChargeResponse.getServiceCharge(mAmount);

                        if (mServiceCharge.compareTo(BigDecimal.ZERO) < 0) {
                            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                        } else {
                            showReviewDialog();
                        }

                    } else {
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }

            mServiceChargeTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_RECOMMENDATION_REQUESTS)) {

            if (this.isAdded()) setContentShown(true);
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mRecommendationRequestsResponse = gson.fromJson(result.getJsonString(), GetIntroductionRequestsResponse.class);

                    if (mRecommendationRequestList != null)
                        mRecommendationRequestList.clear();

                    if (mRecommendationRequestList == null) {
                        mRecommendationRequestList = mRecommendationRequestsResponse.getVerificationRequestList();
                    } else {
                        List<IntroductionRequestClass> tempRecommendationRequestsClasses;
                        tempRecommendationRequestsClasses = mRecommendationRequestsResponse.getVerificationRequestList();
                        mRecommendationRequestList.clear();
                        mRecommendationRequestList.addAll(tempRecommendationRequestsClasses);
                    }

                    if (mRecommendationRequestList != null && mRecommendationRequestList.size() > 0)
                        mNotificationAndRecommendationListAdapter.notifyDataSetChanged();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }

            mProgressDialog.dismiss();
            mGetRecommendationRequestsTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_RECOMMEND_ACTION)) {

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
                    Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mRecommendActionTask = null;
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

        } else if (result.getApiCommand().equals(Constants.COMMAND_REJECT_PAYMENT_REQUEST)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    mPaymentAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                            PaymentAcceptRejectOrCancelResponse.class);
                    String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        refreshNotificationList();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mRejectPaymentTask = null;

        }
        if (moneyRequestList != null && moneyRequestList.size() == 0 && mRecommendationRequestList != null && mRecommendationRequestList.size() == 0 ) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private class NotificationAndRecommendationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int RECOMMENDATION_ITEM_VIEW = 2;
        private static final int RECOMMENDATION_HEADER_VIEW = 3;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;
        private static final int MONEY_REQUEST_HEADER_VIEW = 5;

        public NotificationAndRecommendationListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mDescriptionView;
            private TextView mTitleView;
            private TextView mTimeView;
            private TextView loadMoreTextView;
            private LinearLayout optionsLayout;
            private Button acceptButton;
            private Button rejectButton;
            private Button markAsSpamButton;
            private View viewBetweenRejectAndSpam;

            private TextView mSenderName;
            private TextView mSenderMobileNumber;
            private ImageView mRecommendationStatus;
            private TextView mDate;
            private Button verifyButton;
            private Button rejectRecommendationButton;
            private Button markAsSpamRecommendationButton;
            private ProfileImageView mProfileImageView;

            public ViewHolder(final View itemView) {
                super(itemView);

                // Money request list items
                mDescriptionView = (TextView) itemView.findViewById(R.id.description);
                mTimeView = (TextView) itemView.findViewById(R.id.time);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mTitleView = (TextView) itemView.findViewById(R.id.title);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                acceptButton = (Button) itemView.findViewById(R.id.accept_button);
                rejectButton = (Button) itemView.findViewById(R.id.reject_button);
                markAsSpamButton = (Button) itemView.findViewById(R.id.mark_as_spam_button);
                viewBetweenRejectAndSpam = (View) itemView.findViewById(R.id.view_2);

                // Recommendation list items
                mSenderName = (TextView) itemView.findViewById(R.id.sender_name);
                mSenderMobileNumber = (TextView) itemView.findViewById(R.id.sender_mobile_number);
                mRecommendationStatus = (ImageView) itemView.findViewById(R.id.recommendation_status);
                mDate = (TextView) itemView.findViewById(R.id.request_date);
                verifyButton = (Button) itemView.findViewById(R.id.verify_button);
                rejectRecommendationButton = (Button) itemView.findViewById(R.id.reject_button);
                markAsSpamRecommendationButton = (Button) itemView.findViewById(R.id.mark_as_spam_button);
            }

            public void bindViewMoneyRequestList(int pos) {

                if (mRecommendationRequestList == null) pos = pos - 1;
                else {
                    if (mRecommendationRequestList.size() == 0) pos = pos - 1;
                    else pos = pos - mRecommendationRequestList.size() - 2;
                }

                final String imageUrl = moneyRequestList.get(pos).getOriginatorProfile().getUserProfilePicture();
                final String name = moneyRequestList.get(pos).originatorProfile.getUserName();
                final String mobileNumber = moneyRequestList.get(pos).originatorProfile.getUserMobileNumber();
                final String description = moneyRequestList.get(pos).getDescription();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(moneyRequestList.get(pos).getRequestTime());
                final String title = moneyRequestList.get(pos).getTitle();
                final long id = moneyRequestList.get(pos).getId();
                final BigDecimal amount = moneyRequestList.get(pos).getAmount();
                final int serviceID = moneyRequestList.get(pos).getServiceID();
                final BigDecimal vat = moneyRequestList.get(pos).getVat();
                final List<ItemList> itemList = moneyRequestList.get(pos).getItemList();

                mProfileImageView.setInformation(imageUrl, name);

                mDescriptionView.setText(description);
                mTimeView.setText(time);

                if (title != null && !title.equals("")) {
                    mTitleView.setVisibility(View.VISIBLE);
                    mTitleView.setText(title);

                } else mTitleView.setVisibility(View.GONE);

                if (serviceID == Constants.SERVICE_ID_RECOMMENDATION_REQUEST) {
                    viewBetweenRejectAndSpam.setVisibility(View.VISIBLE);
                    markAsSpamButton.setVisibility(View.VISIBLE);
                }

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
                        mServiceID = serviceID;
                        mVat = vat;
                        mItemList = itemList;

                        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY)
                            attemptGetServiceCharge(Constants.SERVICE_ID_SEND_MONEY);
                        else
                        {
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
                                if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY)
                                    rejectRequestMoney(id);
                                else
                                    rejectPaymentRequest(id);
                            }
                        });
                        rejectDialog.show();
                    }
                });

                markAsSpamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO
                    }
                });

            }

            public void bindViewRecommendationList(int pos) {

                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                final long requestID = mRecommendationRequestList.get(pos).getId();
                final String senderName = mRecommendationRequestList.get(pos).getSenderName();
                final String senderMobileNumber = mRecommendationRequestList.get(pos).getSenderMobileNumber();
                final String recommendationStatus = mRecommendationRequestList.get(pos).getStatus();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(mRecommendationRequestList.get(pos).getDate());

                mSenderName.setText(senderName);
                mSenderMobileNumber.setText(senderMobileNumber);
                mDate.setText(time);

                if (recommendationStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_PENDING)) {
                    mRecommendationStatus.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                } else if (recommendationStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_APPROVED)) {
                    mRecommendationStatus.setImageResource(R.drawable.ic_verified_user_black_24dp);
                } else if (recommendationStatus.equals(Constants.INTRODUCTION_REQUEST_STATUS_SPAM)) {
                    mRecommendationStatus.setImageResource(R.drawable.ic_error_black_24dp);
                } else {
                    // INTRODUCTION_REQUEST_STATUS_REJECTED
                    mRecommendationStatus.setImageResource(R.drawable.ic_warning_black_24dp);
                }

                optionsLayout.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.INTRODUCTION_REQUEST_STATUS_PENDING)) {
                            if (optionsLayout.getVisibility() == View.VISIBLE)
                                optionsLayout.setVisibility(View.GONE);
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

            public void bindViewFooter(int pos) {
                if (hasNext) loadMoreTextView.setText(R.string.load_more);
                else loadMoreTextView.setText(R.string.no_more_results);
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
                            getNotifications();
                        }
                    }
                });

                TextView loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                if (hasNext) loadMoreTextView.setText(R.string.load_more);
                else loadMoreTextView.setText(R.string.no_more_results);
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

            } else if (viewType == RECOMMENDATION_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recommendation_requests, parent, false);
                RecommendationRequestViewHolder vh = new RecommendationRequestViewHolder(v);
                return vh;

            } else if (viewType == RECOMMENDATION_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recommendation_requests_header, parent, false);
                RecommendationListHeaderViewHolder vh = new RecommendationListHeaderViewHolder(v);
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

                } else if (holder instanceof RecommendationListHeaderViewHolder) {
                    RecommendationListHeaderViewHolder vh = (RecommendationListHeaderViewHolder) holder;

                } else if (holder instanceof RecommendationRequestViewHolder) {
                    RecommendationRequestViewHolder vh = (RecommendationRequestViewHolder) holder;
                    vh.bindViewRecommendationList(position);

                } else if (holder instanceof MoneyRequestHeaderViewHolder) {
                    MoneyRequestHeaderViewHolder vh = (MoneyRequestHeaderViewHolder) holder;

                } else if (holder instanceof FooterViewHolder) {
                    FooterViewHolder vh = (FooterViewHolder) holder;
                    vh.bindViewFooter(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {

            int moneyRequestListSize = 0;
            int recommendationRequestListSize = 0;

            if (moneyRequestList == null && mRecommendationRequestList == null) return 0;

            if (moneyRequestList != null)
                moneyRequestListSize = moneyRequestList.size();
            if (mRecommendationRequestList != null)
                recommendationRequestListSize = mRecommendationRequestList.size();

            if (moneyRequestListSize > 0 && recommendationRequestListSize > 0)
                return 1 + moneyRequestListSize + 1 + recommendationRequestListSize + 1;   // recommendation header, recommendation requests, money request header , money requests, footer
            else if (moneyRequestListSize > 0 && recommendationRequestListSize == 0)
                return 1 + moneyRequestListSize + 1;                                       // money request header, money requests, footer
            else if (moneyRequestListSize == 0 && recommendationRequestListSize > 0)
                return 1 + recommendationRequestListSize;                                  // recommendation header , recommendation requests list
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {

            int moneyRequestListSize = 0;
            int recommendationRequestListSize = 0;

            if (moneyRequestList == null && mRecommendationRequestList == null)
                return super.getItemViewType(position);

            if (moneyRequestList != null)
                moneyRequestListSize = moneyRequestList.size();
            if (mRecommendationRequestList != null)
                recommendationRequestListSize = mRecommendationRequestList.size();

            if (moneyRequestListSize > 0 && recommendationRequestListSize > 0) {
                if (position == 0) return RECOMMENDATION_HEADER_VIEW;
                else if (position == recommendationRequestListSize + 1)
                    return MONEY_REQUEST_HEADER_VIEW;
                else if (position == moneyRequestListSize + 1 + recommendationRequestListSize + 1)
                    return FOOTER_VIEW;
                else if (position > recommendationRequestListSize + 1)
                    return MONEY_REQUEST_ITEM_VIEW;
                else return RECOMMENDATION_ITEM_VIEW;

            } else if (moneyRequestListSize > 0 && recommendationRequestListSize == 0) {
                if (position == 0) return MONEY_REQUEST_HEADER_VIEW;
                else if (position == moneyRequestListSize + 1) return FOOTER_VIEW;
                else return MONEY_REQUEST_ITEM_VIEW;

            } else if (moneyRequestListSize == 0 && recommendationRequestListSize > 0) {
                if (position == 0) return RECOMMENDATION_HEADER_VIEW;
                else return RECOMMENDATION_ITEM_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}
