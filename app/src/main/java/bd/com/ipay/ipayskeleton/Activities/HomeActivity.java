package bd.com.ipay.ipayskeleton.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.SyncContactsAsyncTask;
import bd.com.ipay.ipayskeleton.DrawerFragments.AccountSettingsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ActivityHistoryFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.HomeFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.TransactionHistoryFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.RecommendationRequestsFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HttpResponseListener,
        ProfileFragment.ProfilePictureChangeListener {

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private TextView mUserNameTextView;
    private RoundedImageView mPortrait;
    private SharedPreferences pref;
    private String mUserID;
    private Set<UserProfilePictureClass> profilePictures;

    private ProgressDialog mProgressDialog;
    private FloatingActionButton mSendMoneyFAB;
    private FloatingActionButton mMakePaymentFAB;
    private FloatingActionButton mAddMoneyFAB;
    private FloatingActionButton mTopUpFAB;
    private NavigationView mNavigationView;
    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionMenu paymentMenus;

    public static String iPayToken = "";

    private boolean switchedToHomeFragment = true;

    private static final int PROFILE_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mUserID = pref.getString(Constants.USERID, "");
        mProgressDialog = new ProgressDialog(HomeActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_dialog_refreshing));
        profilePictures = new HashSet<>();
        Firebase.setAndroidContext(this);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.payment_menus);

        mSendMoneyFAB = (FloatingActionButton) findViewById(R.id.fab_send_money);
        mMakePaymentFAB = (FloatingActionButton) findViewById(R.id.fab_make_payment);
        mAddMoneyFAB = (FloatingActionButton) findViewById(R.id.fab_cash_in);
        mTopUpFAB = (FloatingActionButton) findViewById(R.id.fab_top_up);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mUserNameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        mPortrait = (RoundedImageView) navigationView.getHeaderView(0).findViewById(R.id.portrait);
        mUserNameTextView.setText(mUserID);
        navigationView.setNavigationItemSelectedListener(this);

        paymentMenus = (FloatingActionMenu) findViewById(R.id.payment_menus);
        createCustomAnimation();
        setActionsOfFAB();

        // TODO: Handle multiple entry in firebase database
        // TODO: Later, we'll update the firebase database periodically
        if (!pref.contains(Constants.FIRST_LAUNCH)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new SyncContactsAsyncTask(HomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new SyncContactsAsyncTask(HomeActivity.this).execute();
            }

        } else {

            // TODO: remove the else part
            // TODO: implement ContactsContract to see the changes in contacts and then update each time
            // TODO: wow. This is complete sh**!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new SyncContactsAsyncTask(HomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new SyncContactsAsyncTask(HomeActivity.this).execute();
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        setProfilePicture("");
        // Load the list of available banks, which will be accessed from multiple activities
        getAvailableBankList();
        // TODO: get userinfo here and set
        getProfileInfo();
//        CommonDataLoader.loadAll(this);
//        Log.i("Token", HomeActivity.iPayToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileInfo();
        // TODO: refresh balance in the navigation drawer here
        // TODO: check if the replace fragment crashes sometimes or not
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ContactsFragment()).commit();
                switchedToHomeFragment = false;
                paymentMenus.close(true);
                return true;
            case R.id.action_transaction:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new TransactionHistoryFragment()).commit();
                switchedToHomeFragment = false;
                paymentMenus.close(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!switchedToHomeFragment)
            switchToHomeFragment();
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

    public void switchToHomeFragment() {
        mNavigationView.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        switchedToHomeFragment = true;
        mFloatingActionMenu.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
            switchedToHomeFragment = true;
            paymentMenus.close(true);

        } else if (id == R.id.nav_profile) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
            switchedToHomeFragment = false;
            paymentMenus.close(true);

        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_bank_account) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new BankAccountsFragment()).commit();
            switchedToHomeFragment = false;
            paymentMenus.close(true);

        } else if (id == R.id.nav_user_activity) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ActivityHistoryFragment()).commit();
            switchedToHomeFragment = false;
            paymentMenus.close(true);

        } else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_withdraw_money) {

            Intent intent = new Intent(HomeActivity.this, CashOutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_request_money) {

            Intent intent = new Intent(HomeActivity.this, RequestMoneyActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_event) {

            Intent intent = new Intent(HomeActivity.this, EventActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new AccountSettingsFragment()).commit();
            switchedToHomeFragment = false;
            paymentMenus.close(true);

        } else if (id == R.id.nav_request_recommendation) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new RecommendationRequestsFragment()).commit();
            switchedToHomeFragment = false;
            paymentMenus.close(true);

        } else if (id == R.id.nav_logout) {

            if (Utilities.isConnectionAvailable(HomeActivity.this)) attemptLogout();
            else {
                finish();
                Intent intent = new Intent(HomeActivity.this, SignupOrLoginActivity.class);
                startActivity(intent);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createCustomAnimation() {

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(paymentMenus.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(paymentMenus.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(paymentMenus.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(paymentMenus.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                paymentMenus.getMenuIconView().setImageResource(paymentMenus.isOpened()
                        ? R.drawable.ic_close_black_24dp : R.drawable.ic_payment_black_24dp);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        paymentMenus.setIconToggleAnimatorSet(set);
    }

    private void setActionsOfFAB() {

        if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE)
            mMakePaymentFAB.setLabelText(getString(R.string.make_payment));
        else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE)
            mMakePaymentFAB.setLabelText(getString(R.string.create_invoice));

        mSendMoneyFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SendMoneyActivity.class);
                startActivity(intent);
            }
        });

        mMakePaymentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MakePaymentActivity.class);
                startActivity(intent);
            }
        });

        mAddMoneyFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CashInActivity.class);
                startActivity(intent);
            }
        });

        mTopUpFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TopUpActivity.class);
                startActivity(intent);
            }
        });
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
                Constants.BASE_URL_POST_MM + Constants.URL_LOG_OUT, json, HomeActivity.this);
        mLogoutTask.mHttpResponseListener = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLogoutTask.execute((Void) null);
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
                    Toast.makeText(HomeActivity.this, R.string.profile_picture_get_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, R.string.profile_picture_get_failed, Toast.LENGTH_SHORT).show();
            }

            mGetProfileInfoTask = null;

        }
    }

    @Override
    public void onProfilePictureChange(String imageUrl) {
        setProfilePicture(imageUrl);
    }
}
