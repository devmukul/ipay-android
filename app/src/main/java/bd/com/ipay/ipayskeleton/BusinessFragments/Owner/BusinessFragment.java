package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.R;

public class BusinessFragment extends Fragment {
    private IconifiedTextViewWithButton mBusinessInformationButton;
    private IconifiedTextViewWithButton mManageEmployeeButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business, container, false);
        getActivity().setTitle(R.string.manage_business);

        mBusinessInformationButton = (IconifiedTextViewWithButton) v.findViewById(R.id.button_business_information);
        mManageEmployeeButton = (IconifiedTextViewWithButton) v.findViewById(R.id.button_manage_employees);

        mBusinessInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BusinessActivity) getActivity()).switchToBusinessInformationFragment();
            }
        });

        mManageEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BusinessActivity) getActivity()).switchToEmployeeManagementFragment();
            }
        });

        return v;
    }
}
