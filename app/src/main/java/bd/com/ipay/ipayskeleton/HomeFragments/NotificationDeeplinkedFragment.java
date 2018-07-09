package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.DeepLinkedNotification;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.GetDeepLinkedNotificationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UpdateNotificationStateRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeepLinkAction;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class NotificationDeeplinkedFragment extends ProgressFragment implements HttpResponseListener {


    private RecyclerView mNotificationsRecyclerView;
    private NotificationListAdapter mNotificationListAdapter;
    private LinearLayoutManager mLayoutManager;
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgressDialog;
    private TextView mEmptyListTextView;
    private List<DeepLinkedNotification> mDeepLinkedNotifications;

    private boolean hasNext = false;
    private boolean isLoading = false;
    private boolean clearListAfterLoading;
    private boolean mIsScrolled = false;
    private int mTotalItemCount = 0;
    private int mPastVisibleItems;
    private int mVisibleItem;
    private long lastTime;

    private HttpRequestGetAsyncTask mGetNotificationAsyncTask;
    private HttpRequestPutAsyncTask mUpdateNotificationStateTask;

    private NotificationBroadcastReceiver notificationBroadcastReceiver;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        lastTime = 0;
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
        implementScrollListener();
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
            setContentShown(true);
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        } else {
            String url = Constants.BASE_URL_PUSH_NOTIFICATION + Constants.URL_PULL_NOTIFICATION;
            Uri.Builder uri = Uri.parse(url)
                    .buildUpon();
            uri.appendQueryParameter("limit", Integer.toString(10));
            if (lastTime != 0) {
                uri.appendQueryParameter("beforeTime", Long.toString(lastTime));
            }
            mGetNotificationAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_NOTIFICATION,
                    uri.build().toString(), getContext(), this, false);
            mGetNotificationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void loadNotifications(List<DeepLinkedNotification> notifications, boolean hasNext) {
        if (clearListAfterLoading || mDeepLinkedNotifications == null || mDeepLinkedNotifications.size() == 0) {
            mDeepLinkedNotifications = notifications;
            clearListAfterLoading = false;
        } else {
            List<DeepLinkedNotification> tempNotifications;
            tempNotifications = notifications;
            mDeepLinkedNotifications.addAll(tempNotifications);
        }

        this.hasNext = hasNext;
        if (mDeepLinkedNotifications != null && mDeepLinkedNotifications.size() > 0)
            mEmptyListTextView.setVisibility(View.GONE);
        else
            mEmptyListTextView.setVisibility(View.VISIBLE);

        if (isLoading)
            isLoading = false;
        mNotificationListAdapter.notifyDataSetChanged();
        setContentShown(true);
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
                mGetNotificationAsyncTask = null;
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
                        List<DeepLinkedNotification> deepLinkedNotifications = getDeepLinkedNotificationResponse.getNotificationList();
                        lastTime = deepLinkedNotifications.get(deepLinkedNotifications.size() - 1).getTime();
                        loadNotifications(deepLinkedNotifications, getDeepLinkedNotificationResponse.isHasNext());
                        SharedPrefManager.setNotificationCount(getDeepLinkedNotificationResponse.getUnseenCount());
                    }
                    setContentShown(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mGetNotificationAsyncTask = null;
                    break;
                case Constants.COMMAND_UPDATE_NOTIFICATION_STATE:
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        Log.d("update notification", result.getJsonString());
                        getNotifications();
                    } else {

                    }
            }
        } catch (Exception e) {
            setContentShown(true);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void implementScrollListener() {
        mNotificationsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisibleItem = recyclerView.getChildCount();
                mTotalItemCount = mLayoutManager.getItemCount();
                mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (mIsScrolled
                        && (mVisibleItem + mPastVisibleItems) == mTotalItemCount && hasNext && mGetNotificationAsyncTask == null) {
                    isLoading = true;
                    mIsScrolled = false;
                    mNotificationListAdapter.notifyDataSetChanged();
                    getNotifications();
                }

            }

        });

    }

    private void updateNotificationState(List<Long> timeList) {
        if (mUpdateNotificationStateTask != null) {
            return;
        } else {
            UpdateNotificationStateRequest updateNotificationStateRequest = new UpdateNotificationStateRequest(timeList);
            mUpdateNotificationStateTask = new HttpRequestPutAsyncTask(Constants.COMMAND_UPDATE_NOTIFICATION_STATE,
                    Constants.BASE_URL_PUSH_NOTIFICATION + "v2/update",
                    new Gson().toJson(updateNotificationStateRequest), getContext(), this, true);
            mUpdateNotificationStateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

        public int FOOTER_VIEW = 1;

        public class NotificationViewHolder extends RecyclerView.ViewHolder {
            private final TextView mNameView;
            private final TextView mTimeView;
            private final ProfileImageView mProfileImageView;
            private TextView mLoadMoreTextView;
            private ProgressBar mLoadMoreProgressBar;
            private TextView titleView;

            public NotificationViewHolder(final View itemView) {
                super(itemView);

                mNameView = (TextView) itemView.findViewById(R.id.textview_description);
                mTimeView = (TextView) itemView.findViewById(R.id.textview_time);
                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mLoadMoreProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
                mLoadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                titleView = (TextView) itemView.findViewById(R.id.textview_title);
            }

            private void setItemVisibilityOfFooterView() {
                if (isLoading) {
                    mLoadMoreProgressBar.setVisibility(View.VISIBLE);
                    mLoadMoreTextView.setVisibility(View.GONE);
                } else {
                    mLoadMoreProgressBar.setVisibility(View.GONE);
                    mLoadMoreTextView.setVisibility(View.VISIBLE);

                    if (hasNext)
                        mLoadMoreTextView.setText(R.string.load_more);
                    else
                        mLoadMoreTextView.setText(R.string.no_more_results);
                }
            }

            public void bindView(final int pos) {
                if (pos == mDeepLinkedNotifications.size()) {
                    mLoadMoreProgressBar.setVisibility(View.VISIBLE);
                    setItemVisibilityOfFooterView();
                } else {
                    DeepLinkedNotification notification = mDeepLinkedNotifications.get(pos);
                    mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + notification.getIcon(), false);
                    mNameView.setText(notification.getMessage());
                    mTimeView.setText(Utilities.formatDateWithTime(notification.getTime()));
                    titleView.setText(notification.getTitle());
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(mDeepLinkedNotifications.get(pos).getDeepLink());
                        DeepLinkAction deepLinkAction;
                        try {
                            deepLinkAction = Utilities.parseUriForDeepLinkingAction(uri);
                            Utilities.performDeepLinkAction(getActivity(), deepLinkAction);
                            List<Long> timeList = new ArrayList<>();
                            timeList.add(mDeepLinkedNotifications.get(pos).getTime());
                            updateNotificationState(timeList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == FOOTER_VIEW) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_load_more_footer, parent, false);
                return new NotificationViewHolder(v);
            } else {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.view_notification_description, parent, false);
                return new NotificationViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(NotificationViewHolder holder, int position) {
            try {
                if (position == mDeepLinkedNotifications.size()) {
                    holder.bindView(position);
                } else {
                    holder.bindView(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mDeepLinkedNotifications.size()) {
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if (mDeepLinkedNotifications == null || mDeepLinkedNotifications.isEmpty())
                return 0;
            else {
                return mDeepLinkedNotifications.size() + 1;
            }
        }

    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
