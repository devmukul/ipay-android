package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.TrendingBusinessOutletSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.BusinessList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

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
                                    } else {
                                        Intent intent = new Intent(context, PaymentActivity.class);
                                        intent.putExtra(Constants.NAME, merchantDetails.getMerchantName());
                                        intent.putExtra(Constants.ADDRESS, merchantDetails.getOutlets().get(0).getAddressString());
                                        intent.putExtra(Constants.DISTRICT, merchantDetails.getOutlets().get(0).getOutletAddress().getDistrictName());
                                        intent.putExtra(Constants.THANA, merchantDetails.getOutlets().get(0).getOutletAddress().getThanaName());
                                        intent.putExtra(Constants.OUTLET_NAME, merchantDetails.getOutlets().get(0).getOutletName());
                                        intent.putExtra(Constants.OUTLET_ID, merchantDetails.getOutlets().get(0).getOutletId());
                                        intent.putExtra(Constants.MOBILE_NUMBER, merchantDetails.getMerchantMobileNumber());
                                        intent.putExtra(Constants.PHOTO_URI, merchantDetails.getBusinessLogo());
                                        intent.putExtra(Constants.FROM_BRANCHING, true);
                                        context.startActivity(intent);
                                    }
                                } else {
                                    Intent intent = new Intent(context, PaymentActivity.class);
                                    intent.putExtra(Constants.NAME, merchantDetails.getMerchantName());
                                    intent.putExtra(Constants.ADDRESS, merchantDetails.getAddressString());
                                    intent.putExtra(Constants.DISTRICT, merchantDetails.getDistrictString());
                                    intent.putExtra(Constants.THANA, merchantDetails.getThanaString());
                                    intent.putExtra(Constants.MOBILE_NUMBER, merchantDetails.getMerchantMobileNumber());
                                    intent.putExtra(Constants.PHOTO_URI, merchantDetails.getBusinessLogo());
                                    intent.putExtra(Constants.FROM_BRANCHING, true);
                                    context.startActivity(intent);
                                }
                            }
                        });
                        payByQCPinChecker.execute();
                    }
                }
            });
        }
    }

    class NormalViewHolder extends PayDashBoardItemAdapter.ViewHolder {
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
        return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dashboard_item, parent, false));
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


