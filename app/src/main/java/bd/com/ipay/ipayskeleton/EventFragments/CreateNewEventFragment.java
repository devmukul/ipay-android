package bd.com.ipay.ipayskeleton.EventFragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Api.SyncContactsAsyncTask;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.CreateNewEventResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.GetEventCategoriesResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateNewEventFragment extends Fragment implements HttpResponseListener {

    private int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_CODE_PERMISSION = 1001;

    private HttpRequestPostAsyncTask mCreateEventTask = null;
    private CreateNewEventResponse mCreateNewEventResponse;

    private HttpRequestGetAsyncTask mGetEventCategoryTask = null;
    private GetEventCategoriesResponse mGetEventCategoriesResponse;

    private Button buttonCreateEvent;
    private EditText mNameOfEventEditText;
    private EditText mLocationOfEventEditText;
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
    private ImageView placePicker;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_new_event, container, false);
        buttonCreateEvent = (Button) v.findViewById(R.id.create_new_event_button);
        mLocationOfEventEditText = (EditText) v.findViewById(R.id.event_location);
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
        placePicker = (ImageView) v.findViewById(R.id.button_select_place);

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

        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRequestForPermission();
            }
        });

        attemptRequestForPermission();

        return v;
    }

    private void startPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attemptRequestForPermission() {
        String[] requiredPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    REQUEST_CODE_PERMISSION);
        } else startPlacePicker();
    }

    private void getCategories() {
        if (mGetEventCategoryTask != null) {
            return;
        }

        String mUri = Constants.BASE_URL_SM + Constants.URL_EVENT_CATEGORIES;
        mGetEventCategoryTask = new HttpRequestGetAsyncTask(Constants.COMMAND_EVENT_CATEGORIES,
                mUri, getActivity());
        mGetEventCategoryTask.mHttpResponseListener = this;

        mGetEventCategoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String placeName = String.format("%s", place.getName());

                mLocationOfEventEditText.setText(placeName);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                for (int i = 0; i < permissions.length; i++) {
                    Log.w(permissions[i], grantResults[i] + "");

                    if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            startPlacePicker();
                        } else {
                            MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
                            dialog.content(getString(R.string.request_for_storage_permission))
                                    .positiveText(R.string.allow_access)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            attemptRequestForPermission();
                                        }
                                    })
                                    .negativeText(R.string.exit)
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            // Nothing
                                        }
                                    })
                                    .show();
                        }
                    }
                }

                break;

        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mCreateEventTask = null;
            mGetEventCategoryTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

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

            mProgressDialog.dismiss();
            mGetEventCategoryTask = null;
        }
    }
}
