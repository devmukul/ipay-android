package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.analytics.Tracker;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SelectAccountTypeFragment extends BaseFragment {

    private Button buttonAccountTypePersonal;
    private Button buttonAccountTypeBusiness;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_select_account_type, container, false);

        buttonAccountTypePersonal = (Button) v.findViewById(R.id.button_account_type_personal);
        buttonAccountTypeBusiness = (Button) v.findViewById(R.id.button_account_type_business);

        buttonAccountTypePersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignupOrLoginActivity) getActivity()).switchToSignupPersonalStepOneFragment();
            }
        });

        buttonAccountTypeBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignupOrLoginActivity) getActivity()).switchToBusinessStepOneFragment();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_select_account_type_page);
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_select_account_type) );
    }
}
