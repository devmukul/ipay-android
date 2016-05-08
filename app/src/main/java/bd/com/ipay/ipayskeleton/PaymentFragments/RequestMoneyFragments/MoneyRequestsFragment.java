package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetNotificationsRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetNotificationsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.NotificationClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MoneyRequestsFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetAllNotificationsTask = null;
    private GetNotificationsResponse mGetNotificationsResponse;

    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private HttpRequestPostAsyncTask mAcceptRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private RecyclerView mNotificationsRecyclerView;
    private NotificationAndRecommendationListAdapter mNotificationAndRecommendationListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<NotificationClass> moneyRequestList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressDialog mProgressDialog;

    private int pageCount = 0;
    private boolean hasNext = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_money_requests, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mNotificationsRecyclerView = (RecyclerView) v.findViewById(R.id.list_notification);
        mProgressDialog = new ProgressDialog(getActivity());

        mNotificationAndRecommendationListAdapter = new NotificationAndRecommendationListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationsRecyclerView.setAdapter(mNotificationAndRecommendationListAdapter);

        // Refresh balance each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getMoneyRequests();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    pageCount = 0;
                    if (moneyRequestList != null)
                        moneyRequestList.clear();
                    getMoneyRequests();
                }
            }
        });

        return v;
    }

    private void getMoneyRequests() {
        if (mGetAllNotificationsTask != null) {
            return;
        }

        GetNotificationsRequest mTransactionHistoryRequest = new GetNotificationsRequest(pageCount);
        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mGetAllNotificationsTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_MONEY_REQUESTS,
                Constants.BASE_URL + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mGetAllNotificationsTask.mHttpResponseListener = this;
        mGetAllNotificationsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                Constants.BASE_URL + Constants.URL_REJECT_NOTIFICATION_REQUEST, json, getActivity());
        mRejectRequestTask.mHttpResponseListener = this;
        mRejectRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                Constants.BASE_URL + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, getActivity());
        mAcceptRequestTask.mHttpResponseListener = this;
        mAcceptRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        if (resultList.get(0).equals(Constants.COMMAND_GET_MONEY_REQUESTS)) {
            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mGetNotificationsResponse = gson.fromJson(resultList.get(2), GetNotificationsResponse.class);

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
                            Toast.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG).show();

            mGetAllNotificationsTask = null;
            mSwipeRefreshLayout.setRefreshing(false);

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
                        if (moneyRequestList != null)
                            moneyRequestList.clear();
                        moneyRequestList = null;
                        pageCount = 0;

                        // TODO: get this from notification table in sqlite
                        getMoneyRequests();

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
                        if (moneyRequestList != null)
                            moneyRequestList.clear();
                        moneyRequestList = null;
                        pageCount = 0;
                        // TODO: get this from notification table in sqlite
                        getMoneyRequests();

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

        }
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

                // Money request list items
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

            public void bindViewMoneyRequestList(int pos) {

                final String imageUrl = moneyRequestList.get(pos).getOriginatorProfile().getUserProfilePicture();
                final String name = moneyRequestList.get(pos).originatorProfile.getUserName();
                final String description = moneyRequestList.get(pos).getDescription();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(moneyRequestList.get(pos).getRequestTime());
                final String title = moneyRequestList.get(pos).getTitle();
                final Long id = moneyRequestList.get(pos).getId();
                final BigDecimal amount = moneyRequestList.get(pos).getAmount();
                final Long serviceID = moneyRequestList.get(pos).getServiceID();

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
                        String customDescription = "";
                        if (serviceID != Constants.SERVICE_ID_RECOMMENDATION_REQUEST) {
                            customDescription = "You're going to send " + amount + " Tk. to " + name;
                        }
                    }
                });

                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String customDescription = "";
                        if (serviceID != Constants.SERVICE_ID_RECOMMENDATION_REQUEST) {
                            customDescription = "You're going to cancel the request for " + amount + " Tk. from " + name;
                        }
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
                            getMoneyRequests();
                        }
                    }
                });

                TextView loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                if (hasNext) loadMoreTextView.setText(R.string.load_more);
                else loadMoreTextView.setText(R.string.no_more_results);
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
            if (moneyRequestList == null) {
                return 0;
            }
            else {
                return 1 + moneyRequestList.size() + 1; // header, money requests list, footer
            }
        }

        @Override
        public int getItemViewType(int position) {

            int moneyRequestListSize = 0;

            if (moneyRequestList == null)
                return super.getItemViewType(position);

            if (position == 0)
                return MONEY_REQUEST_HEADER_VIEW;
            else if (position == moneyRequestListSize + 1)
                return FOOTER_VIEW;
            else
                return MONEY_REQUEST_ITEM_VIEW;
        }
    }

}
