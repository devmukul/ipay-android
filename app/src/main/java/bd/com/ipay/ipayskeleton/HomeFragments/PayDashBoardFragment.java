package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.UtilityBillPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.PayDashBoardItemAdapter;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.BusinessList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.GetAllTrendingBusinessResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.TrendingBusinessList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.GetProviderResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.Provider;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.ProviderCategory;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static bd.com.ipay.ipayskeleton.Utilities.Constants.BLION;
import static bd.com.ipay.ipayskeleton.Utilities.Constants.DESCO;

public class PayDashBoardFragment extends BaseFragment implements HttpResponseListener {

    private static final int REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH = 100;
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
	private View mCarnivalBillPayView;
	private View mLankaBanglaView;
	private View mAmberITBillPayView;
	private View mLankaBanglaDpsView;
	private View mCreditCardBillPayView;
    private HashMap<String, String> mProviderAvailabilityMap;
	private SwipeRefreshLayout trendingBusinessListRefreshLayout;

	private PinChecker pinChecker;
	private RecyclerView mTrendingListRecyclerView;
	private TrendingListAdapter mTrendingListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_paydashboard, container, false);
		mTopUpView = v.findViewById(R.id.topUpView);
		mPayByQCView = v.findViewById(R.id.payByQCView);
		mProviderAvailabilityMap = new HashMap<>();
		mMakePaymentView = v.findViewById(R.id.makePaymentView);
		mRequestPaymentView = v.findViewById(R.id.requestPaymentView);
		mBillPayView = v.findViewById(R.id.billPayView);
		mLink3BillPayView = v.findViewById(R.id.linkThreeBill);
		mDescoBillPayView = v.findViewById(R.id.desco);
		mWestZoneBillPayView = v.findViewById(R.id.west_zone);
		mCarnivalBillPayView = v.findViewById(R.id.carnival);
		mDpdcBillPayView = v.findViewById(R.id.dpdc);
		mAmberITBillPayView = v.findViewById(R.id.amberit);
		mCreditCardBillPayView = v.findViewById(R.id.credit_card_bill);
		mLankaBanglaView = v.findViewById(R.id.lankaBanglaViewCard);
        mLankaBanglaDpsView = v.findViewById(R.id.lankaBanglaViewDps);
		mBrilliantRechargeView = v.findViewById(R.id.brilliant_recharge_view);
		trendingBusinessListRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.trending_business_list_refresh_layout);

		mTrendingListRecyclerView = v.findViewById(R.id.trending_business_recycler_view_parent);
		mTrendingListRecyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
		mTrendingListRecyclerView.setLayoutManager(mLayoutManager);

		getActivity().setTitle(R.string.pay);
		getTrendingBusinessList();
		//getServiceProviderList();


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
                        Intent intent = new Intent(getActivity(), IPayTransactionActionActivity.class);
                        intent.putExtra(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_TOP_UP);
                        startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });


        mCreditCardBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                } else if (mProviderAvailabilityMap.get(Constants.CREDIT_CARD) != null) {
                    if (!mProviderAvailabilityMap.get(Constants.CREDIT_CARD).
                            equals(getString(R.string.active))) {
                        DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(Constants.CREDIT_CARD_WITH_SPACE));
                        return;
                    }
                }
                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), IPayUtilityBillPayActionActivity.class);
                        intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.CREDIT_CARD);
                        startActivityForResult(intent, REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH);
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
                        Intent intent = new Intent(getActivity(), IPayUtilityBillPayActionActivity.class);
                        intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.BILL_PAY_LINK_THREE);
                        startActivityForResult(intent, REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH);
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
                        startActivityForResult(intent, REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH);
                    }
                });
                pinChecker.execute();
            }
        });
        mLankaBanglaDpsView.setOnClickListener(new View.OnClickListener() {
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
                        intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.BILL_PAY_LANKABANGLA_DPS);
                        startActivityForResult(intent, REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH);
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
        mCarnivalBillPayView.setOnClickListener(new View.OnClickListener() {
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
                        Intent intent = new Intent(getActivity(), IPayUtilityBillPayActionActivity.class);
                        intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.BILL_PAY_CARNIVAL);
                        startActivityForResult(intent, REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH);
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

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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


	public class TrendingListAdapter extends RecyclerView.Adapter<TrendingListAdapter.MyViewHolder> {

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
				trendingBusinessCAtegory = (RecyclerView) view.findViewById(R.id.trending_business_recycler_view_category);
			}
		}
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH) {
            if (resultCode == Activity.RESULT_OK) {
                ((DashBoardFragment) getParentFragment()).getViewPager().setCurrentItem(0);
            }
        }
    }
}
