package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;

import bd.com.ipay.ipayskeleton.R;

public class ContactsHolderFragment extends Fragment implements View.OnClickListener {

    public static final int TAB_ALL_CONTACTS = 0;
    public static final int TAB_IPAY_CONTACTS = 1;
    public static int selectedTab = 0;

    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private TextView allTab;
    private TextView iPayTab;

    private BottomSheetLayout mBottomSheetLayout;

    private Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViewPager();

        if (selectedTab == TAB_ALL_CONTACTS) viewPager.setCurrentItem(TAB_ALL_CONTACTS);
        else viewPager.setCurrentItem(TAB_IPAY_CONTACTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_holder, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.contactsViewPager);

        allTab = (TextView) v.findViewById(R.id.all_contacts_tab);
        allTab.setOnClickListener(this);
        iPayTab = (TextView) v.findViewById(R.id.ipay_contacts_tab);
        iPayTab.setOnClickListener(this);

        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);

        return v;
    }

    private void setUpViewPager() {
        adapter = new PendingListPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        setTab();
        viewPager.setCurrentItem(selectedTab);
    }

    private void setTab() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int position) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageSelected(int position) {
                switch (position) {
                    case TAB_ALL_CONTACTS:
                        allTab.setBackgroundResource(R.drawable.contacts_tab_selected_background);
                        iPayTab.setBackgroundResource(android.R.color.transparent);
                        selectedTab = TAB_ALL_CONTACTS;
                        break;

                    case TAB_IPAY_CONTACTS:
                        iPayTab.setBackgroundResource(R.drawable.contacts_tab_selected_background);
                        allTab.setBackgroundResource(android.R.color.transparent);
                        selectedTab = TAB_IPAY_CONTACTS;
                        break;
                }
            }
        });
    }

    private class PendingListPageAdapter extends FragmentStatePagerAdapter {

        public PendingListPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment fragment;
            switch (pos) {
                case 0:
                    AllContactsFragment allContactsFragment = new AllContactsFragment();
                    allContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
                    fragment = allContactsFragment;
                    break;
                case 1:
                    IPayContactsFragment iPayContactsFragment = new IPayContactsFragment();
                    iPayContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
                    fragment = iPayContactsFragment;
                    break;
                default:
                    fragment = new Fragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_contacts_tab:
                viewPager.setCurrentItem(TAB_ALL_CONTACTS);
                break;
            case R.id.ipay_contacts_tab:
                viewPager.setCurrentItem(TAB_IPAY_CONTACTS);
                break;
            default:
                break;
        }
    }


}