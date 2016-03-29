package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.ServicesFragments.MobileTopupFragment;
import bd.com.ipay.ipayskeleton.R;

public class TopUpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new MobileTopupFragment()).commit();

    }

    @Override
    public Context setContext() {
        return TopUpActivity.this;
    }
}



