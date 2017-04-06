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
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ContactsSearchView extends FrameLayout {

    private CustomAutoCompleteView mCustomAutoCompleteView;
    private TextView mMobileNumberHintView;

    private List<DBContactNode> mContactList;
    private ContactListAdapter mContactsAdapter;

    public boolean mFilterByVerifiedUsersOnly;
    public boolean mFilterByiPayMembersOnly;
    public boolean mFilterByBusinessMembersOnly;

    private String mQuery = "";
    private Context mContext;

    private CustomTextChangeListener customTextChangeListener;

    public ContactsSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public ContactsSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ContactsSearchView(Context context) {
        super(context);
        initView(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.mContext = context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_contacts_search_view, this, true);

        mCustomAutoCompleteView = (CustomAutoCompleteView) view.findViewById(R.id.auto_complete_view);
        mMobileNumberHintView = (TextView) view.findViewById(R.id.mobile_number_hint);

        mCustomAutoCompleteView.addTextChangedListener(new CustomAutoCompleteTextChangedListener());

        mContactList = new ArrayList<>();
        setBusinessContactAdapter(mContactList);
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
            if (userInput.length() > 0) {
                mMobileNumberHintView.setVisibility(VISIBLE);

                if (CurrentFragmentTag() != null && CurrentFragmentTag().equals(Constants.TOP_UP))
                    customTextChangeListener.onTextChange(userInput.toString());

            } else mMobileNumberHintView.setVisibility(INVISIBLE);

            mQuery = userInput.toString();

            // Query in database based on the user input
            readContactsFromDB();
        }
    }

    public Editable getText() {
        return mCustomAutoCompleteView.getText();
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

    public void setText(String text) {
        mCustomAutoCompleteView.setText(text);
        mCustomAutoCompleteView.setSelection(text.length());
        mCustomAutoCompleteView.setError(null);

        hideSuggestionList();
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
        mCursor = dataHelper.searchContacts(mQuery, mFilterByiPayMembersOnly, mFilterByBusinessMembersOnly, false,
                mFilterByVerifiedUsersOnly, false, false, null);

        try {
            if (mCursor != null) {
                mContactList = getContactList(mCursor);
                setBusinessContactAdapter(mContactList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCursor.close();
        }
    }

    private void setBusinessContactAdapter(List<DBContactNode> contactList) {
        mContactsAdapter = new ContactListAdapter(mContext, contactList);
        mCustomAutoCompleteView.setAdapter(mContactsAdapter);
    }

    public class ContactListAdapter extends ArrayAdapter<DBContactNode> {
        private LayoutInflater inflater;

        private TextView primaryNameView;
        private TextView secondaryNameView;
        private ProfileImageView profilePictureView;
        private TextView mobileNumberView;
        private ImageView memberStatusView;
        private ImageView verificationStatusView;
        private TextView inviteStatusTextView;
        private Button inviteButton;

        public ContactListAdapter(Context context, List<DBContactNode> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) view = inflater.inflate(R.layout.list_item_contact, null);

            primaryNameView = (TextView) view.findViewById(R.id.name1);
            secondaryNameView = (TextView) view.findViewById(R.id.name2);
            mobileNumberView = (TextView) view.findViewById(R.id.mobile_number);
            profilePictureView = (ProfileImageView) view.findViewById(R.id.profile_picture);
            memberStatusView = (ImageView) view.findViewById(R.id.is_member);
            verificationStatusView = (ImageView) view.findViewById(R.id.verification_status);
            inviteStatusTextView = (TextView) view.findViewById(R.id.invite_status);
            inviteButton = (Button) view.findViewById(R.id.button_invite);

            return bindView(view, position);
        }

        public View bindView(View view, int position) {
            DBContactNode contact = getItem(position);

            final String name = contact.getName();
            final String originalName = contact.getOriginalName();
            final String mobileNumber = contact.getMobileNumber();
            final String profilePictureUrlQualityMedium = Constants.BASE_URL_FTP_SERVER + contact.getProfilePictureUrlQualityMedium();
            final boolean isVerified = contact.getVerificationStatus() == DBConstants.VERIFIED_USER;
            final boolean isMember = contact.getMemberStatus() == DBConstants.IPAY_MEMBER;

            /**
             * We need to show original name on the top if exists
             */
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

            if (isMember) memberStatusView.setVisibility(View.VISIBLE);
            else memberStatusView.setVisibility(View.GONE);

            if (isVerified) verificationStatusView.setVisibility(View.VISIBLE);
            else verificationStatusView.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setText(mobileNumber);
                }
            });

            return view;
        }
    }

    String CurrentFragmentTag() {
        return null;
    }

    public interface CustomTextChangeListener {
        void onTextChange(String inputText);
    }

    public void setCustomTextChangeListener(CustomTextChangeListener customTextChangeListener) {
        this.customTextChangeListener = customTextChangeListener;
    }
}


