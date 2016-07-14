package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.NotificationClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class SingleInvoiceFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetSingleInvoiceTask = null;
    private NotificationClass mGetSingleInvoiceResponse;
    private ProgressDialog mProgressDialog;

    private RecyclerView mReviewRecyclerView;
    private PaymentReviewAdapter paymentReviewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private HttpRequestPostAsyncTask mAcceptPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private List<ItemList> mItemList;
    private BigDecimal mAmount;
    private BigDecimal mNetAmount;
    private BigDecimal mVat;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long mMoneyRequestId;
    private String mTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.dialog_make_payment_notification_review, container, false);
        mProgressDialog = new ProgressDialog(getActivity());

        mReviewRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);

        String result = getArguments().getString(Constants.RESULT);
        getSingleInvoice(Integer.parseInt(result));

        paymentReviewAdapter = new PaymentReviewAdapter();
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mReviewRecyclerView.setLayoutManager(mLayoutManager);

        return v;
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

    private void acceptPaymentRequest(long id, String pin) {

        if (mAcceptPaymentTask != null) {
            return;
        }

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        PaymentAcceptRejectOrCancelRequest mPaymentAcceptRejectOrCancelRequest =
                new PaymentAcceptRejectOrCancelRequest(id, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentAcceptRejectOrCancelRequest);
        mAcceptPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, getActivity());
        mAcceptPaymentTask.mHttpResponseListener = this;
        mAcceptPaymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_notification_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_SINGLE_INVOICE)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mGetSingleInvoiceResponse = gson.fromJson(result.getJsonString(), NotificationClass.class);
                    mMoneyRequestId = mGetSingleInvoiceResponse.getId();
                    mAmount = mGetSingleInvoiceResponse.getAmount();
                    mReceiverName = mGetSingleInvoiceResponse.originatorProfile.getUserName();
                    mReceiverMobileNumber = mGetSingleInvoiceResponse.originatorProfile.getUserMobileNumber();
                    mPhotoUri = mGetSingleInvoiceResponse.originatorProfile.getUserProfilePicture();
                    mTitle = mGetSingleInvoiceResponse.getTitle();
                    mVat = mGetSingleInvoiceResponse.getVat();
                    mItemList = mGetSingleInvoiceResponse.getItemList();

                    mReviewRecyclerView.setAdapter(paymentReviewAdapter);

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
            mProgressDialog.dismiss();

        } else if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST)) {

            try {
                mPaymentAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                        PaymentAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();


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
            getActivity().finish();
        }
    }

    private class PaymentReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int NOTIFICATION_REVIEW_LIST_ITEM_VIEW = 2;
        private static final int NOTIFICATION_REVIEW_LIST_HEADER_VIEW = 1;
        private static final int NOTIFICATION_REVIEW_LIST_FOOTER_VIEW = 3;

        public PaymentReviewAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mItemNameView;
            private TextView mQuantityView;
            private TextView mAmountView;
            private EditText mPinField;
            private ProfileImageView mProfileImageView;
            private TextView mNameView;
            private TextView mMobileNumberView;
            private TextView mTitleView;
            private TextView mNetAmountView;
            private TextView mVatView;
            private TextView mTotalView;
            private Button mMakePaymentButton;

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
                mMakePaymentButton = (Button) itemView.findViewById(R.id.button_make_payment);
            }

            public void bindViewForListItem(int pos) {
                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                mItemNameView.setText(mItemList.get(pos).getItem());
                mQuantityView.setText(mItemList.get(pos).getQuantity().toString());
                mAmountView.setText(Utilities.formatTaka(mItemList.get(pos).getAmount()));
            }

            public void bindViewForHeader(int pos) {
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

                mProfileImageView.setInformation(mReceiverMobileNumber, mPhotoUri, mReceiverName, false);
            }

            public void bindViewForFooter(int pos) {
                mNetAmount = mAmount.subtract(mVat);
                mNetAmountView.setText(Utilities.formatTaka(mNetAmount));
                mVatView.setText(Utilities.formatTaka(mVat));
                mTotalView.setText(Utilities.formatTaka(mAmount));
                mPinField = (EditText) itemView.findViewById(R.id.pin);

                mMakePaymentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String pin = mPinField.getText().toString();

                        if (pin.isEmpty()) {

                            // We had a problem with focusing the error view. The error message was shown in wrong place instead of the PIN field.
                            // So when the MAKE PAYMENT button is clicked we force scroll to top to make the views attached to the list items to refresh their reference again
                            mLayoutManager.scrollToPosition(0);

                            View focusView = mPinField;
                            focusView.requestFocus();
                            mPinField.setError(getActivity().getString(R.string.failed_empty_pin));

                        } else {
                            acceptPaymentRequest(mMoneyRequestId, pin);
                        }
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
