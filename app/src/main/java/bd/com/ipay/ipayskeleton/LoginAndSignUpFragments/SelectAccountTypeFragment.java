package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.R;

public class SelectAccountTypeFragment extends Fragment {

    private Button buttonAccountTypePersonal;
    private Button buttonAccountTypeBusiness;
    private Button buttonLoginWithExistingAccount;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_select_account_type_page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_select_account_type, container, false);

        buttonAccountTypePersonal = (Button) v.findViewById(R.id.button_account_type_personal);
        buttonAccountTypeBusiness = (Button) v.findViewById(R.id.button_account_type_business);
        buttonLoginWithExistingAccount = (Button) v.findViewById(R.id.login_with_existing_account);

        buttonAccountTypePersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((SignupOrLoginActivity) getActivity()).switchToPersonalSignUpFragment();
                ((SignupOrLoginActivity) getActivity()).switchToSignupPersonalStepOneFragment();
            }
        });

        buttonAccountTypeBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignupOrLoginActivity) getActivity()).switchToBusinessStepOneFragment();
            }
        });

        buttonLoginWithExistingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignupOrLoginActivity) getActivity()).switchToLoginFragment();
            }
        });

        return v;
    }
}
