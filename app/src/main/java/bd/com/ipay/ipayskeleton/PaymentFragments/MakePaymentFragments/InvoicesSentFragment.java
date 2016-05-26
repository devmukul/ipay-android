package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Customview.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetPendingPaymentsRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetPendingPaymentsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PendingPaymentClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvoicesSentFragment extends Fragment implements HttpResponseListener {

    private final int ACTION_CANCEL_REQUEST = 0;

    private HttpRequestPostAsyncTask mPendingInvoicesTask = null;
    private GetPendingPaymentsResponse mGetPendingPaymentsResponse;

    private HttpRequestPostAsyncTask mCancelPaymentRequestTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mPendingListRecyclerView;
    private PendingListAdapter mInvoicesSentAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PendingPaymentClass> pendingPaymentClasses;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int historyPageCount = 0;
    private boolean hasNext = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invoice_sent, container, false);
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
    public void onResume() {
        super.onResume();
        if (Utilities.isConnectionAvailable(getActivity())) {
            getInvoicesPendingRequests();
        }
    }

    private void refreshInvoicesPendingList() {
        if (Utilities.isConnectionAvailable(getActivity())) {

            historyPageCount = 0;
            if (pendingPaymentClasses != null)
                pendingPaymentClasses.clear();
            pendingPaymentClasses = null;
            getInvoicesPendingRequests();

        } else if (getActivity() != null)
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    private void getInvoicesPendingRequests() {
        if (mPendingInvoicesTask != null) {
            return;
        }

        GetPendingPaymentsRequest mGetPendingPaymentsRequest = new GetPendingPaymentsRequest(historyPageCount, Constants.SERVICE_ID_REQUEST_INVOICE);
        Gson gson = new Gson();
        String json = gson.toJson(mGetPendingPaymentsRequest);
        mPendingInvoicesTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT,
                Constants.BASE_URL_SM + Constants.URL_GET_SENT_REQUESTS, json, getActivity());
        mPendingInvoicesTask.mHttpResponseListener = this;
        mPendingInvoicesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void cancelRequest(Long id) {
        if (mCancelPaymentRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_cancelling));
        mProgressDialog.show();
        // TODO ask for pin
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id, null);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mCancelPaymentRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CANCEL_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_CANCEL_NOTIFICATION_REQUEST, json, getActivity());
        mCancelPaymentRequestTask.mHttpResponseListener = this;
        mCancelPaymentRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showInvoicesHistoryDialogue(String title, String description, String time, long id, String item, String itemDecription,
                                             BigDecimal rate, BigDecimal quantity, BigDecimal amount, BigDecimal vat, BigDecimal total, int status) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.invoice_details)
                .customView(R.layout.dialog_sent_invoice_details, true)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        View view = dialog.getCustomView();
        final TextView titleTextView = (TextView) view.findViewById(R.id.title);
        final TextView descriptionTextView = (TextView) view.findViewById(R.id.description);
        final TextView timeTextView = (TextView) view.findViewById(R.id.time);
        final TextView invoiceIDTextView = (TextView) view.findViewById(R.id.invoice_id);
        final TextView itemTextView = (TextView) view.findViewById(R.id.item);
        final TextView itemDescriptionTextView = (TextView) view.findViewById(R.id.description1);
        final TextView rateTextView = (TextView) view.findViewById(R.id.rate);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        final TextView amountTextView = (TextView) view.findViewById(R.id.amount);
        final TextView vatTextView = (TextView) view.findViewById(R.id.vat);
        final TextView totalTextView = (TextView) view.findViewById(R.id.total);
        final TextView statusTextView = (TextView) view.findViewById(R.id.status);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        timeTextView.setText(time);
        invoiceIDTextView.setText(getString(R.string.invoice_id) + " " + String.valueOf(id));
        itemTextView.setText(item);
        itemDescriptionTextView.setText(itemDecription);
        rateTextView.setText(Utilities.formatTaka(rate));
        quantityTextView.setText(Utilities.formatTaka(quantity));
        amountTextView.setText(Utilities.formatTaka(total));
        vatTextView.setText(Utilities.formatTaka(vat));
        totalTextView.setText(Utilities.formatTaka(amount));

        if (status == Constants.HTTP_RESPONSE_STATUS_OK) {
            statusTextView.setText(getString(R.string.transaction_successful));
            statusTextView.setTextColor(getResources().getColor(R.color.bottle_green));

        } else if (status == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
            statusTextView.setText(getString(R.string.in_progress));
            statusTextView.setTextColor(getResources().getColor(R.color.background_yellow));

        } else if (status == Constants.HTTP_RESPONSE_STATUS_REJECTED) {
            statusTextView.setText(getString(R.string.transaction_failed));
            statusTextView.setTextColor(getResources().getColor(R.color.background_red));
        } else if (status == Constants.HTTP_RESPONSE_STATUS_CANCELED) {
            statusTextView.setText(getString(R.string.transaction_failed));
            statusTextView.setTextColor(Color.GRAY);
        }
        else if (status == Constants.HTTP_RESPONSE_STATUS_DRAFT) {
            statusTextView.setText(getString(R.string.draft));
            statusTextView.setTextColor(Color.GRAY);
        }
    }


    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mPendingInvoicesTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT)) {


            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {

                    mGetPendingPaymentsResponse = gson.fromJson(result.getJsonString(), GetPendingPaymentsResponse.class);

                    if (pendingPaymentClasses == null) {
                        pendingPaymentClasses = mGetPendingPaymentsResponse.getRequests();
                    } else {
                        List<PendingPaymentClass> tempPendingMoneyRequestClasses;
                        tempPendingMoneyRequestClasses = mGetPendingPaymentsResponse.getRequests();
                        pendingPaymentClasses.addAll(tempPendingMoneyRequestClasses);
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

        } else if (result.getApiCommand().equals(Constants.COMMAND_CANCEL_PAYMENT_REQUEST)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    mPaymentAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                            PaymentAcceptRejectOrCancelResponse.class);
                    String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                    // Refresh the pending list
                    if (pendingPaymentClasses != null)
                        pendingPaymentClasses.clear();
                    pendingPaymentClasses = null;
                    historyPageCount = 0;
                    getInvoicesPendingRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_cancel_money_request, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.could_not_cancel_money_request, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mCancelPaymentRequestTask = null;
        }
    }

    public class PendingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public PendingListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mSenderName;
            private TextView mAmount;
            private TextView mDescription;
            private TextView mTime;
            private ImageView mCancel;
            private RoundedImageView mPortrait;
            private ImageView statusView;
            private TextView mPortraitTextView;
            private TextView loadMoreTextView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mSenderName = (TextView) itemView.findViewById(R.id.request_name);
                mAmount = (TextView) itemView.findViewById(R.id.amount);
                mDescription = (TextView) itemView.findViewById(R.id.description);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mCancel = (ImageView) itemView.findViewById(R.id.cancel_request);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
                mPortraitTextView = (TextView) itemView.findViewById(R.id.portraitTxt);
                statusView = (ImageView) itemView.findViewById(R.id.status);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
            }

            private void setProfilePicture(String url, RoundedImageView pictureView, String name) {

                int position = getAdapterPosition();
                final int randomColor = position % 10;

                if (name.startsWith("+") && name.length() > 1)
                    mPortraitTextView.setText(String.valueOf(name.substring(1).charAt(0)).toUpperCase());
                else mPortraitTextView.setText(String.valueOf(name.charAt(0)).toUpperCase());


                if (randomColor == 0)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle);
                else if (randomColor == 1)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_blue);
                else if (randomColor == 2)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_brightpink);
                else if (randomColor == 3)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_cyan);
                else if (randomColor == 4)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_megenta);
                else if (randomColor == 5)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_orange);
                else if (randomColor == 6)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_red);
                else if (randomColor == 7)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_springgreen);
                else if (randomColor == 8)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_violet);
                else if (randomColor == 9)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_yellow);
                else
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_azure);

                if (url != null) {
                    url = Constants.BASE_URL_IMAGE_SERVER + url;
                    Glide.with(getActivity())
                            .load(url)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(pictureView);
                } else {
                    Glide.with(getActivity())
                            .load(android.R.color.transparent)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(pictureView);
                }

            }


            public void bindView(int pos) {

                String imageUrl = pendingPaymentClasses.get(pos).getReceiverProfile().getUserProfilePicture();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(pendingPaymentClasses.get(pos).getRequestTime());
                String name = pendingPaymentClasses.get(pos).getReceiverProfile().getUserName();
                final int status = pendingPaymentClasses.get(pos).getStatus();
                final BigDecimal amount = pendingPaymentClasses.get(pos).getAmount();
                final BigDecimal vat = pendingPaymentClasses.get(pos).getVat();
                final BigDecimal rate = pendingPaymentClasses.get(pos).getRate();
                final BigDecimal total = pendingPaymentClasses.get(pos).getTotal();
                final BigDecimal quantity = pendingPaymentClasses.get(pos).getQuantity();
                final String description = pendingPaymentClasses.get(pos).getDescription();
                final String title = pendingPaymentClasses.get(pos).getTitle();
                final String itemDescription = pendingPaymentClasses.get(pos).getItemDescription();
                final String item = pendingPaymentClasses.get(pos).getItem();
                final long id = pendingPaymentClasses.get(pos).getId();

                setProfilePicture(imageUrl, mPortrait, name);

                mSenderName.setText(name);

                if (status == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mSenderName.setTextColor(Color.GREEN);
                    statusView.setColorFilter(Color.GREEN);
                    statusView.setImageResource(R.drawable.ic_check_circle_black_24dp);

                } else if (status == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
                    mSenderName.setTextColor(getResources().getColor(R.color.background_yellow));
                    statusView.setColorFilter(getResources().getColor(R.color.background_yellow));
                    statusView.setImageResource(R.drawable.ic_cached_black_24dp);

                } else if (status == Constants.HTTP_RESPONSE_STATUS_REJECTED) {
                    mSenderName.setTextColor(getResources().getColor(R.color.background_red));
                    statusView.setColorFilter(Color.RED);
                    statusView.setImageResource(R.drawable.ic_error_black_24dp);
                } else if (status == Constants.HTTP_RESPONSE_STATUS_CANCELED) {
                    mSenderName.setTextColor(Color.GRAY);
                    statusView.setColorFilter(Color.GRAY);
                    statusView.setImageResource(R.drawable.ic_error_black_24dp);
                }



                mAmount.setText(pendingPaymentClasses.get(pos).getAmount().toBigInteger().toString());
                mDescription.setText(description);
                mTime.setText(time);

                if (status == Constants.HTTP_RESPONSE_STATUS_PROCESSING) mCancel.setVisibility(View.VISIBLE);
                else mCancel.setVisibility(View.INVISIBLE);

                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialogue(getString(R.string.cancel_money_request_confirm), ACTION_CANCEL_REQUEST, id);
                    }
                });

                Glide.with(getActivity())
                        .load(Constants.BASE_URL_IMAGE_SERVER + imageUrl)
                        .crossFade()
                        .error(R.drawable.ic_person)
                        .transform(new CircleTransform(getActivity()))
                        .into(mPortrait);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mSwipeRefreshLayout.isRefreshing())
                            showInvoicesHistoryDialogue(title, description, time, id, item, itemDescription, rate, quantity, amount, vat, total, status);
                    }
                });

            }

            public void bindViewFooter(int pos) {
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

                FooterViewHolder vh = new FooterViewHolder(v);

                return vh;
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_invoices_sent, parent, false);

            NormalViewHolder vh = new NormalViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                if (holder instanceof NormalViewHolder) {
                    NormalViewHolder vh = (NormalViewHolder) holder;
                    vh.bindView(position);

                } else if (holder instanceof FooterViewHolder) {
                    FooterViewHolder vh = (FooterViewHolder) holder;
                    vh.bindViewFooter(position);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (pendingPaymentClasses != null)
                return pendingPaymentClasses.size()+1;
            else return 0;
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

    private void showAlertDialogue(String msg, final int action, final long id) {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
        alertDialogue.setTitle(R.string.confirm_query);
        alertDialogue.setMessage(msg);

        alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (action == ACTION_CANCEL_REQUEST)
                    cancelRequest(id);

            }
        });

        alertDialogue.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        alertDialogue.show();
    }
}
