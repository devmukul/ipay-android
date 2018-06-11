package bd.com.ipay.ipayskeleton.HomeFragments;

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

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.MerchantBranchSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.GetAllMerchantsResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;


public class MerchantsDashBoardFragment extends BaseFragment implements HttpResponseListener {
    private View view;
    private RecyclerView merchantsListRecyclerView;
    private HttpRequestGetAsyncTask mGetAllMerchantsAsyncTask;
    private GetAllMerchantsResponse mGetAllMerchantsResponse;
    private MerchantsListAdapter mMerchantListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mUri;
    private MerchantBranchSelectorDialog merchantBranchSelectorDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_merchant_dashboard, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        merchantsListRecyclerView = (RecyclerView) view.findViewById(R.id.merchants_list_recycler_view);
        attemptGetMerchantsList();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                attemptGetMerchantsList();
            }
        });
        return view;
    }

    private void attemptGetMerchantsList() {
        if (mGetAllMerchantsAsyncTask != null) {
            return;
        } else {
            mUri = Constants.BASE_URL_MM + Constants.URL_GET_ALL_MERCHANTS;
            mGetAllMerchantsAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ALL_MERCHANTS, mUri, getContext(), true);
            mGetAllMerchantsAsyncTask.mHttpResponseListener = this;
            mGetAllMerchantsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (merchantBranchSelectorDialog != null) {
            merchantBranchSelectorDialog.dismiss();
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetAllMerchantsAsyncTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mGetAllMerchantsResponse = new Gson().fromJson
                            (result.getJsonString(), GetAllMerchantsResponse.class);
                    mMerchantListAdapter = new MerchantsListAdapter();
                    merchantsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    merchantsListRecyclerView.setAdapter(mMerchantListAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mGetAllMerchantsAsyncTask = null;
        }
    }

    public class MerchantsListAdapter extends RecyclerView.Adapter<MerchantsListAdapter.MerchantsListViewHolder> {


        @Override
        public MerchantsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MerchantsListViewHolder(LayoutInflater.from(getContext()).
                    inflate(R.layout.list_item_merchants, parent, false));
        }

        @Override
        public void onBindViewHolder(MerchantsListViewHolder holder, final int position) {
            holder.mMerchantLogoView.setBusinessProfilePicture(
                    mGetAllMerchantsResponse.getBranchResponseList().get(position).getBusinessLogo(), false);
            holder.mMerchantNameTextView.setText
                    (mGetAllMerchantsResponse.getBranchResponseList().get(position).getMerchantName());
            String branchNumberText = getContext().getResources().getString(R.string.accepted_in);
            branchNumberText = branchNumberText.replace("number", Integer.toString(mGetAllMerchantsResponse.getBranchResponseList().get(position).getBranches().size()));
            holder.mBranchNumberTextView.setText(branchNumberText);
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    merchantBranchSelectorDialog = new
                            MerchantBranchSelectorDialog(getContext(), mGetAllMerchantsResponse.getBranchResponseList().get(position));
                    merchantBranchSelectorDialog.showDialog();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mGetAllMerchantsResponse.getBranchResponseList().size();
        }

        public class MerchantsListViewHolder extends RecyclerView.ViewHolder {
            private ProfileImageView mMerchantLogoView;
            private TextView mMerchantNameTextView;
            private TextView mBranchNumberTextView;
            private View mainView;

            public MerchantsListViewHolder(View itemView) {
                super(itemView);
                mMerchantNameTextView = (TextView) itemView.findViewById(R.id.name);
                mMerchantLogoView = (ProfileImageView) itemView.findViewById(R.id.business_logo);
                mBranchNumberTextView = (TextView) itemView.findViewById(R.id.branch_number);
                mainView = itemView;
            }
        }
    }
}
