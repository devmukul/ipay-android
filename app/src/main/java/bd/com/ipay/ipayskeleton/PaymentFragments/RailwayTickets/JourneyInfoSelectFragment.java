package bd.com.ipay.ipayskeleton.PaymentFragments.RailwayTickets;


import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.SelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.SelectorDialogWithSearch;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.SpinnerEditTextWithProgressBar;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetStationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetTrainListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.TrainList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Widgets.IPaySnackbar;

public class JourneyInfoSelectFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetStationFromListAsyncTask = null;
    private GetStationResponse mGetStationFromResponse;

    private HttpRequestGetAsyncTask mGetStationToListAsyncTask = null;
    private GetStationResponse mGetStationToResponse;

    private SpinnerEditTextWithProgressBar mSattionFromEditTextProgressBar;
    private SpinnerEditTextWithProgressBar mSattionToEditTextProgressBar;
    private SpinnerEditTextWithProgressBar mGenderEditTextProgressBar;
    private SpinnerEditTextWithProgressBar mAdultEditTextProgressBar;
    private SpinnerEditTextWithProgressBar mChildEditTextProgressBar;

    private EditText mSattionFromSelection;
    private EditText mSattionToSelection;
    private EditText mGenderSelection;
    private EditText mDateSelection;
    private EditText mChildSelection;
    private EditText mAdultSelection;

    private List<String> mStationFromList;
    private List<String> mStationToList;
    private List<String> mGenderList;

    private List<String> mAdultList;
    private List<String> mChildList;

    private SelectorDialogWithSearch stationFromSelectorDialog;
    private SelectorDialogWithSearch stationToSelectorDialog;
    private SelectorDialog genderSelectorDialog;

    private SelectorDialog childSelectorDialog;
    private SelectorDialog adultSelectorDialog;

    private Button mContinueButton;

    private String mSelectedSattionFrom = null;
    private String mSelectedSattionTo = null;
    private String mSelectedGender = null;
    private int mSelectedDate;
    private int mSelectedAdult = 0;
    private int mSelectedChild = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journey_info_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContinueButton = view.findViewById(R.id.continue_button);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        mContinueButton = view.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (verifyUserInputs()) {
                Bundle bundle = new Bundle();
                bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_STATION_FROM, mSelectedSattionFrom);
                bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_STATION_TO, mSelectedSattionTo);
                bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_DATE, mSelectedDate);
                bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_ADULTS, mSelectedAdult);
                bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_CHILD, mSelectedChild);
                bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_GENDER, mSelectedGender);
                ((IPayUtilityBillPayActionActivity) getActivity()).
                        switchFragment(new TrainSelectionFragment(), bundle, 2, true);

            }
            }
        });
        ((IPayUtilityBillPayActionActivity) getActivity()).setSupportActionBar(toolbar);
        ((IPayUtilityBillPayActionActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle("Buy Railway Tickets");


        mSattionFromEditTextProgressBar = view.findViewById(R.id.station_from);
        mSattionToEditTextProgressBar = view.findViewById(R.id.station_to);
        mGenderEditTextProgressBar = view.findViewById(R.id.gender);

        mAdultEditTextProgressBar = view.findViewById(R.id.adult_seat);
        mChildEditTextProgressBar = view.findViewById(R.id.child_seat);

        mSattionFromSelection = mSattionFromEditTextProgressBar.getEditText();
        mSattionToSelection = mSattionToEditTextProgressBar.getEditText();
        mGenderSelection = mGenderEditTextProgressBar.getEditText();

        mAdultSelection = mAdultEditTextProgressBar.getEditText();
        mChildSelection = mChildEditTextProgressBar.getEditText();

        mDateSelection = view.findViewById(R.id.journey_date);

        mDateSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        mSelectedDate = i2;
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(0);
                        cal.set(i, i1, i2, 0, 0, 0);
                        Date chosenDate = cal.getTime();
                        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                        String formattedDate = df.format(chosenDate);
                        mDateSelection.setText(formattedDate);
                    }
                },year,month,day);

                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                calendar.add(Calendar.DATE, 9);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        mAdultList = new ArrayList<>();
        mAdultList.add("1");
        mAdultList.add("2");
        mAdultList.add("3");
        mAdultList.add("4");
        setAdultAdapter(mAdultList);

        mGenderList = new ArrayList<>();
        mGenderList.add("Male");
        mGenderList.add("Female");
        setGenderAdapter(mGenderList);

        getStationFromList();
    }

    private void getStationFromList() {
        if (mGetStationFromListAsyncTask != null) {
            return;
        }
        mSattionFromEditTextProgressBar.showProgressBar();

        mGetStationFromListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_STATION_FROM,
                Constants.BASE_URL_CNS + Constants.URL_ORIGINATING_STATION, getContext(), this, true);
        mGetStationFromListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStationToList(String originatingStation) {
        if (mGetStationToListAsyncTask != null) {
            return;
        }
        mSattionToEditTextProgressBar.showProgressBar();

        mGetStationToListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_STATION_TO,
                Constants.BASE_URL_CNS + Constants.URL_ORIGINATING_STATION_TO + originatingStation, getContext(), this, true);
        mGetStationToListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setStationFromAdapter(List<String> stationList) {
        stationFromSelectorDialog = new SelectorDialogWithSearch(getContext(), getContext().getString(R.string.select_a_station), mStationFromList);
        stationFromSelectorDialog.setOnResourceSelectedListener(new SelectorDialogWithSearch.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(String name) {
                mSattionFromSelection.setError(null);
                mSattionFromSelection.setText(name);
                mSattionToSelection.setError(null);
                mSelectedSattionFrom = name;

                mStationToList = null;
                mSelectedSattionTo = null;
                mSattionToSelection.setText(getContext().getString(R.string.loading));
                getStationToList(mSelectedSattionFrom);
            }
        });

        mSattionFromSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stationFromSelectorDialog.show();
            }
        });
    }

    private void setStationToAdapter(List<String> thanaList) {
        stationToSelectorDialog = new SelectorDialogWithSearch(getContext(), getContext().getString(R.string.select_a_station), thanaList);
        stationToSelectorDialog.setOnResourceSelectedListener(new SelectorDialogWithSearch.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(String name) {
                mSattionToSelection.setError(null);
                mSattionToSelection.setText(name);
                mSelectedSattionTo = name;
            }
        });

        mSattionToSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stationToSelectorDialog.show();
            }
        });
    }

    private void setAdultAdapter(List<String> classList) {
        adultSelectorDialog = new SelectorDialog(getContext(), getContext().getString(R.string.select_no_of_adult), classList);
        adultSelectorDialog.setOnResourceSelectedListener(new SelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(String name) {
                mAdultSelection.setError(null);
                mAdultSelection.setText(name);
                mSelectedAdult = Integer.valueOf(name);
                mChildSelection.setError(null);

                if (Integer.valueOf(mSelectedAdult) < 4 && Integer.valueOf(mSelectedAdult) >0 ) {
                    mChildList = new ArrayList<>();
                    for (int i = 0; i <= 4 - Integer.valueOf(mSelectedAdult); i++) {
                        mChildList.add(String.valueOf(i));
                    }
                    setChildAdapter(mChildList);

                    if(Integer.valueOf(mSelectedAdult) == 1){
                        mGenderEditTextProgressBar.setVisibility(View.VISIBLE);
                    }else{
                        mGenderEditTextProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }

        });

        mAdultSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adultSelectorDialog.show();
            }
        });
    }

    private void setChildAdapter(List<String> classList) {
        childSelectorDialog = new SelectorDialog(getContext(), getContext().getString(R.string.select_no_of_child), classList);
        childSelectorDialog.setOnResourceSelectedListener(new SelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(String name) {
                mChildSelection.setError(null);
                mChildSelection.setText(name);
                mSelectedChild = Integer.valueOf(name);

                if((Integer.valueOf(mSelectedAdult) + Integer.valueOf(mSelectedChild) )== 1){
                    mGenderEditTextProgressBar.setVisibility(View.VISIBLE);
                }else{
                    mGenderEditTextProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        mChildSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childSelectorDialog.show();
            }
        });
    }

    private void setGenderAdapter(List<String> classList) {
        genderSelectorDialog = new SelectorDialog(getContext(), getContext().getString(R.string.select_a_gender), classList);
        genderSelectorDialog.setOnResourceSelectedListener(new SelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(String name) {
                mGenderSelection.setError(null);
                mGenderSelection.setText(name);
                if(name.equalsIgnoreCase("male"))
                {
                    mSelectedGender = "M";
                }else if(name.equalsIgnoreCase("female")){
                    mSelectedGender = "F";
                }else {
                    mSelectedGender = null;
                }
            }
        });

        mGenderSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderSelectorDialog.show();
            }
        });
    }

    protected void showErrorMessage(String errorMessage) {
        if (!TextUtils.isEmpty(errorMessage) && getActivity() != null) {
            IPaySnackbar.error(mContinueButton, errorMessage, IPaySnackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetStationFromListAsyncTask = null;
            mGetStationToListAsyncTask = null;
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_STATION_FROM)) {
            try {
                mGetStationFromResponse = gson.fromJson(result.getJsonString(), GetStationResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mStationFromList = mGetStationFromResponse.getStationList();
                    mSattionFromEditTextProgressBar.hideProgressBar();
                    setStationFromAdapter(mStationFromList);
                    mSattionFromSelection.setText(mSelectedSattionFrom);
                    if (TextUtils.isEmpty(mSelectedSattionFrom)) {
                        getStationToList(mSelectedSattionFrom);
                    }

                } else {
                    if (getContext() != null)
                        Toaster.makeText(getContext(), R.string.failed_loading_station_list, Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getContext() != null)
                    Toaster.makeText(getContext(), R.string.failed_loading_station_list, Toast.LENGTH_LONG);
            }

            mGetStationFromListAsyncTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_STATION_TO)) {
            try {
                mGetStationToResponse = gson.fromJson(result.getJsonString(), GetStationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mStationToList = mGetStationToResponse.getStationList();
                    mSattionToEditTextProgressBar.hideProgressBar();
                    mSattionToSelection.setText("");
                    setStationToAdapter(mStationToList);
                    mSattionToSelection.setText(mSelectedSattionTo);

                } else {
                    if (getContext() != null)
                        Toaster.makeText(getContext(), R.string.failed_loading_station_list, Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getContext() != null)
                    Toaster.makeText(getContext(), R.string.failed_loading_station_list, Toast.LENGTH_LONG);
            }

            mGetStationToListAsyncTask = null;
        }

    }
    public boolean verifyUserInputs() {
        boolean cancel = false;
        View focusedView = null;
        mSattionFromSelection.setError(null);
        mSattionToSelection.setError(null);
        mDateSelection.setError(null);
        mAdultSelection.setError(null);
        mChildSelection.setError(null);
        mGenderSelection.setError(null);

        if (mDateSelection.getText().toString().trim().length() == 0) {
            mDateSelection.setError(getContext().getString(R.string.invalid_journey_date));
            focusedView = mDateSelection;
            cancel = true;
        } else if (mSattionFromSelection.getText().toString().trim().length() == 0) {
            mSattionFromSelection.setError(getContext().getString(R.string.invalid_start_station));
            focusedView = mSattionFromSelection;
            cancel = true;
        } else if (mSattionToSelection.getText().toString().trim().length() == 0) {
            mSattionToSelection.setError(getContext().getString(R.string.invalid_start_station));
            focusedView = mSattionToSelection;
            cancel = true;
        } else if (mAdultSelection.getText().toString().trim().length() == 0) {
            mAdultSelection.setError(getContext().getString(R.string.invalid_no_of_ticket));
            focusedView = mAdultSelection;
            cancel = true;
        } else if (Integer.valueOf(mAdultSelection.getText().toString())== 1) {
            if (mGenderSelection.getText().toString().trim().length() == 0) {
                mGenderSelection.setError(getContext().getString(R.string.invalid_gender));
                focusedView = mGenderSelection;
                cancel = true;
            }
        }

        if (cancel) {
            focusedView.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}
