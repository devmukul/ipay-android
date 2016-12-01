package bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.AddCommentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.AddCommentResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.Comment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.GetTicketDetailsRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.GetTicketDetailsResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TicketDetailsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTicketDetailsTask = null;
    private GetTicketDetailsResponse mGetTicketDetailsResponse;

    private HttpRequestPostAsyncTask mNewCommentTask = null;
    private AddCommentResponse mAddCommentResponse;

    private long ticketId;

    private RecyclerView mCommentListRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Comment> mComments;
    private String requesterId;
    private CommentListAdapter mCommentListAdapter;

    private TextView mSubjectView;
    private FloatingActionButton mSubmitCommentButton;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ticket_details, container, false);

        ticketId = getArguments().getLong(Constants.TICKET_ID);

        mSubjectView = (TextView) v.findViewById(R.id.textview_subject);
        mSubmitCommentButton = (FloatingActionButton) v.findViewById(R.id.fab_new_comment);

        mCommentListAdapter = new CommentListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
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

        mProgressDialog = new ProgressDialog(getActivity());

        mSubmitCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCommentDialog();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTicketDetails();
    }

    private void showAddCommentDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .title(R.string.add_comment)
                .customView(R.layout.dialog_add_comment, false)
                .autoDismiss(false)
                .positiveText(R.string.submit)
                .negativeText(R.string.cancel);

        final EditText commentEditText = (EditText) dialog.build().getCustomView().findViewById(R.id.comment);
        dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String comment = commentEditText.getText().toString();
                if (comment.isEmpty()) {
                    commentEditText.setError(getString(R.string.comment_cannot_be_empty));
                } else {
                    addComment(comment);
                    Utilities.hideKeyboard(getActivity(), commentEditText);
                    dialog.dismiss();
                }
            }
        });

        dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Utilities.hideKeyboard(getActivity(), commentEditText);
                dialog.dismiss();
            }
        });

        dialog.show();
        Utilities.showKeyboard(getActivity());
    }

    private void getTicketDetails() {
        if (mGetTicketDetailsTask != null)
            return;

        setContentShown(false);

        mGetTicketDetailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TICKET_DETAILS,
                new GetTicketDetailsRequestBuilder().generateUri(ticketId).toString(), getActivity(), this);
        mGetTicketDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addComment(String comment) {
        if (mNewCommentTask != null)
            return;

        mProgressDialog.setMessage(getString(R.string.progress_dialog_submitting_comment));
        mProgressDialog.show();

        Gson gson = new Gson();

        AddCommentRequest addCommentRequest = new AddCommentRequest(mGetTicketDetailsResponse.getResponse().getTicket().getId(), comment);
        String json = gson.toJson(addCommentRequest);

        mNewCommentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_COMMENT, Constants.BASE_URL_ADMIN + Constants.URL_ADD_COMMENT,
                json, getActivity(), this);
        mNewCommentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (getActivity() != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressDialog.dismiss();
        }

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

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_TICKET_DETAILS:
                try {
                    mGetTicketDetailsResponse = gson.fromJson(result.getJsonString(), GetTicketDetailsResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                        mComments = mGetTicketDetailsResponse.getResponse().getComments().getComments();
                        requesterId = mGetTicketDetailsResponse.getResponse().getTicket().getRequesterId();

                        mSubjectView.setText(mGetTicketDetailsResponse.getResponse().getTicket().getSubject());
                        mCommentListAdapter.notifyDataSetChanged();

                        String ticketStatus = mGetTicketDetailsResponse.getResponse().getTicket().getStatus();

                        if (ticketStatus.equals(Constants.TICKET_STATUS_NEW)
                                || ticketStatus.equals(Constants.TICKET_STATUS_SOLVED)
                                || ticketStatus.equals(Constants.TICKET_STATUS_CLOSED)) {
                            mSubmitCommentButton.setVisibility(View.GONE);
                        }

                        if (isAdded())
                            setContentShown(true);
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

            case Constants.COMMAND_ADD_COMMENT:
                try {
                    mAddCommentResponse = gson.fromJson(result.getJsonString(), AddCommentResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.comment_successfully_added, Toast.LENGTH_LONG).show();
                            getTicketDetails();
                        }
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), mAddCommentResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_adding_comment, Toast.LENGTH_LONG).show();
                    }
                }

                mNewCommentTask = null;
                break;
        }

    }

    private class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_FROM_ME = 1;
        private static final int VIEW_TYPE_FROM_SUPPORT = 2;

        private class CommentViewHolder extends RecyclerView.ViewHolder {

            private ProfileImageView profilePictureView;
            private TextView commentView;
            private TextView timeView;

            public CommentViewHolder(View itemView) {
                super(itemView);

                profilePictureView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                commentView = (TextView) itemView.findViewById(R.id.textview_comment);
                timeView = (TextView) itemView.findViewById(R.id.textview_time);
            }

            public void bindView(int pos) {
                final Comment comment = mComments.get(pos);

                if (comment.getAuthorId().equals(requesterId)) {
                    profilePictureView.setProfilePicture(ProfileInfoCacheManager.getProfileImageUrl(), false);
                } else {
                    profilePictureView.setProfilePicture(R.drawable.ic_logo);
                }
                commentView.setText(comment.getBody());
                timeView.setText(Utilities.formatDateWithTime(comment.getCreatedAt()));
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
