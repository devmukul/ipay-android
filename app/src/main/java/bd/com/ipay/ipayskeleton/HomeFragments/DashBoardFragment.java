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

import bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments.ContactsHolderFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.HomeFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class DashBoardFragment extends Fragment {

    private final int HOME_TAB = 0;
    private final int WALLET_TAB = 1;
    private final int SERVICES_TAB = 2;
    private final int CONTACTS_TAB = 3;

    private final int TOTAL_PAGE_COUNT = 4;

    private HomeFragment mHomeFragment;
    private WalletFragment mWalletFragment;
    private ServiceFragment mServiceFragment;
    private ContactsHolderFragment mContactsHolderFragment;

    private TabLayout.Tab homeTab;
    private TabLayout.Tab contactsTab;
    private TabLayout.Tab walletTab;
    private TabLayout.Tab servicesTab;

    private static final int[] ICONS_STATE_SELECTED = {R.drawable.ic_home_white_24dp, R.drawable.ic_wallet,
            R.drawable.ic_service, R.drawable.ic_people_white_24dp};
    private static final int[] ICONS_STATE_UNSELECTED = {R.drawable.ic_home_white_outline_24dp, R.drawable.ic_wallet_outline,
            R.drawable.ic_service_outline, R.drawable.ic_people_outline_white_24dp};

    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mHomeFragment = new HomeFragment();
        mWalletFragment = new WalletFragment();
        mServiceFragment = new ServiceFragment();
        mContactsHolderFragment = new ContactsHolderFragment();

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new DashBoardTabAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(3);

        final TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        homeTab = tabLayout.getTabAt(HOME_TAB);
        walletTab = tabLayout.getTabAt(WALLET_TAB);
        servicesTab = tabLayout.getTabAt(SERVICES_TAB);
        contactsTab = tabLayout.getTabAt(CONTACTS_TAB);

        homeTab.setIcon(ICONS_STATE_SELECTED[HOME_TAB]);
        walletTab.setIcon(ICONS_STATE_UNSELECTED[WALLET_TAB]);
        servicesTab.setIcon(ICONS_STATE_UNSELECTED[SERVICES_TAB]);
        contactsTab.setIcon(ICONS_STATE_UNSELECTED[CONTACTS_TAB]);

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
                        if (i == position) {
                            // Selected tab
                            tab.setIcon(ICONS_STATE_SELECTED[i]);
                        } else {
                            // Unselected tab
                            tab.setIcon(ICONS_STATE_UNSELECTED[i]);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return v;
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
                    return mWalletFragment;
                case 2:
                    return mServiceFragment;
                case 3:
                    return mContactsHolderFragment;
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