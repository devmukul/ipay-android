package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.NotificationActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.Business;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.GetBusinessListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetMoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetMoneyAndPaymentRequestResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.MoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.Notification;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.BusinessListRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetIntroductionRequestsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroductionRequestClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class NotificationFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetMoneyAndPaymentRequestTask = null;
    private GetMoneyAndPaymentRequestResponse mGetMoneyAndPaymentRequestResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private HttpRequestGetAsyncTask mGetIntroductionRequestTask = null;
    private GetIntroductionRequestsResponse mIntroductionRequestsResponse;

    private HttpRequestGetAsyncTask mGetBusinessInvitationTask = null;
    private GetBusinessListResponse mGetBusinessListResponse;


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
    private String mDescriptionofRequest;

    private OnNotificationUpdateListener mOnNotificationUpdateListener;

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

    public void onResume() {
        super.onResume();
        if (Utilities.isConnectionAvailable(getActivity())) {
            refreshBusinessInvitationList();
            refreshIntroductionRequestList();
            refreshMoneyAndPaymentRequestList();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // We try to fetch all notification lists as soon as the home activity
        // is launched. So when the user navigates to this fragment, it might be possible that
        // all the lists have already been loaded. We have to call postProcessNotificationList
        // to properly update the view.
        postProcessNotificationList();
    }

    public void setOnNotificationUpdateListener(OnNotificationUpdateListener listener) {
        this.mOnNotificationUpdateListener = listener;
    }

    public void getNotificationLists(Context context) {
        getMoneyAndPaymentRequest(context);
        getIntroductionRequestList(context);
        getBusinessInvitationList(context);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    private void getMoneyAndPaymentRequest(Context context) {
        if (mGetMoneyAndPaymentRequestTask != null) {
            return;
        }

        GetMoneyAndPaymentRequest mGetMoneyAndPaymentRequest = new GetMoneyAndPaymentRequest();
        Gson gson = new Gson();
        String json = gson.toJson(mGetMoneyAndPaymentRequest);
        mGetMoneyAndPaymentRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_MONEY_AND_PAYMENT_REQUESTS,
                Constants.BASE_URL_SM + Constants.URL_GET_All_NOTIFICATIONS, json, context, this);
        mGetMoneyAndPaymentRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getIntroductionRequestList(Context context) {
        if (mGetIntroductionRequestTask != null) {
            return;
        }

        mGetIntroductionRequestTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_RECOMMENDATION_REQUESTS,
                Constants.BASE_URL_MM + Constants.URL_GET_DOWNSTREAM_NOT_APPROVED_INTRODUCTION_REQUESTS, context, this);
        mGetIntroductionRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBusinessInvitationList(Context context) {
        if (mGetBusinessInvitationTask != null) {
            return;
        }

        BusinessListRequestBuilder businessListRequestBuilder = new BusinessListRequestBuilder();
        mGetBusinessInvitationTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_LIST,
                businessListRequestBuilder.getPendingBusinessListUri(), context, this);
        mGetBusinessInvitationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptGetServiceCharge(int serviceId) {

        if (mServiceChargeTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        int accountType = ProfileInfoCacheManager.getAccountType();
        int accountClass = Constants.DEFAULT_USER_CLASS;

        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(serviceId, accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL_SM + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void refreshMoneyAndPaymentRequestList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            mMoneyAndPaymentRequests = null;
            getMoneyAndPaymentRequest(getActivity());
        }
    }

    private void refreshIntroductionRequestList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            mIntroductionRequests = null;
            getIntroductionRequestList(getActivity());
        }
    }

    private void refreshBusinessInvitationList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            mBusinessInvitations = null;
            getBusinessInvitationList(getActivity());
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

    private void postProcessNotificationList() {
        if (isAllNotificationsLoaded()) {

            mNotifications = mergeNotificationLists();
            if (isAdded()) {
                if (mNotifications.isEmpty()) {
                    mEmptyListTextView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyListTextView.setVisibility(View.GONE);
                }

                mSwipeRefreshLayout.setRefreshing(false);
                mNotificationListAdapter.notifyDataSetChanged();
                setContentShown(true);
            }

            // We just can't call something like getActivity().onNotificationUpdate.. because
            // getActivity() might return if user hasn't yet navigated to the notification fragment.
            if (mOnNotificationUpdateListener != null && mNotifications != null)
                mOnNotificationUpdateListener.onNotificationUpdate(mNotifications);
        }
    }

    private void launchInvoiceHistoryFragment() {

        Bundle bundle = new Bundle();
        bundle.putLong(Constants.MONEY_REQUEST_ID, mMoneyRequestId);
        bundle.putString(Constants.MOBILE_NUMBER, mReceiverMobileNumber);
        bundle.putString(Constants.NAME, mReceiverName);
        bundle.putInt(Constants.MONEY_REQUEST_SERVICE_ID, Constants.SERVICE_ID_REQUEST_MONEY);
        bundle.putString(Constants.VAT, mVat.toString());
        bundle.putString(Constants.PHOTO_URI, mPhotoUri);
        bundle.putString(Constants.AMOUNT, mAmount.toString());
        bundle.putString(Constants.TITLE, mTitle);
        bundle.putString(Constants.DESCRIPTION, mDescriptionofRequest);
        bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, new ArrayList<>(mItemList));
        bundle.putString(Constants.TAG, Constants.INVOICE);

        Intent intent = new Intent(this.getContext(), NotificationActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void launchReceivedRequestFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_RECEIVED_REQUEST);
        bundle.putSerializable(Constants.AMOUNT, mAmount);
        bundle.putString(Constants.INVOICE_RECEIVER_TAG, ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
        bundle.putString(Constants.INVOICE_DESCRIPTION_TAG, mDescriptionofRequest);
        bundle.putString(Constants.INVOICE_TITLE_TAG, mTitle);
        bundle.putLong(Constants.MONEY_REQUEST_ID, mMoneyRequestId);
        bundle.putString(Constants.NAME, mReceiverName);
        bundle.putString(Constants.PHOTO_URI, mPhotoUri);
        bundle.putString(Constants.TAG, Constants.REQUEST);

        Intent intent = new Intent(this.getContext(), NotificationActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public interface OnNotificationUpdateListener {
        void onNotificationUpdate(List<Notification> notifications);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetMoneyAndPaymentRequestTask = null;
            mServiceChargeTask = null;
            mGetBusinessInvitationTask = null;
            mGetIntroductionRequestTask = null;

            if (isAdded()) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            }

            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_MONEY_AND_PAYMENT_REQUESTS:
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mGetMoneyAndPaymentRequestTask = null;
                postProcessNotificationList();

                break;
            case Constants.COMMAND_GET_RECOMMENDATION_REQUESTS:
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

                break;
            case Constants.COMMAND_GET_BUSINESS_LIST:
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

                break;
            case Constants.COMMAND_GET_SERVICE_CHARGE:
                mProgressDialog.dismiss();
                try {
                    mGetServiceChargeResponse = gson.fromJson(result.getJsonString(), GetServiceChargeResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (mGetServiceChargeResponse != null) {
                            mServiceCharge = mGetServiceChargeResponse.getServiceCharge(mAmount);

                            if (mServiceCharge.compareTo(BigDecimal.ZERO) < 0) {
                                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                            } else {
                                launchReceivedRequestFragment();
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
                break;
        }
    }

    private class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

        public class NotificationViewHolder extends RecyclerView.ViewHolder {
            private final TextView mTitleView;
            private final TextView mNameView;
            private final TextView mTimeView;
            private final ProfileImageView mProfileImageView;

            public NotificationViewHolder(final View itemView) {
                super(itemView);

                mTitleView = (TextView) itemView.findViewById(R.id.textview_title);
                mNameView = (TextView) itemView.findViewById(R.id.textview_description);
                mTimeView = (TextView) itemView.findViewById(R.id.textview_time);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
            }

            public void bindView(int pos) {

                Notification notification = mNotifications.get(pos);

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + notification.getImageUrl(), false);

                mNameView.setText(notification.getName());
                mTimeView.setText(Utilities.getDateFormat(notification.getTime()));

                if (notification.getNotificationTitle() != null && !notification.getNotificationTitle().equals("")) {
                    mTitleView.setVisibility(View.VISIBLE);
                    mTitleView.setText(notification.getNotificationTitle());

                } else {
                    mTitleView.setVisibility(View.GONE);
                }

            }
        }

        public class MoneyAndPaymentRequestViewHolder extends NotificationViewHolder {
            private final TextView mAmountView;

            public MoneyAndPaymentRequestViewHolder(final View itemView) {

                super(itemView);
                mAmountView = (TextView) itemView.findViewById(R.id.textview_amount);

            }

            @Override
            public void bindView(int pos) {
                super.bindView(pos);

                MoneyAndPaymentRequest moneyAndPaymentRequest = (MoneyAndPaymentRequest) mNotifications.get(pos);

                final String imageUrl = moneyAndPaymentRequest.getOriginatorProfile().getUserProfilePicture();
                final String name = moneyAndPaymentRequest.originatorProfile.getUserName();
                final String mobileNumber = moneyAndPaymentRequest.originatorProfile.getUserMobileNumber();
                final String descriptionofRequest = moneyAndPaymentRequest.getDescriptionofRequest();
                final String title = moneyAndPaymentRequest.getTitle();
                final long id = moneyAndPaymentRequest.getId();
                final BigDecimal amount = moneyAndPaymentRequest.getAmount();
                final int serviceID = moneyAndPaymentRequest.getServiceID();
                final BigDecimal vat = moneyAndPaymentRequest.getVat();
                final List<ItemList> itemList = moneyAndPaymentRequest.getItemList();

                mAmountView.setText(Utilities.formatTaka(amount));


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mMoneyRequestId = id;
                        mAmount = amount;
                        mReceiverName = name;
                        mReceiverMobileNumber = mobileNumber;
                        mPhotoUri = Constants.BASE_URL_FTP_SERVER + imageUrl;
                        mTitle = title;
                        mDescriptionofRequest = descriptionofRequest;
                        mVat = vat;
                        mItemList = itemList;

                        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY)
                            attemptGetServiceCharge(Constants.SERVICE_ID_SEND_MONEY);
                        else {
                            launchInvoiceHistoryFragment();
                        }
                    }
                });

            }
        }

        public class IntroductionRequestViewHolder extends NotificationViewHolder {

            public IntroductionRequestViewHolder(final View itemView) {
                super(itemView);

            }

            @Override
            public void bindView(int pos) {
                super.bindView(pos);

                final IntroductionRequestClass introductionRequest = (IntroductionRequestClass) mNotifications.get(pos);

                final long requestID = introductionRequest.getId();

                final String senderName = introductionRequest.getName();
                final String senderMobileNumber = introductionRequest.getSenderMobileNumber();
                final String photoUri = introductionRequest.getImageUrl();

                final AddressClass mAddress = introductionRequest.getPresentAddress();
                final String fathersName = introductionRequest.getFather();
                final String mothersName = introductionRequest.getMother();


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(Constants.REQUEST_ID, requestID);
                        bundle.putString(Constants.NAME, senderName);
                        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + photoUri);
                        bundle.putString(Constants.MOBILE_NUMBER, senderMobileNumber);
                        bundle.putString(Constants.FATHERS_NAME, fathersName);
                        bundle.putString(Constants.MOTHERS_NAME, mothersName);
                        bundle.putSerializable(Constants.ADDRESS, mAddress);
                        bundle.putString(Constants.TAG, Constants.RECOMMENDATION);

                        Intent intent = new Intent(getActivity(), NotificationActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

        }

        public class BusinessInvitationViewHolder extends NotificationViewHolder {

            private List<String> mReceivedRequestActionList;
            private CustomSelectorDialog mCustomSelectorDialog;

            public BusinessInvitationViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void bindView(int pos) {
                super.bindView(pos);
                final Business businessInvitation = (Business) mNotifications.get(pos);


                final String senderName = businessInvitation.getName();
                final String senderMobileNumber = businessInvitation.getMobileNumber();
                final String photoUri = businessInvitation.getImageUrl();
                final String designation = businessInvitation.getDesignation();
                final long associationId = businessInvitation.getAssociationId();
                final int roleId = businessInvitation.getRoleId();

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.NAME, senderName);
                        bundle.putString(Constants.PHOTO_URI, photoUri);
                        bundle.putString(Constants.MOBILE_NUMBER, senderMobileNumber);
                        bundle.putString(Constants.DESIGNATION, designation);
                        bundle.putLong(Constants.ASSOCIATION_ID, associationId);
                        bundle.putInt(Constants.ROLE_ID, roleId);
                        bundle.putString(Constants.TAG, Constants.BUSINESS);

                        Intent intent = new Intent(getActivity(), NotificationActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                });
            }
        }

        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == Constants.NOTIFICATION_TYPE_INTRODUCTION_REQUEST) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introduction_requests_notification, parent, false);
                return new IntroductionRequestViewHolder(v);

            } else if (viewType == Constants.NOTIFICATION_TYPE_BUSINESS_ACCOUNT_INVITE) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_business_invitation, parent, false);
                return new BusinessInvitationViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_money_and_make_payment_request, parent, false);
                return new MoneyAndPaymentRequestViewHolder(v);
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
