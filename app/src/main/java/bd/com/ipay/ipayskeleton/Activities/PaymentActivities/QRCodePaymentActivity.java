package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import bd.com.ipay.ipayskeleton.PaymentFragments.QRCodePaymentFragments.ScanQRCodeFragment;
import bd.com.ipay.ipayskeleton.R;

public class QRCodePaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_payment);
        switchToScanQRCodeFragment();
    }

    private void switchToScanQRCodeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScanQRCodeFragment()).commit();
    }
}
