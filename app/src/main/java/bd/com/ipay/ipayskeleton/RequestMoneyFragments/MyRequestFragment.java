package bd.com.ipay.ipayskeleton.RequestMoneyFragments;

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
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.GetPendingRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.GetPendingRequestResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.PendingMoneyRequestClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MyRequestFragment extends Fragment implements HttpResponseListener {

    private final int ACTION_CANCEL_REQUEST = 0;

    private HttpRequestPostAsyncTask mPendingRequestTask = null;
    private GetPendingRequestResponse mGetPendingRequestResponse;

    private HttpRequestPostAsyncTask mCancelRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mPendingListRecyclerView;
    private PendingListAdapter mOtherRequestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PendingMoneyRequestClass> pendingMoneyRequestClasses;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int historyPageCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_requests, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mPendingListRecyclerView = (RecyclerView) v.findViewById(R.id.list_my_requests);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mOtherRequestsAdapter = new PendingListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPendingListRecyclerView.setLayoutManager(mLayoutManager);
        mPendingListRecyclerView.setAdapter(mOtherRequestsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshPendingList();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utilities.isConnectionAvailable(getActivity())) {
            getPendingRequests();
        }
    }

    private void refreshPendingList() {
        if (Utilities.isConnectionAvailable(getActivity())) {

            historyPageCount = 0;
            if (pendingMoneyRequestClasses != null)
                pendingMoneyRequestClasses.clear();
            pendingMoneyRequestClasses = null;
            getPendingRequests();

        } else if (getActivity() != null)
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    private void getPendingRequests() {
        if (mPendingRequestTask != null) {
            return;
        }

        GetPendingRequest mUserActivityRequest = new GetPendingRequest(null, historyPageCount);
        Gson gson = new Gson();
        String json = gson.toJson(mUserActivityRequest);
        mPendingRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_REQUESTS_ME,
                Constants.BASE_URL_SM + Constants.URL_PENDING_REQUEST_MONEY_FROM_ME, json, getActivity());
        mPendingRequestTask.mHttpResponseListener = this;
        mPendingRequestTask.execute((Void) null);
    }

    private void cancelRequest(Long id) {
        if (mCancelRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_cancelling));
        mProgressDialog.show();
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mCancelRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CANCEL_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_REQUEST_CANCEL, json, getActivity());
        mCancelRequestTask.mHttpResponseListener = this;
        mCancelRequestTask.execute((Void) null);
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mPendingRequestTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_PENDING_REQUESTS_ME)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {

                        mGetPendingRequestResponse = gson.fromJson(resultList.get(2), GetPendingRequestResponse.class);

                        if (pendingMoneyRequestClasses == null) {
                            pendingMoneyRequestClasses = mGetPendingRequestResponse.getRequests();
                        } else {
                            List<PendingMoneyRequestClass> tempPendingMoneyRequestClasses;
                            tempPendingMoneyRequestClasses = mGetPendingRequestResponse.getRequests();
                            pendingMoneyRequestClasses.addAll(tempPendingMoneyRequestClasses);
                        }

                        mOtherRequestsAdapter.notifyDataSetChanged();

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
            mPendingRequestTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_CANCEL_REQUESTS_MONEY)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                                RequestMoneyAcceptRejectOrCancelResponse.class);
                        String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        // Refresh the pending list
                        if (pendingMoneyRequestClasses != null)
                            pendingMoneyRequestClasses.clear();
                        pendingMoneyRequestClasses = null;
                        historyPageCount = 0;
                        getPendingRequests();
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
            mCancelRequestTask = null;
        }
    }

    public class PendingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public PendingListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mSenderNumber;
            private TextView mAmount;
            private TextView mTime;
            private TextView mDescription;
            private ImageView mCancel;
            private RoundedImageView mPortrait;

            public ViewHolder(final View itemView) {
                super(itemView);

                mSenderNumber = (TextView) itemView.findViewById(R.id.request_number);
                mAmount = (TextView) itemView.findViewById(R.id.amount);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mDescription = (TextView) itemView.findViewById(R.id.description);
                mCancel = (ImageView) itemView.findViewById(R.id.cancel_request);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
            }

            public void bindView(int pos) {

                final long id = pendingMoneyRequestClasses.get(pos).getId();
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, H:MM a").format(pendingMoneyRequestClasses.get(pos).getRequestTime());
                mAmount.setText(pendingMoneyRequestClasses.get(pos).getAmount() + " BDT");
                mTime.setText(time);
                mSenderNumber.setText(pendingMoneyRequestClasses.get(pos).getReceiverMobileNumber());
                mDescription.setText(pendingMoneyRequestClasses.get(pos).getDescription());

                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialogue(getString(R.string.cancel_money_request_confirm), ACTION_CANCEL_REQUEST, id);
                    }
                });

                // TODO: profile pic fetch change hobe
                Glide.with(getActivity())
                        .load(Constants.BASE_URL_IMAGE_SERVER + "/image/"
                                + pendingMoneyRequestClasses.get(pos).getReceiverMobileNumber().replaceAll("[^0-9]", "")
                                + ".jpg")
                        .placeholder(R.drawable.ic_face_black_24dp)
                        .into(mPortrait);

            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pending_request_money_me, parent, false);

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
            if (pendingMoneyRequestClasses != null)
                return pendingMoneyRequestClasses.size();
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
