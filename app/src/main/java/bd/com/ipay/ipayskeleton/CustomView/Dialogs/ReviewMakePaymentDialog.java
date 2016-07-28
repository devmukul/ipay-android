package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

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
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ReviewMakePaymentDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAcceptPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private RecyclerView mReviewRecyclerView;
    private PaymentReviewAdapter paymentReviewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private final List<ItemList> mItemList;
    private final BigDecimal mAmount;
    private BigDecimal mNetAmount;
    private final BigDecimal mVat;
    private final String mReceiverName;
    private final String mReceiverMobileNumber;
    private final String mPhotoUri;
    private final long requestId;
    private final String mTitle;
    private final int mServiceID;

    private ProgressDialog mProgressDialog;
    private final ReviewDialogFinishListener mReviewFinishListener;

    public ReviewMakePaymentDialog(Context context, long moneyRequestId, String receiverMobileNumber, String receiverName, String photoUri, BigDecimal amount,
                                   String title, int serviceID, BigDecimal vat, List<ItemList> itemList, ReviewDialogFinishListener reviewFinishListener) {
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

    private void initializeView() {
        customView(R.layout.fragment_make_payment_notification_review, true);

        View v = this.build().getCustomView();
        autoDismiss(false);

        mReviewRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);
        paymentReviewAdapter = new PaymentReviewAdapter();
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mReviewRecyclerView.setLayoutManager(mLayoutManager);
        mReviewRecyclerView.setAdapter(paymentReviewAdapter);

        mProgressDialog = new ProgressDialog(context);

        positiveText(R.string.make_payment);
        negativeText(R.string.cancel);
    }

    private void initializeButtonActions(final EditText mPinFieldView) {
        onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String pin = mPinFieldView.getText().toString();

                if (pin.isEmpty()) {

                    // We had a problem with focusing the error view. The error message was shown in wrong place instead of the PIN field.
                    // So when the MAKE PAYMENT button is clicked we force scroll to top to make the views attached to the list items to refresh their reference again
                    mLayoutManager.scrollToPosition(0);

                    mPinFieldView.requestFocus();
                    mPinFieldView.setError(getContext().getString(R.string.failed_empty_pin));

                } else {
                    dialog.dismiss();
                    acceptPaymentRequest(requestId, pin);
                }
            }
        });

        onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
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

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.show();
            mAcceptPaymentTask = null;
            if (context != null)
                Toast.makeText(context, R.string.send_money_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST)) {

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

        private static final int NOTIFICATION_REVIEW_LIST_ITEM_VIEW = 2;
        private static final int NOTIFICATION_REVIEW_LIST_HEADER_VIEW = 1;
        private static final int NOTIFICATION_REVIEW_LIST_FOOTER_VIEW = 3;

        public PaymentReviewAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mItemNameView;
            private final TextView mQuantityView;
            private final TextView mAmountView;
            private EditText mPinField;
            private final ProfileImageView mProfileImageView;
            private final TextView mNameView;
            private final TextView mMobileNumberView;
            private final TextView mTitleView;
            private final TextView mNetAmountView;
            private final TextView mVatView;
            private final TextView mTotalView;

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

                mItemNameView.setText(mItemList.get(pos).getItem());
                mQuantityView.setText(mItemList.get(pos).getQuantity().toString());
                mAmountView.setText(Utilities.formatTaka(mItemList.get(pos).getAmount()));
            }

            public void bindViewForHeader() {
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

                mProfileImageView.setProfilePicture(mPhotoUri, false);
            }

            public void bindViewForFooter() {
                mNetAmount = mAmount.subtract(mVat);
                mNetAmountView.setText(Utilities.formatTaka(mNetAmount));
                mVatView.setText(Utilities.formatTaka(mVat));
                mTotalView.setText(Utilities.formatTaka(mAmount));

                mPinField = (EditText) itemView.findViewById(R.id.pin);
                initializeButtonActions(mPinField);
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
            if (mItemList == null) return 0;
            if (mItemList.size() == 0) return 2;
            if (mItemList.size() > 0)
                return 1 + mItemList.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (mItemList == null) return super.getItemViewType(position);

            if (mItemList.size() == 0) {
                if (position == 0) return NOTIFICATION_REVIEW_LIST_HEADER_VIEW;
                else return NOTIFICATION_REVIEW_LIST_FOOTER_VIEW;

            }

            if (mItemList.size() > 0) {
                if (position == 0) return NOTIFICATION_REVIEW_LIST_HEADER_VIEW;
                else if (position == mItemList.size() + 1)
                    return NOTIFICATION_REVIEW_LIST_FOOTER_VIEW;
                else return NOTIFICATION_REVIEW_LIST_ITEM_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}

