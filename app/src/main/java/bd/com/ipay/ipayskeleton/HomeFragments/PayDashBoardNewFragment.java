package bd.com.ipay.ipayskeleton.HomeFragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.CustomDashboardItemView;
import bd.com.ipay.ipayskeleton.CustomView.PayDashBoardHorizontalScrollView;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.TrendingBusiness;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.TrendingBusinessResponse;
import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PayDashBoardNewFragment extends BaseFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetTrendingBusinessListTask = null;
    TrendingBusinessResponse mTrendingBusinessResponse;
    List<TrendingBusiness> mTrendingBusinessList;
    private LinearLayout mScrollViewHolder;
    private View mTopUpView;
    private View mPayByQCView;

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
        View v = inflater.inflate(R.layout.fragment_pay_dashboard_new, container, false);
        mScrollViewHolder = (LinearLayout) v.findViewById(R.id.scrollViewHolder);
        mTopUpView = v.findViewById(R.id.topUpView);
        mPayByQCView = v.findViewById(R.id.payByQCView);

        getActivity().setTitle(R.string.pay);
        getTrendingBusinessList();

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
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_LIST_TRENDING, getActivity());
        mGetTrendingBusinessListTask.mHttpResponseListener = this;
        mGetTrendingBusinessListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.business_contacts_sync_failed, Toast.LENGTH_LONG).show();
                return;
            }
        }
        try {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                Gson gson = new Gson();
                mTrendingBusinessResponse = gson.fromJson(result.getJsonString(), TrendingBusinessResponse.class);
                mTrendingBusinessList = mTrendingBusinessResponse.getTrendingBusinessList();
                for (TrendingBusiness trendingBusiness : mTrendingBusinessList) {
                    String mBusinessType = trendingBusiness.getBusinessType();

                    PayDashBoardHorizontalScrollView payDashBoardHorizontalScrollView = new PayDashBoardHorizontalScrollView(this.getContext());
                    payDashBoardHorizontalScrollView.addHorizontalScrollView(mScrollViewHolder, mBusinessType);

                    List<BusinessAccountEntry> mBusinessAccountEntryList = trendingBusiness.getBusinessProfile();
                    for (final BusinessAccountEntry businessAccountEntry : mBusinessAccountEntryList) {
                        CustomDashboardItemView customDashboardItemView = payDashBoardHorizontalScrollView.addBusinessEntryView(businessAccountEntry);
                        customDashboardItemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pinChecker = new PinChecker(getContext(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent;
                                        intent = new Intent(getActivity(), PaymentActivity.class);
                                        intent.putExtra(Constants.MOBILE_NUMBER, businessAccountEntry.getMobileNumber());
                                        getContext().startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                            }
                        });
                        Logger.logD("trend", businessAccountEntry.getBusinessName());
                    }
                }

            } else {
                if (getActivity() != null) {
                    Toaster.makeText(getActivity(), R.string.business_contacts_sync_failed, Toast.LENGTH_LONG);
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
