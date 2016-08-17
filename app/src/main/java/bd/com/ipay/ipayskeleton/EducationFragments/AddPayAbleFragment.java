package bd.com.ipay.ipayskeleton.EducationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.EducationPaymentActivity;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.MMModule.Education.PayableItem;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;

public class AddPayAbleFragment extends Fragment {

    private ArrayList<PayableItem> mPayablesList;
    private ResourceSelectorDialog paymentItemsSelectorDialog;
    private int mSelectedPaymentItemID = -1;

    private EditText paymentItemSelection;
    private EditText paymentItemDescription;
    private EditText paymentAmount;
    private Button addPaymentItemButton;

    private HashMap<Integer, PayableItem> payableItemsMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_education_add_payable, container, false);
        getActivity().setTitle(R.string.add_new_payment_item);
        mPayablesList = new ArrayList<>();
        payableItemsMap = new HashMap<>();

        if (getArguments() != null) {
            mPayablesList = getArguments().getParcelableArrayList(EducationPaymentActivity.ARGS_ENABLED_PAYABLE_ITEMS);
        }

        paymentItemSelection = (EditText) v.findViewById(R.id.payable_item);
        paymentItemDescription = (EditText) v.findViewById(R.id.description);
        paymentAmount = (EditText) v.findViewById(R.id.amount);
        addPaymentItemButton = (Button) v.findViewById(R.id.button_add);

        // Set filter on amount to avoid more than two digits after decimal point
        paymentAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        if (mPayablesList.size() > 0) setPaymentItemsAdapter();
        else {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.get_all_enabled_payables_failed, Toast.LENGTH_LONG).show();
            ((EducationPaymentActivity) getActivity()).switchToPayEducationFeesFragment();
        }

        addPaymentItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    attemptAddMyPaymentItems();
                }
            }
        });

        return v;
    }

    private void setPaymentItemsAdapter() {

        // Populate the HashMap
        for (PayableItem mPayableItem : mPayablesList) {
            payableItemsMap.put(mPayableItem.getId(), mPayableItem);
        }

        // Set the selector here
        paymentItemsSelectorDialog = new ResourceSelectorDialog(getActivity(), getString(R.string.select_session), mPayablesList, mSelectedPaymentItemID);
        paymentItemsSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                paymentItemSelection.setError(null);
                paymentItemSelection.setText(name);

                mSelectedPaymentItemID = id;
                BigDecimal amount = payableItemsMap.get(mSelectedPaymentItemID).getInstituteFee();
                String description = payableItemsMap.get(mSelectedPaymentItemID).getPayableAccountHead().getDescription();

                if (amount != null) {
                    paymentAmount.setText(amount + "");
                    paymentAmount.setEnabled(false);
                } else {
                    paymentAmount.setText("");
                    paymentAmount.setEnabled(true);
                }

                if (description != null)
                    paymentItemDescription.setText(description);

            }
        });

        paymentItemSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentItemsSelectorDialog.show();
            }
        });
    }

    private boolean validateInputs() {
        if (mSelectedPaymentItemID == -1) {
            paymentItemSelection.setError(getString(R.string.please_select_payment_item));
            return false;
        }

        if (paymentAmount.getText().toString().trim().length() == 0) {
            paymentAmount.setError(getString(R.string.please_enter_amount));
            paymentAmount.requestFocus();
            return false;
        }

        return true;
    }

    private void attemptAddMyPaymentItems() {
        PayableItem mPayableItem = payableItemsMap.get(mSelectedPaymentItemID);
        mPayableItem.setInstituteFee(new BigDecimal(Double.parseDouble(paymentAmount.getText().toString().trim())).setScale(2, RoundingMode.HALF_UP));

        boolean addedAlready = false;
        for (PayableItem loopPayableItem : EducationPaymentActivity.mMyPayableItems) {
            if (loopPayableItem.getId() == mPayableItem.getId()) {
                addedAlready = true;
                break;
            }
        }

        if (!addedAlready) {
            EducationPaymentActivity.mMyPayableItems.add(mPayableItem);
            ((EducationPaymentActivity) getActivity()).switchToPayEducationFeesFragment();

        } else {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.already_added_payment_item, Toast.LENGTH_LONG).show();
            paymentItemSelection.setError(getString(R.string.already_added_payment_item));
        }
    }
}
