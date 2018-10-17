package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.UtilityBillPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.TrendingBusinessOutletSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.MakePaymentContactsSearchView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.BusinessList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.GetAllTrendingBusinessResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.TrendingBusinessList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.GetProviderResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.Provider;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.ProviderCategory;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.IPayTransactionContactFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.TransactionHelperFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static bd.com.ipay.ipayskeleton.Utilities.Constants.BLION;
import static bd.com.ipay.ipayskeleton.Utilities.Constants.DESCO;

public class MakePaymentNewFragment extends BaseFragment implements HttpResponseListener {

	private HttpRequestGetAsyncTask mGetTrendingBusinessListTask = null;
	GetAllTrendingBusinessResponse mTrendingBusinessResponse;
	List<TrendingBusinessList> mTrendingBusinessList;

	private HttpRequestGetAsyncTask mGetUtilityProviderListTask;
	private GetProviderResponse mUtilityProviderResponse;
	private List<ProviderCategory> mUtilityProviderTypeList;

    private View mTopUpView;
    private View mPayByQCView;
    private View mMakePaymentView;
    private View mRequestPaymentView;
    private View mBillPayView;
    private View mLink3BillPayView;
    private View mBrilliantRechargeView;
    private View mWestZoneBillPayView;
    private View mDescoBillPayView;
    private View mDpdcBillPayView;
    private View mDozeBillPayView;
    private View mLankaBanglaView;
    private View mAmberITBillPayView;
    private HashMap<String, String> mProviderAvailabilityMap;
    private SwipeRefreshLayout trendingBusinessListRefreshLayout;

    private PinChecker pinChecker;
    private RecyclerView mTrendingListRecyclerView;
    private TrendingListAdapter mTrendingListAdapter;
	private MakePaymentContactsSearchView mMobileNumberEditText;

    private int isFirstLoad = 0;
    private RecyclerView mContactListRecyclerView;
    private Button mContinueButton;
    private SearchView mContactSearchView;
    private TextView mContactListEmptyMessageTextView;
    private TextView mSearchedNumberTextView;
    private TextView mActionNameTextView;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private String mQuery = "";
    private String mPhoneNumber;


    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private ProgressDialog mProgressDialog;
    private Cursor mCursor;

    private int nameIndex;
    private int originalNameIndex;
    private int phoneNumberIndex;
    private int profilePictureUrlQualityMediumIndex;
    private LinearLayout mSearchedNumberLayout;

