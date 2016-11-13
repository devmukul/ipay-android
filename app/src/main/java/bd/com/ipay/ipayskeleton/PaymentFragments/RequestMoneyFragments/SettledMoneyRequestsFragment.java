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
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.MoneyRequestClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SettledMoneyRequestsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mMoneyRequestListTask = null;
    private GetRequestResponse mGetMoneyRequestListResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mMoneyRequestListRecyclerView;
    private MoneyRequestListAdapter mRequestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MoneyRequestClass> mMoneyRequestClasses;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean clearListAfterLoading;

    private String mName;
    private String mTime;
    private String mProfilePictureUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sent_money_requests, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mMoneyRequestListRecyclerView = (RecyclerView) v.findViewById(R.id.list_my_requests);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mRequestsAdapter = new MoneyRequestListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mMoneyRequestListRecyclerView.setLayoutManager(mLayoutManager);
        mMoneyRequestListRecyclerView.setAdapter(mRequestsAdapter);

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
        if (mMoneyRequestListTask != null) {
            return;
        }

        GetMoneyRequest mMoneyRequest = new GetMoneyRequest(pageCount,
                Constants.SERVICE_ID_REQUEST_MONEY);
        Gson gson = new Gson();
        String json = gson.toJson(mMoneyRequest);
        mMoneyRequestListTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_ALL_SETTLED_REQUESTS,
                Constants.BASE_URL_SM + Constants.URL_GET_ALL_REQUESTS, json, getActivity());
        mMoneyRequestListTask.mHttpResponseListener = this;
        mMoneyRequestListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeProcessingList() {
        List<MoneyRequestClass> tempMoneyRequestClasses = new ArrayList<MoneyRequestClass>();
        for (MoneyRequestClass paymentClass : mMoneyRequestClasses) {
            if (paymentClass.getStatus() == Constants.REQUEST_STATUS_PROCESSING)
                tempMoneyRequestClasses.add(paymentClass);
        }

        mMoneyRequestClasses.removeAll(tempMoneyRequestClasses);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (this.isAdded()) setContentShown(true);
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mMoneyRequestListTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            }
            return;
        }
        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_ALL_SETTLED_REQUESTS)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {

                    mGetMoneyRequestListResponse = gson.fromJson(result.getJsonString(), GetRequestResponse.class);

                    if (clearListAfterLoading || mMoneyRequestClasses == null) {
                        mMoneyRequestClasses = mGetMoneyRequestListResponse.getAllNotifications();
                        clearListAfterLoading = false;
                    } else {
                        List<MoneyRequestClass> tempPendingMoneyRequestClasses;
                        tempPendingMoneyRequestClasses = mGetMoneyRequestListResponse.getAllNotifications();
                        mMoneyRequestClasses.addAll(tempPendingMoneyRequestClasses);

                    }
                    removeProcessingList();
                    hasNext = mGetMoneyRequestListResponse.isHasNext();
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
            mMoneyRequestListTask = null;

        }

        if (mMoneyRequestClasses != null && mMoneyRequestClasses.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    public class MoneyRequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;

        public class MoneyRequestViewHolder extends RecyclerView.ViewHolder {
            private final TextView mNameView;
            private final TextView mTimeView;
            private final TextView mDescriptionView;
            private final ProfileImageView mProfileImageView;


            public MoneyRequestViewHolder(final View itemView) {
                super(itemView);

                mNameView = (TextView) itemView.findViewById(R.id.request_number);
                mTimeView = (TextView) itemView.findViewById(R.id.time);
                mDescriptionView = (TextView) itemView.findViewById(R.id.description);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
            }

            public void bindView(final int pos) {

                final MoneyRequestClass moneyRequestsClass = mMoneyRequestClasses.get(pos);

                mTime = Utilities.getDateFormat(moneyRequestsClass.getRequestTime());

                if (ProfileInfoCacheManager.getMobileNumber().equals(moneyRequestsClass.getOriginatorProfile().getUserMobileNumber())) {
                    mName = moneyRequestsClass.getReceiverProfile().getUserName();
                    mProfilePictureUrl = moneyRequestsClass.getReceiverProfile().getUserProfilePicture();
                } else {
                    mName = moneyRequestsClass.getOriginatorProfile().getUserName();
                    mProfilePictureUrl = moneyRequestsClass.getOriginatorProfile().getUserProfilePicture();
                }

                mTimeView.setText(mTime);
                mNameView.setText(mName);
                mDescriptionView.setText(Utilities.formatTaka(mMoneyRequestClasses.get(pos).getAmount()));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                        intent.putExtra(Constants.STATUS, Constants.REQUEST_STATUS_ACCEPTED);
                        intent.putExtra(Constants.MONEY_REQUEST_ID, moneyRequestsClass.getTransactionID());
                        startActivity(intent);
                    }
                });

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mProfilePictureUrl, false);

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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_money_request, parent, false);
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
            if (mMoneyRequestClasses == null || mMoneyRequestClasses.isEmpty())
                return 0;
            else
                return mMoneyRequestClasses.size() + 1;
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
