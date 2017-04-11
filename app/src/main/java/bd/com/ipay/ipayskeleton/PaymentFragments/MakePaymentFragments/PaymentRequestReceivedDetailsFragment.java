package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.InvoiceItem;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PaymentRequestReceivedDetailsFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAcceptPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptPaymentResponse;

    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mPaymentRejectResponse;

    private LinearLayoutManager mLayoutManager;
    private RecyclerView mReviewRecyclerView;
    private PaymentReviewAdapter paymentReviewAdapter;

    private List<InvoiceItem> mInvoiceItemList;
    private BigDecimal mTotal;
    private BigDecimal mAmount;
    private BigDecimal mNetAmount;
    private BigDecimal mVat;
    public BigDecimal mServiceCharge = new BigDecimal(-1);
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long requestId;
    private String mTitle;
    private String mDescription;

    private boolean isPinRequired = true;
    private boolean switchedFromTransactionHistory = false;

    private ProgressDialog mProgressDialog;

    private final int HEADER_FOOTER_VIEW_COUNT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_make_payment_notification_review, container, false);
        getActivity().setTitle(R.string.make_payment);

        mReviewRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);
        paymentReviewAdapter = new PaymentReviewAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mReviewRecyclerView.setLayoutManager(mLayoutManager);

        mProgressDialog = new ProgressDialog(getActivity());

        switchedFromTransactionHistory = getActivity().getIntent()
                .getBooleanExtra(Constants.SWITCHED_FROM_TRANSACTION_HISTORY, false);

        initializeValues();

        mReviewRecyclerView.setAdapter(paymentReviewAdapter);

        attemptGetServiceCharge();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void attemptAcceptPaymentRequestWithPinCheck() {
        if (this.isPinRequired) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    acceptPaymentRequest(requestId, pin);
                }
            });
        } else
            acceptPaymentRequest(requestId, null);
    }

    private void initializeValues() {
        Bundle bundle = getArguments();

        this.requestId = bundle.getLong(Constants.MONEY_REQUEST_ID);
        this.mReceiverMobileNumber = bundle.getString(Constants.MOBILE_NUMBER);
        this.mReceiverName = bundle.getString(Constants.NAME);
        this.mPhotoUri = bundle.getString(Constants.PHOTO_URI);
        this.mVat = new BigDecimal(bundle.getString(Constants.VAT));
        this.mTotal = new BigDecimal(bundle.getString(Constants.AMOUNT));
        this.mTitle = bundle.getString(Constants.TITLE);
        this.mDescription = bundle.getString(Constants.DESCRIPTION);
        this.mInvoiceItemList = bundle.getParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG);
    }

    private void acceptPaymentRequest(long id, String pin) {

        if (mAcceptPaymentTask != null)
            return;

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        PaymentAcceptRejectOrCancelRequest mPaymentAcceptRejectOrCancelRequest =
                new PaymentAcceptRejectOrCancelRequest(id, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentAcceptRejectOrCancelRequest);
        mAcceptPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, getActivity());
        mAcceptPaymentTask.mHttpResponseListener = this;
        mAcceptPaymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void rejectPaymentRequest(long id) {
        if (mRejectRequestTask != null)
            return;

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_rejecting));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mRejectRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REJECT_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_REJECT_NOTIFICATION_REQUEST, json, getActivity());
        mRejectRequestTask.mHttpResponseListener = this;
        mRejectRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected int getServiceID() {
        return Constants.SERVICE_ID_REQUEST_PAYMENT;
    }

    @Override
    protected BigDecimal getAmount() {
        return mTotal;
    }

    @Override
    protected void onServiceChargeLoadFinished(BigDecimal serviceCharge) {

        this.mServiceCharge = serviceCharge;
        paymentReviewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPinLoadFinished(boolean isPinRequired) {
        this.isPinRequired = isPinRequired;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mAcceptPaymentTask = null;
            mRejectRequestTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.send_money_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST)) {

            try {
                mPaymentAcceptPaymentResponse = gson.fromJson(result.getJsonString(),
                        PaymentAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mPaymentAcceptPaymentResponse.getMessage();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        if (switchedFromTransactionHistory)
                            Utilities.finishLauncherActivity(getActivity());
                        else
                            getActivity().onBackPressed();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mPaymentAcceptPaymentResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
            mAcceptPaymentTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_REJECT_REQUESTS_MONEY)) {

            try {
                mPaymentRejectResponse = gson.fromJson(result.getJsonString(),
                        RequestMoneyAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mPaymentRejectResponse.getMessage();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        if (switchedFromTransactionHistory)
                            Utilities.finishLauncherActivity(getActivity());
                        else
                            getActivity().onBackPressed();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mPaymentRejectResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mRejectRequestTask = null;

        }
    }

    private class PaymentReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int NOTIFICATION_REVIEW_LIST_ITEM_VIEW = 2;
        private static final int NOTIFICATION_REVIEW_LIST_HEADER_VIEW = 1;
        private static final int NOTIFICATION_REVIEW_LIST_FOOTER_VIEW = 3;

        public PaymentReviewAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mItemNameView;
            private final TextView mQuantityView;
            private final TextView mAmountView;
            private final ProfileImageView mProfileImageView;
            private final TextView mNameView;
            private final TextView mMobileNumberView;
            private final TextView mNetAmountView;
            private final TextView mVatView;
            private final View headerView;
            private final TextView mServiceChargeView;
            private final TextView mTotalView;
            private Button mAcceptButton;
            private Button mRejectButton;
            private LinearLayout mLinearLayoutDescriptionHolder;
            private TextView mDescriptionView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mItemNameView = (TextView) itemView.findViewById(R.id.textview_item);
                mQuantityView = (TextView) itemView.findViewById(R.id.textview_quantity);
                mAmountView = (TextView) itemView.findViewById(R.id.textview_amount);

                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);
                mNetAmountView = (TextView) itemView.findViewById(R.id.textview_net_amount);
                mVatView = (TextView) itemView.findViewById(R.id.textview_vat);
                headerView = itemView.findViewById(R.id.header);
                mServiceChargeView = (TextView) itemView.findViewById(R.id.textview_service_charge);
                mTotalView = (TextView) itemView.findViewById(R.id.textview_total);
                mAcceptButton = (Button) itemView.findViewById(R.id.button_accept);
                mRejectButton = (Button) itemView.findViewById(R.id.button_reject);

                mLinearLayoutDescriptionHolder = (LinearLayout) itemView.findViewById(R.id.layout_description_holder);
                mDescriptionView = (TextView) itemView.findViewById(R.id.textview_description);
            }

            public void bindViewForListItem(int pos) {
                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                mItemNameView.setText(mInvoiceItemList.get(pos).getItem());
                mQuantityView.setText(mInvoiceItemList.get(pos).getQuantity().toString());
                mAmountView.setText(Utilities.formatTaka(mInvoiceItemList.get(pos).getAmount()));
            }

            public void bindViewForHeader() {

                if (mInvoiceItemList == null || mInvoiceItemList.size() == 0)
                    headerView.setVisibility(View.GONE);

                if (mReceiverName == null || mReceiverName.isEmpty())
                    mNameView.setVisibility(View.GONE);
                else
                    mNameView.setText(mReceiverName);

                mMobileNumberView.setText(mReceiverMobileNumber);
                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mPhotoUri, false);
            }

            public void bindViewForFooter() {
                mAmount = mTotal.subtract(mVat);
                mNetAmount = mTotal.subtract(mServiceCharge);

                mAmountView.setText(Utilities.formatTaka(mAmount));
                mNetAmountView.setText(Utilities.formatTaka(mNetAmount));
                mVatView.setText(Utilities.formatTaka(mVat));
                mServiceChargeView.setText(Utilities.formatTaka(mServiceCharge));
                mTotalView.setText(Utilities.formatTaka(mTotal));

                if (mTitle.equals("Invoice"))
                    mLinearLayoutDescriptionHolder.setVisibility(View.GONE);
                else
                    mDescriptionView.setText(mDescription);

                mAcceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptAcceptPaymentRequestWithPinCheck();
                    }
                });

                mRejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialDialog.Builder rejectDialog = new MaterialDialog.Builder(getActivity());
                        rejectDialog.content(R.string.confirm_request_rejection);
                        rejectDialog.positiveText(R.string.yes);
                        rejectDialog.negativeText(R.string.no);
                        rejectDialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                rejectPaymentRequest(requestId);
                            }
                        });
                        rejectDialog.show();
                    }
                });
            }
        }

        public class ListFooterViewHolder extends ViewHolder {
            public ListFooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ListHeaderViewHolder extends ViewHolder {
            public ListHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ListItemViewHolder extends ViewHolder {
            public ListItemViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            if (viewType == NOTIFICATION_REVIEW_LIST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_make_payment_notification_review_header, parent, false);
                return new ListHeaderViewHolder(v);

            } else if (viewType == NOTIFICATION_REVIEW_LIST_FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_request_payment_accept_reject_footer_view, parent, false);
                return new ListFooterViewHolder(v);

            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_make_payment_notification_review, parent, false);
                return new ListItemViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                if (holder instanceof ListItemViewHolder) {
                    ListItemViewHolder vh = (ListItemViewHolder) holder;
                    vh.bindViewForListItem(position);

                } else if (holder instanceof ListHeaderViewHolder) {
                    ListHeaderViewHolder vh = (ListHeaderViewHolder) holder;
                    vh.bindViewForHeader();

                } else if (holder instanceof ListFooterViewHolder) {
                    ListFooterViewHolder vh = (ListFooterViewHolder) holder;
                    vh.bindViewForFooter();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mInvoiceItemList == null || mInvoiceItemList.size() == 0)
                return HEADER_FOOTER_VIEW_COUNT;
            if (mInvoiceItemList.size() > 0)
                // Count 2 added for header and footer view
                return 1 + mInvoiceItemList.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (mInvoiceItemList == null || mInvoiceItemList.size() == 0) {
                if (position == 0) return NOTIFICATION_REVIEW_LIST_HEADER_VIEW;
                else return NOTIFICATION_REVIEW_LIST_FOOTER_VIEW;
            }

            if (mInvoiceItemList.size() > 0) {
                if (position == 0) return NOTIFICATION_REVIEW_LIST_HEADER_VIEW;
                else if (position == mInvoiceItemList.size() + 1)
                    return NOTIFICATION_REVIEW_LIST_FOOTER_VIEW;
                else return NOTIFICATION_REVIEW_LIST_ITEM_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}

