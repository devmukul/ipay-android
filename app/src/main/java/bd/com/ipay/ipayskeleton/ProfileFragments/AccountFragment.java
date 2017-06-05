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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.DocumentUploadApi.UploadProfilePictureAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ProfilePictureHelperDialog;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.SetProfilePictureResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionStatusResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.FCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefConstants;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AccountFragment extends Fragment implements HttpResponseListener {

    private static final int REQUEST_CODE_PERMISSION = 1001;
    private final int ACTION_PICK_PROFILE_PICTURE = 100;

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mProfileCompletionStatusView;
    private ImageView mVerificationStatusView;

    private View mProfilePictureHolderView;
    private ImageView mEditProfilePicButton;

    private IconifiedTextViewWithButton mBasicInfo;
    private IconifiedTextViewWithButton mEmail;
    private IconifiedTextViewWithButton mDocuments;
    private IconifiedTextViewWithButton mIntroducer;
    private IconifiedTextViewWithButton mAddress;
    private IconifiedTextViewWithButton mProfileCompleteness;
    private IconifiedTextViewWithButton mManageEmployee;

    private String mName = "";
    private String mMobileNumber = "";
    private String mProfilePicture = "";
    private String mSelectedImagePath = "";

    private List<String> mOptionsForImageSelectionList;
    private int mSelectedOptionForImage = -1;

    private UploadProfilePictureAsyncTask mUploadProfilePictureAsyncTask = null;
    private SetProfilePictureResponse mSetProfilePictureResponse;

    private HttpRequestGetAsyncTask mGetProfileCompletionStatusTask = null;
    private ProfileCompletionStatusResponse mProfileCompletionStatusResponse;

    private ProgressDialog mProgressDialog;
    private MaterialDialog.Builder mProfilePictureErrorDialogBuilder;
    private MaterialDialog mProfilePictureErrorDialog;
    private ProfilePictureHelperDialog profilePictureHelperDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        getActivity().setTitle(R.string.account);

        mProfilePictureView = (ProfileImageView) view.findViewById(R.id.profile_picture);
        mNameView = (TextView) view.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) view.findViewById(R.id.textview_mobile_number);
        mProfileCompletionStatusView = (TextView) view.findViewById(R.id.textview_profile_completion_status);
        mVerificationStatusView = (ImageView) view.findViewById(R.id.textview_verification_status);
        mEditProfilePicButton = (ImageView) view.findViewById(R.id.button_profile_picture_edit);
        mProfilePictureHolderView = view.findViewById(R.id.profile_picture_layout);

        mBasicInfo = (IconifiedTextViewWithButton) view.findViewById(R.id.basic_info);
        mEmail = (IconifiedTextViewWithButton) view.findViewById(R.id.email);
        mAddress = (IconifiedTextViewWithButton) view.findViewById(R.id.present_address);
        mIntroducer = (IconifiedTextViewWithButton) view.findViewById(R.id.introducer);
        mDocuments = (IconifiedTextViewWithButton) view.findViewById(R.id.documents);
        mProfileCompleteness = (IconifiedTextViewWithButton) view.findViewById(R.id.profile_completion);
        mManageEmployee = (IconifiedTextViewWithButton) view.findViewById(R.id.manage_employees);

        mProgressDialog = new ProgressDialog(getActivity());

        mName = ProfileInfoCacheManager.getUserName();
        mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        mProfilePicture = ProfileInfoCacheManager.getProfileImageUrl();

        mOptionsForImageSelectionList = Arrays.asList(getResources().getStringArray(R.array.upload_picker_action));

        setProfileInformation();
        setVisibilityOfProfilePicUploadButton();
        initProfilePicHelperDialog();
        setButtonActions();
        getProfileCompletionStatus();

        return view;
    }

    private void setVisibilityOfProfilePicUploadButton() {
        if (ProfileInfoCacheManager.isAccountVerified())
            mEditProfilePicButton.setVisibility(View.GONE);
        else
            mEditProfilePicButton.setVisibility(View.VISIBLE);
    }

    private void setButtonActions() {
        mProfilePictureHolderView.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.MANAGE_PROFILE_PICTURE)
            public void onClick(View v) {
                if (!ProfileInfoCacheManager.isAccountVerified()) {
                    profilePictureHelperDialog.show();
                } else
                    showProfilePictureUpdateRestrictionDialog();
            }
        });

        mBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProfileInfoCacheManager.isBusinessAccount()) {
                    if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_INFO)) {
                        DialogUtils.showServiceNotAllowedDialog(getContext());
                    } else {
                        ((ProfileActivity) getActivity()).switchToBusinessInfoFragment();
                    }
                } else {
                    if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_USER_INFO) && !ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_PARENT)) {
                        DialogUtils.showServiceNotAllowedDialog(getContext());
                    } else {
                        ((ProfileActivity) getActivity()).switchToBasicInfoFragment();
                    }
                }
            }
        });

        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEE_EMAILS)
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToEmailFragment();
            }
        });

        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEE_ADDRESSES)
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToAddressFragment();
            }
        });

        mIntroducer.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEE_INTRODUCERS)
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToIntroducerFragment();
            }
        });

        mDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProfileInfoCacheManager.isBusinessAccount()) {
                    if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_DOCS)) {
                        DialogUtils.showServiceNotAllowedDialog(getContext());
                        return;
                    }
                } else {
                    if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_IDENTIFICATION_DOCS)) {
                        DialogUtils.showServiceNotAllowedDialog(getContext());
                        return;
                    }
                }
                ((ProfileActivity) getActivity()).switchToIdentificationDocumentListFragment();
            }
        });

        mProfileCompleteness.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEE_PROFILE_COMPLETION)
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToProfileCompletionFragment();
            }
        });

        mManageEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess({ServiceIdConstants.SEE_EMPLOYEE, ServiceIdConstants.MANAGE_EMPLOYEE})
            public void onClick(View v) {
                //((ProfileActivity) getActivity()).switchToEmployeeManagementFragment();
                Intent intent = new Intent(getActivity(), ManagePeopleActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showProfilePictureUpdateRestrictionDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .content(R.string.can_not_change_picture)
                .cancelable(false)
                .positiveText(R.string.got_it)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void initProfilePicHelperDialog() {
        if (!ProfileInfoCacheManager.isAccountVerified()) {
            profilePictureHelperDialog = new ProfilePictureHelperDialog(getActivity(), getString(R.string.select_an_image), mOptionsForImageSelectionList);
            profilePictureHelperDialog.setOnResourceSelectedListener(new ProfilePictureHelperDialog.OnResourceSelectedListener() {
                @Override
                public void onResourceSelected(int mActionId, String action) {
                    if (DocumentPicker.ifNecessaryPermissionExists(getActivity())) {
                        selectProfilePictureIntent(mActionId);
                    } else {
                        mSelectedOptionForImage = mActionId;
                        DocumentPicker.requestRequiredPermissions(AccountFragment.this, REQUEST_CODE_PERMISSION);
                    }
                }
            });
        }
    }

    private void selectProfilePictureIntent(int id) {
        Intent imagePickerIntent = DocumentPicker.getPickerIntentByID(getActivity(), getString(R.string.select_a_document), id);
        startActivityForResult(imagePickerIntent, ACTION_PICK_PROFILE_PICTURE);
    }

    private boolean isSelectedProfileValid(Uri uri) {
        String selectedImagePath = uri.getPath();
        String result = null;

        try {
            result = Utilities.validateProfilePicture(getActivity(), selectedImagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            return true;
        } else {
            String content = "";
            switch (result) {
                case Constants.NO_FACE_DETECTED:
                    content = getString(R.string.no_face_detected);
                    break;
                case Constants.MULTIPLE_FACES:
                    content = getString(R.string.multiple_face_detected);
                    break;
                case Constants.NOT_AN_IMAGE:
                    content = getString(R.string.not_an_image);
                    break;
                default:
                    content = getString(R.string.default_profile_pic_inappropriate_message);
                    break;
            }

            showProfilePictureErrorDialog(content);
            return false;
        }
    }

    private void showProfilePictureErrorDialog(String content) {
        mProfilePictureErrorDialogBuilder = new MaterialDialog.Builder(getActivity())
                .title(R.string.attention)
                .content(content)
                .cancelable(true)
                .positiveText(R.string.take_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        profilePictureHelperDialog.show();
                    }
                });
        mProfilePictureErrorDialog = mProfilePictureErrorDialogBuilder.build();
        mProfilePictureErrorDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (DocumentPicker.ifNecessaryPermissionExists(getActivity())) {
                    selectProfilePictureIntent(mSelectedOptionForImage);
                } else {
                    Toast.makeText(getActivity(), R.string.prompt_grant_permission, Toast.LENGTH_LONG).show();
                }
        }
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
                        // Check for a valid profile picture
                        // To remove the face detection feature just remove the if condition
                        /*
                        // ** Removed face detection for now. Will be added later.
                        if (isSelectedProfileValid(uri)) {
                            mProfilePictureView.setProfilePicture(uri.getPath(), true);
                            updateProfilePicture(uri);
                        }
                        */

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
        Logger.logD("Profile Pic Account", mProfilePicture);
        mMobileNumberView.setText(mMobileNumber);
        mNameView.setText(mName);
        mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER +
                mProfilePicture, false);

        if (ProfileInfoCacheManager.isAccountVerified()) {
            mVerificationStatusView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_verified_profile));
        } else {
            mVerificationStatusView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_not_verified));
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

    public void httpResponseReceiver(GenericHttpResponse result) {
        if (getActivity() != null) {
            mProgressDialog.dismiss();
        }

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mUploadProfilePictureAsyncTask = null;
            mGetProfileCompletionStatusTask = null;
            if (getActivity() != null) {
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
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
                    PushNotificationStatusHolder.setUpdateNeeded(SharedPrefConstants.PUSH_NOTIFICATION_TAG_PROFILE_PICTURE, true);

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
                    Toaster.makeText(getActivity(), R.string.profile_picture_set_failed, Toast.LENGTH_SHORT);
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
                                + "complete.\nSubmit documents and other information to improve your profile.");
                        mProfileCompletionStatusView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mProfileCompletionStatusResponse.getMessage(), Toast.LENGTH_LONG);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.failed_fetching_profile_completion_status, Toast.LENGTH_LONG);
            }

            mGetProfileCompletionStatusTask = null;
        }
    }

}
