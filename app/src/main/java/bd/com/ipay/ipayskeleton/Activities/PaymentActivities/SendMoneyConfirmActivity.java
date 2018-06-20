package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyConfirmFragment;
import bd.com.ipay.ipayskeleton.R;

public class SendMoneyConfirmActivity extends AppCompatActivity {

    public Toolbar toolbar;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backButton = (ImageView) findViewById(R.id.back_button);
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
    }

    public void switchToSendMoneyConfirmFragment() {
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
}
