package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.DeepLinkedNotification;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.GetDeepLinkedNotificationResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class NotificationDeeplinkedFragment extends ProgressFragment implements HttpResponseListener {


    private RecyclerView mNotificationsRecyclerView;
    private NotificationListAdapter mNotificationListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgressDialog;
    private TextView mEmptyListTextView;
    private List<DeepLinkedNotification> mDeepLinkedNotifications;

    private HttpRequestGetAsyncTask mGetNotificationAsyncTask;

    private NotificationBroadcastReceiver notificationBroadcastReceiver;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mNotificationsRecyclerView = (RecyclerView) v.findViewById(R.id.list_notification);
        mProgressDialog = new ProgressDialog(getActivity());
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mNotificationListAdapter = new NotificationListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecyclerView.setLayoutManager(mLayoutManager);
        mNotificationsRecyclerView.setAdapter(mNotificationListAdapter);
        getNotifications();

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });

        return v;
    }

    public void onResume() {
        super.onResume();

        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(notificationBroadcastReceiver,
                new IntentFilter(Constants.NOTIFICATION_UPDATE_BROADCAST));

        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_notifications));
    }

    private void getNotifications() {
        if (mGetNotificationAsyncTask != null) {
            return;
        } else {
            mGetNotificationAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_NOTIFICATION,
                    Constants.BASE_URL_PUSH_NOTIFICATION + Constants.URL_PULL_NOTIFICATION, getContext(), this, false);
            mGetNotificationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(notificationBroadcastReceiver);
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        try {

            if (HttpErrorHandler.isErrorFound(result, getActivity(), mProgressDialog)) {
                setContentShown(true);
                mSwipeRefreshLayout.setRefreshing(false);
                if (isAdded()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                return;
            }

            switch (result.getApiCommand()) {
                case Constants.COMMAND_GET_NOTIFICATION:
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        GetDeepLinkedNotificationResponse getDeepLinkedNotificationResponse = new Gson().
                                fromJson(result.getJsonString(), GetDeepLinkedNotificationResponse.class);
                        mDeepLinkedNotifications = getDeepLinkedNotificationResponse.getNotificationList();
                        mNotificationListAdapter.notifyDataSetChanged();
                    }
                    setContentShown(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mGetNotificationAsyncTask = null;
            }
        } catch (Exception e) {

            setContentShown(true);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

        public class NotificationViewHolder extends RecyclerView.ViewHolder {
            private final TextView mNameView;
            private final TextView mTimeView;
            private final ProfileImageView mProfileImageView;

            public NotificationViewHolder(final View itemView) {
                super(itemView);

                mNameView = (TextView) itemView.findViewById(R.id.textview_description);
                mTimeView = (TextView) itemView.findViewById(R.id.textview_time);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
            }

            public void bindView(int pos) {

                DeepLinkedNotification notification = mDeepLinkedNotifications.get(pos);

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + notification.getIcon(), false);
                mNameView.setText(notification.getMessage());

                mTimeView.setText(Utilities.formatDateWithTime(notification.getTime()));
            }
        }


        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(getContext()).inflate(R.layout.view_notification_description, parent, false);
            return new NotificationViewHolder(v);
        }

        @Override
        public void onBindViewHolder(NotificationViewHolder holder, int position) {
            try {
                holder.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mDeepLinkedNotifications.size();
        }

    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
