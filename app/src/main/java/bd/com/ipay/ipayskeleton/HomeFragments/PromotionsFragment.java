package bd.com.ipay.ipayskeleton.HomeFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.Model.Promotion.Promotion;
import bd.com.ipay.ipayskeleton.QRScanner.BarcodeCaptureActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
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
        mPromotionsViewModel = ViewModelProviders.of(this).get(PromotionsViewModel.class);
        mPromotionsViewModel.progressDialogListener = this;
        mPromotionsViewModel.mPromotionListMutableLiveData.observe(this, new Observer<List<Promotion>>() {
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
        });

        mPromotionsViewModel.offerClaimLiveData.observe(this, new Observer<Boolean>() {
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
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_promotions, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPromotionsViewModel.fetchPromotionsData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = view.findViewById(R.id.promotions_recycler_view);
        promotionsRefreshLayout = view.findViewById(R.id.promotions_refresh_layout);
        mNoPromotionAvailableMessageTextView = view.findViewById(R.id.no_promotion_available_message_text_view);

        promotionAdapter = new PromotionAdapter(getContext(), new OnOfferActionsListener() {

            @Override
            public void onClaimAction(Promotion promotion) {
                if (getContext() != null) {
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
            public void onTermAction(Promotion promotion) {
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
                if (promotion.getStartDate() != null) {
                    startDateTextView.setVisibility(View.VISIBLE);
                    startDateTextView.setText(outputDateFormat.format(promotion.getStartDate()));
                } else {
                    startDateTextView.setVisibility(View.GONE);
                }
                if (promotion.getEndDate() != null) {
                    endDateTextView.setVisibility(View.VISIBLE);
                    endDateTextView.setText(outputDateFormat.format(promotion.getEndDate()));
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
                            .setPositiveButton(android.R.string.cancel, null)
                            .create();
                    alertDialog.show();
                }
            }
        });
        recyclerView.setAdapter(promotionAdapter);

        setContentShown(false);
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
                            Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                            final String result = barcode.displayValue;
                            String[] stringArray = result.split("-");
                            Long outletId = null;
                            if (stringArray.length > 1) {
                                try {
                                    outletId = Long.parseLong(stringArray[1].trim().replaceAll("[^0-9]", ""));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mPromotionsViewModel.claimPromotion(outletId);
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

    public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {

        private LayoutInflater layoutInflater;
        private List<Promotion> mPromotionList;
        private OnOfferActionsListener mOnOfferActionsListener;

        PromotionAdapter(final Context context, final OnOfferActionsListener onOfferActionsListener) {
            layoutInflater = LayoutInflater.from(context);
            this.mOnOfferActionsListener = onOfferActionsListener;
        }

        @NonNull
        @Override
        public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PromotionViewHolder(layoutInflater.inflate(R.layout.list_item_promotion, parent, false), mOnOfferActionsListener);
        }

        @Override
        public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
            Promotion promotion = mPromotionList.get(position);
            holder.promotionTitleTextView.setText(promotion.getCampaignTitle());
            holder.promotionSubDetailsTextView.setText(promotion.getPromotionDetails());
            Glide.with(getActivity()).load(Constants.BASE_URL_FTP_SERVER + promotion.getImageUrl()).into(holder.promotionImageView);
            holder.totalTransactionCountBar.setNumStars(promotion.getTransactionCountPerRedeem());
            holder.totalTransactionCountBar.setRating(promotion.getTransactionCountPerRedeem() - promotion.getTransactionRequiredForNextRedeem());
            holder.totalTransactionCountBar.setStepSize(1);
            holder.offerRedeemCountTextView.setText(promotion.getConsumptionDetails());
            final Date currentDate = Calendar.getInstance().getTime();
            if (promotion.isActive() &&
                    (promotion.getStartDate().before(currentDate) && promotion.getEndDate().after(currentDate)) &&
                    promotion.getRedeemAvailable() > 0) {
                holder.availableRedeemCountTextView.setVisibility(View.VISIBLE);
                holder.totalTransactionCountBar.setVisibility(View.GONE);
                holder.availableRedeemCountTextView.setText(getString(R.string.you_have_already_redeemed_this_offer_times, promotion.getRedeemAvailable()));
                holder.claimButton.setVisibility(View.VISIBLE);
            } else {
                holder.availableRedeemCountTextView.setVisibility(View.GONE);
                holder.totalTransactionCountBar.setVisibility(View.VISIBLE);
                holder.claimButton.setVisibility(View.GONE);
            }
        }

        public void setItem(List<Promotion> promotionList) {
            this.mPromotionList = promotionList;
        }

        @Override
        public int getItemCount() {
            if (mPromotionList == null)
                return 0;
            else
                return mPromotionList.size();
        }

        class PromotionViewHolder extends RecyclerView.ViewHolder {

            private TextView promotionTitleTextView;
            private TextView promotionSubDetailsTextView;
            private TextView availableRedeemCountTextView;
            private TextView offerRedeemCountTextView;
            private ImageView promotionImageView;
            private RatingBar totalTransactionCountBar;
            private Button claimButton;
            private ImageButton termsButton;
            private OnOfferActionsListener mOnOfferActionsListener;

            PromotionViewHolder(View itemView, final OnOfferActionsListener onOfferActionsListener) {
                super(itemView);
                this.mOnOfferActionsListener = onOfferActionsListener;
                promotionTitleTextView = itemView.findViewById(R.id.promotion_title_text_view);
                promotionSubDetailsTextView = itemView.findViewById(R.id.promotion_sub_details_text_view);
                availableRedeemCountTextView = itemView.findViewById(R.id.available_redeem_count_text_view);
                totalTransactionCountBar = itemView.findViewById(R.id.total_transaction_count_bar);
                promotionImageView = itemView.findViewById(R.id.promotion_image_view);
                offerRedeemCountTextView = itemView.findViewById(R.id.offer_redeem_count_text_view);
                claimButton = itemView.findViewById(R.id.claim_button);
                termsButton = itemView.findViewById(R.id.terms_button);

                termsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnOfferActionsListener.onTermAction(mPromotionList.get(getAdapterPosition()));
                    }
                });
                claimButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnOfferActionsListener.onClaimAction(mPromotionList.get(getAdapterPosition()));
                    }
                });
            }
        }
    }

    public interface OnOfferActionsListener {
        void onClaimAction(Promotion promotion);

        void onTermAction(Promotion promotion);
    }
}
