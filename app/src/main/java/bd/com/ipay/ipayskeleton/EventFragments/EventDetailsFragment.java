package bd.com.ipay.ipayskeleton.EventFragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

    private static final int REQUEST_CODE_PERMISSION = 1001;

    // TODO: Pass to create event fragment with some arguments while user needs to edit the event

    private HttpRequestGetAsyncTask mGetEventDetailsTask = null;
    // TODO: Change the response class
    private GetEventCategoriesResponse mGetEventCategoriesResponse;

    private HttpRequestGetAsyncTask mVerifyTicketTask = null;
    // TODO: Change the response class
    private GetEventCategoriesResponse mVerifyTicketResponse;

    private Button buttonEditEvent;
    private Button buttonSeeInOutList;
    private Button buttonVerifyTicket;
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
        buttonSeeInOutList = (Button) v.findViewById(R.id.in_out_list_button);
        buttonVerifyTicket = (Button) v.findViewById(R.id.verify_ticket_button);
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

        buttonVerifyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_PERMISSION);
                } else initiateScan();

            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
        getEventDetails();
    }

    private void initiateScan() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    // TODO: Modify the request
    private void getEventDetails() {
        if (mGetEventDetailsTask != null) {
            return;
        }
        mGetEventDetailsTask.mHttpResponseListener = this;
        mGetEventDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // TODO: Modify the request
    private void verifyTicket() {
        if (mVerifyTicketTask != null) {
            return;
        }
        mVerifyTicketTask.mHttpResponseListener = this;
        mVerifyTicketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateScan();
                } else {
                    Toast.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (scanResult == null) {
                return;
            }
            final String result = scanResult.getContents();
            if (result != null) {
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result.length() > 0) {
                            String[] decodedQRCode = Utilities.parseEventTicket(result);
                            // TODO: Call verify ticket service with the decoded elements from here

                        } else if (getActivity() != null)
                            Toast.makeText(getActivity(), getResources().getString(
                                    R.string.please_scan_a_valid_ticket), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetEventDetailsTask = null;
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
                        Toast.makeText(getActivity(), R.string.events_get_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.events_get_failed, Toast.LENGTH_SHORT).show();
            }

            mGetEventDetailsTask = null;
            if (this.isAdded()) setContentShown(true);

        } else if (result.getApiCommand().equals(Constants.COMMAND_EVENT_CATEGORIES)) {

            // TODO: modify the command and the rest for ticket verification here
            try {
                mGetEventCategoriesResponse = gson.fromJson(result.getJsonString(), GetEventCategoriesResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.ticket_verified)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Initiate scan ticket again when a ticket is scanned properly
                                    initiateScan();
                                }
                            })
                            .setIcon(R.drawable.ic_contacts_verified)
                            .show();
                    // TODO: Handle ticket not valid response
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.please_scan_a_valid_ticket, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.please_scan_a_valid_ticket, Toast.LENGTH_SHORT).show();
            }

            mVerifyTicketTask = null;
        }
    }
}
