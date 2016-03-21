package bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bd.com.ipay.ipayskeleton.Activities.EditProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetIntroducerListRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetIntroducerListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetUserAddressResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetIdentificationDocumentResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IdentificationDocument;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IdentificationDocumentsRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UserAddressRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Common.GenderList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ProfileFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private HttpRequestGetAsyncTask mGetUserAddressTask = null;
    private GetUserAddressResponse mGetUserAddressResponse = null;

    private HttpRequestGetAsyncTask mGetIdentificationDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationDocumentResponse = null;

    private HttpRequestGetAsyncTask mGetIntroducerListTask = null;
    private GetIntroducerListResponse mGetIntroducerListResponse = null;

    private ProgressDialog mProgressDialog;
    private ScrollView mScrollView;

    private RoundedImageView mProfilePicture;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mVerificationStatusView;

    private TextView mEmailAddressView;
    private TextView mDateOfBirthView;
    private TextView mFathersNameView;
    private TextView mMothersNameView;
    private TextView mSpouseNameView;
    private TextView mOccupationView;
    private TextView mGenderView;

    private TextView mPresentAddressView;
    private TextView mPermanentAddressView;
    private TextView mOfficeAddressView;

    private TextView mDocumentCountView;

    private Button mBasicInfoEditButton;
    private Button mPresentAddressEditButton;
    private Button mPermanentAddressEditButton;
    private Button mOfficeAddressEditButton;
    private Button mUploadDocumentsButton;

    private ListView mIntroducerListView;
    private IntroducerListAdapter mIntroducerAdapter;

    private SharedPreferences pref;

    public static String mName = "";
    public static String mMobileNumber = "";
    public static Set<UserProfilePictureClass> mProfilePictures;

    public static String mEmailAddress = "";
    public static String mDateOfBirth = "";
    public static String mFathersName = "";
    public static String mMothersName = "";
    public static String mSpouseName = "";
    public static String mOccupation = "";
    public static String mGender = "";
    public static String mVerificationStatus = "";

    public static AddressClass mPresentAddress;
    public static AddressClass mPermanentAddress;
    public static AddressClass mOfficeAddress;

    public static List<IdentificationDocument> mIdentificationDocuments = new ArrayList<>();
    public static List<Introducer> mIntrdoucers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        getActivity().setTitle(R.string.profile);

        mProfilePictures = new HashSet<>();

        mScrollView = (ScrollView) v.findViewById(R.id.scrollview);

        mProfilePicture = (RoundedImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mVerificationStatusView = (TextView) v.findViewById(R.id.textview_verification_status);

        mEmailAddressView = (TextView) v.findViewById(R.id.textview_email);
        mDateOfBirthView = (TextView) v.findViewById(R.id.textview_date_of_birth);
        mFathersNameView = (TextView) v.findViewById(R.id.textview_fathers_name);
        mMothersNameView = (TextView) v.findViewById(R.id.textview_mothers_name);
        mSpouseNameView = (TextView) v.findViewById(R.id.textview_spouse_name);
        mOccupationView = (TextView) v.findViewById(R.id.textview_occupation);
        mGenderView = (TextView) v.findViewById(R.id.textview_gender);

        mPresentAddressView = (TextView) v.findViewById(R.id.textview_present_address);
        mPermanentAddressView = (TextView) v.findViewById(R.id.textview_permanent_address);
        mOfficeAddressView = (TextView) v.findViewById(R.id.textview_office_address);

        mDocumentCountView = (TextView) v.findViewById(R.id.textview_document_count);

        mBasicInfoEditButton = (Button) v.findViewById(R.id.button_edit_basic_info);
        mPresentAddressEditButton = (Button) v.findViewById(R.id.button_edit_present_address);
        mPermanentAddressEditButton = (Button) v.findViewById(R.id.button_edit_permanent_address);
        mOfficeAddressEditButton = (Button) v.findViewById(R.id.button_edit_office_address);
        mUploadDocumentsButton = (Button) v.findViewById(R.id.button_upload_documents);

        mIntroducerListView = (ListView) v.findViewById(R.id.list_introducers);
        mIntroducerAdapter = new IntroducerListAdapter(getActivity(), new ArrayList<Introducer>());
        mIntroducerListView.setAdapter(mIntroducerAdapter);

        mBasicInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile(EditProfileActivity.TARGET_TAB_BASIC_INFO);
            }
        });
        mPresentAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile(EditProfileActivity.TARGET_TAB_USER_ADDRESS);
            }
        });
        mPermanentAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile(EditProfileActivity.TARGET_TAB_USER_ADDRESS);
            }
        });
        mOfficeAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile(EditProfileActivity.TARGET_TAB_USER_ADDRESS);
            }
        });
        mUploadDocumentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile(EditProfileActivity.TARGET_TAB_UPLOAD_DOCUMENTS);
            }
        });

        mMobileNumber = pref.getString(Constants.USERID, "");
        mGender = pref.getString(Constants.GENDER, "");
        mDateOfBirth = pref.getString(Constants.BIRTHDAY, "");

        mProgressDialog = new ProgressDialog(getActivity());

        // Without these, the listview will gain focus as soon as data loading is finished
        mScrollView.setFocusableInTouchMode(true);
        mScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        setProfilePicture("");
        getProfileInfo();
        getUserAddress();
        getIdentificationDocuments();
        getIntroducerList();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfileInformation();
    }

    private void setProfileInformation() {
        if (mProfilePictures.size() > 0) {

            String imageUrl = "";
            for (Iterator<UserProfilePictureClass> it = mProfilePictures.iterator(); it.hasNext(); ) {
                UserProfilePictureClass userProfilePictureClass = it.next();
                imageUrl = userProfilePictureClass.getUrl();
                break;
            }
            setProfilePicture(imageUrl);
        }
        mMobileNumberView.setText(mMobileNumber);
        mNameView.setText(mName);

        mEmailAddressView.setText(mEmailAddress);
        mGenderView.setText(mGender);
        mDateOfBirthView.setText(mDateOfBirth);
        mFathersNameView.setText(mFathersName);
        mMothersNameView.setText(mMothersName);
        mSpouseNameView.setText(mSpouseName);

        if (mOccupation != null)
            mOccupationView.setText(mOccupation);
        else
            mOccupationView.setText("");
        if (GenderList.genderCodeToNameMap.containsKey(mGender))
            mGenderView.setText(GenderList.genderCodeToNameMap.get(mGender));

        if (mVerificationStatus != null && mVerificationStatus.equals(Constants.VERIFICATION_STATUS_VERIFIED)) {
            mVerificationStatusView.setBackgroundResource(R.drawable.background_verified);
            mVerificationStatusView.setText(R.string.verified);
        }
        else {
            mVerificationStatusView.setBackgroundResource(R.drawable.background_not_verified);
            mVerificationStatusView.setText(R.string.not_verified);
        }

        if (mPresentAddress != null) {
            mPresentAddressView.setText(mPresentAddress.toString());
            mPresentAddressEditButton.setText(R.string.action_edit);
        }
        if (mPermanentAddress != null) {
            mPermanentAddressView.setText(mPermanentAddress.toString());
            mPermanentAddressEditButton.setText(R.string.action_edit);
        }
        if (mOfficeAddress != null) {
            mOfficeAddressView.setText(mOfficeAddress.toString());
            mOfficeAddressEditButton.setText(R.string.action_edit);
        }

        int numberOfDocumentsSubmitted = mIdentificationDocuments.size();

        mDocumentCountView.setText(getString(R.string.you_have_submitted) + " "
                + numberOfDocumentsSubmitted + " " + getString(R.string.documents));

    }

    public void editProfile(int targetTab) {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        intent.putExtra(EditProfileActivity.TARGET_TAB, targetTab);
        startActivity(intent);
    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.fetching_profile_information));
        mProgressDialog.show();

        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_INFO_REQUEST,
                new ProfileInfoRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetProfileInfoTask.execute();
    }

    private void getUserAddress() {
        if (mGetUserAddressTask != null) {
            return;
        }

        mGetUserAddressTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_ADDRESS_REQUEST,
                new UserAddressRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetUserAddressTask.execute();
    }

    private void getIdentificationDocuments() {
        if (mGetIdentificationDocumentsTask != null) {
            return;
        }

        mGetIdentificationDocumentsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST,
                new IdentificationDocumentsRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetIdentificationDocumentsTask.execute();
    }

    private void getIntroducerList() {
        if (mGetIntroducerListTask != null) {
            return;
        }

        mGetIntroducerListTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INTRODUCER_LIST,
                new GetIntroducerListRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetIntroducerListTask.execute();
    }

    private void setProfilePicture(String url) {
        try {
            if (!url.equals("")) {
                if (!url.startsWith("content:"))
                    url = Constants.BASE_URL_IMAGE_SERVER + url;

                Glide.with(getActivity())
                        .load(url)
                        .crossFade()
                        .error(R.drawable.ic_person)
                        .transform(new CircleTransform(getActivity()))
                        .into(mProfilePicture);
                }
            else {
                Glide.with(getActivity())
                        .load(R.drawable.ic_person)
                        .crossFade()
                        .into(mProfilePicture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mGetProfileInfoTask = null;
            mGetUserAddressTask = null;
            mGetIdentificationDocumentsTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_PROFILE_INFO_REQUEST)) {

            try {
                mGetProfileInfoResponse = gson.fromJson(resultList.get(2), GetProfileInfoResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (mGetProfileInfoResponse.getName() != null)
                        mName = mGetProfileInfoResponse.getName();
                    if (mGetProfileInfoResponse.getMobileNumber() != null)
                        mMobileNumber = mGetProfileInfoResponse.getMobileNumber();
                    if (mGetProfileInfoResponse.getEmail() != null)
                        mEmailAddress = mGetProfileInfoResponse.getEmail();
                    if (mGetProfileInfoResponse.getDateOfBirth() != null)
                        mDateOfBirth = mGetProfileInfoResponse.getDateOfBirth();
                    if (mGetProfileInfoResponse.getFather() != null)
                        mFathersName = mGetProfileInfoResponse.getFather();
                    if (mGetProfileInfoResponse.getMother() != null)
                        mMothersName = mGetProfileInfoResponse.getMother();
                    if (mGetProfileInfoResponse.getSpouse() != null)
                        mSpouseName = mGetProfileInfoResponse.getSpouse();
                    if (mGetProfileInfoResponse.getOccupation() != null)
                        mOccupation = mGetProfileInfoResponse.getOccupation();
                    if (mGetProfileInfoResponse.getGender() != null)
                        mGender = mGetProfileInfoResponse.getGender();

                    mVerificationStatus = mGetProfileInfoResponse.getVerificationStatus();
                    mProfilePictures = mGetProfileInfoResponse.getProfilePictures();

                    setProfileInformation();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
            }

            mGetProfileInfoTask = null;
        }
        else if (resultList.get(0).equals(Constants.COMMAND_GET_USER_ADDRESS_REQUEST)) {
            try {
                mGetUserAddressResponse = gson.fromJson(resultList.get(2), GetUserAddressResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mPresentAddress = mGetUserAddressResponse.getPresentAddress();
                    mPermanentAddress = mGetUserAddressResponse.getPermanentAddress();
                    mOfficeAddress = mGetUserAddressResponse.getOfficeAddress();

                    setProfileInformation();
                }
                else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
            }

            mGetUserAddressTask = null;
        }
        else if (resultList.get(0).equals(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST)) {
            try {
                mIdentificationDocumentResponse = gson.fromJson(resultList.get(2), GetIdentificationDocumentResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mIdentificationDocuments = mIdentificationDocumentResponse.getDocuments();

                    setProfileInformation();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
            }

            mGetIdentificationDocumentsTask = null;
        }
        else if (resultList.get(0).equals(Constants.COMMAND_GET_INTRODUCER_LIST)) {
            try {
                mGetIntroducerListResponse = gson.fromJson(resultList.get(2), GetIntroducerListResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mIntrdoucers = mGetIntroducerListResponse.getIntroducers();
                    mIntroducerAdapter.setIntroducers(mIntrdoucers);
                    mIntroducerAdapter.notifyDataSetChanged();
                    Utilities.setUpNonScrollableListView(mIntroducerListView);
//                    mIntroducerAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Failed to fetch introducer list", Toast.LENGTH_SHORT).show();
            }
        }


        if (mGetProfileInfoTask == null && mGetUserAddressTask == null && mGetIdentificationDocumentsTask == null) {
            mProgressDialog.dismiss();

//            mScrollView.post(new Runnable() {
//                @Override
//                public void run() {
//                    mScrollView.smoothScrollTo(0, 0);
//                }
//            });
        }
    }

    public class IntroducerListAdapter extends ArrayAdapter<Introducer> {

        private LayoutInflater inflater;

        public IntroducerListAdapter(Context context, List<Introducer> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setIntroducers(List<Introducer> introducers) {
            clear();
            addAll(introducers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Introducer introducer = getItem(position);

            View view = convertView;
            if (view == null)
                view = inflater.inflate(R.layout.list_item_introducer, null);

            TextView nameView = (TextView) view.findViewById(R.id.textview_name);
            TextView mobileNumberView = (TextView) view.findViewById(R.id.textview_mobile_number);

            nameView.setText(introducer.getName());
            mobileNumberView.setText(introducer.getMobileNumber());

            return view;
        }
    }
}
