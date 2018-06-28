package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.io.File;
import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Activities.QRCodeViewerActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.AddContactAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.CustomView.ContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.CustomContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.InvitationCode.GetPromoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.QRScanner.BarcodeCaptureActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import bd.com.ipay.ipayskeleton.camera.utility.CameraAndImageUtilities;

public class InviteFriendFragment extends BaseFragment implements HttpResponseListener {
    private HttpRequestGetAsyncTask mGetInvitationCodeTask= null;

    private Button buttonInvitePeople;
    private EditText mInvitationCodeEditText;
    private ProgressDialog mProgressDialog;
    private String mInvitationCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_friend, container, false);
        buttonInvitePeople = (Button) v.findViewById(R.id.button_invite_people);
        mInvitationCodeEditText = (EditText) v.findViewById(R.id.invitation_code);
        mProgressDialog = new ProgressDialog(getActivity());

        buttonInvitePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs())
                        shareQrCode(mInvitationCodeEditText.getText().toString());
                } else if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG);
            }
        });

        getInvitationCode();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private boolean verifyUserInputs() {
        mInvitationCodeEditText.setError(null);

        boolean cancel = false;
        View focusView = null;
        String errorMessage = null;

        mInvitationCode = mInvitationCodeEditText.getText().toString().trim();

        if (!(mInvitationCodeEditText.getText().toString().trim().length() > 0)) {
            focusView = mInvitationCodeEditText;
            mInvitationCodeEditText.setError("Please Enter a Invitation Code");
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    private void getInvitationCode() {
        if (mGetInvitationCodeTask != null) {
            return;
        }
        mProgressDialog.setMessage(getString(R.string.please_wait_loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mGetInvitationCodeTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INVITATION_CODE,
                Constants.BASE_URL_OFFER+Constants.URL_GET_INVITATION_CODE, getActivity(), false);
        mGetInvitationCodeTask.mHttpResponseListener = InviteFriendFragment.this;
        mGetInvitationCodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }



    private void shareQrCode(String code) {
        String share_qr_code_message = "Sign up with Invitation Code "+code+" and Explore the Magic of iPay. https://www.ipay.com.bd/signup/personal?code="+code;
        Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_qr_code_message);
                startActivity(Intent.createChooser(shareIntent, "Choose an app to share"));
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();
        Gson gson = new Gson();
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetInvitationCodeTask = null;
            return;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_INVITATION_CODE)) {
            try {

                GetPromoResponse mInvitationCodeResponse = gson.fromJson(result.getJsonString(), GetPromoResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mInvitationCode = mInvitationCodeResponse.getInvitationCode();
                    mInvitationCodeEditText.setText(mInvitationCode);
                }else{
                    Toast.makeText(getContext(), mInvitationCodeResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            mGetInvitationCodeTask = null;
            mProgressDialog.dismiss();
        }

    }

}
