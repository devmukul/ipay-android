package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.MerchantDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.AllowablePackage;
import bd.com.ipay.ipayskeleton.R;

public class BanglalionPackageSelectorDialog extends AlertDialog {
    private Context context;
    private RecyclerView packageListRecyclerView;
    private MerchantBranchAdapter mPackageAdapter;
    private List<AllowablePackage> allowablePackages ;
    private OnResourceSelectedListener onResourceSelectedListener;

    public BanglalionPackageSelectorDialog(@NonNull Context context, List<AllowablePackage> allowablePackages) {
        super(context);
        this.context = context;
        this.allowablePackages = allowablePackages;
        initializeViews();
    }

    private void initializeViews() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_banglalion_packages, null, false);
        packageListRecyclerView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        mPackageAdapter = new MerchantBranchAdapter();
        packageListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        packageListRecyclerView.setAdapter(mPackageAdapter);
        this.setView(view);
    }

    public void showDialog() {
        this.show();
    }

    public void setOnResourceSelectedListener(OnResourceSelectedListener onResourceSelectedListener) {
        this.onResourceSelectedListener = onResourceSelectedListener;
    }

    public interface OnResourceSelectedListener {
        void onResourceSelected(AllowablePackage allowablePackage);
    }

    public class MerchantBranchAdapter extends RecyclerView.Adapter<MerchantBranchAdapter.MerchantBranchAddressViewHolder> {

        @Override
        public MerchantBranchAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MerchantBranchAddressViewHolder
                    (LayoutInflater.from(context).inflate(R.layout.list_item_banglalion_package, parent, false));
        }

        @Override
        public void onBindViewHolder(final MerchantBranchAddressViewHolder holder, final int position) {
            holder.mPackageNameTextView.setText(allowablePackages.get(position).getPackageName());
            holder.mPackageDetailsTextView.setText(allowablePackages.get(position).getAmount().toString()+" Tk, "+allowablePackages.get(position).getValidity());
            holder.addressRadioButton.setChecked(false);
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.addressRadioButton.setChecked(true);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchToMakePaymentActivity(holder, position);
                        }
                    }, 500);

                }
            });
            holder.addressRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.addressRadioButton.setChecked(true);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchToMakePaymentActivity(holder, position);
                        }
                    }, 500);

                }
            });
        }

        private void switchToMakePaymentActivity(MerchantBranchAddressViewHolder holder, int position) {

            AllowablePackage allowablePackage = allowablePackages.get(position);

            if (onResourceSelectedListener != null)
                onResourceSelectedListener.onResourceSelected(allowablePackage);
            BanglalionPackageSelectorDialog.this.dismiss();
        }

        @Override
        public int getItemCount() {
            return allowablePackages.size();
        }


        public class MerchantBranchAddressViewHolder extends RecyclerView.ViewHolder {
            private RadioButton addressRadioButton;
            private TextView mPackageNameTextView;
            private TextView mPackageDetailsTextView;
            private View mainView;

            public MerchantBranchAddressViewHolder(View itemView) {
                super(itemView);
                addressRadioButton = (RadioButton) itemView.findViewById(R.id.package_radio_button);
                mPackageNameTextView = (TextView) itemView.findViewById(R.id.package_name);
                mPackageDetailsTextView = (TextView) itemView.findViewById(R.id.package_details);
                mainView = itemView;
            }
        }
    }
}
