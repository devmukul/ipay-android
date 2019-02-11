package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Activities.InviteFriendActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Activities.QRCodeViewerActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Balance.RefreshBalanceResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.DashboardProfileCompletionPOJO;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionStatusResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionMetaData;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static android.view.View.GONE;

public class HomeFragment extends BaseFragment implements HttpResponseListener {

    public static final String BALANCE_KEY = "BALANCE";
    private HttpRequestPostAsyncTask mRefreshBalanceTask = null;
    private HttpRequestGetAsyncTask mTransactionHistoryTask = null;
    private HttpRequestGetAsyncTask mGetProfileCompletionStatusTask = null;
    private ProfileCompletionStatusResponse mProfileCompletionStatusResponse;

    private ProgressDialog mProgressDialog;
    private TextView balanceView;

    // Transaction History
    private TextView mTransactionDescriptionView;
    private TextView mTimeView;
    private TextView mReceiverView;
    private TextView mBalanceTextView;
    private TextView mNetAmountView;
    private ImageView mOtherImageView;
    private ProfileImageView mProfileImageView;
    private ImageView mStatusIconView;

    private RoundedImageView sponsorImageView;
    private TextView sponsorOrBeneficiaryTextView;

    private View mTransactionHistoryView;
    public ImageButton refreshBalanceButton;
    private View mBottomSheet;
    private ImageView mUpArrow;
    private TextView mUpArrowText;

    private RecyclerView mProfileCompletionRecyclerView;
    private ProgressBar mProgressBarTransaction;

    private TransactionHistoryBroadcastReceiver transactionHistoryBroadcastReceiver;

