package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.ContactsFragment;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.Outlets;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Merchants.BusinessList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static bd.com.ipay.ipayskeleton.Utilities.Common.CommonColorList.PROFILE_PICTURE_BACKGROUNDS;

public class TrendingBusinessOutletSelectorDialog extends AlertDialog implements SearchView.OnQueryTextListener{
    private Context context;
    private BusinessList merchantDetails;
    private ProfileImageView mMerchantLogoView;
    private TextView merchantNameTextView;
    private TextView noResultTextView;
    private RecyclerView merchantAddressListRecyclerView;
    private MerchantOutletAdapter mMerchantBranchAdapter;
    List<Outlets> outlets;
    private String name;
    private String photoUrl;

    private SearchView mSearchView;


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
        noResultTextView = (TextView) view.findViewById(R.id.textView_noresult);
        mMerchantLogoView = (ProfileImageView) view.findViewById(R.id.merchant_logo);
        supportViewsWithData();
        mMerchantBranchAdapter = new MerchantOutletAdapter(merchantDetails.getOutlets());
        merchantAddressListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        merchantAddressListRecyclerView.setAdapter(mMerchantBranchAdapter);

        mSearchView = (SearchView) view.findViewById(R.id.search_outlet);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);

        // prevent auto focus on Dialog launch
        mSearchView.clearFocus();

        getWindow().setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        this.setView(view);
    }

    public void showDialog() {
        this.show();
    }

    private void supportViewsWithData() {
        this.mMerchantLogoView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + merchantDetails.getBusinessLogo(), false);
        this.merchantNameTextView.setText(merchantDetails.getMerchantName());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mMerchantBranchAdapter.getFilter().filter(query);
        return true;
    }

    public class MerchantOutletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable{

        private List<Outlets> mOutlets;
        private List<Outlets> mFilteredOutlets;

        private static final int EMPTY_VIEW = 10;
        private static final int OUTLET_VIEW = 100;


        public MerchantOutletAdapter(List<Outlets> mOutlets) {
            this.mOutlets = mOutlets;
            this.mFilteredOutlets = mOutlets;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_empty_description, parent, false);
                return new EmptyViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_outlets, parent, false);
                return new ViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {

                if (holder instanceof ViewHolder) {
                    ViewHolder vh = (ViewHolder) holder;
                    vh.bindView(position);
                } else if (holder instanceof EmptyViewHolder) {
                    EmptyViewHolder vh = (EmptyViewHolder) holder;
                    vh.mEmptyDescription.setText(context.getString(R.string.no_outlets));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mFilteredOutlets == null)
                return 0;
            else
                return mFilteredOutlets.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == 0)
                return EMPTY_VIEW;
            else
                return OUTLET_VIEW;
        }

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public final TextView mEmptyDescription;

            public EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = (TextView) itemView.findViewById(R.id.empty_description);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View itemView;
            private ProfileImageView outletIcon;
            private TextView mNameTextView;
            private TextView mAddressTextView;

            public ViewHolder(View itemView) {

                super(itemView);

                this.itemView = itemView;
                outletIcon = (ProfileImageView) itemView.findViewById(R.id.outlet_radio_button);
                mNameTextView = (TextView) itemView.findViewById(R.id.outlet_name);
                mAddressTextView = (TextView) itemView.findViewById(R.id.outlet_address);
                outletIcon.setBusinessLogoPlaceHolder();
            }

            public void bindView(final int position) {
                mNameTextView.setText(mFilteredOutlets.get(position).getOutletName());
                mAddressTextView.setText(mFilteredOutlets.get(position).getAddressString());
                outletIcon.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mFilteredOutlets.get(position).getOutletLogoUrl(), false);

                outletIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchToMakePaymentActivity(position);
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchToMakePaymentActivity(position);

                    }
                });
            }
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();

                    if (charString.isEmpty()) {
                        mFilteredOutlets = mOutlets;
                    } else {
                        List<Outlets> filteredList = new ArrayList<>();

                        for (Outlets outletsList : mOutlets) {

                            if (outletsList.getOutletName().toLowerCase().contains(charString.toLowerCase()) || outletsList.getAddressString().toLowerCase().contains(charString)) {
                                filteredList.add(outletsList);
                            }
                        }

                        mFilteredOutlets = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredOutlets;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mFilteredOutlets = (List<Outlets>) filterResults.values;

                    if (mFilteredOutlets.size() == 0) {
                        noResultTextView.setVisibility(View.VISIBLE);
                    }else{
                        noResultTextView.setVisibility(View.GONE);
                    }
                    notifyDataSetChanged();
                }
            };
        }

        private void switchToMakePaymentActivity(int position) {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra(Constants.NAME, merchantDetails.getMerchantName());
            intent.putExtra(Constants.ADDRESS, mFilteredOutlets.get(position).getAddressString());
            intent.putExtra(Constants.DISTRICT, mFilteredOutlets.get(position).getOutletAddress().getDistrictName());
            intent.putExtra(Constants.THANA, mFilteredOutlets.get(position).getOutletAddress().getThanaName());
            intent.putExtra(Constants.MOBILE_NUMBER, merchantDetails.getMerchantMobileNumber());
            intent.putExtra(Constants.PHOTO_URI, merchantDetails.getBusinessLogo());
            intent.putExtra(Constants.OUTLET_ID, mFilteredOutlets.get(position).getOutletId());
            intent.putExtra(Constants.OUTLET_NAME, mFilteredOutlets.get(position).getOutletName());
            intent.putExtra(Constants.FROM_BRANCHING, true);
            context.startActivity(intent);
            TrendingBusinessOutletSelectorDialog.this.dismiss();
        }
    }
}
