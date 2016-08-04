package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments.CreateTicketFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments.TicketDetailsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments.TicketListFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class HelpAndSupportActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        switchToTicketListFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchToCreateTicketFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateTicketFragment()).addToBackStack(null).commit();
    }

    private void switchToTicketListFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TicketListFragment()).commit();
    }

    public void switchToTicketDetailsFragment(long ticketId) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.TICKET_ID, ticketId);
        TicketDetailsFragment fragment = new TicketDetailsFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    @Override
    protected Context setContext() {
        return HelpAndSupportActivity.this;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }
}
