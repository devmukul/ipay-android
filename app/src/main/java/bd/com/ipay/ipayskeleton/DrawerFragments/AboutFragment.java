package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AboutFragment extends Fragment {

    private TextView mBuildNumberView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        mBuildNumberView = (TextView) v.findViewById(R.id.textview_build_number);

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            mBuildNumberView.setText("Version: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return v;
    }

}
