package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.R;

public class BusinessBasicInfoFragment extends Fragment {
    private IconifiedTextViewWithButton mBusinessInformationButton;
    private IconifiedTextViewWithButton mBusinessContact;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business_basic_info, container, false);
        getActivity().setTitle(R.string.basic_info);

        mBusinessInformationButton = (IconifiedTextViewWithButton) v.findViewById(R.id.button_business_information);
        mBusinessContact = (IconifiedTextViewWithButton) v.findViewById(R.id.business_contact);

        mBusinessInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToBusinessInfoFragment();
            }
        });

        mBusinessContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToBusinessContactFragment();
            }
        });

        return v;
    }
}
