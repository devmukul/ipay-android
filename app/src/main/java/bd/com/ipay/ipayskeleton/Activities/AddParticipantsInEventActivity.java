package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.AddParticipantsFragments.SelectParticipantsFromListFragment;
import bd.com.ipay.ipayskeleton.R;

public class AddParticipantsInEventActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SelectParticipantsFromListFragment()).commit();

    }

    @Override
    public Context setContext() {
        return AddParticipantsInEventActivity.this;
    }
}

