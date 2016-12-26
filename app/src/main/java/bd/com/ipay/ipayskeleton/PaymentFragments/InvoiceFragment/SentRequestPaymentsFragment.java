package bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment;

import android.app.ProgressDialog;
import android.graphics.Color;
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
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetPendingPaymentsRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetPendingPaymentsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PendingPaymentClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SentRequestPaymentsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mPendingInvoicesTask = null;
    private GetPendingPaymentsResponse mGetPendingPaymentsResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mPendingListRecyclerView;
    private PendingListAdapter mInvoicesSentAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PendingPaymentClass> pendingPaymentClasses;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mTime;
    private String mDescription;
    private int mStatus;
    private BigDecimal mAmount;
    private BigDecimal mVat;
    private long mId;
    private List<ItemList> mItemList;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;

    private int historyPageCount = 0;
    private boolean hasNext = false;
    private boolean clearListAfterLoading;
    private TextView mEmptyListTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sent_invoice, container, false);
        getActivity().setTitle(R.string.request_payment);

        ((RequestPaymentActivity) getActivity()).mFabCreateInvoice.setVisibility(View.VISIBLE);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mProgressDialog = new ProgressDialog(getActivity());
        mPendingListRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice_sent);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mInvoicesSentAdapter = new PendingListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPendingListRecyclerView.setLayoutManager(mLayoutManager);
        mPendingListRecyclerView.setAdapter(mInvoicesSentAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshInvoicesPendingList();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (pendingPaymentClasses == null) {
            setContentShown(false);
            getInvoicesPendingRequests();
        } else {
            setContentShown(true);
        }
    }

    private void refreshInvoicesPendingList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            historyPageCount = 0;
            clearListAfterLoading = true;
            getInvoicesPendingRequests();

        } else if (getActivity() != null)
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    private void getInvoicesPendingRequests() {
        if (mPendingInvoicesTask != null) {
            return;
        }

        GetPendingPaymentsRequest mGetPendingPaymentsRequest = new GetPendingPaymentsRequest(historyPageCount, Constants.SERVICE_ID_REQUEST_PAYMENT);
        Gson gson = new Gson();
        String json = gson.toJson(mGetPendingPaymentsRequest);
        mPendingInvoicesTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT,
                Constants.BASE_URL_SM + Constants.URL_GET_SENT_REQUESTS, json, getActivity());
        mPendingInvoicesTask.mHttpResponseListener = this;
        mPendingInvoicesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mPendingInvoicesTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        if (this.isAdded()) setContentShown(true);

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT)) {


            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {

                    mGetPendingPaymentsResponse = gson.fromJson(result.getJsonString(), GetPendingPaymentsResponse.class);

                    if (pendingPaymentClasses == null || clearListAfterLoading) {
                        clearListAfterLoading = false;
                        pendingPaymentClasses = mGetPendingPaymentsResponse.getRequests();

                    } else {
                        List<PendingPaymentClass> tempPaymentList;
                        tempPaymentList = mGetPendingPaymentsResponse.getRequests();
                        pendingPaymentClasses.addAll(tempPaymentList);
                    }

                    hasNext = mGetPendingPaymentsResponse.isHasNext();

                    mInvoicesSentAdapter.notifyDataSetChanged();

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
            mPendingInvoicesTask = null;
        }

        if (pendingPaymentClasses != null && pendingPaymentClasses.size() == 0)
            mEmptyListTextView.setVisibility(View.VISIBLE);
        else mEmptyListTextView.setVisibility(View.GONE);

    }

    public class PendingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public PendingListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mSenderNameTextView;
            private final TextView mAmountTextView;
            private final TextView mTimeTextView;
            private final TextView statusView;
            private final TextView loadMoreTextView;
            private final ProfileImageView mProfileImageView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mSenderNameTextView = (TextView) itemView.findViewById(R.id.request_name);
                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                mTimeTextView = (TextView) itemView.findViewById(R.id.time);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                statusView = (TextView) itemView.findViewById(R.id.status);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
            }


            public void bindView(int pos) {

                final String imageUrl = pendingPaymentClasses.get(pos).getReceiverProfile().getUserProfilePicture();
                final String time = Utilities.formatDateWithTime(pendingPaymentClasses.get(pos).getRequestTime());
                final String title = pendingPaymentClasses.get(pos).getTitle();
                final String name = pendingPaymentClasses.get(pos).getReceiverProfile().getUserName();
                final String mobileNumber = pendingPaymentClasses.get(pos).getReceiverProfile().getUserMobileNumber();
                final int status = pendingPaymentClasses.get(pos).getStatus();
                final BigDecimal amount = pendingPaymentClasses.get(pos).getAmount();
                final BigDecimal vat = pendingPaymentClasses.get(pos).getVat();
                final String descriptionofRequest = pendingPaymentClasses.get(pos).getDescriptionofRequest();
                final String description = pendingPaymentClasses.get(pos).getDescription();
                final long id = pendingPaymentClasses.get(pos).getId();
                final ItemList[] itemList = pendingPaymentClasses.get(pos).getItemList();

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);

                mSenderNameTextView.setText(name);

                switch (status) {
                    case Constants.INVOICE_STATUS_ACCEPTED:
                        statusView.setTextColor(getResources().getColor(R.color.bottle_green));
                        statusView.setText(R.string.accepted);
                        break;
                    case Constants.INVOICE_STATUS_PROCESSING:
                        statusView.setTextColor(getResources().getColor(R.color.background_yellow));
                        statusView.setText(R.string.processing);
                        break;
                    case Constants.INVOICE_STATUS_REJECTED:
                        statusView.setTextColor(Color.RED);
                        statusView.setText(R.string.rejected);
                        break;
                    case Constants.INVOICE_STATUS_CANCELED:
                        statusView.setTextColor(Color.GRAY);
                        statusView.setText(R.string.canceled);
                        break;
                    case Constants.INVOICE_STATUS_DRAFT:
                        statusView.setTextColor(Color.RED);
                        statusView.setText(R.string.draft);
                        break;
                    default:
                        statusView.setTextColor(Color.RED);
                        statusView.setText(R.string.not_applicable);
                        break;
                }

                mAmountTextView.setText(Utilities.formatTaka(pendingPaymentClasses.get(pos).getAmount()));
                mTimeTextView.setText(time);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mSwipeRefreshLayout.isRefreshing()) {
                            mTime = time;
                            mId = id;
                            mAmount = amount;
                            mVat = vat;
                            if (itemList != null)
                                mItemList = Arrays.asList(itemList);
                            if (title.equals("Invoice")) mDescription = description;
                            else mDescription = descriptionofRequest;
                            mStatus = status;
                            mReceiverName = name;
                            mReceiverMobileNumber = mobileNumber;
                            mPhotoUri = Constants.BASE_URL_FTP_SERVER + imageUrl;
                            launchInvoiceDetailsFragment();
                        }
                    }
                });
            }


            public void bindViewFooter() {
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
                            historyPageCount = historyPageCount + 1;
                            getInvoicesPendingRequests();
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

                return new FooterViewHolder(v);
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_invoices_sent, parent, false);

            return new NormalViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof NormalViewHolder) {
                    NormalViewHolder vh = (NormalViewHolder) holder;
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

            if (pendingPaymentClasses == null || pendingPaymentClasses.size() == 0) {
                return 0;
            } else {
                return 1 + pendingPaymentClasses.size();
            }

        }

        @Override
        public int getItemViewType(int position) {
            if (position == pendingPaymentClasses.size()) {
                // This is where we'll add footer.
                return FOOTER_VIEW;
            }
            return super.getItemViewType(position);
        }
    }

    private void launchInvoiceDetailsFragment() {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.DESCRIPTION, mDescription);
        bundle.putString(Constants.TIME, mTime);
        bundle.putLong(Constants.MONEY_REQUEST_ID, mId);
        bundle.putString(Constants.AMOUNT, mAmount.toString());
        bundle.putString(Constants.VAT, mVat.toString());
        bundle.putInt(Constants.STATUS, mStatus);
        bundle.putString(Constants.PHOTO_URI, mPhotoUri);
        bundle.putString(Constants.MOBILE_NUMBER, mReceiverMobileNumber);
        bundle.putString(Constants.NAME, mReceiverName);

        if (mItemList != null)
            bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, new ArrayList<>(mItemList));
        else
            bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, null);

        ((RequestPaymentActivity) getActivity()).switchToSentRequestPaymentDetailsFragment(bundle);

    }
}
