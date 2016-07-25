package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.EventFragments.CreateNewEventFragment;
import bd.com.ipay.ipayskeleton.EventFragments.EventDetailsFragment;
import bd.com.ipay.ipayskeleton.EventFragments.EventFragments;
import bd.com.ipay.ipayskeleton.EventFragments.InOutListHolderFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class EventActivity extends BaseActivity {

    private FloatingActionButton mFabCreateNewEvent;
    private boolean switchedToEventFragments = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        mFabCreateNewEvent = (FloatingActionButton) findViewById(R.id.fab_create_new_event);

        mFabCreateNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToCreateNewEventFragment();
            }
        });

        switchToEventFragments();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (switchedToEventFragments) super.onBackPressed();
        else {
            switchToEventFragments();
        }
    }

    public void switchToEventFragments() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new EventFragments()).commit();
        mFabCreateNewEvent.setVisibility(View.VISIBLE);
        switchedToEventFragments = true;
    }

    public void switchToInOutListFragments(long eventID) {

        InOutListHolderFragment mInOutListHolderFragment = new InOutListHolderFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.EVENT_ID, eventID);
        mInOutListHolderFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mInOutListHolderFragment).commit();
        mFabCreateNewEvent.setVisibility(View.VISIBLE);
        switchedToEventFragments = false;
    }

    private void switchToCreateNewEventFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateNewEventFragment()).commit();
        mFabCreateNewEvent.setVisibility(View.GONE);
        switchedToEventFragments = false;
    }

    public void switchToEventDetailsFragment(long eventID) {

        EventDetailsFragment mEventDetailsFragment = new EventDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.EVENT_ID, eventID);
        mEventDetailsFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mEventDetailsFragment).commit();
        mFabCreateNewEvent.setVisibility(View.GONE);
        switchedToEventFragments = false;
    }

    public void switchToTicketQRCode(long eventID, long transactionID, String eventName) {

        String stringToEncode = eventID + ":" + transactionID;
        Intent intent = new Intent(EventActivity.this, QRCodeViewerActivity.class);

        intent.putExtra(Constants.STRING_TO_ENCODE, stringToEncode);
        intent.putExtra(Constants.ACTIVITY_TITLE, eventName);
        startActivity(intent);
        mFabCreateNewEvent.setVisibility(View.GONE);
        switchedToEventFragments = false;
    }

    @Override
    public Context setContext() {
        return EventActivity.this;
    }
}