    private int transactionType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
        setHasOptionsMenu(true);
        mProgressDialog = new ProgressDialog(getActivity());
        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        resetSearchKeyword();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ipay_make_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTopUpView = view.findViewById(R.id.topUpView);
        mPayByQCView = view.findViewById(R.id.payByQCView);
        mProviderAvailabilityMap = new HashMap<>();
        mMakePaymentView = view.findViewById(R.id.makePaymentView);
        mRequestPaymentView = view.findViewById(R.id.requestPaymentView);
        mBillPayView = view.findViewById(R.id.billPayView);
        mLink3BillPayView = view.findViewById(R.id.linkThreeBill);
        mDescoBillPayView = view.findViewById(R.id.desco);
        mWestZoneBillPayView = view.findViewById(R.id.west_zone);
        mDozeBillPayView = view.findViewById(R.id.carnival);
        mDpdcBillPayView = view.findViewById(R.id.dpdc);
        mAmberITBillPayView = view.findViewById(R.id.amberit);
        mLankaBanglaView = view.findViewById(R.id.lankaBanglaView);
        mBrilliantRechargeView = view.findViewById(R.id.brilliant_recharge_view);
        trendingBusinessListRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trending_business_list_refresh_layout);
        mMobileNumberEditText = (MakePaymentContactsSearchView) view.findViewById(R.id.searchView);

        mTrendingListRecyclerView = view.findViewById(R.id.trending_business_recycler_view_parent);
        mTrendingListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mTrendingListRecyclerView.setLayoutManager(mLayoutManager);

        final Button helperBottomSheetDismissButton = view.findViewById(R.id.helper_bottom_sheet_dismiss_button);
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        final LinearLayout helpBottomSheetLayout = view.findViewById(R.id.help_bottom_sheet_layout);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getFragmentManager() != null) {
            final TransactionHelperFragment transactionHelperFragment = new TransactionHelperFragment();
            transactionHelperFragment.setArguments(getArguments());
            getFragmentManager().beginTransaction().replace(R.id.help_fragment_container, transactionHelperFragment).commit();
        }

        bottomSheetBehavior = BottomSheetBehavior.from(helpBottomSheetLayout);
        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_MAKE_PAYMENT:
                getActivity().setTitle(R.string.make_payment);
                if (SharedPrefManager.ifFirstMakePayment()) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
        }

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    switch (transactionType) {
                        case IPayTransactionActionActivity.TRANSACTION_TYPE_MAKE_PAYMENT:
                            SharedPrefManager.setIfFirstMakePayment(false);
                            break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        helperBottomSheetDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        getTrendingBusinessList();
        getServiceProviderList();


        if (ProfileInfoCacheManager.isBusinessAccount())
            mRequestPaymentView.setVisibility(View.VISIBLE);

        mTopUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.TOP_UP)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), TopUpActivity.class);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });


        mPayByQCView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent;
                        intent = new Intent(getActivity(), QRCodePaymentActivity.class);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });

        mMakePaymentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.MAKE_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), PaymentActivity.class);
                        intent.putExtra(PaymentActivity.LAUNCH_NEW_REQUEST, true);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();

            }
        });

        mRequestPaymentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.REQUEST_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
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

        mBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.BLION) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.BLION).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(BLION));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, Constants.BANGLALION);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });
        mLink3BillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.LINK3) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.LINK3).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.LINK3));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, Constants.LINK3);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });
        mBrilliantRechargeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.BRILLIANT) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.BRILLIANT).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.BRILLIANT));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, Constants.BRILLIANT);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });


        mAmberITBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.AMBERIT) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.AMBERIT).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.AMBERIT));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, Constants.AMBERIT);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });

        mLankaBanglaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.LANKABANGLA) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.LANKABANGLA).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.LANKABANGLA));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), IPayUtilityBillPayActionActivity.class);
                        intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.BILL_PAY_LANKABANGLA_CARD);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });


        mWestZoneBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.WESTZONE) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.WESTZONE).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.WESTZONE));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, Constants.WESTZONE);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });
        mDescoBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(DESCO) != null) {
                    if (!mProviderAvailabilityMap.get(DESCO).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.DESCO)
                        );
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, DESCO);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });
        mDozeBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.CARNIVAL) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.CARNIVAL).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.CARNIVAL));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, Constants.CARNIVAL);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });
        mDpdcBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.DPDC) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.DPDC).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.DPDC));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, Constants.DPDC);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });

        trendingBusinessListRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!trendingBusinessListRefreshLayout.isRefreshing()) {
                    getTrendingBusinessList();
                } else {
                    trendingBusinessListRefreshLayout.setRefreshing(false);
                }
            }
        });

        mMobileNumberEditText.setCustomItemClickListener(new MakePaymentContactsSearchView.CustomItemClickListener() {
            @Override
            public void onItemClick(String name, String mobileNumber, String imageURL, String address, Long outletId) {

                Bundle bundle = new Bundle();
                bundle.putString(Constants.NAME, name);
                bundle.putString(Constants.PHOTO_URI, imageURL);
                bundle.putString(Constants.MOBILE_NUMBER, mobileNumber);
                bundle.putString(Constants.ADDRESS, address);
                if(outletId!=null)
                    bundle.putLong(Constants.OUTLET_ID, outletId);

                bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
                if (getActivity() instanceof IPayTransactionActionActivity) {
                    ((IPayTransactionActionActivity) getActivity()).switchToAmountInputFragment(bundle);
                }
            }
        });

    }

	@Override
	public void onResume() {
		super.onResume();
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.transaction_contact_option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_help:
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private void getTrendingBusinessList() {
		if (mGetTrendingBusinessListTask != null) {
			return;
		}

		mGetTrendingBusinessListTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRENDING_BUSINESS_LIST,
				Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_LIST_TRENDING, getActivity(), false);
		mGetTrendingBusinessListTask.mHttpResponseListener = this;
		mGetTrendingBusinessListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void getServiceProviderList() {
		if (mGetUtilityProviderListTask != null) {
			return;
		}

		mGetUtilityProviderListTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_SERVICE_PROVIDER_LIST,
				Constants.BASE_URL_UTILITY + Constants.URL_GET_PROVIDER, getActivity(), false);
		mGetUtilityProviderListTask.mHttpResponseListener = this;
		mGetUtilityProviderListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void httpResponseReceiver(GenericHttpResponse result) {
		if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
			mGetTrendingBusinessListTask = null;
			mGetUtilityProviderListTask = null;
			trendingBusinessListRefreshLayout.setRefreshing(false);
			return;
		}
		try {
			if (result.getApiCommand().equals(Constants.COMMAND_GET_TRENDING_BUSINESS_LIST)) {
				if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
					Gson gson = new Gson();

                    mTrendingBusinessResponse = gson.fromJson(result.getJsonString(), GetAllTrendingBusinessResponse.class);
                    mTrendingBusinessList = mTrendingBusinessResponse.getTrendingBusinessList();
                    mTrendingListAdapter = new TrendingListAdapter(mTrendingBusinessList);
                    mTrendingListRecyclerView.setAdapter(mTrendingListAdapter);

				} else {
					if (getActivity() != null) {
						Toaster.makeText(getActivity(), R.string.business_contacts_sync_failed, Toast.LENGTH_LONG);
					}
				}
				mGetTrendingBusinessListTask = null;
				trendingBusinessListRefreshLayout.setRefreshing(false);
			} else if (result.getApiCommand().equals(Constants.COMMAND_GET_SERVICE_PROVIDER_LIST)) {
				if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
					mUtilityProviderResponse = new Gson().fromJson(result.getJsonString(), GetProviderResponse.class);
					mUtilityProviderTypeList = mUtilityProviderResponse.getProviderCategories();
					if (mUtilityProviderTypeList != null && mUtilityProviderTypeList.size() != 0) {
						for (int i = 0; i < mUtilityProviderTypeList.size(); i++) {
							for (int j = 0; j < mUtilityProviderTypeList.get(i).getProviders().size(); j++) {
								Provider provider = mUtilityProviderTypeList.get(i).getProviders().get(j);
								if (!provider.isActive()) {
									if (provider.getStatusMessage() != null) {
										mProviderAvailabilityMap.put(provider.getCode().toUpperCase(), provider.getStatusMessage());
									} else {
										mProviderAvailabilityMap.put(provider.getCode().toUpperCase(), getString(R.string.you_cant_avail_this_service));
									}
								} else {
									mProviderAvailabilityMap.put(provider.getCode().toUpperCase(), getString(R.string.active));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

            if (getActivity() != null) {
                Toaster.makeText(getActivity(), R.string.business_contacts_sync_failed, Toast.LENGTH_LONG);
            }
        }
    }


    public class TrendingListAdapter extends     RecyclerView.Adapter<TrendingListAdapter.MyViewHolder> {

        //private List<Movie> moviesList;

        private List<TrendingBusinessList> trendingBusinessList;

        public TrendingListAdapter(List<TrendingBusinessList> trendingBusinessList) {
            this.trendingBusinessList = trendingBusinessList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_trending_business_category, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.titleView.setText(trendingBusinessList.get(position).getBusinessType());
            holder.trendingBusinessCAtegory.setNestedScrollingEnabled(false);
            List<BusinessList> mBusinessAccountEntryList = trendingBusinessList.get(position).getBusinessList();
            PayDashBoardItemAdapter payDashBoardItemAdapter = new PayDashBoardItemAdapter(mBusinessAccountEntryList, getActivity());
            holder.trendingBusinessCAtegory.setAdapter(payDashBoardItemAdapter);
            holder.trendingBusinessCAtegory.setLayoutManager(new GridLayoutManager(getContext(), 4));

        }

        @Override
        public int getItemCount() {
            return trendingBusinessList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView titleView;
            public RecyclerView trendingBusinessCAtegory;

            public MyViewHolder(View view) {
                super(view);
                titleView = (TextView) view.findViewById(R.id.trending_business_category_title);
                trendingBusinessCAtegory = (RecyclerView)view.findViewById(R.id.trending_business_recycler_view_category);
            }
        }
    }

    public class PayDashBoardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<BusinessList> mBusinessAccountEntryList;
        private TrendingBusinessOutletSelectorDialog mMerchantBranchSelectorDialog;
        Context context;

        public PayDashBoardItemAdapter(List<BusinessList> mBusinessAccountEntryList, Context context) {
            this.mBusinessAccountEntryList = mBusinessAccountEntryList;
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mImageView;
            private TextView mTextView;
            private int mColorPalette;

            public ViewHolder(final View itemView) {
                super(itemView);
                mImageView = (ImageView) itemView.findViewById(R.id.imageView);
                mTextView = (TextView) itemView.findViewById(R.id.nameView);
            }

            public void bindView(final int pos) {
                final BusinessList merchantDetails = mBusinessAccountEntryList.get(pos);
                final String name = merchantDetails.getMerchantName();
                final String imageUrl = Constants.BASE_URL_FTP_SERVER + merchantDetails.getBusinessLogo();
                mTextView.setText(name);

                try {

                    final DrawableTypeRequest<String> glide = Glide.with(context).load(imageUrl);

                    glide
                            .diskCacheStrategy(DiskCacheStrategy.ALL);

                    glide
                            .placeholder(R.drawable.ic_business_logo_round)
                            .error(R.drawable.ic_business_logo_round)
                            .crossFade()
                            .dontAnimate()
                            .into(mImageView);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.MAKE_PAYMENT)) {
                            DialogUtils.showServiceNotAllowedDialog(context);
                        } else {

                            PinChecker payByQCPinChecker = new PinChecker(context, new PinChecker.PinCheckerListener() {
                                @Override
                                public void ifPinAdded() {
                                    if (mBusinessAccountEntryList.get(pos).getOutlets()!=null && mBusinessAccountEntryList.get(pos).getOutlets().size() > 0) {
                                        if (mBusinessAccountEntryList.get(pos).getOutlets().size() > 1) {
                                            mMerchantBranchSelectorDialog = new TrendingBusinessOutletSelectorDialog(context, mBusinessAccountEntryList.get(pos));
                                            mMerchantBranchSelectorDialog.showDialog();
                                            mMerchantBranchSelectorDialog.setCustomItemClickListener(new TrendingBusinessOutletSelectorDialog.CustomItemClickListener() {
                                                @Override
                                                public void onItemClick(String name, String mobileNumber, String imageURL, String address, Long outletId) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(Constants.NAME, name);
                                                    bundle.putString(Constants.PHOTO_URI, imageURL);
                                                    bundle.putString(Constants.MOBILE_NUMBER, mobileNumber);
                                                    bundle.putString(Constants.ADDRESS, address);
                                                    bundle.putLong(Constants.OUTLET_ID, outletId);

                                                    bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
                                                    if (getActivity() instanceof IPayTransactionActionActivity) {
                                                        ((IPayTransactionActionActivity) getActivity()).switchToAmountInputFragment(bundle);
                                                    }
                                                }
                                            });
                                        } else {
                                            Bundle bundle = new Bundle();
                                            bundle.putString(Constants.NAME, merchantDetails.getOutlets().get(0).getOutletName());
                                            bundle.putString(Constants.PHOTO_URI, merchantDetails.getBusinessLogo());
                                            bundle.putString(Constants.MOBILE_NUMBER, merchantDetails.getMerchantMobileNumber());
                                            bundle.putString(Constants.ADDRESS, merchantDetails.getOutlets().get(0).getAddressString());
                                            bundle.putLong(Constants.OUTLET_ID, merchantDetails.getOutlets().get(0).getOutletId());

                                            bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
                                            if (getActivity() instanceof IPayTransactionActionActivity) {
                                                ((IPayTransactionActionActivity) getActivity()).switchToAmountInputFragment(bundle);
                                            }
                                        }
                                    } else {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Constants.NAME, merchantDetails.getMerchantName());
                                        bundle.putString(Constants.PHOTO_URI, merchantDetails.getBusinessLogo());
                                        bundle.putString(Constants.MOBILE_NUMBER, merchantDetails.getMerchantMobileNumber());
                                        bundle.putString(Constants.ADDRESS, merchantDetails.getAddressString());

                                        bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
                                        if (getActivity() instanceof IPayTransactionActionActivity) {
                                            ((IPayTransactionActionActivity) getActivity()).switchToAmountInputFragment(bundle);
                                        }
                                    }
                                }
                            });
                            payByQCPinChecker.execute();
                        }
                    }
                });
            }
        }

        class NormalViewHolder extends ViewHolder {
            NormalViewHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trending_business, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                NormalViewHolder vh = (NormalViewHolder) holder;
                vh.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mBusinessAccountEntryList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }


    }
}
