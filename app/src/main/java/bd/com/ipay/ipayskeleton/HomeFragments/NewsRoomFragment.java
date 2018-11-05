package bd.com.ipay.ipayskeleton.HomeFragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.WebViewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer.NewsRoom;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Offer.OfferResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class NewsRoomFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mNewsRoomTask = null;
    private RecyclerView mNewsRoomRecyclerView;
    private NewsRoomAdapter mNewsRoomAdapter;
    private List<NewsRoom> mNewsRoomList = new ArrayList<>();
    private TextView mEmptyListTextView;
    private boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_offer, container, false);
        if (getActivity() != null)
            getActivity().setTitle(R.string.offer);

        initializeViews(v);
        setupViewsAndActions();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getNewsRoomList();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                refreshNewsRoomList();
            }
        }
    }

    private void refreshNewsRoomList() {
        getNewsRoomList();
    }

    private void getNewsRoomList() {
        if (mNewsRoomTask != null) {
            return;
        }
        String url = "http://10.10.10.10:6397/api/cms/news";//Constants.BASE_URL_CMS + Constants.URL_NEWSROOM;
        mNewsRoomTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_NEWS,
                url, getActivity(), false);
        mNewsRoomTask.mHttpResponseListener = this;
        mNewsRoomTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadNewsRoomList(List<NewsRoom> newsRooms) {
        mNewsRoomList = new ArrayList<>();
        for (int i = 0; i < newsRooms.size(); i++) {
            NewsRoom values = newsRooms.get(i);
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();

            if (currentTime < values.getExpire_date()) {
                mNewsRoomList.add(values);
            }
        }

        if (mNewsRoomList != null && mNewsRoomList.size() > 0) {
            mNewsRoomRecyclerView.setVisibility(View.VISIBLE);
            mEmptyListTextView.setVisibility(View.GONE);
            mNewsRoomAdapter.notifyDataSetChanged();
            setContentShown(true);
        } else {
            mNewsRoomRecyclerView.setVisibility(View.GONE);
            mEmptyListTextView.setVisibility(View.VISIBLE);
        }

        if (isLoading)
            isLoading = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initializeViews(View v) {
        mEmptyListTextView = v.findViewById(R.id.empty_list_text);
        mNewsRoomRecyclerView = v.findViewById(R.id.list_transaction_history);
        mNewsRoomRecyclerView.setNestedScrollingEnabled(true);
    }

    private void setupViewsAndActions() {
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mNewsRoomAdapter = new NewsRoomAdapter();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mNewsRoomRecyclerView.setLayoutManager(mLayoutManager);
        mNewsRoomRecyclerView.setAdapter(mNewsRoomAdapter);
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mNewsRoomTask = null;
            setContentShown(true);
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_NEWS)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    OfferResponse mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), OfferResponse.class);
                    loadNewsRoomList(mTransactionHistoryResponse.getNewsRooms());
                } catch (Exception e) {
                    e.printStackTrace();
                    mNewsRoomRecyclerView.setVisibility(View.GONE);
                    mEmptyListTextView.setVisibility(View.VISIBLE);
                    if (isLoading)
                        isLoading = false;
                }
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.promotions_get_failed, Toast.LENGTH_LONG).show();
            }
            mNewsRoomTask = null;
            if (this.isAdded()) setContentShown(true);
        }
    }

    private class NewsRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView mPromoImageView;
            private final ProgressBar progressBar;

            public ViewHolder(final View itemView) {
                super(itemView);
                mPromoImageView = itemView.findViewById(R.id.offer_image);
                progressBar = itemView.findViewById(R.id.progress);
            }

            public void bindView(int pos) {
                final NewsRoom newsRoomList = mNewsRoomList.get(pos);
                final String imageUrl = newsRoomList.getImage_url();
                final String offerUrl = newsRoomList.getUrl();
                mPromoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(offerUrl)) {
                            try {
                                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                                intent.putExtra("url", "https://www.ipay.com.bd/promotions?link=" + offerUrl);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getContext(), R.string.no_browser_found_error_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                Glide.with(getContext())
                        .load(imageUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .crossFade()
                        .into(mPromoImageView);
            }


        }


        // Now define the view holder for Normal mNewsRoomList item
        class NormalViewHolder extends ViewHolder {
            NormalViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offer, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            try {
                NormalViewHolder vh = (NormalViewHolder) holder;
                vh.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mNewsRoomList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

    }
}
