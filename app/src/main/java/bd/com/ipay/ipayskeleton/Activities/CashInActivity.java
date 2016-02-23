package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.AddMoneyFragments.CashInFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class CashInActivity extends AppCompatActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new CashInFragment()).commit();

    }
}

