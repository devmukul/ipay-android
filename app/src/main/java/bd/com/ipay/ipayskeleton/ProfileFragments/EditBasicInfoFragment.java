package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.SetProfileInfoRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.SetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Occupation;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EditBasicInfoFragment extends BaseFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSetProfileInfoTask = null;

    private ResourceSelectorDialog<Occupation> mOccupationTypeResourceSelectorDialog;

    private EditText mNameEditText;
    private EditText mDateOfBirthEditText;
    private final DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mDateOfBirthEditText.setText(
                            String.format(Locale.getDefault(), Constants.DATE_FORMAT, dayOfMonth, monthOfYear + 1, year));
                }
            };
    private EditText mOccupationEditText;
    private EditText mOrganizationNameEditText;
    private CheckBox mFemaleCheckBox;
    private CheckBox mMaleCheckBox;
    private ProgressDialog mProgressDialog;
    private String mName = "";
    private DatePickerDialog mDatePickerDialog;
    private String mDateOfBirth = "";
    private String mGender = null;
    private String mOrganizationName;
    private int mOccupation = -1;
    private List<Occupation> mOccupationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_basic_info, container, false);

        if (ProfileInfoCacheManager.isBusinessAccount())
            getActivity().setTitle(getString(R.string.edit_contact_info));
        else getActivity().setTitle(getString(R.string.edit_basic_info));
        Bundle bundle = getArguments();

        mName = bundle.getString(Constants.NAME);
        mDateOfBirth = bundle.getString(Constants.DATE_OF_BIRTH);
        mGender = bundle.getString(Constants.GENDER);
        mOccupation = bundle.getInt(Constants.OCCUPATION);
        mOccupationList = bundle.getParcelableArrayList(Constants.OCCUPATION_LIST);
        mOrganizationName = bundle.getString(Constants.ORGANIZATION_NAME);

        Button infoSaveButton = (Button) view.findViewById(R.id.button_save);
        mNameEditText = (EditText) view.findViewById(R.id.name);
        mDateOfBirthEditText = (EditText) view.findViewById(R.id.birthdayEditText);
        mOccupationEditText = (EditText) view.findViewById(R.id.occupationEditText);
        mOrganizationNameEditText = (EditText) view.findViewById(R.id.organizationNameEditText);
        mMaleCheckBox = (CheckBox) view.findViewById(R.id.checkBoxMale);
        mFemaleCheckBox = (CheckBox) view.findViewById(R.id.checkBoxFemale);
        mProgressDialog = new ProgressDialog(getActivity());

        Date date = Utilities.formatDateFromString(mDateOfBirth);
        mDatePickerDialog = Utilities.getDatePickerDialog(getActivity(), date, mDateSetListener);

        mDateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

        mMaleCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaleCheckBox.setChecked(true);
                mFemaleCheckBox.setChecked(false);

                setGenderCheckBoxTextColor(mMaleCheckBox.isChecked(), mFemaleCheckBox.isChecked());
            }
        });

        mFemaleCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFemaleCheckBox.setChecked(true);
                mMaleCheckBox.setChecked(false);

                setGenderCheckBoxTextColor(mMaleCheckBox.isChecked(), mFemaleCheckBox.isChecked());
            }
        });

        infoSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    Utilities.hideKeyboard(getActivity());
                    attemptSaveBasicInfo();
                }
            }
        });

        setProfileInformation();

        setOccupationAdapter();
        setOccupation();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_basic_info_edit));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    private void setGenderCheckBoxTextColor(boolean maleCheckBoxChecked, boolean femaleCheckBoxChecked) {
        if (maleCheckBoxChecked)
            mMaleCheckBox.setTextColor((Color.WHITE));
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mMaleCheckBox.setTextColor(getContext().getResources().getColor(R.color.colorPrimary, getActivity().getTheme()));
            } else {
                //noinspection deprecation
                mMaleCheckBox.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }

        if (femaleCheckBoxChecked)
            mFemaleCheckBox.setTextColor((Color.WHITE));
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mFemaleCheckBox.setTextColor(getContext().getResources().getColor(R.color.colorPrimary, getActivity().getTheme()));
            } else {
                //noinspection deprecation
                mFemaleCheckBox.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        mName = mNameEditText.getText().toString().trim();
        mDateOfBirth = mDateOfBirthEditText.getText().toString().trim();
        mOrganizationName = mOrganizationNameEditText.getText().toString().trim();

        if (mOrganizationName.isEmpty())
            mOrganizationName = null;

        if (mMaleCheckBox.isChecked())
            mGender = GenderList.genderNameToCodeMap.get(
                    getString(R.string.male));

        if (mFemaleCheckBox.isChecked())
            mGender = GenderList.genderNameToCodeMap.get(
                    getString(R.string.female));

        if (mOccupation < 0) {
            mOccupationEditText.setError(getString(R.string.please_enter_occupation));
            return false;
        }

        if (mName.isEmpty()) {
            mNameEditText.setError(getString(R.string.error_invalid_first_name));
            focusView = mNameEditText;
            cancel = true;
        } else if (!InputValidator.isValidNameWithRequiredLength(mName)) {
            mNameEditText.setError(getString(R.string.error_invalid_name_with_required_length));
            focusView = mNameEditText;
            cancel = true;
        }

        if (!InputValidator.isDateOfBirthValid(mDateOfBirth)) {
            focusView = mDateOfBirthEditText;
            cancel = true;
            mDateOfBirthEditText.setError(getString(R.string.please_enter_valid_date_of_birth));
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void attemptSaveBasicInfo() {
        mProgressDialog.setMessage(getString(R.string.saving_profile_information));
        mProgressDialog.show();

        Gson gson = new Gson();

        SetProfileInfoRequest setProfileInfoRequest = new SetProfileInfoRequest(mName, mGender, mDateOfBirth,
                mOccupation, mOrganizationName);

        String profileInfoJson = gson.toJson(setProfileInfoRequest);
        mSetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_SET_PROFILE_INFO_REQUEST, profileInfoJson, getActivity(), this);
        mSetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setProfileInformation() {
        mNameEditText.setText(mName);
        mDateOfBirthEditText.setText(mDateOfBirth);
        mOrganizationNameEditText.setText(mOrganizationName);

        String[] genderArray = GenderList.genderNames;

        if (mGender != null) {
            if (mGender.equals(GenderList.genderNameToCodeMap.get(
                    genderArray[0]))) {
                mMaleCheckBox.setChecked(true);
                mFemaleCheckBox.setChecked(false);

                setGenderCheckBoxTextColor(mMaleCheckBox.isChecked(), mFemaleCheckBox.isChecked());
            } else if (mGender.equals(GenderList.genderNameToCodeMap.get(
                    genderArray[1]))) {
                mMaleCheckBox.setChecked(false);
                mFemaleCheckBox.setChecked(true);

                setGenderCheckBoxTextColor(mMaleCheckBox.isChecked(), mFemaleCheckBox.isChecked());
            }
        }
    }

    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mSetProfileInfoTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_SET_PROFILE_INFO_REQUEST:

                try {
                    SetProfileInfoResponse mSetProfileInfoResponse = gson.fromJson(result.getJsonString(), SetProfileInfoResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), mSetProfileInfoResponse.getMessage(), Toast.LENGTH_LONG).show();

                            getActivity().onBackPressed();

                            ProfileInfoCacheManager.setGender(mGender);

                            //Google Analytic event
                            Utilities.sendSuccessEventTracker(mTracker, "Basic Info Update", ProfileInfoCacheManager.getAccountId(), 100);
                        }
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();

                        //Google Analytic event
                        Utilities.sendFailedEventTracker(mTracker, "Basic Info Update", ProfileInfoCacheManager.getAccountId(), mSetProfileInfoResponse.getMessage(), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();

                    //Google Analytic event
                    Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
                }

                mSetProfileInfoTask = null;
                break;
        }
    }

    private void setOccupation() {

        for (int i = 0; i < mOccupationList.size(); i++) {
            if (mOccupationList.get(i).getId() == mOccupation) {
                String occupation = mOccupationList.get(i).getName();
                if (occupation != null) {
                    mOccupationEditText.setText(occupation);
                }

                break;
            }
        }
    }

    private void setOccupationAdapter() {
        mOccupationTypeResourceSelectorDialog = new ResourceSelectorDialog<>(getActivity(), getString(R.string.select_an_occupation), mOccupationList);
        mOccupationTypeResourceSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mOccupationEditText.setText(name);
                mOccupation = id;
            }
        });

        mOccupationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOccupationTypeResourceSelectorDialog.show();
            }
        });
    }
}
