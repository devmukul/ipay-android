package bd.com.ipay.ipayskeleton.BusinessFragments.ManagePeopleFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;

public class ManagedBusinessAccountSettingsFragment extends Fragment {
    private TextView mViewAccesListTextView;
    private TextView mResignFromBusinessTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_managed_business_account_settings, container, false);
        setUpViews(view);
        return view;
    }

    private void setUpViews(View view) {
        mViewAccesListTextView = (TextView) view.findViewById(R.id.acces_list_text_view);
        mResignFromBusinessTextView = (TextView) view.findViewById(R.id.leave_account_text_view);
        setButtonActions();
    }

    private void setButtonActions() {
        mViewAccesListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mResignFromBusinessTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
