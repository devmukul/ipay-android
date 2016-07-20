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

import java.text.SimpleDateFormat;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.EventActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.GetPendingMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.GetPendingRequestResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestsSentClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MyEventsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetMyEventsListTask = null;
    // TODO: Change the response class
    private GetPendingRequestResponse mGetPendingRequestResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mEventListRecyclerView;
    private MyEventsListAdapter mMyEventsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // TODO: Change the class name
    private List<RequestsSentClass> listOfMyEvents;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private int pageCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_events, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mEventListRecyclerView = (RecyclerView) v.findViewById(R.id.list_my_events);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);


        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mMyEventsAdapter = new MyEventsListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mEventListRecyclerView.setLayoutManager(mLayoutManager);
        mEventListRecyclerView.setAdapter(mMyEventsAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshEventList();
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
            getMyEventsList();
        }
    }

    // TODO: modify this function
    private void refreshEventList() {
        if (Utilities.isConnectionAvailable(getActivity())) {

            pageCount = 0;
            if (listOfMyEvents != null)
                listOfMyEvents.clear();
            listOfMyEvents = null;
            getMyEventsList();

        } else if (getActivity() != null)
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    // TODO: modify the function with proper url and request POJO
    private void getMyEventsList() {
        if (mGetMyEventsListTask != null) {
            return;
        }

        GetPendingMoneyRequest mUserActivityRequest = new GetPendingMoneyRequest(pageCount, Constants.SERVICE_ID_REQUEST_MONEY);
        Gson gson = new Gson();
        String json = gson.toJson(mUserActivityRequest);
        mGetMyEventsListTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PENDING_REQUESTS_ME,
                Constants.BASE_URL_SM + Constants.URL_GET_SENT_REQUESTS, json, getActivity());
        mGetMyEventsListTask.mHttpResponseListener = this;
        mGetMyEventsListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetMyEventsListTask = null;
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

                    mGetPendingRequestResponse = gson.fromJson(result.getJsonString(), GetPendingRequestResponse.class);

                    if (listOfMyEvents == null) {
                        listOfMyEvents = mGetPendingRequestResponse.getAllNotifications();
                    } else {
                        List<RequestsSentClass> tempPendingMoneyRequestClasses;
                        tempPendingMoneyRequestClasses = mGetPendingRequestResponse.getAllNotifications();
                        listOfMyEvents.addAll(tempPendingMoneyRequestClasses);
                    }

                    mMyEventsAdapter.notifyDataSetChanged();

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
            mGetMyEventsListTask = null;

        }

        if (listOfMyEvents != null && listOfMyEvents.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private class MyEventsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public MyEventsListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mSenderNumber;
            private TextView mTime;
            private TextView mDescription;
            private ProfileImageView mProfileImageView;
            private View divider;

            public ViewHolder(final View itemView) {
                super(itemView);

                mSenderNumber = (TextView) itemView.findViewById(R.id.request_number);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mDescription = (TextView) itemView.findViewById(R.id.description);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                divider = itemView.findViewById(R.id.divider);
            }

            public void bindView(int pos) {


                if (pos == listOfMyEvents.size() - 1) divider.setVisibility(View.GONE);

                final long id = listOfMyEvents.get(pos).getId();
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(listOfMyEvents.get(pos).getRequestTime());
                String name = listOfMyEvents.get(pos).getReceiverProfile().getUserName();
                String imageUrl = listOfMyEvents.get(pos).getReceiverProfile().getUserProfilePicture();
                mTime.setText(time);
                mSenderNumber.setText(name);
                mDescription.setText(listOfMyEvents.get(pos).getDescription());

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((EventActivity) getActivity()).switchToEventDetailsFragment(id);
                    }
                });

            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            // TODO: modify the list item layout as per requirement
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_events, parent, false);

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
            if (listOfMyEvents != null)
                return listOfMyEvents.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
