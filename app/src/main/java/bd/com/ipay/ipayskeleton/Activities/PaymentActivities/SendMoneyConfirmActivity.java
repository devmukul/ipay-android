package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyConfirmFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneySuccessFragment;
import bd.com.ipay.ipayskeleton.R;

public class SendMoneyConfirmActivity extends AppCompatActivity {

    public ImageView backButton;
    private String name;
    private String number;
    private String amount;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_confirm);
        overridePendingTransition(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
        backButton = (ImageView) findViewById(R.id.back_button_black);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        switchToSendMoneyConfirmFragment();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_to_left_enter_from_negative, R.anim.left_to_right_exit);
    }

    public void switchToSendMoneyConfirmFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        Bundle bundle = new Bundle();
        if (getIntent() != null) {
            bundle.putString("name", name = getIntent().getStringExtra("name"));
            bundle.putString("amount", amount = getIntent().getStringExtra("amount"));
            bundle.putString("number", number = getIntent().getStringExtra("number"));
            bundle.putString("imageUrl", imageUrl = getIntent().getStringExtra("imageUrl"));
        }
        SendMoneyConfirmFragment sendMoneyConfirmFragment = new SendMoneyConfirmFragment();
        sendMoneyConfirmFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sendMoneyConfirmFragment).commit();
    }

    public void switchToSendMoneySuccessFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        SendMoneySuccessFragment sendMoneySuccessFragment = new SendMoneySuccessFragment();
        sendMoneySuccessFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.right_to_left_enter,
                        R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit)
                .replace(R.id.fragment_container, sendMoneySuccessFragment).addToBackStack(null).commit();
    }
}
