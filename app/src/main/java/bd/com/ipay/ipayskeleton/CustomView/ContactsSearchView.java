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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.Contact.DBContactNode;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;

public class ContactsSearchView extends FrameLayout {

    public boolean mFilterByVerifiedUsersOnly;
    public boolean mFilterByiPayMembersOnly;
    public boolean mFilterByBusinessMembersOnly;
    private CustomAutoCompleteView mCustomAutoCompleteView;
    private List<DBContactNode> mContactList;
    private String mQuery = "";
    private Context mContext;

    private CustomTextChangeListener customTextChangeListener;

    public ContactsSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ContactsSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ContactsSearchView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_contacts_search_view, this, true);

        mCustomAutoCompleteView = (CustomAutoCompleteView) view.findViewById(R.id.auto_complete_view);

        mCustomAutoCompleteView.addTextChangedListener(new CustomAutoCompleteTextChangedListener());

        mCustomAutoCompleteView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String inputString = mCustomAutoCompleteView.getText().toString().trim();
                    customTextChangeListener.onTextChange(inputString);
                }
            }
        });

        mContactList = new ArrayList<>();
        setBusinessContactAdapter(mContactList);
    }

    public Editable getText() {
        return mCustomAutoCompleteView.getText();
    }

    public void setText(String text) {
        mCustomAutoCompleteView.setText(text);
        mCustomAutoCompleteView.setSelection(text.length());
        mCustomAutoCompleteView.setError(null);

        hideSuggestionList();
    }

    public void setError(String error) {
        mCustomAutoCompleteView.setError(error);
    }

    public void setEnabledStatus(boolean status) {
        mCustomAutoCompleteView.setEnabled(status);
    }

    public void setFocusableStatus(boolean status) {
        mCustomAutoCompleteView.setFocusable(status);
    }

    public void hideSuggestionList() {
        mCustomAutoCompleteView.dismissDropDown();
    }

    private List<DBContactNode> getContactList(Cursor cursor) {
        List<DBContactNode> mContacts;

        int nameIndex;
        int originalNameIndex;
        int phoneNumberIndex;
        int profilePictureUrlQualityMediumIndex;
        int verificationStatusIndex;
        int isMemberIndex;

        mContacts = new ArrayList<>();

        if (cursor != null) {
            mContacts.clear();

            nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
            originalNameIndex = cursor.getColumnIndex(DBConstants.KEY_ORIGINAL_NAME);
            phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            profilePictureUrlQualityMediumIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM);
            verificationStatusIndex = cursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
            isMemberIndex = cursor.getColumnIndex(DBConstants.KEY_IS_MEMBER);

            if (cursor.moveToFirst())
                do {
                    DBContactNode contact = new DBContactNode();

                    contact.setName(cursor.getString(nameIndex));
                    contact.setOriginalName(cursor.getString(originalNameIndex));
                    contact.setMobileNumber(cursor.getString(phoneNumberIndex));
                    contact.setProfilePictureUrlQualityMedium(cursor.getString(profilePictureUrlQualityMediumIndex));
                    contact.setVerificationStatus(cursor.getInt(verificationStatusIndex));
                    contact.setMemberStatus(cursor.getInt(isMemberIndex));

                    mContacts.add(contact);
                } while (cursor.moveToNext());
        }

        return mContacts;
    }

    private void readContactsFromDB() {
        Cursor mCursor;
        DataHelper dataHelper = DataHelper.getInstance(mContext);
        if (!ProfileInfoCacheManager.isAccountSwitched()) {
            mCursor = dataHelper.searchContacts(mQuery, mFilterByiPayMembersOnly, mFilterByBusinessMembersOnly, false,
                    mFilterByVerifiedUsersOnly, false, false, null);
        } else {
            mCursor = dataHelper.searchBusinessContacts(mQuery, mFilterByiPayMembersOnly, mFilterByBusinessMembersOnly, false,
                    mFilterByVerifiedUsersOnly, false, false, null, Long.parseLong(TokenManager.getOnAccountId()));
        }

        try {
            if (mCursor != null) {
                mContactList = getContactList(mCursor);
                setBusinessContactAdapter(mContactList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }

    private void setBusinessContactAdapter(List<DBContactNode> contactList) {
        ContactListAdapter mContactsAdapter = new ContactListAdapter(mContext, contactList);
        mCustomAutoCompleteView.setAdapter(mContactsAdapter);
    }

    String CurrentFragmentTag() {
        return null;
    }

    public void setCustomTextChangeListener(CustomTextChangeListener customTextChangeListener) {
        this.customTextChangeListener = customTextChangeListener;
    }

    public interface CustomTextChangeListener {
        void onTextChange(String inputText);
    }

    public class CustomAutoCompleteTextChangedListener implements TextWatcher {

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
                if (CurrentFragmentTag() != null && CurrentFragmentTag().equals(Constants.TOP_UP))
                    customTextChangeListener.onTextChange(userInput.toString());
            }

            mQuery = userInput.toString();

            try {
                // Query in database based on the user input
                readContactsFromDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ContactListAdapter extends ArrayAdapter<DBContactNode> {
        private LayoutInflater inflater;

        private TextView primaryNameView;
        private TextView secondaryNameView;
        private ProfileImageView profilePictureView;
        private TextView mobileNumberView;
        private ImageView verificationStatusView;
        private Button inviteStatusTextView;
        private Button inviteButton;

        ContactListAdapter(Context context, List<DBContactNode> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) view = inflater.inflate(R.layout.list_item_contact, parent, false);

            primaryNameView = (TextView) view.findViewById(R.id.name1);
            secondaryNameView = (TextView) view.findViewById(R.id.name2);
            mobileNumberView = (TextView) view.findViewById(R.id.mobile_number);
            profilePictureView = (ProfileImageView) view.findViewById(R.id.profile_picture);
            verificationStatusView = (ImageView) view.findViewById(R.id.verification_status);
            inviteStatusTextView = (Button) view.findViewById(R.id.button_invited);
            inviteButton = (Button) view.findViewById(R.id.button_invite);

            return bindView(view, position);
        }

        public View bindView(View view, int position) {
            DBContactNode contact = getItem(position);

            if (contact == null) {
                return view;
            }

            final String name = contact.getName();
            final String originalName = contact.getOriginalName();
            final String mobileNumber = contact.getMobileNumber();
            final String profilePictureUrlQualityMedium = Constants.BASE_URL_FTP_SERVER + contact.getProfilePictureUrlQualityMedium();
            final boolean isVerified = contact.getVerificationStatus() == DBConstants.VERIFIED_USER;

            // We need to show original name on the top if exists
            if (originalName != null && !originalName.isEmpty()) {
                primaryNameView.setText(originalName);
                secondaryNameView.setVisibility(View.VISIBLE);
                secondaryNameView.setText(name);
            } else {
                primaryNameView.setText(name);
                secondaryNameView.setVisibility(View.GONE);
            }

            mobileNumberView.setText(mobileNumber);
            profilePictureView.setProfilePicture(profilePictureUrlQualityMedium, false);
            inviteStatusTextView.setVisibility(View.GONE);
            inviteButton.setVisibility(View.GONE);

            if (isVerified) verificationStatusView.setVisibility(View.VISIBLE);
            else verificationStatusView.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setText(mobileNumber);
                    mCustomAutoCompleteView.clearFocus();
                }
            });

            return view;
        }
    }
}


