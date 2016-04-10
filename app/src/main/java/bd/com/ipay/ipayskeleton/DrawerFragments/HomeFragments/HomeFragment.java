package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import bd.com.ipay.ipayskeleton.Activities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.MakePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.CustomSwipeRefreshLayout;
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
    private String UUID;
    private String userID;
    private ProgressDialog mProgressDialog;
    private TextView balanceView;
    public static List<News> newsFeedResponsesList;

    private ImageView refreshBalanceButton;
    private RelativeLayout mSendMoneyButtonView;
    private RelativeLayout mRequestMoneyView;
    private RelativeLayout mCreateInvoiceOrMobileRechargeButtonView;

    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
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
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        userID = pref.getString(Constants.USERID, "");

        if (pref.contains(Constants.UUID))
            UUID = pref.getString(UUID, null);

        TextView makePaymentOrRechargeLabel = (TextView) v.findViewById(R.id.textview_make_payment_or_recharge);
        if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE)
            makePaymentOrRechargeLabel.setText(getString(R.string.topup));
        else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE)
            makePaymentOrRechargeLabel.setText(getString(R.string.create_invoice));

        mSendMoneyButtonView = (RelativeLayout) v.findViewById(R.id.layout_send_money);
        mRequestMoneyView = (RelativeLayout) v.findViewById(R.id.layout_request_money);
        mCreateInvoiceOrMobileRechargeButtonView = (RelativeLayout) v.findViewById(R.id.layout_create_invoice_or_mobile_recharge);
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        balanceView = (TextView) v.findViewById(R.id.balance);
        mProgressDialog = new ProgressDialog(getActivity());
        refreshBalanceButton = (ImageView) v.findViewById(R.id.refresh_balance_button);

        mAddMoneyButton = (Button) v.findViewById(R.id.button_add_money);
        mWithdrawMoneyButton = (Button) v.findViewById(R.id.button_withdraw_money);

        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);

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

        // Refresh balance each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            refreshBalance();

            // Check if the news feed is already cleared or not
            if (!HomeActivity.newsFeedLoadedOnce) getNewsFeed();

            getTransactionHistory();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
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
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    private void setButtonActions() {
        mSendMoneyButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
                startActivity(intent);
            }
        });

        mCreateInvoiceOrMobileRechargeButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE) {
                    intent = new Intent(getActivity(), TopUpActivity.class);
                    startActivity(intent);
                } else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE) {
                    intent = new Intent(getActivity(), MakePaymentActivity.class);
                    startActivity(intent);
                }
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
                Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
                startActivity(intent);
            }
        });

        mWithdrawMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WithdrawMoneyActivity.class);
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

                try {
                    mAddToTrustedDeviceResponse = gson.fromJson(resultList.get(2), AddToTrustedDeviceResponse.class);

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
                        userTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions();

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

    private class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW = 1;
        private static final int WHATS_NEW_VIEW = 2;
        private static final int NEWS_FEED_ITEM_VIEW = 3;

        public class TransactionHistoryViewHolder extends RecyclerView.ViewHolder {
            private View mItemView;

            private TextView mTransactionDescription;
            private TextView mTime;
            private RoundedImageView mPortrait;
            private TextView mAmountTextView;
            private ImageView statusView;

            private ImageView mNewsImage;
            private TextView mNewsHeadLine;
            private TextView mNewsSubDescription;
            private TextView mNewsShortDescription;

            public TransactionHistoryViewHolder(final View itemView) {
                super(itemView);

                mItemView = itemView;

                mTransactionDescription = (TextView) itemView.findViewById(R.id.activity_description);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                statusView = (ImageView) itemView.findViewById(R.id.status);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);

                mNewsImage = (ImageView) itemView.findViewById(R.id.news_image);
                mNewsHeadLine = (TextView) itemView.findViewById(R.id.news_title);
                mNewsSubDescription = (TextView) itemView.findViewById(R.id.short_news);
                mNewsShortDescription = (TextView) itemView.findViewById(R.id.short_desc);
            }

            public void bindView(int pos) {

                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                double amount = userTransactionHistoryClasses.get(pos).getAmount(userID);

                String description = userTransactionHistoryClasses.get(pos).getDescription(userID);
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(userTransactionHistoryClasses.get(pos).getTime());

                // Handle debit credit
                if (amount > 0)
                    mAmountTextView.setText("+" + String.format("%.2f", amount) + " Tk."); // TODO: Currency will be set later. Put it as a constant now.
                else
                    mAmountTextView.setText(String.format("%.2f", amount) + " Tk.");   // TODO: Set taka unicode character, remove + for credit

                mTransactionDescription.setText(description);
                mTime.setText(time);

                if (userTransactionHistoryClasses.get(pos).getStatusCode().toString()
                        .equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    statusView.setColorFilter(Color.GREEN);
                    statusView.setImageResource(R.drawable.ic_check_circle_black_24dp);

                } else if (userTransactionHistoryClasses.get(pos).getStatusCode().toString()
                        .equals(Constants.HTTP_RESPONSE_STATUS_PROCESSING)) {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.text_gray));
                    statusView.setColorFilter(Color.GRAY);
                    statusView.setImageResource(R.drawable.ic_cached_black_24dp);

                } else {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.background_red));
                }

                //TODO: remove this when pro pic came
                Glide.with(getActivity())
                        .load(R.drawable.ic_transaction_history)
                        .into(mPortrait);
            }

            public void bindViewNewsFeed(int pos) {

                if (userTransactionHistoryClasses == null) pos = pos - 1;
                else {
                    if (userTransactionHistoryClasses.size() == 0) pos = pos - 1;
                    else pos = pos - userTransactionHistoryClasses.size() - 2;
                }

                final long newsID = newsFeedResponsesList.get(pos).getId();
                final String description = newsFeedResponsesList.get(pos).getDescription();
                final String title = newsFeedResponsesList.get(pos).getTitle();
                final String subDescription = newsFeedResponsesList.get(pos).getSubDescription();
                final String imageUrl = newsFeedResponsesList.get(pos).getImageUrl();
                final String imageUrlThumbnail = newsFeedResponsesList.get(pos).getImageThumbnailUrl();

                if (title != null) mNewsHeadLine.setText(title);
                if (subDescription != null) mNewsSubDescription.setText(subDescription);
                if (description != null) mNewsShortDescription.setText(description);

                if (imageUrl != null) Glide.with(getActivity())
                        .load(Constants.BASE_URL_IMAGE_SERVER + imageUrl)
                        .crossFade()
                        .placeholder(R.drawable.dummy)
                        .into(mNewsImage);
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

        public class WhatsNewViewHolder extends TransactionHistoryViewHolder {
            public WhatsNewViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class NewsFeedViewHolder extends TransactionHistoryViewHolder {
            public NewsFeedViewHolder(View itemView) {
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

            if (viewType == WHATS_NEW_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_whats_new, parent, false);
                WhatsNewViewHolder vh = new WhatsNewViewHolder(v);
                return vh;

            } else if (viewType == HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_transaction_histories, parent, false);
                HeaderViewHolder vh = new HeaderViewHolder(v);
                return vh;

            } else if (viewType == NEWS_FEED_ITEM_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_feed, parent, false);
                NewsFeedViewHolder vh = new NewsFeedViewHolder(v);
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
                } else if (holder instanceof WhatsNewViewHolder) {
                    WhatsNewViewHolder vh = (WhatsNewViewHolder) holder;
                } else if (holder instanceof HeaderViewHolder) {
                    HeaderViewHolder vh = (HeaderViewHolder) holder;
                } else if (holder instanceof NewsFeedViewHolder) {
                    NewsFeedViewHolder vh = (NewsFeedViewHolder) holder;
                    vh.bindViewNewsFeed(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {

            int transactionHistoryListSize = 0;
            int newsFeedListSize = 0;

            if (userTransactionHistoryClasses == null && newsFeedResponsesList == null) return 0;

            if (userTransactionHistoryClasses != null)
                transactionHistoryListSize = userTransactionHistoryClasses.size();
            if (newsFeedResponsesList != null) newsFeedListSize = newsFeedResponsesList.size();

            if (transactionHistoryListSize > 0 && newsFeedListSize > 0)
                return 1 + transactionHistoryListSize + 1 + newsFeedListSize;   // Header, transaction histories, whats new header , news feed list
            else if (transactionHistoryListSize > 0 && newsFeedListSize == 0)
                return 1 + transactionHistoryListSize;                          // Header, transaction histories
            else if (transactionHistoryListSize == 0 && newsFeedListSize > 0)
                return 1 + newsFeedListSize;                                    // whats new header , news feed list
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {

            int transactionHistoryListSize = 0;
            int newsFeedListSize = 0;

            if (userTransactionHistoryClasses == null && newsFeedResponsesList == null)
                return super.getItemViewType(position);

            if (userTransactionHistoryClasses != null)
                transactionHistoryListSize = userTransactionHistoryClasses.size();
            if (newsFeedResponsesList != null) newsFeedListSize = newsFeedResponsesList.size();

            if (transactionHistoryListSize > 0 && newsFeedListSize > 0) {
                if (position == 0) return HEADER_VIEW;
                else if (position == transactionHistoryListSize + 1) return WHATS_NEW_VIEW;
                else if (position > transactionHistoryListSize + 1) return NEWS_FEED_ITEM_VIEW;
                else return super.getItemViewType(position);

            } else if (transactionHistoryListSize > 0 && newsFeedListSize == 0) {
                if (position == 0) return HEADER_VIEW;
                else return super.getItemViewType(position);

            } else if (transactionHistoryListSize == 0 && newsFeedListSize > 0) {
                if (position == 0) return WHATS_NEW_VIEW;
                else return NEWS_FEED_ITEM_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}
