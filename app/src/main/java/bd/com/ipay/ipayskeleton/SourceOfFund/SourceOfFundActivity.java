package bd.com.ipay.ipayskeleton.SourceOfFund;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.R;

public class SourceOfFundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_of_fund);
        switchToSourceOfFundListFragment();
    }

    public void switchToSourceOfFundListFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SourceOfFundListShowFragment()).commit();
    }
}
