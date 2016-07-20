package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Api.UploadProfilePictureAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.SetProfilePictureResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;

public class AccountFragment extends Fragment implements HttpResponseListener {

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private ImageView mVerificationStatusView;
    private final int ACTION_PICK_PROFILE_PICTURE = 100;
    private String mName = "";
    private String mMobileNumber = "";
    private String mProfilePicture = "";
    private String mVerificationStatus = null;

    private String mSelectedImagePath;

    private IconifiedTextViewWithButton mBasicInfo;
    private IconifiedTextViewWithButton mEmail;
    private IconifiedTextViewWithButton mDocuments;
    private IconifiedTextViewWithButton mIntroducer;
    private IconifiedTextViewWithButton mAddress;
    private IconifiedTextViewWithButton mProfileCompleteness;
    private UploadProfilePictureAsyncTask mUploadProfilePictureAsyncTask = null;
    private SetProfilePictureResponse mSetProfilePictureResponse;


    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        getActivity().setTitle(R.string.account);

        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mVerificationStatusView = (ImageView) v.findViewById(R.id.textview_verification_status);

        mBasicInfo = (IconifiedTextViewWithButton) v.findViewById(R.id.basic_info);
        mEmail = (IconifiedTextViewWithButton) v.findViewById(R.id.email);
        mAddress = (IconifiedTextViewWithButton) v.findViewById(R.id.present_address);
        mIntroducer = (IconifiedTextViewWithButton) v.findViewById(R.id.introducer);
        mDocuments = (IconifiedTextViewWithButton) v.findViewById(R.id.documents);
        mProfileCompleteness = (IconifiedTextViewWithButton) v.findViewById(R.id.profile_completion);

        ProfileInfoCacheManager profileInfoCacheManager = new ProfileInfoCacheManager(getActivity());

        mName = profileInfoCacheManager.getName();
        mMobileNumber = profileInfoCacheManager.getMobileNumber();
        mProfilePicture = profileInfoCacheManager.getProfileImageUrl();
        mVerificationStatus = profileInfoCacheManager.getVerificationStatus();

        setProfileInformation();

        mProfilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageChooserIntent = DocumentPicker.getPickImageIntent(getActivity(), getString(R.string.select_an_image));
                startActivityForResult(imageChooserIntent, ACTION_PICK_PROFILE_PICTURE);
            }
        });

        mBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToBasicInfoFragment();
            }
        });

        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToEmailFragment();
            }
        });

        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToAddressFragment();
            }
        });

        mIntroducer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToIntroducerFragment();
            }
        });

        mDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToIdentificationDocumentListFragment();
            }
        });

        mProfileCompleteness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToProfileCompletionFragment();
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProfilePictureUpdateBroadcastReceiver,
                new IntentFilter(Constants.PROFILE_PICTURE_UPDATE_BROADCAST));

        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ACTION_PICK_PROFILE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, intent);
                    if (uri == null) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(),
                                    R.string.could_not_load_image,
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        setProfilePicture(uri.toString());
                        updateProfilePicture(uri);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void setProfilePicture(String url) {
        mProfilePictureView.setProfilePicture(mProfilePicture, false);

    }

    public void updateProfilePicture(Uri selectedImageUri) {
        mProgressDialog.setMessage(getString(R.string.uploading_profile_picture));
        mProgressDialog.show();

        mSelectedImagePath = selectedImageUri.getPath();

        mUploadProfilePictureAsyncTask = new UploadProfilePictureAsyncTask(Constants.COMMAND_SET_PROFILE_PICTURE,
                mSelectedImagePath, getActivity());
        mUploadProfilePictureAsyncTask.mHttpResponseListener = this;
        mUploadProfilePictureAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void setProfileInformation() {
        Log.d("Profile Pic", mProfilePicture);
        mMobileNumberView.setText(mMobileNumber);
        mNameView.setText(mName);
        setProfilePicture(mProfilePicture);

        if (mVerificationStatus != null) {
            if (mVerificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                mVerificationStatusView.setVisibility(View.VISIBLE);
            } else {
                mVerificationStatusView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        Log.d("Broadcast receiver", "unregister receiver");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProfilePictureUpdateBroadcastReceiver);

        super.onDestroyView();
    }

    private BroadcastReceiver mProfilePictureUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newProfilePicture = intent.getStringExtra(Constants.PROFILE_PICTURE);
            Log.d("Broadcast received", newProfilePicture);
            mProfilePictureView.setProfilePicture(newProfilePicture, true);
        }
    };


    public void httpResponseReceiver(HttpResponseObject result) {
        mProgressDialog.dismiss();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mUploadProfilePictureAsyncTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_PROFILE_PICTURE)) {
            try {
                mSetProfilePictureResponse = gson.fromJson(result.getJsonString(), SetProfilePictureResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetProfilePictureResponse.getMessage(), Toast.LENGTH_LONG).show();

                    PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
                    pushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, true);

                    Intent intent = new Intent(Constants.PROFILE_PICTURE_UPDATE_BROADCAST);
                    intent.putExtra(Constants.PROFILE_PICTURE, mSelectedImagePath);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetProfilePictureResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_picture_set_failed, Toast.LENGTH_SHORT).show();
            }

            mUploadProfilePictureAsyncTask = null;
        }
    }

}
