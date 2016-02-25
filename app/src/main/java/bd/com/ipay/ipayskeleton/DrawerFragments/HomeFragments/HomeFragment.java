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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DetailsNewsActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Balance.RefreshBalanceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Balance.RefreshBalanceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.GetNewsFeedRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.GetNewsFeedResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.News;
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

    private TextView mUserNameTextView;
    private SharedPreferences pref;
    private String userName;
    private String UUID;
    private String userID;
    private ProgressDialog mProgressDialog;
    private TextView balanceView;
    private RecyclerView mNewsFeedRecyclerView;
    private NewsFeedAdapter mNewsFeedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<News> newsFeedResponsesList;
    private ImageView refreshBalanceButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int pageCount = 0;
    private boolean hasNext = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        userName = pref.getString(Constants.USERNAME, "");
        userID = pref.getString(Constants.USERID, "");

        if (pref.contains(userID))
            UUID = pref.getString(userID, null);

        mUserNameTextView = (TextView) v.findViewById(R.id.welcome_text);
        mUserNameTextView.setText("Welcome " + userName);
        balanceView = (TextView) v.findViewById(R.id.balance);
        mProgressDialog = new ProgressDialog(getActivity());
        mNewsFeedRecyclerView = (RecyclerView) v.findViewById(R.id.list_recent_activity_logs);
        refreshBalanceButton = (ImageView) v.findViewById(R.id.refresh_balance_button);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mNewsFeedAdapter = new NewsFeedAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNewsFeedRecyclerView.setLayoutManager(mLayoutManager);
        mNewsFeedRecyclerView.setAdapter(mNewsFeedAdapter);

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
            getNewsFeed();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    pageCount = 0;
                    newsFeedResponsesList.clear();
                    getNewsFeed();
                }
            }
        });

        // Add to trusted device?
        if (UUID == null) {
            showAlertDialogueForAddTrustedDevice();
        }

        return v;
    }

    private void refreshBalance() {
        if (mRefreshBalanceTask != null) {
            return;
        }

        if (!pref.contains(Constants.FIRST_LAUNCH)) {
            mProgressDialog.setMessage(getString(R.string.progress_dialog_refreshing));
            mProgressDialog.setCancelable(true);
        } else {
            mProgressDialog.setMessage(getString(R.string.progress_dialog_initializing));
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_refreshing));
        mProgressDialog.show();
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

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mRefreshBalanceTask = null;
            mGetNewsFeedTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
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
            } else if (getActivity() != null)
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.balance_update_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
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

            mSwipeRefreshLayout.setRefreshing(false);
            mGetNewsFeedTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ADD_TRUSTED_DEVICE)) {

            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mAddToTrustedDeviceResponse = gson.fromJson(resultList.get(2), AddToTrustedDeviceResponse.class);
                        String UUID = mAddToTrustedDeviceResponse.getUUID();
                        pref.edit().putString(userID, UUID).commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mAddTrustedDeviceTask = null;
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

    public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public NewsFeedAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mNewsImage;
            private TextView mNewsHeadLine;
            private TextView mNewsSubDescription;
            private TextView loadMoreTextView;
            private Button mmButtonReadMore;

            public ViewHolder(final View itemView) {
                super(itemView);

                mNewsImage = (ImageView) itemView.findViewById(R.id.news_image);
                mNewsHeadLine = (TextView) itemView.findViewById(R.id.news_title);
                mNewsSubDescription = (TextView) itemView.findViewById(R.id.short_news);
                mmButtonReadMore = (Button) itemView.findViewById(R.id.read_more);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
            }

            public void bindView(int pos) {

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

                mmButtonReadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Intent intent = new Intent(getActivity(), DetailsNewsActivity.class);

                        if (title != null)
                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_TITLE, title);
                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_TITLE, "");

                        if (description != null)
                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_DESCRIPTION, description);
                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_DESCRIPTION, "");

                        if (subDescription != null)
                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_SUB_DESCRIPTION, subDescription);
                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_SUB_DESCRIPTION, "");

                        if (imageUrl != null)
                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_IMAGE_FULL, Constants.BASE_URL_IMAGE_SERVER + imageUrl);
                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_IMAGE_FULL, "");

                        if (imageUrlThumbnail != null)
                            intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_IMAGE_THUMBNAIL, Constants.BASE_URL_IMAGE_SERVER + imageUrlThumbnail);
                        else intent.putExtra(DetailsNewsActivity.EXTRA_PARAM_IMAGE_THUMBNAIL, "");

                        Pair<View, String> p1 = Pair.create((View) mNewsImage, getString(R.string.transition_image));
                        Pair<View, String> p2 = Pair.create((View) mNewsHeadLine, getString(R.string.transition_title));
                        Pair<View, String> p3 = Pair.create((View) mNewsSubDescription, getString(R.string.transition_sub_title));

                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getActivity(), p1, p2, p3);

                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                        } else startActivity(intent);

                    }
                });
            }

            public void bindViewFooter(int pos) {
                if (hasNext) loadMoreTextView.setText(R.string.load_more);
                else loadMoreTextView.setText(R.string.no_more_results);
            }
        }

        public class FooterViewHolder extends ViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasNext) {
                            pageCount = pageCount + 1;
                            getNewsFeed();
                        }
                    }
                });

                TextView loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                if (hasNext) loadMoreTextView.setText(R.string.load_more);
                else loadMoreTextView.setText(R.string.no_more_results);
            }
        }

        // Now define the viewholder for Normal list item
        public class NormalViewHolder extends ViewHolder {
            public NormalViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do whatever you want on clicking the normal items
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_load_more_footer, parent, false);

                FooterViewHolder vh = new FooterViewHolder(v);

                return vh;
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_feed, parent, false);

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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (newsFeedResponsesList != null)
                return newsFeedResponsesList.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == newsFeedResponsesList.size()) {
                // This is where we'll add footer.
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}
