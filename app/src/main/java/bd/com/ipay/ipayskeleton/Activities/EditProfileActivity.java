package bd.com.ipay.ipayskeleton.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.UploadProfilePictureAsyncTask;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.DocumentUploadFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EditUserAddressFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.ProfileFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfileInfoRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfilePictureResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetUserAddressRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetUserAddressResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Verification.EmailVerificationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Verification.EmailVerificationResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EditProfileActivity extends AppCompatActivity implements HttpResponseListener {

    public static final String TARGET_TAB = "TARGET_TAB";

    public static final int TARGET_TAB_BASIC_INFO = 0;
    public static final int TARGET_TAB_USER_ADDRESS = 1;
    public static final int TARGET_TAB_UPLOAD_DOCUMENTS = 2;

    private ProgressDialog mProgressDialog;

    private HttpRequestPostAsyncTask mEmailVerificationAsyncTask = null;
    private EmailVerificationResponse mEmailVerificationResponse;

    private HttpRequestPostAsyncTask mSetProfileInfoTask = null;
    private SetProfileInfoResponse mSetProfileInfoResponse;

    private HttpRequestPostAsyncTask mSetUserAddressTask = null;
    private SetUserAddressResponse mSetUserAddressResponse;

    private UploadProfilePictureAsyncTask mUploadProfilePictureAsyncTask;
    private SetProfilePictureResponse mSetProfilePictureResponse;

    private boolean mBasicInfoEdited = false;
    private boolean mUserAddressEdited = false;

    private EditBasicInfoFragment mBasicInfoFragment;
    private EditUserAddressFragment mUserAddressFragment;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new EditProfileFragmentAdapter(getSupportFragmentManager(), this));
        viewPager.addOnPageChangeListener(mOnPageChangeListener);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        int targetTabPosition = getIntent().getIntExtra(TARGET_TAB, 0);
        TabLayout.Tab targetTab = tabLayout.getTabAt(targetTabPosition);
        if (targetTab != null)
            targetTab.select();
        // On page selected doesn't get called for the first page, so call it manually
        mOnPageChangeListener.onPageSelected(targetTabPosition);

        mProgressDialog = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            attemptSaveProfile();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile, menu);
    }

    public void attemptSaveProfile() {

        // Hide input keyboard while saving
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);

        if (Utilities.isConnectionAvailable(this)) {

            mProgressDialog.setMessage(getString(R.string.saving_profile_information));

            Gson gson = new Gson();
            if (mBasicInfoEdited && mSetProfileInfoTask == null) {
                if (mBasicInfoFragment != null && mBasicInfoFragment.verifyUserInputs()) {
                    mProgressDialog.show();

                    SetProfileInfoRequest setProfileInfoRequest = new SetProfileInfoRequest(
                            ProfileFragment.mMobileNumber, ProfileFragment.mName,
                            ProfileFragment.mGender, ProfileFragment.mDateOfBirth,
                            ProfileFragment.mEmailAddress, ProfileFragment.mOccupation, ProfileFragment.mFathersName,
                            ProfileFragment.mMothersName, ProfileFragment.mSpouseName,
                            ProfileFragment.mFathersMobileNumber, ProfileFragment.mMothersMobileNumber, ProfileFragment.mSpouseMobileNumber);

                    String profileInfoJson = gson.toJson(setProfileInfoRequest);
                    mSetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PROFILE_INFO_REQUEST,
                            Constants.BASE_URL + Constants.URL_SET_PROFILE_INFO_REQUEST, profileInfoJson, this);
                    mSetProfileInfoTask.mHttpResponseListener = this;
                    mSetProfileInfoTask.execute();
                }
            }

            if (mUserAddressEdited && mSetUserAddressTask == null) {
                if (mUserAddressFragment != null && mUserAddressFragment.verifyUserInputs()) {
                    mProgressDialog.show();

                    SetUserAddressRequest userAddressRequest = new SetUserAddressRequest(
                            ProfileFragment.mPresentAddress, ProfileFragment.mPermanentAddress, ProfileFragment.mOfficeAddress);

                    String addressJson = gson.toJson(userAddressRequest, SetUserAddressRequest.class);
                    mSetUserAddressTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_USER_ADDRESS_REQUEST,
                            Constants.BASE_URL + Constants.URL_SET_USER_ADDRESS_REQUEST, addressJson, this);
                    mSetUserAddressTask.mHttpResponseListener = this;
                    mSetUserAddressTask.execute();
                }
            }
        } else {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
    }

    public void updateProfilePicture(Uri selectedImageUri) {
        mProgressDialog.setMessage(getString(R.string.uploading_profile_picture));
        mProgressDialog.show();

        String selectedOImagePath = selectedImageUri.getPath();

        mUploadProfilePictureAsyncTask = new UploadProfilePictureAsyncTask(Constants.COMMAND_SET_PROFILE_PICTURE,
                selectedOImagePath, EditProfileActivity.this);
        mUploadProfilePictureAsyncTask.mHttpResponseListener = this;
        mUploadProfilePictureAsyncTask.execute();
    }

    public void verifyEmail(String emailAddress) {
        if (mEmailVerificationAsyncTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.sending_email_to_your_email_account));
        mProgressDialog.show();

        EmailVerificationRequest mEmailVerificationRequest = new EmailVerificationRequest(emailAddress);
        Gson gson = new Gson();
        String json = gson.toJson(mEmailVerificationRequest);
        mEmailVerificationAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_EMAIL_VERIFICATION_REQUEST,
                Constants.BASE_URL + Constants.URL_EMAIL_VERIFICATION, json, EditProfileActivity.this);
        mEmailVerificationAsyncTask.mHttpResponseListener = this;
        mEmailVerificationAsyncTask.execute((Void) null);

    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mSetProfileInfoTask = null;
            mEmailVerificationAsyncTask = null;
            mUploadProfilePictureAsyncTask = null;
            Toast.makeText(EditProfileActivity.this, R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();
        if (resultList.get(0).equals(Constants.COMMAND_SET_PROFILE_INFO_REQUEST)) {

            try {
                mSetProfileInfoResponse = gson.fromJson(resultList.get(2), SetProfileInfoResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    Toast.makeText(EditProfileActivity.this, mSetProfileInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                    mBasicInfoEdited = false;
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
            }

            mSetProfileInfoTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_SET_USER_ADDRESS_REQUEST)) {

            try {
                mSetUserAddressResponse = gson.fromJson(resultList.get(2), SetUserAddressResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mUserAddressEdited = false;
//                    Toast.makeText(EditProfileActivity.this, mSetUserAddressResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
            }

            mSetUserAddressTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_SET_PROFILE_PICTURE)) {
            try {
                mSetProfilePictureResponse = gson.fromJson(resultList.get(2), SetProfilePictureResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    Toast.makeText(EditProfileActivity.this, mSetProfilePictureResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.profile_picture_get_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, R.string.profile_picture_get_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mUploadProfilePictureAsyncTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_EMAIL_VERIFICATION_REQUEST)) {

            try {
                mEmailVerificationResponse = gson.fromJson(resultList.get(2), EmailVerificationResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    Toast.makeText(EditProfileActivity.this, mEmailVerificationResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.request_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, R.string.request_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mEmailVerificationAsyncTask = null;
        }

        if ((resultList.get(0).equals(Constants.COMMAND_SET_PROFILE_INFO_REQUEST) ||
                resultList.get(0).equals(Constants.COMMAND_SET_USER_ADDRESS_REQUEST))
                && mSetProfileInfoTask == null && mSetUserAddressTask == null) {
            mProgressDialog.dismiss();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBasicInfoEdited || mUserAddressEdited) {
            showExitConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmation_save_changes))
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attemptSaveProfile();
                    }
                })
                .setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    public class EditProfileFragmentAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String[] tabTitles;

        public EditProfileFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            tabTitles = new String[]{
                    getString(R.string.profile_basic_info),
                    getString(R.string.profile_address),
                    getString(R.string.profile_documents)
            };
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (mBasicInfoFragment == null)
                    mBasicInfoFragment = new EditBasicInfoFragment();
                return mBasicInfoFragment;
            } else if (position == 1) {
                if (mUserAddressFragment == null)
                    mUserAddressFragment = new EditUserAddressFragment();
                return mUserAddressFragment;
            } else if (position == 2) {
                return new DocumentUploadFragment();
            } else
                return new Fragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0)
                mBasicInfoEdited = true;
            else if (position == 1)
                mUserAddressEdited = true;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
