package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SearchViewForBusinessContact extends FrameLayout {

    private CustomAutoCompleteView mCustomAutoCompleteView;
    private TextView mMobileNumberHintView;

    private List<BusinessContact> mBusinessContactList;
    private BusinessContactListAdapter mBusinessContactsAdapter;
    private String mQuery = "";

    private Context mContext;

    public SearchViewForBusinessContact(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public SearchViewForBusinessContact(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SearchViewForBusinessContact(Context context) {
        super(context);
        initView(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.mContext = context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_business_contact_search_view, this, true);

        mCustomAutoCompleteView = (CustomAutoCompleteView) view.findViewById(R.id.auto_complete_view);
        mMobileNumberHintView = (TextView) view.findViewById(R.id.mobile_number_hint);

        mCustomAutoCompleteView.addTextChangedListener(new CustomAutoCompleteTextChangedListener());

        mBusinessContactList = new ArrayList<>();
        setBusinessContactAdapter(mBusinessContactList);
    }

    public class CustomAutoCompleteTextChangedListener implements TextWatcher {

        public CustomAutoCompleteTextChangedListener() {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence userInput, int start, int before, int count) {
            if (userInput.length() > 0)
                mMobileNumberHintView.setVisibility(VISIBLE);
            else
                mMobileNumberHintView.setVisibility(INVISIBLE);

            mQuery = userInput.toString();

            // Query the database based on the user input
            readBusinessContactsFromDB();
        }
    }

    public Editable getText() {
        return mCustomAutoCompleteView.getText();
    }

    public void setText(String text) {
        mCustomAutoCompleteView.setText(text);
    }

    public void setError(String error) {
        mCustomAutoCompleteView.setError(error);
    }

    private List<BusinessContact> getBusinessContactList(Cursor cursor) {
        List<BusinessContact> mBusinessContacts;
        int businessNameIndex;
        int phoneNumberIndex;
        int profilePictureUrlIndex;
        int businessTypeIndex;

        mBusinessContacts = new ArrayList<>();

        if (cursor != null) {
            mBusinessContacts.clear();
            businessNameIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_NAME);
            phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_PROFILE_PICTURE);
            businessTypeIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_TYPE);

            if (cursor.moveToFirst())
                do {
                    String businessName = cursor.getString(businessNameIndex);
                    String mobileNumber = cursor.getString(phoneNumberIndex);
                    String profilePictureUrl = cursor.getString(profilePictureUrlIndex);
                    int businessTypeID = cursor.getInt(businessTypeIndex);

                    BusinessContact businessContact = new BusinessContact();
                    businessContact.setBusinessName(businessName);
                    businessContact.setMobileNumber(mobileNumber);
                    businessContact.setProfilePictureUrl(profilePictureUrl);

                    if (CommonData.getBusinessTypes() != null) {
                        BusinessType businessType = CommonData.getBusinessTypeById(businessTypeID);
                        if(businessType !=null)
                        businessContact.setBusinessType(businessType.getName());
                    }

                    mBusinessContacts.add(businessContact);

                } while (cursor.moveToNext());
        }

        return mBusinessContacts;
    }

    private void readBusinessContactsFromDB() {
        Cursor mCursor;
        DataHelper dataHelper = DataHelper.getInstance(mContext);
        mCursor = dataHelper.searchBusinessContacts(mQuery);

        try {
            if (mCursor != null) {
                mBusinessContactList = getBusinessContactList(mCursor);
                setBusinessContactAdapter(mBusinessContactList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCursor.close();
        }
    }

    private void setBusinessContactAdapter(List<BusinessContact> businessContactList) {
        mBusinessContactsAdapter = new BusinessContactListAdapter(mContext, businessContactList);
        mCustomAutoCompleteView.setAdapter(mBusinessContactsAdapter);
    }

    public class BusinessContactListAdapter extends ArrayAdapter<BusinessContact> {

        private LayoutInflater inflater;

        private TextView businessNameView;
        private TextView businessTypeView;
        private TextView mobileNumberView;
        private ProfileImageView profilePictureView;

        public BusinessContactListAdapter(Context context, List<BusinessContact> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BusinessContact businessContact = getItem(position);
            View view = convertView;

            if (view == null)
                view = inflater.inflate(R.layout.list_item_business_contact, null);

            businessNameView = (TextView) view.findViewById(R.id.business_name);
            businessTypeView = (TextView) view.findViewById(R.id.business_type);
            mobileNumberView = (TextView) view.findViewById(R.id.mobile_number);
            profilePictureView = (ProfileImageView) view.findViewById(R.id.profile_picture);

            final String businessName = businessContact.getBusinessName();
            final String mobileNumber = businessContact.getMobileNumber();
            final String businessType = businessContact.getBusinessType();
            final String profilePictureUrl = Constants.BASE_URL_FTP_SERVER + businessContact.getProfilePictureUrl();

            if (businessName != null && !businessName.isEmpty())
                businessNameView.setText(businessName);

            if (businessType != null) {
                businessTypeView.setText(businessType);
                businessTypeView.setVisibility(VISIBLE);
            }
            mobileNumberView.setText(mobileNumber);
            profilePictureView.setProfilePicture(profilePictureUrl, false);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCustomAutoCompleteView.setFocusable(false);
                    mCustomAutoCompleteView.setFocusableInTouchMode(false);
                    mCustomAutoCompleteView.setText(mobileNumber);
                    mCustomAutoCompleteView.setFocusable(true);
                    mCustomAutoCompleteView.setFocusableInTouchMode(true);

                    mBusinessContactList.clear();
                    mBusinessContactsAdapter.notifyDataSetChanged();
                }
            });

            return view;
        }
    }
}

