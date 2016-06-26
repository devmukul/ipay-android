package bd.com.ipay.ipayskeleton.BusinessFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bd.com.ipay.ipayskeleton.R;

public class EmployeeManagementFragment extends Fragment {

    private Button mAddEmployeeButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_management, container, false);

        mAddEmployeeButton = (Button) v.findViewById(R.id.button_add_employee);
        mAddEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BusinessActivity) getActivity()).switchToEmployeeInformationFragment(null);
            }
        });

        return v;
    }
}
