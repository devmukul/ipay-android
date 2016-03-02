package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.AddMoneyFragments.CashInFragment;
import bd.com.ipay.ipayskeleton.AddParticipantsFragments.SelectParticipantsFromListFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddParticipantsInEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SelectParticipantsFromListFragment()).commit();

    }
}

