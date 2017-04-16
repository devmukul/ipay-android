package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.InvoiceItem;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.MoneyAndPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class SingleInvoiceFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetSingleInvoiceTask = null;
    private MoneyAndPaymentRequest mGetSingleInvoiceResponse;
    private ProgressDialog mProgressDialog;

    private RecyclerView mReviewRecyclerView;
    private PaymentReviewAdapter paymentReviewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private HttpRequestPostAsyncTask mAcceptPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private List<InvoiceItem> mInvoiceItemList;
    private BigDecimal mAmount;
    private BigDecimal mNetAmount;
    private BigDecimal mVat;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long mRequestId;
    private String mDescription;
    private String mTitle;

    public BigDecimal mServiceCharge = new BigDecimal(-1);


    private boolean isPinRequired = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_make_payment_notification_review, container, false);
        mProgressDialog = new ProgressDialog(getActivity());

        mReviewRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);

        String result = getArguments().getString(Constants.RESULT);
        try {
            if (TextUtils.isDigitsOnly(result))
                getSingleInvoice(Integer.parseInt(result));
            else {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), R.string.not_a_valid_invoice_id, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), R.string.not_a_valid_invoice_id, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        paymentReviewAdapter = new PaymentReviewAdapter();
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mReviewRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    private void getSingleInvoice(int invoiceId) {
        if (mGetSingleInvoiceTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_payment_request));
        mProgressDialog.show();
        mGetSingleInvoiceTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SINGLE_INVOICE,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_GET_INVOICE + invoiceId + "/", getActivity());
        mGetSingleInvoiceTask.mHttpResponseListener = this;
        mGetSingleInvoiceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptAcceptPaymentRequestWithPinCheck() {
        if (this.isPinRequired) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    acceptPaymentRequest(mRequestId, pin);
                }
            });
        } else {
            acceptPaymentRequest(mRequestId, null);
        }

    }

    private void acceptPaymentRequest(long id, String pin) {

        if (mAcceptPaymentTask != null) {
            return;
        }

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

    private void rejectRequestMoney(long id) {
        if (mRejectRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_rejecting));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mRejectRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REJECT_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_CANCEL_NOTIFICATION_REQUEST, json, getActivity());
        mRejectRequestTask.mHttpResponseListener = this;
        mRejectRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    protected int getServiceID() {
        return Constants.SERVICE_ID_MAKE_PAYMENT;
    }

    @Override
    protected BigDecimal getAmount() {
        return mAmount;
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

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mAcceptPaymentTask = null;
            mRejectRequestTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_SINGLE_INVOICE:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    try {
                        mGetSingleInvoiceResponse = gson.fromJson(result.getJsonString(), MoneyAndPaymentRequest.class);

                        mPhotoUri = mGetSingleInvoiceResponse.getOriginatorProfile().getUserProfilePicture();
                        mReceiverName = mGetSingleInvoiceResponse.originatorProfile.getUserName();
                        mReceiverMobileNumber = mGetSingleInvoiceResponse.originatorProfile.getUserMobileNumber();
                        mDescription = mGetSingleInvoiceResponse.getDescriptionofRequest();
                        mTitle = mGetSingleInvoiceResponse.getTitle();
                        mRequestId = mGetSingleInvoiceResponse.getId();
                        mAmount = mGetSingleInvoiceResponse.getAmount();
                        mVat = mGetSingleInvoiceResponse.getVat();
                        mInvoiceItemList = mGetSingleInvoiceResponse.getItemList();

                        mReviewRecyclerView.setAdapter(paymentReviewAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_fetching_payment_request, Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_fetching_payment_request, Toast.LENGTH_LONG).show();
                    }
                }

                mGetSingleInvoiceTask = null;
                mProgressDialog.dismiss();

                break;
            case Constants.COMMAND_ACCEPT_PAYMENT_REQUEST:

                try {
                    mPaymentAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                            PaymentAcceptRejectOrCancelResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mPaymentAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
                }
                mProgressDialog.dismiss();
                mAcceptPaymentTask = null;

                break;
            case Constants.COMMAND_REJECT_REQUESTS_MONEY:

                try {
                    mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                            RequestMoneyAcceptRejectOrCancelResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mRequestMoneyAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mRejectRequestTask = null;

                break;
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
            private View mServiceChargeHolder;
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
                mServiceChargeHolder = itemView.findViewById(R.id.service_charge_layout);
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
                if (mInvoiceItemList == null || mInvoiceItemList.size() == 0) {
                    headerView.setVisibility(View.GONE);
                }

                if (mReceiverName == null || mReceiverName.isEmpty()) {
                    mNameView.setVisibility(View.GONE);
                } else {
                    mNameView.setText(mReceiverName);
                }

                mMobileNumberView.setText(mReceiverMobileNumber);
                mProfileImageView.setProfilePicture(mPhotoUri, false);
            }

            public void bindViewForFooter() {
                mNetAmount = mAmount.subtract(mVat);

                if (mServiceCharge.compareTo(BigDecimal.ZERO) <= 0) {
                    mServiceChargeHolder.setVisibility(View.GONE);

                } else {
                    mServiceChargeHolder.setVisibility(View.VISIBLE);
                    mServiceChargeView.setText(Utilities.formatTaka(mServiceCharge));
                    mNetAmount = mAmount.subtract(mServiceCharge);

                }
                mNetAmountView.setText(Utilities.formatTaka(mNetAmount));
                mVatView.setText(Utilities.formatTaka(mVat));
                mTotalView.setText(Utilities.formatTaka(mAmount));

                if (mTitle.equals("Invoice")) {
                    mLinearLayoutDescriptionHolder.setVisibility(View.GONE);
                } else {
                    mDescriptionView.setText(mDescription);
                }

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
                                rejectRequestMoney(mRequestId);
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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_make_payment_notification_review_footer, parent, false);
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
            if (mInvoiceItemList == null) return 0;
            if (mInvoiceItemList.size() == 0) return 2;
            if (mInvoiceItemList.size() > 0)
                return 1 + mInvoiceItemList.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (mInvoiceItemList == null) return super.getItemViewType(position);

            if (mInvoiceItemList.size() == 0) {
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
