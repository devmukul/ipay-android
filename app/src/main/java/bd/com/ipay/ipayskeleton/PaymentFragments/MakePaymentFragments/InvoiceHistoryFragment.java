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
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvoiceHistoryFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAcceptPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mReviewRecyclerView;
    private PaymentReviewAdapter paymentReviewAdapter;

    private List<ItemList> mItemList;
    private BigDecimal mAmount;
    private BigDecimal mNetAmount;
    private BigDecimal mVat;
    public BigDecimal mServiceCharge = new BigDecimal(-1);
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private long requestId;
    private String mTitle;
    private int mServiceID;
    private boolean isPinRequired = true;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_make_payment_notification_review, container, false);
        getActivity().setTitle(R.string.make_payment);

        mReviewRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);
        paymentReviewAdapter = new PaymentReviewAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mReviewRecyclerView.setLayoutManager(mLayoutManager);

        mProgressDialog = new ProgressDialog(getActivity());

        Bundle bundle = getArguments();

        this.requestId = bundle.getLong(Constants.MONEY_REQUEST_ID);
        this.mReceiverMobileNumber = bundle.getString(Constants.MOBILE_NUMBER);
        this.mReceiverName = bundle.getString(Constants.NAME);
        this.mPhotoUri = bundle.getString(Constants.PHOTO_URI);
        this.mVat = new BigDecimal(bundle.getString(Constants.VAT));
        this.mAmount = new BigDecimal(bundle.getString(Constants.AMOUNT));
        this.mTitle = bundle.getString(Constants.TITLE);
        this.mServiceID = bundle.getInt(Constants.MONEY_REQUEST_SERVICE_ID);
        this.mItemList = bundle.getParcelableArrayList(Constants.INVOICE_ITEM_NAME_TAG);

        mReviewRecyclerView.setAdapter(paymentReviewAdapter);

        attemptGetServiceCharge();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void attempAccepttPaymentRequestWithPinCheck() {
        if (this.isPinRequired) {
            final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

            pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    acceptPaymentRequest(requestId, pinInputDialogBuilder.getPin());
                }
            });

            pinInputDialogBuilder.build().show();
        } else {
            acceptPaymentRequest(requestId, null);
        }

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
    public void httpResponseReceiver(HttpResponseObject result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mAcceptPaymentTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.send_money_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST)) {

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
            private View mServiceChargeHolder;
            private final TextView mServiceChargeView;
            private final TextView mTotalView;
            private Button mMakePaymentButton;

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
                mServiceChargeHolder = itemView.findViewById(R.id.service_charge_layout);
                mServiceChargeView = (TextView) itemView.findViewById(R.id.textview_service_charge);
                mTotalView = (TextView) itemView.findViewById(R.id.textview_total);
                mMakePaymentButton = (Button) itemView.findViewById(R.id.button_make_payment);
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

                mMakePaymentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attempAccepttPaymentRequestWithPinCheck();
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

