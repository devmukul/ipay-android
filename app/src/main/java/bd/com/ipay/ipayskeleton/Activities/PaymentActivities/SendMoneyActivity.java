package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyFragment;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyActivity extends BaseActivity {

    private SendMoneyFragment sendMoneyFragment;
    public static final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        sendMoneyFragment = new SendMoneyFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, sendMoneyFragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SendMoneyFragment.REQUEST_CODE_PERMISSION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (sendMoneyFragment != null)
                        sendMoneyFragment.initiateScan();
                } else {
                    Toast.makeText(this, R.string.request_for_camera_permission, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public Context setContext() {
        return SendMoneyActivity.this;
    }
}

