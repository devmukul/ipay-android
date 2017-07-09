package bd.com.ipay.ipayskeleton.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.AboutActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ActivityLogActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.HelpAndSupportActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.InviteActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManageBanksActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.GetContactsAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAllBusinessListAsyncTask;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetBusinessTypesAsyncTask;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetRelationshipListAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.AutoResizeTextView;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HomeFragments.DashBoardFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.NotificationFragment;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.GetAllBusinessContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Employee.GetBusinessInformationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.Notification;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Relationship;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.FCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.AnalyticsConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.IntercomConstants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, HttpResponseListener {

    private static final int REQUEST_CODE_PERMISSION = 1001;

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private HttpRequestGetAsyncTask mGetBusinessInformationAsyncTask;
    private GetBusinessInformationResponse mGetBusinessInformationResponse;

    private GetBusinessTypesAsyncTask mGetBusinessTypesAsyncTask;
    private GetRelationshipListAsyncTask mGetRelationshipListAsyncTask;

    private AutoResizeTextView mMobileNumberView;
    private TextView mNameView;
    private ProfileImageView mProfileImageView;

    private String mUserID;
    private String mDeviceID;

    private ProgressDialog mProgressDialog;
    private NavigationView mNavigationView;

    public static NotificationFragment mNotificationFragment;
    private Menu mOptionsMenu;

    private int mBadgeCount = 0;

    private static boolean switchedToHomeFragment = true;
    private boolean exitFromApplication = false;

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
                mNavigationView.getMenu().getItem(0).setChecked(true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mUserID = ProfileInfoCacheManager.getMobileNumber();
        mDeviceID = DeviceInfoFactory.getDeviceId(HomeActivity.this);

        SharedPrefManager.setFirstLaunch(false);

        mMobileNumberView = (AutoResizeTextView) mNavigationView.getHeaderView(0).findViewById(R.id.textview_mobile_number);
        mNameView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.textview_name);
        mProfileImageView = (ProfileImageView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_picture);
        mMobileNumberView.setText(mUserID);
        mNavigationView.setNavigationItemSelectedListener(this);

        switchToDashBoard();

        updateProfileInfo();

        // Sync contacts
        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.GET_CONTACTS))
            new GetContactsAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // DBContactNode sync is done as follows: first all the contacts are downloaded from the server
        // (#GetContactsAsyncTask) and stored in the database (#SyncContactsAsyncTask).
        // Then difference with phone contacts is calculated, and this difference is sent to the
        // server. If there is any new contact on the phone, we download all contacts from the
        // server again to keep phone and server contacts in sync.

        Logger.logW("Token", TokenManager.getToken());

        // The same notification fragment is used when NotificationActivity is launched.
        // We are initializing it here to load notification badge count.
        mNotificationFragment = new NotificationFragment();
        mNotificationFragment.setOnNotificationUpdateListener(new NotificationFragment.OnNotificationUpdateListener() {
            @Override
            public void onNotificationUpdate(List<Notification> notifications) {
                updateNotificationBadgeCount(notifications.size());
            }
        });
        // We need to show the notification badge count. So loading the notification lists to count
        // the number of pending notifications. Once the notifications are loaded, updateNotificationBadgeCount()
        // is called from NotificationFragment.
        mNotificationFragment.getNotificationLists(this);
        // Registering the notification broadcast receiver
        mNotificationFragment.registerNotificationBroadcastReceiver(this);

        // Load the list of available banks, which will be accessed from multiple activities
        getAvailableBankList();

        // Load the list of available business types, which will be accessed from multiple activities
        getAvailableBusinessTypes();

        // Fetch available relationship list
        getRelationshipList();

        // Check if important permissions (e.g. Contacts permission) is given. If not,
        // request user for permission.
        attemptRequestForPermission();

        // Send Analytics for test purpose in Firebase
        sendAnalytics();

        getAllBusinessAccountsList();

        // If profile picture gets updated, we need to refresh the profile picture in the drawer.
        LocalBroadcastManager.getInstance(this).registerReceiver(mProfilePictureUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_PICTURE_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(this).registerReceiver(mProfileInfoUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_INFO_UPDATE_BROADCAST));
    }

    /**
     * update Profile info fetches from the Profile Information API.
     * If the account type is business then, an additional task is done by calling the
     * Business Information API as the Profile API doesn't provide us the Business Name
     */
    private void updateProfileInfo() {
        getProfileInfo();

        if (ProfileInfoCacheManager.isBusinessAccount()) {
            getBusinessInformation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_activity, menu);
        mOptionsMenu = menu;

        // If the menu is recreated, then restore the previous badge count
        updateNotificationBadgeCount(mBadgeCount);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.hideKeyboard(this);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mProfilePictureUpdateBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mProfileInfoUpdateBroadcastReceiver);
        super.onDestroy();
    }

    private void updateProfileData() {
        mNameView.setText(ProfileInfoCacheManager.getUserName());
        mMobileNumberView.setText(ProfileInfoCacheManager.getMobileNumber());
        mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER +
                ProfileInfoCacheManager.getProfileImageUrl(), false);
    }

    private void attemptRequestForPermission() {
        String[] requiredPermissions = {Manifest.permission.READ_CONTACTS};

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

    private void getAllBusinessAccountsList() {
        GetAllBusinessContactRequestBuilder mGetAllBusinessContactRequestBuilder = new GetAllBusinessContactRequestBuilder(0);
        new GetAllBusinessListAsyncTask(this, mGetAllBusinessContactRequestBuilder.getGeneratedUri()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                for (int i = 0; i < permissions.length; i++) {
                    Logger.logW(permissions[i], grantResults[i] + "");

                    if (permissions[i].equals(Manifest.permission.READ_CONTACTS)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            if (ACLManager.hasServicesAccessibility(ServiceIdConstants.GET_CONTACTS))
                                new GetContactsAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private void updateNotificationBadgeCount(int badgeCount) {
        mBadgeCount = badgeCount;

        Logger.logD("Notification Count", badgeCount + "");
        if (mOptionsMenu != null) {
            if (badgeCount > 0) {
                ActionItemBadge.update(this, mOptionsMenu.findItem(R.id.action_notification), getResources().getDrawable(R.drawable.ic_bell), ActionItemBadge.BadgeStyles.DARK_GREY, badgeCount);
            } else {
                ActionItemBadge.update(this, mOptionsMenu.findItem(R.id.action_notification), getResources().getDrawable(R.drawable.ic_bell), ActionItemBadge.BadgeStyles.DARK_GREY, null);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        int id = item.getItemId();
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.nav_home) {
            drawer.closeDrawer(GravityCompat.START);
        }

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
        if (!ACLManager.checkServicesAccessibilityByNavigationMenuId(id)) {
            DialogUtils.showServiceNotAllowedDialog(HomeActivity.this);
            return;
        }
        if (id == R.id.nav_home) {

            switchToDashBoard();

        } else if (id == R.id.nav_account) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.PROFILE_INFO, new Bundle());
        } else if (id == R.id.nav_bank_account) {

            Intent intent = new Intent(HomeActivity.this, ManageBanksActivity.class);
            startActivity(intent);
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_user_activity) {

            Intent intent = new Intent(HomeActivity.this, ActivityLogActivity.class);
            startActivity(intent);
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_security_settings) {

            Intent intent = new Intent(HomeActivity.this, SecuritySettingsActivity.class);
            startActivity(intent);
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_invite) {

            Intent intent = new Intent(this, InviteActivity.class);
            startActivity(intent);
            switchedToHomeFragment = true;

        } else if (id == R.id.nav_live_chat) {
            if (isProfileInfoAvailable()) {
                Registration registration = Registration.create().withUserId(Integer.toString(ProfileInfoCacheManager.getAccountId()));
                Map<String, Object> customAttributes = getCustomUserAttributes();

                Map<String, Object> userAttributes = new HashMap<>();
                userAttributes.put(IntercomConstants.ATTR_NAME, ProfileInfoCacheManager.getUserName());
                userAttributes.put(IntercomConstants.ATTR_PHONE, ProfileInfoCacheManager.getMobileNumber());
                userAttributes.put(IntercomConstants.ATTR_EMAIL, ProfileInfoCacheManager.getPrimaryEmail());
                userAttributes.put(IntercomConstants.ATTR_MOBILE, DeviceInfoFactory.getDeviceName());
                if (!TextUtils.isEmpty(ProfileInfoCacheManager.getProfileImageUrl())) {
                    Map<String, Object> avatar = new HashMap<>();
                    avatar.put(IntercomConstants.ATTR_TYPE, "avatar");
                    avatar.put(IntercomConstants.ATTR_IMAGE_URL, Constants.BASE_URL_FTP_SERVER + ProfileInfoCacheManager.getProfileImageUrl());
                    userAttributes.put(IntercomConstants.ATTR_AVATAR, avatar);
                }

                userAttributes.put(IntercomConstants.ATTR_CUSTOM_ATTRIBUTES, customAttributes);
                registration.withUserAttributes(userAttributes);

                Intercom.client().registerIdentifiedUser(registration);
                Intercom.client().displayConversationsList();

            } else {
                DialogUtils.showLiveChatNotAvailableDialog(this);
            }
        } else if (id == R.id.nav_help) {

            Intent intent = new Intent(this, HelpAndSupportActivity.class);
            startActivity(intent);
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_logout) {
            if (Utilities.isConnectionAvailable(HomeActivity.this)) {
                attemptLogout();
            } else {
                ((MyApplication) this.getApplication()).launchLoginPage(null);
            }
        }
    }

    private boolean isProfileInfoAvailable() {
        if (!ProfileInfoCacheManager.isProfileInfoFetched()) {
            return false;
        } else if (ProfileInfoCacheManager.isBusinessAccount() && TextUtils.isEmpty(ProfileInfoCacheManager.getUserName())) {
            return false;
        } else {
            return true;
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
                            if (Utilities.isConnectionAvailable(HomeActivity.this)) {
                                exitFromApplication = true;
                                attemptLogout();
                            } else {
                                ProfileInfoCacheManager.setLoggedInStatus(false);
                                ((MyApplication) HomeActivity.this.getApplication()).clearTokenAndTimer();
                                finish();
                            }
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
        LogoutRequest mLogoutModel = new LogoutRequest(ProfileInfoCacheManager.getMobileNumber());
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);

        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL_MM + Constants.URL_LOG_OUT, json, HomeActivity.this);
        mLogoutTask.mHttpResponseListener = this;
        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }

        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_PROFILE_INFO_REQUEST, HomeActivity.this);
        mGetProfileInfoTask.mHttpResponseListener = this;
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBusinessInformation() {
        if (mGetBusinessInformationAsyncTask != null)
            return;

        mGetBusinessInformationAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_INFORMATION,
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_INFORMATION, HomeActivity.this, this);
        mGetBusinessInformationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getAvailableBankList() {
        GetAvailableBankAsyncTask getAvailableBanksTask = new GetAvailableBankAsyncTask(this);
        getAvailableBanksTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getAvailableBusinessTypes() {
        // Load business types, then extract the name of the business type from businessTypeId
        mGetBusinessTypesAsyncTask = new GetBusinessTypesAsyncTask(this, new GetBusinessTypesAsyncTask.BusinessTypeLoadListener() {
            @Override
            public void onLoadSuccess(List<BusinessType> businessTypes) {
            }

            @Override
            public void onLoadFailed() {
            }
        });
        mGetBusinessTypesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getRelationshipList() {
        mGetRelationshipListAsyncTask = new GetRelationshipListAsyncTask(this, new GetRelationshipListAsyncTask.RelationshipLoadListener() {
            @Override
            public void onLoadSuccess(List<Relationship> relationshipList) {
            }

            @Override
            public void onLoadFailed() {

            }
        });
        mGetRelationshipListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mLogoutTask = null;
            mGetProfileInfoTask = null;
            mGetBusinessInformationAsyncTask = null;
            Toast.makeText(HomeActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_LOG_OUT:

                try {
                    mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        Utilities.resetIntercomInformation();
                        if (!exitFromApplication) {
                            ((MyApplication) this.getApplication()).launchLoginPage(null);
                        } else {
                            // Exit the application
                            ((MyApplication) this.getApplication()).clearTokenAndTimer();
                            finish();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mLogoutTask = null;

                break;
            case Constants.COMMAND_GET_PROFILE_INFO_REQUEST:

                try {

                    mGetProfileInfoResponse = gson.fromJson(result.getJsonString(), GetProfileInfoResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                        if (!ProfileInfoCacheManager.isBusinessAccount())
                            mNameView.setText(mGetProfileInfoResponse.getName());

                        String imageUrl = Utilities.getImage(mGetProfileInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_HIGH);

                        //saving user info in shared preference
                        ProfileInfoCacheManager.updateProfileInfoCache(mGetProfileInfoResponse);

                        PushNotificationStatusHolder.setUpdateNeeded(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, false);
                        mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);

                    } else {
                        Toaster.makeText(HomeActivity.this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toaster.makeText(HomeActivity.this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                }

                mGetProfileInfoTask = null;

                break;
            case Constants.COMMAND_GET_BUSINESS_INFORMATION:
                try {
                    mGetBusinessInformationResponse = gson.fromJson(result.getJsonString(), GetBusinessInformationResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mNameView.setText(mGetBusinessInformationResponse.getBusinessName());

                        String imageUrl = Utilities.getImage(mGetBusinessInformationResponse.getProfilePictures(), Constants.IMAGE_QUALITY_HIGH);

                        //saving user info in shared preference
                        ProfileInfoCacheManager.updateBusinessInfoCache(mGetBusinessInformationResponse);
                        PushNotificationStatusHolder.setUpdateNeeded(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, false);
                        mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
                    } else {
                        Toaster.makeText(HomeActivity.this, R.string.failed_loading_business_information, Toast.LENGTH_LONG);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toaster.makeText(HomeActivity.this, R.string.failed_loading_business_information, Toast.LENGTH_LONG);

                }

                mGetBusinessInformationAsyncTask = null;
                break;
        }
    }

    @Override
    public Context setContext() {
        return HomeActivity.this;
    }

    private final BroadcastReceiver mProfilePictureUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newProfilePicture = intent.getStringExtra(Constants.PROFILE_PICTURE);
            Logger.logD("Broadcast home activity", newProfilePicture);

            mProfileImageView.setProfilePicture(newProfilePicture, true);

            // We need to update the profile picture url in ProfileInfoCacheManager. Ideally,
            // we should have received a push from the server and FcmListenerService should have
            // done this task. But as long as push is unreliable, this call is here to stay.
            updateProfileInfo();
        }
    };

    private final BroadcastReceiver mProfileInfoUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateProfileData();
        }
    };

    public Map<String, Object> getCustomUserAttributes() {
        Map<String, Object> customAttributes = new HashMap<>();

        customAttributes.put(IntercomConstants.ATTR_CREATED_AT, System.currentTimeMillis() / 1000L);
        customAttributes.put(IntercomConstants.ATTR_TYPE, ProfileInfoCacheManager.getAccountType(1) == 1 ? Constants.PERSONAL_ACCOUNT : Constants.BUSINESS_ACCOUNT);
        customAttributes.put(IntercomConstants.ATTR_SIGNED_UP_AT, ProfileInfoCacheManager.getSignupTime() / 1000L);
        customAttributes.put(IntercomConstants.ATTR_VERIFICATION_STATUS, ProfileInfoCacheManager.getVerificationStatus());

        return customAttributes;
    }
}
