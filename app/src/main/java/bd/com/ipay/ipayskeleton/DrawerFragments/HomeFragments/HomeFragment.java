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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.MakePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.TopUpActivity;
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

public class HomeFragment extends Fragment implements HttpResponseListener, SwipeStack.SwipeStackListener {

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
    //    private RecyclerView mNewsFeedRecyclerView;
    private NewsFeedAdapter mNewsFeedAdapter;
    public static List<News> newsFeedResponsesList;
    private int itemsRemoved = 0;

    private ImageView refreshBalanceButton;
//    private SwipeRefreshLayout mSwipeRefreshLayout;
//    private RecyclerView.LayoutManager mNewsFeedLayoutManager;

    private View mSendMoneyButtonView;
    private View mMakePaymentButtonView;
    private View mMobileRechargeView;

    private Button mAddMoneyButton;
    private Button mWithdrawMoneyButton;

    private Button mShowAllTransactionButton;

    private View transactionView;
    private String[] transactionHistoryTypes;
    private List<TransactionHistoryClass> userTransactionHistoryClasses;
    private RecyclerView.LayoutManager mTransactionHistoryLayoutManager;
    private RecyclerView mTransactionHistoryRecyclerView;
    private TransactionHistoryAdapter mTransactionHistoryAdapter;

    private SwipeStack swipeStack;

    private int pageCount = 0;
    private boolean hasNext = false;

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

        userName = pref.getString(Constants.USERNAME, "");
        userID = pref.getString(Constants.USERID, "");

        if (pref.contains(userID))
            UUID = pref.getString(userID, null);

        TextView makePaymentLabel = (TextView) v.findViewById(R.id.textview_make_payment);
        if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE)
            makePaymentLabel.setText(getString(R.string.make_payment));
        else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE)
            makePaymentLabel.setText(getString(R.string.create_invoice));

        mSendMoneyButtonView = v.findViewById(R.id.layout_send_money);
        mMakePaymentButtonView = v.findViewById(R.id.layout_make_payment);
        mMobileRechargeView = v.findViewById(R.id.layout_topup);
        swipeStack = (SwipeStack) v.findViewById(R.id.swipeStack);

        balanceView = (TextView) v.findViewById(R.id.balance);
        mProgressDialog = new ProgressDialog(getActivity());
//        mNewsFeedRecyclerView = (RecyclerView) v.findViewById(R.id.list_recent_activity_logs);
        refreshBalanceButton = (ImageView) v.findViewById(R.id.refresh_balance_button);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mAddMoneyButton = (Button) v.findViewById(R.id.button_add_money);
        mWithdrawMoneyButton = (Button) v.findViewById(R.id.button_withdraw_money);

        transactionView = v.findViewById(R.id.layout_transaction);
        transactionHistoryTypes = getResources().getStringArray(R.array.transaction_types);
        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);
        mShowAllTransactionButton = (Button) v.findViewById(R.id.button_show_all_transactions);


//        mNewsFeedLayoutManager = new LinearLayoutManager(getActivity());
        mNewsFeedAdapter = new NewsFeedAdapter();
        swipeStack.setAdapter(mNewsFeedAdapter);
        swipeStack.setListener(this);
//        mNewsFeedRecyclerView.setLayoutManager(mNewsFeedLayoutManager);
//        mNewsFeedRecyclerView.setAdapter(mNewsFeedAdapter);

        mTransactionHistoryLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionHistoryAdapter = new TransactionHistoryAdapter();
        mTransactionHistoryRecyclerView.setLayoutManager(mTransactionHistoryLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);
        mTransactionHistoryRecyclerView.setNestedScrollingEnabled(false);

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
            else if (newsFeedResponsesList.size() == 0) swipeStack.setVisibility(View.GONE);

            getTransactionHistory();
        }

//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (Utilities.isConnectionAvailable(getActivity())) {
//                    pageCount = 0;
//                    newsFeedResponsesList.clear();
//                    getNewsFeed();
//                }
//            }
//        });

        // Add to trusted device?
        if (UUID == null) {
            showAlertDialogueForAddTrustedDevice();
        }

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

        mMobileRechargeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TopUpActivity.class);
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

        mShowAllTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new TransactionHistoryFragment()).commit();
                ((HomeActivity) getActivity()).switchedToHomeFragment = false;
            }
        });

        return v;
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


        mProgressDialog.setMessage(getString(R.string.adding_trusted_device));
        mProgressDialog.show();

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

        TransactionHistoryRequest mTransactionHistoryRequest = new TransactionHistoryRequest(null, 0);
        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY, json, getActivity());
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.execute();
    }

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
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mRefreshBalanceTask = null;
            mGetNewsFeedTask = null;
//            mSwipeRefreshLayout.setRefreshing(false);
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
                        if (balance != null) balanceView.setText(balance + " BDT");
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
                        hasNext = mGetNewsFeedResponse.isHasNext();
                        mNewsFeedAdapter.notifyDataSetChanged();

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

