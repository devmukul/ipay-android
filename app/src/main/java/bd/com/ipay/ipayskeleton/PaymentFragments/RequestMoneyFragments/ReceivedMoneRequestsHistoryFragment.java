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
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.GetMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.GetRequestResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestsSentClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ReceivedMoneRequestsHistoryFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mReceivedRequestTask = null;
    private GetRequestResponse mGetReceivedRequestResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mReceivedListRecyclerView;
    private SentMoneyRequestListAdapter mReceivedRequestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<RequestsSentClass> receivedMoneyRequestClasses;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean clearListAfterLoading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sent_money_requests, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mReceivedListRecyclerView = (RecyclerView) v.findViewById(R.id.list_my_requests);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mReceivedRequestsAdapter = new SentMoneyRequestListAdapter();
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

        return v;
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
                Constants.REQUEST_STATUS_ALL);
        Gson gson = new Gson();
        String json = gson.toJson(mMoneyRequest);
        mReceivedRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_REQUESTS_ME,
                Constants.BASE_URL_SM + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mReceivedRequestTask.mHttpResponseListener = this;
        mReceivedRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeProcessingList() {
        List<RequestsSentClass> tempMoneyRequestClasses = new ArrayList<RequestsSentClass>();
        for (RequestsSentClass paymentClass : receivedMoneyRequestClasses) {
            if (paymentClass.getStatus() == Constants.REQUEST_STATUS_PROCESSING)
                tempMoneyRequestClasses.add(paymentClass);
        }

        receivedMoneyRequestClasses.removeAll(tempMoneyRequestClasses);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (this.isAdded()) setContentShown(true);
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mReceivedRequestTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            }
            return;
        }
        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_REQUESTS_ME)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {

                    mGetReceivedRequestResponse = gson.fromJson(result.getJsonString(), GetRequestResponse.class);

                    if (clearListAfterLoading || receivedMoneyRequestClasses == null) {
                        receivedMoneyRequestClasses = mGetReceivedRequestResponse.getAllNotifications();
                        clearListAfterLoading = false;
                    } else {
                        List<RequestsSentClass> tempPendingMoneyRequestClasses;
                        tempPendingMoneyRequestClasses = mGetReceivedRequestResponse.getAllNotifications();
                        receivedMoneyRequestClasses.addAll(tempPendingMoneyRequestClasses);

                    }
                    removeProcessingList();
                    hasNext = mGetReceivedRequestResponse.isHasNext();
                    mReceivedRequestsAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mReceivedRequestTask = null;

        }

        if (receivedMoneyRequestClasses != null && receivedMoneyRequestClasses.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    public class SentMoneyRequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;

        public class MoneyRequestViewHolder extends RecyclerView.ViewHolder {
            private final TextView mSenderNumber;
            private final TextView mTime;
            private final TextView mDescriptionView;
            private final ProfileImageView mProfileImageView;

            public MoneyRequestViewHolder(final View itemView) {
                super(itemView);

                mSenderNumber = (TextView) itemView.findViewById(R.id.request_number);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mDescriptionView = (TextView) itemView.findViewById(R.id.description);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
            }

            public void bindView(final int pos) {

                final long id = receivedMoneyRequestClasses.get(pos).getId();
                String time = Utilities.getDateFormat(receivedMoneyRequestClasses.get(pos).getRequestTime());
                final String name = receivedMoneyRequestClasses.get(pos).getReceiverProfile().getUserName();
                final String imageUrl = receivedMoneyRequestClasses.get(pos).getReceiverProfile().getUserProfilePicture();
                final String mobileNumber = receivedMoneyRequestClasses.get(pos).getReceiverProfile().getUserMobileNumber();
                final String title = receivedMoneyRequestClasses.get(pos).getTitle();
                final String description = receivedMoneyRequestClasses.get(pos).getDescription();
                final BigDecimal amount = receivedMoneyRequestClasses.get(pos).getAmount();

                mTime.setText(time);
                mSenderNumber.setText(name);
                mDescriptionView.setText(Utilities.formatTaka(receivedMoneyRequestClasses.get(pos).getAmount()));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (receivedMoneyRequestClasses.get(pos).getStatus() == Constants.REQUEST_STATUS_ACCEPTED) {
                            Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                            intent.putExtra(Constants.STATUS, Constants.REQUEST_STATUS_ACCEPTED);
                            intent.putExtra(Constants.MONEY_REQUEST_ID, "74I226-1576A432A2B");
                            startActivity(intent);
                        }
                    }
                });

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);

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
                            getReceivedRequests();
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
            if (viewType == MONEY_REQUEST_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_request_money_me, parent, false);
                return new MoneyRequestViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_load_more_footer, parent, false);
                return new FooterViewHolder(v);
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
            if (receivedMoneyRequestClasses == null || receivedMoneyRequestClasses.isEmpty())
                return 0;
            else
                return receivedMoneyRequestClasses.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1)
                return FOOTER_VIEW;
            else
                return MONEY_REQUEST_ITEM_VIEW;
        }

    }

}
