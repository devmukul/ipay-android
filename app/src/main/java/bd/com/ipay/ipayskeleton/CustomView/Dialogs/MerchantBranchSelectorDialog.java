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

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.MerchantDetails;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MerchantBranchSelectorDialog extends AlertDialog {
    private Context context;
    private MerchantDetails merchantDetails;
    private ProfileImageView mMerchantLogoView;
    private TextView merchantNameTextView;
    private RecyclerView merchantAddressListRecyclerView;
    private MerchantBranchAdapter mMerchantBranchAdapter;


    public MerchantBranchSelectorDialog(@NonNull Context context, MerchantDetails merchantDetails) {
        super(context);
        this.context = context;
        this.merchantDetails = merchantDetails;
        initializeViews();
    }

    private void initializeViews() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_merchant_branches, null, false);
        merchantAddressListRecyclerView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        merchantNameTextView = (TextView) view.findViewById(R.id.merchant_name);
        mMerchantLogoView = (ProfileImageView) view.findViewById(R.id.merchant_logo);
        supportViewsWithData();
        mMerchantBranchAdapter = new MerchantBranchAdapter();
        merchantAddressListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        merchantAddressListRecyclerView.setAdapter(mMerchantBranchAdapter);
        this.setView(view);
        this.setCanceledOnTouchOutside(false);
    }

    public void showDialog() {
        this.show();
    }

    private void supportViewsWithData() {
        this.mMerchantLogoView.setProfilePicture(merchantDetails.getBusinessLogo(), false);
        this.merchantNameTextView.setText(merchantDetails.getMerchantName());
    }

    public class MerchantBranchAdapter extends RecyclerView.Adapter<MerchantBranchAdapter.MerchantBranchAddressViewHolder> {

        @Override
        public MerchantBranchAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MerchantBranchAddressViewHolder
                    (LayoutInflater.from(context).inflate(R.layout.list_item_address, parent, false));
        }

        @Override
        public void onBindViewHolder(MerchantBranchAddressViewHolder holder, final int position) {
            holder.addressRadioButton.setText(merchantDetails.getBranches().get(position).getBranchAddress());
            holder.addressRadioButton.setSelected(false);
            holder.addressRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PaymentActivity.class);
                    intent.putExtra(Constants.NAME, merchantDetails.getMerchantName());
                    intent.putExtra(Constants.ADDRESS, merchantDetails.getBranches().get(position).getBranchAddress());
                    intent.putExtra(Constants.MOBILE_NUMBER, merchantDetails.getBranches().get(position).getMobileNumber());
                    intent.putExtra(Constants.PHOTO_URI, merchantDetails.getBusinessLogo());
                    intent.putExtra(Constants.FROM_BRANCHING, true);
                    context.startActivity(intent);
                    MerchantBranchSelectorDialog.this.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return merchantDetails.getBranches().size();
        }

        public class MerchantBranchAddressViewHolder extends RecyclerView.ViewHolder {
            private RadioButton addressRadioButton;

            public MerchantBranchAddressViewHolder(View itemView) {
                super(itemView);
                addressRadioButton = (RadioButton) itemView.findViewById(R.id.address_radio_button);
            }
        }
    }
}
