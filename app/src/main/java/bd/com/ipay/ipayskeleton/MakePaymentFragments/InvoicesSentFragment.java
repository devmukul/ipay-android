package bd.com.ipay.ipayskeleton.MakePaymentFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetPendingPaymentsRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetPendingPaymentsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PendingPaymentClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.R;
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

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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

        GetPendingPaymentsRequest mGetPendingPaymentsRequest = new GetPendingPaymentsRequest(null, historyPageCount);
        Gson gson = new Gson();
        String json = gson.toJson(mGetPendingPaymentsRequest);
        mPendingInvoicesTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT,
                Constants.BASE_URL_SM + Constants.URL_PENDING_PAYMENT_REQUEST_SENT, json, getActivity());
        mPendingInvoicesTask.mHttpResponseListener = this;
        mPendingInvoicesTask.execute((Void) null);
    }

    private void cancelRequest(Long id) {
        if (mCancelPaymentRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_cancelling));
        mProgressDialog.show();
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mCancelPaymentRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CANCEL_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_REQUEST_CANCEL, json, getActivity());
        mCancelPaymentRequestTask.mHttpResponseListener = this;
        mCancelPaymentRequestTask.execute((Void) null);
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mPendingInvoicesTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_PENDING_PAYMENT_REQUESTS_SENT)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {

                        mGetPendingPaymentsResponse = gson.fromJson(resultList.get(2), GetPendingPaymentsResponse.class);

                        if (pendingPaymentClasses == null) {
                            pendingPaymentClasses = mGetPendingPaymentsResponse.getRequests();
                        } else {
                            List<PendingPaymentClass> tempPendingMoneyRequestClasses;
                            tempPendingMoneyRequestClasses = mGetPendingPaymentsResponse.getRequests();
                            pendingPaymentClasses.addAll(tempPendingMoneyRequestClasses);
                        }

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
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();

            mSwipeRefreshLayout.setRefreshing(false);
            mPendingInvoicesTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_CANCEL_PAYMENT_REQUEST)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mPaymentAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
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
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.could_not_cancel_money_request, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mCancelPaymentRequestTask = null;
        }
    }

    public class PendingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public PendingListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mSenderNumber;
            private TextView mAmount;
            private TextView mTime;
            private TextView mTitleOfProduct;
            private ImageView mCancel;
            private RoundedImageView mPortrait;

            public ViewHolder(final View itemView) {
                super(itemView);

                mSenderNumber = (TextView) itemView.findViewById(R.id.request_number);
                mAmount = (TextView) itemView.findViewById(R.id.amount);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mTitleOfProduct = (TextView) itemView.findViewById(R.id.description);
                mCancel = (ImageView) itemView.findViewById(R.id.cancel_request);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
            }

            public void bindView(int pos) {

                final long id = pendingPaymentClasses.get(pos).getId();
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, H:MM a").format(pendingPaymentClasses.get(pos).getRequestTime());
                mAmount.setText(pendingPaymentClasses.get(pos).getAmount() + " BDT");
                mTime.setText(time);
                mSenderNumber.setText(pendingPaymentClasses.get(pos).getReceiverMobileNumber());
                mTitleOfProduct.setText(pendingPaymentClasses.get(pos).getTitle());

                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialogue(getString(R.string.cancel_money_request_confirm), ACTION_CANCEL_REQUEST, id);
                    }
                });

                // TODO: profile pic fetch change hobe
                Glide.with(getActivity())
                        .load(Constants.BASE_URL_IMAGE_SERVER + "/image/"
                                + pendingPaymentClasses.get(pos).getReceiverMobileNumber().replaceAll("[^0-9]", "")
                                + ".jpg")
                        .placeholder(R.drawable.ic_face_black_24dp)
                        .into(mPortrait);

            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_invoices_sent, parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                ViewHolder vh = (ViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (pendingPaymentClasses != null)
                return pendingPaymentClasses.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
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
