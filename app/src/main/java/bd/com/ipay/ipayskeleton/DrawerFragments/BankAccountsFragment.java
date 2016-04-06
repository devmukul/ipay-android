package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.AddBankRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.AddBankResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.DisableBankAccountRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.DisableBankAccountResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.EnableBankAccountRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.EnableBankAccountResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.GetBankListRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.RemoveBankAccountRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.RemoveBankAccountResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BankBranch;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BankBranchRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetBankBranchesResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class BankAccountsFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetBankBranchesTask = null;
    private GetBankBranchesResponse mGetBankBranchesResponse;

    private HttpRequestPostAsyncTask mAddBankTask = null;
    private AddBankResponse mAddBankResponse;

    private HttpRequestPostAsyncTask mRemoveBankAccountTask = null;
    private RemoveBankAccountResponse mRemoveBankAccountResponse;

    private HttpRequestPostAsyncTask mEnableBankAccountTask = null;
    private EnableBankAccountResponse mEnableBankAccountResponse;

    private HttpRequestPostAsyncTask mDisableBankAccountTask = null;
    private DisableBankAccountResponse mDisableBankAccountResponse;

    private HttpRequestPostAsyncTask mGetBankTask = null;
    private GetBankListResponse mBankListResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mBankListRecyclerView;
    private TextView mEmptyListTextView;
    private BankListAdapter mBankListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button addNewBankButton;
    private List<UserBankClass> mListUserBankClasses;
    private String[] bankAccountTypes;
    private ArrayList<BankBranch> bankBranches;
    private ArrayList<String> bankBrancheNames;
    ArrayAdapter<String> mBranchAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bank_accounts, container, false);
        ((HomeActivity) getActivity()).setTitle(R.string.bank_accounts);
        bankBranches = new ArrayList<BankBranch>();
        bankBrancheNames = new ArrayList<>();
        mBranchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, bankBrancheNames);

        mBankListRecyclerView = (RecyclerView) v.findViewById(R.id.list_bank);
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        addNewBankButton = (Button) v.findViewById(R.id.button_add_bank);
        mProgressDialog = new ProgressDialog(getActivity());
        bankAccountTypes = getResources().getStringArray(R.array.default_bank_account_types);

        // It might be possible that we have failed to load the available bank list during
        // application startup. In that case first try to load the available bank list first, and
        // then load user bank details. Otherwise directly load the bank list.
        if (CommonData.isAvailableBankListLoaded()) {
            getBankList();
        } else {
            attemptRefreshAvailableBankNames();
        }

        addNewBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBankAccountDialogue();
            }
        });

        mBankListAdapter = new BankListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBankListRecyclerView.setLayoutManager(mLayoutManager);
        mBankListRecyclerView.setAdapter(mBankListAdapter);

        return v;
    }

    private void attemptRefreshAvailableBankNames() {
        GetAvailableBankAsyncTask mGetAvailableBankAsyncTask = new GetAvailableBankAsyncTask(getActivity(),
                new GetAvailableBankAsyncTask.BankLoadListener() {
                    @Override
                    public void onLoadSuccess(List<Bank> banks) {
                        mProgressDialog.dismiss();
                        getBankList();
                    }

                    @Override
                    public void onLoadFailed() {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_available_bank_list_loading, Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    }
                });
        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_fetching_bank_list));
        mProgressDialog.show();
        mGetAvailableBankAsyncTask.execute();
    }

    ;

    private void getBankList() {
        if (mGetBankTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_fetching_bank_info));
        mProgressDialog.show();
        GetBankListRequest mGetBankListRequest = new GetBankListRequest(Constants.DUMMY);
        Gson gson = new Gson();
        String json = gson.toJson(mGetBankListRequest);
        mGetBankTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_BANK_LIST,
                Constants.BASE_URL_POST_MM + Constants.URL_GET_BANK, json, getActivity());
        mGetBankTask.mHttpResponseListener = this;
        mGetBankTask.execute((Void) null);
    }

    private void addBankAccountDialogue() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.add_a_bank)
                .customView(R.layout.dialogue_add_bank_account, true)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        View view = dialog.getCustomView();
        final Spinner mBankListSpinner = (Spinner) view.findViewById(R.id.spinner_default_bank_accounts);
        final Spinner mAccountTypesSpinner = (Spinner) view.findViewById(R.id.spinner_default_account_types);
        final Spinner mBankBranchSpinner = (Spinner) view.findViewById(R.id.spinner_bank_branch);
        final EditText mAccountNameEditText = (EditText) view.findViewById(R.id.bank_account_name);
        final EditText mAccountNumberEditText = (EditText) view.findViewById(R.id.bank_account_number);

        ArrayAdapter<CharSequence> mAdapterBanks = new ArrayAdapter<CharSequence>(
                getActivity(), android.R.layout.simple_spinner_item, CommonData.getAvailableBankNames());
        mBankListSpinner.setAdapter(mAdapterBanks);

        ArrayAdapter<CharSequence> mAdapterAccountTypes = ArrayAdapter.createFromResource(getActivity(),
                R.array.default_bank_account_types, android.R.layout.simple_spinner_item);
        mAccountTypesSpinner.setAdapter(mAdapterAccountTypes);

        mBankBranchSpinner.setAdapter(mBranchAdapter);

        mBankListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Bank bank = CommonData.getAvailableBanks().get(position);
                getBankBranch(bank.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                BankBranch bankBranch = bankBranches.get(mBankBranchSpinner.getSelectedItemPosition());
                attemptAddBank((int) bankBranch.getId(), mAccountTypesSpinner.getSelectedItemPosition(),
                        mAccountNameEditText.getText().toString().trim(), mAccountNumberEditText.getText().toString().trim());
                dialog.dismiss();
            }
        });

    }

    private void getBankBranch(long bankID) {
        if (mGetBankBranchesTask != null) {
            return;
        }

        BankBranchRequestBuilder mBankBranchRequestBuilder = new BankBranchRequestBuilder(bankID);

        String mUri = mBankBranchRequestBuilder.getGeneratedUri();
        mGetBankBranchesTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BANK_BRANCH_LIST,
                mUri, getActivity());
        mGetBankBranchesTask.mHttpResponseListener = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mGetBankBranchesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mGetBankBranchesTask.execute((Void) null);
        }
    }

    private void attemptAddBank(int bankID, int accountType, String accountName, String accountNumber) {
        if (accountName.length() == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_enter_an_account_name, Toast.LENGTH_LONG).show();
            return;
        }

        if (accountNumber.length() == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_enter_an_account_number, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.adding_bank));
        mProgressDialog.show();
        AddBankRequest mAddBankRequest = new AddBankRequest(bankID, accountType, accountName, accountNumber);
        Gson gson = new Gson();
        String json = gson.toJson(mAddBankRequest);
        mAddBankTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_A_BANK,
                Constants.BASE_URL_POST_MM + Constants.URL_ADD_A_BANK, json, getActivity());
        mAddBankTask.mHttpResponseListener = this;
        mAddBankTask.execute((Void) null);
    }

    private void attemptRemoveBank(long bankAccountID) {
        if (bankAccountID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.removing_bank));
        mProgressDialog.show();
        RemoveBankAccountRequest mRemoveBankAccountRequest = new RemoveBankAccountRequest(bankAccountID);
        Gson gson = new Gson();
        String json = gson.toJson(mRemoveBankAccountRequest);
        mRemoveBankAccountTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REMOVE_A_BANK,
                Constants.BASE_URL_POST_MM + Constants.URL_REMOVE_A_BANK, json, getActivity());
        mRemoveBankAccountTask.mHttpResponseListener = this;
        mRemoveBankAccountTask.execute((Void) null);
    }

    private void attemptEnableBank(long bankAccountID) {
        if (bankAccountID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.enabling_bank));
        mProgressDialog.show();
        EnableBankAccountRequest mEnableBankAccountRequest = new EnableBankAccountRequest(bankAccountID);
        Gson gson = new Gson();
        String json = gson.toJson(mEnableBankAccountRequest);
        mEnableBankAccountTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ENABLE_A_BANK,
                Constants.BASE_URL_POST_MM + Constants.URL_ENABLE_A_BANK, json, getActivity());
        mEnableBankAccountTask.mHttpResponseListener = this;
        mEnableBankAccountTask.execute((Void) null);
    }

    private void attemptDisableBank(long bankAccountID) {
        if (bankAccountID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.disabling_bank));
        mProgressDialog.show();
        DisableBankAccountRequest mDisableBankAccountRequest = new DisableBankAccountRequest(bankAccountID);
        Gson gson = new Gson();
        String json = gson.toJson(mDisableBankAccountRequest);
        mDisableBankAccountTask = new HttpRequestPostAsyncTask(Constants.COMMAND_DISABLE_A_BANK,
                Constants.BASE_URL_POST_MM + Constants.URL_DISABLE_A_BANK, json, getActivity());
        mDisableBankAccountTask.mHttpResponseListener = this;
        mDisableBankAccountTask.execute((Void) null);
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mAddBankTask = null;
            mGetBankTask = null;
            mRemoveBankAccountTask = null;
            mEnableBankAccountTask = null;
            mDisableBankAccountTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_ADD_A_BANK)) {

            if (resultList.size() > 2) {
                try {
                    mAddBankResponse = gson.fromJson(resultList.get(2), AddBankResponse.class);
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mAddBankResponse.getMessage(), Toast.LENGTH_LONG).show();

                        // Refresh bank list
                        if (mListUserBankClasses != null)
                            mListUserBankClasses.clear();
                        mListUserBankClasses = null;
                        getBankList();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.failed_add_bank, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_add_bank, Toast.LENGTH_SHORT).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_add_bank, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mAddBankTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_BANK_LIST)) {

            if (resultList.size() > 2) {
                try {
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        mBankListResponse = gson.fromJson(resultList.get(2), GetBankListResponse.class);

                        if (mListUserBankClasses == null) {
                            mListUserBankClasses = mBankListResponse.getBanks();
                        } else {
                            List<UserBankClass> tempBankClasses;
                            tempBankClasses = mBankListResponse.getBanks();
                            mListUserBankClasses.clear();
                            mListUserBankClasses.addAll(tempBankClasses);
                        }

                        // Sort bank list by active banks to come first
                        sortBankList();

                        if (mListUserBankClasses != null && mListUserBankClasses.size() > 0)
                            mEmptyListTextView.setVisibility(View.GONE);
                        else mEmptyListTextView.setVisibility(View.VISIBLE);
                        mBankListAdapter.notifyDataSetChanged();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mGetBankTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_REMOVE_A_BANK)) {

            if (resultList.size() > 2) {
                try {
                    mRemoveBankAccountResponse = gson.fromJson(resultList.get(2), RemoveBankAccountResponse.class);
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mRemoveBankAccountResponse.getMessage(), Toast.LENGTH_LONG).show();

                        // Refresh bank list
                        if (mListUserBankClasses != null)
                            mListUserBankClasses.clear();
                        mListUserBankClasses = null;
                        getBankList();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mRemoveBankAccountResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_remove_bank, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_remove_bank, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mRemoveBankAccountTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_DISABLE_A_BANK)) {

            if (resultList.size() > 2) {
                try {
                    mDisableBankAccountResponse = gson.fromJson(resultList.get(2), DisableBankAccountResponse.class);
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mDisableBankAccountResponse.getMessage(), Toast.LENGTH_LONG).show();

                        // Refresh bank list
                        if (mListUserBankClasses != null)
                            mListUserBankClasses.clear();
                        mListUserBankClasses = null;
                        getBankList();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mDisableBankAccountResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_disable_bank, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_disable_bank, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mDisableBankAccountTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ENABLE_A_BANK)) {

            if (resultList.size() > 2) {
                try {
                    mEnableBankAccountResponse = gson.fromJson(resultList.get(2), EnableBankAccountResponse.class);
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mEnableBankAccountResponse.getMessage(), Toast.LENGTH_LONG).show();

                        // Refresh bank list
                        if (mListUserBankClasses != null)
                            mListUserBankClasses.clear();
                        mListUserBankClasses = null;
                        getBankList();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mEnableBankAccountResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_enable_bank, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_enable_bank, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mEnableBankAccountTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_BANK_BRANCH_LIST)) {

            if (resultList.size() > 2) {
                try {
                    mGetBankBranchesResponse = gson.fromJson(resultList.get(2), GetBankBranchesResponse.class);
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        bankBranches.clear();
                        bankBrancheNames.clear();

                        bankBranches = (ArrayList) mGetBankBranchesResponse.getAvailableBranches();
                        for (BankBranch branch : bankBranches) {
                            bankBrancheNames.add(branch.getName());
                        }

                        mBranchAdapter.notifyDataSetChanged();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mGetBankBranchesResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_to_fetch_branch, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_to_fetch_branch, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mGetBankBranchesTask = null;

        }
    }

    private void sortBankList() {
        Collections.sort(mListUserBankClasses, new Comparator<UserBankClass>() {
            @Override
            public int compare(UserBankClass lhs, UserBankClass rhs) {
                if (lhs.getAccountStatus() == Constants.BANK_ACCOUNT_STATUS_ACTIVE) return -1;
                else return +1;
            }
        });
    }

    public class BankListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public BankListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mBankName;
            private TextView mBankAccountNumber;
            private ImageView mBankVerifiedStatus;
            private TextView mBranchName;
            private LinearLayout optionsLayout;
            private Button enableDisableButton;
            private Button removeButton;
            private Button verifyButton;
            private View verifyDivider;
            private CardView mBankCard;

            public ViewHolder(final View itemView) {
                super(itemView);

                mBankAccountNumber = (TextView) itemView.findViewById(R.id.bank_account_number);
                mBankName = (TextView) itemView.findViewById(R.id.bank_name);
                mBankVerifiedStatus = (ImageView) itemView.findViewById(R.id.bank_account_verify_status);
                mBranchName = (TextView) itemView.findViewById(R.id.bank_branch_name);
                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                enableDisableButton = (Button) itemView.findViewById(R.id.enable_disable_button);
                removeButton = (Button) itemView.findViewById(R.id.remove_button);
                verifyButton = (Button) itemView.findViewById(R.id.verify_button);
                verifyDivider = (View) itemView.findViewById(R.id.verify_divider);
                mBankCard = (CardView) itemView.findViewById(R.id.bank_account_card);
            }

            public void bindView(int pos) {

                final long bankAccountID = mListUserBankClasses.get(pos).getBankAccountId();
                final int bankStatus = mListUserBankClasses.get(pos).getAccountStatus();
                String bankName = mListUserBankClasses.get(pos).getBankName();
                String branchName = mListUserBankClasses.get(pos).getBranchName();
                mBankAccountNumber.setText(mListUserBankClasses.get(pos).getAccountNumber());
                mBankName.setText(bankName);
                mBranchName.setText(branchName);

                if (bankStatus == Constants.BANK_ACCOUNT_STATUS_ACTIVE) {
                    enableDisableButton.setText(R.string.disable);
                    mBankCard.setBackgroundColor(getResources().getColor(android.R.color.white));

                } else if (bankStatus == Constants.BANK_ACCOUNT_STATUS_INACTIVE) {
                    enableDisableButton.setText(R.string.enable);
                    mBankCard.setBackgroundColor(getResources().getColor(R.color.home_background));

                } else {
                    enableDisableButton.setText(R.string.enable);
                    enableDisableButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    optionsLayout.setEnabled(false);
                    mBankCard.setBackgroundColor(getResources().getColor(R.color.home_background));
                }

                if (mListUserBankClasses.get(pos).getVerificationStatus().equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED)) {
                    mBankVerifiedStatus.setImageResource(R.drawable.ic_verified);
                    verifyDivider.setVisibility(View.GONE);
                    verifyButton.setVisibility(View.GONE);

                } else if (mListUserBankClasses.get(pos).getVerificationStatus().equals(Constants.BANK_ACCOUNT_STATUS_NOT_VERIFIED)) {
                    mBankVerifiedStatus.setImageResource(R.drawable.ic_error_black_24dp);
                    mBankVerifiedStatus.setColorFilter(Color.RED);

                } else {
                    mBankVerifiedStatus.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                    mBankVerifiedStatus.setColorFilter(Color.YELLOW);
                }

                enableDisableButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.are_you_sure)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (bankStatus == Constants.BANK_ACCOUNT_STATUS_ACTIVE) {
                                            attemptDisableBank(bankAccountID);
                                        } else if (bankStatus == Constants.BANK_ACCOUNT_STATUS_INACTIVE) {
                                            attemptEnableBank(bankAccountID);
                                        }
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                    }
                                })
                                .show();
                    }
                });

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.are_you_sure)
                                .setMessage(R.string.remove_this_account_query)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Utilities.isConnectionAvailable(getActivity())) {
                                            attemptRemoveBank(bankAccountID);
                                        }
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                    }
                                })
                                .show();
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (optionsLayout.getVisibility() == View.VISIBLE)
                            optionsLayout.setVisibility(View.GONE);
                        else optionsLayout.setVisibility(View.VISIBLE);

                        new MaterialShowcaseView.Builder(getActivity())
                                .setTarget(verifyButton)
                                .setDismissText(R.string.got_it)
                                .setContentText(Html.fromHtml(getString(R.string.bank_verification_help_html)))
                                .setDelay(100) // optional but starting animations immediately in onCreate can make them choppy
                                .singleUse(bankAccountID + "6") // provide a unique ID used to ensure it is only shown once
                                .show();
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bank_accounts,
                    parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            try {
                ViewHolder vh = (ViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mListUserBankClasses != null)
                return mListUserBankClasses.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
