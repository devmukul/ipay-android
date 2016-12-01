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

public class SentMoneyRequestsHistoryFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSentRequestTask = null;
    private GetRequestResponse mGetSentRequestResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mRequestListRecyclerView;
    private SentMoneyRequestListAdapter mRequestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<RequestsSentClass> sentMoneyRequestClasses;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean clearListAfterLoading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sent_money_requests, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mRequestListRecyclerView = (RecyclerView) v.findViewById(R.id.list_my_requests);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mRequestsAdapter = new SentMoneyRequestListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRequestListRecyclerView.setLayoutManager(mLayoutManager);
        mRequestListRecyclerView.setAdapter(mRequestsAdapter);

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
        getSentRequests();
    }

    private void refreshPendingList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            clearListAfterLoading = true;
            getSentRequests();
        }
    }

    private void getSentRequests() {
        if (mSentRequestTask != null) {
            return;
        }

        GetMoneyRequest mMoneyRequest = new GetMoneyRequest(pageCount,
                Constants.SERVICE_ID_REQUEST_MONEY,
                Constants.REQUEST_STATUS_ALL);
        Gson gson = new Gson();
        String json = gson.toJson(mMoneyRequest);
        mSentRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_REQUESTS_ME,
                Constants.BASE_URL_SM + Constants.URL_GET_SENT_REQUESTS, json, getActivity());
        mSentRequestTask.mHttpResponseListener = this;
        mSentRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeProcessingList() {
        List<RequestsSentClass> tempMoneyRequestClasses = new ArrayList<RequestsSentClass>();
        for (RequestsSentClass paymentClass : sentMoneyRequestClasses) {
            if (paymentClass.getStatus() == Constants.REQUEST_STATUS_PROCESSING)
                tempMoneyRequestClasses.add(paymentClass);
        }

        sentMoneyRequestClasses.removeAll(tempMoneyRequestClasses);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (this.isAdded()) setContentShown(true);
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mSentRequestTask = null;
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

                    mGetSentRequestResponse = gson.fromJson(result.getJsonString(), GetRequestResponse.class);

                    if (clearListAfterLoading || sentMoneyRequestClasses == null) {
                        sentMoneyRequestClasses = mGetSentRequestResponse.getAllNotifications();
                        clearListAfterLoading = false;
                    } else {
                        List<RequestsSentClass> tempPendingMoneyRequestClasses;
                        tempPendingMoneyRequestClasses = mGetSentRequestResponse.getAllNotifications();
                        sentMoneyRequestClasses.addAll(tempPendingMoneyRequestClasses);

                    }
                    removeProcessingList();
                    hasNext = mGetSentRequestResponse.isHasNext();
                    mRequestsAdapter.notifyDataSetChanged();

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
            mSentRequestTask = null;

        }

        if (sentMoneyRequestClasses != null && sentMoneyRequestClasses.size() == 0) {
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

                final long id = sentMoneyRequestClasses.get(pos).getId();
                String time = Utilities.formatDateWithTime(sentMoneyRequestClasses.get(pos).getRequestTime());
                final String name = sentMoneyRequestClasses.get(pos).getReceiverProfile().getUserName();
                final String imageUrl = sentMoneyRequestClasses.get(pos).getReceiverProfile().getUserProfilePicture();

                mTime.setText(time);
                mSenderNumber.setText(name);
                mDescriptionView.setText(Utilities.formatTaka(sentMoneyRequestClasses.get(pos).getAmount()));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (sentMoneyRequestClasses.get(pos).getStatus() == Constants.REQUEST_STATUS_ACCEPTED) {
                            Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                            intent.putExtra(Constants.STATUS, Constants.REQUEST_STATUS_ACCEPTED);
                            intent.putExtra(Constants.MONEY_REQUEST_ID, sentMoneyRequestClasses.get(pos).getTransactionID());
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
                            getSentRequests();
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
            if (sentMoneyRequestClasses == null || sentMoneyRequestClasses.isEmpty())
                return 0;
            else
                return sentMoneyRequestClasses.size() + 1;
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
