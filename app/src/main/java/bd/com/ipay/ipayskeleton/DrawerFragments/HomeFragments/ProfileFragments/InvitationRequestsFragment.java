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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.RecommendationAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvitationRequestsFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetInvitationRequestsTask = null;
    private GetInviteInfoResponse mInvitationRequestsResponse;

    private List<String> mInvitations;
    private InvitationRequestsListAdapter mInvitationListAdapter;

    private RecyclerView mInvitationListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog mProgressDialog;

    private TextView mEmptyListTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invitation_request, container, false);
        (getActivity()).setTitle(R.string.invitation);

        mInvitationListRecyclerView = (RecyclerView) v.findViewById(R.id.list_invitees);
        mProgressDialog = new ProgressDialog(getActivity());

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        if (Utilities.isConnectionAvailable(getActivity())) {
            getInvitationRequestsList();
        }

        mInvitationListAdapter = new InvitationRequestsListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mInvitationListRecyclerView.setLayoutManager(mLayoutManager);
        mInvitationListRecyclerView.setAdapter(mInvitationListAdapter);

        return v;
    }

    private void getInvitationRequestsList() {
        if (mGetInvitationRequestsTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_invitation_list));
        mProgressDialog.show();

        mGetInvitationRequestsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INVITE_INFO,
                Constants.BASE_URL_MM + "/" + Constants.URL_GET_INVITE_INFO, getActivity(), this);
        mGetInvitationRequestsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mGetInvitationRequestsTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_INVITE_INFO)) {
            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mInvitationRequestsResponse = gson.fromJson(resultList.get(2), GetInviteInfoResponse.class);

                        if (mInvitations == null || mInvitations.size() == 0) {
                            mInvitations = mInvitationRequestsResponse.getInvitees();
                        } else {
                            List<String> tempRecommendationRequestsClasses;
                            tempRecommendationRequestsClasses = mInvitationRequestsResponse.getInvitees();
                            mInvitations.addAll(tempRecommendationRequestsClasses);
                        }

                        if (mInvitations != null && mInvitations.size() > 0) {
                            mEmptyListTextView.setVisibility(View.GONE);
                        } else mEmptyListTextView.setVisibility(View.VISIBLE);

                        mInvitationListAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
        }

    }


    private class InvitationRequestsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public InvitationRequestsListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mMobileNumber;

            public ViewHolder(final View itemView) {
                super(itemView);
                mMobileNumber = (TextView) itemView.findViewById(R.id.invitee_mobile_number);
            }

            public void bindView(int pos) {
                final String MobileNumber = mInvitations.get(pos);
                mMobileNumber.setText(MobileNumber);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_invitees,
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
            if (mInvitations != null)
                return mInvitations.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}




