package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.R;

public class RequestMoneyHelperFragment extends Fragment {
    private Button okButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_money_helper, container, false);
        ((RequestMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.GONE);
        ((RequestMoneyActivity) getActivity()).hideTitle();
        okButton = (Button) view.findViewById(R.id.ok_button);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            try {
                if (bundle.getBoolean("isBackPresent")) {
                    ((RequestMoneyActivity) getActivity()).backButton.setVisibility(View.GONE);
                    this.getArguments().clear();
                    setArguments(null);

                } else {
                    ((RequestMoneyActivity) getActivity()).backButton.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }
        } else {
            ((RequestMoneyActivity) getActivity()).backButton.setVisibility(View.VISIBLE);

        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RequestMoneyActivity) getActivity()).switchToRequestMoneyContactFragment();
            }
        });
        return view;
    }
}
