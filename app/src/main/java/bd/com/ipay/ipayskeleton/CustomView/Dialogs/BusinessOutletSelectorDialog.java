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
import bd.com.ipay.ipayskeleton.Model.BusinessContact.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.Outlets;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.AllowablePackage;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessOutletSelectorDialog extends AlertDialog {
    private Context context;
    //private BusinessContact merchantDetails;
    private ProfileImageView mMerchantLogoView;
    private TextView merchantNameTextView;
    private RecyclerView merchantAddressListRecyclerView;
    private MerchantOutletAdapter mMerchantBranchAdapter;
    List<Outlets> outlets;
    private OnResourceSelectedListener onResourceSelectedListener;
    private String name;
    private String photoUrl;

    public BusinessOutletSelectorDialog(@NonNull Context context, String name , String photoUrl, List<Outlets> outlets) {
        super(context);
        this.context = context;
        this.name = name;
        this.photoUrl = photoUrl;
        this.outlets = outlets;
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
        this.mMerchantLogoView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + photoUrl, false);
        this.merchantNameTextView.setText(name);
    }

    public void setOnResourceSelectedListener(OnResourceSelectedListener onResourceSelectedListener) {
        this.onResourceSelectedListener = onResourceSelectedListener;
    }

    public interface OnResourceSelectedListener {
        void onResourceSelected(Outlets allowablePackage);
    }

    public class MerchantOutletAdapter extends RecyclerView.Adapter<MerchantOutletAdapter.MerchantOutletAddressViewHolder> {


        @Override
        public MerchantOutletAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MerchantOutletAddressViewHolder
                    (LayoutInflater.from(context).inflate(R.layout.list_item_outlets, parent, false));
        }

        @Override
        public void onBindViewHolder(final MerchantOutletAddressViewHolder holder, final int position) {
            holder.mNameTextView.setText(outlets.get(position).getOutletName());
            holder.mAddressTextView.setText(outlets.get(position).getAddressString());
            holder.outletIcon.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + outlets.get(position).getOutletLogoUrl(), false);

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

        private void switchToMakePaymentActivity(MerchantOutletAddressViewHolder holder, int position) {
//            Intent intent = new Intent(context, PaymentActivity.class);
//            intent.putExtra(Constants.NAME, outlets.get(position).getOutletName());
//            intent.putExtra(Constants.OUTLET_ID, outlets.get(position).getOutletId());
//            intent.putExtra(Constants.FROM_BRANCHING, true);
//            context.startActivity(intent);
//            BusinessOutletSelectorDialog.this.dismiss();

            Outlets outletsData = outlets.get(position);

            if (onResourceSelectedListener != null)
                onResourceSelectedListener.onResourceSelected(outletsData);
            BusinessOutletSelectorDialog.this.dismiss();
        }

        @Override
        public int getItemCount() {
            return outlets.size();
        }

        public class MerchantOutletAddressViewHolder extends RecyclerView.ViewHolder {
            private ProfileImageView outletIcon;
            private TextView mNameTextView;
            private TextView mAddressTextView;
            private View mainView;

            public MerchantOutletAddressViewHolder(View itemView) {
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
