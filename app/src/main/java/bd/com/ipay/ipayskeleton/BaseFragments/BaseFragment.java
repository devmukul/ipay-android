package bd.com.ipay.ipayskeleton.BaseFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.Tracker;

import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class BaseFragment extends Fragment {

    protected Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
    }
}
