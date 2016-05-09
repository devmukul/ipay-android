package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetIntroducedListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetIntroducerListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.GetSentRequestListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.Introducer;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.SentRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IntroducerFragment extends Fragment implements HttpResponseListener {


    private HttpRequestGetAsyncTask mGetIntroducerTask = null;
    private GetIntroducerListResponse mIntroducerListResponse;
    private List<Introducer> mIntroducerClasses;

    private ProgressDialog mProgressDialog;
    private RecyclerView mIntroducerRecyclerView;
    private TextView mEmptyListTextView;
    private IntroducerListAdapter mIntroducerListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private GetIntroducedListResponse mIntroducedListResponse;

    private GetSentRequestListResponse mSentRequestListResponse;
    private List<SentRequest> mSentRequestClasses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_introducer_requests, container, false);
        (getActivity()).setTitle(R.string.profile_introducers);

        mIntroducerRecyclerView = (RecyclerView) v.findViewById(R.id.list_introducer_requests);
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mProgressDialog = new ProgressDialog(getActivity());

        if (Utilities.isConnectionAvailable(getActivity())) {
           getIntroducerList();
           //getIntroducedList();
           //getSentRequestList();
        }

        mIntroducerListAdapter = new IntroducerListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mIntroducerRecyclerView.setLayoutManager(mLayoutManager);
        mIntroducerRecyclerView.setAdapter(mIntroducerListAdapter);

        return v;
    }

    private void getIntroducerList() {
        if (mGetIntroducerTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_introducer_list));
        mProgressDialog.show();
        mGetIntroducerTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INTRODUCER_LIST,
                Constants.BASE_URL + Constants.URL_GET_INTRODUCER_LIST, getActivity());
        mGetIntroducerTask.mHttpResponseListener = this;
        mGetIntroducerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getIntroducedList() {
        if (mGetIntroducerTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_introduced_list));
        mProgressDialog.show();
        mGetIntroducerTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INTRODUCED_LIST,
                Constants.BASE_URL + Constants.URL_GET_INTRODUCED_LIST, getActivity());
        mGetIntroducerTask.mHttpResponseListener = this;
        mGetIntroducerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getSentRequestList() {
        if (mGetIntroducerTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_sentRequest_list));
        mProgressDialog.show();
        mGetIntroducerTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SENTREQUEST_LIST,
                Constants.BASE_URL + Constants.URL_GET_SENTREQUEST_LIST, getActivity());
        mGetIntroducerTask.mHttpResponseListener = this;
        mGetIntroducerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mGetIntroducerTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_INTRODUCER_LIST)) {

            if (resultList.size() > 2) {
                try {
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        mIntroducerListResponse = gson.fromJson(resultList.get(2), GetIntroducerListResponse.class);

                        if (mIntroducerClasses == null) {
                            mIntroducerClasses = mIntroducerListResponse.getIntroducers();
                        } else {
                            List<Introducer> tempIntroducerClasses;
                            tempIntroducerClasses = mIntroducerListResponse.getIntroducers();
                            mIntroducerClasses.clear();
                            mIntroducerClasses.addAll(tempIntroducerClasses);
                        }

                        if (mIntroducerClasses != null && mIntroducerClasses.size() > 0)
                            mEmptyListTextView.setVisibility(View.GONE);
                        else mEmptyListTextView.setVisibility(View.VISIBLE);
                        mIntroducerListAdapter.notifyDataSetChanged();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_INTRODUCED_LIST)) {
            if (resultList.size() > 2) {
                try {
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        mIntroducedListResponse = gson.fromJson(resultList.get(2), GetIntroducedListResponse.class);

                        if (mIntroducerClasses == null) {
                            mIntroducerClasses = mIntroducedListResponse.getIntroducedList();
                        } else {
                            List<Introducer> tempIntroducerClasses;
                            tempIntroducerClasses = mIntroducedListResponse.getIntroducedList();
                            mIntroducerClasses.clear();
                            mIntroducerClasses.addAll(tempIntroducerClasses);
                        }

                        if (mIntroducerClasses != null && mIntroducerClasses.size() > 0)
                            mEmptyListTextView.setVisibility(View.GONE);
                        else mEmptyListTextView.setVisibility(View.VISIBLE);
                        mIntroducerListAdapter.notifyDataSetChanged();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();

        }

        else if (resultList.get(0).equals(Constants.COMMAND_GET_SENTREQUEST_LIST)) {
            if (resultList.size() > 2) {
                try {
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        mSentRequestListResponse = gson.fromJson(resultList.get(2), GetSentRequestListResponse.class);

                        if (mSentRequestClasses == null) {
                            mSentRequestClasses = mSentRequestListResponse.getSentRequestList();
                        } else {
                            List<SentRequest> tempIntroducerClasses;
                            tempIntroducerClasses = mSentRequestListResponse.getSentRequestList();
                            mSentRequestClasses.clear();
                            mSentRequestClasses.addAll(tempIntroducerClasses);
                        }

                        if (mSentRequestClasses != null && mSentRequestClasses.size() > 0)
                            mEmptyListTextView.setVisibility(View.GONE);
                        else mEmptyListTextView.setVisibility(View.VISIBLE);
                        mIntroducerListAdapter.notifyDataSetChanged();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();

        }

    }

    private class IntroducerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public IntroducerListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mIntroducerName;
            private TextView mIntroducerMobileNumber;
            private RoundedImageView mProfilePictureView;
            private ImageView mRequestStatus;

            public ViewHolder(final View itemView) {
                super(itemView);

                mProfilePictureView = (RoundedImageView) itemView.findViewById(R.id.profile_picture);
                mIntroducerName = (TextView) itemView.findViewById(R.id.introducer_name);
                mIntroducerMobileNumber = (TextView) itemView.findViewById(R.id.introducer_mobile_number);
                mRequestStatus = (ImageView) itemView.findViewById(R.id.request_status);

            }

            private void setProfilePicture(String url) {

                if (url != null) {
                    url = Constants.BASE_URL_IMAGE_SERVER + url;
                    Glide.with(getActivity())
                            .load(url)
                            .crossFade()
                            .error(R.drawable.ic_person)
                            .transform(new CircleTransform(getActivity()))
                            .into(mProfilePictureView);
                } else {
                    Glide.with(getActivity())
                            .load(R.drawable.ic_person)
                            .crossFade()
                            .into(mProfilePictureView);
                }

            }

            public void bindViewForIntroducer(int pos) {

                final String introducerName = mIntroducerClasses.get(pos).getName();
                final String introducerMobileNumber = mIntroducerClasses.get(pos).getMobileNumber();
                String imageUrl = mIntroducerClasses.get(pos).getprofilePictureUrl();
                setProfilePicture(imageUrl);
                mIntroducerName.setText(introducerName);
                mIntroducerMobileNumber.setText(introducerMobileNumber);
                mRequestStatus.setVisibility(View.GONE);
            }

            public void bindViewForSentRequest(int pos) {

                final String introducerName = mSentRequestClasses.get(pos).getName();
                final String introducerMobileNumber = mSentRequestClasses.get(pos).getMobileNumber();
                final String requestStatus = mSentRequestClasses.get(pos).getStatus();
                String imageUrl = mSentRequestClasses.get(pos).getProfilePictureUrl();
                setProfilePicture(imageUrl);
                mIntroducerName.setText(introducerName);
                mIntroducerMobileNumber.setText(introducerMobileNumber);

                if (requestStatus.equals(Constants.RECOMMENDATION_STATUS_PENDING)) {
                    mRequestStatus.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                } else if (requestStatus.equals(Constants.RECOMMENDATION_STATUS_APPROVED)) {
                    mRequestStatus.setImageResource(R.drawable.ic_verified_user_black_24dp);
                } else if (requestStatus.equals(Constants.RECOMMENDATION_STATUS_SPAM)) {
                    mRequestStatus.setImageResource(R.drawable.ic_error_black_24dp);
                } else {
                    // RECOMMENDATION_STATUS_REJECTED
                    mRequestStatus.setImageResource(R.drawable.ic_warning_black_24dp);
                }

            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_introducer,
                    parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                ViewHolder vh = (ViewHolder) holder;
                vh.bindViewForIntroducer(position);  // for introducer and introduced list
               //vh.bindViewForSentRequest(position);   // for sent request list


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if(mIntroducerClasses != null && mSentRequestClasses != null)
                return mIntroducerClasses.size() + mSentRequestClasses.size();
            else
            {
                if(mIntroducerClasses != null)
                    return mIntroducerClasses.size();
                else if (mSentRequestClasses != null)
                    return mSentRequestClasses.size();
                else
                    return 0;
            }

            /*
            if (mSentRequestClasses != null)
            return mSentRequestClasses.size();
            else return 0;
            */

            /*
            if (mIntroducerClasses != null)
                return mIntroducerClasses.size();
            else return 0;
            */
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
