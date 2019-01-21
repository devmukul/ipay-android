package bd.com.ipay.ipayskeleton.PaymentFragments.RailwayTickets;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;
import java.security.SecurityPermission;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.SelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.SpinnerEditTextWithProgressBar;
import bd.com.ipay.ipayskeleton.CustomView.EditTextWithProgressBar;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetStationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.GetTrainListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets.TrainList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.District;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.DistrictRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Thana;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.ThanaRequestBuilder;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard.Bank;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard.CreditCardInfoInputFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCardAmountInputFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CardNumberValidator;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Widget.View.CardNumberEditText;
import bd.com.ipay.ipayskeleton.Widgets.IPaySnackbar;

public class JourneyInfoSelectFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetStationFromListAsyncTask = null;
    private GetStationResponse mGetStationFromResponse;

    private HttpRequestGetAsyncTask mGetStationToListAsyncTask = null;
    private GetStationResponse mGetStationToResponse;

    private HttpRequestGetAsyncTask mGetTrainListAsyncTask = null;
    private GetTrainListResponse mTrainResponse;


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


    public static List<TrainList> mTrainList;
    private List<String> mStationFromList;
    private List<String> mStationToList;
    private List<String> mGenderList;

    private List<String> mAdultList;

    private SelectorDialog stationFromSelectorDialog;
    private SelectorDialog stationToSelectorDialog;
    private SelectorDialog genderSelectorDialog;

    private SelectorDialog childSelectorDialog;
    private SelectorDialog adultSelectorDialog;

    private Button mContinueButton;

    private String mSelectedSattionFrom = null;
    private String mSelectedSattionTo = null;
    private String mSelectedGender = null;
    private int mSelectedDate;
    private String mSelectedAdult = null;
    private String mSelectedChild = null;

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

                ((IPayUtilityBillPayActionActivity) getActivity()).
                        switchFragment(new TrainSelectionFragment(), null, 2, true);


                if (verifyUserInputs()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_STATION_FROM, mSelectedSattionFrom);
                    bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_STATION_TO, mSelectedSattionTo);
                    bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_DATE, mSelectedDate);
                    bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_ADULTS, Integer.valueOf(mSelectedAdult));
                    bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_CHILD, Integer.valueOf(mSelectedChild));
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
                calendar.add(Calendar.DATE, 8);
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();

//                DialogFragment newFragment = new MyDatePickerFragment();
//                newFragment.show(getActivity().getSupportFragmentManager(), "date picker");
            }
        });

        mAdultList = new ArrayList<>();
        mAdultList.add("1");
        mAdultList.add("2");
        mAdultList.add("3");
        mAdultList.add("4");
        setAdultAdapter(mAdultList);
        setChildAdapter(mAdultList);

        getStationFromList();

//        setTransactionImageResource(selectedBankIconId);
    }

    private void getStationFromList() {
        if (mGetStationFromListAsyncTask != null) {
            return;
        }
        mSattionFromEditTextProgressBar.showProgressBar();

        mGetStationFromListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DISTRICT_LIST,
                "http://10.10.10.11:8866/api/utility/cns/originating-station", getContext(), this, true);
        mGetStationFromListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStationToList(String originatingStation) {
        if (mGetStationToListAsyncTask != null) {
            return;
        }
        mSattionToEditTextProgressBar.showProgressBar();

        mGetStationToListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_THANA_LIST,
                "http://10.10.10.11:8866/api/utility/cns/originating-station?originatingStation="+originatingStation, getContext(), this, true);
        mGetStationToListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

