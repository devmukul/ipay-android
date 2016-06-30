package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyFragment;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SendMoneyActivity extends BaseActivity {

    private SharedPreferences pref;
    public Boolean switchedToAccountSelection = false;

    private SendMoneyFragment sendMoneyFragment;
    public static BigDecimal MAX_AMOUNT_PER_PAYMENT=new BigDecimal("0");
    public static BigDecimal MIN_AMOUNT_PER_PAYMENT=new BigDecimal("0");


    public int has_businessRule=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        sendMoneyFragment = new SendMoneyFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, sendMoneyFragment).commit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SendMoneyFragment.REQUEST_CODE_PERMISSION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (sendMoneyFragment != null)
                        sendMoneyFragment.initiateScan();
                } else {
                    Toast.makeText(this, R.string.request_for_camera_permission, Toast.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public Context setContext() {
        return SendMoneyActivity.this;
    }
}

