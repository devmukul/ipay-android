package bd.com.ipay.ipayskeleton.EventFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.EventActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.GetMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.GetRequestResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.MoneyRequestClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MyTicketsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetMyTicketsListTask = null;
    // TODO: Change the response class
    private GetRequestResponse mGetPendingRequestResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mTicketsListRecyclerView;
    private MyEventsListAdapter mMyTicketsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // TODO: Change the class name
    private List<MoneyRequestClass> listOfMyTickets;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private int pageCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_tickets, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mTicketsListRecyclerView = (RecyclerView) v.findViewById(R.id.list_my_tickets);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mMyTicketsAdapter = new MyEventsListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTicketsListRecyclerView.setLayoutManager(mLayoutManager);
        mTicketsListRecyclerView.setAdapter(mMyTicketsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshTicketList();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utilities.isConnectionAvailable(getActivity())) {
            getMyTicketsList();
        }
    }

    // TODO: modify this function
    private void refreshTicketList() {
        if (Utilities.isConnectionAvailable(getActivity())) {

            pageCount = 0;
            if (listOfMyTickets != null)
                listOfMyTickets.clear();
            listOfMyTickets = null;
            getMyTicketsList();

        } else if (getActivity() != null)
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    // TODO: modify the function with proper url and request POJO
    private void getMyTicketsList() {
        if (mGetMyTicketsListTask != null) {
            return;
        }

        GetMoneyRequest mUserActivityRequest = new GetMoneyRequest(pageCount, Constants.SERVICE_ID_REQUEST_MONEY);
        Gson gson = new Gson();
        String json = gson.toJson(mUserActivityRequest);
        mGetMyTicketsListTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_REQUESTS_ME,
                Constants.BASE_URL_SM + Constants.URL_GET_SENT_REQUESTS, json, getActivity());
        mGetMyTicketsListTask.mHttpResponseListener = this;
        mGetMyTicketsListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetMyTicketsListTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        // TODO: Change command
        if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_REQUESTS_ME)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {

                    mGetPendingRequestResponse = gson.fromJson(result.getJsonString(), GetRequestResponse.class);

                    if (listOfMyTickets == null) {
                        listOfMyTickets = mGetPendingRequestResponse.getAllNotifications();
                    } else {
                        List<MoneyRequestClass> tempPendingMoneyRequestClasses;
                        tempPendingMoneyRequestClasses = mGetPendingRequestResponse.getAllNotifications();
                        listOfMyTickets.addAll(tempPendingMoneyRequestClasses);
                    }

                    mMyTicketsAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.events_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.events_get_failed, Toast.LENGTH_LONG).show();
            }


            if (this.isAdded()) setContentShown(true);
            mSwipeRefreshLayout.setRefreshing(false);
            mGetMyTicketsListTask = null;

        }

        if (listOfMyTickets != null && listOfMyTickets.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private class MyEventsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public MyEventsListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mSenderNumber;
            private final TextView mTime;
            private final TextView mDescription;
            private final ProfileImageView mProfileImageView;
            private final View divider;

            public ViewHolder(final View itemView) {
                super(itemView);

                mSenderNumber = (TextView) itemView.findViewById(R.id.request_number);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mDescription = (TextView) itemView.findViewById(R.id.description);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                divider = itemView.findViewById(R.id.divider);
            }

            public void bindView(int pos) {


                if (pos == listOfMyTickets.size() - 1) divider.setVisibility(View.GONE);

                final long id = listOfMyTickets.get(pos).getId();
                String time = Utilities.getDateFormat(listOfMyTickets.get(pos).getRequestTime());
                final String name = listOfMyTickets.get(pos).getReceiverProfile().getUserName();
                String imageUrl = listOfMyTickets.get(pos).getReceiverProfile().getUserProfilePicture();
                mTime.setText(time);
                mSenderNumber.setText(name);
                mDescription.setText(listOfMyTickets.get(pos).getDescription());

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: Change the parameters here
                        ((EventActivity) getActivity()).switchToTicketQRCode(id, id, name);
                    }
                });

            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            // TODO: modify the list item layout as per requirement
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_events, parent, false);

            return new ViewHolder(v);
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
            if (listOfMyTickets != null)
                return listOfMyTickets.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
