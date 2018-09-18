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

import bd.com.ipay.ipayskeleton.R;

public class LankaBanglaAmountSelectorDialog extends AlertDialog {
    private Context context;
    private RecyclerView amountTypeRecyclerView;
    private MerchantBranchAdapter mPackageAdapter;
    private List<String> mAmountTypes;
    private OnResourceSelectedListener onResourceSelectedListener;

    public LankaBanglaAmountSelectorDialog(@NonNull Context context, List<String> amountTypes) {
        super(context);
        this.context = context;
        this.mAmountTypes = amountTypes;
        initializeViews();
    }

    private void initializeViews() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_banglalion_packages, null, false);
        amountTypeRecyclerView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        mPackageAdapter = new MerchantBranchAdapter();
        amountTypeRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        amountTypeRecyclerView.setAdapter(mPackageAdapter);
        this.setView(view);
    }

    public void showDialog() {
        this.show();
    }

    public void setOnResourceSelectedListener(OnResourceSelectedListener onResourceSelectedListener) {
        this.onResourceSelectedListener = onResourceSelectedListener;
    }

    public interface OnResourceSelectedListener {
        void onResourceSelected(String amountType);
    }

    public class MerchantBranchAdapter extends RecyclerView.Adapter<MerchantBranchAdapter.MerchantBranchAddressViewHolder> {

        @Override
        public MerchantBranchAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MerchantBranchAddressViewHolder
                    (LayoutInflater.from(context).inflate(R.layout.list_item_lanka_bangla_amount_type, parent, false));
        }

        @Override
        public void onBindViewHolder(final MerchantBranchAddressViewHolder holder, final int position) {
            holder.mAmountTypeName.setText(mAmountTypes.get(position));
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mAmountTypeRadioButton.setChecked(true);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LankaBanglaAmountSelectorDialog.this.
                                    onResourceSelectedListener.onResourceSelected(mAmountTypes.get(position));
                        }
                    }, 500);
                }
            });
            holder.mAmountTypeRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mAmountTypeRadioButton.setChecked(true);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LankaBanglaAmountSelectorDialog.this.
                                    onResourceSelectedListener.onResourceSelected(mAmountTypes.get(position));
                        }
                    }, 500);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAmountTypes.size();
        }


        public class MerchantBranchAddressViewHolder extends RecyclerView.ViewHolder {
            private RadioButton mAmountTypeRadioButton;
            private TextView mAmountTypeName;
            private View mainView;

            public MerchantBranchAddressViewHolder(View itemView) {
                super(itemView);
                mAmountTypeName = (TextView) itemView.findViewById(R.id.amount_type_name);
                mAmountTypeRadioButton = (RadioButton) itemView.findViewById(R.id.amount_type_radio_button);
                mainView = itemView;
            }
        }
    }
}