//            mSwipeRefreshLayout.setRefreshing(false);
            mGetNewsFeedTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ADD_TRUSTED_DEVICE)) {

            if (resultList.size() > 2) {

                mAddToTrustedDeviceResponse = gson.fromJson(resultList.get(2), AddToTrustedDeviceResponse.class);

                try {
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        String UUID = mAddToTrustedDeviceResponse.getUUID();
                        pref.edit().putString(userID, UUID).commit();
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

            mProgressDialog.dismiss();
            mAddTrustedDeviceTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {
            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mTransactionHistoryResponse = gson.fromJson(resultList.get(2), TransactionHistoryResponse.class);
                        // Show only last 5 transactions
                        userTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions().subList(
                                0, Math.min(5, mTransactionHistoryResponse.getTransactions().size()));

                        if (!userTransactionHistoryClasses.isEmpty()) {
                            transactionView.setVisibility(View.VISIBLE);

                            if (mTransactionHistoryResponse.getTransactions().size() > userTransactionHistoryClasses.size()) {
                                mShowAllTransactionButton.setVisibility(View.VISIBLE);
                            }

                            mTransactionHistoryAdapter.notifyDataSetChanged();
                        }

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

            mTransactionHistoryTask = null;
        }
    }

    private void showAlertDialogueForAddTrustedDevice() {

        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());

        alertDialogue.setTitle(R.string.confirm_add_to_trusted_title);
        alertDialogue.setMessage(R.string.confirm_add_to_trusted_msg);

        alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isConnectionAvailable(getActivity()))
                    addToTrustedDeviceList();
            }
        });

        alertDialogue.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        alertDialogue.show();
    }

    public class NewsFeedAdapter extends BaseAdapter {

        public NewsFeedAdapter() {
        }

        public void removeFromQueue() {
            newsFeedResponsesList.remove(0);
            notifyDataSetChanged();
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
            Button mmButtonReadMore = (Button) itemView.findViewById(R.id.read_more);

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

//    public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//        private static final int FOOTER_VIEW = 1;
//
//        public NewsFeedAdapter() {
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            private ImageView mNewsImage;
//            private TextView mNewsHeadLine;
//            private TextView mNewsSubDescription;
//            private TextView loadMoreTextView;
//            private Button mmButtonReadMore;
//
//            public ViewHolder(final View itemView) {
//                super(itemView);
//
//                mNewsImage = (ImageView) itemView.findViewById(R.id.news_image);
//                mNewsHeadLine = (TextView) itemView.findViewById(R.id.news_title);
//                mNewsSubDescription = (TextView) itemView.findViewById(R.id.short_news);
//                mmButtonReadMore = (Button) itemView.findViewById(R.id.read_more);
//                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
//            }
//
//            public void bindView(int pos) {
//
//                final long newsID = newsFeedResponsesList.get(pos).getId();
//                final String description = newsFeedResponsesList.get(pos).getDescription();
//                final String title = newsFeedResponsesList.get(pos).getTitle();
//                final String subDescription = newsFeedResponsesList.get(pos).getSubDescription();
//                final String imageUrl = newsFeedResponsesList.get(pos).getImageUrl();
//                final String imageUrlThumbnail = newsFeedResponsesList.get(pos).getImageThumbnailUrl();
//
//                if (title != null) mNewsHeadLine.setText(title);
//                if (subDescription != null) mNewsSubDescription.setText(subDescription);
//
//                if (imageUrl != null) Glide.with(getActivity())
//                        .load(Constants.BASE_URL_IMAGE_SERVER + imageUrl)
//                        .crossFade()
//                        .placeholder(R.drawable.dummy)
//                        .into(mNewsImage);
//
//                mmButtonReadMore.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        final Intent intent = new Intent(getActivity(), DetailsNewsActivity.class);
//
//                        if (title != null)
//                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_TITLE, title);
//                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_TITLE, "");
//
//                        if (description != null)
//                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_DESCRIPTION, description);
//                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_DESCRIPTION, "");
//
//                        if (subDescription != null)
//                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_SUB_DESCRIPTION, subDescription);
//                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_SUB_DESCRIPTION, "");
//
//                        if (imageUrl != null)
//                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_IMAGE_FULL, Constants.BASE_URL_IMAGE_SERVER + imageUrl);
//                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_IMAGE_FULL, "");
//
//                        if (imageUrlThumbnail != null)
//                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_IMAGE_THUMBNAIL, Constants.BASE_URL_IMAGE_SERVER + imageUrlThumbnail);
//                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_IMAGE_THUMBNAIL, "");
//
//                        Pair<View, String> p1 = Pair.create((View) mNewsImage, getString(R.string.transition_image));
//                        Pair<View, String> p2 = Pair.create((View) mNewsHeadLine, getString(R.string.transition_title));
//                        Pair<View, String> p3 = Pair.create((View) mNewsSubDescription, getString(R.string.transition_sub_title));
//
//                        ActivityOptionsCompat options = ActivityOptionsCompat.
//                                makeSceneTransitionAnimation(getActivity(), p1, p2, p3);
//
//                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//                        } else startActivity(intent);
//
//                    }
//                });
//            }
//
//            public void bindViewFooter(int pos) {
//                if (hasNext) loadMoreTextView.setText(R.string.load_more);
//                else loadMoreTextView.setText(R.string.no_more_results);
//            }
//        }
//
//        public class FooterViewHolder extends ViewHolder {
//            public FooterViewHolder(View itemView) {
//                super(itemView);
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (hasNext) {
//                            pageCount = pageCount + 1;
//                            getNewsFeed();
//                        }
//                    }
//                });
//
//                TextView loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
//                if (hasNext) loadMoreTextView.setText(R.string.load_more);
//                else loadMoreTextView.setText(R.string.no_more_results);
//            }
//        }
//
//        // Now define the viewholder for Normal list item
//        public class NormalViewHolder extends ViewHolder {
//            public NormalViewHolder(View itemView) {
//                super(itemView);
//
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Do whatever you want on clicking the normal items
//                    }
//                });
//            }
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//            View v;
//
//            if (viewType == FOOTER_VIEW) {
//                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_load_more_footer, parent, false);
//
//                FooterViewHolder vh = new FooterViewHolder(v);
//
//                return vh;
//            }
//
//            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_feed, parent, false);
//
//            NormalViewHolder vh = new NormalViewHolder(v);
//
//            return vh;
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            try {
//                if (holder instanceof NormalViewHolder) {
//                    NormalViewHolder vh = (NormalViewHolder) holder;
//                    vh.bindView(position);
//                } else if (holder instanceof FooterViewHolder) {
//                    FooterViewHolder vh = (FooterViewHolder) holder;
//                    vh.bindViewFooter(position);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            if (newsFeedResponsesList != null)
//                return newsFeedResponsesList.size() + 1;
//            else return 0;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//
//            if (position == newsFeedResponsesList.size()) {
//                // This is where we'll add footer.
//                return FOOTER_VIEW;
//            }
//
//            return super.getItemViewType(position);
//        }
//    }

    public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class TransactionHistoryViewHolder extends RecyclerView.ViewHolder {
            private View mItemView;

            private TextView mTransactionType;
            private TextView mTransactionDescription;
            private TextView mTime;
            private TextView mOtherUserName;
            private RoundedImageView mPortrait;
            private TextView mAmountTextView;
            private ImageView statusView;

            public TransactionHistoryViewHolder(final View itemView) {
                super(itemView);
                Log.w("View", itemView.getHeight() + " " + itemView.getMeasuredHeight());

                mItemView = itemView;

                mTransactionType = (TextView) itemView.findViewById(R.id.transaction_type);
                mTransactionDescription = (TextView) itemView.findViewById(R.id.activity_description);
                mOtherUserName = (TextView) itemView.findViewById(R.id.otherUserName);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                statusView = (ImageView) itemView.findViewById(R.id.status);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
            }

            public void bindView(int pos) {
                int transactionType = userTransactionHistoryClasses.get(pos).getTransactionType();
                int index = 0;
                if (transactionType == Constants.TRANSACTION_TYPE_DEBIT) {
                    index = 0;
                } else if (transactionType == Constants.TRANSACTION_TYPE_CREDIT) {
                    index = 1;
                }
                String type = transactionHistoryTypes[index];
                String description = userTransactionHistoryClasses.get(pos).getDescription();
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, H:MM a")
                        .format(userTransactionHistoryClasses.get(pos).getTime());
                mTransactionType.setText(type);

                // Handle debit credit
                if (userTransactionHistoryClasses.get(pos).getTransactionType() == Constants.TRANSACTION_TYPE_DEBIT)
                    mAmountTextView.setText("+" + userTransactionHistoryClasses.get(pos).getAmount());
                else
                    mAmountTextView.setText("-" + userTransactionHistoryClasses.get(pos).getAmount());

                if (userTransactionHistoryClasses.get(pos).getOtherUserName() != null)
                    mOtherUserName.setText(userTransactionHistoryClasses.get(pos).getOtherUserName());
                else
                    mOtherUserName.setText(userTransactionHistoryClasses.get(pos).getOtherMobileNumber());

                mTransactionDescription.setText(description);
                mTime.setText(time);

                if (userTransactionHistoryClasses.get(pos).getStatus().toString()
                        .equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mAmountTextView.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    statusView.setVisibility(View.GONE);

                } else if (userTransactionHistoryClasses.get(pos).getStatus().toString()
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

            public View getItemView() {
                return mItemView;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_history, parent, false);
            TransactionHistoryViewHolder vh = new TransactionHistoryViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                TransactionHistoryViewHolder vh = (TransactionHistoryViewHolder) holder;
                vh.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (userTransactionHistoryClasses != null)
                return userTransactionHistoryClasses.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
