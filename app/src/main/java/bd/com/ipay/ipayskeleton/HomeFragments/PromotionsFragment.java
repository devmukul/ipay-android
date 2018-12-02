package bd.com.ipay.ipayskeleton.HomeFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Adapters.PromotionAdapter;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;
import bd.com.ipay.ipayskeleton.Model.Promotion.PromotionMetaData;
import bd.com.ipay.ipayskeleton.QRScanner.BarcodeCaptureActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.ViewHolders.Promotions.PromotionViewHolder;
import bd.com.ipay.ipayskeleton.ViewModel.ProgressDialogListener;
import bd.com.ipay.ipayskeleton.ViewModel.PromotionsViewModel;

public class PromotionsFragment extends ProgressFragment implements ProgressDialogListener {

	private static final int CAMERA_PERMISSION_REQUEST_CODE = 8211;
	private PromotionsViewModel mPromotionsViewModel;
	private PromotionAdapter promotionAdapter;
	private TextView mNoPromotionAvailableMessageTextView;
	private SwipeRefreshLayout promotionsRefreshLayout;
	private CustomProgressDialog progressDialog;
	private SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault());

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progressDialog = new CustomProgressDialog(getActivity());
		if (getActivity() != null) {
			mPromotionsViewModel = ViewModelProviders.of(getActivity()).get(PromotionsViewModel.class);
		} else {
			mPromotionsViewModel = ViewModelProviders.of(this).get(PromotionsViewModel.class);
		}
		mPromotionsViewModel.progressDialogListener = this;
	}

	private final Observer<List<Promotion>> promotionListObserver = new Observer<List<Promotion>>() {
		@Override
		public void onChanged(@Nullable List<Promotion> promotions) {
			promotionAdapter.setItem(promotions);
			promotionAdapter.notifyDataSetChanged();
			setContentShown(true);
			promotionsRefreshLayout.setRefreshing(false);
			if (promotions == null || promotions.size() == 0) {
				mNoPromotionAvailableMessageTextView.setVisibility(View.VISIBLE);
			} else {
				mNoPromotionAvailableMessageTextView.setVisibility(View.GONE);
			}
		}
	};

	private final Observer<Boolean> claimOfferObserver = new Observer<Boolean>() {
		@Override
		public void onChanged(@Nullable Boolean isOfferClaimed) {
			if (isOfferClaimed != null && getActivity() != null) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity()).setCancelable(false);
				alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						promotionsRefreshLayout.setRefreshing(true);
						mPromotionsViewModel.fetchPromotionsData();
					}
				});
				if (isOfferClaimed) {
					alertDialogBuilder.setTitle(R.string.intercom_congratulations);
					alertDialogBuilder.setMessage(R.string.receive_offer_from_merchant_message);
				} else {
					alertDialogBuilder.setTitle(R.string.sorry);
					alertDialogBuilder.setMessage(R.string.offer_redemption_failed);
				}
				alertDialogBuilder.show();
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_promotions, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!mPromotionsViewModel.mPromotionListMutableLiveData.hasActiveObservers()) {
			mPromotionsViewModel.mPromotionListMutableLiveData.removeObservers(this);
			mPromotionsViewModel.mPromotionListMutableLiveData
					.observe(this, promotionListObserver);
		}
		if (!mPromotionsViewModel.offerClaimLiveData.hasActiveObservers()) {
			mPromotionsViewModel.offerClaimLiveData.removeObservers(this);
			mPromotionsViewModel.offerClaimLiveData
					.observe(this, claimOfferObserver);
		}
		mPromotionsViewModel.fetchPromotionsData();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final RecyclerView recyclerView = view.findViewById(R.id.promotions_recycler_view);
		promotionsRefreshLayout = view.findViewById(R.id.promotions_refresh_layout);
		mNoPromotionAvailableMessageTextView = view.findViewById(R.id.no_promotion_available_message_text_view);

		recyclerView.setHasFixedSize(false);
		promotionAdapter = new PromotionAdapter(getContext(), new PromotionViewHolder.OnOfferActionsListener() {

			@Override
			public void onClaimAction(int promotionPosition) {
				final Promotion promotion = mPromotionsViewModel.getPromotion(promotionPosition);
				if (getContext() != null && promotion != null) {
					if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
						mPromotionsViewModel.setClaimedPromotion(promotion);
						Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
						startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
					} else {
						mPromotionsViewModel.setClaimedPromotion(promotion);
						requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
					}
				}
			}

			@Override
			public void onTermAction(int promotionPosition) {
				final Promotion promotion = mPromotionsViewModel.getPromotion(promotionPosition);
				if (promotion == null)
					return;

				@SuppressLint("InflateParams") final View customView = LayoutInflater.from(getContext()).inflate(R.layout.layout_promotion_terms, null, false);
				if (customView == null)
					return;
				final TextView promotionTitleTextView = customView.findViewById(R.id.promotion_title_text_view);
				final TextView promotionSubDetailsTextView = customView.findViewById(R.id.promotion_sub_details_text_view);
				final TextView startDateTextView = customView.findViewById(R.id.start_date_text_view);
				final TextView endDateTextView = customView.findViewById(R.id.end_date_text_view);
				final TextView termsTextView = customView.findViewById(R.id.terms_text_view);

				promotionTitleTextView.setText(promotion.getCampaignTitle());
				promotionSubDetailsTextView.setText(promotion.getPromotionDetails());
				PromotionMetaData promotionMetaData = promotion.getMedata(PromotionMetaData.class);
				if (promotionMetaData.getStartDate() != null) {
					startDateTextView.setVisibility(View.VISIBLE);
					startDateTextView.setText(outputDateFormat.format(promotionMetaData.getStartDate()));
				} else {
					startDateTextView.setVisibility(View.GONE);
				}
				if (promotionMetaData.getEndDate() != null) {
					endDateTextView.setVisibility(View.VISIBLE);
					endDateTextView.setText(outputDateFormat.format(promotionMetaData.getEndDate()));
				} else {
					endDateTextView.setVisibility(View.GONE);
				}
				if (promotion.getTerms() != null) {
					termsTextView.setVisibility(View.VISIBLE);
					termsTextView.setText(promotion.getTerms());
				} else {
					termsTextView.setVisibility(View.GONE);
				}

				if (getActivity() != null) {
					AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
							.setView(customView)
							.setPositiveButton(android.R.string.ok, null)
							.create();
					alertDialog.show();
				}
			}
		});
		recyclerView.setAdapter(promotionAdapter);


		if (mPromotionsViewModel.mPromotionListMutableLiveData.getValue() != null) {
			List<Promotion> promotions = mPromotionsViewModel.mPromotionListMutableLiveData.getValue();
			promotionAdapter.setItem(promotions);
			promotionAdapter.notifyDataSetChanged();
			setContentShown(true);
			promotionsRefreshLayout.setRefreshing(false);
			if (promotions == null || promotions.size() == 0) {
				mNoPromotionAvailableMessageTextView.setVisibility(View.VISIBLE);
			} else {
				mNoPromotionAvailableMessageTextView.setVisibility(View.GONE);
			}
		} else {
			setContentShown(false);
		}
		promotionsRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (mPromotionsViewModel.isFetchingData()) {
					promotionsRefreshLayout.setRefreshing(false);
				} else {
					mPromotionsViewModel.fetchPromotionsData();
				}
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case CAMERA_PERMISSION_REQUEST_CODE:
				switch (grantResults[0]) {
					case PackageManager.PERMISSION_GRANTED:
						Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
						startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
						break;
					case PackageManager.PERMISSION_DENIED:
						mPromotionsViewModel.setClaimedPromotion(null);
						Toaster.makeText(getContext(), R.string.error_camera_permission_denied, Toast.LENGTH_SHORT);
						break;
				}
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constants.RC_BARCODE_CAPTURE:
				switch (resultCode) {
					case CommonStatusCodes.SUCCESS:
						if (data != null) {
							if (data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject) instanceof Barcode)
								mPromotionsViewModel.processClaimedPromotionViaQRScan((Barcode) data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject));
						} else {
							mPromotionsViewModel.setClaimedPromotion(null);
						}
						break;
					default:
						mPromotionsViewModel.setClaimedPromotion(null);
				}
				break;
			default:
				mPromotionsViewModel.setClaimedPromotion(null);
				break;
		}

	}

	@Override
	public void showDialog() {
		progressDialog.showDialog();
	}

	@Override
	public void dismissDialog() {
		progressDialog.dismissDialog();
	}

	@Override
	public void setLoadingMessage(String message) {
		progressDialog.setLoadingMessage(message);
	}

	@Override
	public void showSuccessAnimationAndMessage(String successMessage) {
		progressDialog.showSuccessAnimationAndMessage(successMessage);
	}

	@Override
	public void showFailureAnimationAndMessage(String failureMessage) {
		progressDialog.showSuccessAnimationAndMessage(failureMessage);
	}
}
