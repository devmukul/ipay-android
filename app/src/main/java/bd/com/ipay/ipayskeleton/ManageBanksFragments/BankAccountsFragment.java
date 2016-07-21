package bd.com.ipay.ipayskeleton.ManageBanksFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.RemoveBankAccountResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.VerifyBankWithAmountRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.VerifyBankWithAmountResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class BankAccountsFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestDeleteAsyncTask mRemoveBankAccountTask = null;
    private RemoveBankAccountResponse mRemoveBankAccountResponse;

    private HttpRequestPostAsyncTask mSendForVerificationWithAmountTask = null;
    private VerifyBankWithAmountResponse mVerifyBankWithAmountResponse;

    private HttpRequestGetAsyncTask mGetBankTask = null;
    private GetBankListResponse mBankListResponse;

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
        mGetAvailableBankAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankList() {
        if (mGetBankTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_fetching_bank_info));
        if (getActivity() != null)
            mProgressDialog.show();
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

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetBankTask = null;
            mRemoveBankAccountTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_BANK_LIST)) {
            if (this.isAdded()) setContentShown(true);
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
            mProgressDialog.dismiss();
            mGetBankTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_REMOVE_A_BANK)) {

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

        } else if (result.getApiCommand().equals(Constants.COMMAND_VERIFICATION_BANK_WITH_AMOUNT)) {

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
        }
    }

    public class UserBankListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public UserBankListAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mBankName;
            private TextView mBankAccountNumber;
            private ImageView mBankVerifiedStatus;
            private TextView mBranchName;
            private LinearLayout optionsLayout;
            private Button removeButton;
            private Button verifyButton;
            private View verifyDivider;
            private View divider;
            private RoundedImageView bankIcon;

            public ViewHolder(final View itemView) {
                super(itemView);

                mBankAccountNumber = (TextView) itemView.findViewById(R.id.bank_account_number);
                mBankName = (TextView) itemView.findViewById(R.id.bank_name);
                mBankVerifiedStatus = (ImageView) itemView.findViewById(R.id.bank_account_verify_status);
                mBranchName = (TextView) itemView.findViewById(R.id.bank_branch_name);
                optionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                removeButton = (Button) itemView.findViewById(R.id.remove_button);
                verifyButton = (Button) itemView.findViewById(R.id.verify_button);
                verifyDivider = (View) itemView.findViewById(R.id.verify_divider);
                divider = itemView.findViewById(R.id.divider);
                bankIcon = (RoundedImageView) itemView.findViewById(R.id.portrait);
            }

            public void bindView(int pos) {

                if (pos == mListUserBankClasses.size() - 1) divider.setVisibility(View.GONE);

                final long bankAccountID = mListUserBankClasses.get(pos).getBankAccountId();
                final int bankStatus = mListUserBankClasses.get(pos).getAccountStatus();
                final String bankName = mListUserBankClasses.get(pos).getBankName();
                final String branchName = mListUserBankClasses.get(pos).getBranchName();
                final String verificationStatus = mListUserBankClasses.get(pos).getVerificationStatus();
                Drawable icon = getResources().getDrawable(mListUserBankClasses.get(pos).getBankIcon(getContext()));

                mBankAccountNumber.setText(mListUserBankClasses.get(pos).getAccountNumber());
                mBankName.setText(bankName);
                mBranchName.setText(branchName);
                bankIcon.setImageDrawable(icon);
                optionsLayout.setVisibility(View.GONE);


                if (verificationStatus.equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED)) {
                    mBankVerifiedStatus.setImageResource(R.drawable.ic_verified3x);
                    mBankVerifiedStatus.clearColorFilter();

                    verifyDivider.setVisibility(View.GONE);
                    verifyButton.setVisibility(View.GONE);

                } else if (verificationStatus.equals(Constants.BANK_ACCOUNT_STATUS_NOT_VERIFIED)) {
                    mBankVerifiedStatus.setImageResource(R.drawable.ic_notverified3x);
                    mBankVerifiedStatus.setColorFilter(Color.RED);

                    verifyDivider.setVisibility(View.GONE);
                    verifyButton.setVisibility(View.GONE);

                } else {

                    // Bank verification status pending
                    mBankVerifiedStatus.setImageResource(R.drawable.ic_wip);
                    mBankVerifiedStatus.setColorFilter(Color.GRAY);

                    verifyDivider.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.VISIBLE);
                }

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

                verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (verificationStatus.equals(Constants.BANK_ACCOUNT_STATUS_PENDING)) {

                            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                    .title(R.string.enter_the_amount_we_sent)
                                    .customView(R.layout.dialog_verify_bank_with_amount, true)
                                    .positiveText(R.string.submit)
                                    .negativeText(R.string.cancel)
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();

                            View view = dialog.getCustomView();
                            final EditText mAmountEditText = (EditText) view.findViewById(R.id.amount);

                            dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    if (mAmountEditText.getText().toString().trim().length() == 0) {
                                        mAmountEditText.setError(getString(R.string.please_enter_amount));
                                        View focusView = mAmountEditText;
                                        focusView.requestFocus();
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

                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (optionsLayout.getVisibility() == View.VISIBLE)
                            optionsLayout.setVisibility(View.GONE);
                        else optionsLayout.setVisibility(View.VISIBLE);

                        if (verificationStatus.equals(Constants.BANK_ACCOUNT_STATUS_PENDING))
                            new MaterialShowcaseView.Builder(getActivity())
                                    .setTarget(verifyButton)
                                    .setDismissText(R.string.got_it)
                                    .setContentText(Html.fromHtml(getString(R.string.bank_verification_help_html)))
                                    .setDelay(100) // optional but starting animations immediately in onCreate can make them choppy
                                    .singleUse(bankAccountID + "") // provide a unique ID used to ensure it is only shown once // TODO: removed for now. Comment out later
                                    .setDismissOnTargetTouch(true)
                                    .setDismissOnTouch(true)
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
