package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.RequestMoneyReviewDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ReviewMakePaymentDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ReviewDialogFinishListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.Business;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.ConfirmBusinessInvitationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.ConfirmBusinessInvitationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.GetBusinessListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetMoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetMoneyAndPaymentRequestResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.MoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.Notification;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.BusinessListRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetIntroductionRequestsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroduceActionResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroductionRequestClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class NotificationFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetMoneyAndPaymentRequestTask = null;
    private GetMoneyAndPaymentRequestResponse mGetMoneyAndPaymentRequestResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private HttpRequestPostAsyncTask mRecommendActionTask = null;
    private IntroduceActionResponse mIntroduceActionResponse;

    private HttpRequestGetAsyncTask mGetIntroductionRequestTask = null;
    private GetIntroductionRequestsResponse mIntroductionRequestsResponse;
    
    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;
    
    private HttpRequestPostAsyncTask mRejectPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private HttpRequestGetAsyncTask mGetBusinessInvitationTask = null;
    private GetBusinessListResponse mGetBusinessListResponse;

    private HttpRequestPutAsyncTask mConfirmBusinessInvitationTask = null;
    private ConfirmBusinessInvitationResponse mConfirmBusinessInvitationResponse;

    private RecyclerView mNotificationsRecyclerView;
    private NotificationListAdapter mNotificationListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgressDialog;
    private TextView mEmptyListTextView;

    private List<Notification> mNotifications;
    private List<MoneyAndPaymentRequest> mMoneyAndPaymentRequests;
    private List<IntroductionRequestClass> mIntroductionRequests;
    private List<Business> mBusinessInvitations;

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

    private boolean mSwitchToEmployeeFragment;

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

        mNotificationListAdapter = new NotificationListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationsRecyclerView.setAdapter(mNotificationListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshMoneyAndPaymentRequestList();
                    refreshIntroductionRequestList();
                    refreshBusinessInvitationList();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Utilities.isConnectionAvailable(getActivity())) {
            getMoneyAndPaymentRequest();
            getIntroductionRequestList();
            getBusinessInvitationList();
        }

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

    private void getMoneyAndPaymentRequest() {
        if (mGetMoneyAndPaymentRequestTask != null) {
            return;
        }

        GetMoneyAndPaymentRequest mGetMoneyAndPaymentRequest = new GetMoneyAndPaymentRequest();
        Gson gson = new Gson();
        String json = gson.toJson(mGetMoneyAndPaymentRequest);
        mGetMoneyAndPaymentRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_MONEY_AND_PAYMENT_REQUESTS,
                Constants.BASE_URL_SM + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mGetMoneyAndPaymentRequestTask.mHttpResponseListener = this;
        mGetMoneyAndPaymentRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getIntroductionRequestList() {
        if (mGetIntroductionRequestTask != null) {
            return;
        }

        mGetIntroductionRequestTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_RECOMMENDATION_REQUESTS,
                Constants.BASE_URL_MM + Constants.URL_GET_DOWNSTREAM_NOT_APPROVED_INTRODUCTION_REQUESTS, getActivity());
        mGetIntroductionRequestTask.mHttpResponseListener = this;
        mGetIntroductionRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBusinessInvitationList() {
        if (mGetBusinessInvitationTask != null) {
            return;
        }

        BusinessListRequestBuilder businessListRequestBuilder = new BusinessListRequestBuilder();
        mGetBusinessInvitationTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_LIST,
                businessListRequestBuilder.getPendingBusinessListUri(), getActivity(), this);
        mGetBusinessInvitationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void attemptGetServiceCharge(int serviceId) {

        if (mServiceChargeTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

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
                refreshMoneyAndPaymentRequestList();
            }
        });
        dialog.show();
    }

    private void refreshMoneyAndPaymentRequestList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            mMoneyAndPaymentRequests = null;
            getMoneyAndPaymentRequest();
        }
    }

    private void refreshIntroductionRequestList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            mIntroductionRequests = null;
            getIntroductionRequestList();
        }
    }

    private void refreshBusinessInvitationList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            mBusinessInvitations = null;
            getBusinessInvitationList();
        }
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

    private void rejectRequestMoney(long id) {
        if (mRejectRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_rejecting));
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

        mProgressDialog.setMessage(getString(R.string.progress_dialog_rejecting));
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

    private void acceptBusinessInvitation(long id) {
        mSwitchToEmployeeFragment = true;
        confirmBusinessInvitationRequest(id, Constants.BUSINESS_INVITATION_ACCEPTED, getString(R.string.loading_accepting_invitation));
    }

    private void rejectBusinessInvitation(long id) {
        mSwitchToEmployeeFragment = false;
        confirmBusinessInvitationRequest(id, Constants.BUSINESS_INVITATION_REJECTED, getString(R.string.loading_rejecting_invitation));
    }

    private void markBusinessInvitationAsSpam(long id) {
        mSwitchToEmployeeFragment = false;
        confirmBusinessInvitationRequest(id, Constants.BUSINESS_INVITATION_SPAM, getString(R.string.loading_marking_invitation_as_spam));
    }

    private void confirmBusinessInvitationRequest(long id, String status, String message) {
        if (mConfirmBusinessInvitationTask != null) {
            return;
        }

        mProgressDialog.setMessage(message);
        mProgressDialog.show();

        Gson gson = new Gson();
        ConfirmBusinessInvitationRequest confirmBusinessInvitationRequest = new ConfirmBusinessInvitationRequest(id, status);
        String json = gson.toJson(confirmBusinessInvitationRequest);

        mConfirmBusinessInvitationTask = new HttpRequestPutAsyncTask(Constants.COMMAND_CONFIRM_BUSINESS_INVITATION,
                Constants.BASE_URL_MM + Constants.URL_CONFIRM_BUSINESS_INVITATION, json, getActivity(), this);
        mConfirmBusinessInvitationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mRejectRequestTask = null;
            mRejectPaymentTask = null;
            mGetMoneyAndPaymentRequestTask = null;
            mServiceChargeTask = null;
            mConfirmBusinessInvitationTask = null;
            mGetBusinessInvitationTask = null;
            mGetIntroductionRequestTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_MONEY_AND_PAYMENT_REQUESTS)) {
            mGetMoneyAndPaymentRequestResponse = gson.fromJson(result.getJsonString(), GetMoneyAndPaymentRequestResponse.class);

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    mMoneyAndPaymentRequests = mGetMoneyAndPaymentRequestResponse.getAllMoneyAndPaymentRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mGetMoneyAndPaymentRequestResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            }

            mGetMoneyAndPaymentRequestTask = null;
            postProcessNotificationList();

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_RECOMMENDATION_REQUESTS)) {
            try {
                mIntroductionRequestsResponse = gson.fromJson(result.getJsonString(), GetIntroductionRequestsResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mIntroductionRequests = mIntroductionRequestsResponse.getVerificationRequestList();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mIntroductionRequestsResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }

            mGetIntroductionRequestTask = null;
            postProcessNotificationList();

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_LIST)) {
            try {
                mGetBusinessListResponse = gson.fromJson(result.getJsonString(), GetBusinessListResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mBusinessInvitations = mGetBusinessListResponse.getBusinessList();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mGetBusinessListResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }

            mGetBusinessInvitationTask = null;
            postProcessNotificationList();

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
        } else if (result.getApiCommand().equals(Constants.COMMAND_INTRODUCE_ACTION)) {

            try {
                mIntroduceActionResponse = gson.fromJson(result.getJsonString(), IntroduceActionResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mIntroduceActionResponse.getMessage(), Toast.LENGTH_LONG).show();

                    // Refresh recommendation requests list
                    if (mIntroductionRequests != null)
                        mIntroductionRequests.clear();
                    mIntroductionRequests = null;
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
        } else if (result.getApiCommand().equals(Constants.COMMAND_CONFIRM_BUSINESS_INVITATION)) {
            try {
                mConfirmBusinessInvitationResponse = gson.fromJson(result.getJsonString(), ConfirmBusinessInvitationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (mSwitchToEmployeeFragment) {
                        mSwitchToEmployeeFragment = false;
                    }

                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mConfirmBusinessInvitationResponse.getMessage(), Toast.LENGTH_LONG).show();
                 } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mConfirmBusinessInvitationResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

                refreshBusinessInvitationList();

            } catch (Exception e) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_confirming_business_invitation, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mConfirmBusinessInvitationTask = null;
        }
        else if (result.getApiCommand().equals(Constants.COMMAND_REJECT_REQUESTS_MONEY)) {

            try {
                mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                        RequestMoneyAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        refreshMoneyAndPaymentRequestList();
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
                        refreshMoneyAndPaymentRequestList();
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
    }

    private void postProcessNotificationList() {
        Log.d("Notification", "Trying to post process...");
        if (isAllNotificationsLoaded()) {
            Log.d("Notification", "Merging and refreshing list");

            mNotifications = mergeNotificationLists();

            if (mNotifications.isEmpty()) {
                mEmptyListTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyListTextView.setVisibility(View.GONE);
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mNotificationListAdapter.notifyDataSetChanged();

            if (isAdded())
                setContentShown(true);
        }
    }

    private boolean isAllNotificationsLoaded() {
        return mGetMoneyAndPaymentRequestTask == null && mGetIntroductionRequestTask == null && mGetBusinessInvitationTask == null;
    }

    private List<Notification> mergeNotificationLists() {
        List<Notification> notifications = new ArrayList<>();
        if (mMoneyAndPaymentRequests != null)
            notifications.addAll(mMoneyAndPaymentRequests);
        if (mIntroductionRequests != null)
            notifications.addAll(mIntroductionRequests);
        if (mBusinessInvitations != null)
            notifications.addAll(mBusinessInvitations);

        // Date wise sort all notifications
        Collections.sort(notifications, new Comparator<Notification>() {
            @Override
            public int compare(Notification lhs, Notification rhs) {
                if (lhs.getTime() > rhs.getTime())
                    return -1;
                else if (lhs.getTime() < rhs.getTime())
                    return 1;
                else
                    return 0;
            }
        });

        return notifications;
    }

    private class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

        public class NotificationViewHolder extends RecyclerView.ViewHolder {
            private TextView mTitleView;
            private TextView mDescriptionView;
            private TextView mTimeView;
            private LinearLayout optionsLayout;
            private ProfileImageView mProfileImageView;

            public NotificationViewHolder(final View itemView) {
                super(itemView);

                mTitleView = (TextView) itemView.findViewById(R.id.textview_title);
                mDescriptionView = (TextView) itemView.findViewById(R.id.textview_description);
                mTimeView = (TextView) itemView.findViewById(R.id.textview_time);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
            }

            public void bindView(int pos) {
                if (mNotifications.size() == 1) {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_upper_round_white));

                } else if (pos == 0) {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_upper_round_white));

                } else if (pos == mNotifications.size() - 1) {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_half_lower_round_white));

                } else {
                    itemView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_no_round_white));
                }

                Notification notification = mNotifications.get(pos);

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + notification.getImageUrl(), false);

                mDescriptionView.setText(notification.getDescription());
                mTimeView.setText(new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(notification.getTime()));

                if (notification.getTitle() != null && !notification.getTitle().equals("")) {
                    mTitleView.setVisibility(View.VISIBLE);
                    mTitleView.setText(notification.getTitle());

                } else {
                    mTitleView.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (optionsLayout.getVisibility() == View.VISIBLE) {
                            optionsLayout.setVisibility(View.GONE);
                        }
                        else {
                            optionsLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
                optionsLayout.setVisibility(View.GONE);

            }
        }

        public class MoneyAndPaymentRequestViewHolder extends NotificationViewHolder {

            private Button acceptButton;
            private Button rejectButton;

            public MoneyAndPaymentRequestViewHolder(final View itemView) {
                super(itemView);

                acceptButton = (Button) itemView.findViewById(R.id.accept_button);
                rejectButton = (Button) itemView.findViewById(R.id.reject_button);
            }

            @Override
            public void bindView(int pos) {
                super.bindView(pos);

                MoneyAndPaymentRequest moneyAndPaymentRequest = (MoneyAndPaymentRequest) mNotifications.get(pos);

                final String imageUrl = moneyAndPaymentRequest.getOriginatorProfile().getUserProfilePicture();
                final String name = moneyAndPaymentRequest.originatorProfile.getUserName();
                final String mobileNumber = moneyAndPaymentRequest.originatorProfile.getUserMobileNumber();
                final String description = moneyAndPaymentRequest.getDescription();
                final String title = moneyAndPaymentRequest.getTitle();
                final long id = moneyAndPaymentRequest.getId();
                final BigDecimal amount = moneyAndPaymentRequest.getAmount();
                final int serviceID = moneyAndPaymentRequest.getServiceID();
                final BigDecimal vat = moneyAndPaymentRequest.getVat();
                final List<ItemList> itemList = moneyAndPaymentRequest.getItemList();

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
                        else {
                            ReviewMakePaymentDialog dialog = new ReviewMakePaymentDialog(getActivity(), mMoneyRequestId, mReceiverMobileNumber,
                                    mReceiverName, mPhotoUri, mAmount, mTitle, Constants.SERVICE_ID_REQUEST_MONEY, mVat, mItemList,
                                    new ReviewDialogFinishListener() {
                                        @Override
                                        public void onReviewFinish() {
                                            refreshMoneyAndPaymentRequestList();
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

            }
        }

        public class IntroductionRequestViewHolder extends NotificationViewHolder {

            private Button verifyButton;
            private Button rejectRecommendationButton;
            private Button markAsSpamRecommendationButton;

            public IntroductionRequestViewHolder(final View itemView) {
                super(itemView);

                verifyButton = (Button) itemView.findViewById(R.id.verify_button);
                rejectRecommendationButton = (Button) itemView.findViewById(R.id.reject_button);
                markAsSpamRecommendationButton = (Button) itemView.findViewById(R.id.mark_as_spam_button);
            }

            @Override
            public void bindView(int pos) {
                super.bindView(pos);

                final IntroductionRequestClass introductionRequest = (IntroductionRequestClass) mNotifications.get(pos);

                final long requestID = introductionRequest.getId();
                final String recommendationStatus = introductionRequest.getStatus();

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
                                    .setNegativeButton(android.R.string.no, null)
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
                                    .setNegativeButton(android.R.string.no, null)
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
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                    }
                });
            }
        }

        public class BusinessInvitationViewHolder extends NotificationViewHolder {

            private Button mAcceptButton;
            private Button mRejectButton;
            private Button mMarkSpamButton;

            public BusinessInvitationViewHolder(View itemView) {
                super(itemView);

                mAcceptButton = (Button) itemView.findViewById(R.id.button_accept);
                mRejectButton = (Button) itemView.findViewById(R.id.button_reject);
                mMarkSpamButton = (Button) itemView.findViewById(R.id.button_mark_spam);
            }

            @Override
            public void bindView(int pos) {
                super.bindView(pos);

                final Business businessInvitation = mBusinessInvitations.get(pos);

                mAcceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptBusinessInvitation(businessInvitation.getAssociationId());
                    }
                });

                mRejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectBusinessInvitation(businessInvitation.getAssociationId());
                    }
                });

                mMarkSpamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        markBusinessInvitationAsSpam(businessInvitation.getAssociationId());
                    }
                });
            }
        }

        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == Constants.NOTIFICATION_TYPE_INTROUDCTION_REQUEST) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduction_requests, parent, false);
                IntroductionRequestViewHolder vh = new IntroductionRequestViewHolder(v);
                return vh;

            } else if (viewType == Constants.NOTIFICATION_TYPE_BUSINESS_ACCOUNT_INVITE) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_business_invitation, parent, false);
                BusinessInvitationViewHolder vh = new BusinessInvitationViewHolder(v);
                return vh;
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_money_and_make_payment_request, parent, false);
                MoneyAndPaymentRequestViewHolder vh = new MoneyAndPaymentRequestViewHolder(v);
                return vh;
            }
        }

        @Override
        public void onBindViewHolder(NotificationViewHolder holder, int position) {
            try {
                holder.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mNotifications == null)
                return 0;
            else
                return mNotifications.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mNotifications.get(position).getNotificationType();
        }
    }
}
