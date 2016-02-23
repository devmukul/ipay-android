package bd.com.ipay.ipayskeleton.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.DrawerFragments.MobileTopupFragment;
import bd.com.ipay.ipayskeleton.R;

public class TopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new MobileTopupFragment()).commit();

    }
}



