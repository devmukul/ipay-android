package bd.com.ipay.ipayskeleton.ManageBanksFragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Activities.ManageBanksActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.EditTextWithProgressBar;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BankBranch;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BankBranchRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.GetBankBranchesResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToastandLogger.ToastWrapper;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class LinkBankFragment extends Fragment implements HttpResponseListener {

    private final String STARTED_FROM_PROFILE_ACTIVITY = "started_from_profile_activity";

    private HttpRequestGetAsyncTask mGetBankBranchesTask = null;
    private GetBankBranchesResponse mGetBankBranchesResponse;

    private ProgressDialog mProgressDialog;
    private List<UserBankClass> mListUserBankClasses;

    // Contains a list of bank branch corresponding to each district
    private Map<String, ArrayList<BankBranch>> bankDistrictToBranchMap;
    private ArrayList<String> mDistrictNames;
    private ArrayList<BankBranch> mBranches;
    private ArrayList<String> mBranchNames;
    private ArrayList<Bank> bankNames;

    private EditText mBankListSelection;
    private EditText mDistrictSelection;
    private EditText mBankBranchSelection;
    private EditText mAccountNameEditText;
    private EditText mAccountNumberEditText;
    private Button addBank;
    private EditTextWithProgressBar mBankBranchEditTextProgressBar;

    private ResourceSelectorDialog<Bank> bankSelectorDialog;
    private CustomSelectorDialog districtSelectorDialog;
    private CustomSelectorDialog bankBranchSelectorDialog;

    private String mSelectedBankName;
    private String mBankAccountNumber;
    private int mSelectedBranchId = -1;
    private int mSelectedBankId = -1;
    private int mSelectedDistrictId = -1;

    private boolean startedFromProfileCompletion = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_link_bank, container, false);
        getActivity().setTitle(R.string.link_bank);
        getActivity().setTitleColor(Color.WHITE);

        Bundle args = getArguments();
        if (args != null)
            startedFromProfileCompletion = args.getBoolean(STARTED_FROM_PROFILE_ACTIVITY);

        mSelectedBankId = -1;
        mDistrictNames = new ArrayList<>();
        mBranches = new ArrayList<>();
        mBranchNames = new ArrayList<>();
        bankNames = new ArrayList<>();
        if (CommonData.getAvailableBanks() != null)
            bankNames.addAll((ArrayList) CommonData.getAvailableBanks());

        mProgressDialog = new ProgressDialog(getActivity());
        mBankListSelection = (EditText) v.findViewById(R.id.default_bank_accounts);
        mDistrictSelection = (EditText) v.findViewById(R.id.branch_districts);
        mAccountNameEditText = (EditText) v.findViewById(R.id.bank_account_name);
        mAccountNumberEditText = (EditText) v.findViewById(R.id.bank_account_number);
        addBank = (Button) v.findViewById(R.id.button_add_bank);
        mBankBranchEditTextProgressBar = (EditTextWithProgressBar) v.findViewById(R.id.editText_with_progressBar_branch);
        mBankBranchSelection = mBankBranchEditTextProgressBar.getEditText();

        setBankAdapter(bankNames);

        mAccountNameEditText.setText(ProfileInfoCacheManager.getName());

        addBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUserInputs();
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        ((ManageBanksActivity) getActivity()).mSelectedBankId = mSelectedBankId;
        ((ManageBanksActivity) getActivity()).mSelectedDistrictId = mSelectedDistrictId;
        ((ManageBanksActivity) getActivity()).mSelectedBranchId = mSelectedBranchId;
        ((ManageBanksActivity) getActivity()).mDistrictNames = mDistrictNames;
        ((ManageBanksActivity) getActivity()).mBranches = mBranches;
        ((ManageBanksActivity) getActivity()).mBranchNames = mBranchNames;
    }

    @Override
    public void onResume() {
        super.onResume();

        mSelectedBankId = ((ManageBanksActivity) getActivity()).mSelectedBankId;
        mSelectedDistrictId = ((ManageBanksActivity) getActivity()).mSelectedDistrictId;
        mSelectedBranchId = ((ManageBanksActivity) getActivity()).mSelectedBranchId;
        mDistrictNames = ((ManageBanksActivity) getActivity()).mDistrictNames;
        mBranches = ((ManageBanksActivity) getActivity()).mBranches;
        mBranchNames = ((ManageBanksActivity) getActivity()).mBranchNames;
    }

    private void setBankAdapter(List<Bank> bankList) {

        bankSelectorDialog = new ResourceSelectorDialog<>(getContext(), getString(R.string.select_a_bank), bankList);
        bankSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mBankListSelection.setError(null);
                mBankListSelection.setText(name);
                mSelectedBankId = id;
                mSelectedDistrictId = -1;
                mSelectedBankName = name;
                getBankBranches(mSelectedBankId);
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

        districtSelectorDialog = new CustomSelectorDialog(getContext(), getString(R.string.select_a_district), districtList);
        districtSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mDistrictSelection.setError(null);
                mDistrictSelection.setText(name);
                mSelectedDistrictId = id;
                mSelectedBranchId = -1;

                mBranches = new ArrayList<>();
                if (bankDistrictToBranchMap.containsKey(name)) {
                    mBranches.addAll(bankDistrictToBranchMap.get(name));
                }

                mBranchNames.clear();
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

        bankBranchSelectorDialog = new CustomSelectorDialog(getContext(), getString(R.string.bank_branch), bankBranchList);
        bankBranchSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mBankBranchSelection.setError(null);
                mBankBranchSelection.setText(name);
                mSelectedBranchId = id;
            }
        });

        mBankBranchSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankBranchSelectorDialog.show();
            }
        });
    }

    private void verifyUserInputs() {
        // The first position is "Select One"
        View focusView;
        if (mSelectedBankId < 0) {
            mBankListSelection.setError(getString(R.string.please_select_a_bank));
        } else if (mSelectedDistrictId < 0) {
            mDistrictSelection.setError(getString(R.string.please_select_a_district));
        } else if (mSelectedBranchId < 0) {
            mBankBranchSelection.setError(getString(R.string.please_select_a_branch));
        } else if (mAccountNameEditText.getText().toString().trim().length() == 0) {
            mAccountNameEditText.setError(getString(R.string.please_enter_an_account_name));
            focusView = mAccountNameEditText;
            focusView.requestFocus();
        } else if (mAccountNumberEditText.getText().toString().trim().length() == 0) {
            mAccountNumberEditText.setError(getString(R.string.please_enter_an_account_number));
            focusView = mAccountNumberEditText;
            focusView.requestFocus();
        } else if (mAccountNumberEditText.getText().toString().trim().length() < 10) {
            mAccountNumberEditText.setError(getString(R.string.please_enter_an_account_number_of_minimum_digit));
            focusView = mAccountNumberEditText;
            focusView.requestFocus();
        } else {
            Utilities.hideKeyboard(getActivity());
            launchAddBankAgreementPage();
        }
    }

    private void launchAddBankAgreementPage() {
        BankBranch bankBranch = mBranches.get(mSelectedBranchId);
        mBankAccountNumber = mAccountNumberEditText.getText().toString().trim();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BANK_NAME, mSelectedBankName);
        bundle.putParcelable(Constants.BANK_BRANCH, bankBranch);
        bundle.putString(Constants.BANK_ACCOUNT_NUMBER, mBankAccountNumber);
        bundle.putBoolean(Constants.IS_STARTED_FROM_PROFILE_COMPLETION, startedFromProfileCompletion);

        ((ManageBanksActivity) getActivity()).switchToAddBankAgreementFragment(bundle);
    }

    private void getBankBranches(long bankID) {
        if (mGetBankBranchesTask != null)
            return;

        mBankBranchEditTextProgressBar.showProgressBar();
        BankBranchRequestBuilder mBankBranchRequestBuilder = new BankBranchRequestBuilder(bankID);

        String mUri = mBankBranchRequestBuilder.getGeneratedUri();
        mGetBankBranchesTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BANK_BRANCH_LIST,
                mUri, getActivity());
        mGetBankBranchesTask.mHttpResponseListener = this;

        mGetBankBranchesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            if (getActivity() != null)
                ToastWrapper.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_BANK_BRANCH_LIST:

                try {
                    mGetBankBranchesResponse = gson.fromJson(result.getJsonString(), GetBankBranchesResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mDistrictNames.clear();

                        bankDistrictToBranchMap = new HashMap<>();

                        for (BankBranch branch : mGetBankBranchesResponse.getAvailableBranches()) {
                            if (!bankDistrictToBranchMap.containsKey(branch.getDistrict())) {
                                bankDistrictToBranchMap.put(branch.getDistrict(), new ArrayList<BankBranch>());
                                mDistrictNames.add(branch.getDistrict());
                            }
                            bankDistrictToBranchMap.get(branch.getDistrict()).add(branch);
                        }

                        setDistrictAdapter(mDistrictNames);
                        mBankBranchEditTextProgressBar.hideProgressBar();

                    } else {
                        if (getActivity() != null)
                            ToastWrapper.makeText(getActivity(), mGetBankBranchesResponse.getMessage(), Toast.LENGTH_LONG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        ToastWrapper.makeText(getActivity(), R.string.failed_to_fetch_branch, Toast.LENGTH_LONG);
                }

                mProgressDialog.dismiss();
                mGetBankBranchesTask = null;

                break;

            default:
                break;
        }
    }

}
