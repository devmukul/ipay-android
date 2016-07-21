package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.ForgotPasswordFragments.OTPVerificationForgotPasswordFragment;
import bd.com.ipay.ipayskeleton.R;

public class ForgotPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new OTPVerificationForgotPasswordFragment()).commit();

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
    public Context setContext() {
        return ForgotPasswordActivity.this;
    }
}

