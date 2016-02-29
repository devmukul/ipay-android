package bd.com.ipay.ipayskeleton.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

import bd.com.ipay.ipayskeleton.R;

public class EventActivity extends AppCompatActivity {

    private FloatingActionButton mFabCreateNewEvent;
    private boolean switchedToEventList = true;

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

        switchToEventsFragment();
    }

    @Override
    public void onBackPressed() {
        if (switchedToEventList) super.onBackPressed();
        else {
            switchToEventsFragment();
        }
    }

    public void switchToEventsFragment() {
        // TODO
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new EventsFragment()).commit();
//        mFabCreateNewEvent.setVisibility(View.VISIBLE);
//        switchedToEventList = true;
    }

    public void switchToCreateNewEventFragment() {
        // TODO
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new CreateNewEventFragment()).commit();
//        mFabCreateNewEvent.setVisibility(View.GONE);
//        switchedToEventList = false;
    }
}




