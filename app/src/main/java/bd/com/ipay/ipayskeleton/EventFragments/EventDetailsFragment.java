package bd.com.ipay.ipayskeleton.EventFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.EventActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.GetEventCategoriesResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EventDetailsFragment extends ProgressFragment implements HttpResponseListener {

    // TODO: Pass to create event fragment with some arguments while user needs to edit the event

    private HttpRequestGetAsyncTask mGetEventDetaillsTask = null;
    // TODO: Change the response class
    private GetEventCategoriesResponse mGetEventCategoriesResponse;

    private Button buttonEditEvent;
    private Button buttonSeeInOutList;
    private TextView mNameOfEventTextView;
    private TextView mLocationOfEventTextView;
    private TextView mEventDescriptionTextView;
    private TextView mPerTicketCostTextView;
    private TextView mStartDateTextView;
    private TextView mEndDateTextView;
    private TextView mMaxNumberOfParticipantsTextView;
    private TextView mContactPersonNameTextView;
    private TextView mContactPersonNumberTextView;
    private TextView mNoOfParticipantsCanPayFromOneAccTextView;
    private TextView mEventLinkTextView;

    private TextView eventCategoryTextView;
    private TextView eventParticipantTypeTextView;
    private TextView eventStatusTextView;

    private long eventID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_details, container, false);
        buttonEditEvent = (Button) v.findViewById(R.id.edit_event_button);
        buttonEditEvent = (Button) v.findViewById(R.id.in_out_list_button);
        mLocationOfEventTextView = (EditText) v.findViewById(R.id.event_location);
        mNameOfEventTextView = (EditText) v.findViewById(R.id.event_name);
        mEventDescriptionTextView = (EditText) v.findViewById(R.id.event_description);
        mPerTicketCostTextView = (EditText) v.findViewById(R.id.per_ticket_cost);
        mStartDateTextView = (EditText) v.findViewById(R.id.start_date);
        mEndDateTextView = (EditText) v.findViewById(R.id.end_date);
        mMaxNumberOfParticipantsTextView = (EditText) v.findViewById(R.id.max_number_of_participants);
        mContactPersonNameTextView = (EditText) v.findViewById(R.id.contact_person);
        mContactPersonNumberTextView = (EditText) v.findViewById(R.id.contact_person_number);
        mNoOfParticipantsCanPayFromOneAccTextView = (EditText) v.findViewById(R.id.max_number_of_participants_can_pay_from_one_account);
        mEventLinkTextView = (EditText) v.findViewById(R.id.event_link);
        eventCategoryTextView = (TextView) v.findViewById(R.id.event_category);
        eventParticipantTypeTextView = (TextView) v.findViewById(R.id.event_participant_type);
        eventStatusTextView = (TextView) v.findViewById(R.id.event_status);

        if (getArguments() != null)
            eventID = getArguments().getLong(Constants.EVENT_ID);
        else {
            Toast.makeText(getActivity(), R.string.event_not_found, Toast.LENGTH_LONG).show();
            ((EventActivity) getActivity()).switchToEventFragments();
        }

        buttonEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // TODO: pass to create event with arguments to edit
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        buttonSeeInOutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    ((EventActivity) getActivity()).switchToInOutListFragments(eventID);
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
        getEventDetails(eventID);
    }

    // TODO: Modify the request
    private void getEventDetails(long eventId) {
        if (mGetEventDetaillsTask != null) {
            return;
        }

        RequestMoneyRequest mRequestMoneyRequest = new RequestMoneyRequest("", 0, "", "");

        Gson gson = new Gson();
        String json = gson.toJson(mRequestMoneyRequest);
        //mGetEventDetaillsTask = new HttpRequestGetAsyncTask();
        mGetEventDetaillsTask.mHttpResponseListener = this;
        mGetEventDetaillsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetEventDetaillsTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        // TODO: Modify the request command and response class
        if (result.getApiCommand().equals(Constants.COMMAND_EVENT_CATEGORIES)) {

            try {
                mGetEventCategoriesResponse = gson.fromJson(result.getJsonString(), GetEventCategoriesResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    // TODO
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_to_fetch_categories, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_to_fetch_categories, Toast.LENGTH_SHORT).show();
            }

            mGetEventDetaillsTask = null;
            if (this.isAdded()) setContentShown(true);
        }
    }
}
