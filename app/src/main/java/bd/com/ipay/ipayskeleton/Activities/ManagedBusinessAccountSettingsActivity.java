package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.BusinessFragments.ManagePeopleFragments.ManagedBusinessAccountSettingsFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ManagedBusinessAccountSettingsActivity extends BaseActivity {
    public int mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        mId = getIntent().getIntExtra(Constants.ID, -1);
        switchToManagedBusinessAccountSettingsFragment();
    }

    private void switchToManagedBusinessAccountSettingsFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new ManagedBusinessAccountSettingsFragment()).commit();
    }


    @Override
    protected Context setContext() {
        return ManagedBusinessAccountSettingsActivity.this;
    }
}
