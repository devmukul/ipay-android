package bd.com.ipay.ipayskeleton.HomeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.R;

public class NotificationHolderFragment extends Fragment {
    private ViewPager mNotificationViewPager;
    private NotificationFragmentPagerAdapter mNotificationFragmentPagerAdapter;
    private TabLayout mNotificationTabLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_holder, container, false);
        mNotificationViewPager = (ViewPager) view.findViewById(R.id.view_pager_notification);
        mNotificationFragmentPagerAdapter = new NotificationFragmentPagerAdapter(getChildFragmentManager());
        mNotificationViewPager.setAdapter(mNotificationFragmentPagerAdapter);
        mNotificationTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        mNotificationTabLayout.setupWithViewPager(mNotificationViewPager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
