package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.UtilityBillPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAllBusinessListAsyncTask;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.TrendingBusinessOutletSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.MakePaymentContactsSearchView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.GetAllBusinessContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.BusinessList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.GetAllTrendingBusinessResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.TrendingBusinessList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.GetProviderResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.Provider;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.ProviderCategory;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MakePaymentNewFragment extends BaseFragment implements HttpResponseListener {

    private static final int REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH = 100;
    private HttpRequestGetAsyncTask mGetTrendingBusinessListTask = null;
	GetAllTrendingBusinessResponse mTrendingBusinessResponse;
	List<TrendingBusinessList> mTrendingBusinessList;

	private HttpRequestGetAsyncTask mGetUtilityProviderListTask;
	private GetProviderResponse mUtilityProviderResponse;
	private List<ProviderCategory> mUtilityProviderTypeList;

    private View mBillPayView;
    private View mLink3BillPayView;
    private View mBrilliantRechargeView;
    private View mWestZoneBillPayView;
    private View mDescoBillPayView;
    private View mDpdcBillPayView;
    private View mDozeBillPayView;
    private View mLankaBanglaView;
    private View mLankaBanglaDpsView;
    private View mAmberITBillPayView;
    private HashMap<String, String> mProviderAvailabilityMap;
    private SwipeRefreshLayout trendingBusinessListRefreshLayout;

    private PinChecker pinChecker;
    private RecyclerView mTrendingListRecyclerView;
    private TrendingListAdapter mTrendingListAdapter;
	private MakePaymentContactsSearchView mMobileNumberEditText;
    private ProgressDialog mProgressDialog;
    private String trendingJson;

    private int transactionType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ipay_make_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProviderAvailabilityMap = new HashMap<>();
        mBillPayView = view.findViewById(R.id.billPayView);
        mLink3BillPayView = view.findViewById(R.id.linkThreeBill);
        mDescoBillPayView = view.findViewById(R.id.desco);
        mWestZoneBillPayView = view.findViewById(R.id.west_zone);
        mDozeBillPayView = view.findViewById(R.id.carnival);
        mDpdcBillPayView = view.findViewById(R.id.dpdc);
        mAmberITBillPayView = view.findViewById(R.id.amberit);
        mLankaBanglaView = view.findViewById(R.id.lankaBanglaViewCard);
        mLankaBanglaDpsView = view.findViewById(R.id.lankaBanglaViewDps);
        mBrilliantRechargeView = view.findViewById(R.id.brilliant_recharge_view);
        trendingBusinessListRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trending_business_list_refresh_layout);
        mMobileNumberEditText = (MakePaymentContactsSearchView) view.findViewById(R.id.searchView);

        mTrendingListRecyclerView = view.findViewById(R.id.trending_business_recycler_view_parent);
        mTrendingListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mTrendingListRecyclerView.setLayoutManager(mLayoutManager);

        trendingJson = SharedPrefManager.getTrendingBusiness(null);
        if(!TextUtils.isEmpty(trendingJson)){
            Gson gson = new Gson();
            mTrendingBusinessResponse = gson.fromJson(trendingJson, GetAllTrendingBusinessResponse.class);
            mTrendingBusinessList = mTrendingBusinessResponse.getTrendingBusinessList();
            mTrendingListAdapter = new TrendingListAdapter(mTrendingBusinessList);
            mTrendingListRecyclerView.setAdapter(mTrendingListAdapter);
        }

        getTrendingBusinessList();
        getServiceProviderList();

        mBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payBill(Constants.BLION, null);
            }
        });

        mLink3BillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payBill(Constants.LINK3, null);
            }
        });

        mBrilliantRechargeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payBill(Constants.BRILLIANT, null);
            }
        });

        mAmberITBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payBill(Constants.AMBERIT, null);
            }
        });

        mLankaBanglaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payBill(Constants.LANKABANGLA, "CARD");
            }
        });

        mLankaBanglaDpsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payBill(Constants.LANKABANGLA, "DPS");
            }
        });

        mWestZoneBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payBill(Constants.WESTZONE, null);
            }
        });

        mDescoBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payBill(Constants.DESCO, null);
            }
        });

        mDozeBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payBill(Constants.CARNIVAL, null);
            }
        });

        mDpdcBillPayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payBill(Constants.DPDC, null);
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

        mMobileNumberEditText.setCustomBillPaymentClickListener(new MakePaymentContactsSearchView.CustomBillPaymentClickListener() {
            @Override
            public void onItemClick(String name, String id) {
                if(name.equals(getContext().getString(R.string.lanka_bangla_card)))
                    payBill(id, "CARD");
                else if(name.equals(getContext().getString(R.string.lanka_bangla_dps)))
                    payBill(id, "DPS");
                else
                    payBill(id, null);
            }
        });
    }

    private void getAllBusinessAccountsList() {
        GetAllBusinessContactRequestBuilder mGetAllBusinessContactRequestBuilder = new GetAllBusinessContactRequestBuilder(0);
        new GetAllBusinessListAsyncTask(getContext(), mGetAllBusinessContactRequestBuilder.getGeneratedUri()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void payBill(final String provider, final String type){
        if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.UTILITY_BILL_PAYMENT)) {
            DialogUtils.showServiceNotAllowedDialog(getContext());
            return;
        } else if (mProviderAvailabilityMap.get(provider) != null) {
            if (!mProviderAvailabilityMap.get(provider).
                    equals(getString(R.string.active))) {
                DialogUtils.showCancelableAlertDialog(getContext(), mProviderAvailabilityMap.get(provider));
                return;
            }
        }
        pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
            @Override
            public void ifPinAdded() {
                Intent intent;
                switch (provider){
                    case Constants.BRILLIANT:
                    case Constants.AMBERIT:
                    case Constants.WESTZONE:
                    case Constants.DESCO:
                    case Constants.DPDC:
                        intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, provider);
                        startActivity(intent);
                        break;
                    case Constants.LINK3:
                        intent = new Intent(getActivity(), IPayUtilityBillPayActionActivity.class);
                        intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.BILL_PAY_LINK_THREE);
                        startActivityForResult(intent, REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH);
                        break;
                    case Constants.CARNIVAL:
                        intent = new Intent(getActivity(), IPayUtilityBillPayActionActivity.class);
                        intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.BILL_PAY_CARNIVAL);
                        startActivityForResult(intent, REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH);
                        break;
                    case Constants.BLION:
                        intent = new Intent(getActivity(), UtilityBillPaymentActivity.class);
                        intent.putExtra(Constants.SERVICE, Constants.BANGLALION);
                        startActivity(intent);
                        break;
                    case Constants.LANKABANGLA:
                        intent = new Intent(getActivity(), IPayUtilityBillPayActionActivity.class);
                        if(type.equals("CARD"))
                            intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.BILL_PAY_LANKABANGLA_CARD);
                        else
                            intent.putExtra(IPayUtilityBillPayActionActivity.BILL_PAY_PARTY_NAME_KEY, IPayUtilityBillPayActionActivity.BILL_PAY_LANKABANGLA_DPS);
                        startActivityForResult(intent, REQUEST_CODE_SUCCESSFUL_ACTIVITY_FINISH);
                        break;
                }
            }
        });
        pinChecker.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

	@Override
	public void onResume() {
		super.onResume();
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
                    SharedPrefManager.setTrendingBusiness(result.getJsonString());
					Gson gson = new Gson();
                    mTrendingBusinessResponse = gson.fromJson(result.getJsonString(), GetAllTrendingBusinessResponse.class);
                    mTrendingBusinessList = mTrendingBusinessResponse.getTrendingBusinessList();
                    mTrendingListAdapter = new TrendingListAdapter(mTrendingBusinessList);
                    mTrendingListRecyclerView.setAdapter(mTrendingListAdapter);

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

                    glide.diskCacheStrategy(DiskCacheStrategy.ALL);

                    glide.placeholder(R.drawable.ic_business_logo_round)
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
                                                    bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + imageURL);
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
                                            bundle.putString(Constants.NAME, merchantDetails.getMerchantName());
                                            bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + merchantDetails.getBusinessLogo());
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
                                        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + merchantDetails.getBusinessLogo());
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
