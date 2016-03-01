package bd.com.ipay.ipayskeleton.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

import bd.com.ipay.ipayskeleton.EventFragments.CreateNewEventFragment;
import bd.com.ipay.ipayskeleton.EventFragments.EventFragments;
import bd.com.ipay.ipayskeleton.R;

public class EventActivity extends AppCompatActivity {

    private FloatingActionButton mFabCreateNewEvent;
    private boolean switchedToEventFragments = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);
        mFabCreateNewEvent = (FloatingActionButton) findViewById(R.id.fab_create_new_event);

        mFabCreateNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToCreateNewEventFragment();
            }
        });

        switchToEventFragments();
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

    public void switchToCreateNewEventFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateNewEventFragment()).commit();
        mFabCreateNewEvent.setVisibility(View.GONE);
        switchedToEventFragments = false;
    }
}




