package bd.com.ipay.ipayskeleton.ProfileFragments;

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
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
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
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
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

        getActivity().setTitle(getString(R.string.edit_basic_info));

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        Bundle bundle = getArguments();

        mName = bundle.getString(Constants.NAME);
        mFathersName = bundle.getString(Constants.FATHERS_NAME);
        mMothersName = bundle.getString(Constants.MOTHERS_NAME);
        mProfilePicture = bundle.getString(Constants.PROFILE_PICTURE);
        mDateOfBirth = bundle.getString(Constants.DATE_OF_BIRTH);
        mGender = bundle.getString(Constants.GENDER);
        mOccupation = bundle.getInt(Constants.OCCUPATION);

        mProfilePictureView = (RoundedImageView) v.findViewById(R.id.profile_picture);
        mEditProfilePictureButton = (ImageButton) v.findViewById(R.id.button_profile_picture_edit);
        mNameEditText = (EditText) v.findViewById(R.id.name);

        mFathersNameEditText = (EditText) v.findViewById(R.id.fathers_name);
        mMothersNameEditText = (EditText) v.findViewById(R.id.mothers_name);

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
        
        SetProfileInfoRequest setProfileInfoRequest = new SetProfileInfoRequest(mName, mGender, mDateOfBirth,
                mOccupation, mFathersName, mMothersName);

        String profileInfoJson = gson.toJson(setProfileInfoRequest);
        mSetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_SET_PROFILE_INFO_REQUEST, profileInfoJson, getActivity(), this);
        mSetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setProfileInformation() {

        mNameEditText.setText(mName);

        mFathersNameEditText.setText(mFathersName);
        mMothersNameEditText.setText(mMothersName);

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
                    url = Constants.BASE_URL_FTP_SERVER + url;

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

    public void httpResponseReceiver(HttpResponseObject result) {
        mProgressDialog.dismiss();

        if (result == null) {
            mSetProfileInfoTask = null;
            mUploadProfilePictureAsyncTask = null;
            mGetOccupationTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_PROFILE_INFO_REQUEST)) {

            try {
                mSetProfileInfoResponse = gson.fromJson(result.getJsonString(), SetProfileInfoResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mSetProfileInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ((ProfileActivity) getActivity()).switchToBasicInfoFragment();

                        // We need to update the basic info page when user navigates to that page from the current edit page.
                        // But by default, the basic info stored in our database is refreshed only when a push is received.
                        // It might be the case that push notification is not yet received on the phone and user already
                        // navigated to the basic info page. To handle this case, we are setting updateNeeded to true.
                        PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
                        pushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_INFO_UPDATE, true);
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
        } else if (result.getApiCommand().equals(Constants.COMMAND_SET_PROFILE_PICTURE)) {
            try {
                mSetProfilePictureResponse = gson.fromJson(result.getJsonString(), SetProfilePictureResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetProfilePictureResponse.getMessage(), Toast.LENGTH_LONG).show();

                    PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
                    pushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, true);
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
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_OCCUPATIONS_REQUEST)) {

            try {
                mGetOccupationResponse = gson.fromJson(result.getJsonString(), GetOccupationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
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
