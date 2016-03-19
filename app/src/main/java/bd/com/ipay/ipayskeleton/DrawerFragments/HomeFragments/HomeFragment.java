package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.CashInActivity;
import bd.com.ipay.ipayskeleton.Activities.CashOutActivity;
import bd.com.ipay.ipayskeleton.Activities.EditProfileActivity;
import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.MakePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Balance.RefreshBalanceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Balance.RefreshBalanceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.GetNewsFeedRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.GetNewsFeedResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.News;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.AddToTrustedDeviceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.AddToTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import link.fls.swipestack.SwipeStack;

public class HomeFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRefreshBalanceTask = null;
    private RefreshBalanceResponse mRefreshBalanceResponse;

    private HttpRequestPostAsyncTask mAddTrustedDeviceTask = null;
    private AddToTrustedDeviceResponse mAddToTrustedDeviceResponse;

    private HttpRequestGetAsyncTask mGetNewsFeedTask = null;
    private GetNewsFeedResponse mGetNewsFeedResponse;

    private HttpRequestPostAsyncTask mTransactionHistoryTask = null;
    private TransactionHistoryResponse mTransactionHistoryResponse;

    private SharedPreferences pref;
    private String userName;
    private String UUID;
    private String userID;
    private ProgressDialog mProgressDialog;
    private TextView balanceView;
    private NewsFeedAdapter mNewsFeedAdapter;
    public static List<News> newsFeedResponsesList;
    private int itemsRemoved = 0;

    private ImageView refreshBalanceButton;
    private RelativeLayout mSendMoneyButtonView;
    private RelativeLayout mRequestMoneyView;
    private RelativeLayout mMakePaymentButtonView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button mAddMoneyButton;
    private Button mWithdrawMoneyButton;

    private List<TransactionHistoryClass> userTransactionHistoryClasses;
    private RecyclerView.LayoutManager mTransactionHistoryLayoutManager;
    private RecyclerView mTransactionHistoryRecyclerView;
    private TransactionHistoryAdapter mTransactionHistoryAdapter;

    private final int pageCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (itemsRemoved > 0) {
            for (int i = 0; i < itemsRemoved; i++) newsFeedResponsesList.remove(0);
            if (mNewsFeedAdapter != null) mNewsFeedAdapter.notifyDataSetChanged();
            itemsRemoved = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        getActivity().setTitle(R.string.app_name);

        userID = pref.getString(Constants.USERID, "");

        if (pref.contains(Constants.UUID))
            UUID = pref.getString(UUID, null);

        TextView makePaymentLabel = (TextView) v.findViewById(R.id.textview_make_payment);
        if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE)
            makePaymentLabel.setText(getString(R.string.make_payment));
        else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE)
            makePaymentLabel.setText(getString(R.string.create_invoice));

        mSendMoneyButtonView = (RelativeLayout) v.findViewById(R.id.layout_send_money);
        mRequestMoneyView = (RelativeLayout) v.findViewById(R.id.layout_request_money);
        mMakePaymentButtonView = (RelativeLayout) v.findViewById(R.id.layout_make_payment);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        balanceView = (TextView) v.findViewById(R.id.balance);
        mProgressDialog = new ProgressDialog(getActivity());
        refreshBalanceButton = (ImageView) v.findViewById(R.id.refresh_balance_button);

        mAddMoneyButton = (Button) v.findViewById(R.id.button_add_money);
        mWithdrawMoneyButton = (Button) v.findViewById(R.id.button_withdraw_money);

        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);

        mNewsFeedAdapter = new NewsFeedAdapter();
        mTransactionHistoryLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionHistoryAdapter = new TransactionHistoryAdapter();
        mTransactionHistoryRecyclerView.setLayoutManager(mTransactionHistoryLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);

        refreshBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshBalance();
                }
            }
        });

        // Refresh balance each time home page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            refreshBalance();

            // Check if the news feed is already cleared or not
            if (!HomeActivity.newsFeedLoadedOnce) getNewsFeed();

            getTransactionHistory();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    getTransactionHistory();
                }
            }
        });

        // TODO: Place this in HomeActivity
        // Add to trusted device
        if (UUID == null) {
            if (Utilities.isConnectionAvailable(getActivity()))
                addToTrustedDeviceList();
        }

        setButtonActions();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            ((EditProfileActivity) getActivity()).attemptSaveProfile();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setButtonActions() {
        mSendMoneyButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
                startActivity(intent);
            }
        });

        mMakePaymentButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MakePaymentActivity.class);
                startActivity(intent);
            }
        });

        mRequestMoneyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestMoneyActivity.class);
                startActivity(intent);
            }
        });

        mAddMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CashInActivity.class);
                startActivity(intent);
            }
        });

        mWithdrawMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CashOutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void refreshBalance() {
        if (mRefreshBalanceTask != null) {
            return;
        }

        Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshBalanceButton.startAnimation(rotation);

        RefreshBalanceRequest mLoginModel = new RefreshBalanceRequest(pref.getString(Constants.USERID, ""));
        Gson gson = new Gson();
        String json = gson.toJson(mLoginModel);
        mRefreshBalanceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_BALANCE,
                Constants.BASE_URL_SM + Constants.URL_REFRESH_BALANCE, json, getActivity());
        mRefreshBalanceTask.mHttpResponseListener = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mRefreshBalanceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mRefreshBalanceTask.execute((Void) null);
        }
    }

    private void addToTrustedDeviceList() {
        if (mAddTrustedDeviceTask != null) {
            return;
        }

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String mDeviceID = telephonyManager.getDeviceId();
        String mDeveiceName = android.os.Build.MANUFACTURER + "-" + android.os.Build.PRODUCT + " -" + Build.MODEL;

        AddToTrustedDeviceRequest mAddToTrustedDeviceRequest = new AddToTrustedDeviceRequest(mDeveiceName, Constants.MOBILE_ANDROID + mDeviceID);
        Gson gson = new Gson();
        String json = gson.toJson(mAddToTrustedDeviceRequest);
        mAddTrustedDeviceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_TRUSTED_DEVICE,
                Constants.BASE_URL_POST_MM + Constants.URL_ADD_TRUSTED_DEVICE, json, getActivity());
        mAddTrustedDeviceTask.mHttpResponseListener = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mAddTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAddTrustedDeviceTask.execute((Void) null);
        }
    }

    private void getNewsFeed() {
        if (mGetNewsFeedTask != null) {
            return;
        }

        GetNewsFeedRequestBuilder mGetNewsFeedRequestBuilder = new GetNewsFeedRequestBuilder(pageCount);

        String mUri = mGetNewsFeedRequestBuilder.getGeneratedUri();
        mGetNewsFeedTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_NEWS_FEED,
                mUri, getActivity());
        mGetNewsFeedTask.mHttpResponseListener = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mGetNewsFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mGetNewsFeedTask.execute((Void) null);
        }
    }

    private void getTransactionHistory() {
        if (mTransactionHistoryTask != null) {
            return;
        }

        TransactionHistoryRequest mTransactionHistoryRequest = new TransactionHistoryRequest(null, pageCount);
        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY, json, getActivity());
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.execute();
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mRefreshBalanceTask = null;
            mGetNewsFeedTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();

            refreshBalanceButton.clearAnimation();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_REFRESH_BALANCE)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mRefreshBalanceResponse = gson.fromJson(resultList.get(2), RefreshBalanceResponse.class);
                        String balance = mRefreshBalanceResponse.getBalance() + "";
                        if (balance != null)
                            balanceView.setText(getString(R.string.balance_placeholder) + balance);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.balance_update_failed, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.balance_update_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.balance_update_failed, Toast.LENGTH_LONG).show();
            }

            refreshBalanceButton.clearAnimation();
            mRefreshBalanceTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_NEWS_FEED)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mGetNewsFeedResponse = gson.fromJson(resultList.get(2), GetNewsFeedResponse.class);

                        if (newsFeedResponsesList == null) {
                            newsFeedResponsesList = mGetNewsFeedResponse.getNewsFeed();
                        } else {
                            List<News> tempUserActivityResponsesList;
                            tempUserActivityResponsesList = mGetNewsFeedResponse.getNewsFeed();
                            newsFeedResponsesList.addAll(tempUserActivityResponsesList);
                        }

                        HomeActivity.newsFeedLoadedOnce = true;
                        // TODO: Handle news feed hasNext in future
                        mNewsFeedAdapter.notifyDataSetChanged();
                        mTransactionHistoryAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.news_feed_get_failed, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.news_feed_get_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.news_feed_get_failed, Toast.LENGTH_LONG).show();

            mGetNewsFeedTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ADD_TRUSTED_DEVICE)) {

            if (resultList.size() > 2) {

                mAddToTrustedDeviceResponse = gson.fromJson(resultList.get(2), AddToTrustedDeviceResponse.class);

                try {
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        String UUID = mAddToTrustedDeviceResponse.getUUID();
                        pref.edit().putString(Constants.UUID, UUID).commit();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mAddToTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();

            mAddTrustedDeviceTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {
            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mTransactionHistoryResponse = gson.fromJson(resultList.get(2), TransactionHistoryResponse.class);

                        // Show only last 5 transactions
                        userTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions().subList(
                                0, Math.min(5, mTransactionHistoryResponse.getTransactions().size()));

                        if (userTransactionHistoryClasses.size() == 0)
                            userTransactionHistoryClasses = null;
                        mTransactionHistoryAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();

            mSwipeRefreshLayout.setRefreshing(false);
            mTransactionHistoryTask = null;
        }
    }

    public class NewsFeedAdapter extends BaseAdapter {

        public NewsFeedAdapter() {
        }

        @Override
        public int getCount() {
            if (newsFeedResponsesList != null)
                return newsFeedResponsesList.size();
            else return 0;
        }

        @Override
        public News getItem(int position) {
            return newsFeedResponsesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int pos, View itemView, ViewGroup parent) {

            itemView = getActivity().getLayoutInflater().inflate(R.layout.list_item_news_feed, parent, false);
            ImageView mNewsImage = (ImageView) itemView.findViewById(R.id.news_image);
            TextView mNewsHeadLine = (TextView) itemView.findViewById(R.id.news_title);
            TextView mNewsSubDescription = (TextView) itemView.findViewById(R.id.short_news);

            final long newsID = newsFeedResponsesList.get(pos).getId();
            final String description = newsFeedResponsesList.get(pos).getDescription();
            final String title = newsFeedResponsesList.get(pos).getTitle();
            final String subDescription = newsFeedResponsesList.get(pos).getSubDescription();
            final String imageUrl = newsFeedResponsesList.get(pos).getImageUrl();
            final String imageUrlThumbnail = newsFeedResponsesList.get(pos).getImageThumbnailUrl();

            if (title != null) mNewsHeadLine.setText(title);
            if (subDescription != null) mNewsSubDescription.setText(subDescription);

            if (imageUrl != null) Glide.with(getActivity())
                    .load(Constants.BASE_URL_IMAGE_SERVER + imageUrl)
                    .crossFade()
                    .placeholder(R.drawable.dummy)
                    .into(mNewsImage);

            return itemView;
        }
    }

    private class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int HEADER_VIEW = 2;

        public class TransactionHistoryViewHolder extends RecyclerView.ViewHolder implements SwipeStack.SwipeStackListener {
            private View mItemView;

            private TextView mTransactionDescription;
            private TextView mTime;
            private TextView mPurposeView;
            private RoundedImageView mPortrait;
            private TextView mAmountTextView;
            private ImageView statusView;
            private TextView whatNewTextViewHeader;

            private SwipeStack swipeStack;

            @Override
            public void onViewSwipedToLeft(int position) {
                itemsRemoved++;
            }

            @Override
            public void onViewSwipedToRight(int position) {
                itemsRemoved++;
            }

            @Override
            public void onStackEmpty() {
                swipeStack.setVisibility(View.GONE);
                whatNewTextViewHeader.setVisibility(View.GONE);
            }

            public TransactionHistoryViewHolder(final View itemView) {
                super(itemView);

                mItemView = itemView;

                mTransactionDescription = (TextView) itemView.findViewById(R.id.activity_description);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mPurposeView = (TextView) itemView.findViewById(R.id.purpose);
                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                statusView = (ImageView) itemView.findViewById(R.id.status);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);

                // Views for news feed
                swipeStack = (SwipeStack) itemView.findViewById(R.id.swipeStack);
                whatNewTextViewHeader = (TextView) itemView.findViewById(R.id.whats_new_text);
            }

            public void bindView(int pos) {

                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                double amount = userTransactionHistoryClasses.get(pos).getAmount(userID);

                String description = userTransactionHistoryClasses.get(pos).getDescription(userID);
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, H:MM a").format(userTransactionHistoryClasses.get(pos).getTime());

                // Handle debit credit
                if (amount > 0)
                    mAmountTextView.setText("+" + String.format("%.2f", amount));
                else
                    mAmountTextView.setText(String.format("%.2f", amount));

                mTransactionDescription.setText(description);
                mTime.setText(time);
                mPurposeView.setText(userTransactionHistoryClasses.get(pos).getPurpose());

                if (userTransactionHistoryClasses.get(pos).getStatusCode().toString()
                        .equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    statusView.setVisibility(View.GONE);

                } else if (userTransactionHistoryClasses.get(pos).getStatusCode().toString()
                        .equals(Constants.HTTP_RESPONSE_STATUS_PROCESSING)) {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.colorDivider));
                    statusView.setVisibility(View.GONE);

                } else {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.background_red));
                    statusView.setVisibility(View.VISIBLE);
                }

                //TODO: remove this when pro pic came
                Glide.with(getActivity())
                        .load(R.drawable.ic_transaction_history)
                        .into(mPortrait);
            }

            public void bindViewFooter(int pos) {
                if (newsFeedResponsesList.size() == 0) {
                    swipeStack.setVisibility(View.GONE);
                    whatNewTextViewHeader.setVisibility(View.GONE);
                } else {
                    swipeStack.setAdapter(mNewsFeedAdapter);
                    swipeStack.setListener(this);
                }
            }

            public View getItemView() {
                return mItemView;
            }
        }

        public class HeaderViewHolder extends TransactionHistoryViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class FooterViewHolder extends TransactionHistoryViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        // Now define the viewholder for Normal list item
        public class NormalViewHolder extends TransactionHistoryViewHolder {
            public NormalViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_footer_news_feed_swipestack, parent, false);
                FooterViewHolder vh = new FooterViewHolder(v);
                return vh;

            } else if (viewType == HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_transaction_histories, parent, false);
                HeaderViewHolder vh = new HeaderViewHolder(v);
                return vh;
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_history, parent, false);
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
                } else if (holder instanceof HeaderViewHolder) {
                    HeaderViewHolder vh = (HeaderViewHolder) holder;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (userTransactionHistoryClasses != null && newsFeedResponsesList != null)
                return userTransactionHistoryClasses.size() + 2;
            else if (userTransactionHistoryClasses != null && newsFeedResponsesList == null)
                return userTransactionHistoryClasses.size() + 1;
            else if (userTransactionHistoryClasses == null && newsFeedResponsesList != null)
                return 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {

            if (userTransactionHistoryClasses == null && newsFeedResponsesList != null) {
                return FOOTER_VIEW;
            } else if (position == userTransactionHistoryClasses.size() + 1) {
                return FOOTER_VIEW;
            } else if (position == 0) {
                return HEADER_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}
