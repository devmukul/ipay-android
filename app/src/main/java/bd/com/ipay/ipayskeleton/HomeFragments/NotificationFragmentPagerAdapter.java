package bd.com.ipay.ipayskeleton.HomeFragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class NotificationFragmentPagerAdapter extends FragmentPagerAdapter {
    public NotificationFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new NotificationDeeplinkedFragment();
            case 1:
                return new NotificationFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Notifications";
            case 1:
                return "Pending";
        }
        return super.getPageTitle(position);
    }
}
