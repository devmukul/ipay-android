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
    }

    public void showDialog() {
        this.show();
    }

    private void supportViewsWithData() {
        this.mMerchantLogoView.setBusinessProfilePicture(merchantDetails.getBusinessLogo(), false);
        this.merchantNameTextView.setText(merchantDetails.getMerchantName());
    }

    public class MerchantBranchAdapter extends RecyclerView.Adapter<MerchantBranchAdapter.MerchantBranchAddressViewHolder> {

        @Override
        public MerchantBranchAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MerchantBranchAddressViewHolder
                    (LayoutInflater.from(context).inflate(R.layout.list_item_address, parent, false));
        }

        @Override
        public void onBindViewHolder(final MerchantBranchAddressViewHolder holder, final int position) {
            holder.mAddressLineOneTextView.setText(merchantDetails.getBranches().get(position).getBranchAddress1());
            holder.mAddressLineTwoTextView.setText(merchantDetails.getBranches().get(position).getBranchAddress2());
            holder.addressRadioButton.setChecked(false);
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchToMakePaymentActivity(holder, position);
                }
            });
            holder.addressRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchToMakePaymentActivity(holder, position);
                }
            });
        }

        private void switchToMakePaymentActivity(MerchantBranchAddressViewHolder holder, int position) {
            holder.addressRadioButton.setChecked(true);
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra(Constants.NAME, merchantDetails.getMerchantName());
            intent.putExtra(Constants.ADDRESS_ONE, merchantDetails.getBranches().get(position).getBranchAddress1());
            intent.putExtra(Constants.ADDRESS_TWO, merchantDetails.getBranches().get(position).getBranchAddress2());
            intent.putExtra(Constants.MOBILE_NUMBER, merchantDetails.getBranches().get(position).getMobileNumber());
            intent.putExtra(Constants.PHOTO_URI, merchantDetails.getBusinessLogo());
            intent.putExtra(Constants.FROM_BRANCHING, true);
            context.startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return merchantDetails.getBranches().size();
        }

        public class MerchantBranchAddressViewHolder extends RecyclerView.ViewHolder {
            private RadioButton addressRadioButton;
            private TextView mAddressLineOneTextView;
            private TextView mAddressLineTwoTextView;
            private View mainView;

            public MerchantBranchAddressViewHolder(View itemView) {
                super(itemView);
                addressRadioButton = (RadioButton) itemView.findViewById(R.id.address_radio_button);
                mAddressLineOneTextView = (TextView) itemView.findViewById(R.id.address_line_1);
                mAddressLineTwoTextView = (TextView) itemView.findViewById(R.id.address_line_2);
                mainView = itemView;
            }
        }
    }
}
