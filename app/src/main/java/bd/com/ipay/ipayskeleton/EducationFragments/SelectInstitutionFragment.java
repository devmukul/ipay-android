package bd.com.ipay.ipayskeleton.EducationFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ManageBanksActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.AddBankDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.AddBankRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.AddBankResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.VerifyBankAccountRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.VerifyBankAccountResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Education.GetInstitutionsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BankBranch;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BankBranchRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetBankBranchesResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetOccupationResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SelectInstitutionFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetAllInstitutionsTask = null;
    private GetInstitutionsResponse mGetInstitutionsResponse;

    private HttpRequestGetAsyncTask mGetSessionsByInstitutionTask = null;
    private GetBankBranchesResponse mGetBankBranchesResponse;

    private ProgressDialog mProgressDialog;
    private Button nextButton;

    // Contains a list of bank branch corresponding to each district
    private ArrayList<String> mInstitutionsList;
    private ArrayList<String> mSessionList;

    private ResourceSelectorDialog<Bank> bankSelectorDialog;
    private AddBankDialog<String> districtSelectorDialog;
    private AddBankDialog<String> bankBranchSelectorDialog;
    private int mSelectedSessionId = -1;
    private int mSelectedInstitutionId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_education_select_institutions, container, false);

        mInstitutionsList = new ArrayList<String>();
        mSessionList = new ArrayList<String>();

        mProgressDialog = new ProgressDialog(getActivity());
        nextButton = (Button) v.findViewById(R.id.button_next);

        mSelectedInstitutionId = -1;
        getAllInstitutions();

        return v;
    }

    private void setBankAdapter(List<Bank> bankList) {

        bankSelectorDialog = new ResourceSelectorDialog<>(getContext(), bankList, mSelectedInstitutionId);
        bankSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mBankListSelection.setError(null);
                mBankListSelection.setText(name);
                mSelectedInstitutionId = id;
                mSelectedDistrictId = -1;
                getInstitutions(mSelectedInstitutionId);
            }
        });

        mBankListSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankSelectorDialog.show();
            }
        });
    }

    private void setDistrictAdapter(List<String> districtList) {

        districtSelectorDialog = new AddBankDialog<>(getContext(), districtList, mSelectedDistrictId);
        districtSelectorDialog.setOnDistrictSelectedListener(new AddBankDialog.OnDistrictSelectedListener() {
            @Override
            public void onDistrictSelected(int id, String name) {
                mDistrictSelection.setError(null);
                mDistrictSelection.setText(name);
                mSelectedDistrictId = id;

                mSelectedSessionId = -1;

                mBranches = new ArrayList<>();
                if (bankDistrictToBranchMap.containsKey(name)) {
                    mBranches.addAll(bankDistrictToBranchMap.get(name));
                }

                mBranchNames.clear();
                mBranchNames.add(getString(R.string.select_one));
                for (BankBranch bankBranch : mBranches) {
                    mBranchNames.add(bankBranch.getName());
                }
                setBankBranchAdapter(mBranchNames);

            }
        });

        mDistrictSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                districtSelectorDialog.show();
            }
        });
    }

    private void setBankBranchAdapter(List<String> bankBranchList) {

        bankBranchSelectorDialog = new AddBankDialog<>(getContext(), bankBranchList, mSelectedInstitutionId);
        bankBranchSelectorDialog.setOnDistrictSelectedListener(new AddBankDialog.OnDistrictSelectedListener() {
            @Override
            public void onDistrictSelected(int id, String name) {
                mBankBranchSelection.setError(null);
                mBankBranchSelection.setText(name);
                mSelectedSessionId = id;
            }
        });

        mBankBranchSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankBranchSelectorDialog.show();
            }
        });
    }

    private void getAllInstitutions() {
        if (mGetAllInstitutionsTask != null) {
            return;
        }

        setContentShown(false);

        mGetAllInstitutionsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INSTITUTION_LIST,
                Constants.BASE_URL_EDU + Constants.URL_GET_ALL_INSTITUTIONS_LIST, getActivity(), this);
        mGetAllInstitutionsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetAllInstitutionsTask = null;
            mGetSessionsByInstitutionTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_INSTITUTION_LIST)) {

            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    // TODO: setAdapter of the institution spinner
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.get_all_institution_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.get_all_institution_failed, Toast.LENGTH_SHORT).show();
            }

            mGetAllInstitutionsTask = null;
        }
    }

}
