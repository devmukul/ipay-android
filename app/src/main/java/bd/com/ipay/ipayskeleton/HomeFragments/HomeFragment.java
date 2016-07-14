package bd.com.ipay.ipayskeleton.HomeFragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.MakePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentMakingActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SingleInvoiceActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CircularProgressBar;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.AddPinDialogBuilder;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Balance.RefreshBalanceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Balance.RefreshBalanceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.GetNewsFeedRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.GetNewsFeedResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.NewsFeed.News;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionStatusResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HomeFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRefreshBalanceTask = null;
    private RefreshBalanceResponse mRefreshBalanceResponse;

    private HttpRequestGetAsyncTask mGetNewsFeedTask = null;
    private GetNewsFeedResponse mGetNewsFeedResponse;

    private HttpRequestGetAsyncTask mGetProfileCompletionStatusTask = null;
    private ProfileCompletionStatusResponse mProfileCompletionStatusResponse;

    private SharedPreferences pref;
    private String userID;
    private ProgressDialog mProgressDialog;
    private TextView balanceView;
    public static List<News> newsFeedResponsesList;

    private TextView mNameView;
    private TextView mMobileNumberView;
    private ImageView mVerificationStatusView;
    private ProfileImageView mProfilePictureView;

    private View mAddMoneyButton;
    private View mWithdrawMoneyButton;
    private TextView mSendMoneyButton;
    private TextView mRequestMoneyButton;
    private TextView mMobileTopUpButton;
    private TextView mMakePaymentButton;
    private TextView mPayByQRCodeButton;

    private ImageView refreshBalanceButton;


    public static final int REQUEST_CODE_PERMISSION = 1001;

//    private List<TransactionHistoryClass> userTransactionHistoryClasses;
//    private RecyclerView.LayoutManager mTransactionHistoryLayoutManager;
//    private RecyclerView mTransactionHistoryRecyclerView;
//    private TransactionHistoryAndNewsFeedAdapter mTransactionHistoryAndNewsFeedAdapter;

    private View mProfileCompletionPromptView;

    private final int pageCount = 0;

    private static boolean profileCompletionPromptShown = false;

    private CircularProgressBar mProgressBar;
    private TextView mProfileCompletionMessageView;
    private ImageButton mCloseButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        userID = pref.getString(Constants.USERID, "");

        mProfileCompletionPromptView = v.findViewById(R.id.profile_completion);

        balanceView = (TextView) v.findViewById(R.id.balance);
        mProgressDialog = new ProgressDialog(getActivity());
        refreshBalanceButton = (ImageView) v.findViewById(R.id.refresh_balance_button);

        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mVerificationStatusView = (ImageView) v.findViewById(R.id.verification_status);
        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);

        mAddMoneyButton = v.findViewById(R.id.button_add_money);
        mWithdrawMoneyButton = v.findViewById(R.id.button_withdraw_money);
        mSendMoneyButton = (Button) v.findViewById(R.id.button_send_money);
        mRequestMoneyButton = (Button) v.findViewById(R.id.button_request_money);
        mMobileTopUpButton = (Button) v.findViewById(R.id.button_mobile_topup);
        mMakePaymentButton = (Button) v.findViewById(R.id.button_make_payment);
        mPayByQRCodeButton = (Button) v.findViewById(R.id.button_pay_by_QR_code);

        mProgressBar = (CircularProgressBar) mProfileCompletionPromptView.findViewById(R.id.profile_completion_percentage);
        mProfileCompletionMessageView = (TextView) mProfileCompletionPromptView.findViewById(R.id.profile_completion_message);
        mCloseButton = (ImageButton) mProfileCompletionPromptView.findViewById(R.id.button_close);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileCompletionPromptView.setVisibility(View.GONE);
            }
        });

        if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE)
            mMakePaymentButton.setText(getString(R.string.make_payment));
        else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE)
            mMakePaymentButton.setText(getString(R.string.create_invoice));

        mAddMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinChecker addMoneyPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
                        startActivity(intent);
                    }
                });
                addMoneyPinChecker.execute();
            }
        });

        mWithdrawMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinChecker withdrawMoneyPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), WithdrawMoneyActivity.class);
                        startActivity(intent);
                    }
                });
                withdrawMoneyPinChecker.execute();
            }
        });

        mSendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinChecker sendMoneyPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
                        startActivity(intent);
                    }
                });
                sendMoneyPinChecker.execute();
            }
        });

        mRequestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestMoneyActivityIntent = new Intent(getActivity(), RequestMoneyActivity.class);
                startActivity(requestMoneyActivityIntent);
            }
        });

        mMakePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE) {
                    PinChecker makePaymentPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                        @Override
                        public void ifPinAdded() {
                            Intent intent = new Intent(getActivity(), PaymentMakingActivity.class);
                            startActivity(intent);
                        }
                    });
                    makePaymentPinChecker.execute();
                } else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE) {
                    Intent intent = new Intent(getActivity(), MakePaymentActivity.class);
                    startActivity(intent);
                }
            }
        });

        mPayByQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA},
                            REQUEST_CODE_PERMISSION);
                } else initiateScan();
            }
        });


        mMobileTopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinChecker topUpPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), TopUpActivity.class);
                        startActivity(intent);
                    }
                });
                topUpPinChecker.execute();
            }
        });

