package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.CustomView.Dialogs.InviteDialog;
import bd.com.ipay.ipayskeleton.DrawerFragments.InviteListHolderFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InviteActivity extends BaseActivity {

    private FloatingActionButton mSendInviteButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        mSendInviteButton = (FloatingActionButton) findViewById(R.id.fab_invite);

        mSendInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new InviteDialog(InviteActivity.this, null);
            }
        });

        switchToInviteListHolderFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void switchToInviteListHolderFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InviteListHolderFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected Context setContext() {
        return InviteActivity.this;
    }

}
