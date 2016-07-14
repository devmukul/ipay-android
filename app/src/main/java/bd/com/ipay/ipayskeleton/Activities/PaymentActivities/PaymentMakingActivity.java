package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.PaymentMakingFragment;
import bd.com.ipay.ipayskeleton.R;



public class PaymentMakingActivity extends BaseActivity {


    public static MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_making);

        getFragmentManager().beginTransaction().add(R.id.fragment_container,new PaymentMakingFragment()).commit();

    }

    @Override
    public Context setContext() {
        return PaymentMakingActivity.this;
    }
}



