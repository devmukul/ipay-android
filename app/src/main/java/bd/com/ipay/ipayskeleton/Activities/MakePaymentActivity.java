package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

import bd.com.ipay.ipayskeleton.MakePaymentFragments.CreateInvoiceFragment;
import bd.com.ipay.ipayskeleton.MakePaymentFragments.InvoicesReceivedFragment;
import bd.com.ipay.ipayskeleton.MakePaymentFragments.InvoicesSentFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.RequestMoneyFragments.RequestMoneyFragment;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MakePaymentActivity extends AppCompatActivity {

    private FloatingActionButton mFabCreateInvoice;
    private boolean switchedToInvoicesList = true;
    private SharedPreferences pref;
    private int accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        accountType = pref.getInt(Constants.ACCOUNT_TYPE, 1);

        mFabCreateInvoice = (FloatingActionButton) findViewById(R.id.fab_create_invoice);

        mFabCreateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToCreateInvoiceFragment();
            }
        });

        if (accountType == Constants.PERSONAL_ACCOUNT_TYPE) {
            switchToInvoicesReceivedFragment();
            mFabCreateInvoice.setVisibility(View.GONE);
        } else switchToInvoicesSentFragment();
    }

    @Override
    public void onBackPressed() {
        if (switchedToInvoicesList) super.onBackPressed();
        else {
            if (accountType == Constants.PERSONAL_ACCOUNT_TYPE)
                switchToInvoicesReceivedFragment();
            else switchToInvoicesSentFragment();
        }
    }

    public void switchToInvoicesReceivedFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InvoicesReceivedFragment()).commit();
        switchedToInvoicesList = true;
    }

    public void switchToInvoicesSentFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InvoicesSentFragment()).commit();
        mFabCreateInvoice.setVisibility(View.VISIBLE);
        switchedToInvoicesList = true;
    }
    
    public void switchToCreateInvoiceFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateInvoiceFragment()).commit();
        mFabCreateInvoice.setVisibility(View.GONE);
        switchedToInvoicesList = false;
    }
}





