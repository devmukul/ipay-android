package bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.Comment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.GetTicketDetailsRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.GetTicketDetailsResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TicketDetailsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTicketDetailsTask = null;
    private GetTicketDetailsResponse mGetTicketDetailsResponse;

    private long ticketId;

    private RecyclerView mCommentListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Comment> mComments;
    private String requesterId;
    private CommentListAdapter mCommentListAdapter;

    private TextView mSubjectView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ticket_details, container, false);

        ticketId = getArguments().getLong(Constants.TICKET_ID);

        mSubjectView = (TextView) v.findViewById(R.id.textview_subject);

        mCommentListAdapter = new CommentListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCommentListRecyclerView = (RecyclerView) v.findViewById(R.id.list_comments);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mCommentListRecyclerView.setLayoutManager(mLayoutManager);
        mCommentListRecyclerView.setAdapter(mCommentListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    getTicketDetails();
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
        getTicketDetails();
    }

    private void getTicketDetails() {
        if (mGetTicketDetailsTask != null)
            return;

        mGetTicketDetailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TICKET_DETAILS,
                new GetTicketDetailsRequestBuilder().generateUri(ticketId).toString(), getActivity(), this);
        mGetTicketDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetTicketDetailsTask = null;
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
            return;
        }

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        if (getActivity() != null)
            mSwipeRefreshLayout.setRefreshing(false);

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_TICKET_DETAILS:
                try {
                    mGetTicketDetailsResponse = gson.fromJson(result.getJsonString(), GetTicketDetailsResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (isAdded())
                            setContentShown(true);

                        Log.w("Details", mGetTicketDetailsResponse.toString());

                        mComments = mGetTicketDetailsResponse.getResponse().getComments().getComments();
                        requesterId = mGetTicketDetailsResponse.getResponse().getTicket().getRequesterId();

                        mSubjectView.setText(mGetTicketDetailsResponse.getResponse().getTicket().getSubject());
                        mCommentListAdapter.notifyDataSetChanged();
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_loading_ticket_details, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_ticket_details, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetTicketDetailsTask = null;
                break;
        }
    }

    private class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_FROM_ME = 1;
        private static final int VIEW_TYPE_FROM_SUPPORT = 2;

        private class CommentViewHolder extends RecyclerView.ViewHolder {
            private TextView commentView;
            private TextView timeView;

            public CommentViewHolder(View itemView) {
                super(itemView);

                commentView = (TextView) itemView.findViewById(R.id.textview_comment);
                timeView = (TextView) itemView.findViewById(R.id.textview_time);
            }

            public void bindView(int pos) {
                final Comment comment = mComments.get(pos);

                commentView.setText(comment.getBody());
                timeView.setText(new SimpleDateFormat("dd/MM/yy, h:mm a").format(comment.getCreatedAt()));
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout;

            if (viewType == VIEW_TYPE_FROM_ME)
                layout = R.layout.list_item_ticket_comment_right;
            else
                layout = R.layout.list_item_ticket_comment_left;

            View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new CommentViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CommentViewHolder vh = (CommentViewHolder) holder;
            vh.bindView(position);
        }

        @Override
        public int getItemViewType(int position) {
            if (mComments.get(position).getAuthorId().equals(requesterId))
                return VIEW_TYPE_FROM_ME;
            else
                return VIEW_TYPE_FROM_SUPPORT;
        }

        @Override
        public int getItemCount() {
            if (mComments == null)
                return 0;
            else
                return mComments.size();
        }
    }
}
