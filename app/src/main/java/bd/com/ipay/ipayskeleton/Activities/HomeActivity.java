package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Api.DownloadImageFromUrlAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Api.SyncContactsAsyncTask;
import bd.com.ipay.ipayskeleton.DrawerFragments.AccountSettingsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ActivityHistoryFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.DashBoardFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.NotificationFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.TransactionHistoryFragment;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.GetRefreshTokenResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.RegistrationIntentService;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, HttpResponseListener {

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;

    public static HttpRequestPostAsyncTask mRefreshTokenAsyncTask = null;
    public static GetRefreshTokenResponse mGetRefreshTokenResponse;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private HttpRequestGetAsyncTask mGetAllContactsTask;
    private List<FriendNode> mGetAllContactsResponse;

    private TextView mMobileNumberView;
    private TextView mNameView;
    private RoundedImageView mPortrait;
    private SharedPreferences pref;
    private String mUserID;
    private List<UserProfilePictureClass> profilePictures;

    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    public static String iPayToken = "";
    public static String iPayRefreshToken = "";
    public static String fireBaseToken = "";
    public static boolean newsFeedLoadedOnce = false;
    public static CountDownTimer tokenTimer;
    public static long iPayTokenTimeInMs = 60000;  // By default this is one minute

    public static boolean switchedToHomeFragment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logo_ipay);
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mUserID = pref.getString(Constants.USERID, "");
        mProgressDialog = new ProgressDialog(HomeActivity.this);
        profilePictures = new ArrayList<>();

        // Initialize token timer
        tokenTimer = new CountDownTimer(iPayTokenTimeInMs, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                refreshToken();
            }
        }.start();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Utilities.hideKeyboard(HomeActivity.this);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mMobileNumberView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.textview_mobile_number);
        mNameView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.textview_name);
        mPortrait = (RoundedImageView) mNavigationView.getHeaderView(0).findViewById(R.id.portrait);
        mMobileNumberView.setText(mUserID);
        mNavigationView.setNavigationItemSelectedListener(this);

        switchToDashBoard();

        // Set the inital profile picture
        setProfilePicture("");

        // Load the list of available banks, which will be accessed from multiple activities
        getAvailableBankList();

        // Check if there's anything new from the server
        checkForUpdateFromPush();

        // Sync contacts
        syncContacts();

        // Start service for GCM
        if (Utilities.checkPlayServices(HomeActivity.this)) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        if (Constants.DEBUG) {
            Log.w("Token", HomeActivity.iPayToken);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_notification:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new NotificationFragment()).commit();
                switchedToHomeFragment = false;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getProfileInfo();
        // TODO: refresh balance in the navigation drawer here
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!switchedToHomeFragment)
            switchToDashBoard();
        else {
            new AlertDialog.Builder(HomeActivity.this)
                    .setMessage(R.string.are_you_sure_to_exit)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }
    }

    private void setProfilePicture(String imageUrl) {
        try {

            File dir = new File(Environment.getExternalStorageDirectory().getPath()
                    + Constants.PICTURE_FOLDER);
            if (!dir.exists()) dir.mkdir();
            File file = new File(dir, mUserID.replaceAll("[^0-9]", "") + ".jpg");

            Uri imageUri = null;
            if (file.exists()) imageUri = Uri.fromFile(file);

            if (imageUri != null) {
                Glide.with(HomeActivity.this)
                        .load(Constants.BASE_URL_IMAGE_SERVER + imageUrl)
                        .error(R.drawable.ic_person)
                        .crossFade()
                        .transform(new CircleTransform(HomeActivity.this))
                        .into(mPortrait);
            } else {
                if (!imageUrl.equals(""))
                    Glide.with(HomeActivity.this)
                            .load(Constants.BASE_URL_IMAGE_SERVER + imageUrl)
                            .error(R.drawable.ic_person)
                            .crossFade()
                            .transform(new CircleTransform(HomeActivity.this))
                            .into(mPortrait);
                else Glide.with(HomeActivity.this)
                        .load(R.drawable.ic_person)
                        .transform(new CircleTransform(HomeActivity.this))
                        .into(mPortrait);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncContacts() {
        if (mGetAllContactsTask != null) {
            return;
        }

        mGetAllContactsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_FRIENDS,
                Constants.BASE_URL_FRIEND + Constants.URL_GET_CONTACTS, this, this);
        mGetAllContactsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void switchToDashBoard() {
        mNavigationView.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new DashBoardFragment()).commit();
        switchedToHomeFragment = true;
    }

    public void changeMenuVisibility(int id, boolean visible) {
        mNavigationView.getMenu().findItem(id).setVisible(visible);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoDrawerItem(item);
            }
        }, 250);
        return true;
    }

    private void gotoDrawerItem(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            switchToDashBoard();

        } else if (id == R.id.nav_bank_account) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new BankAccountsFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_user_activity) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ActivityHistoryFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_transaction_history) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new TransactionHistoryFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_event) {

            Intent intent = new Intent(HomeActivity.this, EventActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new AccountSettingsFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_logout) {

            if (Utilities.isConnectionAvailable(HomeActivity.this)) attemptLogout();
            else {
                finish();
                Intent intent = new Intent(HomeActivity.this, SignupOrLoginActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_profile_basic_info) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.BASIC_PROFILE, new Bundle());
        } else if (id == R.id.nav_profile_addresses) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.ADDRESS, new Bundle());

        } else if (id == R.id.nav_profile_documents) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.VERIFICATION_DOCUMENT, new Bundle());

        } else if (id == R.id.nav_email_addresses) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.VERIFIED_EMAIL, new Bundle());

        } else if (id == R.id.nav_trusted_network) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.TRUSTED_NETWORK, new Bundle());

        } else if (id == R.id.nav_profile_completeness) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.PROFILE_COMPLETENESS, new Bundle());
        } else if (id == R.id.nav_identification) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.INTRODUCER, new Bundle());
            //getSupportFragmentManager().beginTransaction().replace(R.id.container, new IntroducerFragment()).commit();
            //switchedToHomeFragment = false;
        }

    }

    private void launchEditProfileActivity(String type, Bundle bundle) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(Constants.TARGET_FRAGMENT, type);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void attemptLogout() {
        if (mLogoutTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_signing_out));
        mProgressDialog.show();
        LogoutRequest mLogoutModel = new LogoutRequest(pref.getString(Constants.USERID, ""));
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);

        // Set the preference
        pref.edit().putBoolean(Constants.LOGGEDIN, false).commit();

        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL_MM + Constants.URL_LOG_OUT, json, HomeActivity.this);
        mLogoutTask.mHttpResponseListener = this;

        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }

        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(pref.getString(Constants.USERID, ""));

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, HomeActivity.this);
        mGetProfileInfoTask.mHttpResponseListener = this;
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getAvailableBankList() {
        GetAvailableBankAsyncTask getAvailableBanksTask = new GetAvailableBankAsyncTask(this);
        getAvailableBanksTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void checkForUpdateFromPush() {
        SharedPreferences pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        // Get the changes
        boolean isProfilePictureUpdated = true;
        if (pref.contains(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE))
            isProfilePictureUpdated = pref.getBoolean(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, false);

        // Take actions
        if (isProfilePictureUpdated) {
            // Set the preference to false again to set the update action is resolved
            pref.edit().putBoolean(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, false).commit();
            getProfileInfo();
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mLogoutTask = null;
            mGetProfileInfoTask = null;
            mGetAllContactsTask = null;
            Toast.makeText(HomeActivity.this, R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_LOG_OUT)) {

            try {
                mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    finish();
                    Intent intent = new Intent(HomeActivity.this, SignupOrLoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mLogoutTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {

            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mNameView.setText(mGetUserInfoResponse.getName());

                    profilePictures = mGetUserInfoResponse.getProfilePictures();

                    String imageUrl = "";
                    if (profilePictures.size() > 0) {
                        for (Iterator<UserProfilePictureClass> it = profilePictures.iterator(); it.hasNext(); ) {
                            UserProfilePictureClass userProfilePictureClass = it.next();
                            imageUrl = userProfilePictureClass.getUrl();
                            break;
                        }
                    }

                    pref.edit().putString(Constants.VERIFICATION_STATUS, mGetUserInfoResponse.getAccountStatus()).apply();

                    // Download the profile picture and store it in local storage
                    new DownloadImageFromUrlAsyncTask(imageUrl, mUserID)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    setProfilePicture(imageUrl);

                } else {
                    Toast.makeText(HomeActivity.this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
            }

            mGetProfileInfoTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_FRIENDS)) {
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    FriendNode[] friendNodeArray = gson.fromJson(result.getJsonString(), FriendNode[].class);
                    mGetAllContactsResponse = Arrays.asList(friendNodeArray);

                    SyncContactsAsyncTask syncContactsAsyncTask = new SyncContactsAsyncTask(this, mGetAllContactsResponse);
                    syncContactsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    Log.e(getString(R.string.contacts_sync_failed), result.getStatus() + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Context setContext() {
        return HomeActivity.this;
    }

}
