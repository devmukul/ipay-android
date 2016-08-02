package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments.CreateTicketFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments.TicketDetailsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments.TicketListFragment;
import bd.com.ipay.ipayskeleton.R;

public class HelpAndSupportActivity extends BaseActivity {

    private FloatingActionButton mNewTicketButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        mNewTicketButton = (FloatingActionButton) findViewById(R.id.fab_new_ticket);

        mNewTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToCreateTicketFragment();
            }
        });

        switchToTicketListFragment();
    }

    private void switchToCreateTicketFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateTicketFragment()).commit();
    }

    private void switchToTicketListFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TicketListFragment()).commit();
    }

    private void switchToTicketDetailsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TicketDetailsFragment()).addToBackStack(null).commit();
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
