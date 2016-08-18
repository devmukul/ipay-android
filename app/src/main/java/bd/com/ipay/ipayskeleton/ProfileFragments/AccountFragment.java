package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Api.UploadProfilePictureAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.SetProfilePictureResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionStatusResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;

public class AccountFragment extends Fragment implements HttpResponseListener {

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mProfileCompletionStatusView;
    private ImageView mVerificationStatusView;
    private View mDividerMAnageEmployee;
    private View mDividerPresentAddress;
    private String mName = "";
    private String mMobileNumber = "";
    private String mProfilePicture = "";

    private String mSelectedImagePath = "";

    private IconifiedTextViewWithButton mBasicInfo;
    private IconifiedTextViewWithButton mEmail;
    private IconifiedTextViewWithButton mDocuments;
    private IconifiedTextViewWithButton mIntroducer;
    private IconifiedTextViewWithButton mAddress;
    private IconifiedTextViewWithButton mProfileCompleteness;
    private IconifiedTextViewWithButton mManageEmployee;
    private UploadProfilePictureAsyncTask mUploadProfilePictureAsyncTask = null;
    private SetProfilePictureResponse mSetProfilePictureResponse;

    private HttpRequestGetAsyncTask mGetProfileCompletionStatusTask = null;
    private ProfileCompletionStatusResponse mProfileCompletionStatusResponse;

    private ProgressDialog mProgressDialog;

    private static final int REQUEST_CODE_PERMISSION = 1001;
    private final int ACTION_PICK_PROFILE_PICTURE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        getActivity().setTitle(R.string.account);

        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mProfileCompletionStatusView = (TextView) v.findViewById(R.id.textview_profile_completion_status);
        mVerificationStatusView = (ImageView) v.findViewById(R.id.textview_verification_status);
        mDividerMAnageEmployee = v.findViewById(R.id.divider_manage_employee);
        mDividerPresentAddress = v.findViewById(R.id.divider_present_address);

        mBasicInfo = (IconifiedTextViewWithButton) v.findViewById(R.id.basic_info);
        mEmail = (IconifiedTextViewWithButton) v.findViewById(R.id.email);
        mAddress = (IconifiedTextViewWithButton) v.findViewById(R.id.present_address);
        mIntroducer = (IconifiedTextViewWithButton) v.findViewById(R.id.introducer);
        mDocuments = (IconifiedTextViewWithButton) v.findViewById(R.id.documents);
        mProfileCompleteness = (IconifiedTextViewWithButton) v.findViewById(R.id.profile_completion);
        mManageEmployee = (IconifiedTextViewWithButton) v.findViewById(R.id.manage_employees);


        if (ProfileInfoCacheManager.isBusinessAccount()) {
            mManageEmployee.setVisibility(View.VISIBLE);
            mDividerMAnageEmployee.setVisibility(View.VISIBLE);
        } else {
            mManageEmployee.setVisibility(View.GONE);
            mDividerMAnageEmployee.setVisibility(View.GONE);
        }

        mProgressDialog = new ProgressDialog(getActivity());

        mName = ProfileInfoCacheManager.getName();
        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        mProfilePicture = ProfileInfoCacheManager.getProfileImageUrl();

        setProfileInformation();

        if (!ProfileInfoCacheManager.isAccountVerified()) {
            mProfilePictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DocumentPicker.ifNecessaryPermissionExists(getActivity())) {
                        Intent imageChooserIntent = DocumentPicker.getPickImageIntent(getContext(), getString(R.string.select_an_image));
                        startActivityForResult(imageChooserIntent, ACTION_PICK_PROFILE_PICTURE);
                    } else {
                        DocumentPicker.requestRequiredPermissions(AccountFragment.this, REQUEST_CODE_PERMISSION);
                    }
                }
            });
        } else {
            mProfilePictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), R.string.can_not_change_picture, Toast.LENGTH_LONG).show();
                }
            });
        }

        mBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProfileInfoCacheManager.isBusinessAccount())
                    ((ProfileActivity) getActivity()).switchToBusinessBasicInfoHolderFragment();
                else ((ProfileActivity) getActivity()).switchToBasicInfoFragment();
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

        mManageEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((ProfileActivity) getActivity()).switchToEmployeeManagementFragment();
                Intent intent = new Intent(getActivity(), ManagePeopleActivity.class);
                startActivity(intent);
            }
        });

        getProfileCompletionStatus();

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (DocumentPicker.ifNecessaryPermissionExists(getActivity())) {
                    Intent imageChooserIntent = DocumentPicker.getPickImageIntent(getContext(), getString(R.string.select_an_image));
                    startActivityForResult(imageChooserIntent, ACTION_PICK_PROFILE_PICTURE);
                } else {
                    Toast.makeText(getActivity(), R.string.prompt_grant_permission, Toast.LENGTH_LONG).show();
                }
        }
    }

    private void getProfileCompletionStatus() {
        if (mGetProfileCompletionStatusTask != null) {
            return;
        }

        mGetProfileCompletionStatusTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS,
                Constants.BASE_URL_MM + Constants.URL_GET_PROFILE_COMPLETION_STATUS, getActivity(), this);
        mGetProfileCompletionStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void updateProfilePicture(Uri selectedImageUri) {
        mProgressDialog.setMessage(getString(R.string.uploading_profile_picture));
        mProgressDialog.show();

        mSelectedImagePath = selectedImageUri.getPath();

        mUploadProfilePictureAsyncTask = new UploadProfilePictureAsyncTask(Constants.COMMAND_SET_PROFILE_PICTURE,
                mSelectedImagePath, getActivity());
        mUploadProfilePictureAsyncTask.mHttpResponseListener = this;
        mUploadProfilePictureAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_PICK_PROFILE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, data);
                    if (uri == null) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(),
                                    R.string.could_not_load_image,
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        mProfilePictureView.setProfilePicture(uri.getPath(), true);
                        updateProfilePicture(uri);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setProfileInformation() {
        Log.d("Profile Pic Account", mProfilePicture);
        mMobileNumberView.setText(mMobileNumber);
        mNameView.setText(mName);
        mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER +
                mProfilePicture, false);

        if (ProfileInfoCacheManager.isAccountVerified()) {
            mVerificationStatusView.setVisibility(View.VISIBLE);
        } else {
            mVerificationStatusView.setVisibility(View.GONE);
        }
    }

    public void httpResponseReceiver(HttpResponseObject result) {
        if (getActivity() != null) {
            mProgressDialog.dismiss();
        }

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mUploadProfilePictureAsyncTask = null;
            mGetProfileCompletionStatusTask = null;
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_PROFILE_PICTURE)) {
            try {
                mSetProfilePictureResponse = gson.fromJson(result.getJsonString(), SetProfilePictureResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetProfilePictureResponse.getMessage(), Toast.LENGTH_LONG).show();

                    getProfileCompletionStatus();
                    PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, true);

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
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS)) {
            try {
                mProfileCompletionStatusResponse = gson.fromJson(result.getJsonString(), ProfileCompletionStatusResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    Intent intent = new Intent(Constants.PROFILE_COMPLETION_UPDATE_BROADCAST);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                    if (!mProfileCompletionStatusResponse.isCompletedMandetoryFields()) {
                        mProfileCompletionStatusView.setText("Your profile is " +
                                mProfileCompletionStatusResponse.getCompletionPercentage() + "% "
                                + "complete. Complete profile to get verified.");
                        mProfileCompletionStatusView.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mProfileCompletionStatusResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_fetching_profile_completion_status, Toast.LENGTH_LONG).show();
            }

            mGetProfileCompletionStatusTask = null;
        }
    }

}
