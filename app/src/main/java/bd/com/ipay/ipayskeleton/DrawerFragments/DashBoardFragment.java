package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments.ContactsHolderFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.HomeFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.NotificationFragment;
import bd.com.ipay.ipayskeleton.R;

public class DashBoardFragment extends Fragment {

    private final int HOME_TAB = 0;
    private final int CONTACTS_TAB = 1;
    private final int NOTIFICATIONS_TAB = 2;

    private final int TOTAL_PAGE_COUNT = 3;

    private HomeFragment mHomeFragment;
    private ContactsHolderFragment mContactsHolderFragment;
    private NotificationFragment mNotificationFragment;
    private TabLayout.Tab homeTab;
    private TabLayout.Tab contactsTab;
    private TabLayout.Tab notificationsTab;

    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mHomeFragment = new HomeFragment();
        mContactsHolderFragment = new ContactsHolderFragment();
        mNotificationFragment = new NotificationFragment();

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new DashBoardTabAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        homeTab =  tabLayout.getTabAt(HOME_TAB);
        contactsTab = tabLayout.getTabAt(CONTACTS_TAB);
        notificationsTab =  tabLayout.getTabAt(NOTIFICATIONS_TAB);

        homeTab.setIcon(R.drawable.ic_home_white_24dp);
        contactsTab.setIcon(R.drawable.ic_people_outline_white_24dp);
        notificationsTab.setIcon(R.drawable.ic_notifications_none_white_24dp);


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
                switch (position) {
                    case 0:
                        homeTab.setIcon(R.drawable.ic_home_white_24dp);
                        contactsTab.setIcon(R.drawable.ic_people_outline_white_24dp);
                        notificationsTab.setIcon(R.drawable.ic_notifications_none_white_24dp);
                        break;
                    case 1:
                        homeTab.setIcon(R.drawable.ic_home_white_outline_24dp);
                        contactsTab.setIcon(R.drawable.ic_people_white_24dp);
                        notificationsTab.setIcon(R.drawable.ic_notifications_none_white_24dp);
                        break;
                    case 2:
                        homeTab.setIcon(R.drawable.ic_home_white_outline_24dp);
                        contactsTab.setIcon(R.drawable.ic_people_outline_white_24dp);
                        notificationsTab.setIcon(R.drawable.ic_notifications_white_24dp);
                        break;
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
                    return mContactsHolderFragment;
                case 2:
                    return mNotificationFragment;
                default:
                    return mHomeFragment;
            }
        }

        @Override
        public int getCount() {
            return TOTAL_PAGE_COUNT;
        }
    }

}