//        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.list_transaction_history);
//
//        mTransactionHistoryLayoutManager = new LinearLayoutManager(getActivity());
//        mTransactionHistoryAndNewsFeedAdapter = new TransactionHistoryAndNewsFeedAdapter();
//        mTransactionHistoryRecyclerView.setLayoutManager(mTransactionHistoryLayoutManager);
//        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAndNewsFeedAdapter);

        refreshBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshBalance();
                }
            }
        });

        // Refresh balance each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            // Check if the news feed is already cleared or not
            if (!HomeActivity.newsFeedLoadedOnce) getNewsFeed();

            getProfileCompletionStatus();
        }

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateScan();
                } else {
                    Toast.makeText(getActivity(), R.string.error_permission_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void initiateScan() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (scanResult == null) {
                return;
            }
            final String result = scanResult.getContents();
            if (result != null) {
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PinChecker singleInvoicePinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                @Override
                                public void ifPinAdded() {
                                    Intent intent = new Intent(getActivity(), SingleInvoiceActivity.class);
                                    intent.putExtra(Constants.RESULT,result);
                                    startActivity(intent);
                                }
                            });
                            singleInvoicePinChecker.execute();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), R.string.error_invalid_QR_code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }


    private BroadcastReceiver mProfileInfoUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateProfileData();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();

        updateProfileData();
        refreshBalance();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProfileInfoUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_INFO_UPDATE_BROADCAST));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProfileInfoUpdateBroadcastReceiver);

        super.onPause();
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    public void updateProfileData() {
        ProfileInfoCacheManager profileInfoCacheManager = new ProfileInfoCacheManager(getActivity());

        mNameView.setText(profileInfoCacheManager.getName());
        mMobileNumberView.setText(profileInfoCacheManager.getMobileNumber());
        boolean imageLoadedFromDisk = mProfilePictureView.setProfilePictureFromDisk(profileInfoCacheManager.getMobileNumber(), true);
        if (!imageLoadedFromDisk)
            mProfilePictureView.setProfilePicture(profileInfoCacheManager.getProfileImageUrl(), false);

        if (profileInfoCacheManager.getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED))
            mVerificationStatusView.setVisibility(View.VISIBLE);
        else
            mVerificationStatusView.setVisibility(View.GONE);
    }

    private void promptForProfileCompletion() {

        if (!profileCompletionPromptShown) {
            profileCompletionPromptShown = true;

            mProfileCompletionStatusResponse.analyzeProfileCompletionData();

            if (!mProfileCompletionStatusResponse.isCompletedMandetoryFields()) {

                mProfileCompletionMessageView.setText("Your profile is " +
                        mProfileCompletionStatusResponse.getCompletionPercentage() + "% "
                        + "complete. Complete your profile to get verified.");

                mProgressBar.startAnimation(mProfileCompletionStatusResponse.getCompletionPercentage());

                mProfileCompletionPromptView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProfileCompletionPromptView.setVisibility(View.GONE);

                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra(Constants.TARGET_FRAGMENT, ProfileCompletionPropertyConstants.PROFILE_COMPLETENESS);
                        startActivity(intent);
                    }
                });

                mProfileCompletionPromptView.setVisibility(View.VISIBLE);
            } else {
                // "Good to have" properties
                List<ProfileCompletionStatusResponse.PropertyDetails> otherCompletionDetails =
                        mProfileCompletionStatusResponse.getOtherCompletionDetails();
                final List<ProfileCompletionStatusResponse.PropertyDetails> incompleteOtherCompletionDetails = new ArrayList<>();
                for (ProfileCompletionStatusResponse.PropertyDetails propertyDetails : otherCompletionDetails) {
                    if (!propertyDetails.isCompleted()) {
                        incompleteOtherCompletionDetails.add(propertyDetails);
                    }
                }

                if (incompleteOtherCompletionDetails.size() > 0) {
                    Random random = new Random();

                    /**
                     * We want to show the prompt once in every five launch on average.
                     */
                    if (random.nextInt(5) == 0) {
                        int index = random.nextInt(incompleteOtherCompletionDetails.size());
                        final ProfileCompletionStatusResponse.PropertyDetails incompletePropertyDetails = incompleteOtherCompletionDetails.get(index);

                        String profileCompletionMessage = "Your profile is " +
                                mProfileCompletionStatusResponse.getCompletionPercentage() + "% "
                                + "complete.\n"
                                + incompletePropertyDetails.getPropertyTitle()
                                + " to improve your profile";
                        String completeButtonMessage = incompletePropertyDetails.getActionName();

                        mProfileCompletionMessageView.setText(profileCompletionMessage);

                        /**
                         * For ADD_PIN, we show a PIN input dialog to the user.
                         * For other cases, we forward the user to the corresponding fragment
                         * in the ProfileActivity.
                         */
                        mProfileCompletionPromptView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (incompletePropertyDetails.getPropertyName().equals(ProfileCompletionPropertyConstants.ADD_PIN)) {
                                    AddPinDialogBuilder addPinDialogBuilder = new AddPinDialogBuilder(getActivity(), null);
                                    addPinDialogBuilder.show();
                                } else {
                                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                                    intent.putExtra(Constants.TARGET_FRAGMENT, incompletePropertyDetails.getPropertyName());
                                    startActivity(intent);
                                }
                            }
                        });

                        mProgressBar.startAnimation(mProfileCompletionStatusResponse.getCompletionPercentage());

                        mProfileCompletionPromptView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void refreshBalance() {
        if (mRefreshBalanceTask != null) {
            return;
        }

        Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshBalanceButton.startAnimation(rotation);

        RefreshBalanceRequest mLoginModel = new RefreshBalanceRequest(pref.getString(Constants.USERID, ""));
        Gson gson = new Gson();
        String json = gson.toJson(mLoginModel);
        mRefreshBalanceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_BALANCE,
                Constants.BASE_URL_SM + Constants.URL_REFRESH_BALANCE, json, getActivity());
        mRefreshBalanceTask.mHttpResponseListener = this;
        mRefreshBalanceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getNewsFeed() {
        if (mGetNewsFeedTask != null) {
            return;
        }

        GetNewsFeedRequestBuilder mGetNewsFeedRequestBuilder = new GetNewsFeedRequestBuilder(pageCount);

        String mUri = mGetNewsFeedRequestBuilder.getGeneratedUri();
        mGetNewsFeedTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_NEWS_FEED,
                mUri, getActivity());
        mGetNewsFeedTask.mHttpResponseListener = this;
        mGetNewsFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getProfileCompletionStatus() {
        if (mGetProfileCompletionStatusTask != null) {
            return;
        }

        mGetProfileCompletionStatusTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS,
                Constants.BASE_URL_MM + Constants.URL_GET_PROFILE_COMPLETION_STATUS, getActivity(), this);
        mGetProfileCompletionStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();

            mRefreshBalanceTask = null;
            mGetNewsFeedTask = null;
            mGetProfileCompletionStatusTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG).show();

            refreshBalanceButton.clearAnimation();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_REFRESH_BALANCE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mRefreshBalanceResponse = gson.fromJson(result.getJsonString(), RefreshBalanceResponse.class);
                    String balance = mRefreshBalanceResponse.getBalance() + "";
                    if (balance != null)
                        balanceView.setText(balance + " " + getString(R.string.bdt));
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.balance_update_failed, Toast.LENGTH_LONG).show();
                }
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.balance_update_failed, Toast.LENGTH_LONG).show();
            }

            refreshBalanceButton.clearAnimation();
            mRefreshBalanceTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_NEWS_FEED)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mGetNewsFeedResponse = gson.fromJson(result.getJsonString(), GetNewsFeedResponse.class);

                    if (newsFeedResponsesList == null) {
                        newsFeedResponsesList = mGetNewsFeedResponse.getNewsFeed();
                    } else {
                        List<News> tempUserActivityResponsesList;
                        tempUserActivityResponsesList = mGetNewsFeedResponse.getNewsFeed();
                        newsFeedResponsesList.addAll(tempUserActivityResponsesList);
                    }

                    HomeActivity.newsFeedLoadedOnce = true;
                    // TODO: Handle news feed hasNext in future
//                    mTransactionHistoryAndNewsFeedAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.news_feed_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.news_feed_get_failed, Toast.LENGTH_LONG).show();
            }


            mGetNewsFeedTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS)) {
            try {
                mProfileCompletionStatusResponse = gson.fromJson(result.getJsonString(), ProfileCompletionStatusResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    promptForProfileCompletion();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mProfileCompletionStatusResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_fetching_profile_completion_status, Toast.LENGTH_LONG).show();
            }

            mGetProfileCompletionStatusTask = null;
        }
    }

//    private class TransactionHistoryAndNewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//        private static final int TRANSACTION_HISTORY_HEADER_VIEW = 1;
//        private static final int WHATS_NEW_VIEW = 2;
//        private static final int NEWS_FEED_ITEM_VIEW = 3;
//
//        public class TransactionHistoryViewHolder extends RecyclerView.ViewHolder {
//            private View mItemView;
//
//            private TextView mTransactionDescription;
//            private TextView mTime;
//            private RoundedImageView mPortrait;
//            private TextView mAmountTextView;
//            private ImageView statusView;
//
//            private ImageView mNewsImage;
//            private TextView mNewsHeadLine;
//            private TextView mNewsSubDescription;
//            private TextView mNewsShortDescription;
//
//            public TransactionHistoryViewHolder(final View itemView) {
//                super(itemView);
//
//                mItemView = itemView;
//
//                mTransactionDescription = (TextView) itemView.findViewById(R.id.activity_description);
//                mTime = (TextView) itemView.findViewById(R.id.time);
//                mAmountTextView = (TextView) itemView.findViewById(R.id.amount);
//                statusView = (ImageView) itemView.findViewById(R.id.status);
//                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
//
//                mNewsImage = (ImageView) itemView.findViewById(R.id.news_image);
//                mNewsHeadLine = (TextView) itemView.findViewById(R.id.news_title);
//                mNewsSubDescription = (TextView) itemView.findViewById(R.id.short_news);
//                mNewsShortDescription = (TextView) itemView.findViewById(R.id.short_desc);
//            }
//
//            public void bindViewTransactionHistory(int pos) {
//
//                // Decrease pos by 1 as there is a header view now.
//                pos = pos - 1;
//
//                double amount = userTransactionHistoryClasses.get(pos).getAmount(userID);
//
//                final double amountWithoutProcessing = userTransactionHistoryClasses.get(pos).getAmount();
//                final double fee = userTransactionHistoryClasses.get(pos).getFee();
//                final double netAmount = userTransactionHistoryClasses.get(pos).getNetAmount();
//                final String transactionID = userTransactionHistoryClasses.get(pos).getTransactionID();
//                final String purpose = userTransactionHistoryClasses.get(pos).getPurpose();
//                final Integer statusCode = userTransactionHistoryClasses.get(pos).getStatusCode();
//                final double balance = userTransactionHistoryClasses.get(pos).getBalance();
//                final String description = userTransactionHistoryClasses.get(pos).getDescription();
//                final String time = new SimpleDateFormat("EEE, MMM d, ''yy, h:mm a").format(userTransactionHistoryClasses.get(pos).getTime());
//
//                mAmountTextView.setText(Utilities.formatTaka(amount));
//
//                mTransactionDescription.setText(description);
//                mTime.setText(time);
//
//                if (userTransactionHistoryClasses.get(pos).getStatusCode() == Constants.HTTP_RESPONSE_STATUS_OK) {
//                    mAmountTextView.setTextColor(getResources().getColor(R.color.colorTextPrimary));
//                    statusView.setColorFilter(Color.GREEN);
//                    statusView.setImageResource(R.drawable.ic_check_circle_black_24dp);
//
//                } else if (userTransactionHistoryClasses.get(pos).getStatusCode() == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
//                    mAmountTextView.setTextColor(getResources().getColor(R.color.text_gray));
//                    statusView.setColorFilter(Color.GRAY);
//                    statusView.setImageResource(R.drawable.ic_cached_black_24dp);
//
//                } else {
//                    mAmountTextView.setTextColor(getResources().getColor(R.color.background_red));
//                    statusView.setColorFilter(Color.RED);
//                    statusView.setImageResource(R.drawable.ic_error_black_24dp);
//                }
//
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showTransactionHistoryDialogue(amountWithoutProcessing, fee, netAmount,
//                                balance, purpose, time, statusCode, description, transactionID);
//                    }
//                });
//
//                //TODO: remove this when pro pic will come
////                Glide.with(getActivity())
////                        .load(R.drawable.ic_transaction_history)
////                        .into(mPortrait);
//            }
//
//            public void bindViewNewsFeed(int pos) {
//
//                if (userTransactionHistoryClasses == null) pos = pos - 1;
//                else {
//                    if (userTransactionHistoryClasses.size() == 0) pos = pos - 1;
//                    else pos = pos - userTransactionHistoryClasses.size() - 2;
//                }
//
//                final long newsID = newsFeedResponsesList.get(pos).getId();
//                final String description = newsFeedResponsesList.get(pos).getDescription();
//                final String title = newsFeedResponsesList.get(pos).getTitle();
//                final String subDescription = newsFeedResponsesList.get(pos).getSubDescription();
//                final String imageUrl = newsFeedResponsesList.get(pos).getImageUrl();
//                final String imageUrlThumbnail = newsFeedResponsesList.get(pos).getImageThumbnailUrl();
//
//                if (title != null) mNewsHeadLine.setText(title);
//                if (subDescription != null) mNewsSubDescription.setText(subDescription);
//                if (description != null) mNewsShortDescription.setText(description);
//
//                if (imageUrl != null) Glide.with(getActivity())
//                        .load(Constants.BASE_URL_FTP_SERVER + imageUrl)
//                        .crossFade()
//                        .placeholder(R.drawable.dummy)
//                        .into(mNewsImage);
//            }
//
//            public View getItemView() {
//                return mItemView;
//            }
//        }
//
//        public class HeaderViewHolder extends TransactionHistoryViewHolder {
//            public HeaderViewHolder(View itemView) {
//                super(itemView);
//            }
//        }
//
//        public class WhatsNewViewHolder extends TransactionHistoryViewHolder {
//            public WhatsNewViewHolder(View itemView) {
//                super(itemView);
//            }
//        }
//
//        public class NewsFeedViewHolder extends TransactionHistoryViewHolder {
//            public NewsFeedViewHolder(View itemView) {
//                super(itemView);
//            }
//        }
//
//        // Now define the ViewHolder for Normal list item
//        public class NormalViewHolder extends TransactionHistoryViewHolder {
//            public NormalViewHolder(View itemView) {
//                super(itemView);
//            }
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//            View v;
//
//            if (viewType == WHATS_NEW_VIEW) {
//                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_whats_new, parent, false);
//                WhatsNewViewHolder vh = new WhatsNewViewHolder(v);
//                return vh;
//
//            } else if (viewType == TRANSACTION_HISTORY_HEADER_VIEW) {
//                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_transaction_histories, parent, false);
//                HeaderViewHolder vh = new HeaderViewHolder(v);
//                return vh;
//
//            } else if (viewType == NEWS_FEED_ITEM_VIEW) {
//                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_feed, parent, false);
//                NewsFeedViewHolder vh = new NewsFeedViewHolder(v);
//                return vh;
//            }
//
//            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transaction_history, parent, false);
//            NormalViewHolder vh = new NormalViewHolder(v);
//
//            return vh;
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            try {
//                if (holder instanceof NormalViewHolder) {
//                    NormalViewHolder vh = (NormalViewHolder) holder;
//                    vh.bindViewTransactionHistory(position);
//                } else if (holder instanceof WhatsNewViewHolder) {
//                    WhatsNewViewHolder vh = (WhatsNewViewHolder) holder;
//                } else if (holder instanceof HeaderViewHolder) {
//                    HeaderViewHolder vh = (HeaderViewHolder) holder;
//                } else if (holder instanceof NewsFeedViewHolder) {
//                    NewsFeedViewHolder vh = (NewsFeedViewHolder) holder;
//                    vh.bindViewNewsFeed(position);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//
//            int transactionHistoryListSize = 0;
//            int newsFeedListSize = 0;
//
//            if (userTransactionHistoryClasses == null && newsFeedResponsesList == null) return 0;
//
//            if (userTransactionHistoryClasses != null)
//                transactionHistoryListSize = userTransactionHistoryClasses.size();
//            if (newsFeedResponsesList != null) newsFeedListSize = newsFeedResponsesList.size();
//
//            if (transactionHistoryListSize > 0 && newsFeedListSize > 0)
//                return 1 + transactionHistoryListSize + 1 + newsFeedListSize;   // Header, transaction histories, whats new header , news feed list
//            else if (transactionHistoryListSize > 0 && newsFeedListSize == 0)
//                return 1 + transactionHistoryListSize;                          // Header, transaction histories
//            else if (transactionHistoryListSize == 0 && newsFeedListSize > 0)
//                return 1 + newsFeedListSize;                                    // whats new header , news feed list
//            else return 0;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//
//            int transactionHistoryListSize = 0;
//            int newsFeedListSize = 0;
//
//            if (userTransactionHistoryClasses == null && newsFeedResponsesList == null)
//                return super.getItemViewType(position);
//
//            if (userTransactionHistoryClasses != null)
//                transactionHistoryListSize = userTransactionHistoryClasses.size();
//            if (newsFeedResponsesList != null) newsFeedListSize = newsFeedResponsesList.size();
//
//            if (transactionHistoryListSize > 0 && newsFeedListSize > 0) {
//                if (position == 0) return TRANSACTION_HISTORY_HEADER_VIEW;
//                else if (position == transactionHistoryListSize + 1) return WHATS_NEW_VIEW;
//                else if (position > transactionHistoryListSize + 1) return NEWS_FEED_ITEM_VIEW;
//                else return super.getItemViewType(position);
//
//            } else if (transactionHistoryListSize > 0 && newsFeedListSize == 0) {
//                if (position == 0) return TRANSACTION_HISTORY_HEADER_VIEW;
//                else return super.getItemViewType(position);
//
//            } else if (transactionHistoryListSize == 0 && newsFeedListSize > 0) {
//                if (position == 0) return WHATS_NEW_VIEW;
//                else return NEWS_FEED_ITEM_VIEW;
//            }
//
//            return super.getItemViewType(position);
//        }
//    }
}
