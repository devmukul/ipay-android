package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SentReceivedRequestReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.GetMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.GetRequestResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.MoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SentMoneyRequestsFragment extends ProgressFragment implements HttpResponseListener {

    private final int ACTION_CANCEL_REQUEST = 0;

    private HttpRequestPostAsyncTask mPendingRequestTask = null;
    private GetRequestResponse mGetPendingRequestResponse;

    private HttpRequestPostAsyncTask mCancelRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mPendingListRecyclerView;
    private SentMoneyRequestListAdapter mPendingRequestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MoneyRequest> pendingMoneyRequests;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean isLoading = false;
    private boolean clearListAfterLoading;

    // These variables hold the information needed to populate the review dialog
    private BigDecimal mAmount;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long mMoneyRequestId;
    private String mDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent_money_requests, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mPendingListRecyclerView = (RecyclerView) view.findViewById(R.id.list_my_requests);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        mEmptyListTextView = (TextView) view.findViewById(R.id.empty_list_text);
        mPendingRequestsAdapter = new SentMoneyRequestListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPendingListRecyclerView.setLayoutManager(mLayoutManager);
        mPendingListRecyclerView.setAdapter(mPendingRequestsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.SENT_REQUEST)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                }
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.SENT_REQUEST)) {
            return;
        }
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            clearListAfterLoading = true;
            getPendingRequests();
        }
    }

    private void refreshPendingList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            clearListAfterLoading = true;
            getPendingRequests();
        }
    }

    private void getPendingRequests() {
        if (mPendingRequestTask != null) {
            return;
        }

        GetMoneyRequest mMoneyRequest = new GetMoneyRequest(pageCount,
                Constants.SERVICE_ID_REQUEST_MONEY,
                Constants.MONEY_REQUEST_STATUS_PROCESSING);
        Gson gson = new Gson();
        String json = gson.toJson(mMoneyRequest);
        mPendingRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_REQUESTS_ME,
                Constants.BASE_URL_SM + Constants.URL_GET_SENT_REQUESTS, json, getActivity());
        mPendingRequestTask.mHttpResponseListener = this;
        mPendingRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void cancelRequest(Long id) {
        if (mCancelRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_cancelling));
        mProgressDialog.show();
        // No PIN needed for now to place a request from me
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id, null);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mCancelRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CANCEL_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_CANCEL_NOTIFICATION_REQUEST, json, getActivity());
        mCancelRequestTask.mHttpResponseListener = this;
        mCancelRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (this.isAdded()) setContentShown(true);
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mPendingRequestTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null) {
                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
            }
            return;
        }
        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_REQUESTS_ME)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {

                    mGetPendingRequestResponse = gson.fromJson(result.getJsonString(), GetRequestResponse.class);

                    if (clearListAfterLoading || pendingMoneyRequests == null) {
                        pendingMoneyRequests = mGetPendingRequestResponse.getAllNotifications();
                        clearListAfterLoading = false;
                    } else {
                        List<MoneyRequest> tempPendingMoneyRequests;
                        tempPendingMoneyRequests = mGetPendingRequestResponse.getAllNotifications();
                        pendingMoneyRequests.addAll(tempPendingMoneyRequests);
                    }

                    hasNext = mGetPendingRequestResponse.isHasNext();
                    if (isLoading) isLoading = false;
                    mPendingRequestsAdapter.notifyDataSetChanged();

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
            mPendingRequestTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_CANCEL_REQUESTS_MONEY)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                            RequestMoneyAcceptRejectOrCancelResponse.class);
                    String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), message, Toast.LENGTH_LONG);

                    // Refresh the pending list
                    if (pendingMoneyRequests != null)
                        pendingMoneyRequests.clear();
                    pendingMoneyRequests = null;
                    pageCount = 0;
                    getPendingRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.could_not_cancel_money_request, Toast.LENGTH_LONG);
                }

            } else {
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.could_not_cancel_money_request, Toast.LENGTH_LONG);
            }

            mProgressDialog.dismiss();
            mCancelRequestTask = null;
        }

        if (pendingMoneyRequests != null && pendingMoneyRequests.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    public class SentMoneyRequestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;

        private final int REQUEST_MONEY_REVIEW_REQUEST = 101;

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

            public void bindView(int pos) {

                final long id = pendingMoneyRequests.get(pos).getId();
                String time = Utilities.formatDateWithTime(pendingMoneyRequests.get(pos).getRequestTime());
                final String name = pendingMoneyRequests.get(pos).getReceiverProfile().getUserName();
                final String imageUrl = pendingMoneyRequests.get(pos).getReceiverProfile().getUserProfilePicture();
                final String mobileNumber = pendingMoneyRequests.get(pos).getReceiverProfile().getUserMobileNumber();
                final String description = pendingMoneyRequests.get(pos).getDescription();
                final BigDecimal amount = pendingMoneyRequests.get(pos).getAmount();

                mTime.setText(time);
                mSenderNumber.setText(name);
                mDescriptionView.setText(Utilities.formatTaka(pendingMoneyRequests.get(pos).getAmount()));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mMoneyRequestId = id;
                        mAmount = amount;
                        mReceiverName = name;
                        mReceiverMobileNumber = mobileNumber;
                        mPhotoUri = Constants.BASE_URL_FTP_SERVER + imageUrl;
                        mDescription = description;

                        launchReviewPage();

                        //showAlertDialogue(getString(R.string.cancel_money_request_confirm), ACTION_CANCEL_REQUEST, id);
                    }
                });

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
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
                            getPendingRequests();
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
                    vh.bindViewFooter();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (pendingMoneyRequests == null || pendingMoneyRequests.isEmpty())
                return 0;
            else
                // Count 1 is added for load more footer
                return pendingMoneyRequests.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1)
                return FOOTER_VIEW;
            else
                return MONEY_REQUEST_ITEM_VIEW;
        }

        private void launchReviewPage() {

            Intent intent = new Intent(getActivity(), SentReceivedRequestReviewActivity.class);
            intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_SENT_REQUEST);
            intent.putExtra(Constants.AMOUNT, mAmount);
            intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
            intent.putExtra(Constants.DESCRIPTION_TAG, mDescription);
            intent.putExtra(Constants.MONEY_REQUEST_ID, mMoneyRequestId);
            intent.putExtra(Constants.NAME, mReceiverName);
            intent.putExtra(Constants.PHOTO_URI, mPhotoUri);
            intent.putExtra(Constants.IS_IN_CONTACTS, new ContactSearchHelper(getActivity()).searchMobileNumber(mReceiverMobileNumber));

            startActivityForResult(intent, REQUEST_MONEY_REVIEW_REQUEST);
        }
    }

    private void showAlertDialog(String msg, final int action, final long id) {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
        alertDialogue.setTitle(R.string.confirm_query);
        alertDialogue.setMessage(msg);

        alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (action == ACTION_CANCEL_REQUEST)
                    cancelRequest(id);

            }
        });

        alertDialogue.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        alertDialogue.show();
    }
}
