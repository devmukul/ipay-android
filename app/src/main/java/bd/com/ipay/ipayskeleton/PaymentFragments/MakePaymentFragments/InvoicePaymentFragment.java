package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ReviewDialogFinishListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ReviewMakePaymentDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetMoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetMoneyAndPaymentRequestResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.MoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvoicePaymentFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetAllNotificationsTask = null;
    private GetMoneyAndPaymentRequestResponse mGetMoneyAndPaymentRequestResponse;

    private HttpRequestGetAsyncTask mGetSingleInvoiceTask = null;
    private MoneyAndPaymentRequest mGetSingleInvoiceResponse;

    private RecyclerView mInvoiceRecyclerView;
    private InvoiceListAdapter mInvoiceListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MoneyAndPaymentRequest> moneyRequestList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressDialog mProgressDialog;

    private int pageCount = 0;
    private boolean hasNext = false;
    private boolean clearListAfterLoading;

    // These variables hold the information needed to populate the review dialog
    private List<ItemList> mItemList;
    private BigDecimal mAmount;
    private BigDecimal mVat;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long mMoneyRequestId;
    private String mTitle;
    private String mDescription;
    private TextView mEmptyListTextView;


    private static final int REQUEST_CODE_PERMISSION = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invoice_payment, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mInvoiceRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);
        mProgressDialog = new ProgressDialog(getActivity());

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mInvoiceListAdapter = new InvoiceListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mInvoiceRecyclerView.setLayoutManager(mLayoutManager);
        mInvoiceRecyclerView.setAdapter(mInvoiceListAdapter);

        // Refresh each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getMakePaymentRequests();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCount = 0;
                clearListAfterLoading = true;
                refreshNotificationList();
            }
        });

        return v;
    }


    public void onResume() {
        super.onResume();
        if (Utilities.isConnectionAvailable(getActivity())) {
            refreshNotificationList();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.scan_qr_code, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan_qr_code) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_PERMISSION);
            } else initiateScan();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateScan();
                } else {
                    Toast.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initiateScan() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (scanResult == null) {
                return;
            }
            final String result = scanResult.getContents();
            if (result != null) {
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getSingleInvoice(Integer.parseInt(result));
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), R.string.error_invalid_QR_code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void refreshNotificationList() {
        if (Utilities.isConnectionAvailable(getActivity())) {
            pageCount = 0;
            clearListAfterLoading = true;
            getMakePaymentRequests();
        }
    }


    private void getMakePaymentRequests() {
        if (mGetAllNotificationsTask != null) {
            return;
        }

        GetMoneyAndPaymentRequest mTransactionHistoryRequest = new GetMoneyAndPaymentRequest(
                pageCount, Constants.SERVICE_ID_REQUEST_INVOICE);
        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mGetAllNotificationsTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_MONEY_REQUESTS,
                Constants.BASE_URL_SM + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mGetAllNotificationsTask.mHttpResponseListener = this;
        mGetAllNotificationsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getSingleInvoice(int invoiceId) {
        if (mGetSingleInvoiceTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_single_invoice));
        mProgressDialog.show();
        mGetSingleInvoiceTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SINGLE_INVOICE,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_GET_INVOICE + invoiceId + "/", getActivity());
        mGetSingleInvoiceTask.mHttpResponseListener = this;
        mGetSingleInvoiceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (this.isAdded()) setContentShown(true);
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetAllNotificationsTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_MONEY_REQUESTS:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    try {
                        mGetMoneyAndPaymentRequestResponse = gson.fromJson(result.getJsonString(), GetMoneyAndPaymentRequestResponse.class);

                        if (moneyRequestList == null || clearListAfterLoading || moneyRequestList.size() == 0) {
                            moneyRequestList = mGetMoneyAndPaymentRequestResponse.getAllMoneyAndPaymentRequests();
                            clearListAfterLoading = false;
                        } else {
                            List<MoneyAndPaymentRequest> tempNotificationList;
                            tempNotificationList = mGetMoneyAndPaymentRequestResponse.getAllMoneyAndPaymentRequests();
                            moneyRequestList.addAll(tempNotificationList);
                        }

                        hasNext = mGetMoneyAndPaymentRequestResponse.isHasNext();
                        mInvoiceListAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_fetching_money_requests, Toast.LENGTH_LONG).show();
                }

                mGetAllNotificationsTask = null;
                mSwipeRefreshLayout.setRefreshing(false);

                break;
            case Constants.COMMAND_GET_SINGLE_INVOICE:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    try {
                        mGetSingleInvoiceResponse = gson.fromJson(result.getJsonString(), MoneyAndPaymentRequest.class);
                        mMoneyRequestId = mGetSingleInvoiceResponse.getId();
                        mAmount = mGetSingleInvoiceResponse.getAmount();
                        mReceiverName = mGetSingleInvoiceResponse.originatorProfile.getUserName();
                        mReceiverMobileNumber = mGetSingleInvoiceResponse.originatorProfile.getUserMobileNumber();
                        mPhotoUri = mGetSingleInvoiceResponse.originatorProfile.getUserProfilePicture();
                        mTitle = mGetSingleInvoiceResponse.getTitle();
                        mVat = mGetSingleInvoiceResponse.getVat();
                        mItemList = mGetSingleInvoiceResponse.getItemList();

                        ReviewMakePaymentDialog dialog = new ReviewMakePaymentDialog(getActivity(), mMoneyRequestId, mReceiverMobileNumber,
                                mReceiverName, mPhotoUri, mAmount, mTitle, Constants.SERVICE_ID_REQUEST_MONEY, mVat, mItemList,
                                new ReviewDialogFinishListener() {
                                    @Override
                                    public void onReviewFinish() {
                                        refreshNotificationList();
                                    }
                                });
                        dialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_fetching_single_invoice, Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_fetching_single_invoice, Toast.LENGTH_LONG).show();
                    }
                }
                mGetSingleInvoiceTask = null;
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressDialog.dismiss();

                break;
        }

        if (moneyRequestList != null && moneyRequestList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private class InvoiceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int MONEY_REQUEST_ITEM_VIEW = 4;

        public InvoiceListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mDescriptionView;
            private final TextView mTitleView;
            private final TextView mTimeView;
            private final TextView loadMoreTextView;
            private final ProfileImageView mProfileImageView;

            public ViewHolder(final View itemView) {
                super(itemView);

                // Money request list items
                mDescriptionView = (TextView) itemView.findViewById(R.id.textview_description);
                mTimeView = (TextView) itemView.findViewById(R.id.textview_time);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mTitleView = (TextView) itemView.findViewById(R.id.textview_title);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
            }

            public void bindViewMoneyRequestList(int pos) {
                final MoneyAndPaymentRequest moneyRequest = moneyRequestList.get(pos);

                final long id = moneyRequest.getId();
                final String imageUrl = moneyRequest.getOriginatorProfile().getUserProfilePicture();
                final String name = moneyRequest.originatorProfile.getUserName();
                final String mobileNumber = moneyRequest.originatorProfile.getUserMobileNumber();
                final String description = moneyRequest.getDescriptionofRequest();
                final String time = Utilities.getDateFormat(moneyRequest.getRequestTime());
                final String title = moneyRequest.getTitle();
                final BigDecimal amount = moneyRequest.getAmount();
                final BigDecimal vat = moneyRequest.getVat();
                final List<ItemList> itemList = moneyRequest.getItemList();

                mDescriptionView.setText(Utilities.formatTaka(amount));
                mTimeView.setText(time);
                mTitleView.setText(name);

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMoneyRequestId = id;
                        mAmount = amount;
                        mReceiverName = name;
                        mReceiverMobileNumber = mobileNumber;
                        mPhotoUri = imageUrl;
                        mTitle = title;
                        mVat = vat;
                        mItemList = itemList;
                        mDescription = description;
                        launchInvoiceHistoryFragment();
                    }
                });
            }

            public void bindViewFooter() {
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
                            getMakePaymentRequests();
                        }
                    }
                });

                TextView loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                if (hasNext)
                    loadMoreTextView.setText(R.string.load_more);
                else
                    loadMoreTextView.setText(R.string.no_more_results);
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
                    vh.bindViewMoneyRequestList(position);

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
                return moneyRequestList.size() + 1; // money requests list, footer
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (moneyRequestList == null)
                return super.getItemViewType(position);

            else if (position == getItemCount() - 1)
                return FOOTER_VIEW;
            else
                return MONEY_REQUEST_ITEM_VIEW;
        }

        private void launchInvoiceHistoryFragment() {

            Bundle bundle = new Bundle();
            bundle.putLong(Constants.MONEY_REQUEST_ID, mMoneyRequestId);
            bundle.putString(Constants.VAT, mVat.toString());
            bundle.putString(Constants.PHOTO_URI, mPhotoUri);
            bundle.putString(Constants.MOBILE_NUMBER, mReceiverMobileNumber);
            bundle.putString(Constants.NAME, mReceiverName);
            bundle.putInt(Constants.MONEY_REQUEST_SERVICE_ID, Constants.SERVICE_ID_REQUEST_MONEY);
            bundle.putString(Constants.AMOUNT, mAmount.toString());
            bundle.putString(Constants.TITLE, mTitle);
            bundle.putString(Constants.DESCRIPTION, mDescription);
            bundle.putParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG, new ArrayList<>(mItemList));

            ((PaymentActivity) getActivity()).switchToInvoiceHistoryFragment(bundle);
        }
    }

}