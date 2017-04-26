package bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.HelpAndSupportActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.GetTicketsResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.Ticket;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TicketListFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTicketsTask = null;
    private GetTicketsResponse mGetTicketsResponse;

    private RecyclerView mTicketListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FloatingActionButton mFabCreateNewTicket;
    private TextView mEmptyListTextView;

    private List<Ticket> mTickets;
    private TicketListAdapter mTicketListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        setTitle();

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);

        mFabCreateNewTicket = (FloatingActionButton) v.findViewById(R.id.fab_new_ticket);

        mFabCreateNewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HelpAndSupportActivity) getActivity()).switchToCreateTicketFragment();
            }
        });

        mTicketListAdapter = new TicketListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTicketListRecyclerView = (RecyclerView) v.findViewById(R.id.list_tickets);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mTicketListRecyclerView.setLayoutManager(mLayoutManager);
        mTicketListRecyclerView.setAdapter(mTicketListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity()))
                    getTickets();
            }
        });

        return v;
    }

    private void setTitle() {
        getActivity().setTitle(R.string.help);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
        getTickets();
    }

    private void getTickets() {
        if (mGetTicketsTask != null) {
            return;
        }

        mGetTicketsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TICKETS,
                Constants.BASE_URL_ADMIN + Constants.URL_GET_TICKETS, getActivity(), this);
        mGetTicketsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showErrorDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.sorry)
                .content(R.string.support_not_available)
                .cancelable(false)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getActivity().onBackPressed();
                    }
                })
                .show();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (getActivity() != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetTicketsTask = null;
            if (getActivity() != null) {
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
                showErrorDialog();
            }
            return;
        }

        /**
         * Admin module sends names like created_at instead of createdAt. Ugh!
         */
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_TICKETS:
                if (isAdded())
                    setContentShown(true);
                mFabCreateNewTicket.setVisibility(View.VISIBLE);

                try {
                    mGetTicketsResponse = gson.fromJson(result.getJsonString(), GetTicketsResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (mGetTicketsResponse.getResponse() != null) {
                            mTickets = mGetTicketsResponse.getResponse().getTickets();
                            if (mTickets != null) {
                                mTicketListAdapter.notifyDataSetChanged();
                                mEmptyListTextView.setVisibility(View.GONE);
                            } else
                                mEmptyListTextView.setVisibility(View.VISIBLE);

                        } else
                            mEmptyListTextView.setVisibility(View.VISIBLE);

                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND ||
                            result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE) {
                        mEmptyListTextView.setVisibility(View.VISIBLE);

                        if (getActivity() != null) {
                            showErrorDialog();
                        }

                    } else {
                        if (getActivity() != null) {
                            showErrorDialog();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        showErrorDialog();
                    }
                }

                mGetTicketsTask = null;
                break;
        }
    }

    private class TicketListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ITEM_TYPE_TICKET = 1;

        private class TicketViewHolder extends RecyclerView.ViewHolder {

            private TextView subjectView;
            private TextView categoryView;
            private TextView timeView;
            private TextView statusView;

            public TicketViewHolder(View itemView) {
                super(itemView);

                subjectView = (TextView) itemView.findViewById(R.id.textview_subject);
                categoryView = (TextView) itemView.findViewById(R.id.textview_category);
                timeView = (TextView) itemView.findViewById(R.id.textview_time);
                statusView = (TextView) itemView.findViewById(R.id.textview_status);
            }

            public void bindView(int pos) {
                final Ticket ticket = mTickets.get(pos);

                subjectView.setText(ticket.getId() + " : " + ticket.getSubject());
                categoryView.setText(getString(R.string.category) + ": " + ticket.getCategory());
                timeView.setText(Utilities.formatDateWithTime(ticket.getCreatedAt()));
                statusView.setText(ticket.getStatus().toUpperCase());

                switch (ticket.getStatus()) {
                    case Constants.TICKET_STATUS_NEW:
                        statusView.setTextColor(getResources().getColor(R.color.bottle_green));
                        break;
                    case Constants.TICKET_STATUS_OPEN:
                    case Constants.TICKET_STATUS_ON_HOLD:
                        statusView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        break;
                    case Constants.TICKET_STATUS_PENDING:
                        statusView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        break;
                    case Constants.TICKET_STATUS_SOLVED:
                    case Constants.TICKET_STATUS_CLOSED:
                        statusView.setTextColor(getResources().getColor(R.color.colorGray));
                        break;
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HelpAndSupportActivity) getActivity()).switchToTicketDetailsFragment(ticket.getId());
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ticket, parent, false);
            return new TicketViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TicketViewHolder) {
                TicketViewHolder vh = (TicketViewHolder) holder;
                vh.bindView(position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return ITEM_TYPE_TICKET;
        }

        @Override
        public int getItemCount() {
            if (mTickets != null)
                return mTickets.size();
            else
                return 0;
        }
    }
}
