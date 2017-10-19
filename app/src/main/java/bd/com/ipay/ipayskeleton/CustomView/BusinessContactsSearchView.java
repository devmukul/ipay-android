package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
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

public class BusinessContactsSearchView extends FrameLayout {

    private CustomAutoCompleteView mCustomAutoCompleteView;

    private List<BusinessContact> mBusinessContactList;
    private String mQuery = "";

    private Context mContext;

    public BusinessContactsSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public BusinessContactsSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BusinessContactsSearchView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_contacts_search_view, this, true);

        mCustomAutoCompleteView = (CustomAutoCompleteView) view.findViewById(R.id.auto_complete_view);

        mCustomAutoCompleteView.addTextChangedListener(new CustomAutoCompleteTextChangedListener());

        mBusinessContactList = new ArrayList<>();
        setBusinessContactAdapter(mBusinessContactList);
    }

    public class CustomAutoCompleteTextChangedListener implements TextWatcher {

        CustomAutoCompleteTextChangedListener() {
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
            if (userInput.length() > 0) {

                mQuery = userInput.toString();

                try {
                    // Query the database based on the user input
                    readBusinessContactsFromDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Editable getText() {
        return mCustomAutoCompleteView.getText();
    }


    public void setError(String error) {
        mCustomAutoCompleteView.setError(error);
    }

    public void setText(String text) {
        mCustomAutoCompleteView.setText(text);
        mCustomAutoCompleteView.setSelection(text.length());
        mCustomAutoCompleteView.setError(null);

        hideSuggestionList();
    }

    public void hideSuggestionList() {
        mCustomAutoCompleteView.dismissDropDown();
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
                        if (businessType != null)
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
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }

    private void setBusinessContactAdapter(List<BusinessContact> businessContactList) {
        BusinessContactListAdapter mBusinessContactsAdapter = new BusinessContactListAdapter(mContext, businessContactList);
        mCustomAutoCompleteView.setAdapter(mBusinessContactsAdapter);
    }

    public class BusinessContactListAdapter extends ArrayAdapter<BusinessContact> {
        private LayoutInflater inflater;

        private TextView businessNameView;
        private TextView businessTypeView;
        private TextView mobileNumberView;
        private ProfileImageView profilePictureView;

        BusinessContactListAdapter(Context context, List<BusinessContact> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null)
                view = inflater.inflate(R.layout.list_item_business_contact, parent, false);

            businessNameView = (TextView) view.findViewById(R.id.business_name);
            businessTypeView = (TextView) view.findViewById(R.id.business_type);
            mobileNumberView = (TextView) view.findViewById(R.id.mobile_number);
            profilePictureView = (ProfileImageView) view.findViewById(R.id.profile_picture);

            return bindView(view, position);
        }

        public View bindView(View view, int position) {
            BusinessContact businessContact = getItem(position);

            if (businessContact == null) {
                return view;
            }

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
                    setText(mobileNumber);
                }
            });

            return view;
        }
    }
}

