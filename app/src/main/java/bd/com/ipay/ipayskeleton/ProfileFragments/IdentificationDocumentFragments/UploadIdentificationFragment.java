package bd.com.ipay.ipayskeleton.ProfileFragments.IdentificationDocumentFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomUploadPickerDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.IdentificationDocument;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;
import bd.com.ipay.ipayskeleton.Utilities.IdentificationDocumentConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import bd.com.ipay.ipayskeleton.camera.CameraActivity;

public class UploadIdentificationFragment extends BaseFragment {

    private static final int ACTION_UPLOAD_DOCUMENT = 100;
    private static final int REQUEST_CODE_PERMISSION = 1001;

    private IdentificationDocument mSelectedIdentificationDocument;

    private EditText documentNameEditText;
    private EditText documentIdEditText;

    private View documentBackSideUploadOptionViewHolder;

    private ImageView mDocumentFrontSideImageView;
    private ImageView mDocumentBackSideImageView;

    private int maxDocumentSideCount;
    private boolean mIsOtherTypeDocument;
    private String mDocumentIdEditTextHint;

    private int mPickerActionId;
    private String mSelectedDocumentSide;

    private Uri mDocumentFirstPageImageUri;
    private Uri mDocumentSecondPageImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSelectedIdentificationDocument = getArguments().getParcelable(Constants.SELECTED_IDENTIFICATION_DOCUMENT);
            if (mSelectedIdentificationDocument != null) {
                maxDocumentSideCount = IdentificationDocumentConstants.DOCUMENT_ID_MAX_PAGE_COUNT_MAP.get(mSelectedIdentificationDocument.getDocumentType());
                mIsOtherTypeDocument = mSelectedIdentificationDocument.getDocumentType().equals(IdentificationDocumentConstants.DOCUMENT_TYPE_OTHER);
                mDocumentIdEditTextHint = getString(IdentificationDocumentConstants.DOCUMENT_ID_TO_EDIT_TEXT_HINT_MAP.get(mSelectedIdentificationDocument.getDocumentType()));
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_identification_document, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.upload_document);

        final View documentNameViewHolder = findViewById(R.id.document_name_view_holder);
        documentNameEditText = findViewById(R.id.document_name_edit_text);

        if (mIsOtherTypeDocument) {
            documentNameViewHolder.setVisibility(View.VISIBLE);
        } else {
            documentNameViewHolder.setVisibility(View.GONE);
        }

        documentIdEditText = findViewById(R.id.document_id_edit_text);

        final TextInputLayout documentIdTextInputLayout = findViewById(R.id.document_id_text_input_layout);

        if (!TextUtils.isEmpty(mDocumentIdEditTextHint))
            documentIdTextInputLayout.setHint(mDocumentIdEditTextHint);
        if (mSelectedIdentificationDocument != null) {
            if (!TextUtils.isEmpty(mSelectedIdentificationDocument.getDocumentIdNumber())) {
                documentIdEditText.setText(mSelectedIdentificationDocument.getDocumentIdNumber());
            }
        }

        mDocumentFrontSideImageView = findViewById(R.id.document_front_side_image_view);

        final DocumentChooserButtonClickListener documentChooserButtonClickListener = new DocumentChooserButtonClickListener();
        final Button documentFrontSideSelectorButton = findViewById(R.id.document_front_side_selector_button);

        documentFrontSideSelectorButton.setOnClickListener(documentChooserButtonClickListener);

        documentBackSideUploadOptionViewHolder = findViewById(R.id.document_back_side_upload_option_view_holder);

        documentBackSideUploadOptionViewHolder.setVisibility(View.GONE);
        if (maxDocumentSideCount >= 2) {
            final Button documentBackSideSelectorButton = findViewById(R.id.document_back_side_selector_button);
            mDocumentBackSideImageView = findViewById(R.id.document_back_side_image_view);
            documentBackSideSelectorButton.setOnClickListener(documentChooserButtonClickListener);
        }

