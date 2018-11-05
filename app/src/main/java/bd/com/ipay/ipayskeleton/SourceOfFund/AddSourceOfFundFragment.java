package bd.com.ipay.ipayskeleton.SourceOfFund;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.GenericResponseWithMessageOnly;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.AddSponsorRequest;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Widgets.IPaySnackbar;

public class AddSourceOfFundFragment extends Fragment implements HttpResponseListener {

    private String mName;
    private String mMobileNumber;
    private String mProfileImageUrl;

    private EditText mNumberEditText;
    private ImageView mContactImageView;
    private TextView mNameTextView;
    private RoundedImageView profileImageView;
    private ResourceSelectorDialog resourceSelectorDialog;
    private String relationShip;
    private EditText relationShipEditText;

    private Button doneButton;

    private boolean isSelectedFromContact;

    private IpayProgressDialog ipayProgressDialog;

    private final int PICK_CONTACT_REQUEST = 100;
    private HttpRequestGetAsyncTask mGetProfileInfoTask;
    private HttpRequestPostAsyncTask mAddSponsorAsyncTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isSelectedFromContact = false;
        ipayProgressDialog = new IpayProgressDialog(getContext());
        return inflater.inflate(R.layout.fragment_add_source_of_fund, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNumberEditText = view.findViewById(R.id.number_edit_text);
        mNameTextView = view.findViewById(R.id.name);
        doneButton = view.findViewById(R.id.done);
        profileImageView = view.findViewById(R.id.profile_picture);
        relationShipEditText = view.findViewById(R.id.relationship_edit_text);
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        relationShipEditText.setFocusable(false);
        relationShipEditText.setClickable(true);
        relationShipEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resourceSelectorDialog.show();
            }
        });
        resourceSelectorDialog = new ResourceSelectorDialog(getActivity(),
                getString(R.string.relationship), CommonData.getRelationshipList());
        resourceSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int selectedIndex, String mRelation) {
                relationShipEditText.setError(null);
                relationShipEditText.setText(mRelation);
                relationShip = mRelation;
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relationShip == null || relationShip.equals("")) {
                    showErrorMessage("Please select a relationship");
                } else {
                    mMobileNumber = mNumberEditText.getText().toString();
                    mMobileNumber = mMobileNumber.replaceAll("[^0-9.]", "");

                    if (!InputValidator.isValidNumber(mMobileNumber)) {
                        showErrorMessage("Please enter a valid mobile number");
                    } else {
                        if (isSelectedFromContact) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                            bundle.putString(Constants.NAME, mName);
                            bundle.putString(Constants.RELATION, relationShip);
                            bundle.putString(Constants.PROFILE_PICTURE, mProfileImageUrl);
                            ((SourceOfFundActivity) getActivity()).switchToAddSourceOfFundConfirmFragment(bundle);
                        } else {
                            ContactEngine.ContactData contactData = searchLocalContacts(ContactEngine.formatMobileNumberBD(mMobileNumber));
                            if (contactData == null) {
                                getProfileInfo();
                            } else {
                                mProfileImageUrl = contactData.photoUri;
                                mName = contactData.name;
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                                bundle.putString(Constants.NAME, mName);
                                bundle.putString(Constants.RELATION, relationShip);
                                bundle.putString(Constants.PROFILE_PICTURE, mProfileImageUrl);
                                ((SourceOfFundActivity) getActivity()).switchToAddSourceOfFundConfirmFragment(bundle);
                            }

                        }
                    }

                }
            }
        });

        mContactImageView = view.findViewById(R.id.contact_image_view);
        profileImageView = (RoundedImageView) view.findViewById(R.id.profile_image);
        mContactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                intent.putExtra(Constants.VERIFIED_USERS_ONLY, true);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });
        mNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mNumberEditText.getText().toString().equals("+880-1")) {
                        mNumberEditText.setSelection(6);
                    } else {
                        Selection.setSelection(mNumberEditText.getText(), mNumberEditText.getText().length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isSelectedFromContact = false;
                if (s.toString().length() < 6) {
                    mNumberEditText.setText("+880-1");
                }
                if (s.toString().length() < 15) {
                    mName = "";
                    mMobileNumber = "";
                    mProfileImageUrl = "";

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mNumberEditText.getText().toString().equals("+880-1")) {
                    mNumberEditText.setSelection(6);
                } else {
                    mNumberEditText.setSelection(mNumberEditText.getText().length());
                }

            }
        });
    }

    public void attemptAddSponsor() {
        if (mAddSponsorAsyncTask != null) {
            return;
        } else {
            AddSponsorRequest addSponsorRequest = new AddSponsorRequest(ContactEngine.formatMobileNumberBD(mMobileNumber), relationShip);
            mAddSponsorAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_SPONSOR
                    , Constants.BASE_URL_MM + Constants.URL_ADD_SPONSOR,
                    new Gson().toJson(addSponsorRequest), getContext(), this, false);
            ipayProgressDialog.setMessage("Please wait . . . ");
            ipayProgressDialog.show();
            mAddSponsorAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    protected void showErrorMessage(String errorMessage) {
        if (!TextUtils.isEmpty(errorMessage) && getActivity() != null) {
            IPaySnackbar.error(doneButton, errorMessage, IPaySnackbar.LENGTH_LONG).show();
        }
    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }
        ipayProgressDialog.setMessage("Please wait . . .");
        ipayProgressDialog.show();
        GetUserInfoRequestBuilder getUserInfoRequestBuilder = new GetUserInfoRequestBuilder(ContactEngine.
                formatMobileNumberBD(mMobileNumber));

        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_INFO_REQUEST,
                getUserInfoRequestBuilder.getGeneratedUri(), getContext(), false);
        mGetProfileInfoTask.mHttpResponseListener = this;
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private ContactEngine.ContactData searchLocalContacts(String mobileNumber) {
        DataHelper dataHelper = DataHelper.getInstance(getActivity());
        int nameIndex, originalNameIndex, phoneNumberIndex, profilePictureUrlQualityMediumIndex;
        Cursor cursor = dataHelper.searchContacts(mobileNumber, true, false, false,
                true, false, false, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
                originalNameIndex = cursor.getColumnIndex(DBConstants.KEY_ORIGINAL_NAME);
                profilePictureUrlQualityMediumIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM);
                String name = cursor.getString(originalNameIndex);
                if (name == null || TextUtils.isEmpty(name)) {
                    name = cursor.getString(nameIndex);
                }
                String profilePictureUrl = cursor.getString(profilePictureUrlQualityMediumIndex);

                return new ContactEngine.ContactData(0, name, "", profilePictureUrl);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mMobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
                mName = data.getStringExtra(Constants.NAME);
                mProfileImageUrl = data.getStringExtra(Constants.PROFILE_PICTURE);
                if (mMobileNumber != null) {
                    mMobileNumber = mMobileNumber.substring(0, 4) + "-" + mMobileNumber.substring(4, mMobileNumber.length());
                    mNumberEditText.setText(mMobileNumber);
                }
                if (mName != null) {
                    mNameTextView.setText(mName);
                }
                isSelectedFromContact = true;
            }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetProfileInfoTask = null;
            mAddSponsorAsyncTask = null;
            ipayProgressDialog.dismiss();
        } else {
            ipayProgressDialog.dismiss();
            try {
                if (result.getApiCommand().equals(Constants.COMMAND_GET_PROFILE_INFO_REQUEST)) {
                    GetUserInfoResponse getUserInfoResponse = new Gson().fromJson(result.getJsonString(), GetUserInfoResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProfileImageUrl = getUserInfoResponse.getProfilePictures().get(0).getUrl();
                        mName = getUserInfoResponse.getName();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.PROFILE_PICTURE, mProfileImageUrl);
                        bundle.putString(Constants.MOBILE_NUMBER, mMobileNumber);
                        bundle.putString(Constants.NAME, mName);
                        bundle.putString(Constants.RELATION, relationShip);
                        ((SourceOfFundActivity) (getActivity())).switchToAddSourceOfFundConfirmFragment(bundle);
                    } else {
                        Toast.makeText(getContext(), getUserInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    mGetProfileInfoTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_ADD_SPONSOR)) {
                    GenericResponseWithMessageOnly responseWithMessageOnly = new Gson().fromJson
                            (result.getJsonString(), GenericResponseWithMessageOnly.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        Toast.makeText(getContext(), responseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                        ((SourceOfFundActivity) getActivity()).switchToSourceOfFundListFragment();
                    } else {
                        Toast.makeText(getContext(), responseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    mAddSponsorAsyncTask = null;
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
            }
        }

    }
}

