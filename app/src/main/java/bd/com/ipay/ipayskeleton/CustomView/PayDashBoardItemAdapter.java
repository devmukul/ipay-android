package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;


public class PayDashBoardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BusinessAccountEntry> mBusinessAccountEntryList;
    Context context;

    public PayDashBoardItemAdapter(List<BusinessAccountEntry> mBusinessAccountEntryList, Context context) {
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

        public void bindView(int pos) {
            final BusinessAccountEntry businessAccountEntry = mBusinessAccountEntryList.get(pos);
            final String name = businessAccountEntry.getBusinessName();
            final String imageUrl = Constants.BASE_URL_FTP_SERVER + businessAccountEntry.getProfilePictureUrl();
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
//                        PinChecker pinChecker = new PinChecker(context, new PinChecker.PinCheckerListener() {
//                            @Override
//                            public void ifPinAdded() {
//                                Intent intent;
//                                intent = new Intent(context, PaymentActivity.class);
//                                intent.putExtra(Constants.MOBILE_NUMBER, businessAccountEntry.getMobileNumber());
//                                intent.putExtra(Constants.NAME, businessAccountEntry.getBusinessName());
//                                intent.putExtra(Constants.PHOTO_URI, businessAccountEntry.getProfilePictureUrl());
//                                context.startActivity(intent);
//                            }
//                        });
//                        pinChecker.execute();

                        PinChecker payByQCPinChecker = new PinChecker(context, new PinChecker.PinCheckerListener() {
                            @Override
                            public void ifPinAdded() {
                                Intent intent;
                                intent = new Intent(context, QRCodePaymentActivity.class);
                                context.startActivity(intent);
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


