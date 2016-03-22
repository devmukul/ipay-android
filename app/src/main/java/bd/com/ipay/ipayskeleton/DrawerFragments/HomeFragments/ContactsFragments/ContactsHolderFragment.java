package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;

import bd.com.ipay.ipayskeleton.R;

public class ContactsHolderFragment extends Fragment {

    private BottomSheetLayout mBottomSheetLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.profile));

        View v = inflater.inflate(R.layout.fragment_contact_holder, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new ContactListTabAdapter(getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);

        return v;
    }

    private class ContactListTabAdapter extends FragmentPagerAdapter {

        private String[] tabTitles;
        public ContactListTabAdapter(FragmentManager fm) {
            super(fm);
            tabTitles = new String[] {
                getString(R.string.ipay_contacts),
                getString(R.string.all_contacts)
            };
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment fragment;
            switch (pos) {
                case 0:
                    IPayContactsFragment iPayContactsFragment = new IPayContactsFragment();
                    iPayContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
                    fragment = iPayContactsFragment;
                    break;
                case 1:
                    AllContactsFragment allContactsFragment = new AllContactsFragment();
                    allContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
                    fragment = allContactsFragment;
                    break;
                default:
                    fragment = new Fragment();
            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}