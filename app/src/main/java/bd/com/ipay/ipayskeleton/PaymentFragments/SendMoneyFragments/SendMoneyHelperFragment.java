package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.R;

public class SendMoneyHelperFragment extends Fragment {
    private Button okButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_money_helper, container, false);
        ((SendMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.GONE);
        ((SendMoneyActivity) getActivity()).hideTitle();
        okButton = (Button) view.findViewById(R.id.ok_button);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            try {
                if (bundle.getBoolean("isBackPresent")) {
                    ((SendMoneyActivity) getActivity()).backButton.setVisibility(View.GONE);
                    this.getArguments().clear();
                    setArguments(null);

                } else {
                    ((SendMoneyActivity) getActivity()).backButton.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }
        } else {
            ((SendMoneyActivity) getActivity()).backButton.setVisibility(View.VISIBLE);

        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SendMoneyActivity) getActivity()).switchToSendMoneyContactFragment();
            }
        });
        return view;
    }
}
