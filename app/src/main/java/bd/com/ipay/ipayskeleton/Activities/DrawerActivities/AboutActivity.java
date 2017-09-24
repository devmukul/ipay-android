package bd.com.ipay.ipayskeleton.Activities.DrawerActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.DrawerFragments.AboutContactsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.AboutFragment;
import bd.com.ipay.ipayskeleton.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switchToAboutFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                setTitle(R.string.about);
            } else {
                finish();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void switchToAboutFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AboutFragment()).commit();
    }

    public void switchToAboutContactsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new AboutContactsFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            setTitle(R.string.about);
        } else
            super.onBackPressed();
    }

    @Override
    public Context setContext() {
        return AboutActivity.this;
    }
}
