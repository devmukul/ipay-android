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
import bd.com.ipay.ipayskeleton.Model.Friend.Contact;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ContactsSearchView extends FrameLayout {

    private CustomAutoCompleteView mCustomAutoCompleteView;
    private TextView mMobileNumberHintView;

    private List<Contact> mContactList;
    private ContactListAdapter mContactsAdapter;

    private boolean mFilterByVerifiedUsersOnly;
    private boolean mFilterByiPayMembersOnly;
    private boolean mFilterByBusinessMembersOnly;

    private String mQuery = "";
    private Context mContext;

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
            if (userInput.length() > 0)
                mMobileNumberHintView.setVisibility(VISIBLE);
            else
                mMobileNumberHintView.setVisibility(INVISIBLE);

            mQuery = userInput.toString();

            // Query in database based on the user input
            readContactsFromDB();
        }
    }

    public void setSearchViewFilters(ContactSearchHelper contactSearchHelper) {
        mFilterByVerifiedUsersOnly = contactSearchHelper.isFilterByVerifiedMembers();
        mFilterByiPayMembersOnly = contactSearchHelper.isFilterByiPayMembers();
        mFilterByBusinessMembersOnly = contactSearchHelper.isFilterByBusinessMembers();
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

    private List<Contact> getContactList(Cursor cursor) {
        List<Contact> mContacts;

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
                    Contact contact = new Contact();

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
        mCursor = dataHelper.searchFriends(mQuery, mFilterByiPayMembersOnly, mFilterByBusinessMembersOnly, false,
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

    private void setBusinessContactAdapter(List<Contact> contactList) {
        mContactsAdapter = new ContactListAdapter(mContext, contactList);
        mCustomAutoCompleteView.setAdapter(mContactsAdapter);
    }

    public class ContactListAdapter extends ArrayAdapter<Contact> {
        private LayoutInflater inflater;

        private TextView name1View;
        private TextView name2View;
        private ProfileImageView profilePictureView;
        private TextView mobileNumberView;
        private ImageView memberStatusView;
        private ImageView verificationStatusView;
        private TextView inviteStatusTextView;
        private Button inviteButton;

        public ContactListAdapter(Context context, List<Contact> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null)
                view = inflater.inflate(R.layout.list_item_contact, null);

            name1View = (TextView) view.findViewById(R.id.name1);
            name2View = (TextView) view.findViewById(R.id.name2);
            mobileNumberView = (TextView) view.findViewById(R.id.mobile_number);
            profilePictureView = (ProfileImageView) view.findViewById(R.id.profile_picture);
            memberStatusView = (ImageView) view.findViewById(R.id.is_member);
            verificationStatusView = (ImageView) view.findViewById(R.id.verification_status);
            inviteStatusTextView = (TextView) view.findViewById(R.id.invite_status);
            inviteButton = (Button) view.findViewById(R.id.button_invite);

            return bindView(view, position);
        }

        public View bindView(View view, int position) {
            Contact contact = getItem(position);

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
                name1View.setText(originalName);
                name2View.setVisibility(View.VISIBLE);
                name2View.setText(name);
            } else {
                name1View.setText(name);
                name2View.setVisibility(View.GONE);
            }

            mobileNumberView.setText(mobileNumber);
            profilePictureView.setProfilePicture(profilePictureUrlQualityMedium, false);
            inviteStatusTextView.setVisibility(View.GONE);
            inviteButton.setVisibility(View.GONE);

            if (isMember) {
                memberStatusView.setVisibility(View.VISIBLE);
            } else {
                memberStatusView.setVisibility(View.GONE);
            }

            if (isVerified) {
                verificationStatusView.setVisibility(View.VISIBLE);
            } else {
                verificationStatusView.setVisibility(View.GONE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomAutoCompleteView.setFocusable(false);
                    mCustomAutoCompleteView.setFocusableInTouchMode(false);
                    mCustomAutoCompleteView.setText(mobileNumber);
                    mCustomAutoCompleteView.setFocusable(true);
                    mCustomAutoCompleteView.setFocusableInTouchMode(true);

                    mContactList.clear();
                    mContactsAdapter.notifyDataSetChanged();
                }
            });

            return view;
        }
    }
}


