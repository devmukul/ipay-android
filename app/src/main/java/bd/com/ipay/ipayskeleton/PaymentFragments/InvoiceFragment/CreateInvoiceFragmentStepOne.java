package bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.FriendPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.InvoiceActivity;
import bd.com.ipay.ipayskeleton.Activities.QRCodeViewerActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateInvoiceFragmentStepOne extends Fragment {

    private final int PICK_CONTACT_REQUEST = 100;

    private Button buttonCreateInvoice;
    private ImageView buttonSelectFromContacts;
    private ImageView buttonShowQRCode;
    private EditText mMobileNumberEditText;
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mQuantityEditText;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_invoice_step_one, container, false);
        getActivity().setTitle(R.string.create_invoice);

        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        buttonShowQRCode = (ImageView) v.findViewById(R.id.button_show_qr_code);
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_sender_from_contacts);
        buttonCreateInvoice = (Button) v.findViewById(R.id.button_request_money);
        mNameEditText = (EditText) v.findViewById(R.id.item_name);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mQuantityEditText = (EditText) v.findViewById(R.id.quantity);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.submitting_request_money));

        buttonCreateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs()) {
                        goToNextPage();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendPickerDialogActivity.class);
                intent.putExtra(Constants.IPAY_MEMBERS_ONLY, true);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        buttonShowQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRCodeViewerActivity.class);
                String userID = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE)
                        .getString(Constants.USERID, "").replaceAll("\\D", "");
                intent.putExtra(Constants.STRING_TO_ENCODE, userID);
                intent.putExtra(Constants.ACTIVITY_TITLE, getString(R.string.request_money));
                startActivity(intent);
            }
        });

        return v;
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        String receiver = mMobileNumberEditText.getText().toString();
        String name = mNameEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
        String quantity = mQuantityEditText.getText().toString();

        // Check for a validation
        if (!(quantity.length() > 0 && Double.parseDouble(quantity) > 0)) {
            mQuantityEditText.setError(getString(R.string.please_enter_amount));
            focusView = mQuantityEditText;
            cancel = true;
        }

        if (name.length() == 0) {
            mNameEditText.setError(getString(R.string.please_add_item_name));
            focusView = mNameEditText;
            cancel = true;
        }

        if (description.length() == 0) {
            mDescriptionEditText.setError(getString(R.string.please_add_description));
            focusView = mDescriptionEditText;
            cancel = true;
        }

        if (!ContactEngine.isValidNumber(receiver)) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            cancel = true;
        } else if (ContactEngine.formatMobileNumberBD(receiver).equals(ProfileInfoCacheManager.getMobileNumber())) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.you_cannot_request_money_from_your_number));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void goToNextPage() {
        String receiver = mMobileNumberEditText.getText().toString();
        String item_name = mNameEditText.getText().toString();
        String description = mDescriptionEditText.getText().toString();
        String quantity = mQuantityEditText.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.INVOICE_RECEIVER_TAG, receiver);
        bundle.putString(Constants.INVOICE_ITEM_NAME_TAG, item_name);
        bundle.putString(Constants.INVOICE_DESCRIPTION_TAG, description);
        bundle.putString(Constants.INVOICE_QUANTITY_TAG, quantity);

        ((InvoiceActivity)getActivity()).switchToCreateInvoiceStepTwoFragment(bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {

            if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
                String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
                if (mobileNumber != null) {
                    mMobileNumberEditText.setText(mobileNumber);
                    mMobileNumberEditText.setError(null);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == PICK_CONTACT_REQUEST) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), getString(R.string.no_contact_selected),
                        Toast.LENGTH_SHORT).show();
        }
    }
}
