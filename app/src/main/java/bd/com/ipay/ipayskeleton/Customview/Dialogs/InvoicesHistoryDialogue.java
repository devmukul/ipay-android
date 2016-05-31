package bd.com.ipay.ipayskeleton.Customview.Dialogs;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvoicesHistoryDialogue extends MaterialDialog.Builder {

    private RecyclerView mReviewRecyclerView;
    private InvoiceReviewAdapter invoiceReviewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ItemList mItemList[];
    public BigDecimal mAmount;
    private BigDecimal mNetAmount;
    private BigDecimal mVat;

    private String mDescription;
    private String mTime;
    private  long id;
    private int status;
    private Context context;
    private String mTitle;

    public InvoicesHistoryDialogue(Context context,String title, String description, String time, long id, BigDecimal amount, BigDecimal vat, ItemList[] itemList, int status) {
        super(context);

        this.mVat = vat;
        this.mAmount = amount;
        this.mItemList = itemList;
        this.mTitle = title;
        this.mDescription = description;
        this.mTime = time;
        this.id = id;
        this.status = status;
        this.context = context;

        initializeView();
    }

    public void initializeView() {

        MaterialDialog dialog = new MaterialDialog.Builder(this.getContext())
                .title(R.string.invoice_details)
                .customView(R.layout.dialog_make_payment_notification_review, true)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        View v = dialog.getCustomView();
        mReviewRecyclerView = (RecyclerView) v.findViewById(R.id.list_invoice);
        invoiceReviewAdapter = new InvoiceReviewAdapter();
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mReviewRecyclerView.setLayoutManager(mLayoutManager);
        mReviewRecyclerView.setAdapter(invoiceReviewAdapter);
    }

private class InvoiceReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int INVOICE_DETAILS_LIST_ITEM_VIEW = 1;
        private static final int INVOICE_DETAILS_LIST_HEADER_VIEW = 2;
        private static final int INVOICE_DETAILS_LIST_FOOTER_VIEW = 3;

        public InvoiceReviewAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            final TextView titleTextView;
            final TextView descriptionTextView;
            final TextView timeTextView;
            final TextView invoiceIDTextView;

            private TextView mItemNameView;
            private TextView mQuantityView;
            private TextView mAmountView;


            private TextView mNetAmountView;
            private TextView mVatView;
            private TextView mTotalView;
            private TextView mStatusView;

            public ViewHolder(final View itemView) {
                super(itemView);

                titleTextView = (TextView) itemView.findViewById(R.id.title);
                descriptionTextView = (TextView) itemView.findViewById(R.id.description);
                timeTextView = (TextView) itemView.findViewById(R.id.time);
                invoiceIDTextView = (TextView) itemView.findViewById(R.id.invoice_id);

                mItemNameView = (TextView) itemView.findViewById(R.id.textview_item);
                mQuantityView = (TextView) itemView.findViewById(R.id.textview_quantity);
                mAmountView = (TextView) itemView.findViewById(R.id.textview_amount);

                mNetAmountView = (TextView) itemView.findViewById(R.id.textview_net_amount);
                mVatView = (TextView) itemView.findViewById(R.id.textview_vat);
                mTotalView = (TextView) itemView.findViewById(R.id.textview_total);
                mStatusView = (TextView) itemView.findViewById(R.id.status);

            }

            public void bindViewForListItem(int pos) {
                // Decrease pos by 1 as there is a header view now.
                pos = pos - 1;

                mItemNameView.setText(mItemList[pos].getItem());
                mQuantityView.setText(Utilities.formatTaka(mItemList[pos].getQuantity()));
                mAmountView.setText(Utilities.formatTaka(mItemList[pos].getAmount()));
            }

            public void bindViewForHeader(int pos) {
                titleTextView.setText(mTitle);
                descriptionTextView.setText(mDescription);
                timeTextView.setText(mTime);
                invoiceIDTextView.setText(context.getString(R.string.invoice_id) + " " + String.valueOf(id));
            }

            public void bindViewForFooter(int pos) {
                mNetAmount = mAmount.subtract(mVat);
                mNetAmountView.setText(Utilities.formatTaka(mNetAmount));
                mVatView.setText(Utilities.formatTaka(mVat));
                mTotalView.setText(Utilities.formatTaka(mAmount));
                if (status == Constants.INVOICE_STATUS_ACCEPTED) {
                    mStatusView.setText(context.getString(R.string.transaction_successful));
                    mStatusView.setTextColor(context.getResources().getColor(R.color.bottle_green));

                } else if (status == Constants.INVOICE_STATUS_PROCESSING) {
                    mStatusView.setText(context.getString(R.string.in_progress));
                    mStatusView.setTextColor(context.getResources().getColor(R.color.background_yellow));

                } else if (status == Constants.INVOICE_STATUS_REJECTED) {
                    mStatusView.setText(context.getString(R.string.transaction_failed));
                    mStatusView.setTextColor(context.getResources().getColor(R.color.background_red));
                } else if (status == Constants.INVOICE_STATUS_CANCELED) {
                    mStatusView.setText(context.getString(R.string.transaction_failed));
                    mStatusView.setTextColor(Color.GRAY);
                }
                else if (status == Constants.INVOICE_STATUS_DRAFT) {
                    mStatusView.setText(context.getString(R.string.draft));
                    mStatusView.setTextColor(Color.GRAY);
                }
            }

        }

        public class ListFooterViewHolder extends ViewHolder {
            public ListFooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ListHeaderViewHolder extends ViewHolder {
            public ListHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class ListItemViewHolder extends ViewHolder {
            public ListItemViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            if (viewType == INVOICE_DETAILS_LIST_HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_sent_invoice_details_header, parent, false);
                ListHeaderViewHolder vh = new ListHeaderViewHolder(v);
                return vh;

            } else if (viewType == INVOICE_DETAILS_LIST_FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_sent_invoice_details_footer, parent, false);
                ListFooterViewHolder vh = new ListFooterViewHolder(v);
                return vh;

            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_make_payment_notification_review, parent, false);
                ListItemViewHolder vh = new ListItemViewHolder(v);
                return vh;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                if (holder instanceof ListItemViewHolder) {
                    ListItemViewHolder vh = (ListItemViewHolder) holder;
                    vh.bindViewForListItem(position);

                } else if (holder instanceof ListHeaderViewHolder) {
                    ListHeaderViewHolder vh = (ListHeaderViewHolder) holder;
                    vh.bindViewForHeader(position);

                } else if (holder instanceof ListFooterViewHolder) {
                    ListFooterViewHolder vh = (ListFooterViewHolder) holder;
                    vh.bindViewForFooter(position);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mItemList == null) return 0;
            if (mItemList.length > 0)
                return 1 + mItemList.length + 1;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (mItemList == null) return super.getItemViewType(position);

            if (mItemList.length > 0) {
                if (position == 0) return INVOICE_DETAILS_LIST_HEADER_VIEW;

                else if (position == mItemList.length + 1)
                    return INVOICE_DETAILS_LIST_FOOTER_VIEW;

                else return INVOICE_DETAILS_LIST_ITEM_VIEW;
            }

            return super.getItemViewType(position);
        }
    }
}

