package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork.GetTrustedPersonsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork.TrustedPerson;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TrustedNetworkFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTrustedPersonsTask = null;
    private GetTrustedPersonsResponse mGetTrustedPersonsResponse = null;

    private List<TrustedPerson> mTrustedPersons;
    private TrustedPersonListAdapter mTrustedPersonListAdapter;

    private FloatingActionButton mAddTrustedPersonButton;

    private RecyclerView mTrustedPersonListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mEmptyListTextView;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trusted_network, container, false);
        setTitle();

        mAddTrustedPersonButton = (FloatingActionButton) v.findViewById(R.id.fab_add_trusted_person);
        mTrustedPersonListRecyclerView = (RecyclerView) v.findViewById(R.id.list_trusted_person);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mProgressDialog = new ProgressDialog(getActivity());

        mTrustedPersonListAdapter = new TrustedPersonListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTrustedPersonListRecyclerView.setLayoutManager(mLayoutManager);
        mTrustedPersonListRecyclerView.setAdapter(mTrustedPersonListAdapter);

        mAddTrustedPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecuritySettingsActivity) getActivity()).switchToAddTrustedPerson();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    getTrustedPersons();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_TRUSTED_PERSON_UPDATE))
            getTrustedPersons();
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_TRUSTED_PERSON_UPDATE);

            if (json == null)
                getTrustedPersons();
            else {
                processGetTrustedPersonList(json);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utilities.isConnectionAvailable(getActivity())) {
            getTrustedPersons();
        }
    }

    private void getTrustedPersons() {
        if (mGetTrustedPersonsTask != null) {
            return;
        }

        setContentShown(false);

        mGetTrustedPersonsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRUSTED_PERSONS,
                Constants.BASE_URL_MM + Constants.URL_GET_TRUSTED_PERSONS, getActivity(), this);
        mGetTrustedPersonsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setTitle() {
        getActivity().setTitle(R.string.password_recovery);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetTrustedPersonsTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_TRUSTED_PERSONS)) {
            try {
                mGetTrustedPersonsResponse = gson.fromJson(result.getJsonString(), GetTrustedPersonsResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    processGetTrustedPersonList(result.getJsonString());

                    DataHelper dataHelper = DataHelper.getInstance(getActivity());
                    dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_TRUSTED_PERSON_UPDATE, result.getJsonString());

                    PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_TRUSTED_PERSON_UPDATE, false);
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mGetTrustedPersonsResponse.getMessage(), Toast.LENGTH_LONG).show();
                    ((HomeActivity) getActivity()).switchToDashBoard();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_loading_trusted_person_list, Toast.LENGTH_LONG).show();
                ((SecuritySettingsActivity) getActivity()).switchToAccountSettingsFragment();
            }

            mSwipeRefreshLayout.setRefreshing(false);
            mGetTrustedPersonsTask = null;
        }
        if (mTrustedPersons.isEmpty()) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListTextView.setVisibility(View.GONE);
        }
    }

    private void processGetTrustedPersonList(String json) {
        Gson gson = new Gson();
        mGetTrustedPersonsResponse = gson.fromJson(json, GetTrustedPersonsResponse.class);

        mTrustedPersons = mGetTrustedPersonsResponse.getTrustedPersons();
        mTrustedPersonListAdapter.notifyDataSetChanged();

        setContentShown(true);
    }


    public class TrustedPersonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public TrustedPersonListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mNameView;
            private TextView mMobileNumberView;
            private TextView mRelationshipView;

            public ViewHolder(final View itemView) {
                super(itemView);

                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);
                mRelationshipView = (TextView) itemView.findViewById(R.id.textview_relationship);
            }

            public void bindView(int pos) {

                final TrustedPerson trustedPerson = mTrustedPersons.get(pos);

                mNameView.setText(trustedPerson.getName());
                mMobileNumberView.setText(trustedPerson.getMobileNumber());
                mRelationshipView.setText(trustedPerson.getRelationship());
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trusted_network,
                    parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                ViewHolder vh = (ViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mTrustedPersons != null)
                return mTrustedPersons.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
