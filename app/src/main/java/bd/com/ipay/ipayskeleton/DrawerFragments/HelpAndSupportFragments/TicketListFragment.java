package bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments;

import android.os.AsyncTask;
import android.os.Bundle;
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

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HelpAndSupportActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.GetTicketsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Ticket.Ticket;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TicketListFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTicketsTask = null;
    private GetTicketsResponse mGetTicketsResponse;

    private RecyclerView mTicketListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FloatingActionButton mNewTicketButton;

    private List<Ticket> mTickets;
    private TicketListAdapter mTicketListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        mNewTicketButton = (FloatingActionButton) v.findViewById(R.id.fab_new_ticket);

        mNewTicketButton.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (getActivity() != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetTicketsTask = null;
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
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
                try {
                    mGetTicketsResponse = gson.fromJson(result.getJsonString(), GetTicketsResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mTickets = mGetTicketsResponse.getResponse().getTickets();
                        mTicketListAdapter.notifyDataSetChanged();

                        if (isAdded())
                            setContentShown(true);
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_loading_tickets, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_tickets, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetTicketsTask = null;
                break;

        }
    }

    private class TicketListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ITEM_TYPE_TICKET = 1;
        private static final int ITEM_TYPE_FOOTER = 2;

        private class TicketViewHolder extends RecyclerView.ViewHolder {

            private TextView subjectView;
            private TextView descriptionView;
            private TextView timeView;
            private TextView statusView;

            public TicketViewHolder(View itemView) {
                super(itemView);

                subjectView = (TextView) itemView.findViewById(R.id.textview_subject);
                descriptionView = (TextView) itemView.findViewById(R.id.textview_description);
                timeView = (TextView) itemView.findViewById(R.id.textview_time);
                statusView = (TextView) itemView.findViewById(R.id.textview_status);
            }

            public void bindView(int pos) {
                final Ticket ticket = mTickets.get(pos);

                subjectView.setText(ticket.getSubject());
                descriptionView.setText(ticket.getDescription());
                timeView.setText(Utilities.getDateFormat(ticket.getCreatedAt()));
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
