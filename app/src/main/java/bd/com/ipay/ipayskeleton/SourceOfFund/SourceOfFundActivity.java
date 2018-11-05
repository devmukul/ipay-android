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

    public void switchToAddSourceOfFundFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit).replace
                (R.id.fragment_container, new AddSourceOfFundFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void switchToAddSourceOfFundConfirmFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStack();
        }
        AddSourceOfFundConfirmFragment addSourceOfFundConfirmFragment = new AddSourceOfFundConfirmFragment();
        addSourceOfFundConfirmFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit).replace
                (R.id.fragment_container, addSourceOfFundConfirmFragment).addToBackStack(null).commit();
    }

    public void switchToSourceOfSuccessFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3) {
            getSupportFragmentManager().popBackStack();
        }
        AddSourceOfFundSuccessFragment addSourceOfFundSuccessFragment = new AddSourceOfFundSuccessFragment();
        addSourceOfFundSuccessFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit).replace
                (R.id.fragment_container, addSourceOfFundSuccessFragment).addToBackStack(null).commit();
    }
}