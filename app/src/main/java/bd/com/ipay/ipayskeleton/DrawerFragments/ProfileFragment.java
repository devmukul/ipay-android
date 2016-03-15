package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetProfileInfoRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ProfileFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private ProgressDialog mProgressDialog;

    private RoundedImageView mProfilePicture;
    private TextView mNameView;
    private TextView mMobileNumberView;

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

    private Set<UserProfilePictureClass> profilePictures;

    private SharedPreferences pref;

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

        profilePictures = new HashSet<>();

        mProfilePicture = (RoundedImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);

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
        mPresentAddressView = (Button) v.findViewById(R.id.button_edit_present_address);
        mPermanentAddressView = (Button) v.findViewById(R.id.button_edit_permanent_address);
        mOfficeAddressEditButton = (Button) v.findViewById(R.id.button_edit_office_address);
        mUploadDocumentsButton = (Button) v.findViewById(R.id.button_upload_documents);

        mMobileNumberView.setText(pref.getString(Constants.USERID, ""));
        mGenderView.setText(pref.getString(Constants.GENDER, ""));
        mDateOfBirthView.setText(pref.getString(Constants.BIRTHDAY, ""));

        mProgressDialog = new ProgressDialog(getActivity());

        setProfilePicture("");
        getProfileInfo();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.fetching_profile_information));
        mProgressDialog.show();

        GetProfileInfoRequest mGetProfileInfoRequest = new GetProfileInfoRequest();
        Gson gson = new Gson();
        String json = gson.toJson(mGetProfileInfoRequest);
        mGetProfileInfoTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL_POST_MM + Constants.URL_GET_PROFILE_INFO_REQUEST, json, getActivity());
        mGetProfileInfoTask.mHttpResponseListener = this;
        mGetProfileInfoTask.execute();
    }

    private void setProfileInformation() {
        if (profilePictures.size() > 0) {

            String imageUrl = "";
            for (Iterator<UserProfilePictureClass> it = profilePictures.iterator(); it.hasNext(); ) {
                UserProfilePictureClass userProfilePictureClass = it.next();
                imageUrl = userProfilePictureClass.getUrl();
                break;
            }
            setProfilePicture(imageUrl);
        }

    }

    private void setProfilePicture(String url) {
        try {
            if (!url.equals("")) {
                Glide.with(getActivity())
                        .load(Constants.BASE_URL_IMAGE_SERVER + url)
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
                        mNameView.setText(mGetProfileInfoResponse.getName());
                    if (mGetProfileInfoResponse.getMobileNumber() != null)
                        mMobileNumberView.setText(mGetProfileInfoResponse.getMobileNumber());
                    if (mGetProfileInfoResponse.getEmail() != null)
                        mEmailAddressView.setText(mGetProfileInfoResponse.getEmail());
                    if (mGetProfileInfoResponse.getFather() != null)
                        mFathersNameView.setText(mGetProfileInfoResponse.getFather());
                    if (mGetProfileInfoResponse.getMother() != null)
                        mMothersNameView.setText(mGetProfileInfoResponse.getMother());
                    if (mGetProfileInfoResponse.getSpouse() != null)
                        mSpouseNameView.setText(mGetProfileInfoResponse.getSpouse());
                    if (mGetProfileInfoResponse.getOccupation() != null)
                        mOccupationView.setText(mGetProfileInfoResponse.getOccupation());
                    if (mGetProfileInfoResponse.getGender() != null)
                        mGenderView.setText(mGetProfileInfoResponse.getGender());
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();
            mGetProfileInfoTask = null;

        }
    }

}
