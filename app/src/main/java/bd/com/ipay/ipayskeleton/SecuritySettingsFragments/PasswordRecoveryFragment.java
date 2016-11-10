package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.R;

public class PasswordRecoveryFragment extends Fragment {

    private IconifiedTextViewWithButton securityQuestionHeader;
    private IconifiedTextViewWithButton trustedPersonHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_password_recovery, container, false);
        setTitle();

        securityQuestionHeader = (IconifiedTextViewWithButton) v.findViewById(R.id.security_question);
        trustedPersonHeader = (IconifiedTextViewWithButton) v.findViewById(R.id.trusted_person);

        securityQuestionHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecuritySettingsActivity) getActivity()).switchToSecurityQuestionFragment();
            }
        });

        trustedPersonHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SecuritySettingsActivity) getActivity()).switchToTrustedPersonFragment();
            }
        });
        return v;
    }

    public void setTitle() {
        getActivity().setTitle(R.string.password_recovery);
    }

}