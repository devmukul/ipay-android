package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.UploadProfilePictureAsyncTask;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetProfileInfoRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfileInfoRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfilePictureResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Verification.EmailVerificationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Verification.EmailVerificationResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ProfileFragment extends Fragment implements HttpResponseListener {

    private final int ACTION_PICK_PROFILE_PICTURE = 100;
    private final int ACTION_VERIFY_EMAIL = 1;

    private HttpRequestPostAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private HttpRequestPostAsyncTask mEmailVerificationAsyncTask = null;
    private EmailVerificationResponse mEmailVerificationResponse;

    private HttpRequestPostAsyncTask mSetProfileInfoTask = null;
    private SetProfileInfoResponse mSetProfileInfoResponse;

    private UploadProfilePictureAsyncTask mUploadProfilePictureAsyncTask;
    private SetProfilePictureResponse mSetProfilePictureResponse;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private EditText mAddressLine1EditText;
    private EditText mAddressLine2EditText;
    private EditText mNIDEditText;
    private EditText mPostCodeEditText;
    private TextView mNameOfUserTextView;
    private TextView mDateOfBirthTextView;
    private TextView mMobileNumberTextView;
    private ImageView mEmailVerify;
    private RoundedImageView mProfilePicture;
    private ProgressDialog mProgressDialog;

    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userOccupation;
    private String userNID;
    private String userAddressLine1;
    private String userAddressLine2;
    private String userPostCode;
    private String userCity;
    private String userDistrict;
    private Set<UserProfilePictureClass> profilePictures;

    private boolean isEditEnabled = false;
    private SharedPreferences pref;

    private String userID;
    private String userGender;
    private String userDOB;
    private String userCountry;

    private Spinner mOccupationSpinner;
    private Spinner mCitySpinner;
    private Spinner mDistrictSpinner;

    private int emailVerificationStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        profilePictures = new HashSet<>();

        userID = pref.getString(Constants.USERID, "");
        userGender = pref.getString(Constants.GENDER, "");
        userDOB = pref.getString(Constants.BIRTHDAY, "");
        userCountry = pref.getString(Constants.USERCOUNTRY, "");

        mFirstNameEditText = (EditText) v.findViewById(R.id.first_name);
        mLastNameEditText = (EditText) v.findViewById(R.id.last_name);
        mEmailEditText = (EditText) v.findViewById(R.id.email);
        mAddressLine1EditText = (EditText) v.findViewById(R.id.address_line_1);
        mAddressLine2EditText = (EditText) v.findViewById(R.id.address_line_2);
        mNIDEditText = (EditText) v.findViewById(R.id.nid);
        mPostCodeEditText = (EditText) v.findViewById(R.id.postcode);

        mNameOfUserTextView = (TextView) v.findViewById(R.id.name_text);
        mMobileNumberTextView = (TextView) v.findViewById(R.id.mobile_number_text);
        mDateOfBirthTextView = (TextView) v.findViewById(R.id.dob_text);

        mProfilePicture = (RoundedImageView) v.findViewById(R.id.profile_picture);
        mOccupationSpinner = (Spinner) v.findViewById(R.id.occupation);
        mCitySpinner = (Spinner) v.findViewById(R.id.city);
        mDistrictSpinner = (Spinner) v.findViewById(R.id.district);
        mEmailVerify = (ImageView) v.findViewById(R.id.email_verification_status);

        mProgressDialog = new ProgressDialog(getActivity());

        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), ACTION_PICK_PROFILE_PICTURE);
            }
        });

        ArrayAdapter<CharSequence> mAdapterOccupation = ArrayAdapter.createFromResource(getActivity(),
                R.array.occupations, android.R.layout.simple_dropdown_item_1line);
        mOccupationSpinner.setAdapter(mAdapterOccupation);

        ArrayAdapter<CharSequence> mAdapterCity = ArrayAdapter.createFromResource(getActivity(),
                R.array.city, android.R.layout.simple_dropdown_item_1line);
        mCitySpinner.setAdapter(mAdapterCity);

        ArrayAdapter<CharSequence> mAdapterDistrict = ArrayAdapter.createFromResource(getActivity(),
                R.array.district, android.R.layout.simple_dropdown_item_1line);
        mDistrictSpinner.setAdapter(mAdapterDistrict);

        mEmailVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();
                if (email.length() > 0 && emailVerificationStatus == Constants.EMAIL_VERIFICATION_STATUS_NOT_VERIFIED) {
                    showAlertDialogue(getString(R.string.alert_verify_email), ACTION_VERIFY_EMAIL);
                }
            }
        });

        getProfileInfo();
        disableAllEdits();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (isEditEnabled) {

                        if (mSetProfileInfoTask == null) {
                            disableAllEdits();
                            item.setIcon(getResources().getDrawable(R.drawable.ic_edit_white_24dp));
                            saveProfileInfo();
                        }

                    } else {
                        enableAllEdits();
                        item.setIcon(getResources().getDrawable(R.drawable.ic_save_white_24dp));
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void verifyEmail() {
        if (mEmailVerificationAsyncTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.sending_email_to_your_email_account));
        mProgressDialog.show();
        String email = mEmailEditText.getText().toString().trim();
        EmailVerificationRequest mEmailVerificationRequest = new EmailVerificationRequest(email);
        Gson gson = new Gson();
        String json = gson.toJson(mEmailVerificationRequest);
        mEmailVerificationAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_EMAIL_VERIFICATION_REQUEST,
                Constants.BASE_URL_POST_MM + Constants.URL_EMAIL_VERIFICATION, json, getActivity());
        mEmailVerificationAsyncTask.mHttpResponseListener = this;
        mEmailVerificationAsyncTask.execute((Void) null);

    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.fetching_profile_information));
        mProgressDialog.show();
        GetProfileInfoRequest mGetProfileInfoRequest = new GetProfileInfoRequest(userID);
        Gson gson = new Gson();
        String json = gson.toJson(mGetProfileInfoRequest);
        mGetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL_POST_MM + Constants.URL_GET_PROFILE_INFO_REQUEST, json, getActivity());
        mGetProfileInfoTask.mHttpResponseListener = this;
        mGetProfileInfoTask.execute((Void) null);

    }

    private void saveProfileInfo() {
        if (mSetProfileInfoTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (mFirstNameEditText.getText().toString().trim().length() == 0) {
            mFirstNameEditText.setError(getString(R.string.error_invalid_first_name));
            focusView = mFirstNameEditText;
            cancel = true;
        }

        if (mLastNameEditText.getText().toString().trim().length() == 0) {
            mLastNameEditText.setError(getString(R.string.error_invalid_last_name));
            focusView = mLastNameEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            userFirstName = mFirstNameEditText.getText().toString().trim();
            userLastName = mLastNameEditText.getText().toString().trim();
            userNID = mNIDEditText.getText().toString().trim();
            userEmail = mEmailEditText.getText().toString().trim();
            userAddressLine1 = mAddressLine1EditText.getText().toString().trim();
            userAddressLine2 = mAddressLine2EditText.getText().toString().trim();
            userPostCode = mPostCodeEditText.getText().toString().trim();
            userCity = "";
            userDistrict = "";
            userOccupation = "";

            if (mCitySpinner.getSelectedItemPosition() != 0)
                userCity = mCitySpinner.getSelectedItem().toString();
            if (mDistrictSpinner.getSelectedItemPosition() != 0)
                userDistrict = mDistrictSpinner.getSelectedItem().toString();
            if (mOccupationSpinner.getSelectedItemPosition() != 0)
                userOccupation = mOccupationSpinner.getSelectedItem().toString();

            mProgressDialog.setMessage(getString(R.string.saving_profile_information));
            mProgressDialog.show();
            SetProfileInfoRequest mSetProfileInfoRequest = new SetProfileInfoRequest(userID, userFirstName, userLastName,
                    userGender, userDOB, userCountry, userEmail, userNID, userOccupation, userAddressLine1,
                    userAddressLine2, userCity, userDistrict, userPostCode);
            Gson gson = new Gson();
            String json = gson.toJson(mSetProfileInfoRequest);
            Log.d("json", json);
            mSetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PROFILE_INFO_REQUEST,
                    Constants.BASE_URL_POST_MM + Constants.URL_SET_PROFILE_INFO_REQUEST, json, getActivity());
            mSetProfileInfoTask.mHttpResponseListener = this;
            mSetProfileInfoTask.execute((Void) null);
        }
    }

    private void disableAllEdits() {
        isEditEnabled = false;

        mFirstNameEditText.setEnabled(false);
        mLastNameEditText.setEnabled(false);
        mEmailEditText.setEnabled(false);
        mAddressLine1EditText.setEnabled(false);
        mAddressLine2EditText.setEnabled(false);
        mPostCodeEditText.setEnabled(false);
        mNIDEditText.setEnabled(false);
        mOccupationSpinner.setEnabled(false);
        mDistrictSpinner.setEnabled(false);
        mCitySpinner.setEnabled(false);
    }

    private void enableAllEdits() {
        isEditEnabled = true;

        mFirstNameEditText.setEnabled(true);
        mLastNameEditText.setEnabled(true);
        mEmailEditText.setEnabled(true);
        mAddressLine1EditText.setEnabled(true);
        mAddressLine2EditText.setEnabled(true);
        mPostCodeEditText.setEnabled(true);
        mNIDEditText.setEnabled(true);
        mOccupationSpinner.setEnabled(true);
        mDistrictSpinner.setEnabled(true);
        mCitySpinner.setEnabled(true);
    }

    private void setProfileInformation() {
        mFirstNameEditText.setText(userFirstName);
        mLastNameEditText.setText(userLastName);
        mNIDEditText.setText(userNID);
        mEmailEditText.setText(userEmail);
        mAddressLine1EditText.setText(userAddressLine1);
        mAddressLine2EditText.setText(userAddressLine2);
        mPostCodeEditText.setText(userPostCode);
        mNameOfUserTextView.setText(userFirstName + " " + userLastName);
        mDateOfBirthTextView.setText(userDOB);
        mMobileNumberTextView.setText(userID);

        if (profilePictures.size() > 0) {

            String imageUrl = "";
            for (Iterator<UserProfilePictureClass> it = profilePictures.iterator(); it.hasNext(); ) {
                UserProfilePictureClass userProfilePictureClass = it.next();
                imageUrl = userProfilePictureClass.getUrl();
                break;
            }
            setProfilePicture(imageUrl);
        }

        // Update preferences
        // TODO: Date doesn't save in proper format in database
//        if (userDOB.length() > 0) pref.edit().putString(Constants.BIRTHDAY, userDOB).commit();
//        if (userGender.length() > 0) pref.edit().putString(Constants.GENDER, userGender).commit();
//        if (userID.length() > 0) pref.edit().putString(Constants.USERID, userID).commit();
//        if (userCountry != null && userCountry.length() > 0)
//            pref.edit().putString(Constants.USERCOUNTRY, userCountry).commit();

        String[] cityArray = getResources().getStringArray(R.array.city);
        for (int i = 0; i < cityArray.length; i++) {
            if (cityArray[i].equals(userCity)) {
                mCitySpinner.setSelection(i);
                break;
            }
        }

        String[] districtArray = getResources().getStringArray(R.array.district);
        for (int i = 0; i < districtArray.length; i++) {
            if (districtArray[i].equals(userDistrict)) {
                mDistrictSpinner.setSelection(i);
                break;
            }
        }

        String[] occupationArray = getResources().getStringArray(R.array.occupations);
        for (int i = 0; i < occupationArray.length; i++) {
            if (occupationArray[i].equals(userOccupation)) {
                mOccupationSpinner.setSelection(i);
                break;
            }
        }
    }

    private void setProfilePicture(String url) {
        try {
            if (!url.equals(""))
                Glide.with(getActivity())
                        .load(Constants.BASE_URL_IMAGE_SERVER + url)
                        .crossFade()
                        .transform(new CircleTransform(getActivity()))
                        .into(mProfilePicture);

            else Glide.with(getActivity())
                    .load(android.R.color.transparent)
                    .placeholder(R.drawable.ic_person)
                    .into(mProfilePicture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mGetProfileInfoTask = null;
            mSetProfileInfoTask = null;
            mEmailVerificationAsyncTask = null;
            mUploadProfilePictureAsyncTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_PROFILE_INFO_REQUEST)) {

            try {
                mGetProfileInfoResponse = gson.fromJson(resultList.get(2), GetProfileInfoResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    userFirstName = mGetProfileInfoResponse.getFirstName();
                    userLastName = mGetProfileInfoResponse.getLastName();
                    userEmail = mGetProfileInfoResponse.getEmail();
                    userAddressLine1 = mGetProfileInfoResponse.getAddressLine1();
                    userAddressLine2 = mGetProfileInfoResponse.getAddressLine2();
                    userNID = mGetProfileInfoResponse.getNIDNumber();
                    userOccupation = mGetProfileInfoResponse.getOccupation();
                    userPostCode = mGetProfileInfoResponse.getPostalCode();
                    userCity = mGetProfileInfoResponse.getCity();
                    userDistrict = mGetProfileInfoResponse.getDistrict();
                    emailVerificationStatus = mGetProfileInfoResponse.getEmailVerificationStatus();
                    if (emailVerificationStatus == Constants.EMAIL_VERIFICATION_STATUS_VERIFIED)
                        mEmailVerify.setImageResource(R.drawable.ic_verified_user_black_24dp);
                    else if (emailVerificationStatus == Constants.EMAIL_VERIFICATION_STATUS_VERIFICATION_IN_PROGRESS)
                        mEmailVerify.setImageResource(R.drawable.ic_sync_problem_black_24dp);

                    // Set profile pictures now
                    profilePictures = mGetProfileInfoResponse.getProfilePictures();

                    // TODO: Date doesn't save in proper format in database
                    if (userDOB.length() == 0) {
                        userDOB = mGetProfileInfoResponse.getDob();
                        pref.edit().putString(Constants.BIRTHDAY, userDOB);
                    }
//                userID = mGetProfileInfoResponse.getMobileNumber();
//                userGender = mGetProfileInfoResponse.getGender();
//                userCountry = mGetProfileInfoResponse.getCountry();

                    setProfileInformation();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();
            mGetProfileInfoTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_SET_PROFILE_INFO_REQUEST)) {

            try {
                mSetProfileInfoResponse = gson.fromJson(resultList.get(2), SetProfileInfoResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetProfileInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                    setProfileInformation();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mSetProfileInfoTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_SET_PROFILE_PICTURE)) {
            try {
                mSetProfilePictureResponse = gson.fromJson(resultList.get(2), SetProfilePictureResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetProfilePictureResponse.getMessage(), Toast.LENGTH_LONG).show();

                    getProfileInfo();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_picture_get_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_picture_get_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mUploadProfilePictureAsyncTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_EMAIL_VERIFICATION_REQUEST)) {

            try {
                mEmailVerificationResponse = gson.fromJson(resultList.get(2), EmailVerificationResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mEmailVerificationResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mEmailVerificationAsyncTask = null;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_CANCELED) {
            try {
                if (requestCode == ACTION_PICK_PROFILE_PICTURE && resultCode != 0) {
                    if (intent != null) {

                        mProgressDialog.setMessage(getString(R.string.uploading_profile_picture));
                        mProgressDialog.show();

                        Uri selectedImageUri = intent.getData();
                        String selectedOImagePath = Utilities.getFilePath(getActivity(), selectedImageUri);
                        mUploadProfilePictureAsyncTask = new UploadProfilePictureAsyncTask(Constants.COMMAND_SET_PROFILE_PICTURE,
                                selectedOImagePath, getActivity());
                        mUploadProfilePictureAsyncTask.mHttpResponseListener = this;
                        mUploadProfilePictureAsyncTask.execute();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity().getApplicationContext(),
                                    R.string.could_not_load_image,
                                    Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlertDialogue(String msg, final int action) {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
        alertDialogue.setMessage(msg);

        alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (action == ACTION_VERIFY_EMAIL)
                    verifyEmail();

            }
        });

        alertDialogue.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        alertDialogue.show();
    }

}
