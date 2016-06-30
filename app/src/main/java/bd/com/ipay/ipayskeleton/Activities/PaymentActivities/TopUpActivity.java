package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments.MobileTopupFragment;
import bd.com.ipay.ipayskeleton.R;

public class TopUpActivity extends BaseActivity {


    public static BigDecimal MAX_AMOUNT_PER_PAYMENT=new BigDecimal("0");
    public static BigDecimal MIN_AMOUNT_PER_PAYMENT=new BigDecimal("0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new MobileTopupFragment()).commit();

    }

    @Override
    public Context setContext() {
        return TopUpActivity.this;
    }
}



