package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionHistoryFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTransactionHistoryTask = null;
    private TransactionHistoryResponse mTransactionHistoryResponse;

    private String[] transactionHistoryTypes;
    private RecyclerView mTransactionHistoryRecyclerView;
    private TransactionHistoryAdapter mTransactionHistoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TransactionHistoryClass> userTransactionHistoryClasses;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mMobileNumber;

    private int historyPageCount = 0;
    private boolean hasNext = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        getActivity().setTitle(R.string.transaction_history);

        SharedPreferences pref = getActivity()
                .getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mMobileNumber = pref.getString(Constants.USERID, "");

        transactionHistoryTypes = getResources().getStringArray(R.array.transaction_types);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);

        mTransactionHistoryAdapter = new TransactionHistoryAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);

        // Refresh balance each time home page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getTransactionHistory();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    historyPageCount = 0;
                    if (userTransactionHistoryClasses != null)
                        userTransactionHistoryClasses.clear();
                    getTransactionHistory();
                }
            }
        });

        return v;
    }

    private void getTransactionHistory() {
        if (mTransactionHistoryTask != null) {
            return;
        }

        TransactionHistoryRequest mTransactionHistoryRequest = new TransactionHistoryRequest(null, historyPageCount);
        Gson gson = new Gson();
        String json = gson.toJson(mTransactionHistoryRequest);
        mTransactionHistoryTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                Constants.BASE_URL_SM + Constants.URL_TRANSACTION_HISTORY, json, getActivity());
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.execute((Void) null);
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mTransactionHistoryTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_TRANSACTION_HISTORY)) {
            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    try {
                        mTransactionHistoryResponse = gson.fromJson(resultList.get(2), TransactionHistoryResponse.class);

                        if (userTransactionHistoryClasses == null || userTransactionHistoryClasses.size() == 0) {
                            userTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions();
                        } else {
                            List<TransactionHistoryClass> tempTransactionHistoryClasses;
                            tempTransactionHistoryClasses = mTransactionHistoryResponse.getTransactions();
                            userTransactionHistoryClasses.addAll(tempTransactionHistoryClasses);
                        }

                        hasNext = mTransactionHistoryResponse.isHasNext();
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

    public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public TransactionHistoryAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mTransactionType;
            private TextView mTransactionDescription;
            private TextView mTime;
//            private TextView mOtherUserName;
            private TextView mPurposeView;
            private TextView loadMoreTextView;
            private RoundedImageView mPortrait;
            private TextView mAmountTextView;
            private ImageView statusView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mTransactionType = (TextView) itemView.findViewById(R.id.transaction_type);
                mTransactionDescription = (TextView) itemView.findViewById(R.id.activity_description);
//                mOtherUserName = (TextView) itemView.findViewById(R.id.otherUserName);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mPurposeView = (TextView) itemView.findViewById(R.id.purpose);
                loadMoreTextView = (TextView) itemView.findViewById(R.id.load_more);
                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
                statusView = (ImageView) itemView.findViewById(R.id.status);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
            }

            public void bindView(int pos) {
                double amount = userTransactionHistoryClasses.get(pos).getAmount(mMobileNumber);

                int index = 0;
                if (amount >= 0) {
                    index = 0;
                } else {
                    index = 1;
                }
                String type = transactionHistoryTypes[index];
                String description = userTransactionHistoryClasses.get(pos).getDescription(mMobileNumber);
                String time = new SimpleDateFormat("EEE, MMM d, ''yy, H:MM a").format(userTransactionHistoryClasses.get(pos).getTime());
                mTransactionType.setText(type);

                // Handle debit credit
                if (amount > 0)
                    mAmountTextView.setText("+" + String.format("%.2f", amount));
                else
                    mAmountTextView.setText(String.format("%.2f", amount));

//                if (userTransactionHistoryClasses.get(pos).getOtherUserName() != null)
//                    mOtherUserName.setText(userTransactionHistoryClasses.get(pos).getOtherUserName());
//                else
//                    mOtherUserName.setText(userTransactionHistoryClasses.get(pos).getReceiverInfo());

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


                // TODO: uncomment this when pro pic will be available
//                Set<UserProfilePictureClass> userProfilePictureClassSet = userTransactionHistoryClasses.
//                        get(pos).getOtherUserprofilePictures();


//                if (userProfilePictureClassSet.size() > 0) {
//                    for (Iterator<UserProfilePictureClass> it = userProfilePictureClassSet.iterator(); it.hasNext(); ) {
//                        UserProfilePictureClass temp = it.next();
//                        Glide.with(getActivity())
//                                .load(Constants.BASE_URL_IMAGE + temp.getUrl())
//                                .into(mPortrait);
//                        break;
//                    }
//                } else {
//                    Glide.with(getActivity())
//                            .load(R.drawable.ic_transaction_history)
//                            .into(mPortrait);
//                }

                //TODO: remove this when pro pic came
                Glide.with(getActivity())
                        .load(R.drawable.ic_transaction_history)
                        .into(mPortrait);
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
                            historyPageCount = historyPageCount + 1;
                            getTransactionHistory();
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (userTransactionHistoryClasses != null)
                return userTransactionHistoryClasses.size() + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == userTransactionHistoryClasses.size()) {
                // This is where we'll add footer.
                return FOOTER_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}
