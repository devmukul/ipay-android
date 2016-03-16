package bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import bd.com.ipay.ipayskeleton.Activities.EditProfileActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class EditBasicInfoFragment extends Fragment {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private ImageView mEmailVerify;
    private EditText mFathersNameEditText;
    private EditText mMothersNameEditText;
    private EditText mSpouseNameEditText;
    private EditText mDateOfBirthEditText;
    private Spinner mOccupationSpinner;
    private Spinner mGenderSpinner;

    private RoundedImageView mProfilePicture;
    private Set<UserProfilePictureClass> profilePictures;

    private int emailVerificationStatus;
    private boolean profilePictureUpdated = false;

    private final int ACTION_PICK_PROFILE_PICTURE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_basic_info, container, false);

        profilePictures = new HashSet<>();

        mProfilePicture = (RoundedImageView) v.findViewById(R.id.profile_picture);
        mNameEditText = (EditText) v.findViewById(R.id.name);
        mEmailEditText = (EditText) v.findViewById(R.id.email);
        mFathersNameEditText = (EditText) v.findViewById(R.id.fathers_name);
        mMothersNameEditText = (EditText) v.findViewById(R.id.mothers_name);
        mSpouseNameEditText = (EditText) v.findViewById(R.id.spouse_name);
        mDateOfBirthEditText = (EditText) v.findViewById(R.id.birthdayEditText);

        mOccupationSpinner = (Spinner) v.findViewById(R.id.occupation);
        mGenderSpinner = (Spinner) v.findViewById(R.id.gender);
        mEmailVerify = (ImageView) v.findViewById(R.id.email_verification_status);

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

        ArrayAdapter<CharSequence> mAdapterGender = new ArrayAdapter<CharSequence>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, GenderList.genderNames);
        mGenderSpinner.setAdapter(mAdapterGender);

        mEmailVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();
                if (email.length() > 0 && emailVerificationStatus == Constants.EMAIL_VERIFICATION_STATUS_NOT_VERIFIED) {
                    showAlertDialogue(getString(R.string.alert_verify_email));
                }
            }
        });

        setProfilePicture("");
        setProfileInformation();
        setHasOptionsMenu(true);

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
            case R.id.action_save:
                boolean isInputValid = verifyUserInputs();
                if (isInputValid) {
                    ((EditProfileActivity) getActivity()).saveProfile();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        if (mNameEditText.getText().toString().trim().length() == 0) {
            mNameEditText.setError(getString(R.string.error_invalid_first_name));
            focusView = mNameEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        else {
            ProfileFragment.mEmailAddress = mEmailEditText.getText().toString().trim();
            ProfileFragment.mName = mNameEditText.getText().toString().trim();
            ProfileFragment.mDateOfBirth = mDateOfBirthEditText.getText().toString().trim();
            ProfileFragment.mFathersName = mFathersNameEditText.getText().toString().trim();
            ProfileFragment.mMothersName = mMothersNameEditText.getText().toString().trim();
            ProfileFragment.mSpouseName = mSpouseNameEditText.getText().toString().trim();
            ProfileFragment.mOccupation = mOccupationSpinner.getSelectedItem().toString();
            ProfileFragment.mGender = GenderList.genderNameToCodeMap.get(
                    mGenderSpinner.getSelectedItem().toString());

            return true;
        }
    }

    private void setProfileInformation() {
        if (ProfileFragment.mProfilePictures.size() > 0) {

            String imageUrl = "";
            for (Iterator<UserProfilePictureClass> it = ProfileFragment.mProfilePictures.iterator(); it.hasNext(); ) {
                UserProfilePictureClass userProfilePictureClass = it.next();
                imageUrl = userProfilePictureClass.getUrl();
                break;
            }
            setProfilePicture(imageUrl);
        }

        mNameEditText.setText(ProfileFragment.mName);
        mEmailEditText.setText(ProfileFragment.mEmailAddress);
        mFathersNameEditText.setText(ProfileFragment.mFathersName);
        mMothersNameEditText.setText(ProfileFragment.mMothersName);
        mSpouseNameEditText.setText(ProfileFragment.mSpouseName);
        mDateOfBirthEditText.setText(ProfileFragment.mDateOfBirth);

        String[] occupationArray = getResources().getStringArray(R.array.occupations);
        for (int i = 0; i < occupationArray.length; i++) {
            if (occupationArray[i].equals(ProfileFragment.mOccupation)) {
                mOccupationSpinner.setSelection(i);
                break;
            }
        }

        String[] genderArray = GenderList.genderNames;
        for (int i = 0; i < genderArray.length; i++) {
            String genderCode = GenderList.genderNameToCodeMap.get(
                    genderArray[i]);
            if (genderCode.equals(ProfileFragment.mGender)) {
                mGenderSpinner.setSelection(i);
                break;
            }
        }

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
                        .into(mProfilePicture);
                if (profilePictureUpdated) {
                    ProfilePictureChangeListener listener = (ProfilePictureChangeListener) getActivity();
                    listener.onProfilePictureChange(url);
                }
            } else {
                Glide.with(getActivity())
                        .load(R.drawable.ic_person)
                        .crossFade()
                        .into(mProfilePicture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (requestCode == ACTION_PICK_PROFILE_PICTURE) {
                    if (intent != null && intent.getData() != null) {
                        setProfilePicture(intent.getData().toString());
                        ProfileFragment.mProfilePictures.clear();
                        ProfileFragment.mProfilePictures.add(new UserProfilePictureClass(
                                intent.getData().toString(), ""));
                        ((EditProfileActivity) getActivity()).updateProfilePicture(intent.getData());
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

    private void showAlertDialogue(String msg) {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
        alertDialogue.setMessage(msg);

        alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String emailAddress = mEmailEditText.getText().toString().trim();
                ProfileFragment.mEmailAddress = emailAddress;
                        ((EditProfileActivity) getActivity()).verifyEmail(emailAddress);
            }
        });

        alertDialogue.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        alertDialogue.show();
    }

    public interface ProfilePictureChangeListener {
        void onProfilePictureChange(String imageUrl);
    }

}
