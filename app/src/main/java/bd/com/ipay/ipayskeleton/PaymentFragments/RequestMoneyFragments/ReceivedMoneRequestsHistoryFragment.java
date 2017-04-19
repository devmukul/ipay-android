package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.GetMoneyAndPaymentRequestResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.MoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.GetMoneyRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ReceivedMoneRequestsHistoryFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mReceivedRequestTask = null;
    private GetMoneyAndPaymentRequestResponse mGetReceivedRequestResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mReceivedListRecyclerView;
    private ReceivedMoneyRequestListAdapter mReceivedRequestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MoneyAndPaymentRequest> moneyRequestList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean isLoading = false;
    private boolean clearListAfterLoading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent_money_requests, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mReceivedListRecyclerView = (RecyclerView) view.findViewById(R.id.list_my_requests);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        mEmptyListTextView = (TextView) view.findViewById(R.id.empty_list_text);
        mReceivedRequestsAdapter = new ReceivedMoneyRequestListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mReceivedListRecyclerView.setLayoutManager(mLayoutManager);
        mReceivedListRecyclerView.setAdapter(mReceivedRequestsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshPendingList();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
        getReceivedRequests();
    }

    private void refreshPendingList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            clearListAfterLoading = true;
            getReceivedRequests();
        }
    }

    private void getReceivedRequests() {
        if (mReceivedRequestTask != null) {
            return;
        }

        GetMoneyRequest mMoneyRequest = new GetMoneyRequest(pageCount,
                Constants.SERVICE_ID_REQUEST_MONEY,
                Constants.PAYMENT_REQUEST_STATUS_ALL);
        Gson gson = new Gson();
        String json = gson.toJson(mMoneyRequest);
        mReceivedRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_MONEY_REQUESTS,
                Constants.BASE_URL_SM + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mReceivedRequestTask.mHttpResponseListener = this;
        mReceivedRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeProcessingList() {
        List<MoneyAndPaymentRequest> tempMoneyRequestClasses = new ArrayList<MoneyAndPaymentRequest>();
        for (MoneyAndPaymentRequest paymentClass : moneyRequestList) {
            if (paymentClass.getStatus() == Constants.MONEY_REQUEST_STATUS_PROCESSING)
                tempMoneyRequestClasses.add(paymentClass);
        }

        moneyRequestList.removeAll(tempMoneyRequestClasses);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (this.isAdded()) setContentShown(true);
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mReceivedRequestTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null) {
                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
            }
            return;
        }
        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_MONEY_REQUESTS)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {

                    mGetReceivedRequestResponse = gson.fromJson(result.getJsonString(), GetMoneyAndPaymentRequestResponse.class);

                    if (clearListAfterLoading || moneyRequestList == null) {
                        moneyRequestList = mGetReceivedRequestResponse.getAllMoneyAndPaymentRequests();
                        clearListAfterLoading = false;
                    } else {
                        List<MoneyAndPaymentRequest> tempPendingMoneyRequestClasses;
                        tempPendingMoneyRequestClasses = mGetReceivedRequestResponse.getAllMoneyAndPaymentRequests();
                        moneyRequestList.addAll(tempPendingMoneyRequestClasses);
                    }

                    removeProcessingList();
                    hasNext = mGetReceivedRequestResponse.isHasNext();
                    if (isLoading) isLoading = false;
                    mReceivedRequestsAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG);
                }

            } else {
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG);
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mReceivedRequestTask = null;
        }

        if (moneyRequestList != null && moneyRequestList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private class ReceivedMoneyRequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;

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

            public void bindView(final int pos) {
                final MoneyAndPaymentRequest moneyRequest = moneyRequestList.get(pos);

                final long id = moneyRequest.getId();
                final String imageUrl = moneyRequest.getOriginatorProfile().getUserProfilePicture();
                final String name = moneyRequest.originatorProfile.getUserName();
                final String time = Utilities.formatDateWithTime(moneyRequest.getRequestTime());
                final BigDecimal amount = moneyRequest.getAmount();

                mDescriptionView.setText(Utilities.formatTaka(amount));
                mTimeView.setText(time);

                mTitleView.setText(name);

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (moneyRequestList.get(pos).getStatus() == Constants.MONEY_REQUEST_STATUS_ACCEPTED) {
                            Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                            intent.putExtra(Constants.STATUS, Constants.MONEY_REQUEST_STATUS_ACCEPTED);
                            intent.putExtra(Constants.MONEY_REQUEST_ID, moneyRequest.getTransactionID());
                            startActivity(intent);
                        }
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

                mLoadMoreTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasNext) {
                            pageCount = pageCount + 1;
                            showLoadingInFooter();
                            getReceivedRequests();
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

    }

}
