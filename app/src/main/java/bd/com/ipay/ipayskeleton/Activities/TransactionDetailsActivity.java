package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments.TransactionDetailsFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TransactionDetailsActivity extends BaseActivity {

    private TransactionHistoryClass transactionHistoryClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transaction_details);

        transactionHistoryClass = getIntent().getParcelableExtra(Constants.TRANSACTION_DETAILS);

        TransactionDetailsFragment transactionDetailsFragment = new TransactionDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.TRANSACTION_DETAILS, transactionHistoryClass);
        transactionDetailsFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, transactionDetailsFragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Context setContext() {
        return TransactionDetailsActivity.this;
    }
}
