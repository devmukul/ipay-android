package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.R;

public class CreditCardBottomDialogFragment extends BottomSheetDialogFragment {

    public static CreditCardBottomDialogFragment newInstance() {
        return new CreditCardBottomDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_card_payment, container, false);

        // get the views and attach the listener

        return view;

    }
}