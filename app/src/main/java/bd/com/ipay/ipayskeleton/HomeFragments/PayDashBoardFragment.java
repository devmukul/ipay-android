package bd.com.ipay.ipayskeleton.HomeFragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.UtilityBillPaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.CustomDashBoardTitleView;
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

    private HttpRequestGetAsyncTask mGetTrendingBusinessListTask = null;
    GetAllTrendingBusinessResponse mTrendingBusinessResponse;
    List<TrendingBusinessList> mTrendingBusinessList;

    private HttpRequestGetAsyncTask mGetUtilityProviderListTask;
    private GetProviderResponse mUtilityProviderResponse;
    private List<ProviderCategory> mUtilityProviderTypeList;

    private LinearLayout mScrollViewHolder;
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
    private HashMap<String, String> mProviderAvailabilityMap;
    private SwipeRefreshLayout trendingBusinessListRefreshLayout;

    private PinChecker pinChecker;

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
        mScrollViewHolder = (LinearLayout) v.findViewById(R.id.scrollViewHolder);
        mTopUpView = v.findViewById(R.id.topUpView);
        mPayByQCView = v.findViewById(R.id.payByQCView);
        mProviderAvailabilityMap = new HashMap<>();
        mMakePaymentView = v.findViewById(R.id.makePaymentView);
        mRequestPaymentView = v.findViewById(R.id.requestPaymentView);
        mBillPayView = v.findViewById(R.id.billPayView);
        mLink3BillPayView = v.findViewById(R.id.linkThreeBill);
        mDescoBillPayView = v.findViewById(R.id.desco);
        mWestZoneBillPayView = v.findViewById(R.id.west_zone);
        mDozeBillPayView = v.findViewById(R.id.carnival);
        mDpdcBillPayView = v.findViewById(R.id.dpdc);
        mBrilliantRechargeView = v.findViewById(R.id.brilliant_recharge_view);
        trendingBusinessListRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.trending_business_list_refresh_layout);
        getActivity().setTitle(R.string.pay);
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
                if (mScrollViewHolder.getVisibility() == View.VISIBLE) {
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

                    mScrollViewHolder.removeAllViews();

                    mTrendingBusinessResponse = gson.fromJson(result.getJsonString(), GetAllTrendingBusinessResponse.class);
                    mTrendingBusinessList = mTrendingBusinessResponse.getTrendingBusinessList();
                    for (TrendingBusinessList trendingBusiness : mTrendingBusinessList) {

                        String mBusinessType = trendingBusiness.getBusinessType();
                        CustomDashBoardTitleView customDashBoardTitleView = new CustomDashBoardTitleView(getContext());
                        customDashBoardTitleView.setTitleView(mBusinessType);
                        mScrollViewHolder.addView(customDashBoardTitleView);

                        RecyclerView recyclerView = new RecyclerView(getContext());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
                        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(layoutParams);
                        recyclerView.setLayoutParams(params);
                        recyclerView.setNestedScrollingEnabled(false);

                        List<BusinessList> mBusinessAccountEntryList = trendingBusiness.getBusinessList();
                        PayDashBoardItemAdapter payDashBoardItemAdapter = new PayDashBoardItemAdapter(mBusinessAccountEntryList, getActivity());
                        recyclerView.setAdapter(payDashBoardItemAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        mScrollViewHolder.addView(recyclerView);
                        mScrollViewHolder.setVisibility(View.VISIBLE);
                    }

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
}
