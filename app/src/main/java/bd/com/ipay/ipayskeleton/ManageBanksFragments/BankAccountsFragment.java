package bd.com.ipay.ipayskeleton.ManageBanksFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.RemoveBankAccountResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.VerifyBankAccountRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.VerifyBankAccountResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.VerifyBankWithAmountRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.VerifyBankWithAmountResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class BankAccountsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestDeleteAsyncTask mRemoveBankAccountTask = null;
    private RemoveBankAccountResponse mRemoveBankAccountResponse;

    private HttpRequestPostAsyncTask mSendForVerificationWithAmountTask = null;
    private VerifyBankWithAmountResponse mVerifyBankWithAmountResponse;

    private HttpRequestGetAsyncTask mGetBankTask = null;
    private GetBankListResponse mBankListResponse;

    private HttpRequestPostAsyncTask mSendForVerificationTask = null;
    private VerifyBankAccountResponse mVerifyBankAccountResponse;

    private ProgressDialog mProgressDialog;
    private RecyclerView mBankListRecyclerView;
    private TextView mEmptyListTextView;
    private UserBankListAdapter mUserBankListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserBankClass> mListUserBankClasses;

    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onResume() {
        super.onResume();
        attemptRefreshAvailableBankNames();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bank_accounts, container, false);
        getActivity().setTitle(R.string.bank_list);

        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mBankListRecyclerView = (RecyclerView) v.findViewById(R.id.list_bank);
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mProgressDialog = new ProgressDialog(getActivity());

        mUserBankListAdapter = new UserBankListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBankListRecyclerView.setLayoutManager(mLayoutManager);
        mBankListRecyclerView.setAdapter(mUserBankListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    getBankList();
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
        
        if (PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_BANK_UPDATE))
            getBankList();
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_BANK_UPDATE);

            if (json == null)
                getBankList();
            else {
                processGetBankListResponse(json);
            }
        }
    }

    private void attemptRefreshAvailableBankNames() {
        GetAvailableBankAsyncTask mGetAvailableBankAsyncTask = new GetAvailableBankAsyncTask(getActivity(),
                new GetAvailableBankAsyncTask.BankLoadListener() {
                    @Override
                    public void onLoadSuccess() {
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

        mGetAvailableBankAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankList() {
        if (mGetBankTask != null) {
            return;
        }

        mGetBankTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BANK_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_BANK, getActivity());
        mGetBankTask.mHttpResponseListener = this;
        mGetBankTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptVerificationWithAmount(Long userBankID, double amount) {
        if (userBankID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        if (amount <= 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.please_enter_amount, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.sending_for_verification_with_amount));
        mProgressDialog.show();
        VerifyBankWithAmountRequest mVerifyBankWithAmountRequest = new VerifyBankWithAmountRequest(userBankID, amount);
        Gson gson = new Gson();
        String json = gson.toJson(mVerifyBankWithAmountRequest);
        mSendForVerificationWithAmountTask = new HttpRequestPostAsyncTask(Constants.COMMAND_VERIFICATION_BANK_WITH_AMOUNT,
                Constants.BASE_URL_SM + Constants.URL_BANK_VERIFICATION_WITH_AMOUNT, json, getActivity());
        mSendForVerificationWithAmountTask.mHttpResponseListener = this;
        mSendForVerificationWithAmountTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptRemoveBank(long bankAccountID) {
        if (bankAccountID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(getString(R.string.removing_bank));
        mProgressDialog.show();
        mRemoveBankAccountTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_REMOVE_A_BANK,
                Constants.BASE_URL_MM + Constants.URL_REMOVE_A_BANK + bankAccountID, getActivity());
        mRemoveBankAccountTask.mHttpResponseListener = this;
        mRemoveBankAccountTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void processGetBankListResponse(String json) {
        Gson gson = new Gson();
        mBankListResponse = gson.fromJson(json, GetBankListResponse.class);

        if (mListUserBankClasses == null) {
            mListUserBankClasses = mBankListResponse.getBanks();
        } else {
            List<UserBankClass> tempBankClasses;
            tempBankClasses = mBankListResponse.getBanks();
            mListUserBankClasses.clear();
            mListUserBankClasses.addAll(tempBankClasses);
        }

        if (mListUserBankClasses != null && mListUserBankClasses.size() > 0)
            mEmptyListTextView.setVisibility(View.GONE);
        else mEmptyListTextView.setVisibility(View.VISIBLE);

        mUserBankListAdapter.notifyDataSetChanged();

        PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_BANK_UPDATE, false);

    }

    private void attemptSendForVerification(Long userBankID) {
        if (userBankID == 0) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        VerifyBankAccountRequest mVerifyBankAccountRequest = new VerifyBankAccountRequest(userBankID);
        Gson gson = new Gson();
        String json = gson.toJson(mVerifyBankAccountRequest);
        mSendForVerificationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_FOR_VERIFICATION_BANK,
                Constants.BASE_URL_SM + Constants.URL_SEND_FOR_VERIFICATION_BANK, json, getActivity());
        mSendForVerificationTask.mHttpResponseListener = this;
        mSendForVerificationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetBankTask = null;
            mRemoveBankAccountTask = null;
            mSendForVerificationTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        if (this.isAdded())
            setContentShown(true);

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_BANK_LIST:
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                        processGetBankListResponse(result.getJsonString());

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mSwipeRefreshLayout.setRefreshing(false);
                mGetBankTask = null;

                break;
            case Constants.COMMAND_REMOVE_A_BANK:

                try {
                    mRemoveBankAccountResponse = gson.fromJson(result.getJsonString(), RemoveBankAccountResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
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

                mProgressDialog.dismiss();
                mRemoveBankAccountTask = null;

                break;
            case Constants.COMMAND_VERIFICATION_BANK_WITH_AMOUNT:

                try {
                    mVerifyBankWithAmountResponse = gson.fromJson(result.getJsonString(), VerifyBankWithAmountResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mVerifyBankWithAmountResponse.getMessage(), Toast.LENGTH_LONG).show();

                        // Refresh bank list
                        if (mListUserBankClasses != null)
                            mListUserBankClasses.clear();
                        mListUserBankClasses = null;
                        getBankList();

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mVerifyBankWithAmountResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_to_bank_verification, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mSendForVerificationWithAmountTask = null;
                break;
            case Constants.COMMAND_SEND_FOR_VERIFICATION_BANK:

                try {
                    mVerifyBankAccountResponse = gson.fromJson(result.getJsonString(), VerifyBankAccountResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mVerifyBankAccountResponse.getMessage(), Toast.LENGTH_LONG).show();

                        // Refresh bank list
                        if (mListUserBankClasses != null)
                            mListUserBankClasses.clear();
                        mListUserBankClasses = null;

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mVerifyBankAccountResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_to_send_for_bank_verification, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mSendForVerificationTask = null;
                break;
        }
    }

    public class UserBankListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;
        private static final int BANK_LIST_ITEM_VIEW = 2;

        public UserBankListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mBankName;
            private final TextView mBankAccountNumber;
            private final ImageView mBankVerifiedStatus;
            private final TextView mBranchName;
            private final View divider;
            private final ImageView bankIcon;

            private CustomSelectorDialog mCustomSelectorDialog;
            private List<String> mBankActionList;

            public ViewHolder(final View itemView) {
                super(itemView);

                mBankAccountNumber = (TextView) itemView.findViewById(R.id.bank_account_number);
                mBankName = (TextView) itemView.findViewById(R.id.bank_name);
                mBankVerifiedStatus = (ImageView) itemView.findViewById(R.id.bank_account_verify_status);
                mBranchName = (TextView) itemView.findViewById(R.id.bank_branch_name);
                divider = itemView.findViewById(R.id.divider);
                bankIcon = (ImageView) itemView.findViewById(R.id.portrait);
            }

            public void bindView(int pos) {

                final long bankAccountID = mListUserBankClasses.get(pos).getBankAccountId();
                final String bankName = mListUserBankClasses.get(pos).getBankName();
                final String branchName = mListUserBankClasses.get(pos).getBranchName();
                final String verificationStatus = mListUserBankClasses.get(pos).getVerificationStatus();
                Drawable icon = getResources().getDrawable(mListUserBankClasses.get(pos).getBankIcon(getContext()));

                mBankAccountNumber.setText(mListUserBankClasses.get(pos).getAccountNumber());
                mBankName.setText(bankName);
                mBranchName.setText(branchName);
                bankIcon.setImageDrawable(icon);

                switch (verificationStatus) {
                    case Constants.BANK_ACCOUNT_STATUS_VERIFIED:
                        mBankVerifiedStatus.setImageResource(R.drawable.ic_verified);
                        mBankVerifiedStatus.clearColorFilter();

                        mBankActionList = Arrays.asList(getResources().getStringArray(R.array.verified_bank_action));

                        break;
                    case Constants.BANK_ACCOUNT_STATUS_NOT_VERIFIED:
                        mBankVerifiedStatus.setImageResource(R.drawable.ic_notverified);
                        mBankVerifiedStatus.setColorFilter(Color.RED);
                        attemptSendForVerification(bankAccountID);
                        mBankActionList = Arrays.asList(getResources().getStringArray(R.array.not_verified_bank_action));
                        break;
                    default:

                        // Bank verification status pending
                        mBankVerifiedStatus.setImageResource(R.drawable.ic_workinprogress);
                        mBankVerifiedStatus.setColorFilter(Color.GRAY);

                        mBankActionList = Arrays.asList(getResources().getStringArray(R.array.not_verified_bank_action));
                        break;
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomSelectorDialog = new CustomSelectorDialog(getActivity(), bankName, mBankActionList);
                        mCustomSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
                            @Override
                            public void onResourceSelected(int selectedIndex, String action) {
                                if (Constants.ACTION_TYPE_REMOVE.equals(action)) {
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage(R.string.are_you_sure_to_remove_bank_account)
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

                                } else if (Constants.ACTION_TYPE_VERIFY.equals(action)) {
                                    if (!verificationStatus.equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED)) {

                                        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                                .title(R.string.enter_the_amount_we_sent)
                                                .customView(R.layout.dialog_verify_bank_with_amount, true)
                                                .positiveText(R.string.submit)
                                                .negativeText(R.string.cancel)
                                                .show();

                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                        View view = dialog.getCustomView();
                                        final EditText mAmountEditText = (EditText) view.findViewById(R.id.amount);


                                        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                imm.hideSoftInputFromWindow(mAmountEditText.getWindowToken(), 0);
                                                if (mAmountEditText.getText().toString().trim().length() == 0) {
                                                    mAmountEditText.setError(getString(R.string.please_enter_amount));
                                                    mAmountEditText.requestFocus();
                                                    Toast.makeText(getActivity(), R.string.please_enter_amount, Toast.LENGTH_LONG).show();

                                                } else {
                                                    String amount = mAmountEditText.getText().toString().trim();
                                                    if (Utilities.isConnectionAvailable(getActivity()))
                                                        attemptVerificationWithAmount(bankAccountID, Double.parseDouble(amount));
                                                    else
                                                        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                }
                                            }
                                        });

                                        dialog.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                imm.hideSoftInputFromWindow(mAmountEditText.getWindowToken(), 0);
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        mCustomSelectorDialog.show();
                    }
                });
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == FOOTER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bank_footer, parent, false);
                return new FooterViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bank_accounts, parent, false);
                return new ViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                if (holder instanceof ViewHolder) {
                    ViewHolder vh = (ViewHolder) holder;
                    vh.bindView(position);
                } else if (holder instanceof FooterViewHolder) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mListUserBankClasses != null)
                return mListUserBankClasses.size() + 1;
            else return 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return FOOTER_VIEW;
            } else {
                return BANK_LIST_ITEM_VIEW;
            }
        }
    }
}
