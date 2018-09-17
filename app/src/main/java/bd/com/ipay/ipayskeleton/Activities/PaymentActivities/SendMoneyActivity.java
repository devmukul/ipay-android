package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyEnterAmountFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.TransactionContactFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyActivity extends BaseActivity {

    public static MandatoryBusinessRules mMandatoryBusinessRules;
    public Toolbar toolbar;
    public TextView mToolbarHelpText;
    public TextView mTitle;
    public ImageView backButtonToolbar;
    public Button mRemoveHelperViewButton;

    public View mHelperView;
    public View mHolderView;
    public Bundle bundle;
    public LinearLayout mMainLayout;

    public float mHeight;
    public float mWidth;

    public boolean isFromQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarHelpText = (TextView) toolbar.findViewById(R.id.help_text_view);
        mTitle = (TextView) toolbar.findViewById(R.id.title);
        backButtonToolbar = (ImageView) toolbar.findViewById(R.id.back_button_toolbar);
        Drawable mBackButtonIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
        mBackButtonIcon.setColorFilter(new
                PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
        backButtonToolbar.setImageDrawable(null);
        isFromQRCode = false;
        backButtonToolbar.setImageDrawable(mBackButtonIcon);

        mHelperView = findViewById(R.id.helper_view);
        mMainLayout = (LinearLayout) findViewById(R.id.main_view);
        mHolderView = findViewById(R.id.holder_view);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mHeight = mMainLayout.getHeight();
            }
        });
        mRemoveHelperViewButton = findViewById(R.id.ok_button);

        mRemoveHelperViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideDown(mHelperView);
            }
        });
        backButtonToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mToolbarHelpText.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        if (getIntent().hasExtra(Constants.FROM_QR_SCAN)) {
            isFromQRCode = true;
            mHelperView.setVisibility(View.GONE);
            mHolderView.setVisibility(View.VISIBLE);
            String mobileNumbr = getIntent().getStringExtra(Constants.MOBILE_NUMBER);
            String imageUrl = getIntent().getStringExtra(Constants.PHOTO_URI);
            String name = getIntent().getStringExtra(Constants.NAME);
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("number", mobileNumbr);
            bundle.putString("imageUrl", Constants.BASE_URL_FTP_SERVER + imageUrl);
            switchToSendMoneyRecheckFragment(bundle);


        } else {
            mHelperView.setVisibility(View.VISIBLE);
            mHolderView.setVisibility(View.GONE);
            switchToSendMoneyContactFragment();
        }
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


    public void slideUp(View view, int height) {
        mHolderView.setVisibility(View.GONE);
        mHelperView.setVisibility(View.VISIBLE);
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
        mHeight = view.getHeight();
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
            getSupportFragmentManager().popBackStack();
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
            getSupportFragmentManager().popBackStack();
        }
        this.bundle = bundle;
        SendMoneyEnterAmountFragment sendMoneyEnterAmountFragment = new SendMoneyEnterAmountFragment();
        sendMoneyEnterAmountFragment.setArguments(bundle);
        if (isFromQRCode) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                    R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit)
                    .replace(R.id.fragment_container, sendMoneyEnterAmountFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                    R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit)
                    .replace(R.id.fragment_container, sendMoneyEnterAmountFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBackPressed() {
        Utilities.hideKeyboard(this);
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                ((SendMoneyEnterAmountFragment) getSupportFragmentManager().getFragments().get(1)).mBackButton.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.right_to_left_enter_from_negative, R.anim.left_to_right_exit);
        }

    }

    @Override
    public Context setContext() {
        return SendMoneyActivity.this;
    }
}