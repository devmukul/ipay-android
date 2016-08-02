package bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.List;

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

    private List<Ticket> mTickets;
    private TicketListAdapter mTicketListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ticket_list, container, false);

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
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetTicketsTask = null;
            if (getActivity() != null) {
                getActivity().onBackPressed();
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_TICKETS:
                try {
                    mGetTicketsResponse = gson.fromJson(result.getJsonString(), GetTicketsResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mTickets = mGetTicketsResponse.getTickets();
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
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_tickets, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetTicketsTask = null;
                break;

        }
    }

    private class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.TicketViewHolder> {

        public class TicketViewHolder extends RecyclerView.ViewHolder {

            public TicketViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ticket, parent, false);
            return new TicketViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TicketViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
