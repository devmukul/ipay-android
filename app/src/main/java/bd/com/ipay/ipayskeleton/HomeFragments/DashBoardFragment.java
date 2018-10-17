package bd.com.ipay.ipayskeleton.HomeFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.BroadcastServiceIntent;
import bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments.TransactionHistoryHolderFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BottomNavigationViewHelper;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class DashBoardFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final int HOME_TAB = 0;
    private final int PAY_TAB = 1;
    private final int TRANSACTION_HISTORY_TAB = 2;
    private final int CONTACTS_TAB = 3;
    private final int TOTAL_PAGE_COUNT = 4;

    private HomeFragment mHomeFragment;
    private PayDashBoardFragment mPayFragment;
    private PromotionsFragment mPromotionsFragment;
    private TransactionHistoryHolderFragment mTransactionHistoryFragment;

    private ViewPager viewPager;
    private MenuItem mPrevMenuItem;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        setTitle();
        mHomeFragment = new HomeFragment();
        mTransactionHistoryFragment = new TransactionHistoryHolderFragment();
        mPromotionsFragment = new PromotionsFragment();
        mPayFragment = new PayDashBoardFragment();

        viewPager = v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new DashBoardTabAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(TOTAL_PAGE_COUNT - 1);

        final BottomNavigationView bottomNavigationView = v.findViewById(R.id.navigationView);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (getActivity() != null)
                    Utilities.hideKeyboard(getActivity());
                if (mPrevMenuItem != null) {
                    mPrevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                mPrevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

//        if (mDeepLinkActionPath != null && mDeepLinkActionPath.contains("promotions")) {
//            viewPager.setCurrentItem(OFFER_TAB, true);
//        }

        return v;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    private void setTitle() {
        if (getActivity() instanceof HomeActivity && ((HomeActivity) getActivity()).getSupportActionBar() != null) {
            ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_wallet:
                viewPager.setCurrentItem(HOME_TAB);
                break;
            case R.id.navigation_pay:
                viewPager.setCurrentItem(PAY_TAB);
                break;
            case R.id.navigation_transaction:
                BroadcastServiceIntent.sendBroadcast(getActivity(), Constants.PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST);
                viewPager.setCurrentItem(TRANSACTION_HISTORY_TAB);
                break;
            case R.id.navigation_promotions:
                viewPager.setCurrentItem(CONTACTS_TAB);
                break;
        }
        return true;
    }

    private class DashBoardTabAdapter extends FragmentPagerAdapter {

        DashBoardTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case HOME_TAB:
                    return mHomeFragment;
                case PAY_TAB:
                    return mPayFragment;
                case TRANSACTION_HISTORY_TAB:
                    return mTransactionHistoryFragment;
                case CONTACTS_TAB:
                    return mPromotionsFragment;
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return TOTAL_PAGE_COUNT;
        }
    }

}