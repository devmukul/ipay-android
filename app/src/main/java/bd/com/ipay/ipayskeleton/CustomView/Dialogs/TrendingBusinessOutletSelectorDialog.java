package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.Outlets;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.BusinessList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.MerchantDetails;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TrendingBusinessOutletSelectorDialog extends AlertDialog {
    private Context context;
    private BusinessList merchantDetails;
    private ProfileImageView mMerchantLogoView;
    private TextView merchantNameTextView;
    private RecyclerView merchantAddressListRecyclerView;
    private MerchantOutletAdapter mMerchantBranchAdapter;
    List<Outlets> outlets;
    private String name;
    private String photoUrl;


    public TrendingBusinessOutletSelectorDialog(@NonNull Context context, BusinessList merchantDetails) {
        super(context);
        this.context = context;
        this.merchantDetails = merchantDetails;
        outlets = merchantDetails.getOutlets();
        initializeViews();
    }

    private void initializeViews() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_merchant_branches, null, false);
        merchantAddressListRecyclerView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        merchantNameTextView = (TextView) view.findViewById(R.id.merchant_name);
        mMerchantLogoView = (ProfileImageView) view.findViewById(R.id.merchant_logo);
        supportViewsWithData();
        mMerchantBranchAdapter = new MerchantOutletAdapter();
        merchantAddressListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        merchantAddressListRecyclerView.setAdapter(mMerchantBranchAdapter);
        this.setView(view);
    }

    public void showDialog() {
        this.show();
    }

    private void supportViewsWithData() {
        this.mMerchantLogoView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + merchantDetails.getBusinessLogo(), false);
        this.merchantNameTextView.setText(merchantDetails.getMerchantName());
    }

    public class MerchantOutletAdapter extends RecyclerView.Adapter<MerchantOutletAdapter.MerchantBranchAddressViewHolder> {

        @Override
        public MerchantBranchAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MerchantBranchAddressViewHolder
                    (LayoutInflater.from(context).inflate(R.layout.list_item_outlets, parent, false));
        }

        @Override
        public void onBindViewHolder(final MerchantBranchAddressViewHolder holder, final int position) {
            holder.mNameTextView.setText(merchantDetails.getOutlets().get(position).getOutletName());
            holder.mAddressTextView.setText(merchantDetails.getOutlets().get(position).getAddressString());
            holder.outletIcon.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + merchantDetails.getOutlets().get(position).getOutletLogoUrl(), false);

            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchToMakePaymentActivity(holder, position);

                }
            });
            holder.outletIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchToMakePaymentActivity(holder, position);
                }
            });
        }

        private void switchToMakePaymentActivity(MerchantBranchAddressViewHolder holder, int position) {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra(Constants.NAME, merchantDetails.getMerchantName());
            intent.putExtra(Constants.ADDRESS_ONE, merchantDetails.getAddressList().getOFFICE().get(0).getAddressLine1());
            intent.putExtra(Constants.ADDRESS_TWO, merchantDetails.getAddressList().getOFFICE().get(0).getAddressLine2());
            intent.putExtra(Constants.MOBILE_NUMBER, merchantDetails.getMerchantMobileNumber());
            intent.putExtra(Constants.PHOTO_URI, merchantDetails.getBusinessLogo());
            intent.putExtra(Constants.OUTLET_ID, outlets.get(position).getOutletId());
            intent.putExtra(Constants.FROM_BRANCHING, true);
            context.startActivity(intent);
            TrendingBusinessOutletSelectorDialog.this.dismiss();
        }

        @Override
        public int getItemCount() {
            return merchantDetails.getOutlets().size();
        }

        public class MerchantBranchAddressViewHolder extends RecyclerView.ViewHolder {
            private ProfileImageView outletIcon;
            private TextView mNameTextView;
            private TextView mAddressTextView;
            private View mainView;

            public MerchantBranchAddressViewHolder(View itemView) {
                super(itemView);
                outletIcon = (ProfileImageView) itemView.findViewById(R.id.outlet_radio_button);
                mNameTextView = (TextView) itemView.findViewById(R.id.outlet_name);
                mAddressTextView = (TextView) itemView.findViewById(R.id.outlet_address);
                outletIcon.setBusinessLogoPlaceHolder();
                mainView = itemView;
            }
        }
    }
}
