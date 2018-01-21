package bd.com.ipay.ipayskeleton.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.AboutActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ActivityLogActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.HelpAndSupportActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.InviteActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManageBanksActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.GetContactsAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAllBusinessListAsyncTask;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetBusinessTypesAsyncTask;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetRelationshipListAsyncTask;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.AutoResizeTextView;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DataCollectors.Model.LocationCollector;
import bd.com.ipay.ipayskeleton.DataCollectors.Model.UserLocation;
import bd.com.ipay.ipayskeleton.HomeFragments.DashBoardFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.NotificationFragment;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.GetAllBusinessContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AccessControl.GetAccessControlResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Employee.GetBusinessInformationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.RemoveEmployeeResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessAccountDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.GetManagedBusinessAccountsResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.Notification;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Relationship;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessAccountSwitch;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener, HttpResponseListener {

    private static final int REQUEST_CODE_PERMISSION = 1001;

    private HttpRequestPostAsyncTask mLocationUpdateRequestAsyncTask;
    private HttpRequestDeleteAsyncTask mRemoveAccountAsyncTask;

    private HttpRequestGetAsyncTask mGetBusinessAccountsAsyncTask;
    private GetManagedBusinessAccountsResponse mGetManagedBusinessAccountsResponse;
    private List<BusinessAccountDetails> mManagedBusinessAccountList = new ArrayList<>();

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;
    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private HttpRequestGetAsyncTask mGetAccessControlTask = null;
    private GetAccessControlResponse mGetAccessControlResponse;

    private HttpRequestGetAsyncTask mGetBusinessInformationAsyncTask;
    private GetBusinessInformationResponse mGetBusinessInformationResponse;

    private GetBusinessTypesAsyncTask mGetBusinessTypesAsyncTask;
    private GetRelationshipListAsyncTask mGetRelationshipListAsyncTask;

    private HttpRequestDeleteAsyncTask mResignFromBusinessAsyncTask;
    private RemoveEmployeeResponse mResignFromBusinessResponse;

    private AutoResizeTextView mMobileNumberView;
    private TextView mNameView;
    private ProfileImageView mProfileImageView;
    private NavigationView mNavigationView;
    private RecyclerView mManagedBusinessListRecyclerView;
    private ImageView mMoreBusinessListImageView;

    private String mUserID;
    private String mDeviceID;

    public ProgressDialog mProgressDialog;
    public static NotificationFragment mNotificationFragment;
    private Menu mOptionsMenu;
    private Menu mNavigationMenu;
    private int mBadgeCount = 0;

    private static boolean switchedToHomeFragment = true;
    private boolean exitFromApplication = false;

    private String onAccountID = null;
    private LocationManager mLocationManager;
    private DrawerLayout drawer;

    private ManagedBusinessAcountAdapter mManageBusinessAcountAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mProgressDialog = new ProgressDialog(HomeActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.logo_ipay);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mManagedBusinessListRecyclerView = (RecyclerView) mNavigationView.getHeaderView(0).findViewById(R.id.managed_business_list);
        mMoreBusinessListImageView = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.drop_arrow);
        mNavigationMenu = mNavigationView.getMenu();

        mMoreBusinessListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mManagedBusinessListRecyclerView.getVisibility() == View.VISIBLE) {
                    mMoreBusinessListImageView.animate().rotation(0).start();
                    mManagedBusinessListRecyclerView.setVisibility(View.GONE);
                } else {
                    mMoreBusinessListImageView.animate().rotation(180).start();
                    mManagedBusinessListRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        if (!ProfileInfoCacheManager.isBusinessAccount() || ProfileInfoCacheManager.isAccountSwitched())
            mNavigationMenu.findItem(R.id.nav_manage_account).setVisible(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        mManagedBusinessListRecyclerView.setHasFixedSize(true);
        mManagedBusinessListRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        mManagedBusinessListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mManagedBusinessListRecyclerView.setVisibility(View.GONE);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Utilities.hideKeyboard(HomeActivity.this);
                mManageBusinessAcountAdapter = new ManagedBusinessAcountAdapter(mManagedBusinessAccountList);
                mManagedBusinessListRecyclerView.setAdapter(mManageBusinessAcountAdapter);
                mManageBusinessAcountAdapter.notifyDataSetChanged();
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

        // Fetch ACL List
        if (SharedPrefManager.isRememberMeActive() && !ProfileInfoCacheManager.isAccountSwitched()) {
            getAccessControlList();
        }

        // Check if important permissions (e.g. Contacts permission) is given. If not,
        // request user for permission.
        attemptRequestForPermission();
        if (Utilities.isNecessaryPermissionExists(this, Constants.LOCATION_PERMISSIONS)) {
            startLocationCollection();
        }

        getAllBusinessAccountsList();

        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_MANAGERS) && !ProfileInfoCacheManager.isAccountSwitched()) {
            getManagedBusinessAccountList();
        } else {
            mManagedBusinessAccountList = new ArrayList<>();
            String userName = "";
            if (Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()).getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE)
                userName = Utilities.getMainBusinessInfo(ProfileInfoCacheManager.getMainUserBusinessInfo()).getBusinessName();
            else
                userName = Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()).getName();

            BusinessAccountDetails tempProfileInfo = new BusinessAccountDetails(Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()).getAccountId(),
                    userName, Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()).getProfilePictures());
            mManagedBusinessAccountList.add(tempProfileInfo);
            mMoreBusinessListImageView.setVisibility(View.VISIBLE);
        }

        // If profile picture gets updated, we need to refresh the profile picture in the drawer.
        LocalBroadcastManager.getInstance(this).registerReceiver(mProfilePictureUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_PICTURE_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(this).registerReceiver(mProfileInfoUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_INFO_UPDATE_BROADCAST));
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationCollection() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Utilities.isNecessaryPermissionExists(this, Constants.LOCATION_PERMISSIONS) && mLocationManager != null) {
            final String locationProvider;
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                locationProvider = LocationManager.GPS_PROVIDER;
            }
            mLocationManager.requestSingleUpdate(locationProvider, this, Looper.myLooper());
        }
    }

    private void getManagedBusinessAccountList() {
        if (mGetBusinessAccountsAsyncTask != null)
            return;

        mGetBusinessAccountsAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_MANAGED_BUSINESS_ACCOUNTS,
                Constants.BASE_URL_MM + Constants.URL_SWITCH_ACCOUNT, this, this);
        mGetBusinessAccountsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_MANAGERS) && !ProfileInfoCacheManager.isAccountSwitched()) {
            getManagedBusinessAccountList();
        }

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
        mProfileImageView.setAccountPhoto(Constants.BASE_URL_FTP_SERVER +
                ProfileInfoCacheManager.getProfileImageUrl(), false);
    }

    private void attemptRequestForPermission() {
        String[] requiredPermissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION};

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

    private void resignFromBusiness(long associationId) {
        if (mResignFromBusinessAsyncTask != null) {
            return;
        }

        mResignFromBusinessAsyncTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_REMOVE_AN_EMPLOYEE,
                Constants.BASE_URL_MM + Constants.URL_REMOVE_AN_EMPLOYEE_FIRST_PART + associationId, this, this);
        mResignFromBusinessAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();
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
                    } else if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        startLocationCollection();
                    }
                }

                break;
        }
    }

    @ValidateAccess
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

    @ValidateAccess
    public void switchToManageBanksActivity() {
        Intent intent = new Intent(HomeActivity.this, ManageBanksActivity.class);
        startActivity(intent);
        switchedToHomeFragment = false;
    }

    @ValidateAccess
    public void switchToActivityLogActivity() {
        Intent intent = new Intent(HomeActivity.this, ActivityLogActivity.class);
        startActivity(intent);
        switchedToHomeFragment = false;
    }

    @ValidateAccess
    public void switchToSecuritySettingsActivity() {
        Intent intent = new Intent(HomeActivity.this, SecuritySettingsActivity.class);
        startActivity(intent);
        switchedToHomeFragment = false;
    }

    @ValidateAccess(ServiceIdConstants.SEE_MANAGERS)
    public void switchToManageAccountsActivity() {
        Intent intent = new Intent(HomeActivity.this, ManagePeopleActivity.class);
        startActivity(intent);
        switchedToHomeFragment = false;
    }

    @ValidateAccess
    public void switchToAboutActivity() {
        Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
        startActivity(intent);
        switchedToHomeFragment = false;
    }

    @ValidateAccess
    public void switchToHelpActivity() {
        Intent intent = new Intent(HomeActivity.this, HelpAndSupportActivity.class);
        startActivity(intent);
        switchedToHomeFragment = false;
    }

    @ValidateAccess
    public void switchToInviteActivity() {
        Intent intent = new Intent(HomeActivity.this, InviteActivity.class);
        startActivity(intent);
        switchedToHomeFragment = false;
    }

    @ValidateAccess
    public void attemptLiveChat() {
        if (isProfileInfoAvailable()) {
            Utilities.initIntercomLogin();
        } else {
            DialogUtils.showAlertDialog(this, getString(R.string.live_chat_not_available));
        }
    }

    @Override
    @ValidateAccess
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

        if (id == R.id.nav_home) {

            switchToDashBoard();

        } else if (id == R.id.nav_account) {

            launchEditProfileActivity(ProfileCompletionPropertyConstants.PROFILE_INFO, new Bundle());
        } else if (id == R.id.nav_bank_account) {

            switchToManageBanksActivity();

        } else if (id == R.id.nav_user_activity) {
            switchToActivityLogActivity();

        } else if (id == R.id.nav_security_settings) {

            switchToSecuritySettingsActivity();

        } else if (id == R.id.nav_invite) {

            switchToInviteActivity();

        } else if (id == R.id.nav_manage_account) {

            switchToManageAccountsActivity();

        } else if (id == R.id.nav_live_chat) {

            attemptLiveChat();

        } else if (id == R.id.nav_help) {

            switchToHelpActivity();

        } else if (id == R.id.nav_about) {

            switchToAboutActivity();
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
        if (ProfileInfoCacheManager.getAccountId() == Constants.INVALID_ACCOUNT_ID) {
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
                            if (SharedPrefManager.isRememberMeActive()) {
                                finish();
                            } else {
                                if (Utilities.isConnectionAvailable(HomeActivity.this)) {
                                    exitFromApplication = true;
                                    attemptLogout();
                                } else {
                                    ProfileInfoCacheManager.setLoggedInStatus(false);
                                    ((MyApplication) HomeActivity.this.getApplication()).clearTokenAndTimer();
                                    finish();
                                }
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

    @ValidateAccess
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
        if (ProfileInfoCacheManager.isAccountSwitched()) {
            // If logout is failed, then we restore the onAccount ID value in token
            onAccountID = TokenManager.getOnAccountId();
            TokenManager.setOnAccountId(Constants.ON_ACCOUNT_ID_DEFAULT);
        }
        TokenManager.setOnAccountId(Constants.ON_ACCOUNT_ID_DEFAULT);
        mProgressDialog.setMessage(getString(R.string.progress_dialog_signing_out));
        mProgressDialog.show();
        String m = Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()).getMobileNumber();
        LogoutRequest mLogoutModel = new LogoutRequest(Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()).getMobileNumber());
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

    private void getAccessControlList() {
        if (mGetAccessControlTask != null || this == null)
            return;

        mGetAccessControlTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ACCESS_CONTROL_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_ACCESS_CONTROL_LIST, HomeActivity.this);
        mGetAccessControlTask.mHttpResponseListener = this;
        mGetAccessControlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mLogoutTask = null;
            mGetProfileInfoTask = null;
            mGetBusinessInformationAsyncTask = null;
            mLocationUpdateRequestAsyncTask = null;
            return;
        }
        mProgressDialog.dismiss();
        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_LOG_OUT:

                try {
                    mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (ProfileInfoCacheManager.isAccountSwitched()) {
                            ProfileInfoCacheManager.setAccountType(Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()).getAccountType());
                            ProfileInfoCacheManager.updateBusinessInfoCache(Constants.ACCOUNT_INFO_DEFAULT);
                            ProfileInfoCacheManager.saveMainUserBusinessInfo(Utilities.getMainBusinessProfileInfoString(Constants.ACCOUNT_INFO_DEFAULT));
                            ProfileInfoCacheManager.updateProfileInfoCache(Utilities.
                                    getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()));
                            ProfileInfoCacheManager.setSwitchAccount(Constants.ACCOUNT_DEFAULT);
                            TokenManager.setOnAccountId(Constants.ON_ACCOUNT_ID_DEFAULT);
                            ProfileInfoCacheManager.setOnAccountId(Constants.ON_ACCOUNT_ID_DEFAULT);
                            ProfileInfoCacheManager.setId(Constants.ACCOUNT_ID_DEFAULT);
                        }
                        Utilities.resetIntercomInformation();
                        if (!exitFromApplication) {
                            ((MyApplication) this.getApplication()).launchLoginPage(null);
                        } else {
                            // Exit the application
                            ((MyApplication) this.getApplication()).clearTokenAndTimer();
                            finish();
                        }
                    } else {
                        if (ProfileInfoCacheManager.isAccountSwitched()) {
                            ((MyApplication) this.getApplication()).launchLoginPage(null);
                        }
                        Toast.makeText(HomeActivity.this, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    if (ProfileInfoCacheManager.isAccountSwitched()) {
                        ((MyApplication) this.getApplication()).launchLoginPage(null);
                    }
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
                        mProfileImageView.setAccountPhoto(Constants.BASE_URL_FTP_SERVER + imageUrl, false);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toaster.makeText(HomeActivity.this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                }

                mGetProfileInfoTask = null;

                break;
            case Constants.COMMAND_GET_ACCESS_CONTROL_LIST:

                try {
                    mGetAccessControlResponse = gson.fromJson(result.getJsonString(), GetAccessControlResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                        // Saving the allowed services id for the user
                        if (mGetAccessControlResponse.getAccessControlList() != null) {
                            ACLManager.updateAllowedServiceArray(mGetAccessControlResponse.getAccessControlList());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mGetAccessControlTask = null;

                break;
            case Constants.COMMAND_GET_BUSINESS_INFORMATION:
                try {
                    mGetBusinessInformationResponse = gson.fromJson(result.getJsonString(), GetBusinessInformationResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mNameView.setText(mGetBusinessInformationResponse.getBusinessName());

                        String imageUrl = Utilities.getImage(mGetBusinessInformationResponse.getProfilePictures(), Constants.IMAGE_QUALITY_HIGH);

                        //saving user info in shared preference
                        ProfileInfoCacheManager.updateBusinessInfoCache(mGetBusinessInformationResponse);
                        ProfileInfoCacheManager.saveMainUserBusinessInfo(Utilities.getMainBusinessProfileInfoString(mGetBusinessInformationResponse));
                        mProfileImageView.setAccountPhoto(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mGetBusinessInformationAsyncTask = null;
                break;
            case Constants.COMMAND_POST_USER_LOCATION:
                mLocationUpdateRequestAsyncTask = null;
                break;

            case Constants.COMMAND_GET_MANAGED_BUSINESS_ACCOUNTS:
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mGetManagedBusinessAccountsResponse = gson.fromJson(result.getJsonString(), GetManagedBusinessAccountsResponse.class);
                        mManagedBusinessAccountList = mGetManagedBusinessAccountsResponse.getBusinessList();
                        if (mManagedBusinessAccountList == null || mManagedBusinessAccountList.size() == 0)
                            mMoreBusinessListImageView.setVisibility(View.INVISIBLE);
                        else {
                            mMoreBusinessListImageView.setVisibility(View.VISIBLE);

                            mManageBusinessAcountAdapter = new ManagedBusinessAcountAdapter(mManagedBusinessAccountList);
                            mManagedBusinessListRecyclerView.setAdapter(mManageBusinessAcountAdapter);
                            mManageBusinessAcountAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mGetBusinessAccountsAsyncTask = null;
                break;
            case Constants.COMMAND_REMOVE_AN_EMPLOYEE:
                mResignFromBusinessResponse = new Gson().fromJson(result.getJsonString(), RemoveEmployeeResponse.class);
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        Toaster.makeText(this, mResignFromBusinessResponse.getMessage(), Toast.LENGTH_LONG);
                        getManagedBusinessAccountList();
                    } else {
                        Toaster.makeText(this, mResignFromBusinessResponse.getMessage(), Toast.LENGTH_LONG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mResignFromBusinessAsyncTask = null;
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
            if (mLocationUpdateRequestAsyncTask == null) {
                try {
                    sendUserLocation(Collections.singletonList(userLocation));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void sendUserLocation(List<UserLocation> userLocationList) {
        LocationCollector locationCollector = new LocationCollector();
        locationCollector.setDeviceId(DeviceInfoFactory.getDeviceId(this));
        locationCollector.setUuid(ProfileInfoCacheManager.getUUID());
        locationCollector.setMobileNumber(ProfileInfoCacheManager.getMobileNumber());
        locationCollector.setLocationList(userLocationList);
        String body = new GsonBuilder().create().toJson(locationCollector);
        mLocationUpdateRequestAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_POST_USER_LOCATION, Constants.BASE_URL_DATA_COLLECTOR + Constants.URL_ENDPOINT_LOCATION_COLLECTOR, body, this, this);
        mLocationUpdateRequestAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mLocationManager.removeUpdates(this);
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

            mProfileImageView.setAccountPhoto(newProfilePicture, true);

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

    private class ManagedBusinessAcountAdapter extends RecyclerView.Adapter<ManagedBusinessAcountAdapter.ViewHolder> {
        private final List<BusinessAccountDetails> items;

        public ManagedBusinessAcountAdapter(List<BusinessAccountDetails> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_manage_business_drawer, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView nameTextView;
            private TextView roleTextView;
            private ProfileImageView profileImageView;
            private ImageView resignFromBusinessImageView;


            public ViewHolder(View itemView) {
                super(itemView);
                nameTextView = (TextView) itemView.findViewById(R.id.title_text_view);
                roleTextView = (TextView) itemView.findViewById(R.id.role_text_view);
                profileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_image_view);
                resignFromBusinessImageView = (ImageView) itemView.findViewById(R.id.leave_account);
            }

            public void bind(final BusinessAccountDetails item) {
                nameTextView.setText(item.getBusinessName());
                if (TextUtils.isEmpty(item.getRoleName())) {
                    roleTextView.setVisibility(View.GONE);
                } else {
                    roleTextView.setText(item.getRoleName());
                }
                if (!ProfileInfoCacheManager.isAccountSwitched() || Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()).getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE)
                    profileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + item.getBusinessProfilePictureUrlHigh(), false);
                else {
                    profileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + item.getBusinessProfilePictureUrlHigh(), false);
                }
                if (ProfileInfoCacheManager.isAccountSwitched()) {
                    resignFromBusinessImageView.setVisibility(View.GONE);
                } else {
                    resignFromBusinessImageView.setVisibility(View.VISIBLE);
                }
                resignFromBusinessImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(HomeActivity.this).
                                setMessage(getString(R.string.do_you_want_to_resign))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        drawer.closeDrawer(GravityCompat.START);
                                        resignFromBusiness(item.getId());
                                    }
                                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

                    }
                });


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }

                        BusinessAccountSwitch businessAccountSwitch = new BusinessAccountSwitch(
                                (int) item.getBusinessAccountId(), HomeActivity.this);
                        businessAccountSwitch.requestSwitchAccount();
                    }
                });
            }
        }
    }
}