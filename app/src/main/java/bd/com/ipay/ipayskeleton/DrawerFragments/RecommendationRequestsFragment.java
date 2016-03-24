package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.GetRecommendationRequests;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.GetRecommendationRequestsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.RecommendRequestClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.RecommendationActionRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.RecommendationActionResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RecommendationRequestsFragment extends Fragment implements HttpResponseListener {


    private HttpRequestPostAsyncTask mRecommendActionTask = null;
    private RecommendationActionResponse mRecommendationActionResponse;

    private HttpRequestPostAsyncTask mGetRecommendationRequestsTask = null;
    private GetRecommendationRequestsResponse mRecommendationRequestsResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mRecommendationRequestsRecyclerView;
    private TextView mEmptyListTextView;
    private RecommendationRequestsListAdapter mRecommendationRequestsListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<RecommendRequestClass> mRecommendationRequestsClasses;
    private SharedPreferences pref;
    private String mUserID;

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
        View v = inflater.inflate(R.layout.fragment_recommendation_requests, container, false);
        ((HomeActivity) getActivity()).setTitle(R.string.recommendation);

        mRecommendationRequestsRecyclerView = (RecyclerView) v.findViewById(R.id.list_recommendation_requests);
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mProgressDialog = new ProgressDialog(getActivity());
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mUserID = pref.getString(Constants.USERID, "");

        if (Utilities.isConnectionAvailable(getActivity())) {
            getRecommendationRequestsList();
        }

        mRecommendationRequestsListAdapter = new RecommendationRequestsListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecommendationRequestsRecyclerView.setLayoutManager(mLayoutManager);
        mRecommendationRequestsRecyclerView.setAdapter(mRecommendationRequestsListAdapter);

        return v;
    }

    private void getRecommendationRequestsList() {
        if (mGetRecommendationRequestsTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_recommendation_list));
        mProgressDialog.show();
        GetRecommendationRequests mGetRecommendationRequests = new GetRecommendationRequests(mUserID);
        Gson gson = new Gson();
        String json = gson.toJson(mGetRecommendationRequests);
        mGetRecommendationRequestsTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_RECOMMENDATION_REQUESTS,
                Constants.BASE_URL_POST_MM + Constants.URL_GET_RECOMMENDATION_REQUESTS, json, getActivity());
        mGetRecommendationRequestsTask.mHttpResponseListener = this;
        mGetRecommendationRequestsTask.execute((Void) null);
    }

    private void attemptRecommendAction(long requestID, String recommendationStatus) {
        if (requestID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.verifying_user));
        mProgressDialog.show();
        RecommendationActionRequest mRecommendationActionRequest = new RecommendationActionRequest(requestID, recommendationStatus);
        Gson gson = new Gson();
        String json = gson.toJson(mRecommendationActionRequest);
        mRecommendActionTask = new HttpRequestPostAsyncTask(Constants.COMMAND_RECOMMEND_ACTION,
                Constants.BASE_URL_POST_MM + Constants.URL_RECOMMEND_ACTION, json, getActivity());
        mRecommendActionTask.mHttpResponseListener = this;
        mRecommendActionTask.execute((Void) null);
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mRecommendActionTask = null;
            mGetRecommendationRequestsTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_RECOMMENDATION_REQUESTS)) {

            if (resultList.size() > 2) {
                try {
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        mRecommendationRequestsResponse = gson.fromJson(resultList.get(2), GetRecommendationRequestsResponse.class);

                        if (mRecommendationRequestsClasses == null) {
                            mRecommendationRequestsClasses = mRecommendationRequestsResponse.getVerificationRequestList();
                        } else {
                            List<RecommendRequestClass> tempRecommendationRequestsClasses;
                            tempRecommendationRequestsClasses = mRecommendationRequestsResponse.getVerificationRequestList();
                            mRecommendationRequestsClasses.clear();
                            mRecommendationRequestsClasses.addAll(tempRecommendationRequestsClasses);
                        }

                        if (mRecommendationRequestsClasses != null && mRecommendationRequestsClasses.size() > 0)
                            mEmptyListTextView.setVisibility(View.GONE);
                        else mEmptyListTextView.setVisibility(View.VISIBLE);
                        mRecommendationRequestsListAdapter.notifyDataSetChanged();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mGetRecommendationRequestsTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_RECOMMEND_ACTION)) {

            if (resultList.size() > 2) {
                try {
                    mRecommendationActionResponse = gson.fromJson(resultList.get(2), RecommendationActionResponse.class);
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mRecommendationActionResponse.getMessage(), Toast.LENGTH_LONG).show();

                        // Refresh recommendation requests list
                        if (mRecommendationRequestsClasses != null)
                            mRecommendationRequestsClasses.clear();
                        mRecommendationRequestsClasses = null;
                        getRecommendationRequestsList();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mRecommendationActionResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mRecommendActionTask = null;
        }
    }

    private class RecommendationRequestsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public RecommendationRequestsListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mSenderName;
            private TextView mSenderMobileNumber;
            private ImageView mRecommendationStatus;
            private TextView mDate;
            private LinearLayout optionsLayout;
            private Button verifyButton;
            private Button rejectButton;
            private Button markAsSpamButton;

            public ViewHolder(final View itemView) {
                super(itemView);

                mSenderName = (TextView) itemView.findViewById(R.id.sender_name);
                mSenderMobileNumber = (TextView) itemView.findViewById(R.id.sender_mobile_number);
                mRecommendationStatus = (ImageView) itemView.findViewById(R.id.recommendation_status);
                mDate = (TextView) itemView.findViewById(R.id.request_date);
                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                verifyButton = (Button) itemView.findViewById(R.id.verify_button);
                rejectButton = (Button) itemView.findViewById(R.id.reject_button);
                markAsSpamButton = (Button) itemView.findViewById(R.id.mark_as_spam_button);
            }

            public void bindView(int pos) {

                final long requestID = mRecommendationRequestsClasses.get(pos).getId();
                final String senderName = mRecommendationRequestsClasses.get(pos).getSenderName();
                final String senderMobileNumber = mRecommendationRequestsClasses.get(pos).getSenderMobileNumber();
                final String recommendationStatus = mRecommendationRequestsClasses.get(pos).getStatus();
                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, H:MM a").format(mRecommendationRequestsClasses.get(pos).getDate());


                mSenderName.setText(senderName);
                mSenderMobileNumber.setText(senderMobileNumber);
                mDate.setText(time);

                if (recommendationStatus.equals(Constants.RECOMMENDATION_STATUS_PENDING)) {
                    mRecommendationStatus.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                } else if (recommendationStatus.equals(Constants.RECOMMENDATION_STATUS_APPROVED)) {
                    mRecommendationStatus.setImageResource(R.drawable.ic_verified_user_black_24dp);
                } else if (recommendationStatus.equals(Constants.RECOMMENDATION_STATUS_SPAM)) {
                    mRecommendationStatus.setImageResource(R.drawable.ic_error_black_24dp);
                } else {
                    // RECOMMENDATION_STATUS_REJECTED
                    mRecommendationStatus.setImageResource(R.drawable.ic_warning_black_24dp);
                }

                optionsLayout.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.RECOMMENDATION_STATUS_PENDING)) {
                            if (optionsLayout.getVisibility() == View.VISIBLE)
                                optionsLayout.setVisibility(View.GONE);
                            else optionsLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });

                verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.RECOMMENDATION_STATUS_PENDING))
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.are_you_sure)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            attemptRecommendAction(requestID, Constants.RECOMMENDATION_STATUS_APPROVED);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                        }
                                    })
                                    .show();
                    }
                });

                rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.RECOMMENDATION_STATUS_PENDING))
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.are_you_sure)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            attemptRecommendAction(requestID, Constants.RECOMMENDATION_STATUS_REJECTED);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                        }
                                    })
                                    .show();
                    }
                });

                markAsSpamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recommendationStatus.equalsIgnoreCase(Constants.RECOMMENDATION_STATUS_PENDING))
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.are_you_sure)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            attemptRecommendAction(requestID, Constants.RECOMMENDATION_STATUS_SPAM);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                        }
                                    })
                                    .show();
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recommendation_requests,
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
            if (mRecommendationRequestsClasses != null)
                return mRecommendationRequestsClasses.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
