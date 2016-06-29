package bd.com.ipay.ipayskeleton.HomeFragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.DrawerFragments.TransactionHistoryFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.ContactsHolderFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class DashBoardFragment extends Fragment {

    private final int HOME_TAB = 0;
    private final int SERVICES_TAB = 1;
    private final int CONTACTS_TAB = 2;
    private final int TRANSACTION_HISTORY_TAB = 3;

    private final int TOTAL_PAGE_COUNT = 4;

    private HomeFragment mHomeFragment;
    private ServiceFragment mServiceFragment;
    private ContactsHolderFragment mContactsHolderFragment;
    private TransactionHistoryFragment mTransactionHistoryFragment;

    private TabLayout.Tab homeTab;
    private TabLayout.Tab servicesTab;
    private TabLayout.Tab contactsTab;
    private TabLayout.Tab transactionHistoryTab;

    private View homeTabView;
    private View servicesTabView;
    private View contactsTabView;
    private View transactionHistoryTabView;

    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mHomeFragment = new HomeFragment();
        mTransactionHistoryFragment = new TransactionHistoryFragment();
        mServiceFragment = new ServiceFragment();
        mContactsHolderFragment = new ContactsHolderFragment();

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new DashBoardTabAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(3);    // TODO: Change upon number of tabs

        final TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupCustomViewsForTabLayout();

        homeTab = tabLayout.getTabAt(HOME_TAB);
        transactionHistoryTab = tabLayout.getTabAt(TRANSACTION_HISTORY_TAB);
        servicesTab = tabLayout.getTabAt(SERVICES_TAB);
        contactsTab = tabLayout.getTabAt(CONTACTS_TAB);

        setHomeTabSelected();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Utilities.hideKeyboard(getActivity());

                if (position == CONTACTS_TAB) {
                    if ((mContactsHolderFragment != null)) {
                        mContactsHolderFragment.onFocus();
                    }
                }

                for (int i = 0; i < TOTAL_PAGE_COUNT; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);

                    if (tab != null) {
                        // Selected tab
                        if (position == HOME_TAB) setHomeTabSelected();
                        else if (position == CONTACTS_TAB) setContactsTabSelected();
                        else if (position == TRANSACTION_HISTORY_TAB) setNotificationTabSelected();
                        else if (position == SERVICES_TAB) setServicesTabSelected();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return v;
    }

    private void setupCustomViewsForTabLayout() {
        homeTabView = getActivity().getLayoutInflater().inflate(R.layout.view_single_tab_background, null);

        contactsTabView = getActivity().getLayoutInflater().inflate(R.layout.view_single_tab_background, null);

        transactionHistoryTabView = getActivity().getLayoutInflater().inflate(R.layout.view_single_tab_background, null);

        servicesTabView = getActivity().getLayoutInflater().inflate(R.layout.view_single_tab_background, null);
    }

    private void setHomeTabSelected() {

        homeTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_home_white_24dp);
        homeTab.setCustomView(homeTabView);

        contactsTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_people_outline_white_24dp);
        contactsTab.setCustomView(contactsTabView);

        transactionHistoryTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_notifications_none_white_24dp);
        transactionHistoryTab.setCustomView(transactionHistoryTabView);

        servicesTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_service_outline);
        servicesTab.setCustomView(servicesTabView);
    }

    private void setContactsTabSelected() {

        homeTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_home_white_outline_24dp);
        homeTab.setCustomView(homeTabView);

        contactsTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_people_white_24dp);
        contactsTab.setCustomView(contactsTabView);

        transactionHistoryTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_notifications_none_white_24dp);
        transactionHistoryTab.setCustomView(transactionHistoryTabView);

        servicesTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_service_outline);
        servicesTab.setCustomView(servicesTabView);
    }

    private void setNotificationTabSelected() {

        homeTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_home_white_outline_24dp);
        homeTab.setCustomView(homeTabView);

        contactsTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_people_outline_white_24dp);
        contactsTab.setCustomView(contactsTabView);

        transactionHistoryTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_notifications_white_24dp);
        transactionHistoryTab.setCustomView(transactionHistoryTabView);

        servicesTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_service_outline);
        servicesTab.setCustomView(servicesTabView);
    }

    private void setServicesTabSelected() {

        homeTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_home_white_outline_24dp);
        homeTab.setCustomView(homeTabView);

        contactsTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_people_outline_white_24dp);
        contactsTab.setCustomView(contactsTabView);

        transactionHistoryTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_notifications_none_white_24dp);
        transactionHistoryTab.setCustomView(transactionHistoryTabView);

        servicesTabView.findViewById(R.id.tab_icon).setBackgroundResource(R.drawable.ic_service);
        servicesTab.setCustomView(servicesTabView);
    }

    private class DashBoardTabAdapter extends FragmentPagerAdapter {

        public DashBoardTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return mHomeFragment;
                case 1:
                    return mServiceFragment;
                case 2:
                    return mContactsHolderFragment;
                case 3:
                    return mTransactionHistoryFragment;
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