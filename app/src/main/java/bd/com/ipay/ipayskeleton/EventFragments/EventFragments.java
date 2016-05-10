package bd.com.ipay.ipayskeleton.EventFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.MyRequestsFragment;

public class EventFragments extends Fragment implements View.OnClickListener {

    public static final int TAB_FIRST = 0;
    public static final int TAB_SECOND = 1;
    public static int selectedTab = 0;

    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private TextView firstTab;
    private TextView secondTab;
    private View firstTabIndicator;
    private View secondTabIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViewPager();

        if (selectedTab == TAB_FIRST) viewPager.setCurrentItem(TAB_FIRST);
        else viewPager.setCurrentItem(TAB_SECOND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_list_fragments, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.eventViewPager);

        firstTab = (TextView) v.findViewById(R.id.events_tab);
        firstTab.setOnClickListener(this);
        secondTab = (TextView) v.findViewById(R.id.payments_tab);
        secondTab.setOnClickListener(this);

        firstTabIndicator = v.findViewById(R.id.events_tab_indicator);
        secondTabIndicator = v.findViewById(R.id.payments_tab_indicator);

        return v;
    }

    private void setUpViewPager() {

        adapter = new EventPageAdapter(getChildFragmentManager());
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
                    case TAB_FIRST:
                        firstTabIndicator.setBackgroundColor(getResources().getColor(android.R.color.white));
                        secondTabIndicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        selectedTab = TAB_FIRST;
                        break;

                    case TAB_SECOND:
                        firstTabIndicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        secondTabIndicator.setBackgroundColor(getResources().getColor(android.R.color.white));
                        selectedTab = TAB_SECOND;
                        break;
                }
            }
        });
    }

    private class EventPageAdapter extends FragmentStatePagerAdapter {

        public EventPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment fragment;
            switch (pos) {
                case 0:
                    // TODO: event list fragment
                    fragment = new MyRequestsFragment();
                    break;
                case 1:
                    // TODO: payments list fragment
                    fragment = new MyRequestsFragment();
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
            case R.id.events_tab:
                viewPager.setCurrentItem(TAB_FIRST);
                break;
            case R.id.payments_tab:
                viewPager.setCurrentItem(TAB_SECOND);
                break;
            default:
                break;
        }
    }

}