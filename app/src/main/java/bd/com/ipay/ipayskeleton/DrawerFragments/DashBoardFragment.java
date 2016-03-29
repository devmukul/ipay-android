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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments.ContactsHolderFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments.IPayContactsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.HomeFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.NotificationFragment;
import bd.com.ipay.ipayskeleton.R;

public class DashBoardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new DashBoardTabAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return v;
    }

    private class DashBoardTabAdapter extends FragmentPagerAdapter {

        private int[] tabIcons;

        public DashBoardTabAdapter(FragmentManager fm) {
            super(fm);
            tabIcons = new int[]{
                    R.drawable.ic_home_white_24dp,
                    R.drawable.ic_people_white_24dp,
                    R.drawable.ic_notifications_white_24dp
            };
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    HomeActivity.switchedToHomeFragment = true;
                    return new HomeFragment();
                case 1:
                    HomeActivity.switchedToHomeFragment = false;
                    return new ContactsHolderFragment();
                case 2:
                    HomeActivity.switchedToHomeFragment = false;
                    return new NotificationFragment();
                default:
                    return new Fragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable image = getActivity().getResources().getDrawable(tabIcons[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}