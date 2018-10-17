package bd.com.ipay.ipayskeleton.PaymentFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;

public abstract class IpayAbstractDpsInputFragment extends Fragment {
    private Button continueButton;
    private EditText dpsNumberEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dps_account_number_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        }
        dpsNumberEditText = (EditText) view.findViewById(R.id.dps_number_edit_text);
        continueButton = (Button) view.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyInput()) {
                    performButtonAction();
                }

            }
        });

    }

    public void showErrorMessage(String errorMessage) {
        if (!TextUtils.isEmpty(errorMessage) && getActivity() != null) {
            Snackbar snackbar = Snackbar.make(continueButton, errorMessage, Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
            ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
            layoutParams.height = continueButton.getHeight();
            snackbarView.setLayoutParams(layoutParams);
            TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ActivityCompat.getColor(getActivity(), android.R.color.white));
            snackbar.show();
        }
    }

    public String getDpsNumber() {
        if(dpsNumberEditText.getText() == null ){
            return "";
        }
        else {
            if(dpsNumberEditText.getText().toString() == null || dpsNumberEditText.getText().toString().equals("")){
                return "";
            }
            else{
                return dpsNumberEditText.getText().toString();
            }
        }
    }

    public abstract boolean verifyInput();

    public abstract void performButtonAction();
}
