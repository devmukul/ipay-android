package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.TrendingBusinessOutletSelectorDialog;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments.TransactionHistoryCompletedFragment;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.CustomBusinessContact;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.Outlets;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MakePaymentContactsSearchView extends RelativeLayout implements SearchView.OnQueryTextListener{

    private SearchView mCustomAutoCompleteView;

    private RecyclerView mTransactionHistoryRecyclerView;
    private BusinessContactListAdapter mTransactionHistoryAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<CustomBusinessContact> userTransactionHistories;

//    private BusinessContactListAdapter mBusinessContactsAdapter;

    private List<BusinessContact> mBusinessContactList;
    private String mQuery = "";
    private String mImageURL = "";
    private String mName = "";
    private String mAddress = "";
    private String mThanaDistrict = "";
    private String mOutlet = "";


    private View mPayByQCView;
    private PinChecker pinChecker;

    private Context mContext;
    private CustomFocusListener mCustomFocusListener;

    public MakePaymentContactsSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public MakePaymentContactsSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MakePaymentContactsSearchView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_payment_search, this, true);


        mPayByQCView = view.findViewById(R.id.qr_scan);
        mCustomAutoCompleteView = (SearchView) view.findViewById(R.id.search_business);
        mCustomAutoCompleteView.setIconified(false);
        mCustomAutoCompleteView.setOnQueryTextListener(this);
        mCustomAutoCompleteView.clearFocus();

        mTransactionHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.address_recycler_view);
        mLayoutManager = new LinearLayoutManager(mContext);

        mCustomAutoCompleteView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    readBusinessContactsFromDB();
                    mTransactionHistoryRecyclerView.setVisibility(VISIBLE);
                }
                else {
                    mTransactionHistoryRecyclerView.setVisibility(GONE);
                }
            }
        });

        mPayByQCView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinChecker = new PinChecker(mContext, new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent;
                        intent = new Intent(mContext, QRCodePaymentActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                pinChecker.execute();
            }
        });

        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void setOnCustomFocusChangeListener(CustomFocusListener mCustomFocusListener) {
        this.mCustomFocusListener = mCustomFocusListener;
    }

    private List<CustomBusinessContact> getBusinessContactList(Cursor cursor) {
        List<CustomBusinessContact> mBusinessContacts;
        int businessNameIndex;
        int phoneNumberIndex;
        int profilePictureUrlIndex;
        int businessTypeIndex;
        int businessAddressIndex;
        int businessThanaIndex;
        int businessDistrictIndex;
        int businessOutletIndex;


        mBusinessContacts = new ArrayList<>();

        if (cursor != null) {
            mBusinessContacts.clear();
            businessNameIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_NAME);
            phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_PROFILE_PICTURE);
            businessTypeIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_TYPE);
            businessAddressIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_ADDRESS);
            businessThanaIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_THANA);
            businessDistrictIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_DISTRICT);
            businessOutletIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_OUTLET);

            if (cursor.moveToFirst())
                do {
                    String businessName = cursor.getString(businessNameIndex);
                    String mobileNumber = cursor.getString(phoneNumberIndex);
                    String profilePictureUrl = cursor.getString(profilePictureUrlIndex);
                    int businessTypeID = cursor.getInt(businessTypeIndex);
                    String businessAddress = cursor.getString(businessAddressIndex);
                    String businessThana = cursor.getString(businessThanaIndex);
                    String businessDistrict = cursor.getString(businessDistrictIndex);
                    String businessOutlet = cursor.getString(businessOutletIndex);

                    System.out.println("Bus Type>> "+businessTypeID);

                    if(!TextUtils.isEmpty(businessOutlet)) {
                        Outlets[] outlets = new Gson().fromJson(businessOutlet, Outlets[].class);
                        if(outlets.length>0) {
                            for (Outlets outlet : outlets) {
                                CustomBusinessContact businessContact = new CustomBusinessContact();
                                businessContact.setTypeInList("Outlet");
                                businessContact.setBusinessName(businessName);
                                businessContact.setMobileNumber(mobileNumber);

                                if (CommonData.getBusinessTypes() != null) {
                                    BusinessType businessType = CommonData.getBusinessTypeById(businessTypeID);
                                    if (businessType != null)
                                        businessContact.setBusinessType(businessType.getName());
                                }
                                businessContact.setProfilePictureUrl(outlet.getOutletLogoUrl());
                                businessContact.setAddressString(outlet.getAddressString());
                                businessContact.setThanaString(outlet.getOutletAddress().getThanaName());
                                businessContact.setDistrictString(outlet.getOutletAddress().getDistrictName());
                                businessContact.setOutletName(outlet.getOutletName());
                                businessContact.setOutletId(outlet.getOutletId());
                                mBusinessContacts.add(businessContact);
                            }
                        }else {

                            CustomBusinessContact businessContact = new CustomBusinessContact();
                            businessContact.setTypeInList("Business");
                            businessContact.setBusinessName(businessName);
                            businessContact.setMobileNumber(mobileNumber);
                            businessContact.setProfilePictureUrl(profilePictureUrl);
                            businessContact.setAddressString(businessAddress);
                            businessContact.setThanaString(businessThana);
                            businessContact.setDistrictString(businessDistrict);

                            if (CommonData.getBusinessTypes() != null) {
                                BusinessType businessType = CommonData.getBusinessTypeById(businessTypeID);
                                if (businessType != null)
                                    businessContact.setBusinessType(businessType.getName());
                            }

                            mBusinessContacts.add(businessContact);
                        }
                    }else {
                        CustomBusinessContact businessContact = new CustomBusinessContact();
                        businessContact.setTypeInList("Business");
                        businessContact.setBusinessName(businessName);
                        businessContact.setMobileNumber(mobileNumber);
                        businessContact.setProfilePictureUrl(profilePictureUrl);
                        businessContact.setAddressString(businessAddress);
                        businessContact.setThanaString(businessThana);
                        businessContact.setDistrictString(businessDistrict);

                        if (CommonData.getBusinessTypes() != null) {
                            BusinessType businessType = CommonData.getBusinessTypeById(businessTypeID);
                            if (businessType != null)
                                businessContact.setBusinessType(businessType.getName());
                        }

                        mBusinessContacts.add(businessContact);
                    }

                } while (cursor.moveToNext());
        }

        return mBusinessContacts;
    }

    private void readBusinessContactsFromDB() {
        Cursor mCursor;
        DataHelper dataHelper = DataHelper.getInstance(mContext);
        mCursor = dataHelper.searchBusinessAccounts(mQuery);

        try {
            if (mCursor != null) {
                userTransactionHistories = getBusinessContactList(mCursor);
                setBusinessContactAdapter(userTransactionHistories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }

    private void setBusinessContactAdapter(List<CustomBusinessContact> businessContactList) {
        mTransactionHistoryAdapter = new BusinessContactListAdapter(mContext, businessContactList);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mTransactionHistoryAdapter.getFilter().filter(query);
        return true;
    }

    private class BusinessContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

        Context c;
        List<CustomBusinessContact> userTransactionHistories;
        List<CustomBusinessContact> mFilteredOutlets;

        public BusinessContactListAdapter(Context c, List<CustomBusinessContact> userTransactionHistories) {
            this.c = c;
            this.userTransactionHistories = userTransactionHistories;
            this.mFilteredOutlets = userTransactionHistories;
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();
                    System.out.println(">>>>Q  "+charString);

                    if (charString.isEmpty()) {
                        mFilteredOutlets = userTransactionHistories;
                    } else {
                        List<CustomBusinessContact> filteredList = new ArrayList<>();

                        for (CustomBusinessContact outletsList : userTransactionHistories) {

                            if (!TextUtils.isEmpty(outletsList.getBusinessName()) && outletsList.getBusinessName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(outletsList);
                            }else if(!TextUtils.isEmpty(outletsList.getAddressString()) && outletsList.getAddressString().toLowerCase().contains(charString.toLowerCase())){
                                filteredList.add(outletsList);
                            }else if(!TextUtils.isEmpty(outletsList.getOutletName()) && outletsList.getOutletName().toLowerCase().contains(charString.toLowerCase())){
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
                    mFilteredOutlets = (List<CustomBusinessContact>) filterResults.values;
//                    if (mFilteredOutlets.size() == 0) {
//                        noResultTextView.setVisibility(View.VISIBLE);
//                    }else{
//                        noResultTextView.setVisibility(View.GONE);
//                    }
                    notifyDataSetChanged();
                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView businessNameView;
            private TextView businessTypeView;
            private ProfileImageView profilePictureView;
            private TextView businessAddressView;



            public ViewHolder(final View itemView) {
                super(itemView);
                businessNameView = (TextView) itemView.findViewById(R.id.business_name);
                businessTypeView = (TextView) itemView.findViewById(R.id.business_type);
                profilePictureView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                businessAddressView = (TextView) itemView.findViewById(R.id.business_address);
            }

            public void bindView(final int pos) {

                final CustomBusinessContact businessContact = mFilteredOutlets.get(pos);

                final String businessName = businessContact.getBusinessName();
                final String mobileNumber = businessContact.getMobileNumber();
                final String businessType = businessContact.getBusinessType();
                final String profilePictureUrl = businessContact.getProfilePictureUrl();
                final String businessAddress = businessContact.getAddressString();
                final String businessThana = businessContact.getThanaString();
                final String businessDistrict = businessContact.getDistrictString();
                final String businessOutlet = businessContact.getOutletName();

                if(businessContact.getTypeInList().equals("Outlet")){
                    if (businessOutlet != null && !businessOutlet.isEmpty())
                        businessNameView.setText(businessOutlet);
                }else{

                    if (businessName != null && !businessName.isEmpty())
                        businessNameView.setText(businessName);
                }



                if (businessType != null) {
                    businessTypeView.setText(businessType);
                    businessTypeView.setVisibility(VISIBLE);
                }
                if (businessAddress != null && !businessAddress.isEmpty()) {
                    businessAddressView.setText(businessAddress);
                }
                profilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + profilePictureUrl, false);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchToMakePaymentActivity(pos);

//                    setText(mobileNumber);
//
//                    mName = businessName;
//                    mImageURL = profilePictureUrl;
//                    mAddress = businessAddress;
//                    mThanaDistrict = businessThana + ", " + businessDistrict;
//                    mOutlet = businessOutlet;
//                    mCustomAutoCompleteView.clearFocus();
//                    Utilities.hideKeyboard(mContext, mCustomAutoCompleteView);
                    }
                });
            }
        }



        // Now define the view holder for Normal list item
        class NormalViewHolder extends BusinessContactListAdapter.ViewHolder {
            NormalViewHolder(final View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do whatever you want on clicking the normal items


                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BusinessContactListAdapter.NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_business_contact, parent, false));

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                BusinessContactListAdapter.NormalViewHolder vh = (BusinessContactListAdapter.NormalViewHolder) holder;
                vh.bindView(position);
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
            return super.getItemViewType(position);
        }

        private void switchToMakePaymentActivity(int position) {
            Intent intent = new Intent(mContext, PaymentActivity.class);
            intent.putExtra(Constants.NAME, mFilteredOutlets.get(position).getBusinessName());
            intent.putExtra(Constants.ADDRESS, mFilteredOutlets.get(position).getAddressString());
            intent.putExtra(Constants.DISTRICT, mFilteredOutlets.get(position).getDistrictString());
            intent.putExtra(Constants.THANA, mFilteredOutlets.get(position).getThanaString());
            intent.putExtra(Constants.MOBILE_NUMBER, mFilteredOutlets.get(position).getMobileNumber());
            intent.putExtra(Constants.PHOTO_URI, mFilteredOutlets.get(position).getProfilePictureUrl());
            intent.putExtra(Constants.OUTLET_ID, mFilteredOutlets.get(position).getOutletId());
            intent.putExtra(Constants.OUTLET_NAME, mFilteredOutlets.get(position).getOutletName());
            intent.putExtra(Constants.FROM_BRANCHING, true);
            mContext.startActivity(intent);
        }

    }

    public interface CustomFocusListener {
        void onFocusChange(View v, boolean hasFocus);
    }



}

