package bd.com.ipay.ipayskeleton.EventFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.CreateNewEventResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.GetEventCategoriesRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.GetEventCategoriesResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateNewEventFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCreateEventTask = null;
    private CreateNewEventResponse mCreateNewEventResponse;

    private HttpRequestGetAsyncTask mGetEventCategoryTask = null;
    private GetEventCategoriesResponse mGetEventCategoriesResponse;

    private Button buttonCreateEvent;
    private EditText mNameOfEventEditText;
    private EditText mEventDescriptionEditText;
    private EditText mPerTicketCostEditText;
    private EditText mStartDateEditText;
    private EditText mEndDateEditText;
    private EditText mMaxNumberOfParticipantsEditText;
    private EditText mContactPersonNameEditText;
    private EditText mContactPersonNumberEditText;
    private EditText mNoOfParticipantsCanPayFromOneAccEditText;
    private EditText mEventLinkEditText;

    private Spinner eventCategorySpinner;
    private Spinner eventParticipantTypeSpinner;
    private Spinner eventStatusSpinner;

    private ImageView startDatePicker;
    private ImageView endDatePicker;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_new_event, container, false);
        buttonCreateEvent = (Button) v.findViewById(R.id.create_new_event_button);
        mNameOfEventEditText = (EditText) v.findViewById(R.id.event_name);
        mEventDescriptionEditText = (EditText) v.findViewById(R.id.event_description);
        mPerTicketCostEditText = (EditText) v.findViewById(R.id.per_ticket_cost);
        mStartDateEditText = (EditText) v.findViewById(R.id.start_date);
        mEndDateEditText = (EditText) v.findViewById(R.id.end_date);
        mMaxNumberOfParticipantsEditText = (EditText) v.findViewById(R.id.max_number_of_participants);
        mContactPersonNameEditText = (EditText) v.findViewById(R.id.contact_person);
        mContactPersonNumberEditText = (EditText) v.findViewById(R.id.contact_person_number);
        mNoOfParticipantsCanPayFromOneAccEditText = (EditText) v.findViewById(R.id.max_number_of_participants_can_pay_from_one_account);
        mEventLinkEditText = (EditText) v.findViewById(R.id.event_link);
        eventCategorySpinner = (Spinner) v.findViewById(R.id.spinner_category);
        eventParticipantTypeSpinner = (Spinner) v.findViewById(R.id.spinner_participation_type);
        eventStatusSpinner = (Spinner) v.findViewById(R.id.spinner_event_status);
        startDatePicker = (ImageView) v.findViewById(R.id.startDatePicker);
        endDatePicker = (ImageView) v.findViewById(R.id.endDatePicker);

        // Get event categories
        getCategories();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.submitting_request_money));

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) { // TODO: create event
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    private void getCategories() {
        if (mGetEventCategoryTask != null) {
            return;
        }

        GetEventCategoriesRequestBuilder mGetEventCategoriesRequestBuilder =
                new GetEventCategoriesRequestBuilder();

        String mUri = mGetEventCategoriesRequestBuilder.getGeneratedUri();
        mGetEventCategoryTask = new HttpRequestGetAsyncTask(Constants.COMMAND_EVENT_CATEGORIES,
                mUri, getActivity());
        mGetEventCategoryTask.mHttpResponseListener = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mGetEventCategoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mGetEventCategoryTask.execute((Void) null);
        }
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mCreateEventTask = null;
            mGetEventCategoryTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_EVENT_CATEGORIES)) {

            if (resultList.size() > 2) {
                try {
                    mGetEventCategoriesResponse = gson.fromJson(resultList.get(2), GetEventCategoriesResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
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
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_to_fetch_categories, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mGetEventCategoryTask = null;
        }
    }
}
