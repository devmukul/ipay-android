package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.SyncContactsAsyncTask;
import bd.com.ipay.ipayskeleton.DrawerFragments.AccountSettingsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ActivityHistoryFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.DashBoardFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.AddressFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.BasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.DocumentUploadFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EmailFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.FragmentEditAddress;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.Old.ProfileFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.TransactionHistoryFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.RecommendationRequestsFragment;
import bd.com.ipay.ipayskeleton.Model.FireBase.GetFireBaseTokenResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.GetRefreshTokenResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, HttpResponseListener,
        EditBasicInfoFragment.ProfilePictureChangeListener {

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;

    public static HttpRequestPostAsyncTask mRefreshTokenAsyncTask = null;
    public static GetRefreshTokenResponse mGetRefreshTokenResponse;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private HttpRequestGetAsyncTask mGetFireBaseTokenTask = null;
    private GetFireBaseTokenResponse mGetFireBaseTokenResponse;

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
    public static boolean contactsSyncedOnce = false;
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
        Firebase.setAndroidContext(this);

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mMobileNumberView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textview_mobile_number);
        mNameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textview_name);
        mPortrait = (RoundedImageView) navigationView.getHeaderView(0).findViewById(R.id.portrait);
        mMobileNumberView.setText(mUserID);
        navigationView.setNavigationItemSelectedListener(this);

        // Get FireBase Token
        if (!contactsSyncedOnce) getFireBaseToken();

        switchToDashBoard();

        setProfilePicture("");
        // Load the list of available banks, which will be accessed from multiple activities
        getAvailableBankList();
        // TODO: get userinfo here and set
        getProfileInfo();
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
            case R.id.action_transaction_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new TransactionHistoryFragment()).commit();
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
        } else if (!switchedToHomeFragment)
            switchToDashBoard();
        else
            super.onBackPressed();
    }

    private void setProfilePicture(String imageUrl) {
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchToDashBoard() {
        mNavigationView.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new DashBoardFragment()).commit();
        switchedToHomeFragment = true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
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

        } else if (id == R.id.nav_topup) {

            Intent intent = new Intent(HomeActivity.this, TopUpActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_event) {

            Intent intent = new Intent(HomeActivity.this, EventActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new AccountSettingsFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_identification) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new RecommendationRequestsFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_logout) {

            if (Utilities.isConnectionAvailable(HomeActivity.this)) attemptLogout();
            else {
                finish();
                Intent intent = new Intent(HomeActivity.this, SignupOrLoginActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_profile_basic_info) {
            switchToBasicInfoFragment();
        } else if (id == R.id.nav_profile_addresses) {

            switchToAddressFragment();

        } else if (id == R.id.nav_profile_documents) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new DocumentUploadFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_email_addresses) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new EmailFragment()).commit();
            switchedToHomeFragment = false;

        } else if (id == R.id.nav_trusted_network) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new TrustedNetworkFragment()).commit();
            switchedToHomeFragment = false;
        }
    }

    public void switchToAddressFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AddressFragment()).commit();
        switchedToHomeFragment = false;
    }

    public void switchToBasicInfoFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new BasicInfoFragment()).commit();
        switchedToHomeFragment = false;
    }

    public void switchToEditBasicInfoFragment(Bundle bundle) {
        EditBasicInfoFragment editBasicInfoFragment = new EditBasicInfoFragment();
        editBasicInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, editBasicInfoFragment).commit();
        switchedToHomeFragment = false;
    }

    public void switchToAccountSettingsFragmentForPin() {
        AccountSettingsFragment accountSettingsFragment = new AccountSettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.EXPAND_PIN, true);
        accountSettingsFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, accountSettingsFragment).commit();
        switchedToHomeFragment = false;

        mNavigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
    }

    public void switchToEditAddressFragment(Bundle bundle) {
        FragmentEditAddress fragmentEditAddress = new FragmentEditAddress();
        fragmentEditAddress.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentEditAddress).commit();
        switchedToHomeFragment = false;
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
        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL + Constants.URL_LOG_OUT, json, HomeActivity.this);
        mLogoutTask.mHttpResponseListener = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLogoutTask.execute((Void) null);
        }
    }

    private void getFireBaseToken() {
        if (mGetFireBaseTokenTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String mUri = Constants.BASE_URL + "/" + Constants.URL_GET_FIREBASE_TOKEN;
        mGetFireBaseTokenTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_FIREBASE_TOKEN,
                mUri, HomeActivity.this);
        mGetFireBaseTokenTask.mHttpResponseListener = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mGetFireBaseTokenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mGetFireBaseTokenTask.execute((Void) null);
        }
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

        // TODO: execute thread like this in all other places
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mGetProfileInfoTask.execute((Void) null);
        }
    }

    private void getAvailableBankList() {
        GetAvailableBankAsyncTask getAvailableBanksTask = new GetAvailableBankAsyncTask(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getAvailableBanksTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            getAvailableBanksTask.execute();
        }
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mLogoutTask = null;
            mGetProfileInfoTask = null;
            mGetFireBaseTokenTask = null;
            Toast.makeText(HomeActivity.this, R.string.logout_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_LOG_OUT)) {

            try {
                if (resultList.size() > 2) {
                    mLogOutResponse = gson.fromJson(resultList.get(2), LogoutResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        finish();
                        Intent intent = new Intent(HomeActivity.this, SignupOrLoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(HomeActivity.this, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mLogoutTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_USER_INFO)) {

            try {
                mGetUserInfoResponse = gson.fromJson(resultList.get(2), GetUserInfoResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
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

                    setProfilePicture(imageUrl);
                } else {
                    Toast.makeText(HomeActivity.this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
            }

            mGetProfileInfoTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_FIREBASE_TOKEN)) {

            try {
                mGetFireBaseTokenResponse = gson.fromJson(resultList.get(2), GetFireBaseTokenResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    fireBaseToken = mGetFireBaseTokenResponse.getFirebaseToken();

                    // Sync contacts
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new SyncContactsAsyncTask(HomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new SyncContactsAsyncTask(HomeActivity.this).execute();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, R.string.could_not_sync_contact, Toast.LENGTH_SHORT).show();
            }

            if (mProgressDialog != null) mProgressDialog.dismiss();
            mGetFireBaseTokenTask = null;

        }
    }

    @Override
    public void onProfilePictureChange(String imageUrl) {
        setProfilePicture(imageUrl);
    }

    @Override
    public Context setContext() {
        return HomeActivity.this;
    }

}
