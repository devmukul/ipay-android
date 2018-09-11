package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyRecheckFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.TransactionContactFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyActivity extends BaseActivity {

    public static MandatoryBusinessRules mMandatoryBusinessRules;
    public Toolbar toolbar;
    public TextView mToolbarHelpText;
    public TextView mTitle;
    public ImageView backButton;
    public Button mRemoveHelperViewButton;

    public View mHelperView;
    public View mHolderView;
    public Bundle bundle;

    public float mHeight;
    public float mWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarHelpText = (TextView) toolbar.findViewById(R.id.help_text_view);
        mTitle = (TextView) toolbar.findViewById(R.id.title);
        backButton = (ImageView) toolbar.findViewById(R.id.back_button);
        mHelperView = findViewById(R.id.helper_view);
        mHolderView = findViewById(R.id.holder_view);
        mHelperView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHelperView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mHeight = mHelperView.getHeight(); //height is ready
            }
        });
        mRemoveHelperViewButton = findViewById(R.id.ok_button);

        mRemoveHelperViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideDown(mHelperView);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mToolbarHelpText.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        switchToSendMoneyContactFragment();
    }

    public void showTitle() {
        mTitle.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        mTitle.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void slideUp(View view) {
        mHelperView.setVisibility(View.VISIBLE);
        mHeight = view.getHeight();
        if (mHeight == 0) {
            mHeight = 3000;
        }
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                mHeight,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mHelperView.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHelperView.setVisibility(View.GONE);
                mHolderView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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

    public void switchToSendMoneyFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SendMoneyFragment()).commit();
    }

    public void switchToSendMoneyContactFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        TransactionContactFragment transactionContactFragment = new TransactionContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SOURCE, Constants.SEND_MONEY);
        transactionContactFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, transactionContactFragment).commit();
    }

    public void switchToSendMoneyRecheckFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        this.bundle = bundle;
        SendMoneyRecheckFragment sendMoneyRecheckFragment = new SendMoneyRecheckFragment();
        sendMoneyRecheckFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit)
                .replace(R.id.fragment_container, sendMoneyRecheckFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Context setContext() {
        return SendMoneyActivity.this;
    }


}