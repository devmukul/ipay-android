package bd.com.ipay.ipayskeleton.PaymentFragments.QRCodePaymentFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.QRScanner.BarcodeCaptureActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ScanQRCodeFragment extends BaseFragment implements HttpResponseListener {

    public static final int REQUEST_CODE_PERMISSION = 1001;

    private HttpRequestGetAsyncTask mGetUserInfoTask;
    private List<BusinessContact> mBusinessContactList;

    private View mRootView;
    private ProgressDialog mProgressDialog;

    private String mobileNumber;
    private String name;
    private String imageUrl;
    private String address;
    private String country;
    private String district;
    private String thana;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_scan_qr_code, container, false);
        }
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.please_wait_loading));
        mProgressDialog.setCancelable(false);

        Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
        startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_scan_qr_code));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    final String result = barcode.displayValue;

                    final Handler mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (InputValidator.isValidNumber(result)) {
                                Cursor mCursor;
                                DataHelper dataHelper = DataHelper.getInstance(getContext());
                                mCursor = dataHelper.searchBusinessAccountsByMobile(result.replaceAll("[^0-9]", ""));

                                try {
                                    if (mCursor != null) {
                                        mobileNumber = ContactEngine.formatMobileNumberBD(result);
                                        mBusinessContactList = getBusinessContactList(mCursor);
                                        if (!mBusinessContactList.get(0).getProfilePictureUrl().isEmpty())
                                            imageUrl = mBusinessContactList.get(0).getProfilePictureUrl();
                                        if (!mBusinessContactList.get(0).getBusinessName().isEmpty())
                                            name = mBusinessContactList.get(0).getBusinessName();
                                        if (!mBusinessContactList.get(0).getAddressString().isEmpty())
                                            address = mBusinessContactList.get(0).getAddressString();
                                        if (!mBusinessContactList.get(0).getDistrictString().isEmpty())
                                            district = mBusinessContactList.get(0).getDistrictString();
                                        if (!mBusinessContactList.get(0).getThanaString().isEmpty())
                                            thana = mBusinessContactList.get(0).getThanaString();

                                        if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.MAKE_PAYMENT)) {
                                            DialogUtils.showServiceNotAllowedDialog(getContext());
                                        } else {
                                            switchActivity(PaymentActivity.class);
                                        }
                                    } else {
                                        fetchUserInfo(result);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    fetchUserInfo(result);
                                } finally {
                                    if (mCursor != null) {
                                        mCursor.close();
                                    }
                                }

                            } else if (getActivity() != null) {
                                DialogUtils.showDialogForInvalidQRCode(getActivity(), getString(R.string.scan_valid_ipay_qr_code));
                            }
                        }
                    });
                } else {
                    getActivity().finish();
                }
            } else {
                getActivity().finish();
            }
        }
    }


    public void fetchUserInfo(String result) {
        if (Utilities.isConnectionAvailable(getActivity())) {
            mobileNumber = ContactEngine.formatMobileNumberBD(result);
            GetUserInfoRequestBuilder getUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

            if (mGetUserInfoTask != null) {
                return;
            }

            mProgressDialog.show();
            mGetUserInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                    getUserInfoRequestBuilder.getGeneratedUri(), getActivity(), false);
            mGetUserInfoTask.mHttpResponseListener = ScanQRCodeFragment.this;
            mGetUserInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toaster.makeText(getActivity(), getResources().getString(
                    R.string.no_internet_connection), Toast.LENGTH_SHORT);
            mProgressDialog.cancel();
            getActivity().finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                    startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
                } else {
                    getActivity().finish();
                    Toaster.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();
        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mGetUserInfoTask = null;
            return;
        }
        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_USER_INFO:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    try {
                        Gson gson = new GsonBuilder().create();
                        GetUserInfoResponse getUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);
                        if (!getUserInfoResponse.getProfilePictures().isEmpty())
                            imageUrl = getUserInfoResponse.getProfilePictures().get(0).getUrl();
                        if (!getUserInfoResponse.getName().isEmpty())
                            name = getUserInfoResponse.getName();
                        if (getUserInfoResponse.getAddressList() != null) {
                            if (getUserInfoResponse.getAddressList().getOFFICE() != null) {
                                address = getUserInfoResponse.getAddressList().getOFFICE().get(0).getAddressLine1();
                                country = getUserInfoResponse.getAddressList().getOFFICE().get(0).getCountry();
                                district = getUserInfoResponse.getAddressList().getOFFICE().get(0).getDistrict();
                                thana = getUserInfoResponse.getAddressList().getOFFICE().get(0).getThana();
                            }
                        }

                        // We will do a check here to know if the account is a personal account or business account.
                        // For Personal Account we have to launch the SendMoneyActivity
                        // For Business Account we have to launch the PaymentActivity with a account status verification check
                        if (getUserInfoResponse.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE) {
                            if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.SEND_MONEY)) {
                                DialogUtils.showServiceNotAllowedDialog(getContext());
                            } else {
                                switchActivity(SendMoneyActivity.class);
                            }
                        } else if (getUserInfoResponse.getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE) {
                            if (getUserInfoResponse.getAccountStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                                if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.MAKE_PAYMENT)) {
                                    DialogUtils.showServiceNotAllowedDialog(getContext());
                                } else {
                                    switchActivity(PaymentActivity.class);
                                }
                            } else {
                                DialogUtils.showDialogForInvalidQRCode(getActivity(), getString(R.string.business_account_not_verified));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();

                    }
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    DialogUtils.showDialogForInvalidQRCode(getActivity(), getString(R.string.scan_valid_ipay_qr_code));
                }
                break;
        }
    }

    private void switchActivity(Class tClass) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(Constants.MOBILE_NUMBER, mobileNumber);
        intent.putExtra(Constants.FROM_QR_SCAN, true);
        intent.putExtra(Constants.NAME, name);
        intent.putExtra(Constants.PHOTO_URI, imageUrl);
        intent.putExtra(Constants.COUNTRY, country);
        intent.putExtra(Constants.DISTRICT, district);
        intent.putExtra(Constants.ADDRESS, address);
        intent.putExtra(Constants.THANA, thana);
        intent.putExtra(Constants.FROM_QR_SCAN, true);
        startActivity(intent);
        getActivity().finish();
    }


    private List<BusinessContact> getBusinessContactList(Cursor cursor) {
        List<BusinessContact> mBusinessContacts;
        int businessNameIndex;
        int phoneNumberIndex;
        int profilePictureUrlIndex;
        int businessTypeIndex;
        int businessAddressIndex;
        int businessThanaIndex;
        int businessDistrictIndex;


        mBusinessContacts = new ArrayList<>();

        if (cursor != null) {
            mBusinessContacts.clear();
            businessNameIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_NAME);
            phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_PROFILE_PICTURE);
            businessTypeIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_TYPE);
            businessAddressIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_ADDRESS);
            businessThanaIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_THANA);
            businessDistrictIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_DISTRICT);

            if (cursor.moveToFirst())
                do {
                    String businessName = cursor.getString(businessNameIndex);
                    String mobileNumber = cursor.getString(phoneNumberIndex);
                    String profilePictureUrl = cursor.getString(profilePictureUrlIndex);
                    int businessTypeID = cursor.getInt(businessTypeIndex);
                    String businessAddress = cursor.getString(businessAddressIndex);
                    String businessThana = cursor.getString(businessThanaIndex);
                    String businessDistrict = cursor.getString(businessDistrictIndex);

                    BusinessContact businessContact = new BusinessContact();
                    businessContact.setBusinessName(businessName);
                    businessContact.setMobileNumber(mobileNumber);
                    businessContact.setProfilePictureUrl(profilePictureUrl);
                    businessContact.setAddressString(businessAddress);
                    businessContact.setThanaString(businessThana);
                    businessContact.setDistrictString(businessDistrict);

                    if (CommonData.getBusinessTypes() != null) {
                        BusinessType businessType = CommonData.getBusinessTypeById(businessTypeID);
                        if (businessType != null)
                            businessContact.setBusinessType(businessType.getName());
                    }

                    mBusinessContacts.add(businessContact);

                } while (cursor.moveToNext());
        }

        return mBusinessContacts;
    }
}
