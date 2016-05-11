package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.UploadProfilePictureAsyncTask;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.SetProfileInfoRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.SetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.SetProfilePictureResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetOccupationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Occupation;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.OccupationRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EditBasicInfoFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSetProfileInfoTask = null;
    private SetProfileInfoResponse mSetProfileInfoResponse;

    private UploadProfilePictureAsyncTask mUploadProfilePictureAsyncTask = null;
    private SetProfilePictureResponse mSetProfilePictureResponse;

    private HttpRequestGetAsyncTask mGetOccupationTask = null;
    private GetOccupationResponse mGetOccupationResponse;

    private RoundedImageView mProfilePictureView;
    private ImageButton mEditProfilePictureButton;

    private EditText mNameEditText;

    private EditText mFathersNameEditText;
    private EditText mMothersNameEditText;
    private EditText mSpouseNameEditText;

    private EditText mFathersMobileNumberEditText;
    private EditText mMothersMobileNumberEditText;
    private EditText mSpouseMobileNumberEditText;

    private EditText mDateOfBirthEditText;
    private Spinner mOccupationSpinner;
    private Spinner mGenderSpinner;

    private ImageView mDatePickerButton;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private final int ACTION_PICK_PROFILE_PICTURE = 100;

    private String mName = "";
    private String mDateOfBirth = "";
    private String mProfilePicture;

    private String mFathersName = "";
    private String mMothersName = "";
    private String mSpouseName = "";

    private String mFathersMobileNumber = "";
    private String mMothersMobileNumber = "";
    private String mSpouseMobileNumber = "";

    private int mOccupation = 0;
    private String mGender = "";

    private List<Occupation> mOccupationList;
    private List<String> mOccupationNameList;

    private ArrayAdapter<String> mAdapterOccupation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);

        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (verifyUserInputs()) {
                Utilities.hideKeyboard(getActivity());
                attemptSaveBasicInfo();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_basic_info, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        Bundle bundle = getArguments();

        mName = bundle.getString(Constants.NAME);
        mFathersName = bundle.getString(Constants.FATHERS_NAME);
        mMothersName = bundle.getString(Constants.MOTHERS_NAME);
        mSpouseName = bundle.getString(Constants.SPOUSES_NAME);
        mFathersMobileNumber = bundle.getString(Constants.FATHERS_MOBILE_NUMBER);
        mMothersMobileNumber = bundle.getString(Constants.MOTHERS_MOBILE_NUMBER);
        mSpouseMobileNumber = bundle.getString(Constants.SPOUSES_MOBILE_NUMBER);
        mProfilePicture = bundle.getString(Constants.PROFILE_PICTURE);
        mDateOfBirth = bundle.getString(Constants.DATE_OF_BIRTH);
        mGender = bundle.getString(Constants.GENDER);
        mOccupation = bundle.getInt(Constants.OCCUPATION);

        mProfilePictureView = (RoundedImageView) v.findViewById(R.id.introducer_profile_picture);
        mEditProfilePictureButton = (ImageButton) v.findViewById(R.id.button_profile_picture_edit);
        mNameEditText = (EditText) v.findViewById(R.id.name);

        mFathersNameEditText = (EditText) v.findViewById(R.id.fathers_name);
        mMothersNameEditText = (EditText) v.findViewById(R.id.mothers_name);
        mSpouseNameEditText = (EditText) v.findViewById(R.id.spouse_name);

        mFathersMobileNumberEditText = (EditText) v.findViewById(R.id.fathers_mobile_number);
        mMothersMobileNumberEditText = (EditText) v.findViewById(R.id.mothers_mobile_number);
        mSpouseMobileNumberEditText = (EditText) v.findViewById(R.id.spouse_mobile_number);

        mDateOfBirthEditText = (EditText) v.findViewById(R.id.birthdayEditText);

        mDatePickerButton = (ImageView) v.findViewById(R.id.myDatePickerButton);

        mOccupationSpinner = (Spinner) v.findViewById(R.id.occupation);
        mGenderSpinner = (Spinner) v.findViewById(R.id.gender);

        mProfilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageChooserIntent = DocumentPicker.getPickImageIntent(getActivity(), getString(R.string.select_an_image));
                startActivityForResult(imageChooserIntent, ACTION_PICK_PROFILE_PICTURE);
            }
        });

        mEditProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageChooserIntent = DocumentPicker.getPickImageIntent(getActivity(), getString(R.string.select_an_image));
                startActivityForResult(imageChooserIntent, ACTION_PICK_PROFILE_PICTURE);
            }
        });

        ArrayAdapter<CharSequence> mAdapterGender = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, GenderList.genderNames);
        mGenderSpinner.setAdapter(mAdapterGender);

        final DatePickerDialog dialog = new DatePickerDialog(
                getActivity(), mDateSetListener, 1990, 0, 1);
        mDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
        mOccupationNameList = new ArrayList<>();
        mOccupationNameList.add(getString(R.string.loading));

        mAdapterOccupation = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mOccupationNameList);
        mOccupationSpinner.setAdapter(mAdapterOccupation);

        setProfilePicture("");
        setProfileInformation();
        getOccupationList();

        return v;
    }

    public boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        mName = mNameEditText.getText().toString().trim();
        mDateOfBirth = mDateOfBirthEditText.getText().toString().trim();

        mFathersName = mFathersNameEditText.getText().toString().trim();
        mMothersName = mMothersNameEditText.getText().toString().trim();
        mSpouseName = mSpouseNameEditText.getText().toString().trim();

        mFathersMobileNumber = mFathersMobileNumberEditText.getText().toString().trim();
        mMothersMobileNumber = mMothersMobileNumberEditText.getText().toString().trim();
        mSpouseMobileNumber = mSpouseMobileNumberEditText.getText().toString().trim();

        mGender = GenderList.genderNameToCodeMap.get(
                mGenderSpinner.getSelectedItem().toString());

        if (mOccupationSpinner.getSelectedItemPosition() == 0) {
            focusView = mOccupationSpinner.getSelectedView();
            cancel = true;
            ((TextView) mOccupationSpinner.getSelectedView()).setError("");
        } else {
            if (mOccupationSpinner.getSelectedItemPosition() - 1 < mOccupationList.size()) {
                mOccupation = mOccupationList.get(
                        mOccupationSpinner.getSelectedItemPosition() - 1).getId();
            }
        }

        if (mName.isEmpty()) {
            mNameEditText.setError(getString(R.string.error_invalid_first_name));
            focusView = mNameEditText;
            cancel = true;
        }

        if (!ContactEngine.isValidNumber(mFathersMobileNumber)) {
            focusView = mFathersMobileNumberEditText;
            cancel = true;
            mFathersMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
        }

        if (!ContactEngine.isValidNumber(mMothersMobileNumber)) {
            focusView = mMothersMobileNumberEditText;
            cancel = true;
            mMothersMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
        }

        if (!ContactEngine.isValidNumber(mSpouseMobileNumber)) {
            focusView = mSpouseMobileNumberEditText;
            cancel = true;
            mSpouseMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
        }

        if (!Utilities.isDateOfBirthValid(mDateOfBirth)) {
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

    private void getOccupationList() {
        if (mGetOccupationTask != null) {
            return;
        }

        mGetOccupationTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_OCCUPATIONS_REQUEST,
                new OccupationRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetOccupationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    
    private void attemptSaveBasicInfo() {
        mProgressDialog.setMessage(getString(R.string.saving_profile_information));
        mProgressDialog.show();

        Gson gson = new Gson();
        
        SetProfileInfoRequest setProfileInfoRequest = new SetProfileInfoRequest(
                pref.getString(Constants.USERID, ""), mName, mGender, mDateOfBirth,
                mOccupation, mFathersName,
                mMothersName, mSpouseName,
                mFathersMobileNumber, mMothersMobileNumber, mSpouseMobileNumber);

        String profileInfoJson = gson.toJson(setProfileInfoRequest);
        mSetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL + Constants.URL_SET_PROFILE_INFO_REQUEST, profileInfoJson, getActivity(), this);
        mSetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setProfileInformation() {

        mNameEditText.setText(mName);

        mFathersNameEditText.setText(mFathersName);
        mMothersNameEditText.setText(mMothersName);
        mSpouseNameEditText.setText(mSpouseName);

        mFathersMobileNumberEditText.setText(mFathersMobileNumber);
        mMothersMobileNumberEditText.setText(mMothersMobileNumber);
        mSpouseMobileNumberEditText.setText(mSpouseMobileNumber);

        mDateOfBirthEditText.setText(mDateOfBirth);

        String[] genderArray = GenderList.genderNames;
        for (int i = 0; i < genderArray.length; i++) {
            String genderCode = GenderList.genderNameToCodeMap.get(
                    genderArray[i]);
            if (genderCode.equals(mGender)) {
                mGenderSpinner.setSelection(i);
                break;
            }
        }

        setProfilePicture(mProfilePicture);

    }

    private void setProfilePicture(String url) {
        try {
            if (!url.equals("")) {
                if (!url.startsWith("content:"))
                    url = Constants.BASE_URL_IMAGE_SERVER + url;

                Glide.with(getActivity())
                        .load(url)
                        .crossFade()
                        .error(R.drawable.ic_person)
                        .transform(new CircleTransform(getActivity()))
                        .into(mProfilePictureView);
            } else {
                Glide.with(getActivity())
                        .load(R.drawable.ic_person)
                        .crossFade()
                        .into(mProfilePictureView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProfilePicture(Uri selectedImageUri) {
        mProgressDialog.setMessage(getString(R.string.uploading_profile_picture));
        mProgressDialog.show();

        String selectedOImagePath = selectedImageUri.getPath();

        mUploadProfilePictureAsyncTask = new UploadProfilePictureAsyncTask(Constants.COMMAND_SET_PROFILE_PICTURE,
                selectedOImagePath, getActivity());
        mUploadProfilePictureAsyncTask.mHttpResponseListener = this;
        mUploadProfilePictureAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ((ProfilePictureChangeListener) getActivity()).onProfilePictureChange(selectedOImagePath);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mDateOfBirthEditText.setText(
                            String.format("%02d/%02d/%4d", dayOfMonth, monthOfYear + 1, year));
                }
            };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ACTION_PICK_PROFILE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, intent);
                    if (uri == null) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(),
                                    R.string.could_not_load_image,
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        setProfilePicture(uri.toString());
                        updateProfilePicture(uri);
                    }

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    public interface ProfilePictureChangeListener {
        void onProfilePictureChange(String imageUrl);
    }

    public void httpResponseReceiver(String result) {
        mProgressDialog.dismiss();

        if (result == null) {
            mSetProfileInfoTask = null;
            mUploadProfilePictureAsyncTask = null;
            mGetOccupationTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SET_PROFILE_INFO_REQUEST)) {

            try {
                mSetProfileInfoResponse = gson.fromJson(resultList.get(2), SetProfileInfoResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mSetProfileInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ((HomeActivity) getActivity()).switchToBasicInfoFragment();
                    }
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
            }

            mSetProfileInfoTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_SET_PROFILE_PICTURE)) {
            try {
                mSetProfilePictureResponse = gson.fromJson(resultList.get(2), SetProfilePictureResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetProfilePictureResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetProfilePictureResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_picture_set_failed, Toast.LENGTH_SHORT).show();
            }

            mUploadProfilePictureAsyncTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_GET_OCCUPATIONS_REQUEST)) {

            try {
                mGetOccupationResponse = gson.fromJson(resultList.get(2), GetOccupationResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mOccupationList = mGetOccupationResponse.getOccupations();

                    mOccupationNameList.clear();
                    mOccupationNameList.add(getString(R.string.select_one));
                    mOccupationNameList.addAll(mGetOccupationResponse.getOccupationNames());

                    for (int i = 0; i < mOccupationList.size(); i++) {
                        if (mOccupationList.get(i).getId() == mOccupation) {
                            mOccupationSpinner.setSelection(i + 1);
                            break;
                        }
                    }

                    mAdapterOccupation.notifyDataSetChanged();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_loading_occupation_list, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_loading_occupation_list, Toast.LENGTH_LONG).show();
            }

            mGetOccupationTask = null;
        }
    }
}