    private final BroadcastReceiver mProfileCompletionInfoUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getProfileCompletionStatus();
        }
    };

    private final BroadcastReceiver mBalanceUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(BALANCE_KEY)) {
                BigDecimal balance = (BigDecimal) intent.getSerializableExtra(BALANCE_KEY);
                if (isAdded() && balance != null) {
                    balanceView.setText(getString(R.string.balance_holder, Utilities.takaWithComma(new BigDecimal(balance.toString()))));
                    SharedPrefManager.setUserBalance(balance.toString());
                }
            } else {
                if (isAdded())
                    refreshBalanceButton.performClick();
                else
                    refreshBalance();
            }
        }
    };

    private class TransactionHistoryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ProfileInfoCacheManager.isAccountVerified()) {
                getTransactionHistory();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        balanceView = view.findViewById(R.id.balance);
        mProgressDialog = new ProgressDialog(getActivity());
        refreshBalanceButton = view.findViewById(R.id.refresh_balance_button);
        View mAddMoneyButton = view.findViewById(R.id.button_add_money);
        View mWithdrawMoneyButton = view.findViewById(R.id.button_withdraw_money);
        LinearLayout mSendMoneyButton = view.findViewById(R.id.continue_button);
        LinearLayout mRequestMoneyButton = view.findViewById(R.id.button_request_money);
        LinearLayout mPayByQRCodeButton = view.findViewById(R.id.button_pay_by_qr_code);
        LinearLayout mMakePaymentButton = view.findViewById(R.id.button_make_payment);
        LinearLayout mTopUpButton = view.findViewById(R.id.button_topup);
        LinearLayout mInviteFriendButton = view.findViewById(R.id.button_invite_to_ipay);
        ImageButton mShowQRCodeButton = view.findViewById(R.id.show_qr_code_button);
        LinearLayout mRequestPaymentButton = view.findViewById(R.id.button_request_paymnet);

        if (ProfileInfoCacheManager.isBusinessAccount()) {
            mRequestPaymentButton.setVisibility(View.VISIBLE);
            mInviteFriendButton.setVisibility(GONE);
        } else {
            mRequestPaymentButton.setVisibility(GONE);
            mInviteFriendButton.setVisibility(View.VISIBLE);
        }

        sponsorImageView = (RoundedImageView) view.findViewById(R.id.sponsor);
        sponsorOrBeneficiaryTextView = (TextView) view.findViewById(R.id.sponsor_name);
        mTransactionDescriptionView = view.findViewById(R.id.activity_description);
        mTimeView = view.findViewById(R.id.time);
        mReceiverView = view.findViewById(R.id.receiver);
        mBalanceTextView = view.findViewById(R.id.amount);
        mNetAmountView = view.findViewById(R.id.net_amount);
        mStatusIconView = view.findViewById(R.id.status_description_icon);
        mProfileImageView = view.findViewById(R.id.profile_picture);
        mOtherImageView = view.findViewById(R.id.other_image);

        mTransactionHistoryView = view.findViewById(R.id.transaction_view);
        mProfileCompletionRecyclerView = view.findViewById(R.id.profile_completion);
        mProgressBarTransaction = view.findViewById(R.id.progress_bar_transaction);

        // find container view
        mBottomSheet = view.findViewById(R.id.bottom_sheet);
        mUpArrow = view.findViewById(R.id.up_arrow);
        mUpArrowText = view.findViewById(R.id.up_arrow_text);

		mAddMoneyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			@ValidateAccess(or = {ServiceIdConstants.ADD_MONEY_BY_BANK, ServiceIdConstants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD, ServiceIdConstants.ADD_MONEY_BY_BANK_INSTANTLY})
			public void onClick(View v) {
				PinChecker addMoneyPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
					@Override
					public void ifPinAdded() {
						Intent intent = new Intent(getActivity(), IPayTransactionActionActivity.class);
						intent.putExtra(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_ADD_MONEY);
						startActivity(intent);
					}
				});
				addMoneyPinChecker.execute();
			}
		});
		if (SharedPrefManager.getUserBalance().equals("0.0")) {
			balanceView.setText(R.string.loading);
		} else {
			try {
				balanceView.setText(getString(R.string.balance_holder, Utilities.takaWithComma(new BigDecimal(SharedPrefManager.getUserBalance()))));
			} catch (Exception e) {
				mTracker.send(new HitBuilders.ExceptionBuilder()
						.setDescription("Parsing Error- " + SharedPrefManager.getUserBalance())
						.build());
			}
		}

        mWithdrawMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess({ServiceIdConstants.WITHDRAW_MONEY})
            public void onClick(View v) {
                PinChecker withdrawMoneyPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), IPayTransactionActionActivity.class);
                        intent.putExtra(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_WITHDRAW_MONEY);
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
                        Intent intent = new Intent(getActivity(), IPayTransactionActionActivity.class);
                        intent.putExtra(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY);
                        startActivity(intent);
                    }
                });
                sendMoneyPinChecker.execute();
            }
        });

        mRequestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IPayTransactionActionActivity.class);
                intent.putExtra(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY);
                startActivity(intent);
            }
        });

        mMakePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.MAKE_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                }
                PinChecker makePaymentPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), IPayTransactionActionActivity.class);
                        intent.putExtra(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_MAKE_PAYMENT);
                        startActivity(intent);
                    }
                });
                makePaymentPinChecker.execute();

            }
        });

        mPayByQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinChecker payByQCPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent;
                        intent = new Intent(getActivity(), QRCodePaymentActivity.class);
                        intent.putExtra(Constants.SPONSOR_LIST, ((HomeActivity) getActivity()).mSponsorList);
                        startActivity(intent);
                    }
                });
                payByQCPinChecker.execute();
            }
        });

        mTopUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess({ServiceIdConstants.TOP_UP})
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.TOP_UP)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                }
                PinChecker pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), IPayTransactionActionActivity.class);
                        intent.putExtra(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_TOP_UP);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });

        mInviteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InviteFriendActivity.class);
                startActivity(intent);
            }
        });

        refreshBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.BALANCE)
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    refreshBalance();
                }
            }
        });

        mShowQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRCodeViewerActivity.class);
                intent.putExtra(Constants.STRING_TO_ENCODE, ProfileInfoCacheManager.getMobileNumber());
                intent.putExtra(Constants.ACTIVITY_TITLE, "My QR Code to Share");
                startActivity(intent);
            }
        });

        mRequestPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.REQUEST_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                }
                PinChecker pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent;
                        intent = new Intent(getActivity(), RequestPaymentActivity.class);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });

        // Refresh balance each time home_activity page appears
        if (Utilities.isConnectionAvailable(getActivity())) {
            if (!ProfileInfoCacheManager.isAccountVerified() && ProfileInfoCacheManager.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE) {
                getProfileCompletionStatus();
            } else {
                getTransactionHistory();
            }
        }

		transactionHistoryBroadcastReceiver = new TransactionHistoryBroadcastReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(transactionHistoryBroadcastReceiver,
				new IntentFilter(Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBalanceUpdateBroadcastReceiver,
                new IntentFilter(Constants.BALANCE_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProfileCompletionInfoUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_COMPLETION_UPDATE_BROADCAST));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            transactionHistoryBroadcastReceiver = new TransactionHistoryBroadcastReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(transactionHistoryBroadcastReceiver,
                    new IntentFilter(Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST));
        }

        // TODO we should refresh the balance only based on push notification, no need to fetch it
        // from the server every time someone navigates to the home activity. Once push is implemented
        // properly, move it to onCreate.
        refreshBalance();
        if (!ProfileInfoCacheManager.isAccountVerified() && ProfileInfoCacheManager.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE) {
            getProfileCompletionStatus();
        } else {
            getTransactionHistory();
        }
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_home));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(transactionHistoryBroadcastReceiver);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBalanceUpdateBroadcastReceiver);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProfileCompletionInfoUpdateBroadcastReceiver);
        }
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

    private void promptForProfileCompletion() {
        mTransactionHistoryView.setVisibility(GONE);
        mProgressBarTransaction.setVisibility(GONE);
        mProfileCompletionRecyclerView.setVisibility(View.VISIBLE);
        List<DashboardProfileCompletionPOJO> requiredInfo = mProfileCompletionStatusResponse.dashboardProfileCompletionData();
        if (requiredInfo != null && requiredInfo.size() > 0) {
            ProfileCompletionAdapter mProfileCompletionAdapter = new ProfileCompletionAdapter(requiredInfo);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            mProfileCompletionRecyclerView.setLayoutManager(mLayoutManager);
            mProfileCompletionRecyclerView.setAdapter(mProfileCompletionAdapter);
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mProfileCompletionRecyclerView);
        } else {
            mTransactionHistoryView.setVisibility(GONE);
            mProgressBarTransaction.setVisibility(View.VISIBLE);
            mProfileCompletionRecyclerView.setVisibility(GONE);
            getTransactionHistory();
        }
    }

    private void refreshBalance() {
        if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.BALANCE)) {
            balanceView.setText(R.string.not_available);
            return;
        }
        if (mRefreshBalanceTask != null || getActivity() == null)
            return;

        Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        refreshBalanceButton.startAnimation(rotation);

        mRefreshBalanceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_BALANCE,
                Constants.BASE_URL_SM + Constants.URL_REFRESH_BALANCE, null, getActivity(), true);
        mRefreshBalanceTask.mHttpResponseListener = this;
        mRefreshBalanceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getProfileCompletionStatus() {
        if (mGetProfileCompletionStatusTask != null) {
            return;
        }

        mGetProfileCompletionStatusTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS,
                Constants.BASE_URL_MM + Constants.URL_GET_PROFILE_COMPLETION_STATUS, getActivity(), this, true);
        mGetProfileCompletionStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getTransactionHistory() {
        if (mTransactionHistoryTask != null) {
            return;
        }
        int historyPageCount = 1;
        String url = TransactionHistoryRequest.generateUri(null,
                null, null, historyPageCount, 1, null);

        mTransactionHistoryTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRANSACTION_HISTORY,
                url, getActivity(), false);
        mTransactionHistoryTask.mHttpResponseListener = this;
        mTransactionHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void loadTransactionHistory(final TransactionHistory transactionHistory) {
        mTransactionHistoryView.setVisibility(View.VISIBLE);
        mProgressBarTransaction.setVisibility(GONE);
        mProfileCompletionRecyclerView.setVisibility(GONE);
        TransactionMetaData metaData;

        final String description = transactionHistory.getShortDescription();
        final String receiver = transactionHistory.getReceiver();
        String responseTime = Utilities.formatDayMonthYear(transactionHistory.getTime());
        final String netAmountWithSign = String.valueOf(Utilities.formatTakaFromString(transactionHistory.getNetAmountFormatted()));
        final Integer statusCode = transactionHistory.getStatusCode();
        if (transactionHistory.getNetAmount() != 0.0) {
            mNetAmountView.setText(netAmountWithSign);
        } else {
            mNetAmountView.setText(Utilities.formatTaka(transactionHistory.getAmount()));
        }
        mBalanceTextView.setVisibility(GONE);

        switch (statusCode) {
            case Constants.TRANSACTION_STATUS_ACCEPTED: {
                mNetAmountView.setPaintFlags(mNetAmountView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.transaction_tick_sign));
                break;
            }
            case Constants.TRANSACTION_STATUS_CANCELLED: {
                mNetAmountView.setPaintFlags(mNetAmountView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.transaction_cross_sign));
                break;
            }
            case Constants.TRANSACTION_STATUS_REJECTED: {
                mNetAmountView.setPaintFlags(mNetAmountView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.transaction_cross_sign));
                break;
            }
            case Constants.TRANSACTION_STATUS_FAILED: {
                mNetAmountView.setPaintFlags(mNetAmountView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mStatusIconView.setImageDrawable(getResources().getDrawable(R.drawable.transaction_cross_sign));
                break;
            }
        }

        mTransactionDescriptionView.setText(description);

        if (receiver != null && !receiver.equals("")) {
            mReceiverView.setVisibility(View.VISIBLE);
            mReceiverView.setText(receiver);
        } else mReceiverView.setVisibility(GONE);

        if (DateUtils.isToday(transactionHistory.getTime())) {
            responseTime = "Today, " + Utilities.formatTimeOnly(transactionHistory.getTime());
        }
        mTimeView.setText(responseTime);

        if (transactionHistory.getAdditionalInfo().getType().equalsIgnoreCase(Constants.TRANSACTION_TYPE_USER)) {
            String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();
            mOtherImageView.setVisibility(View.INVISIBLE);
            mProfileImageView.setVisibility(View.VISIBLE);
            mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
            if (!ProfileInfoCacheManager.isBusinessAccount()) {
                if (transactionHistory.getMetaData() != null) {
                    metaData = transactionHistory.getMetaData();
                    if (metaData.isSponsoredByOther()) {
                        sponsorImageView.setVisibility(View.VISIBLE);
                        sponsorOrBeneficiaryTextView.setVisibility(View.VISIBLE);
                        if (metaData.getSponsorMobileNumber().equals(ContactEngine.formatMobileNumberBD(
                                ProfileInfoCacheManager.getMobileNumber()))) {

                            sponsorOrBeneficiaryTextView.setText("Paid for " + metaData.getBeneficiaryName());

                            if (metaData.getBeneficiaryProfilePictures() != null) {
                                if (metaData.getBeneficiaryProfilePictures().size() != 0) {
                                    Glide.with(getContext())
                                            .load(Constants.BASE_URL_FTP_SERVER + metaData.getBeneficiaryProfilePictures().get(0).getUrl())
                                            .centerCrop()
                                            .error(R.drawable.user_brand_bg)
                                            .into(sponsorImageView);
                                    sponsorImageView.setVisibility(View.VISIBLE);
                                } else {
                                    Glide.with(getContext())
                                            .load(R.drawable.user_brand_bg)
                                            .centerCrop()
                                            .into(sponsorImageView);
                                }
                            } else {
                                Glide.with(getContext())
                                        .load(R.drawable.user_brand_bg)
                                        .centerCrop()
                                        .into(sponsorImageView);
                            }

                        } else {
                            if (metaData.getSponsorProfilePictures() != null) {
                                if (metaData.getSponsorProfilePictures().size() != 0) {
                                    Glide.with(getContext())
                                            .load(Constants.BASE_URL_FTP_SERVER + metaData.getSponsorProfilePictures().get(0).getUrl())
                                            .centerCrop()
                                            .error(R.drawable.user_brand_bg)
                                            .into(sponsorImageView);
                                }
                            }
                            sponsorOrBeneficiaryTextView.setText("Paid by " + metaData.getSponsorName());

                        }

                    } else {
                        sponsorOrBeneficiaryTextView.setVisibility(GONE);
                        sponsorImageView.setVisibility(GONE);
                    }
                }
            }
        } else {
            int iconId = transactionHistory.getAdditionalInfo().getImageWithType(getContext());
            mProfileImageView.setVisibility(View.INVISIBLE);
            mOtherImageView.setVisibility(View.VISIBLE);
            mOtherImageView.setImageResource(iconId);
        }

        mTransactionHistoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.TRANSACTION_DETAILS)
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                intent.putExtra(Constants.TRANSACTION_DETAILS, transactionHistory);
                startActivity(intent);
            }
        });

    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mProgressDialog.dismiss();
            mRefreshBalanceTask = null;
            mGetProfileCompletionStatusTask = null;
            mTransactionHistoryTask = null;
            refreshBalanceButton.clearAnimation();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_REFRESH_BALANCE:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    try {
                        RefreshBalanceResponse mRefreshBalanceResponse = gson.fromJson(result.getJsonString(), RefreshBalanceResponse.class);
                        if (mRefreshBalanceResponse.getBalance() != null) {
                            if (isAdded())
                                balanceView.setText(getString(R.string.balance_holder, Utilities.takaWithComma(mRefreshBalanceResponse.getBalance())));
                            SharedPrefManager.setUserBalance(mRefreshBalanceResponse.getBalance().toString());
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

                break;
            case Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS:
                try {
                    mProfileCompletionStatusResponse = gson.fromJson(result.getJsonString(), ProfileCompletionStatusResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (!ProfileInfoCacheManager.isAccountVerified() && ProfileInfoCacheManager.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE) {
                            promptForProfileCompletion();
                        } else {
                            getTransactionHistory();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mGetProfileCompletionStatusTask = null;
                break;
            case Constants.COMMAND_GET_TRANSACTION_HISTORY:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    try {
                        TransactionHistoryResponse mTransactionHistoryResponse = gson.fromJson(result.getJsonString(), TransactionHistoryResponse.class);
                        loadTransactionHistory(mTransactionHistoryResponse.getTransactions().get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.transaction_history_get_failed, Toast.LENGTH_LONG).show();
                }
                mTransactionHistoryTask = null;
                break;
        }
    }

    private class ProfileCompletionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<DashboardProfileCompletionPOJO> requiredInfo;

        ProfileCompletionAdapter(List<DashboardProfileCompletionPOJO> requiredInfo) {
            this.requiredInfo = requiredInfo;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mTitleView;
            private final TextView mSubTitleView;
            private final TextView mNumberView;
            private final ImageView mImageView;

            public ViewHolder(final View itemView) {
                super(itemView);
                mTitleView = itemView.findViewById(R.id.profile_completion_msg_view);
                mSubTitleView = itemView.findViewById(R.id.profile_completion_subtitle_view);
                mNumberView = itemView.findViewById(R.id.number_view);
                mImageView = itemView.findViewById(R.id.other_image);
            }

            public void bindView(int pos) {
                final DashboardProfileCompletionPOJO profileCompletionData = requiredInfo.get(pos);

                mTitleView.setText(profileCompletionData.getTitle());
                mSubTitleView.setText(profileCompletionData.getSubTitle());
                mImageView.setImageResource(profileCompletionData.getImgDrawable());
                mNumberView.setText(String.format(Locale.getDefault(), "%d/%d", pos + 1, requiredInfo.size()));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (profileCompletionData.getProperty().equals(ProfileCompletionPropertyConstants.PROFILE_PICTURE) && ProfileInfoCacheManager.isAccountVerified()) {
                            DialogUtils.showProfilePictureUpdateRestrictionDialog(getContext());
                        } else {
                            Intent i = new Intent(getActivity(), ProfileActivity.class);
                            i.putExtra(Constants.TARGET_FRAGMENT, profileCompletionData.getProperty());
                            startActivity(i);

                        }
                    }
                });
            }
        }

        // Now define the view holder for Normal list item
        class NormalViewHolder extends ViewHolder {
            NormalViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do whatever you want on clicking the normal items
                    }
                });
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dashboard_profile_completion, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            try {
                NormalViewHolder vh = (NormalViewHolder) holder;
                vh.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return requiredInfo.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

    }

}