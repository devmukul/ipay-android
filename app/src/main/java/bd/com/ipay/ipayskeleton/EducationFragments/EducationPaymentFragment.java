package bd.com.ipay.ipayskeleton.EducationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.EducationPaymentActivity;
import bd.com.ipay.ipayskeleton.R;

public class EducationPaymentFragment extends Fragment {

    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDepartmentView;
    private Button mContinueButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_education_show_student_info, container, false);
        getActivity().setTitle(R.string.student_info);

        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDepartmentView = (TextView) v.findViewById(R.id.textview_department);
        mContinueButton = (Button) v.findViewById(R.id.button_continue);

        if (getArguments() != null) setStudentInformation(getArguments());

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EducationPaymentActivity) getActivity()).switchToEducationPaymentFragment();
            }
        });

        return v;
    }

    private void setStudentInformation(Bundle args) {

        mMobileNumberView.setText(args.getString(EducationPaymentActivity.STUDENT_MOBILE_NUMBER));
        mNameView.setText(args.getString(EducationPaymentActivity.STUDENT_NAME));
        mDepartmentView.setText(args.getString(EducationPaymentActivity.STUDENT_DEPARTMENT));
    }
}
