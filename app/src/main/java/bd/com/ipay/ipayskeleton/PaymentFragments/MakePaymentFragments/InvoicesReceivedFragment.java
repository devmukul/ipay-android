package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetPendingPaymentsRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.GetPendingPaymentsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PendingPaymentClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvoicesReceivedFragment extends Fragment implements HttpResponseListener {

    private final int ACTION_ACCEPT_REQUEST = 0;
    private final int ACTION_REJECT_REQUEST = 1;

    private HttpRequestPostAsyncTask mPendingPaymentsRequestTask = null;
    private GetPendingPaymentsResponse mGetPendingPaymentsResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private HttpRequestPostAsyncTask mRejectPaymentTask = null;
    private HttpRequestPostAsyncTask mAcceptPaymentTask = null;
    private PaymentAcceptRejectOrCancelResponse mPaymentAcceptRejectOrCancelResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mPendingListRecyclerView;
    private PendingListAdapter mPaymentRequestsReceivedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PendingPaymentClass> pendingPaymentRequestClasses;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int historyPageCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invoice_received, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mPendingListRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice_received);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mPaymentRequestsReceivedAdapter = new PendingListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPendingListRecyclerView.setLayoutManager(mLayoutManager);
        mPendingListRecyclerView.setAdapter(mPaymentRequestsReceivedAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshPendingPaymentList();
                }
            }
        });

        if (Utilities.isConnectionAvailable(getActivity()))
            attemptGetServiceCharge();
        else
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utilities.isConnectionAvailable(getActivity())) {
            getPendingPaymentRequests();
        }
    }

    private void attemptGetServiceCharge() {
        if (mServiceChargeTask != null) {
            return;
        }

        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Context.MODE_PRIVATE);
        int accountType = pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
        int accountClass = Constants.DEFAULT_USER_CLASS;

        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();
        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(Constants.SERVICE_ID_MAKE_PAYMENT, accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.execute((Void) null);
    }

    private void refreshPendingPaymentList() {
        if (Utilities.isConnectionAvailable(getActivity())) {

            historyPageCount = 0;
            if (pendingPaymentRequestClasses != null)
                pendingPaymentRequestClasses.clear();
            pendingPaymentRequestClasses = null;
            getPendingPaymentRequests();

        } else if (getActivity() != null)
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    private void getPendingPaymentRequests() {
        if (mPendingPaymentsRequestTask != null) {
            return;
        }

        GetPendingPaymentsRequest mGetPendingPaymentsRequest = new GetPendingPaymentsRequest(historyPageCount, Constants.SERVICE_ID_REQUEST_INVOICE);
        Gson gson = new Gson();
        String json = gson.toJson(mGetPendingPaymentsRequest);
        mPendingPaymentsRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_PAYMENT_REQUESTS_RECEIVED,
                Constants.BASE_URL + Constants.URL_GET_NOTIFICATIONS, json, getActivity());
        mPendingPaymentsRequestTask.mHttpResponseListener = this;
        mPendingPaymentsRequestTask.execute((Void) null);
    }

    private void rejectPaymentRequest(Long id) {

        if (mRejectPaymentTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_rejecting));
        mProgressDialog.show();
        PaymentAcceptRejectOrCancelRequest mPaymentAcceptRejectOrCancelRequest =
                new PaymentAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentAcceptRejectOrCancelRequest);
        mRejectPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REJECT_PAYMENT_REQUEST,
                Constants.BASE_URL + Constants.URL_REJECT_NOTIFICATION_REQUEST, json, getActivity());
        mRejectPaymentTask.mHttpResponseListener = this;
        mRejectPaymentTask.execute((Void) null);
    }

    private void acceptPaymentRequest(Long id) {

        if (mAcceptPaymentTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        PaymentAcceptRejectOrCancelRequest mPaymentAcceptRejectOrCancelRequest =
                new PaymentAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentAcceptRejectOrCancelRequest);
        mAcceptPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST,
                Constants.BASE_URL + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, getActivity());
        mAcceptPaymentTask.mHttpResponseListener = this;
        mAcceptPaymentTask.execute((Void) null);
    }

    private void showAlertDialogue(String msg, final int action, final long id, BigDecimal amount) {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
        alertDialogue.setTitle(R.string.confirm_query);

        String serviceChargeDescription = "";

        if (mGetServiceChargeResponse != null) {
            if (mGetServiceChargeResponse.getServiceCharge(amount).compareTo(new BigDecimal(0)) > 0)
                serviceChargeDescription = "You'll be charged " + mGetServiceChargeResponse.getServiceCharge(amount) + " Tk. for this transaction.";
            else if (mGetServiceChargeResponse.getServiceCharge(amount).compareTo(new BigDecimal(0)) == 0)
                serviceChargeDescription = getString(R.string.no_extra_charges);
            else {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                return;
            }

        } else {
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        if (action == ACTION_ACCEPT_REQUEST) msg = serviceChargeDescription + "\n" + msg;
        alertDialogue.setMessage(msg);

        alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (action == ACTION_ACCEPT_REQUEST) {

                    acceptPaymentRequest(id);

                } else if (action == ACTION_REJECT_REQUEST) {

                    rejectPaymentRequest(id);
                }
            }
        });

        alertDialogue.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        alertDialogue.show();
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            mPendingPaymentsRequestTask = null;
            mRejectPaymentTask = null;
            mAcceptPaymentTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_PENDING_PAYMENT_REQUESTS_RECEIVED)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mGetPendingPaymentsResponse = gson.fromJson(resultList.get(2), GetPendingPaymentsResponse.class);

                        if (pendingPaymentRequestClasses == null) {
                            pendingPaymentRequestClasses = mGetPendingPaymentsResponse.getRequests();
                        } else {
                            List<PendingPaymentClass> tempPendingMoneyRequestClasses;
                            tempPendingMoneyRequestClasses = mGetPendingPaymentsResponse.getRequests();
                            pendingPaymentRequestClasses.addAll(tempPendingMoneyRequestClasses);
                        }

                        mPaymentRequestsReceivedAdapter.notifyDataSetChanged();
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
            mPendingPaymentsRequestTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_REJECT_PAYMENT_REQUEST)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mPaymentAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                                PaymentAcceptRejectOrCancelResponse.class);
                        String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        // Refresh the pending list
                        if (pendingPaymentRequestClasses != null)
                            pendingPaymentRequestClasses.clear();
                        pendingPaymentRequestClasses = null;
                        historyPageCount = 0;
                        getPendingPaymentRequests();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mRejectPaymentTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mPaymentAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                                PaymentAcceptRejectOrCancelResponse.class);
                        String message = mPaymentAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        // Refresh the pending list
                        if (pendingPaymentRequestClasses != null)
                            pendingPaymentRequestClasses.clear();
                        pendingPaymentRequestClasses = null;
                        historyPageCount = 0;
                        getPendingPaymentRequests();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mAcceptPaymentTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_SERVICE_CHARGE)) {
            if (resultList.size() > 2) {
                try {
                    mGetServiceChargeResponse = gson.fromJson(resultList.get(2), GetServiceChargeResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        // Do nothing
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mServiceChargeTask = null;
        }
    }

    public class PendingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public PendingListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mSenderNumber;
            private TextView mDescription;
            private TextView mTime;
            private TextView mTitle;
            private ImageView mCancel;
            private ImageView mAccept;
            private RoundedImageView mPortrait;

            public ViewHolder(final View itemView) {
                super(itemView);

                mSenderNumber = (TextView) itemView.findViewById(R.id.request_number);
                mDescription = (TextView) itemView.findViewById(R.id.description);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mTitle = (TextView) itemView.findViewById(R.id.description);
                mCancel = (ImageView) itemView.findViewById(R.id.cancel_request);
                mAccept = (ImageView) itemView.findViewById(R.id.accept_request);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
            }

            public void bindView(int pos) {

                final long id = pendingPaymentRequestClasses.get(pos).getId();
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(pendingPaymentRequestClasses.get(pos).getRequestTime());
                String imageUrl = pendingPaymentRequestClasses.get(pos).getOriginatorProfile().getUserProfilePicture();
                final BigDecimal amount = pendingPaymentRequestClasses.get(pos).getAmount();

                mDescription.setText(pendingPaymentRequestClasses.get(pos).getDescription());
                mTime.setText(time);
                mSenderNumber.setText(pendingPaymentRequestClasses.get(pos).getOriginatorProfile().getUserName());
                mTitle.setText(pendingPaymentRequestClasses.get(pos).getTitle());

                mAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialogue(getString(R.string.accept_money_request_confirm), ACTION_ACCEPT_REQUEST, id, amount);
                    }
                });

                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialogue(getString(R.string.reject_money_request_confirm), ACTION_REJECT_REQUEST, id, amount);
                    }
                });

                Glide.with(getActivity())
                        .load(Constants.BASE_URL_IMAGE_SERVER + imageUrl)
                        .crossFade()
                        .error(R.drawable.ic_person)
                        .transform(new CircleTransform(getActivity()))
                        .into(mPortrait);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_payments_received,
                    parent, false);

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
            if (pendingPaymentRequestClasses != null)
                return pendingPaymentRequestClasses.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
