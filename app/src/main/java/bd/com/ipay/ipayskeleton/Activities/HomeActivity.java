package bd.com.ipay.ipayskeleton.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Api.SyncContactsAsyncTask;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.BusinessActivity;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DrawerFragments.AboutFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.AccountSettingsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ActivityLogFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.DashBoardFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.NotificationFragment;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.GetRefreshTokenResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.AddToTrustedDeviceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.AddToTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.ProfileFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Service.GCM.RegistrationIntentService;
import bd.com.ipay.ipayskeleton.Utilities.AnalyticsConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceIdFactory;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
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

    private HttpRequestPostAsyncTask mAddTrustedDeviceTask = null;
    private AddToTrustedDeviceResponse mAddToTrustedDeviceResponse;

    private TextView mMobileNumberView;
    private TextView mNameView;
    private ProfileImageView mPortrait;
    private SharedPreferences pref;
    private String mUserID;
    private int mAccountType;
    private String mDeviceID;

    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    public static boolean newsFeedLoadedOnce = false;

    public static boolean switchedToHomeFragment = true;

    private static final int REQUEST_CODE_PERMISSION = 1001;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mProgressDialog = new ProgressDialog(HomeActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.logo_ipay);

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

        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mUserID = pref.getString(Constants.USERID, "");
        mAccountType = pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
        mDeviceID = DeviceIdFactory.getDeviceId(HomeActivity.this);

        pref.edit().putBoolean(Constants.FIRST_LAUNCH, false).apply();

        if (mAccountType == Constants.PERSONAL_ACCOUNT_TYPE) {
            setDrawerMenuVisibility(R.id.nav_manage_business, false);
        } else {
            setDrawerMenuVisibility(R.id.nav_manage_business, true);
        }

        // Initialize token timer
        CountDownTimer tokenTimer = new CountDownTimer(TokenManager.getiPayTokenTimeInMs(), 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                refreshToken();
            }
        }.start();
        TokenManager.setTokenTimer(tokenTimer);

        mMobileNumberView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.textview_mobile_number);
        mNameView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.textview_name);
        mPortrait = (ProfileImageView) mNavigationView.getHeaderView(0).findViewById(R.id.portrait);
        mMobileNumberView.setText(mUserID);
        mNavigationView.setNavigationItemSelectedListener(this);

        switchToDashBoard();

        // Load the list of available banks, which will be accessed from multiple activities
        getAvailableBankList();

        // Check if there's anything new from the server
        checkForUpdateFromPush();

        // Sync contacts
        getContacts();

        // Start service for GCM
        if (Utilities.checkPlayServices(HomeActivity.this)) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        // Add to trusted device
        if (!pref.contains(Constants.UUID)) {
            if (Utilities.isConnectionAvailable(this))
                addToTrustedDeviceList();
        }

        if (Constants.DEBUG) {
            Log.w("Token", TokenManager.getToken());
        }

        //attemptRequestForPermission();
        sendAnalytics();

        LocalBroadcastManager.getInstance(this).registerReceiver(mProfilePictureUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_PICTURE_UPDATE_BROADCAST));
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
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mProfilePictureUpdateBroadcastReceiver);
        super.onDestroy();
    }

    private void attemptRequestForPermission() {
        String[] requiredPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    REQUEST_CODE_PERMISSION);
        }
    }

    private void sendAnalytics() {
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsConstants.USER_ID, mUserID);
        bundle.putString(AnalyticsConstants.IP_V4_ADDRESS, Utilities.getIPAddress(true));
        bundle.putString(AnalyticsConstants.IP_V6_ADDRESS, Utilities.getIPAddress(false));
        bundle.putString(AnalyticsConstants.W_LAN_0, Utilities.getMACAddress(AnalyticsConstants.W_LAN_0));
        bundle.putString(AnalyticsConstants.ETH_0, Utilities.getMACAddress(AnalyticsConstants.ETH_0));
        bundle.putString(AnalyticsConstants.DEVICE_ID, mDeviceID);

        String longLat = Utilities.getLongLatWithoutGPS(HomeActivity.this);
        if (longLat != null)
            bundle.putString(AnalyticsConstants.DEVICE_LONG_LAT, longLat);

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    private void getContacts() {
        if (mGetAllContactsTask != null) {
            return;
        }

        mGetAllContactsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_FRIENDS,
                Constants.BASE_URL_FRIEND + Constants.URL_GET_FRIENDS, this, this);
        mGetAllContactsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addToTrustedDeviceList() {
        if (mAddTrustedDeviceTask != null) {
            return;
        }

        String mDeviceID = DeviceIdFactory.getDeviceId(this);

        String pushRegistrationID = pref.getString(Constants.PUSH_NOTIFICATION_TOKEN, null);
        String mDeviceName = android.os.Build.MANUFACTURER + "-" + android.os.Build.PRODUCT + " -" + Build.MODEL;

        AddToTrustedDeviceRequest mAddToTrustedDeviceRequest = new AddToTrustedDeviceRequest(mDeviceName,
                Constants.MOBILE_ANDROID + mDeviceID, pushRegistrationID);
        Gson gson = new Gson();
        String json = gson.toJson(mAddToTrustedDeviceRequest);
        mAddTrustedDeviceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_TRUSTED_DEVICE,
                Constants.BASE_URL_MM + Constants.URL_ADD_TRUSTED_DEVICE, json, this);
        mAddTrustedDeviceTask.mHttpResponseListener = this;
        mAddTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                for (int i = 0; i < permissions.length; i++) {
                    Log.w(permissions[i], grantResults[i] + "");

                    if (permissions[i].equals(Manifest.permission.READ_CONTACTS)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            if (mGetAllContactsResponse != null) {
                                SyncContactsAsyncTask syncContactsAsyncTask = new SyncContactsAsyncTask(this, mGetAllContactsResponse);
                                syncContactsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                    } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            getProfileInfo();
                        } else {
                            MaterialDialog.Builder dialog = new MaterialDialog.Builder(this);
                            dialog.content(getString(R.string.request_for_storage_permission))
                                    .positiveText(R.string.allow_access)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            attemptRequestForPermission();
                                        }
                                    })
                                    .negativeText(R.string.exit)
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                }

                break;

        }
    }

    public void switchToDashBoard() {
        mNavigationView.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new DashBoardFragment()).commit();
        switchedToHomeFragment = true;
    }

    public void switchToPasswordRecovery() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new TrustedNetworkFragment()).commit();
        switchedToHomeFragment = false;
    }

    public void setDrawerMenuVisibility(int id, boolean visible) {
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

            Intent intent = new Intent(HomeActivity.this, ManageBanksActivity.class);
            startActivity(intent);
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_user_activity) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ActivityLogFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_manage_business) {

            Intent intent = new Intent(this, BusinessActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new AboutFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_event) {

            Intent intent = new Intent(HomeActivity.this, EventActivity.class);
            startActivity(intent);
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_security_settings) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new AccountSettingsFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_logout) {

            if (Utilities.isConnectionAvailable(HomeActivity.this)) attemptLogout();
            else {
                finish();
                Intent intent = new Intent(HomeActivity.this, SignupOrLoginActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_account) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.PROFILE_INFO, new Bundle());

        }

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
        pref.edit().putBoolean(Constants.LOGGED_IN, false).apply();

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
        // Get the changes
        boolean isProfilePictureUpdated = true;
        try {
            // TODO migration code, remove try-catch later
            PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(this);
            isProfilePictureUpdated = pushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Take actions
        if (isProfilePictureUpdated) {
            getProfileInfo();
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mLogoutTask = null;
            mGetProfileInfoTask = null;
            mGetAllContactsTask = null;
            mAddTrustedDeviceTask = null;
            Toast.makeText(HomeActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
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

                    String imageUrl = Constants.BASE_URL_FTP_SERVER +
                            Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_HIGH);

                    //saving user info in shared preference
                    ProfileInfoCacheManager profileInfoCacheManager = new ProfileInfoCacheManager(this);
                    profileInfoCacheManager.updateCache(mGetUserInfoResponse.getName(), imageUrl, mGetUserInfoResponse.getAccountStatus());

                    PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(this);
                    pushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, false);
                    mPortrait.setProfilePicture(imageUrl,false);


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
        } else if (result.getApiCommand().equals(Constants.COMMAND_ADD_TRUSTED_DEVICE)) {

            try {
                mAddToTrustedDeviceResponse = gson.fromJson(result.getJsonString(), AddToTrustedDeviceResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String UUID = mAddToTrustedDeviceResponse.getUUID();
                    pref.edit().putString(Constants.UUID, UUID).apply();
                } else {
                    Toast.makeText(this, mAddToTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();
            }

            mAddTrustedDeviceTask = null;

        }
    }

    @Override
    public Context setContext() {
        return HomeActivity.this;
    }

    private BroadcastReceiver mProfilePictureUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newProfilePicture = intent.getStringExtra(Constants.PROFILE_PICTURE);
            Log.d("Broadcast received home", newProfilePicture);
            mPortrait.setProfilePicture(newProfilePicture, true);
        }
    };
}
