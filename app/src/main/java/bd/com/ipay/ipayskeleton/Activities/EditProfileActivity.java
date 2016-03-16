package bd.com.ipay.ipayskeleton.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.UploadProfilePictureAsyncTask;
import bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments.DocumentUploadFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments.EditUserAddressFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments.ProfileFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfileInfoRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.SetProfilePictureResponse;
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

    private final int ACTION_VERIFY_EMAIL = 1;

    private HttpRequestPostAsyncTask mEmailVerificationAsyncTask = null;
    private EmailVerificationResponse mEmailVerificationResponse;

    private HttpRequestPostAsyncTask mSetProfileInfoTask = null;
    private SetProfileInfoResponse mSetProfileInfoResponse;

    private UploadProfilePictureAsyncTask mUploadProfilePictureAsyncTask;
    private SetProfilePictureResponse mSetProfilePictureResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new EditProfileFragmentAdapter(getSupportFragmentManager(), this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (getIntent().hasExtra(TARGET_TAB)) {
            int targetTabPosition = getIntent().getIntExtra(TARGET_TAB, 0);
            TabLayout.Tab targetTab = tabLayout.getTabAt(targetTabPosition);
            if (targetTab != null)
                    targetTab.select();
        }

        mProgressDialog = new ProgressDialog(this);
    }

    public void saveProfile() {
        if (Utilities.isConnectionAvailable(this)) {

            if (mSetProfileInfoTask != null) {
                return;
            }
            mProgressDialog.setMessage(getString(R.string.saving_profile_information));
            mProgressDialog.show();
            SetProfileInfoRequest mSetProfileInfoRequest = new SetProfileInfoRequest(
                    ProfileFragment.mMobileNumber, ProfileFragment.mName,
                    ProfileFragment.mGender, ProfileFragment.mDateOfBirth,
                    ProfileFragment.mEmailAddress, ProfileFragment.mOccupation, ProfileFragment.mFathersName,
                    ProfileFragment.mMothersName, ProfileFragment.mSpouseName);

            Gson gson = new Gson();
            String json = gson.toJson(mSetProfileInfoRequest);
            mSetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PROFILE_INFO_REQUEST,
                    Constants.BASE_URL_POST_MM + Constants.URL_SET_PROFILE_INFO_REQUEST, json, this);
            mSetProfileInfoTask.mHttpResponseListener = this;
            mSetProfileInfoTask.execute();
        }
        else {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
    }
    
    public void updateProfilePicture(Uri selectedImageUri) {
        mProgressDialog.setMessage(getString(R.string.uploading_profile_picture));
        mProgressDialog.show();

        String selectedOImagePath = Utilities.getFilePath(EditProfileActivity.this, selectedImageUri);

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
                Constants.BASE_URL_POST_MM + Constants.URL_EMAIL_VERIFICATION, json, EditProfileActivity.this);
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
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mSetProfileInfoTask = null;
            finish();

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
    }


    public class EditProfileFragmentAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String[] tabTitles;

        public EditProfileFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            tabTitles = new String[] {
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
            if (position == 0)
                return new EditBasicInfoFragment();
            else if (position == 1)
                return new EditUserAddressFragment();
            else if (position == 2)
                return new DocumentUploadFragment();
            else
                return new Fragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
