package bd.com.ipay.ipayskeleton.SourceOfFund;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.GenericResponseWithMessageOnly;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.AddBeneficiaryRequest;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.AddSponsorRequest;
import bd.com.ipay.ipayskeleton.SourceOfFund.view.BeneficiaryUpdateDialog;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;


public class AddSourceOfFundConfirmFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAddSponsorOrBeneficiaryAsyncTask;
    private String name;
    private String number;
    private String relation;
    private IpayProgressDialog ipayProgressDialog;

    private EditText numberEditText;
    private EditText relationEditEext;

    private String type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ipayProgressDialog = new IpayProgressDialog(getContext());
        type = getArguments().getString(Constants.TYPE);
        return inflater.inflate(R.layout.fragment_add_source_of_fund, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView nameTextView = view.findViewById(R.id.name);
        nameTextView.setVisibility(View.VISIBLE);

        numberEditText = (EditText) view.findViewById(R.id.number_edit_text);
        relationEditEext = (EditText) view.findViewById(R.id.relationship_edit_text);

        relationEditEext.setFocusable(false);
        relationEditEext.setClickable(true);

        numberEditText.setFocusable(false);
        numberEditText.setClickable(true);

        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        ImageView contactImageView = view.findViewById(R.id.contact_image_view);
        contactImageView.setVisibility(View.GONE);

        EditText numberEditText = view.findViewById(R.id.number_edit_text);
        EditText relationShipEditText = view.findViewById(R.id.relationship_edit_text);

        RoundedImageView profileImageView = view.findViewById(R.id.profile_image);

        Button doneButton = view.findViewById(R.id.done);

        ImageView downImageView = view.findViewById(R.id.down_image_view);
        downImageView.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        name = bundle.getString(Constants.NAME);
        String profileImageUrl = bundle.getString(Constants.PROFILE_PICTURE);
        number = bundle.getString(Constants.MOBILE_NUMBER);
        relation = bundle.getString(Constants.RELATION);

        String profileUrlToUse = "";
        try {
            if (profileImageUrl != null) {
                if (profileImageUrl.contains("ipay.com")) {

                } else {
                    profileImageUrl = Constants.BASE_URL_FTP_SERVER + profileImageUrl;
                }
            }
        } catch (Exception e) {

        }

        try {
            nameTextView.setText(name);
            numberEditText.setText(ContactEngine.formatMobileNumberBD(number));
            relationShipEditText.setText(relation);
            Glide.with(getContext())
                    .load(profileImageUrl)
                    .centerCrop()
                    .error(getContext().getResources().getDrawable(R.drawable.user_brand_bg))
                    .into(profileImageView);

        } catch (Exception e) {

        }
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddSponsorOrBeneficiary();
            }
        });

    }

    public void attemptAddSponsorOrBeneficiary() {
        if (mAddSponsorOrBeneficiaryAsyncTask != null) {
            return;
        } else {
            if (type.equals(Constants.SPONSOR)) {
                AddSponsorRequest addSponsorRequest = new AddSponsorRequest(ContactEngine.formatMobileNumberBD(number), relation);
                mAddSponsorOrBeneficiaryAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_SPONSOR
                        , Constants.BASE_URL_MM + Constants.URL_ADD_SPONSOR,
                        new Gson().toJson(addSponsorRequest), getContext(), this, false);
                ipayProgressDialog.setMessage("Please wait . . . ");
                ipayProgressDialog.show();
                mAddSponsorOrBeneficiaryAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                BeneficiaryUpdateDialog beneficiaryUpdateDialog =
                        new BeneficiaryUpdateDialog(getContext(), name, relation, ContactEngine.formatMobileNumberBD(number), new BeneficiaryUpdateDialog.BeneficiaryAddSuccessListener() {
                            @Override
                            public void onBeneficiaryAdded() {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.NAME, name);
                                bundle.putString(Constants.TYPE, type);
                                bundle.putString(Constants.PROFILE_PICTURE, getArguments().getString(Constants.PROFILE_PICTURE));
                                ((SourceOfFundActivity) getActivity()).switchToSourceOfSuccessFragment(bundle);
                            }
                        });
            }

        }

    }

    private void attemptAddBeneficiary(String pin) {
        AddBeneficiaryRequest addBeneficiaryRequest = new AddBeneficiaryRequest(
                ContactEngine.formatMobileNumberBD(number), pin, relation);
        mAddSponsorOrBeneficiaryAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_BENEFICIARY
                , Constants.BASE_URL_MM + Constants.URL_ADD_BENEFICIARY,
                new Gson().toJson(addBeneficiaryRequest), getContext(), this, false);
        ipayProgressDialog.setMessage("Please wait . . .");
        ipayProgressDialog.show();
        mAddSponsorOrBeneficiaryAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mAddSponsorOrBeneficiaryAsyncTask = null;
            ipayProgressDialog.dismiss();
            return;
        } else {
            try {
                ipayProgressDialog.dismiss();
                GenericResponseWithMessageOnly responseWithMessageOnly = new Gson().fromJson
                        (result.getJsonString(), GenericResponseWithMessageOnly.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Toast.makeText(getContext(), responseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.NAME, name);
                    bundle.putString(Constants.TYPE, type);
                    bundle.putString(Constants.PROFILE_PICTURE, getArguments().getString(Constants.PROFILE_PICTURE));
                    ((SourceOfFundActivity) getActivity()).switchToSourceOfSuccessFragment(bundle);
                } else {
                    Toast.makeText(getContext(), responseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
            }
            mAddSponsorOrBeneficiaryAsyncTask = null;
        }
    }
}