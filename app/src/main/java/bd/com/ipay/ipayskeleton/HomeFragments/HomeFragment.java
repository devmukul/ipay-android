package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.CircularProgressBar;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.AddPinDialogBuilder;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Balance.RefreshBalanceResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionStatusResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class HomeFragment extends Fragment implements HttpResponseListener {

    private static boolean profileCompletionPromptShown = false;

    private HttpRequestPostAsyncTask mRefreshBalanceTask = null;
    private RefreshBalanceResponse mRefreshBalanceResponse;

    private HttpRequestGetAsyncTask mGetProfileCompletionStatusTask = null;
    private ProfileCompletionStatusResponse mProfileCompletionStatusResponse;

    private ProgressDialog mProgressDialog;
    private TextView balanceView;

    private TextView mNameView;
    private TextView mMobileNumberView;
    private ImageView mVerificationStatusView;
    private ProfileImageView mProfilePictureView;
    private View mProfileInfo;

    private View mAddMoneyButton;
    private View mWithdrawMoneyButton;
    private LinearLayout mSendMoneyButton;
    private LinearLayout mRequestMoneyButton;
    private LinearLayout mMobileTopUpButton;
    private LinearLayout mMakePaymentButton;
    private ImageView refreshBalanceButton;

    private View mProfileCompletionPromptView;

    private CircularProgressBar mProgressBar;
    private ProgressBar mProgressBarWithoutAnimation;
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

        mProfileCompletionPromptView = v.findViewById(R.id.profile_completion);

        balanceView = (TextView) v.findViewById(R.id.balance);
        mProgressDialog = new ProgressDialog(getActivity());
        refreshBalanceButton = (ImageView) v.findViewById(R.id.refresh_balance_button);

        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mVerificationStatusView = (ImageView) v.findViewById(R.id.verification_status);
        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mProfileInfo = v.findViewById(R.id.profile_info);

        mAddMoneyButton = v.findViewById(R.id.button_add_money);
        mWithdrawMoneyButton = v.findViewById(R.id.button_withdraw_money);
        mSendMoneyButton = (LinearLayout) v.findViewById(R.id.button_send_money);
        mRequestMoneyButton = (LinearLayout) v.findViewById(R.id.button_request_money);
        mMobileTopUpButton = (LinearLayout) v.findViewById(R.id.button_mobile_topup);
        mMakePaymentButton = (LinearLayout) v.findViewById(R.id.button_make_payment);

        mProgressBarWithoutAnimation = (ProgressBar) v.findViewById(R.id.circular_progress_bar);

        mProgressBar = (CircularProgressBar) mProfileCompletionPromptView.findViewById(R.id.profile_completion_percentage);
        mProfileCompletionMessageView = (TextView) mProfileCompletionPromptView.findViewById(R.id.profile_completion_message);
        mCloseButton = (ImageButton) mProfileCompletionPromptView.findViewById(R.id.button_close);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileCompletionPromptView.setVisibility(View.GONE);
            }
        });

        mAddMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess({ServiceIdConstants.ADD_MONEY})
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
            @ValidateAccess({ServiceIdConstants.WITHDRAW_MONEY})
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
            @ValidateAccess({ServiceIdConstants.SEND_MONEY})
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
            @ValidateAccess({ServiceIdConstants.REQUEST_MONEY})
            public void onClick(View v) {
                Intent requestMoneyActivityIntent = new Intent(getActivity(), RequestMoneyActivity.class);
                startActivity(requestMoneyActivityIntent);
            }
        });

        mMakePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess({ServiceIdConstants.MAKE_PAYMENT})
            public void onClick(View v) {
                PinChecker makePaymentPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), PaymentActivity.class);
                        intent.putExtra(PaymentActivity.LAUNCH_NEW_REQUEST, true);
                        startActivity(intent);
                    }
                });
                makePaymentPinChecker.execute();
            }
        });

        mMobileTopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess({ServiceIdConstants.TOP_UP})
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

        refreshBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess({ServiceIdConstants.BALANCE})
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshBalance();
                }
            }
        });
        mProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess({ServiceIdConstants.SEE_PROFILE})
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Refresh balance each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            getProfileCompletionStatus();
        }

        updateProfileData();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProfileInfoUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_INFO_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProfilePictureUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_PICTURE_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBalanceUpdateBroadcastReceiver,
                new IntentFilter(Constants.BALANCE_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProfileCompletionInfoUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_COMPLETION_UPDATE_BROADCAST));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO we should refresh the balance only based on push notification, no need to fetch it
        // from the server every time someone navigates to the home activity. Once push is implemented
        // properly, move it to onCreate.
        if (!ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.BALANCE)) {
            balanceView.setText(R.string.not_available);
            return;
        }
        refreshBalance();
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProfileInfoUpdateBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProfilePictureUpdateBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBalanceUpdateBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProfileCompletionInfoUpdateBroadcastReceiver);

        super.onDestroyView();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);

        if (menu.findItem(R.id.action_filter_by_service) != null)
            menu.findItem(R.id.action_filter_by_service).setVisible(false);
        if (menu.findItem(R.id.action_filter_by_date) != null)
            menu.findItem(R.id.action_filter_by_date).setVisible(false);
    }

    private void updateProfileData() {
        mNameView.setText(ProfileInfoCacheManager.getUserName());
        mMobileNumberView.setText(ProfileInfoCacheManager.getMobileNumber());
        mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER +
                ProfileInfoCacheManager.getProfileImageUrl(), false);

        if (ProfileInfoCacheManager.isAccountVerified())
            mVerificationStatusView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_verified_profile));
        else
            mVerificationStatusView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_not_verified));
    }

    private void promptForProfileCompletion() {

        if (!profileCompletionPromptShown) {
            profileCompletionPromptShown = true;

            mProfileCompletionStatusResponse.analyzeProfileCompletionData();

            if (!mProfileCompletionStatusResponse.isCompletedMandetoryFields()) {

                mProfileCompletionMessageView.setText("Your profile is " +
                        mProfileCompletionStatusResponse.getCompletionPercentage() + "% "
                        + "complete.\nSubmit documents and other information to improve your profile.");

                mProgressBar.startAnimation(mProfileCompletionStatusResponse.getCompletionPercentage());

                mProfileCompletionPromptView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    @ValidateAccess(ServiceIdConstants.TOP_UP)
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
        if (mRefreshBalanceTask != null || getActivity() == null)
            return;

        Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshBalanceButton.startAnimation(rotation);

        mRefreshBalanceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_BALANCE,
                Constants.BASE_URL_SM + Constants.URL_REFRESH_BALANCE, null, getActivity());
        mRefreshBalanceTask.mHttpResponseListener = this;
        mRefreshBalanceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();

            mRefreshBalanceTask = null;
            mGetProfileCompletionStatusTask = null;

            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);

            refreshBalanceButton.clearAnimation();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_REFRESH_BALANCE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mRefreshBalanceResponse = gson.fromJson(result.getJsonString(), RefreshBalanceResponse.class);
                    String balance = mRefreshBalanceResponse.getBalance() + "";
                    if (balance != null) {
                        if (isAdded())
                            balanceView.setText(Utilities.takaWithComma(Double.parseDouble(balance)) + " " + getString(R.string.bdt));
                        SharedPrefManager.setUserBalance(balance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.balance_update_failed, Toast.LENGTH_LONG);
                }
            } else {
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.balance_update_failed, Toast.LENGTH_LONG);
            }

            mRefreshBalanceTask = null;
            refreshBalanceButton.clearAnimation();

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS)) {
            try {
                mProfileCompletionStatusResponse = gson.fromJson(result.getJsonString(), ProfileCompletionStatusResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    promptForProfileCompletion();
                    mProgressBarWithoutAnimation.setProgress(mProfileCompletionStatusResponse.getCompletionPercentage());
                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mProfileCompletionStatusResponse.getMessage(), Toast.LENGTH_LONG);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.failed_fetching_profile_completion_status, Toast.LENGTH_LONG);
            }

            mGetProfileCompletionStatusTask = null;
        }
    }

    private final BroadcastReceiver mProfileInfoUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateProfileData();
        }
    };

    private final BroadcastReceiver mProfilePictureUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newProfilePicture = intent.getStringExtra(Constants.PROFILE_PICTURE);
            Logger.logD("Broadcast home fragment", newProfilePicture);
            mProfilePictureView.setProfilePicture(newProfilePicture, true);
        }
    };

    private final BroadcastReceiver mBalanceUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.BALANCE)) {
                balanceView.setText(R.string.not_available);
                return;
            }
            refreshBalance();
        }
    };

    private final BroadcastReceiver mProfileCompletionInfoUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getProfileCompletionStatus();
        }
    };
}
