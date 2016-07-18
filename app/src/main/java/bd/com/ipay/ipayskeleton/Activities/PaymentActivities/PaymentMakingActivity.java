package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.InvoicePaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.PaymentMakingFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyFragment;
import bd.com.ipay.ipayskeleton.R;


public class PaymentMakingActivity extends BaseActivity {

    private FloatingActionButton mFabMakingPayment;
    private boolean switchedToPendingList = true;

    public static MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_making);
        mFabMakingPayment = (FloatingActionButton) findViewById(R.id.fab_payment_making);

        switchToInvoicePaymentFrament();

        mFabMakingPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToPaymentMakingFragment();
            }
        });
    }

    public void onBackPressed() {
        if (switchedToPendingList) {
            super.onBackPressed();
        } else {
            switchToInvoicePaymentFrament();
        }
    }

    public void switchToPaymentMakingFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PaymentMakingFragment()).commit();
        mFabMakingPayment.setVisibility(View.GONE);
        switchedToPendingList = false;
    }

    public void switchToInvoicePaymentFrament() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InvoicePaymentFragment()).commit();
        mFabMakingPayment.setVisibility(View.VISIBLE);
        switchedToPendingList = true;
    }


    @Override
    public Context setContext() {
        return PaymentMakingActivity.this;
    }
}



