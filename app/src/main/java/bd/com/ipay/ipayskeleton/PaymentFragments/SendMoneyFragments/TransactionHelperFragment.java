package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.R;

public class TransactionHelperFragment extends Fragment {

    private int transactionType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                return inflater.inflate(R.layout.fragment_send_money_helper, container, false);
            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                return inflater.inflate(R.layout.fragment_request_money_helper, container, false);
            case IPayTransactionActionActivity.TRANSACTION_TYPE_TOP_UP:
                return inflater.inflate(R.layout.fragment_top_up_helper, container, false);
            case IPayTransactionActionActivity.TRANSACTION_TYPE_MAKE_PAYMENT:
                return inflater.inflate(R.layout.fragment_make_payment_helper, container, false);
            case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
            default:
                return null;
        }
    }
}