        final Button uploadButton = findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ACTION_UPLOAD_DOCUMENT:
                if (resultCode == Activity.RESULT_OK) {
                    String filePath = DocumentPicker.getFilePathFromResult(getActivity(), intent);

                    if (filePath != null) {
                        String[] temp = filePath.split(File.separator);
                        final String mFileName = temp[temp.length - 1];
                        Uri mSelectedDocumentUri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, intent, mFileName);
                        final File imageFile = new File(mSelectedDocumentUri.getPath());

                        final Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        if (mSelectedDocumentSide.equals(IdentificationDocumentConstants.DOCUMENT_SIDE_FRONT)) {
                            mDocumentFrontSideImageView.setImageBitmap(imageBitmap);
                            if (maxDocumentSideCount > 1) {
                                documentBackSideUploadOptionViewHolder.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (mDocumentBackSideImageView != null) {
                                mDocumentBackSideImageView.setImageBitmap(imageBitmap);
                            }
                        }
                        mPickerActionId = -1;
                        mSelectedDocumentSide = "";
                    }
                } else if (resultCode == CameraActivity.CAMERA_ACTIVITY_CRASHED) {
                    Intent systemCameraOpenIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    systemCameraOpenIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, DocumentPicker.getTempFile(getActivity(), "document_" + mSelectedDocumentSide + ".jpg")));
                    startActivityForResult(systemCameraOpenIntent, ACTION_UPLOAD_DOCUMENT);
                } else {
                    mPickerActionId = -1;
                    mSelectedDocumentSide = "";
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (Utilities.isNecessaryPermissionExists(getActivity(), DocumentPicker.DOCUMENT_PICK_PERMISSIONS))
                    selectDocument(mPickerActionId, mSelectedDocumentSide);
                else
                    Toast.makeText(getActivity(), R.string.prompt_grant_permission, Toast.LENGTH_LONG).show();
        }
    }

    private void selectDocument(int actionId, String selectedDocumentSide) {
        mPickerActionId = actionId;
        mSelectedDocumentSide = selectedDocumentSide;
        Intent imagePickerIntent = DocumentPicker.getPickerIntentByID(getActivity(), getString(R.string.select_a_document), actionId, Constants.CAMERA_REAR, "document_" + selectedDocumentSide + ".jpg");
        startActivityForResult(imagePickerIntent, ACTION_UPLOAD_DOCUMENT);
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(id);
    }

    class DocumentChooserButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            final String documentSide;
            switch (view.getId()) {
                case R.id.document_back_side_selector_button:
                    documentSide = IdentificationDocumentConstants.DOCUMENT_SIDE_BACK;
                    break;
                case R.id.document_front_side_selector_button:
                default:
                    documentSide = IdentificationDocumentConstants.DOCUMENT_SIDE_FRONT;
                    break;

            }
            if (isAdded()) {
                CustomUploadPickerDialog customUploadPickerDialog = new CustomUploadPickerDialog(getActivity(),
                        getActivity().getString(R.string.select_a_document),
                        Arrays.asList(getResources().getStringArray(R.array.upload_picker_action)));
                customUploadPickerDialog.setOnResourceSelectedListener(new CustomUploadPickerDialog.OnResourceSelectedListener() {
                    @Override
                    public void onResourceSelected(int actionId, String action) {
                        if (Constants.ACTION_TYPE_TAKE_PICTURE.equals(action) || Constants.ACTION_TYPE_SELECT_FROM_GALLERY.equals(action))
                            if (Utilities.isNecessaryPermissionExists(getActivity(), DocumentPicker.DOCUMENT_PICK_PERMISSIONS))
                                selectDocument(actionId, documentSide);
                            else {
                                mPickerActionId = actionId;
                                mSelectedDocumentSide = documentSide;
                                Utilities.requestRequiredPermissions(UploadIdentificationFragment.this, REQUEST_CODE_PERMISSION, DocumentPicker.DOCUMENT_PICK_PERMISSIONS);
                            }
                    }
                });
                customUploadPickerDialog.show();
            }
        }
    }
}