//    private void getTrainList(String originatingStation, String destinationStation) {
//        if (mGetTrainListAsyncTask != null) {
//            return;
//        }
//        mClassEditTextProgressBar.showProgressBar();
//
//        mGetTrainListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_CONTACTS,
//                "http://10.10.10.11:8866/api/utility/cns/train?originatingStation="+originatingStation+"&destinationStation="+destinationStation, getContext(), this, true);
//        mGetTrainListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }

    private void setStationFromAdapter(List<String> stationList) {
        stationFromSelectorDialog = new SelectorDialog(getContext(), getContext().getString(R.string.select_a_district), mStationFromList);
        stationFromSelectorDialog.setOnResourceSelectedListener(new SelectorDialog.OnResourceSelectedListener() {
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

    private void setThanaAdapter(List<String> thanaList) {
        stationToSelectorDialog = new SelectorDialog(getContext(), getContext().getString(R.string.select_a_thana), thanaList);
        stationToSelectorDialog.setOnResourceSelectedListener(new SelectorDialog.OnResourceSelectedListener() {
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

//    private void setClassAdapter(List<String> classList) {
//        classSelectorDialog = new SelectorDialog(getContext(), getContext().getString(R.string.select_a_thana), classList);
//        classSelectorDialog.setOnResourceSelectedListener(new SelectorDialog.OnResourceSelectedListener() {
//            @Override
//            public void onResourceSelected(String name) {
//                mClassSelection.setError(null);
//                mClassSelection.setText(name);
//                mSelectedClass = name;
//            }
//        });
//
//        mClassSelection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                classSelectorDialog.show();
//            }
//        });
//    }

    private void setAdultAdapter(List<String> classList) {
        adultSelectorDialog = new SelectorDialog(getContext(), getContext().getString(R.string.select_a_thana), classList);
        adultSelectorDialog.setOnResourceSelectedListener(new SelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(String name) {
                mAdultSelection.setError(null);
                mAdultSelection.setText(name);
                mSelectedAdult = name;
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
        childSelectorDialog = new SelectorDialog(getContext(), getContext().getString(R.string.select_a_thana), classList);
        childSelectorDialog.setOnResourceSelectedListener(new SelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(String name) {
                mChildSelection.setError(null);
                mChildSelection.setText(name);
                mSelectedChild = name;
            }
        });

        mChildSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childSelectorDialog.show();
            }
        });
    }

//    public int getBankIcon(Bank bank) {
//        Resources resources = getContext().getResources();
//        int resourceId;
//        if (bank.getBankCode() != null)
//            resourceId = resources.getIdentifier("ic_bank" + bank.getBankCode(), "drawable",
//                    getContext().getPackageName());
//        else
//            resourceId = resources.getIdentifier("ic_bank" + "111", "drawable",
//                    getContext().getPackageName());
//        return resourceId;
//    }

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
            mGetTrainListAsyncTask = null;
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_DISTRICT_LIST)) {
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
                        Toaster.makeText(getContext(), R.string.failed_loading_district_list, Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getContext() != null)
                    Toaster.makeText(getContext(), R.string.failed_loading_district_list, Toast.LENGTH_LONG);
            }

            mGetStationFromListAsyncTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_THANA_LIST)) {
            try {
                mGetStationToResponse = gson.fromJson(result.getJsonString(), GetStationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mStationToList = mGetStationToResponse.getStationList();
                    mSattionToEditTextProgressBar.hideProgressBar();
                    mSattionToSelection.setText("");
                    setThanaAdapter(mStationToList);
                    mSattionToSelection.setText(mSelectedSattionTo);

                } else {
                    if (getContext() != null)
                        Toaster.makeText(getContext(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getContext() != null)
                    Toaster.makeText(getContext(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG);
            }

            mGetStationToListAsyncTask = null;
        }
//        else if (result.getApiCommand().equals(Constants.COMMAND_GET_CONTACTS)) {
//            try {
//                mTrainResponse = gson.fromJson(result.getJsonString(), GetTrainListResponse.class);
//                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
//                    mTrainList = mTrainResponse.getTrainList();
//                    Set<String> list3 = new LinkedHashSet<String>();
//                    for(TrainList trainList : mTrainList){
//                       list3.addAll(trainList.getClassList()) ;
//                    }
//
//                    mClassList = new ArrayList<>(list3);
//                    mClassEditTextProgressBar.hideProgressBar();
//                    mClassSelection.setText("");
//                    setClassAdapter(mClassList);
//                    mClassSelection.setText(mSelectedClass);
//
//                } else {
//                    if (getContext() != null)
//                        Toaster.makeText(getContext(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (getContext() != null)
//                    Toaster.makeText(getContext(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG);
//            }
//
//            mGetTrainListAsyncTask = null;
//        }

    }

//    public void setTransactionImageResource(int imageResource) {
//        if (getContext() != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                transactionImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), imageResource, getContext().getTheme()));
//            } else {
//                Glide.with(getContext()).load(imageResource)
//                        .asBitmap()
//                        .transform(new CircleTransform(getContext()))
//                        .crossFade()
//                        .into(transactionImageView);
//            }
//        }
//    }
//
//    public String getCardNumber() {
//        if (mCardNumberEditText.getText() != null)
//            return mCardNumberEditText.getText().toString();
//        else
//            return "";
//    }
//
//    public String getCardHolderName() {
//        if (mNameEditText.getText() != null) {
//            return mNameEditText.getText().toString();
//        } else {
//            return "";
//        }
//    }

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
            mDateSelection.setError(getContext().getString(R.string.invalid_address_line_1));
            focusedView = mDateSelection;
            cancel = true;
        } else if (mSattionFromSelection.getText().toString().trim().length() == 0) {
            mSattionFromSelection.setError(getContext().getString(R.string.invalid_district));
            focusedView = mSattionFromSelection;
            cancel = true;
        } else if (mSattionToSelection.getText().toString().trim().length() == 0) {
            mSattionToSelection.setError(getContext().getString(R.string.invalid_district));
            focusedView = mSattionToSelection;
            cancel = true;
        } else if (mAdultSelection.getText().toString().trim().length() == 0) {
            mAdultSelection.setError(getContext().getString(R.string.invalid_thana));
            focusedView = mAdultSelection;
            cancel = true;
        } else if (Integer.valueOf(mAdultSelection.getText().toString())== 1) {
            if (mGenderSelection.getText().toString().trim().length() == 0) {
                mGenderSelection.setError(getContext().getString(R.string.invalid_address_line_1));
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

//    protected boolean verifyInput() {
//        if (TextUtils.isEmpty(getCardNumber())) {
//            showErrorMessage(getString(R.string.empty_card_number_message));
//            return false;
//        } else if (!CardNumberValidator.validateCardNumber(getCardNumber())) {
//            showErrorMessage(getString(R.string.invalid_card_number_message));
//            return false;
//        } else {
//            if (TextUtils.isEmpty(getCardHolderName())) {
//                showErrorMessage(getString(R.string.enter_a_name));
//                return false;
//            } else {
//                return true;
//            }
//
//        }
//    }

    @SuppressLint("ValidFragment")
    private class MyDatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        }

        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                    }
                };
    }
}
