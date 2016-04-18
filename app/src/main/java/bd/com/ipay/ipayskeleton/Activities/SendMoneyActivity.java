package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyFragment;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SendMoneyActivity extends BaseActivity {

    private SharedPreferences pref;
    private Boolean switchedToAccountSelection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SendMoneyFragment()).commit();

    }

    @Override
    public Context setContext() {
        return SendMoneyActivity.this;
    }
}

