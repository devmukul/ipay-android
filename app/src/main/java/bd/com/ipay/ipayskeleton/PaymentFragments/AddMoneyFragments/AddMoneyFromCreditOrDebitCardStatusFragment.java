package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.CardPaymentWebViewActivity;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddMoneyFromCreditOrDebitCardStatusFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_money_by_credit_or_debit_card_status, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView mMessageTextView = findViewById(R.id.message_text_view);
        if (getActivity().getIntent().hasExtra(Constants.CARD_TRANSACTION_DATA)) {
            final int transactionCompletionType = getActivity().getIntent().getBundleExtra(Constants.CARD_TRANSACTION_DATA).getInt(Constants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD_STATUS, 0);
            switch (transactionCompletionType) {
                case CardPaymentWebViewActivity.CARD_TRANSACTION_SUCCESSFUL:
                    mMessageTextView.setText(R.string.add_money_card_in_progress_message);
                    break;
                case CardPaymentWebViewActivity.CARD_TRANSACTION_FAILED:
                    mMessageTextView.setText(R.string.add_money_card_failed_message);
                    break;
                case CardPaymentWebViewActivity.CARD_TRANSACTION_CANCELED:
                    mMessageTextView.setText(R.string.add_money_card_cancel_message);
                    break;

            }
        }
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(id);
    }
}
