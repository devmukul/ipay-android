package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.GetNewsFeedRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.GetNewsFeedResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.News;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import link.fls.swipestack.SwipeStack;

public class NewsFeedAndOfferFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetNewsFeedTask = null;
    private GetNewsFeedResponse mGetNewsFeedResponse;

    private SwipeStack swipeStack;
    private NewsFeedAdapter mNewsFeedAdapter;
    private List<News> newsFeedResponsesList;

    private int pageCount = 0;
    private boolean hasNext = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_news_feed_and_offer, container, false);
        mNewsFeedAdapter = new NewsFeedAdapter();
        SwipeStack swipeStack = (SwipeStack) v.findViewById(R.id.swipeStack);
        swipeStack.setAdapter(mNewsFeedAdapter);

        getNewsFeed();

        return v;
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
            mGetNewsFeedTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();

            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_NEWS_FEED)) {

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

            mGetNewsFeedTask = null;

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

}
