package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetNotificationsRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetNotificationsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.NotificationClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class NotificationFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetAllNotificationsTask = null;
    private GetNotificationsResponse mGetNotificationsResponse;

    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private HttpRequestPostAsyncTask mAcceptRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private HttpRequestPostAsyncTask mRejectPaymentTask = null;
    private HttpRequestPostAsyncTask mAcceptPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private RecyclerView mNotificationsRecyclerView;
    private NotificationAdapter mNotificationHistoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<NotificationClass> notificationList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgressDialog;

    private int pageCount = 0;
    private boolean hasNext = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        getActivity().setTitle(R.string.notification);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mNotificationsRecyclerView = (RecyclerView) v.findViewById(R.id.list_notification);
        mProgressDialog = new ProgressDialog(getActivity());

        mNotificationHistoryAdapter = new NotificationAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationsRecyclerView.setAdapter(mNotificationHistoryAdapter);

        // Refresh balance each time home page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getNotifications();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    pageCount = 0;
                    if (notificationList != null)
                        notificationList.clear();
                    getNotifications();
                }
            }
        });

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_notification).setVisible(false);
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
        mGetAllNotificationsTask.execute((Void) null);
    }

    private void rejectPaymentRequest(Long id) {

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
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_REQUEST_REJECT, json, getActivity());
        mRejectPaymentTask.mHttpResponseListener = this;
        mRejectPaymentTask.execute((Void) null);
    }

    private void acceptPaymentRequest(Long id) {

        if (mAcceptPaymentTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        PaymentAcceptRejectOrCancelRequest mPaymentAcceptRejectOrCancelRequest =
                new PaymentAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentAcceptRejectOrCancelRequest);
        mAcceptPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_REQUEST_ACCEPT, json, getActivity());
        mAcceptPaymentTask.mHttpResponseListener = this;
        mAcceptPaymentTask.execute((Void) null);
    }

    private void rejectRequestMoney(Long id) {
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
                Constants.BASE_URL_SM + Constants.URL_REQUEST_REJECT, json, getActivity());
        mRejectRequestTask.mHttpResponseListener = this;
        mRejectRequestTask.execute((Void) null);
    }

    private void acceptRequestMoney(Long id) {
        if (mAcceptRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mAcceptRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_REQUEST_MONEY_ACCEPT, json, getActivity());
        mAcceptRequestTask.mHttpResponseListener = this;
        mAcceptRequestTask.execute((Void) null);
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mGetAllNotificationsTask = null;
            mAcceptRequestTask = null;
            mRejectRequestTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_NOTIFICATIONS)) {
            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mGetNotificationsResponse = gson.fromJson(resultList.get(2), GetNotificationsResponse.class);

                        if (notificationList == null || notificationList.size() == 0) {
                            notificationList = mGetNotificationsResponse.getAllNotifications();
                        } else {
                            List<NotificationClass> tempNotificationList;
                            tempNotificationList = mGetNotificationsResponse.getAllNotifications();
                            notificationList.addAll(tempNotificationList);
                        }

                        hasNext = mGetNotificationsResponse.isHasNext();
                        mNotificationHistoryAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();

            mSwipeRefreshLayout.setRefreshing(false);
            mGetAllNotificationsTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_REJECT_REQUESTS_MONEY)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                                RequestMoneyAcceptRejectOrCancelResponse.class);
                        String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        // Refresh the pending list
                        if (notificationList != null)
                            notificationList.clear();
                        notificationList = null;
                        pageCount = 0;

                        // TODO: get this from notification table in sqlite
                        getNotifications();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mRejectRequestTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ACCEPT_REQUESTS_MONEY)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                                RequestMoneyAcceptRejectOrCancelResponse.class);
                        String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        // Refresh the pending list
                        if (notificationList != null)
                            notificationList.clear();
                        notificationList = null;
                        pageCount = 0;
                        // TODO: get this from notification table in sqlite
                        getNotifications();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mAcceptRequestTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_REJECT_PAYMENT_REQUEST)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mPaymentAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                                PaymentAcceptRejectOrCancelResponse.class);
                        String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        // Refresh the pending list
                        if (notificationList != null)
                            notificationList.clear();
                        notificationList = null;
                        pageCount = 0;

                        // TODO: get this from notification table in sqlite
                        getNotifications();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mRejectPaymentTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mPaymentAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                                PaymentAcceptRejectOrCancelResponse.class);
                        String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        // Refresh the pending list
                        if (notificationList != null)
                            notificationList.clear();
                        notificationList = null;
                        pageCount = 0;

                        // TODO: get this from notification table in sqlite
                        getNotifications();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mAcceptPaymentTask = null;
        }
    }

    private class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public NotificationAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mDescription;
            private TextView mTitle;
            private TextView mTime;
            private TextView loadMoreTextView;
            private RoundedImageView mPortrait;
            private LinearLayout optionsLayout;
            private Button acceptButton;
            private Button rejectButton;
            private Button markAsSpamButton;
            private View viewBetweenRejectAndSpam;

            public ViewHolder(final View itemView) {
                super(itemView);

                mDescription = (TextView) itemView.findViewById(R.id.description);
                mTime = (TextView) itemView.findViewById(R.id.time);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mTitle = (TextView) itemView.findViewById(R.id.title);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                acceptButton = (Button) itemView.findViewById(R.id.accept_button);
                rejectButton = (Button) itemView.findViewById(R.id.reject_button);
                markAsSpamButton = (Button) itemView.findViewById(R.id.mark_as_spam_button);
                viewBetweenRejectAndSpam = (View) itemView.findViewById(R.id.view_2);
            }

            public void bindView(int pos) {

                final String imageUrl = notificationList.get(pos).getOriginatorProfile().getUserProfilePicture();
                final String description = notificationList.get(pos).getDescription();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, H:MM a").format(notificationList.get(pos).getRequestTime());
                final String title = notificationList.get(pos).getTitle();
                final Long id = notificationList.get(pos).getId();
                final Long serviceID = notificationList.get(pos).getServiceID();

                mDescription.setText(description);
                mTime.setText(time);
                mTitle.setText(title);

                Glide.with(getActivity())
                        .load(Constants.BASE_URL_IMAGE_SERVER + imageUrl)
                        .crossFade()
                        .error(R.drawable.ic_person)
                        .transform(new CircleTransform(getActivity()))
                        .into(mPortrait);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (optionsLayout.getVisibility() == View.VISIBLE)
                            optionsLayout.setVisibility(View.GONE);
                        else optionsLayout.setVisibility(View.VISIBLE);
                    }
                });

                if (serviceID == Constants.SERVICE_ID_RECOMMENDATION_REQUEST) {
                    viewBetweenRejectAndSpam.setVisibility(View.VISIBLE);
                    markAsSpamButton.setVisibility(View.VISIBLE);
                }

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) acceptRequestMoney(id);
                        else if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE)
                            acceptPaymentRequest(id);
                    }
                });

                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) rejectRequestMoney(id);
                        else if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE)
                            rejectPaymentRequest(id);
                    }
                });

                markAsSpamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO
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

        // Now define the viewholder for Normal list item
        public class NormalViewHolder extends ViewHolder {
            public NormalViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do whatever you want on clicking the normal items
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_load_more_footer, parent, false);

                FooterViewHolder vh = new FooterViewHolder(v);

                return vh;
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);

            NormalViewHolder vh = new NormalViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof NormalViewHolder) {
                    NormalViewHolder vh = (NormalViewHolder) holder;
                    vh.bindView(position);
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
            if (notificationList != null)
                return notificationList.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == notificationList.size()) {
                // This is where we'll add footer.
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}
