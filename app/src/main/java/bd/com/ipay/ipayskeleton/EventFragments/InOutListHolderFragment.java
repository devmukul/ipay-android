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
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Activities.EventActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class InOutListHolderFragment extends Fragment implements View.OnClickListener {

    public static final int NUMBER_OF_TABS = 2;
    public static final int TAB_FIRST = 0;
    public static final int TAB_SECOND = 1;
    public static int selectedTab = 0;

    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private TextView firstTab;
    private TextView secondTab;
    private View firstTabIndicator;
    private View secondTabIndicator;

    private long eventID;
    private PeopleParticipatedFragment mPeopleParticipatedFragment;
    private PeopleAbsentFragment mPeopleAbsentFragment;

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
        View v = inflater.inflate(R.layout.fragment_in_out_list_fragments, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.eventViewPager);

        firstTab = (TextView) v.findViewById(R.id.in_tab);
        firstTab.setOnClickListener(this);
        secondTab = (TextView) v.findViewById(R.id.out_tab);
        secondTab.setOnClickListener(this);

        firstTabIndicator = v.findViewById(R.id.in_tab_indicator);
        secondTabIndicator = v.findViewById(R.id.out_tab_indicator);

        if (getArguments() != null)
            eventID = getArguments().getLong(Constants.EVENT_ID);
        else {
            Toast.makeText(getActivity(), R.string.event_not_found, Toast.LENGTH_LONG).show();
            ((EventActivity) getActivity()).switchToEventFragments();
        }

        Bundle bundle = new Bundle();
        bundle.putLong(Constants.EVENT_ID, eventID);
        mPeopleParticipatedFragment = new PeopleParticipatedFragment();
        mPeopleParticipatedFragment.setArguments(bundle);
        mPeopleAbsentFragment = new PeopleAbsentFragment();
        mPeopleAbsentFragment.setArguments(bundle);

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
                    fragment = mPeopleParticipatedFragment;
                    break;
                case 1:
                    fragment = mPeopleAbsentFragment;
                    break;
                default:
                    fragment = new Fragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_TABS;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.in_tab:
                viewPager.setCurrentItem(TAB_FIRST);
                break;
            case R.id.out_tab:
                viewPager.setCurrentItem(TAB_SECOND);
                break;
            default:
                break;
        }
    }

}