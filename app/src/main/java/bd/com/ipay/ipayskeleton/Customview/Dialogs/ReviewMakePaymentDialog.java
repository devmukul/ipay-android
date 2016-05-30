package bd.com.ipay.ipayskeleton.Customview.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Customview.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ReviewMakePaymentDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAcceptRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private HttpRequestPostAsyncTask mAcceptPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private RecyclerView mReviewRecyclerView;
    private PaymentReviewAdapter paymentReviewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ItemList mItemList[];
    public BigDecimal mAmount;
    private BigDecimal mNetAmount;
    private BigDecimal mVat;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long requestId;
    private String mTitle;
    private int mServiceID;

    private ProgressDialog mProgressDialog;
    private ReviewDialogFinishListener mReviewFinishListener;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mTitleView;
    private TextView mNetAmountView;
    private TextView mVatView;
    private TextView mTotalView;
    private EditText mPinField;

    public ReviewMakePaymentDialog(Context context, long moneyRequestId, String receiverMobileNumber, String receiverName, String photoUri, BigDecimal amount,
                                   String title, int serviceID, BigDecimal vat, ItemList[] itemList, ReviewDialogFinishListener reviewFinishListener) {
        super(context);

        this.requestId = moneyRequestId;
        this.mReceiverMobileNumber = receiverMobileNumber;
        this.mReceiverName = receiverName;
        this.mPhotoUri = photoUri;
        this.mVat = vat;
        this.mAmount = amount;
        this.mTitle = title;
        this.mReviewFinishListener = reviewFinishListener;
        this.mServiceID = serviceID;
        this.mItemList = itemList;

        initializeView();
    }

    public void initializeView() {
        customView(R.layout.dialog_make_payment_notification_review, true);

        View v = this.build().getCustomView();

        mReviewRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);
        paymentReviewAdapter = new PaymentReviewAdapter();
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mReviewRecyclerView.setLayoutManager(mLayoutManager);
        mReviewRecyclerView.setAdapter(paymentReviewAdapter);

        mProgressDialog = new ProgressDialog(context);

        positiveText(R.string.make_payment);
        negativeText(R.string.cancel);

        onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String pin = mPinField.getText().toString();
                if (pin.isEmpty())
                    Toast.makeText(context, R.string.failed_empty_pin, Toast.LENGTH_LONG).show();
                else {
                    if (mServiceID == Constants.SERVICE_ID_REQUEST_MONEY)
                        acceptRequestMoney(requestId, pin);
                    else
                        acceptPaymentRequest(requestId, pin);
                }
            }
        });

        onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        });

    }

    private void acceptRequestMoney(long id, String pin) {
        if (mAcceptRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(context.getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id, pin);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mAcceptRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, context);
        mAcceptRequestTask.mHttpResponseListener = this;
        mAcceptRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void acceptPaymentRequest(long id, String pin) {

        if (mAcceptPaymentTask != null) {
            return;
        }

        mProgressDialog.setMessage(context.getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        PaymentAcceptRejectOrCancelRequest mPaymentAcceptRejectOrCancelRequest =
                new PaymentAcceptRejectOrCancelRequest(id, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentAcceptRejectOrCancelRequest);
        mAcceptPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, context);
        mAcceptPaymentTask.mHttpResponseListener = this;
        mAcceptPaymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null) {
            mProgressDialog.show();
            mAcceptRequestTask = null;
            mAcceptPaymentTask = null;
            if (context != null)
                Toast.makeText(context, R.string.send_money_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_REQUESTS_MONEY)) {
            try {
                mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                        RequestMoneyAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (context != null)
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if (mReviewFinishListener != null)
                        mReviewFinishListener.onReviewFinish();

                } else {
                    if (context != null)
                        Toast.makeText(context, mRequestMoneyAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (context != null)
                    Toast.makeText(context, R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mAcceptRequestTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST)) {

            try {
                mPaymentAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                        PaymentAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                    if (context != null)
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if (mReviewFinishListener != null)
                        mReviewFinishListener.onReviewFinish();

                } else {
                    if (context != null)
                        Toast.makeText(context, mPaymentAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (context != null)
                    Toast.makeText(context, R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
            mAcceptPaymentTask = null;

        }
    }

    private class PaymentReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int NOTIFICATION_REVIEW_LIST_ITEM_VIEW = 1;
        private static final int NOTIFICATION_REVIEW_LIST_HEADER_VIEW = 2;
        private static final int NOTIFICATION_REVIEW_LIST_FOOTER_VIEW = 3;

        public PaymentReviewAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mItemNameView;
            private TextView mQuantityView;
            private TextView mAmountView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mItemNameView = (TextView) itemView.findViewById(R.id.textview_item);
                mQuantityView = (TextView) itemView.findViewById(R.id.textview_quantity);
                mAmountView = (TextView) itemView.findViewById(R.id.textview_amount);

                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);
                mTitleView = (TextView) itemView.findViewById(R.id.textview_title);
                mNetAmountView = (TextView) itemView.findViewById(R.id.textview_net_amount);
                mVatView = (TextView) itemView.findViewById(R.id.textview_vat);
                mTotalView = (TextView) itemView.findViewById(R.id.textview_total);
                mPinField = (EditText) itemView.findViewById(R.id.pin);

            }

            public void bindViewForListItem(int pos) {
                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                mItemNameView.setText(mItemList[pos].getItem());
                mQuantityView.setText(Utilities.formatTaka(mItemList[pos].getQuantity()));
                mAmountView.setText(Utilities.formatTaka(mItemList[pos].getAmount()));
            }

            public void bindViewForHeader(int pos) {
                mProfileImageView.setInformation(mPhotoUri, mReceiverName);

                if (mReceiverName == null || mReceiverName.isEmpty()) {
                    mNameView.setVisibility(View.GONE);
                } else {
                    mNameView.setText(mReceiverName);
                }

                mMobileNumberView.setText(mReceiverMobileNumber);

                if (mTitle == null || mTitle.isEmpty()) {
                    mTitleView.setVisibility(View.GONE);
                } else {
                    mTitleView.setText(mTitle);
                }
            }

            public void bindViewForFooter(int pos) {
                mNetAmount = mAmount.subtract(mVat);
                mNetAmountView.setText(Utilities.formatTaka(mNetAmount));
                mVatView.setText(Utilities.formatTaka(mVat));
                mTotalView.setText(Utilities.formatTaka(mAmount));
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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_make_payment_notification_review_header, parent, false);
                ListHeaderViewHolder vh = new ListHeaderViewHolder(v);
                return vh;

            } else if (viewType == NOTIFICATION_REVIEW_LIST_FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_make_payment_notification_review_footer, parent, false);
                ListFooterViewHolder vh = new ListFooterViewHolder(v);
                return vh;

            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_make_payment_notification_review, parent, false);
                ListItemViewHolder vh = new ListItemViewHolder(v);
                return vh;
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
                    vh.bindViewForHeader(position);

                } else if (holder instanceof ListFooterViewHolder) {
                    ListFooterViewHolder vh = (ListFooterViewHolder) holder;
                    vh.bindViewForFooter(position);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mItemList == null) return 0;
            if (mItemList.length > 0)
                return 1 + mItemList.length + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (mItemList == null) return super.getItemViewType(position);

            if (mItemList.length > 0) {
                if (position == 0) return NOTIFICATION_REVIEW_LIST_HEADER_VIEW;

                else if (position == mItemList.length + 1)
                    return NOTIFICATION_REVIEW_LIST_FOOTER_VIEW;

                else return NOTIFICATION_REVIEW_LIST_ITEM_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}

